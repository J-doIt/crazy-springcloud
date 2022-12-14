package com.crazymaker.springcloud.user.info.controller;

import com.crazymaker.springcloud.base.service.impl.UserServiceImpl;
import com.crazymaker.springcloud.common.constants.SessionConstants;
import com.crazymaker.springcloud.common.exception.BusinessException;
import com.crazymaker.springcloud.common.result.RestOut;
import com.crazymaker.springcloud.user.info.api.dto.LoginInfoDTO;
import com.crazymaker.springcloud.user.info.api.dto.LoginOutDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Api(value = "用户端登录与退出", tags = {"用户端登录与退出DEMO"})
@RestController
@RequestMapping("/api/session" )
public class SessionController
{

    //用户端会话服务，和管理控制台会话服务进行区分
    @Resource
    private UserServiceImpl userService;


    @PostMapping("/login/v1" )
    @ApiOperation(value = "用户端登陆" )
    public RestOut<LoginOutDTO> login(@RequestBody LoginInfoDTO loginInfoDTO,
                                      HttpServletRequest request,
                                      HttpServletResponse response)
    {

        LoginOutDTO dto = userService.login(loginInfoDTO);
        response.setHeader("Content-Type", "application/json;charset=utf-8" );
        response.setHeader(SessionConstants.AUTHORIZATION_HEAD, dto.getToken());
        return RestOut.success(dto);
    }

    @PostMapping("/token/refresh/v1" )
    @ApiOperation(value = "前台刷新token" )
    public RestOut<LoginOutDTO> tokenRefresh(HttpServletRequest request, HttpServletResponse response)
    {

        String token = request.getHeader(SessionConstants.AUTHORIZATION_HEAD);
        if (StringUtils.isEmpty(token))
        {
            throw BusinessException.builder().errMsg("刷新失败" ).build();
        }
        LoginOutDTO outDTO = userService.tokenRefresh(token);
        response.setHeader("Content-Type", "text/html;charset=utf-8" );
        response.setHeader(SessionConstants.AUTHORIZATION_HEAD, outDTO.getToken());
        return RestOut.success(outDTO);
    }

}
