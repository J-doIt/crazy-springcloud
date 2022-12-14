package com.crazymaker.springcloud.seckill.remote.fallback;


import com.alibaba.fastjson.JSONObject;
import com.crazymaker.springcloud.common.result.RestOut;
import com.crazymaker.springcloud.seckill.remote.client.DemoClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

@Component
public class DemoDefaultFallback implements DemoClient
{
  /**
     * 测试远程调用
     *
     * @return hello
     */
    @GetMapping("/hello/v1" )
    public RestOut<JSONObject> hello()
    {

        return RestOut.error("远程调用失败,返回熔断后的调用结果" );
    }

    /**
     * 非常简单的一个 回显 接口，主要用于远程调用
     *
     * @param word
     * @return echo 回显消息
     */
    @Override
    public RestOut<JSONObject> echo(String word)
    {
        return RestOut.error("远程调用失败,返回熔断后的调用结果" );
    }
}
