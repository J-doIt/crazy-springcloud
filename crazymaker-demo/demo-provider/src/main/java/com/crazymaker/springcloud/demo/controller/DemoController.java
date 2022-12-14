package com.crazymaker.springcloud.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.crazymaker.springcloud.common.result.RestOut;
import com.crazymaker.springcloud.user.info.api.dto.LoginInfoDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;


@RestController
@RequestMapping("/api/demo/")
@Api(tags = "Demo 演示")
public class DemoController
{

    /**
     * 非常简单的一个接口,返回Json 对象
     *
     * @return hello world
     */
    @GetMapping("/hello/v1")
    @ApiOperation(value = "hello world api 接口")
    public RestOut<JSONObject> hello()
    {
        JSONObject data = new JSONObject();
        data.put("hello", "world");
        return RestOut.success(data).setRespMsg("操作成功");
    }


    /**
     * 回显消息 接口
     *
     * @return echo 回显消息
     */
    @GetMapping("/echo/{word}/v1")
    @ApiOperation(value = "回显消息  api 接口")
    public RestOut<JSONObject> echo(@PathVariable(value = "word") String word)
    {
        JSONObject data = new JSONObject();
        data.put("echo", word);
        return RestOut.success(data).setRespMsg("操作成功");
    }

    /**
     * 获取头部信息 接口
     *
     * @return RestOut
     */
    @GetMapping("/header/echo/v1")
    @ApiOperation(value = "回显头部信息")
    public RestOut<JSONObject> echo(HttpServletRequest request)
    {
        /**
         * 获取头部信息，放入  JSONObject 实例
         */
        JSONObject data = new JSONObject();
        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements())
        {
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);
            data.put(key, value);
        }

        return RestOut.success(data).setRespMsg("操作成功");
    }

    /**
     * 提交表单  mime类型 application/x-www-form-urlencoded
     *
     * @return RestOut
     */
//    @PostMapping("/post/demo/v1")
    @RequestMapping(value = "/post/demo/v1", method = RequestMethod.POST)
    @ApiOperation(value = "post请求演示")
    public RestOut<LoginInfoDTO> postDemo(@RequestParam String username, @RequestParam String password)
    {
        /**
         * 直接返回
         */
        LoginInfoDTO dto = new LoginInfoDTO();
        dto.setUsername(username);
        dto.setPassword(password);
        return RestOut.success(dto).setRespMsg("body的内容回显给客户端");
    }

    /**
     * 提交body  mime类型 为 application/json
     *
     * @return RestOut
     */
//    @PostMapping("/post/demo/v2")
    @RequestMapping(value = "/post/demo/v2", method = RequestMethod.POST)
    @ApiOperation(value = "post请求演示 2")
    public RestOut<LoginInfoDTO> postDemo2(@RequestBody LoginInfoDTO dto)
    {
        /**
         * 直接返回
         */

        return RestOut.success(dto).setRespMsg("body的内容回显给客户端");
    }

}
