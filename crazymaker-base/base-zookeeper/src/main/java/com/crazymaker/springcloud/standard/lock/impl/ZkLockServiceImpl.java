package com.crazymaker.springcloud.standard.lock.impl;

import com.crazymaker.springcloud.distribute.lock.LockService;
import com.crazymaker.springcloud.distribute.zookeeper.ZKClient;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ZkLockServiceImpl implements LockService
{

    Map<String, InterProcessMutex> lockMap = new ConcurrentHashMap<>();


    /**
     * 取得ZK 的分布式锁
     *
     * @param key 锁的key
     * @return ZK 的分布式锁
     */
    public InterProcessMutex getZookeeperLock(String key)
    {
        CuratorFramework client = ZKClient.getSingleton().getClient();
        InterProcessMutex lock = lockMap.get(key);
        if (null == lock)
        {
            lock = new InterProcessMutex(client, "/mutex/seckill/" + key);
            lockMap.put(key, lock);
        }
        return lock;
    }

}
