package com.crazymaker.springcloud.demo.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;


@Aspect
@Component
public class AopConfig {
//  @Pointcut(“execution(public * com.crazymaker.springcloud.demo..(…))”)

    // public * [公共] com.crazymaker.springcloud.demo [包路径] .* [所有类] .* [所有方法]（…）[所有参数]


    //切入点

    @Pointcut("execution(* com.crazymaker.springcloud.demo.aop.ProxyDemoService2.*(..))")
    public void pointCut() {}


    @Around("pointCut()")
    public Object around(ProceedingJoinPoint joinPoint) {
        System.out.println(" 被增强  process!!!");
        Object obj = null;
        try {
            //执行原始代码，目标代码
            obj = joinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        return obj;
    }
}

