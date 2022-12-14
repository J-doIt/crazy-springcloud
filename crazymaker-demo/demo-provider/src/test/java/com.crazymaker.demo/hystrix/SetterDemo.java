package com.crazymaker.demo.hystrix;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.HystrixThreadPoolProperties;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SetterDemo
{

    /**
     * 获取配置
     *
     * @param groupKey
     * @param commandKey
     * @param threadPoolKey
     * @return
     */
    public static HystrixCommand.Setter buildSetter(
            String groupKey,
            String commandKey,
            String threadPoolKey)
    {
        /**
         * 与命令执行相关的一些属性集
         */
        HystrixCommandProperties.Setter commandSetter =
                HystrixCommandProperties.Setter()
                        //至少有3个请求, 断路器才达到熔断触发的次数阈值
                        .withCircuitBreakerRequestVolumeThreshold(3)
                        //熔断器中断请求5秒后会进入 half-open 状态, 进行尝试放行
                        .withCircuitBreakerSleepWindowInMilliseconds(5000)
                        //错误率超过60%,快速失败
                        .withCircuitBreakerErrorThresholdPercentage(60)
                        //启用超时
                        .withExecutionTimeoutEnabled(true)
                        //执行的超时时间，默认为 1000 ms
                        .withExecutionTimeoutInMilliseconds(5000)
                        // 可统计的滑动窗口内的buckets数量,用于熔断器和指标发布
                        .withMetricsRollingStatisticalWindowBuckets(10)
                        // 可统计的滑动窗口的时间长度
                        // 这段时间内的执行数据用于熔断器和指标发布
                        .withMetricsRollingStatisticalWindowInMilliseconds(10000);
        /**
         * 线程池配置
         */
        HystrixThreadPoolProperties.Setter poolSetter =
                HystrixThreadPoolProperties.Setter()
                        //这里我们设置了线程池大小为5
                        .withCoreSize(5)
                        .withMaxQueueSize(50)
                        .withQueueSizeRejectionThreshold(50)
                        .withMaximumSize(5);


        /**
         * 与线程池相关的一些属性集
         */
        HystrixCommandGroupKey hGroupKey = HystrixCommandGroupKey.Factory.asKey(groupKey);
        HystrixCommandKey hCommondKey = HystrixCommandKey.Factory.asKey(commandKey);
        HystrixThreadPoolKey hThreadPoolKey = HystrixThreadPoolKey.Factory.asKey(threadPoolKey);
        HystrixCommand.Setter outerSetter = HystrixCommand.Setter
                .withGroupKey(hGroupKey)
                .andCommandKey(hCommondKey)
                .andThreadPoolKey(hThreadPoolKey)
                .andCommandPropertiesDefaults(commandSetter)
                .andThreadPoolPropertiesDefaults(poolSetter);
        return outerSetter;
    }


}