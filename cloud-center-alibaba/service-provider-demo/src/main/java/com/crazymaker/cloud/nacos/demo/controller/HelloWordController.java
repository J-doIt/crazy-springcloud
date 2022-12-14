package com.crazymaker.cloud.nacos.demo.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hello")
@Api(tags = "HelloWord 演示")
@Slf4j
public class HelloWordController {

    //回显服务
    @ApiOperation(value = "hello world")
    @RequestMapping(value = "/world", method = RequestMethod.GET)
    public String echo() {
        log.info(Thread.currentThread().getName() + "\t" + "...echo 被调用");
        return "hello world ";
    }
}
