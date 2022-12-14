package com.crazymaker.springcloud.ratelimit;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

// 计速器 限速
@Slf4j
public class CounterLimiter {

    // 起始时间
    private static long lastTime = System.currentTimeMillis();
    // 时间区间的时间间隔 ms
    private static long interval = 1000;
    // 每秒限制数量
    private static long capacity = 2;
    //累加器
    private static AtomicLong counter = new AtomicLong();

    //返回正数，表示没有被限流
    //返回-1 ，表示被限流
    // 计数判断, 是否超出限制
    private static long tryPass(long requestId, int turn) {
        long nowTime = System.currentTimeMillis();
        //当前时间，在时间区间之内
        if (nowTime < lastTime + interval) {
            //增加计数
            long count = counter.incrementAndGet();

            if (count <= capacity) {
                return count;
            } else {
                return -1;  // 容量耗尽
            }
        } else {
            //在时间区间之外
            synchronized (CounterLimiter.class) {
                log.info("新时间区到了,requestId{}, turn {}..", requestId, turn);
                // 再一次判断，防止重复初始化
                // dc 双重校验
                if (nowTime > lastTime + interval) {
                    //reset counter
                    counter.set(1);
                    lastTime = nowTime;
                }else {
                    //补充上遗漏的场景， 计数器算法看似简单，实际上陷阱重重
                    //增加计数
                    long count = counter.incrementAndGet();

                    if (count <= capacity) {
                        return count;
                    } else {
                        return -1;  // 容量耗尽
                    }
                }
            }
            return 1;
        }
    }

    //线程池，用于多线程模拟测试
    private ExecutorService pool = Executors.newFixedThreadPool(10);

    @Test
    public void testLimit() {

        // 被限制的次数
        AtomicInteger limited = new AtomicInteger(0);
        // 线程数
        final int threads = 2;
        // 每条线程的执行轮数
        final int turns = 20;
        // 同步器
        CountDownLatch countDownLatch = new CountDownLatch(threads);
        long start = System.currentTimeMillis();
        for (int i = 0; i < threads; i++) {
            pool.submit(() ->
            {
                try {

                    for (int j = 0; j < turns; j++) {

                        long taskId = Thread.currentThread().getId();
                        long index = tryPass(taskId, j);
                        System.out.println("index = " + index);

                        if (index <= 0) {
                            // 被限制的次数累积
                            limited.getAndIncrement();

                        }
                        Thread.sleep(200);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
                //等待所有线程结束
                countDownLatch.countDown();

            });
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        float time = (System.currentTimeMillis() - start) / 1000F;
        //输出统计结果

        log.info("限制的次数为：" + limited.get() +
                ",通过的次数为：" + (threads * turns - limited.get()));
        log.info("限制的比例为：" + (float) limited.get() / (float) (threads * turns));
        log.info("运行的时长为：" + time);
    }


}
