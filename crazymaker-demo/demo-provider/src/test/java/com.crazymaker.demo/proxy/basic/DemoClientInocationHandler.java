package com.crazymaker.demo.proxy.basic;

import com.crazymaker.demo.proxy.MockDemoClient;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 动态代理的调用处理器
 */
@Slf4j
public class DemoClientInocationHandler implements InvocationHandler
{
    /**
     * 被代理的委托类实例
     */
    private MockDemoClient realClient;

    public DemoClientInocationHandler(MockDemoClient realClient)
    {
        this.realClient = realClient;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
    {

        String name = method.getName();
        log.info("{} 方法被调用", method.getName());

        /**
         * 直接调用 hello 方法
         */
        if (name.equals("hello"))
        {
            return realClient.hello();
        }
        /**
         * 通过 Java 反射 调用 echo 方法
         */
        if (name.equals("echo"))
        {
            return method.invoke(realClient, args);
        }
        /**
         * 通过 Java 反射 调用其他的方法
         */
        Object result = method.invoke(realClient, args);
        return result;
    }


}