package com.crazymaker.demo;

import com.crazymaker.springcloud.demo.aop.ProxyDemoService;
import com.crazymaker.springcloud.demo.aop.ProxyDemoService2;
import com.crazymaker.springcloud.demo.start.DemoCloudApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {DemoCloudApplication.class})
// 指定启动类
public class AopProxyTest {

    @Resource
    ProxyDemoService proxyDemoService;

    @Resource
    ProxyDemoService2 proxyDemoService2;

    @Test
    public void testProxyDemoService() {

        proxyDemoService.foo();
        proxyDemoService2.foo();
    }
}

