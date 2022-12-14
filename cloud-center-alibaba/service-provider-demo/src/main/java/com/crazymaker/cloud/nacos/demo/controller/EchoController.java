package com.crazymaker.cloud.nacos.demo.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/echo")
@Api(tags = "Echo 演示")
@Slf4j
public class EchoController {
    //回显服务
    @ApiOperation(value = "回显路径占位符")
    @RequestMapping(value = "/variable/{variable}", method = RequestMethod.GET)
    public String echoVarable(@PathVariable String variable) {
        log.info(Thread.currentThread().getName() + "\t" + "...echo Variable 被调用");
        return "echo path variable: " + variable;
    }


    //回显服务
    @ApiOperation(value = "回显请求参数值")
    @RequestMapping(value = "/param", method = RequestMethod.GET)
    public String echo(@RequestParam(value = "p1", required = false) String param1,
                       @RequestParam(value = "p2", required = false) String param2
    ) {
        log.info(Thread.currentThread().getName() + "\t" + "...echo param1 :" + param1);
        return "echo param: " + param1 + "， param2: " + param2;
    }
}
