package com.crazymaker.springcloud.standard.rateLimit.impl;

import com.crazymaker.springcloud.common.distribute.rateLimit.RateLimitService;
import com.crazymaker.springcloud.distribute.zookeeper.ZKClient;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.atomic.DistributedAtomicInteger;
import org.apache.curator.retry.RetryNTimes;

import java.util.LinkedHashMap;
import java.util.Map;

public class ZkRateLimitServiceImpl implements RateLimitService
{

    Map<String, DistributedAtomicInteger> counterMap = new LinkedHashMap<>();
    //秒杀的限流阈值
    public static final int MAX_ENTER = 50;

    /**
     * 取得ZK 的  分布式计数器
     *
     * @param key 锁的key
     * @return ZK 的   分布式计数器
     */
    public Boolean tryAcquire(String key)
    {
        CuratorFramework client = ZKClient.getSingleton().getClient();
        String path = "/counter/seckill/" + key;
        DistributedAtomicInteger counter = counterMap.get(key);
        if (null == counter)
        {
            counter = new DistributedAtomicInteger(client, path, new RetryNTimes(3, 5000));
            counterMap.put(key, counter);
        }

        try
        {
            if (counter.get().preValue() <= MAX_ENTER)
            {
                return false;
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return true;
    }

    /**
     * 创建一个限流的key
     *
     * @param type       类型
     * @param key        id
     * @param maxPermits 上限
     * @param rate       速度
     */
    @Override
    public void initLimitKey(String type, String key, Integer maxPermits, Integer rate)
    {

    }


}
