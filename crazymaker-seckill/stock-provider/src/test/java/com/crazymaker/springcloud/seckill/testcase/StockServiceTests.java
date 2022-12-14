package com.crazymaker.springcloud.seckill.testcase;

//import com.crazymaker.l2cache.config.J2CacheAutoConfiguration;
//import com.crazymaker.l2cache.config.J2CacheSpringCacheAutoConfiguration;
import com.crazymaker.springcloud.seckill.service.TestSkuStockService;
import com.crazymaker.springcloud.standard.config.CustomedRedisAutoConfiguration;
import com.crazymaker.springcloud.standard.lock.JedisMultiSegmentLock;
import com.crazymaker.springcloud.standard.lock.RedisLockService;
import com.crazymaker.springcloud.stock.start.StockCloudApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {StockCloudApplication.class,
        TestSkuStockService.class,
//        J2CacheAutoConfiguration.class,
//        J2CacheSpringCacheAutoConfiguration.class,
        CustomedRedisAutoConfiguration.class},
        properties = {
                "j2cache.config-location=classpath:j2cache-test.properties",
                "spring.cache.type=GENERIC",
                "j2cache.open-spring-cache=true",
                "j2cache.j2CacheConfig.serialization=json",
                "j2cache.redis-client=jedis",
                "j2cache.cache-clean-mode=active",
                "j2cache.allow-null-values=true",
                "j2cache.l2-cache-open=true"
        })
public class StockServiceTests {

    public static final long TEST_sku_ID = 1L;
    @Resource
    private TestSkuStockService testSkuStockService;

    @Test
    public void testInitStockAmount() throws IOException {
        testSkuStockService.initStockAmount(TEST_sku_ID, 10000, 10);

    }


    @Resource
    RedisLockService redisLockService;

    private ExecutorService pool = Executors.newFixedThreadPool(10);


    /**

     10个线程每个累加1000为： = 10000
     剩余库存为： = 0
     运行的时长为(ms)：30757.0
     每一次执行的时长为(ms)：3.0757

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
                            testSkuStockService.decreaseStock(TEST_sku_ID,index);
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
        int sumStockCount = testSkuStockService.sumStockCountById(TEST_sku_ID);
        System.out.println("剩余库存为： = " + sumStockCount);

        System.out.println("运行的时长为(ms)：" + time);
        System.out.println("每一次执行的时长为(ms)：" + time / sum);

    }


}
