package com.crazymaker.springcloud.ratelimit;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

// 漏桶 限流
@Slf4j
public class LeakBucketLimiter {

    // 计算的起始时间
    private static long lastOutTime = System.currentTimeMillis();

    // 时间区间的时间间隔 ms
    private static long interval = 1000;

    // 流出速率 每秒 2 次
    private static int leakRate = 2;

    // 桶的容量
    private static int capacity = 20;

    //剩余的水量
    private static AtomicInteger waterInBucket = new AtomicInteger(0);

    //返回值说明：
    // false 没有被限制到
    // true 被限流
    public static synchronized boolean isLimit(long taskId, int turn) {
        // 如果是空桶，就当前时间作为漏出的时间
        if (waterInBucket.get() == 0) {
            lastOutTime = System.currentTimeMillis();
            waterInBucket.addAndGet(1);
            return false;
        }

        //补充上遗漏的场景， 讲课的意义，不断的优化代码
        //场景三：当前请求和上次请求，在同一个时间区间
        long nowTime = System.currentTimeMillis();
        //当前时间，在时间区间之内
        //漏水以时间区间为计算维度，同一个区间，没有必要重复去计算漏水
        if (nowTime < lastOutTime + interval) {
            // 尝试加水,并且水还未满 ，放行
            if ((waterInBucket.get()) < capacity) {
                waterInBucket.addAndGet(1);
                return false;
            } else {
                // 水满，拒绝加水， 限流
                return true;
            }
        }

        //场景二： 桶里边有水
        //当前时间，在时间区间之外
        // 计算漏水，以时间的区间为维度的
        int waterLeaked = ((int) ((System.currentTimeMillis() - lastOutTime) / 1000)) * leakRate;

        // 计算剩余水量
        int waterLeft = waterInBucket.get() - waterLeaked;

        //校正数据
        waterLeft = Math.max(0, waterLeft);
        waterInBucket.set(waterLeft);
        // 重新更新leakTimeStamp
        lastOutTime = System.currentTimeMillis();
        // 尝试加水,并且水还未满 ，放行
        if ((waterInBucket.get()) < capacity) {
            waterInBucket.addAndGet(1);
            return false;
        } else {
            // 水满，拒绝加水， 限流
            return true;
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
        // 线程同步器
        CountDownLatch countDownLatch = new CountDownLatch(threads);
        long start = System.currentTimeMillis();
        for (int i = 0; i < threads; i++) {
            pool.submit(() ->
            {
                try {

                    for (int j = 0; j < turns; j++) {

                        long taskId = Thread.currentThread().getId();
                        boolean intercepted = isLimit(taskId, j);
                        if (intercepted) {
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
