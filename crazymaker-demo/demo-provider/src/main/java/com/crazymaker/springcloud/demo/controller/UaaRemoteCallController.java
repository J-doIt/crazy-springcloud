package com.crazymaker.springcloud.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.crazymaker.springcloud.common.dto.UserDTO;
import com.crazymaker.springcloud.common.result.RestOut;
import com.crazymaker.springcloud.common.util.JsonUtil;
import com.crazymaker.springcloud.user.info.remote.client.UserClient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;


@RestController
@RequestMapping("/api/call/uaa/")
@Api(tags = "演示 uaa-provider 远程调用")
public class UaaRemoteCallController
{
    //注入 Spring Boot 自动配置 RestTemplateBuilder 建造者IOC容器实例
    @Resource
    private RestTemplateBuilder restTemplateBuilder;


    @GetMapping("/user/detail/v1")
    @ApiOperation(value = "RestTemplate 远程调用")
    public RestOut<JSONObject> remoteCallV1()
    {
        /**
         * 根据实际的地址调整：UAA 服务的获取用户信息地址
         */
        String url = "http://crazydemo.com:7702/uaa-provider/api/user/detail/v1?userId=1";
        /**
         *  使用建造者的build()方法，建造 restTemplate 实例
         */
            RestTemplate restTemplate = restTemplateBuilder.build();

        ResponseEntity<String> responseEntity =
                restTemplate.getForEntity(url, String.class);

        TypeReference<RestOut<UserDTO>> pojoType =
                new TypeReference<RestOut<UserDTO>>() {};
        /**
         * 转成json对象,用到了阿里 FastJson
         */
        RestOut<UserDTO> result =
                JsonUtil.jsonToPojo(responseEntity.getBody(), pojoType);
        /**
         * 组装成最终的结果，然后返回到客户端
         */
        JSONObject data = new JSONObject();
        data.put("uaa-data", result);
        return RestOut.success(data).setRespMsg("操作成功");
    }


     //注入 @FeignClient注解配置 所配置的 客户端实例
    @Resource
    UserClient userClient;

    @GetMapping("/user/detail/v2")
    @ApiOperation(value = "Feign 远程调用")
    public RestOut<JSONObject> remoteCallV2(
            @RequestParam(value = "userId") Long userId)
    {
        //使用Feign进行远程调用
        RestOut<UserDTO> result = userClient.detail(userId);
        JSONObject data = new JSONObject();
        data.put("uaa-data", result);
        return RestOut.success(data).setRespMsg("操作成功");
    }
}
