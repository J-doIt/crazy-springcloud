package com.crazymaker.demo.hystrix;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandMetrics;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolKey;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class CircuitBreakerDemo
{
    //执行的总次数，线程安全
    private static AtomicInteger total = new AtomicInteger(0);

    /**
     * 内部类：一个能够设置运行时长的自定义命令类
     */
    static class TakeTimeDemoCommand extends HystrixCommand<String>
    {

        //run 方法 是否执行
        private boolean hasRun = false;
        //执行的次序
        private int index;
        // 运行的占用时间
        long takeTime;

        public TakeTimeDemoCommand(long takeTime, Setter setter)
        {
            super(setter);
            this.takeTime = takeTime;
        }

        @Override
        protected String run() throws Exception
        {
            hasRun = true;
            index = total.incrementAndGet();

            Thread.sleep(takeTime);
            HystrixCommandMetrics.HealthCounts hc = super.getMetrics().getHealthCounts();
            log.info("succeed- req{}:断路器状态：{}, 失败率：{}%",
                    index, super.isCircuitBreakerOpen(), hc.getErrorPercentage());
            return "req" + index + ":succeed";
        }

        @Override
        protected String getFallback()
        {
            //是否直接失败
            boolean isFastFall = !hasRun;
            if (isFastFall)
            {
                index = total.incrementAndGet();
            }
            HystrixCommandMetrics.HealthCounts hc = super.getMetrics().getHealthCounts();
            log.info("fallback- req{}:断路器状态：{}, 失败率：{}%",
                    index, super.isCircuitBreakerOpen(), hc.getErrorPercentage());
            return "req" + index + ":failed";
        }

    }

    /**
     * 测试用例：断路器熔断
     */

    @Test
    public void testCircuitBreaker() throws Exception
    {
        /**
         * 命令参数配置
         */
        HystrixCommandProperties.Setter propertiesSetter =
                HystrixCommandProperties.Setter()
                        //至少有3个请求, 断路器才达到熔断触发的次数阈值
                        .withCircuitBreakerRequestVolumeThreshold(3)
                        //熔断器中断请求5秒后会进入 half-open 状态, 进行尝试放行
                        .withCircuitBreakerSleepWindowInMilliseconds(5000)
                        //错误率超过60%,快速失败
                        .withCircuitBreakerErrorThresholdPercentage(60)
                        //启用超时
                        .withExecutionTimeoutEnabled(true)
                        //执行的超时时间，默认为 1000 ms，这里设置为500ms
                        .withExecutionTimeoutInMilliseconds(500)
                        // 可统计的滑动窗口内的buckets数量,用于熔断器和指标发布
                        .withMetricsRollingStatisticalWindowBuckets(10)
                        // 可统计的滑动窗口的时间长度
                        // 这段时间内的执行数据用于熔断器和指标发布
                        .withMetricsRollingStatisticalWindowInMilliseconds(10000);

        HystrixCommand.Setter rpcPool = HystrixCommand.Setter
                .withGroupKey(HystrixCommandGroupKey.Factory.asKey("group-1"))
                .andCommandKey(HystrixCommandKey.Factory.asKey("command-1"))
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("threadPool-1"))
                .andCommandPropertiesDefaults(propertiesSetter);

        /**
         * 首先设置运行时间800ms，大于命令的超时限制 500ms
         */
        long takeTime = 800;
        for (int i = 1; i <= 10; i++)
        {

            TakeTimeDemoCommand command = new TakeTimeDemoCommand(takeTime, rpcPool);
            command.execute();

            //健康信息
            HystrixCommandMetrics.HealthCounts hc = command.getMetrics().getHealthCounts();
            if (command.isCircuitBreakerOpen())
            {
                /**
                 * 熔断之后，设置运行时间300ms，小于命令的超时限制 500ms
                 */
                takeTime = 300;
                log.info("============  断路器打开了，等待休眠期（默认5秒）结束");

                /**
                 * 等待7s之后，再一次发起请求
                 */
                Thread.sleep(7000);
            }

        }

        Thread.sleep(Integer.MAX_VALUE);

    }
}
