package com.crazymaker.springcloud.user.info.controller;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.crazymaker.springcloud.common.dto.UserDTO;
import com.crazymaker.springcloud.common.result.RestOut;
import com.crazymaker.springcloud.common.util.JsonUtil;
import com.crazymaker.springcloud.seckill.remote.client.DemoClient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("/api/call/demo/")
@Api(tags = "演示 demo-provider 远程调用")
public class DemoRPCController
{

    //注入 @FeignClient 注解配置 所配置的 demo-provider 客户端Feign实例
    @Resource
    DemoClient demoClient;

    @GetMapping("/hello/v1")
    @ApiOperation(value = "hello 远程调用")
    public RestOut<JSONObject> remoteHello()
    {
        log.info("方法 remoteHello 被调用了");
        /**
         * 调用   demo-provider 的  REST 接口  api/demo/hello/v1
         */
        RestOut<JSONObject> result = demoClient.hello();
        JSONObject data = new JSONObject();
        data.put("demo-data", result);
        return RestOut.success(data).setRespMsg("操作成功");
    }


    @GetMapping("/echo/{word}/v1")
    @ApiOperation(value = "echo 远程调用")
    public RestOut<JSONObject> remoteEcho(
            @PathVariable(value = "word") String word)
    {
        /**
         * 调用  demo-provider 的  REST 接口   api/demo/echo/{0}/v1
         */
        RestOut<JSONObject> result = demoClient.echo(word);
        JSONObject data = new JSONObject();
        data.put("demo-data", result);
        return RestOut.success(data).setRespMsg("操作成功");
    }

    //注入 Spring Boot 自动配置 RestTemplateBuilder 建造者IOC容器实例
    @Resource
    private RestTemplateBuilder restTemplateBuilder;


    @GetMapping("/restTemplate/v1")
    @ApiOperation(value = "RestTemplate 远程调用")
    public RestOut<JSONObject> remoteCallV1()
    {
        /**
         * 根据实际的地址调整：UAA 服务的获取用户信息地址
         */
        String url = "http://localhost:7700/demo-provider/api/demo/hello/1";
        /**
         *  使用建造者的build()方法，建造 restTemplate 实例
         */
        RestTemplate restTemplate = restTemplateBuilder.build();

        ResponseEntity<String> responseEntity =
                restTemplate.getForEntity(url, String.class);


        /**
         * 组装成最终的结果，然后返回到客户端
         */
        JSONObject data = new JSONObject();
        data.put("demo-data", responseEntity.getBody());
        return RestOut.success(data).setRespMsg("操作成功");
    }

}
