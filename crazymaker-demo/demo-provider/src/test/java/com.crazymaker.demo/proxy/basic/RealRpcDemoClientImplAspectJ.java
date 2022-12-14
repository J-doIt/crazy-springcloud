package com.crazymaker.demo.proxy.basic;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RealRpcDemoClientImplAspectJ {

  //  @Pointcut(“execution(public * com.crazymaker.springcloud.demo..(…))”)

   // public * [公共] com.crazymaker.springcloud.demo [包路径] .* [所有类] .* [所有方法]（…）[所有参数]
    //切入点：待增强的方法
    @Pointcut("execution(public * com.crazymaker.demo.proxy.basic.RealRpcDemoClientImpl.*(..))")
    //切入点签名
    public void log() {
        System.out.println("pointCut签名。。。");
    }

    //前置通知
    @Before("log()")
    public void deBefore(JoinPoint jp) throws Throwable {

        System.out.println("调用前。。。");

    }

    //返回通知
    @AfterReturning(returning = "ret", pointcut = "log()")
    public void doAfterReturning(Object ret) throws Throwable {
        System.out.println("调用前。。。");
    }

    //异常通知
    @AfterThrowing(throwing = "ex", pointcut = "log()")
    public void throwss(JoinPoint jp, Exception ex) {
        System.out.println("发生异常。。。");
    }

}
