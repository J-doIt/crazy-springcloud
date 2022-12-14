package com.crazymaker.demo.hystrix;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandMetrics;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.HystrixThreadPoolProperties;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class TakeTimeCommand extends HystrixCommand<String>
{

    private String url;
    //run 方法 是否执行
    private boolean hasRun = false;
    //执行的次序
    private int index;
    //执行的总次数，线程安全
    private static AtomicInteger total = new AtomicInteger(0);

    //失败的总次数，线程安全
    private static AtomicInteger failed = new AtomicInteger(0);

    /**
     * 使用统一配置类
     */
  final static   HystrixCommand.Setter  setter = SetterDemo.buildSetter(
            "group-1",
            "testCommand",
            "testThreadPool");

    public TakeTimeCommand(String url)
    {
        super(setter);
        this.url = url;
    }

    @Override
    protected String run() throws Exception
    {
        hasRun = true;
        index = total.incrementAndGet();

        Thread.sleep(1000);

//        throw  new RuntimeException("biz error");
        //断路器是否打开
        boolean broken = isCircuitBreakerOpen();

        HystrixCommandMetrics.HealthCounts hc = super.getMetrics().getHealthCounts();
        log.info("//run req{},totalRequests：{},errorPercentage:{},断否:{}",
                index,
                hc.getTotalRequests(),//滑动窗口总的请求数
                hc.getErrorPercentage(),//滑动窗口出错比例
                broken  );


        return "req" + index + ": success" ;
    }


    protected String getFallback()
    {
        //是否直接失败
        boolean isFastFall = !hasRun;
        if (isFastFall)
        {
            index = total.incrementAndGet();
        }
        if (super.isCircuitBreakerOpen())
        {
            log.error("isCircuitBreakerOpen ");

        }
        //断路器是否打开
        boolean broken = isCircuitBreakerOpen();

        HystrixCommandMetrics.HealthCounts hc = super.getMetrics().getHealthCounts();
        log.info("/getFallback req{}, totalRequests：{},errorPercentage:{},断否:{}",
                index,
                hc.getTotalRequests(),//滑动窗口总的请求数
                hc.getErrorPercentage(),//滑动窗口出错比例
                broken  );

        return "req" + index + ":调用失败";
    }



    @Slf4j
    static class SetterDemo
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
                            .withCircuitBreakerRequestVolumeThreshold(1)
                            //熔断器中断请求5秒后会进入 half-open 状态, 进行尝试放行
                            .withCircuitBreakerSleepWindowInMilliseconds(1000)
                            //错误率超过60%,快速失败
                            .withCircuitBreakerErrorThresholdPercentage(60)
                            //启用超时
                            .withCircuitBreakerEnabled(true)
                            .withExecutionTimeoutEnabled(true)
                            //执行的超时时间，默认为 1000 ms
                            .withExecutionTimeoutInMilliseconds(500)
                            // 可统计的滑动窗口内的buckets数量,用于熔断器和指标发布
                            .withMetricsRollingStatisticalWindowBuckets(10)
                            // 可统计的滑动窗口的时间长度
                            // 这段时间内的执行数据用于熔断器和指标发布
                            .withMetricsRollingStatisticalWindowInMilliseconds(1000);
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
}