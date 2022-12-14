package com.crazymaker.cloud.nacos.demo.sentinel.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.crazymaker.cloud.nacos.demo.sentinel.handler.CustomerBlockHandler;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/flowControllDemo")
@Api(tags = "FlowControllDemoController 流控演示")
@Slf4j
public class FlowControllDemoController {
    @SentinelResource(value = "test1", blockHandler = "exceptionHandler")
    @GetMapping("/test1")
    public String test1() {
        log.info(Thread.currentThread().getName() + "\t" + "...test1");
        return "-------hello baby，i am test1";
    }


    // Block 异常处理函数，参数最后多一个 BlockException，其余与原函数一致.
    public String exceptionHandler(BlockException ex) {
        // Do some log here.
        ex.printStackTrace();
        log.info(Thread.currentThread().getName() + "\t" + "...exceptionHandler");
        return String.format("error: test1  is not OK");
    }

    @SentinelResource(value = "test1_ref")
    @GetMapping("/test1_ref")
    public String test1_ref() {
        log.info(Thread.currentThread().getName() + "\t" + "...test1_related");
        return "-------hello baby，i am test1_ref";
    }


    @SentinelResource(value = "testWarmUP", blockHandler = "exceptionHandlerOfWarmUp")
    @GetMapping("/testWarmUP")
    public String testWarmUP() {
        log.info(Thread.currentThread().getName() + "\t" + "...test1");
        return "-------hello baby，i am testWarmUP";
    }

    // Block 异常处理函数，参数最后多一个 BlockException，其余与原函数一致.
    public String exceptionHandlerOfWarmUp(BlockException ex) {
        // Do some log here.
        ex.printStackTrace();
        log.info(Thread.currentThread().getName() + "\t" + "...exceptionHandler");
        return String.format("error: testWarmUP  is not OK");
    }

    @SentinelResource(value = "testLineUp", blockHandler = "exceptionHandlerOftestLineUp")
    @GetMapping("/testLineUp")
    public String testLineUp() {
        log.info(Thread.currentThread().getName() + "\t" + "...test1");
        return "-------hello baby，i am testLineUp";
    }

    // Block 异常处理函数，参数最后多一个 BlockException，其余与原函数一致.
    public String exceptionHandlerOftestLineUp(BlockException ex) {
        // Do some log here.
        ex.printStackTrace();
        log.info(Thread.currentThread().getName() + "\t" + "...exceptionHandler");
        return String.format("error: testLineUp  is not OK");
    }


    @GetMapping("/test2")
    public String test2() {
        int num = 1 / 0;
        log.info(Thread.currentThread().getName() + "\t" + "...test2");
        return "------测试异常数，i am test2";
    }


    @GetMapping("/test3")
    @SentinelResource(value = "byUrl")
    public String test3() {
        log.info(Thread.currentThread().getName() + "\t" + "...test3");
        return "------按url限流测试OK，i am test3";
    }

    @GetMapping("/byHotKey")
    @SentinelResource(value = "byHotKey",
            blockHandler = "skuAccessError", fallback = "skuAccessFallback")
    public String test4(@RequestParam(value = "userId", required = false) String userId,
                        @RequestParam(value = "skuId", required = false) int skuId) {
        log.info(Thread.currentThread().getName() + "\t" + "...byHotKey");
        return "-----------by HotKey： skuId：" + skuId;
    }

    public String skuAccessFallback(String userId, int skuId) {
        return "------skuAccessFallback，i am blocked  byHotKey";
    }

    public String skuAccessError(String userId, int skuId, BlockException exception) {

        return "------skuAccessError，Error  byHotKey";
    }


    @GetMapping("/test5")
    @SentinelResource(value = "byHotKeyUserId",
            blockHandlerClass = CustomerBlockHandler.class,
            blockHandler = "userAccessError")
    public String test5(@RequestParam(value = "userId", required = false) String userId,
                        @RequestParam(value = "skuId", required = false) String skuId) {
        log.info(Thread.currentThread().getName() + "\t" + "...test4");
        return "-----------by HotKey： UserId";
    }
}
