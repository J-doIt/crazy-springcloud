package com.crazymaker.demo.hystrix;

import com.netflix.hystrix.HystrixCommandMetrics;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;


@Slf4j
public class TakeTimeExecutedDemo
{

    /**
     * 演示用地址： demo-provider 的 REST 接口  /api/demo/hello/v1
     * 根据实际的地址调整
     */
    public static final String HELLO_TEST_URL =
            "http://127.0.0.1:7700/demo-provider/api/demo/hello/v1";

    public static final int COUNT = 50;



    @Test
    public void testToObservable() throws Exception
    {

        TakeTimeCommand command = null;
        for (int i = 0; i < COUNT; i++)
        {
            Thread.sleep(2);

            command = new TakeTimeCommand(HELLO_TEST_URL);
            command.toObservable()
                    .subscribe(result->result.length(),
                            error ->error.getCause()
                    );

//            command.toObservable()
//                    .subscribe(result -> log.info("result={}", result),
//                            error -> log.error("error={}", error)
//                    );
        }
        HystrixCommandMetrics.HealthCounts hc = command.getMetrics().getHealthCounts();
        log.info("window totalRequests：{},errorPercentage:{}",
                hc.getTotalRequests(),//滑动窗口总的请求数
                hc.getErrorPercentage());//滑动窗口出错比例

        Thread.sleep(Integer.MAX_VALUE);
    }


}