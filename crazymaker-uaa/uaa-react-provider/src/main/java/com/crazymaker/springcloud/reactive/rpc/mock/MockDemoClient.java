package com.crazymaker.springcloud.reactive.rpc.mock;

import com.alibaba.fastjson.JSONObject;
import com.crazymaker.springcloud.common.result.RestOut;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;


/**
 * @description:模拟远程调用接口
 * @date 2019年7月22日
 */

@RestController(value = "http://crazydemo.com:7700/demo-provider/")
public interface MockDemoClient
{
    public static final String DEMO_CLIENT_PATH = "http://crazydemo.com:7700/demo-provider/";

    /**
     * 远程调用接口的方法:
     * 调用   demo-provider 的  REST 接口  api/demo/hello/v1
     * REST 接口 功能：返回 hello world
     *
     * @return JSON 响应实例
     */
    @GetMapping(name = "api/demo/hello/v1")
    Mono<RestOut<JSONObject>> hello();


    /**
     * 远程调用接口的方法:
     * 调用   demo-provider 的  REST 接口  api/demo/echo/{0}/v1
     * REST 接口 功能： 回显输入的信息
     *
     * @return echo 回显消息 JSON 响应实例
     */
    @GetMapping(name = "api/demo/echo/{0}/v1")
    Mono<RestOut<JSONObject>> echo(String word);
}
