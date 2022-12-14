package com.crazymaker.demo.proxy.basic;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.crazymaker.demo.proxy.MockDemoClient;
import com.crazymaker.springcloud.common.result.RestOut;
import com.crazymaker.springcloud.common.util.HttpRequestUtil;
import com.crazymaker.springcloud.common.util.JsonUtil;
import com.crazymaker.springcloud.demo.constants.TestConstants;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.text.MessageFormat;

@AllArgsConstructor
@Slf4j
class RealRpcDemoClientImpl implements MockDemoClient
{
    final String contextPath = TestConstants.DEMO_CLIENT_PATH;

    //hello简单实现
    public RestOut<JSONObject> hello()
    {
        /**
         * 远程调用接口的方法，完成 demo-provider 的 REST API 远程调用
         * REST API 功能：返回 hello world
         */
        String uri = "api/demo/hello/v1";
        /**
         * 组装 REST 接口 URL
         */
        String restUrl = contextPath + uri;
        log.info("restUrl={}", restUrl);


        /**
         * 通过 HttpClient 组件调用 REST 接口
         */
        String responseData = null;
        try
        {
            responseData = HttpRequestUtil.simpleGet(restUrl);
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        /**
         * 解析 REST 接口的响应结果，解析成 JSON 对象，并且返回
         */
        RestOut<JSONObject> result = JsonUtil.jsonToPojo(responseData, new TypeReference<RestOut<JSONObject>>()
        {
        });

        return result;
    }

    //echo简单实现
    public RestOut<JSONObject> echo(String word)
    {
        /**
         * 远程调用接口的方法, 完成 demo-provider 的   REST API 远程调用
         * REST API 功能： 回显输入的信息
         */
        String uri = "api/demo/echo/{0}/v1";
        /**
         * 组装 REST 接口 URL
         */
        String restUrl = contextPath + MessageFormat.format(uri, word);
        log.info("restUrl={}", restUrl);


        /**
         * 通过 HttpClient 组件调用 REST 接口
         */
        String responseData = null;
        try
        {
            responseData = HttpRequestUtil.simpleGet(restUrl);
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        /**
         * 解析 REST 接口的响应结果，解析成 JSON 对象，并且返回
         */
        RestOut<JSONObject> result = JsonUtil.jsonToPojo(responseData, new TypeReference<RestOut<JSONObject>>()
        {
        });
        return result;
    }

}
