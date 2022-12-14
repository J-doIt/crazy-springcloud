package com.crazymaker.demo.proxy.basic;

import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class CglibMethodInterceptor implements MethodInterceptor {
    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        System.out.println("调用前。。。");
        Object obj = methodProxy.invokeSuper(o, objects);
        System.out.println("调用后。。。");
        return obj;
    }
}