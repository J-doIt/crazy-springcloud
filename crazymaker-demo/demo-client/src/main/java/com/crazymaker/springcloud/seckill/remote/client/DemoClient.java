package com.crazymaker.springcloud.seckill.remote.client;

import com.alibaba.fastjson.JSONObject;
import com.crazymaker.springcloud.common.result.RestOut;
import com.crazymaker.springcloud.seckill.remote.fallback.DemoDefaultFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @description:远程服务的本地声明式接口
 */

@FeignClient(
        value = "demo-provider", path = "/demo-provider/api/demo/",
        fallback = DemoDefaultFallback.class
)
public interface DemoClient
{
    /**
     * 远程调用接口的方法:
     * 调用   demo-provider 的  REST 接口  api/demo/hello/v1
     * REST 接口 功能：返回 hello world
     * @return JSON 响应实例
     */
    @GetMapping("/hello/v1")
    RestOut<JSONObject> hello();

    /**
     * 远程调用接口的方法:
     * 调用   demo-provider 的  REST 接口  api/demo/echo/{0}/v1
     * REST 接口 功能： 回显输入的信息
     * @return echo 回显消息 JSON 响应实例
     */
    @RequestMapping(value = "/echo/{word}/v1",
            method = RequestMethod.GET)
    RestOut<JSONObject> echo(
            @PathVariable(value = "word") String word);

}
