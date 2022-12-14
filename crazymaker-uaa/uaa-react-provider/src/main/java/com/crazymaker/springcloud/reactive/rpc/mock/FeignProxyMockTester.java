package com.crazymaker.springcloud.reactive.rpc.mock;

import com.crazymaker.springcloud.common.util.ThreadUtil;
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
        proxy.hello().subscribe(responseData ->
        {
            log.info(responseData.toString());
        }, e ->
        {
            log.info("error:" + e.getMessage());
        });


        /**
         * 通过模拟接口，完成远程调用
         */
        proxy.echo("proxyTest").subscribe(responseData ->
        {
            log.info(responseData.toString());
        }, e ->
        {
            log.info("error:" + e.getMessage());
        });

        //主线程等待， 一切都是为了查看到异步结果
        ThreadUtil.sleepSeconds(1000);
    }
}
