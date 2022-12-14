package com.crazymaker.demo.proxy.FeignMock;

import com.alibaba.fastjson.JSONObject;
import com.crazymaker.demo.proxy.MockDemoClient;
import com.crazymaker.springcloud.common.result.RestOut;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.IOException;

@Slf4j
public class FeignProxyMockTester
{
    /**
     * 测试用例
     */
    @Test
    public void test() throws IOException
    {

        /**
         * 创建远程调用接口的本地JDK Proxy代理实例
         */
        MockDemoClient proxy =
                MockInvocationHandler.newInstance(MockDemoClient.class);

        /**
         * 通过模拟接口，完成远程调用
         */
        RestOut<JSONObject> responseData = proxy.hello();
        log.info(responseData.toString());

        /**
         * 通过模拟接口，完成远程调用
         */
        RestOut<JSONObject> echo = proxy.echo("proxyTest");
        log.info(echo.toString());
    }
}
