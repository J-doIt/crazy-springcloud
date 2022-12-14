package com.crazymaker.demo.hystrix;

import com.crazymaker.springcloud.common.util.HttpRequestUtil;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandMetrics;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class HttpGetterCommand extends HystrixCommand<String>
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


    public HttpGetterCommand(String url, Setter setter)
    {
        super(setter);
        this.url = url;
    }

    @Override
    protected String run() throws Exception
    {
        hasRun = true;
        index = total.incrementAndGet();
        log.info("req{} begin...", index);
        String responseData = HttpRequestUtil.simpleGet(url);
        log.info(" req{} end: {}", index, responseData);
        return "req" + index + ":" + responseData;
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
        if (super.isCircuitBreakerOpen())
        {
            HystrixCommandMetrics.HealthCounts hc = super.getMetrics().getHealthCounts();
            log.info("window totalRequests：{},errorPercentage:{}",
                    hc.getTotalRequests(),//滑动窗口总的请求数
                    hc.getErrorPercentage());//滑动窗口出错比例
        }


        //断路器是否打开
        boolean isCircuitBreakerOpen = isCircuitBreakerOpen();
        log.info("req{} fallback: 熔断{},直接失败 {}，失败次数{}",
                index,
                isCircuitBreakerOpen,
                isFastFall,
                failed.incrementAndGet());

        return "req" + index + ":调用失败";
    }

}