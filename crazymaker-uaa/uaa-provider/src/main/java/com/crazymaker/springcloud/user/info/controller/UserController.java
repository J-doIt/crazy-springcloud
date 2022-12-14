package com.crazymaker.springcloud.user.info.controller;

import com.crazymaker.springcloud.base.service.impl.UserServiceImpl;
import com.crazymaker.springcloud.common.dto.UserDTO;
import com.crazymaker.springcloud.common.result.RestOut;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@Api(value = "用户信息、基础学习DEMO", tags = {"用户信息DEMO"})
@RestController
@RequestMapping("/api/user")
public class UserController
{

    @Resource
    private UserServiceImpl userService;
    /**
     * 注入全局的加密器
     */
    @Resource
    PasswordEncoder passwordEncoder;


    @ApiOperation(value = "接口的功能介绍", notes = "提示接口使用者注意事项", httpMethod = "GET")
    @ApiImplicitParam(dataType = "string", name = "name", value = "姓名", required = true)
    @RequestMapping(value = "/hello")
    public String hello(String name)
    {
        log.info("方法 hello 被调用了");

        return "hello " + name;
    }

    @GetMapping("/detail/v1")
    @ApiOperation(value = "获取用户信息")
    public RestOut<UserDTO> getUser(@RequestParam(value = "userId", required = true) Long userId)
    {
        log.info("方法 getUser 被调用了");
        UserDTO dto = userService.getUser(userId);
        if (null == dto)
        {
            return RestOut.error("没有找到用户");
        }
        return RestOut.success(dto).setRespMsg("操作成功");
    }


    @GetMapping("/passwordEncoder/v1")
    @ApiOperation(value = "密码加密")
    public RestOut<String> passwordEncoder(
            @RequestParam(value = "raw", required = true) String raw)
    {

//        passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        String encode = passwordEncoder.encode(raw);
        return RestOut.success(encode);
    }
}
