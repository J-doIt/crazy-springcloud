package com.crazymaker.springcloud.ratelimit;

import com.crazymaker.springcloud.demo.start.DemoCloudApplication;
import com.crazymaker.springcloud.standard.ratelimit.RedisRateLimitImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RunWith(SpringRunner.class)
// 指定启动类
@SpringBootTest(classes = {DemoCloudApplication.class})
/**
 * redis 分布式令牌桶测试类
 */
public class RedisRateLimitTest
{

    @Resource(name = "redisRateLimitImpl")
    RedisRateLimitImpl limitService;


    //线程池，用于多线程模拟测试
    private ExecutorService pool = Executors.newFixedThreadPool(10);

    @Test
    public void testRedisRateLimit()
    {

        //初始化的分布式令牌桶限流器
        limitService.initLimitKey(
                "seckill", //redis key 中的类型
                "10000",  //redis key 中的业务key，比如商品id
                2, //桶容量
                2); //每秒令牌数
        AtomicInteger count = new AtomicInteger();
        long start = System.currentTimeMillis();
        // 线程数
        final int threads = 2;
        // 每条线程的执行轮数
        final int turns = 20;
        // 同步器
        CountDownLatch countDownLatch = new CountDownLatch(threads);
        for (int i = 0; i < threads; i++)
        {
            pool.submit(() ->
            {
                try
                {

                    //每个用户，访问 turns 次
                    for (int j = 0; j < turns; j++)
                    {
                        boolean limited = limitService.tryAcquire("seckill:10000");
                        if (limited)
                        {
                            count.getAndIncrement();
                        }

                        Thread.sleep(200);
                    }

                } catch (Exception e)
                {
                    e.printStackTrace();
                }
                countDownLatch.countDown();

            });
        }
        try
        {
            countDownLatch.await();
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }



        float time = (System.currentTimeMillis() - start) / 1000F;
        //输出统计结果
        log.info("限制的次数为：" + count.get() + " 时长为：" + time);

        log.info("限制的次数为：" + count.get() +
                ",通过的次数为：" + (threads * turns - count.get()));
        log.info("限制的比例为：" + (float) count.get() / (float) (threads * turns));
        log.info("运行的时长为：" + time);
        try
        {
            Thread.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }


}
