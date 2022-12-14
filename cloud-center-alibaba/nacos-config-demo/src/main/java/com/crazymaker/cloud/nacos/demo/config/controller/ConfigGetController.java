package com.crazymaker.cloud.nacos.demo.config.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/config")
@Api(tags = "Nacos 配置中心演示")
public class ConfigGetController {


    @Value("${foo.bar:empty}")
    private String bar;


    @Value("${spring.datasource.username:empty}")
    private String dbusername;


    @Value("${some.proper:empty}")
    private String proper;


    //获取配置的内容
    @ApiOperation(value = "获取配置的内容")
    @RequestMapping(value = "/bar", method = RequestMethod.GET)
    public String getBar() {
        return "bar is :" + bar;
    }

    //获取配置的内容
    @ApiOperation(value = "获取配置的db username")
    @RequestMapping(value = "/dbusername", method = RequestMethod.GET)
    public String getDbusername() {
        return "db username is :" + dbusername;
    }


    //获取独立配置的内容
    @ApiOperation(value = "获取独立配置 proper")
    @RequestMapping(value = "/proper", method = RequestMethod.GET)
    public String getProper() {
        return "some.proper is :" + proper;
    }


}
