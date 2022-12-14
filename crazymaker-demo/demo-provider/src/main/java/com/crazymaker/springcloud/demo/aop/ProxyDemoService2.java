package com.crazymaker.springcloud.demo.aop;

import org.springframework.stereotype.Component;

@Component
public class ProxyDemoService2 {

    public void foo() {

        System.out.println(" this target from ProxyDemoService2 ");
        System.out.println("-------bar from ProxyDemoService2 !");
    }
}
