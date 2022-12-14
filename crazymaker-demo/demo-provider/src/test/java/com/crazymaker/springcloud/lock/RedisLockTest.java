package com.crazymaker.springcloud.lock;

import com.crazymaker.springcloud.demo.start.DemoCloudApplication;
import com.crazymaker.springcloud.standard.lock.JedisMultiSegmentLock;
import com.crazymaker.springcloud.standard.lock.RedisLockService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {DemoCloudApplication.class})
// 指定启动类
public class RedisLockTest {

    @Resource
    RedisLockService redisLockService;

    private ExecutorService pool = Executors.newFixedThreadPool(10);


    /**
     * 10个线程每个累加1000为： = 10000
     * 运行的时长为(ms)：35589.0
     * 每一次执行的时长为(ms)：3.5589
     */
    @Test
    public void testLock() {
        int threads = 10;
        final int[] count = {0};
        CountDownLatch countDownLatch = new CountDownLatch(threads);
        long start = System.currentTimeMillis();
        for (int i = 0; i < threads; i++) {
            pool.submit(() ->
            {
                String requestId = UUID.randomUUID().toString();
                for (int j = 0; j < 1000; j++) {

                    Lock lock = redisLockService.getLock("test:lock:1", requestId);

                    boolean locked = false;
                    try {
                        locked = lock.tryLock(20, TimeUnit.SECONDS);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (locked) {
                        try {

                            count[0]++;

                            if (count[0] % 100 == 0)
                                log.info("count = " + count[0]);

                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            lock.unlock();
                        }
                    } else {
                        System.out.println("抢锁失败");
                    }
                }
                countDownLatch.countDown();
            });
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("10个线程每个累加1000为： = " + count[0]);
        //输出统计结果
        float time = System.currentTimeMillis() - start;

        System.out.println("运行的时长为(ms)：" + time);
        System.out.println("每一次执行的时长为(ms)：" + time / count[0]);

    }

    /**
     * 10个线程每个累加1000为： = 10000
     * 运行的时长为(ms)：15954.0
     * 每一次执行的时长为(ms)：1.5954
     */

    @Test
    public void testSegmentLock() {
        int threads = 10;
        final int[] count = new int[threads];
        CountDownLatch countDownLatch = new CountDownLatch(threads);
        long start = System.currentTimeMillis();
        for (int i = 0; i < threads; i++) {
            pool.submit(() ->
            {
                String requestId = UUID.randomUUID().toString();
                JedisMultiSegmentLock lock = redisLockService.getSegmentLock("test:segmentLock:1", requestId, threads);
                for (int j = 0; j < 1000; j++) {
                    boolean locked = false;
                    try {
                        locked = lock.tryLock(20, TimeUnit.SECONDS);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (locked) {
                        try {
                            int index = lock.getSegmentIndexLocked();
                            count[index]++;

                            if (count[index] % 100 == 0)
                                log.info("index= {}, count = {} ", index, count[index]);

                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            lock.unlock();
                        }
                    } else {
                        System.out.println("抢锁失败");
                    }
                }
                countDownLatch.countDown();
                log.info("线程 执行完成 ");
            });
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //输出统计结果
        float time = System.currentTimeMillis() - start;

        int sum = Arrays.stream(count).reduce(0, Integer::sum);
        System.out.println("10个线程每个累加1000为： = " + sum);

        System.out.println("运行的时长为(ms)：" + time);
        System.out.println("每一次执行的时长为(ms)：" + time / sum);

    }


}


