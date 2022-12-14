package com.crazymaker.springcloud.demo.aop;

import org.springframework.stereotype.Component;

@DemoTypeAnnotation
@Component
public class ProxyDemoService {

    @DemoMethodAnnotation
    public void foo() {
        System.out.println(" this target from ProxyDemoService  1");
        System.out.println("-------bar from ProxyDemoService 1 !");
    }
}
