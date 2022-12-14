package com.crazymaker.springcloud.demo.aop;

import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.PointcutAdvisor;
import org.springframework.stereotype.Component;

@Component
public class DemoAdvisor implements PointcutAdvisor {

    @Override
    public Advice getAdvice() {
        return new DemoInterceptAdvice();
    }

    @Override
    public boolean isPerInstance() {
        return false;
    }

    @Override
    public Pointcut getPointcut() {
        return new DemoPointCut();
    }
}
