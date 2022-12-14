package com.crazymaker.demo.proxy.basic;

import com.alibaba.fastjson.JSONObject;
import com.crazymaker.demo.proxy.MockDemoClient;
import com.crazymaker.springcloud.common.result.RestOut;
import com.crazymaker.springcloud.seckill.remote.client.DemoClient;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
class DemoClientStaticProxy implements DemoClient
{
    /**
     * 被代理的真正实例
     */
    private MockDemoClient realClient;

    @Override
    public RestOut<JSONObject> hello()
    {
        log.info("hello 方法被调用");
        return realClient.hello();
    }


    @Override
    public RestOut<JSONObject> echo(String word)
    {
        log.info("echo 方法被调用");
        return realClient.echo(word);
    }
}
