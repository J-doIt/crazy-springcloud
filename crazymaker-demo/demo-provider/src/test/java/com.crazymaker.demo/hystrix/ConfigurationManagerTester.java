package com.crazymaker.demo.hystrix;

import com.netflix.config.ConfigurationManager;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandMetrics;
import com.netflix.hystrix.HystrixCommandProperties;

public class ConfigurationManagerTester
{

    static class TestCommand extends HystrixCommand<String>
    {

        private boolean isTimeout;

        public TestCommand(boolean isTimeout)
        {
            super(Setter.withGroupKey(
                    HystrixCommandGroupKey.Factory.asKey("ExampleGroup"))
                    .andCommandPropertiesDefaults(
                            HystrixCommandProperties.Setter()
                                    .withExecutionTimeoutInMilliseconds(500)));
            this.isTimeout = isTimeout;
        }

        @Override
        protected String run() throws Exception
        {
            if (isTimeout)
            {
                Thread.sleep(800);
            } else
            {
                Thread.sleep(200);
            }
            return "";
        }

        @Override
        protected String getFallback()
        {
            return "fallback";
        }
    }


    public static void main(String[] args) throws Exception
    {
        // 断路器的请求次数阈值：大于3次请求
        ConfigurationManager
                .getConfigInstance()
                .setProperty(
                        "hystrix.command.default.circuitBreaker.requestVolumeThreshold",
                        3);
        boolean isTimeout = true;
        for (int i = 0; i < 10; i++)
        {
            TestCommand c = new TestCommand(isTimeout);
            c.execute();
            HystrixCommandMetrics.HealthCounts hc = c.getMetrics().getHealthCounts();
            System.out.println("断路器状态："
                    + c.isCircuitBreakerOpen()
                    + ",   请求数量：" + hc.getTotalRequests());
            if (c.isCircuitBreakerOpen())
            {
                isTimeout = false;
                System.out.println("============  断路器打开了，等待休眠期结束");
                Thread.sleep(6000);
            }
        }
    }


}
