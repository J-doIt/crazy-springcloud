package com.crazymaker.springcloud.seckill.main;

import com.crazymaker.redission.demo.RedissonManager;
import com.crazymaker.springcloud.common.dto.UserDTO;
import com.crazymaker.springcloud.common.util.ThreadUtil;
import com.crazymaker.springcloud.seckill.start.RedissionDemoCloudApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RBucket;
import org.redisson.api.RList;
import org.redisson.api.RLock;
import org.redisson.api.RLongAdder;
import org.redisson.api.RMap;
import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Slf4j
@RunWith(SpringRunner.class)
// 指定启动类
@SpringBootTest(classes = {RedissionDemoCloudApplication.class})
public class RedissionTest {

    @Resource
    RedissonManager redissonManager;

    @Test
    public void testRBucketExamples() {
        // 默认连接上 127.0.0.1:6379
        RedissonClient client = redissonManager.getRedisson();
        // RList 继承了 java.util.List 接口
        RBucket<String> rstring = client.getBucket("redission:test:bucket:string");
        rstring.set("this is a string");

        RBucket<UserDTO> ruser = client.getBucket("redission:test:bucket:user");
        UserDTO dto = new UserDTO();
        dto.setToken(UUID.randomUUID().toString());
        ruser.set(dto);
        System.out.println("string is: " + rstring.get());
        System.out.println("dto is: " + ruser.get());

        client.shutdown();
    }


    @Test
    public void testListExamples() {
        // 默认连接上 127.0.0.1:6379
        RedissonClient client = redissonManager.getRedisson();
        // RList 继承了 java.util.List 接口
        RList<String> nameList = client.getList("redission:test:nameList");
        nameList.clear();
        nameList.add("张三");
        nameList.add("李四");
        nameList.add("王五");
        nameList.remove(-1);


        System.out.println("List size: " + nameList.size());


        boolean contains = nameList.contains("李四");
        System.out.println("Is list contains name '李四': " + contains);

        nameList.forEach(System.out::println);


        client.shutdown();
    }


    @Test
    public void testMapExamples() {
        // 默认连接上 127.0.0.1:6379
        RedissonClient client = redissonManager.getRedisson();
        // RMap 继承了 java.util.concurrent.ConcurrentMap 接口
        RMap<String, Object> map = client.getMap("redission:test:personalMap");
        map.put("name", "张三");
        map.put("address", "北京");
        map.put("age", new Integer(50));

        System.out.println("Map size: " + map.size());

        boolean contains = map.containsKey("age");
        System.out.println("Is map contains key 'age': " + contains);
        String value = String.valueOf(map.get("name"));
        System.out.println("Value mapped by key 'name': " + value);

        client.shutdown();
    }

    @Test
    public void testLuaExamples() {
        // 默认连接上 127.0.0.1:6379
        RedissonClient redisson = redissonManager.getRedisson();

        redisson.getBucket("redission:test:foo").set("bar");
        String r = redisson.getScript().eval(RScript.Mode.READ_ONLY,
                "return redis.call('get', 'redission:test:foo')", RScript.ReturnType.VALUE);
        System.out.println("foo: " + r);

        // 通过预存的脚本进行同样的操作
        RScript s = redisson.getScript();
        // 首先将脚本加载到Redis
        String sha1 = s.scriptLoad("return redis.call('get', 'redission:test:foo')");
        // 返回值 res == 282297a0228f48cd3fc6a55de6316f31422f5d17
        System.out.println("sha1: " + sha1);
        // 再通过SHA值调用脚本
        Future<Object> r1 = redisson.getScript().evalShaAsync(RScript.Mode.READ_ONLY,
                sha1,
                RScript.ReturnType.VALUE,
                Collections.emptyList());

        try {
            System.out.println("res: " + r1.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        redisson.shutdown();
    }


    @Test
    public void testRAtomicLongExamples() {
        // 默认连接上 127.0.0.1:6379
        RedissonClient redisson = redissonManager.getRedisson();
        RAtomicLong atomicLong = redisson.getAtomicLong("redission:test:myLong");
        // 线程数
        final int threads = 10;
        // 每条线程的执行轮数
        final int turns = 1000;
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        long start = System.currentTimeMillis();
        for (int i = 0; i < threads; i++) {
            pool.submit(() ->
            {
                try {
                    for (int j = 0; j < turns; j++) {
                        atomicLong.incrementAndGet();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        ThreadUtil.sleepSeconds(5);
        long sum = atomicLong.get();
        System.out.println("atomicLong: " + sum);
        //输出统计结果
        float time = System.currentTimeMillis() - start;

        System.out.println("运行的时长为：" + time);
        System.out.println("每一次执行的时长为：" + time / sum);
        redisson.shutdown();
    }


    @Test
    public void testLockExamples() {
        // 默认连接上 127.0.0.1:6379
        RedissonClient redisson = redissonManager.getRedisson();
        // RLock 继承了 java.util.concurrent.locks.Lock 接口
        RLock lock = redisson.getLock("redission:test:lock:1");

        final int[] count = {0};
        int threads = 10;
        ExecutorService pool = Executors.newFixedThreadPool(10);
        CountDownLatch countDownLatch = new CountDownLatch(threads);
        long start = System.currentTimeMillis();
        for (int i = 0; i < threads; i++) {
            pool.submit(() ->
            {
                for (int j = 0; j < 1000; j++) {
                    lock.lock();

                    count[0]++;
                    lock.unlock();
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

        System.out.println("运行的时长为：" + time);
        System.out.println("每一次执行的时长为：" + time / count[0]);
    }

    @Test
    public void testRLongAdderExamples() {
        // 默认连接上 127.0.0.1:6379
        RedissonClient redisson = redissonManager.getRedisson();
        RLongAdder longAdder = redisson.getLongAdder("redission:test:myLongAdder");
        // 线程数
        final int threads = 10;
        // 每条线程的执行轮数
        final int turns = 1000;
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        long start = System.currentTimeMillis();
        for (int i = 0; i < threads; i++) {
            pool.submit(() ->
            {
                try {
                    for (int j = 0; j < turns; j++) {
                        longAdder.increment();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        ThreadUtil.sleepSeconds(5);
        System.out.println("longAdder: " + longAdder.sum());
        long sum = longAdder.sum();
        //输出统计结果
        float time = System.currentTimeMillis() - start;

        System.out.println("运行的时长为：" + time);
        System.out.println("每一次执行的时长为：" + time / sum);
        redisson.shutdown();

    }


    @Test
    public void testLockDemo() {
        // 默认连接上 127.0.0.1:6379
        RedissonClient redisson = redissonManager.getRedisson();
        // RLock 继承了 java.util.concurrent.locks.Lock 接口
        RLock disLock = redisson.getLock("DISLOCK");
//尝试获取分布式锁
        boolean isLock = false;
        try {
            isLock = disLock.tryLock(500, 25000, TimeUnit.MILLISECONDS);

            if (isLock) {

                //TODO if get lock success, do something;
                Thread.sleep(15000);
            }
        } catch (Exception e) {
        } finally {
            // 无论如何, 最后都要解锁
            disLock.unlock();
        }
    }


}