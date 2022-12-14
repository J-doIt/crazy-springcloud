package com.crazymaker.springcloud.demo.aop;


import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class DemoInterceptAdvice implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        System.out.println(" ~~~~~~~~~ 我来做前面的增强 process!!!");
        Object ret = invocation.proceed();
        System.out.println(" ~~~~~~~~~ 我来做后面的增强 process!!!");

        return ret;
    }

}
