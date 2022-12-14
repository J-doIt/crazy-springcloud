package com.crazymaker.cloud.nacos.demo.sentinel.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.crazymaker.cloud.nacos.demo.sentinel.handler.CustomerBlockHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fallBackDemo")
@Api(tags = "FallBackDemoController 失败回退演示")
@Slf4j
public class FallBackDemoController {
    // 原函数
    @ApiOperation(value = "checknum 数字测试")
    @RequestMapping(value = "/checknum", method = RequestMethod.GET)
    @SentinelResource(value = "checknum",
            blockHandler = "exceptionHandler", fallback = "checknumFallback")
    public String checknum(@RequestParam(value = "num", required = false) long num) {
        if (num > 1000) {
            throw new RuntimeException("num is too big!");
        }
        log.info(Thread.currentThread().getName() + "\t" + "...checknum");
        return String.format("input num  %d is OK", num);
    }

    // Fallback 函数，函数签名与原函数一致或加一个 Throwable 类型的参数.
    public String checknumFallback(long num) {
        log.info(Thread.currentThread().getName() + "\t" + "...checknumFallback");
        return String.format("invalid num is  %d", num);
    }

    // Block 异常处理函数，参数最后多一个 BlockException，其余与原函数一致.
    public String exceptionHandler(long num, BlockException ex) {
        // Do some log here.
        ex.printStackTrace();
        log.info(Thread.currentThread().getName() + "\t" + "...exceptionHandler");
        return String.format("error: input num  %d is not OK", num);
    }

    @ApiOperation(value = "checknum2 输入测试2")
    @RequestMapping(value = "/checknum2", method = RequestMethod.GET)
    // 这里单独演示 blockHandlerClass 的配置.
    // 对应的 `handleException` 函数需要位于 `ExceptionUtil` 类中，并且必须为 public static 函数.
    @SentinelResource(value = "checknum2", blockHandler = "exceptionHandler", blockHandlerClass = {CustomerBlockHandler.class})
    public String checknum2(long s) {
        if (s > 1000) {
            throw new RuntimeException("num is too big!");
        }
        log.info(Thread.currentThread().getName() + "\t" + "...checknum2");
        return String.format("input num  %d is OK", s);
    }
}