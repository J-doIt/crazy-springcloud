package com.crazymaker.demo.proxy.FeignMock;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.crazymaker.springcloud.common.result.RestOut;
import com.crazymaker.springcloud.common.util.HttpRequestUtil;
import com.crazymaker.springcloud.common.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;

import java.text.MessageFormat;

@Slf4j
public class MockRpcMethodHandler implements RpcMethodHandler
{

    /**
     * REST URL 的前面部分，来自于远程调用 Feign 接口的类级别注解
     * 如 "http://crazydemo.com:7700/demo-provider/";
     */
    final String contextPath;

    /**
     * REST URL 的前面部分，来自于远程调用 Feign 接口的方法级别的注解
     * 如 "api/demo/hello/v1";
     */
    final String url;

    public MockRpcMethodHandler(String contextPath, String url)
    {
        this.contextPath = contextPath;
        this.url = url;
    }

    /**
     * 功能：组装 url，完成 REST RPC 远程调用，并且返回 JSON结果
     *
     * @param argv RPC 方法的参数
     * @return REST 接口的响应结果
     * @throws Throwable 异常
     */
    @Override
    public Object invoke(Object[] argv) throws Throwable
    {
        /**
         * 组装 REST 接口 URL
         */
        String restUrl = contextPath + MessageFormat.format(url, argv);
        log.info("restUrl={}", restUrl);


        /**
         * 通过 HttpClient 组件调用 REST 接口
         */
        String responseData = HttpRequestUtil.simpleGet(restUrl);

        /**
         * 解析 REST 接口的响应结果，解析成 JSON 对象，并且返回
         */
        RestOut<JSONObject> result = JsonUtil.jsonToPojo(responseData, new TypeReference<RestOut<JSONObject>>()
        {
        });

        return result;

    }


}
