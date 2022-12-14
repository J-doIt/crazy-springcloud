package com.crazymaker.demo.proxy.FeignMock;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
class MockInvocationHandler implements InvocationHandler
{

    /**
     * 远程调用的分发映射：根据方法名称，分发方法处理器
     * key:  远程调用 Feign 接口的方法名称
     * value: 方法处理器 handler
     */
    private Map<Method, RpcMethodHandler> dispatch;


    /**
     * 代理对象的创建
     *
     * @param clazz 被代理的接口类型
     * @return 代理对象
     */
    public static <T> T newInstance(Class<T> clazz)
    {
        /**
         * 从远程调用接口的类级别注解中，获取 REST 地址的 contextPath 部分
         */
        Annotation controllerAnno = clazz.getAnnotation(RestController.class);
        if (controllerAnno == null)
        {
            return null;
        }
        String contextPath = ((RestController) controllerAnno).value();

        MockInvocationHandler invokeHandler = new MockInvocationHandler();
        invokeHandler.dispatch = new LinkedHashMap<>();

        /**
         *  通过反射，迭代远程调用接口中的每一个方法，组装对应的 MockRpcMethodHandler 模拟方法处理器
         */
        for (Method method : clazz.getMethods())
        {
            Annotation methodAnnotation = method.getAnnotation(GetMapping.class);
            if (methodAnnotation == null)
            {
                continue;
            }

            /**
             * 从远程调用接口的方法级别注解中，获取 REST 地址的 uri 部分
             */
            String uri = ((GetMapping) methodAnnotation).name();
            /**
             * 组装 MockRpcMethodHandler 模拟方法处理器
             * 注入 REST 地址的 contextPath 部分和uri部分
             */
            MockRpcMethodHandler handler = new MockRpcMethodHandler(contextPath, uri);


            /**
             *将模拟方法处理器handler 实例缓存到 dispatch 映射中
             */
            invokeHandler.dispatch.put(method, handler);

        }
        T proxy = (T) Proxy.newProxyInstance(clazz.getClassLoader(),
                new Class<?>[]{clazz},
                invokeHandler);
        return proxy;
    }


    /**
     * 动态代理的方法调用
     *
     * @param proxy  动态代理实例
     * @param method 待调用的方法
     * @param args   方法实参
     * @return 返回值
     * @throws Throwable 抛出的异常
     */
    @Override
    public Object invoke(Object proxy,
                         Method method, Object[] args) throws Throwable
    {

        if ("equals".equals(method.getName()))
        {
            Object other = args.length > 0 && args[0] != null ? args[0] : null;
            return equals(other);
        } else if ("hashCode".equals(method.getName()))
        {
            return hashCode();
        } else if ("toString".equals(method.getName()))
        {
            return toString();
        }
        log.info("远程方法 {} 被调用", method.getName());

        /**
         *  从 dispatch 映射中，根据方法名称，获取方法处理器
         */
        RpcMethodHandler rpcMethodHandler = dispatch.get(method);

        /**
         * 方法处理器组装 url，完成 REST RPC 远程调用，并且返回 JSON结果
         */
        return rpcMethodHandler.invoke(args);
    }
}
