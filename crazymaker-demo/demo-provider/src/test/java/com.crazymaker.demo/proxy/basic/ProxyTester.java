package com.crazymaker.demo.proxy.basic;

import com.alibaba.fastjson.JSONObject;
import com.crazymaker.demo.proxy.MockDemoClient;
import com.crazymaker.springcloud.common.result.RestOut;
import com.crazymaker.springcloud.seckill.remote.client.DemoClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.cglib.core.DebuggingClassWriter;
import org.springframework.cglib.proxy.Enhancer;
import sun.misc.ProxyGenerator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 静态代理和动态代理，测试用例
 */
@Slf4j
public class ProxyTester {

    /**
     * 不用代理，进行简单的远程调用
     */
    @Test
    public void simpleRPCTest() {
        /**
         * 简单的 PRC 调用类
         */
        MockDemoClient realObject = new RealRpcDemoClientImpl();

        /**
         * 调用   demo-provider 的  REST 接口  api/demo/hello/v1
         */
        RestOut<JSONObject> result1 = realObject.hello();
        log.info("result1={}", result1.toString());


        /**
         * 调用   demo-provider 的  REST 接口  api/demo/echo/{0}/v1
         */
        RestOut<JSONObject> result2 = realObject.echo("回显内容");
        log.info("result2={}", result2.toString());
    }


    /**
     * 静态代理测试
     */
    @Test
    public void staticProxyTest() {
        /**
         * 被代理的真实 PRC 调用类
         */
        MockDemoClient realObject = new RealRpcDemoClientImpl();

        /**
         *  静态的代理类
         */
        DemoClient proxy = new DemoClientStaticProxy(realObject);

        RestOut<JSONObject> result1 = proxy.hello();
        log.info("result1={}", result1.toString());

        RestOut<JSONObject> result2 = proxy.echo("回显内容");
        log.info("result2={}", result2.toString());
    }


    @Test
    public void dynamicProxyTest() throws IOException {
        /**
         * 被代理的真实 PRC 调用类
         */
        MockDemoClient realObject = new RealRpcDemoClientImpl();

        //参数1：类装载器
        ClassLoader classLoader = ProxyTester.class.getClassLoader();
        //参数2：代理类和委托类共同的抽象接口
        Class[] clazz = new Class[]{MockDemoClient.class};

        //参数3：动态代理的调用处理器
        InvocationHandler invocationHandler = new DemoClientInocationHandler(realObject);
        /**
         * 使用以上三个参数，创建 JDK 动态代理类
         */
        MockDemoClient proxy = (MockDemoClient)
                Proxy.newProxyInstance(classLoader, clazz, invocationHandler);
        RestOut<JSONObject> result1 = proxy.hello();
        log.info("result1={}", result1.toString());
        RestOut<JSONObject> result2 = proxy.echo("回显内容");
        log.info("result2={}", result2.toString());


        /**
         * 将动态代理类的class字节码，保存在当前的工程目录下
         */
        byte[] classFile = ProxyGenerator.generateProxyClass(
                "Proxy0",
                RealRpcDemoClientImpl.class.getInterfaces());
        /**
         * 输出到文件
         */
        FileOutputStream fos = new FileOutputStream(new File("Proxy0.class"));
        fos.write(classFile);
        fos.flush();
        fos.close();
    }

    @Test
    public void dynamicCglibProxyTest() throws IOException {
        System.setProperty(DebuggingClassWriter.DEBUG_LOCATION_PROPERTY, System.getProperty("user.dir")+"/gen/");

        /**
               * 被代理的真实 PRC 调用类
               */

        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(RealRpcDemoClientImpl.class);
        enhancer.setCallback(new CglibMethodInterceptor());

        MockDemoClient proxy = (MockDemoClient)enhancer.create();


        RestOut<JSONObject> result1 = proxy.hello();
        log.info("result1={}", result1.toString());
        RestOut<JSONObject> result2 = proxy.echo("回显内容");
        log.info("result2={}", result2.toString());


    }


    public interface Foo {
        void bar();
    }


    public class FooInvocationHandler implements InvocationHandler {
        Object target = null;

        public FooInvocationHandler(Object target) {
            this.target = target;
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            System.out.println("method :" + method.getName() + " is invoked!");
            return method.invoke(target, args); // 执行相应的目标方法
        }
    }


    /**
     * 动态代理测试
     */
    @Test
    public void simpleDynamicProxyTest() {
        try {
            // 这里有两种写法，采用复杂的一种写法，有助于理解。
            Class<?> proxyClass = Proxy.getProxyClass(FooInvocationHandler.class.getClassLoader(), Foo.class);
            final Constructor<?> cons;

            cons = proxyClass.getConstructor(InvocationHandler.class);

            final InvocationHandler ih = new FooInvocationHandler(new Foo() {
                @Override
                public void bar() {
                    System.out.println("匿名的 br is invoked!");
                }
            });
            Foo foo = (Foo) cons.newInstance(ih);
            foo.bar();

            // 下面是简单的一种写法，本质上和上面是一样的
        /*
        HelloWorld helloWorld=(HelloWorld)Proxy.
                 newProxyInstance(JDKProxyTest.class.getClassLoader(),
                        new Class<?>[]{HelloWorld.class},
                        new MyInvocationHandler(new HelloworldImpl()));
        helloWorld.sayHello();
        */
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    /**
     * 动态代理测试
     */
    @Test
    public void simpleDynamicProxyTest2() {
        try {
            FooInvocationHandler handler = new FooInvocationHandler(new Foo() {
                @Override
                public void bar() {
                    System.out.println("匿名的 br is invoked!");
                }
            });
            // 这里有两种写法，采用复杂的一种简单写法，对比上面的写法，有助于理解。
            Foo foo = (Foo) Proxy.newProxyInstance(FooInvocationHandler.class.getClassLoader(),
                    new Class<?>[]{Foo.class}, handler);
            foo.bar();


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // 面试题：被final修饰的类可以被spring代理吗？

    public final class FinalFoo {
        void bar(){
            System.out.println("final class FinalFoo 的 bar 方法 is invoked!");
        }
    }

    public class FinalFooInvocationHandler implements InvocationHandler {
        FinalFoo target = null;

        public FinalFooInvocationHandler(FinalFoo target) {
            this.target = target;
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            System.out.println("method :" + method.getName() + " is invoked!");
            return method.invoke(target, args); // 执行相应的目标方法
        }
    }

    /**
     * 动态代理测试
     */
    @Test
    public void simpleDynamicProxyTest3() {
        try {
            FinalFooInvocationHandler handler = new FinalFooInvocationHandler(new FinalFoo());
            // 面试题：被final修饰的类可以被spring代理吗？
            FinalFoo foo = (FinalFoo) Proxy.newProxyInstance(FooInvocationHandler.class.getClassLoader(),
                    new Class<?>[]{FinalFoo.class}, handler);
            foo.bar();


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
