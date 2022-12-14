package com.crazymaker.springcloud.distribute.lock;

import org.apache.curator.framework.recipes.locks.InterProcessMutex;

/**
 * build by 尼恩 @ 疯狂创客圈
 **/
public interface LockService
{

    /**
     * 取得ZK 的分布式锁
     *
     * @param key 锁的key
     * @return ZK 的分布式锁
     */
    InterProcessMutex getZookeeperLock(String key);
}
