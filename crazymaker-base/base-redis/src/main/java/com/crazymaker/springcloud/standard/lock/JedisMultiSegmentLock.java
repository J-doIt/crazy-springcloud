package com.crazymaker.springcloud.standard.lock;

import com.crazymaker.springcloud.common.util.RandomUtil;
import com.crazymaker.springcloud.common.util.ThreadUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

@Slf4j
@Data
@AllArgsConstructor
public class JedisMultiSegmentLock implements Lock {

    public static final int NO_SEG = -1;
    //拿到锁的线程
    private Thread thread;

    //拿到锁的状态
    private volatile boolean isLocked = false;

    //段数
    private final int segAmount;

    public static final int DEFAULT_TIMEOUT = 2000;
    public static final Long WAIT_GAT = Long.valueOf(100);

    //内部的锁
    InnerLock[] innerLocks = null;

    //被锁住的分段
    int segmentIndexLocked = NO_SEG;
    /**
     * 默认为2000ms
     */
    long expire = 2000L;
    int segmentIndex = 0;

    public JedisMultiSegmentLock(String lockKey, String requestId, int segAmount) {
        this.segAmount = segAmount;
        innerLocks = new InnerLock[segAmount];
        for (int i = 0; i < this.segAmount; i++) {
            //每一个分段，加上一个编号
            String innerLockKey = lockKey + ":" + i;
            innerLocks[i] = new InnerLock(innerLockKey, requestId);
        }
        segmentIndex = RandomUtil.randInModLower(this.segAmount);
    }


    /**
     * 获取一个分布式锁 , 超时则返回失败
     *
     * @return 获锁成功 - true | 获锁失败 - false
     */
    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {

        //本地可重入
        if (isLocked && thread == Thread.currentThread()) {
            return true;
        }
        expire = unit != null ? unit.toMillis(time) : DEFAULT_TIMEOUT;
        long startMillis = System.currentTimeMillis();
        Long millisToWait = expire;

        boolean localLocked = false;

        int turn = 1;

        InnerLock innerLock = innerLocks[segmentIndex];

        while (!localLocked) {

            localLocked = innerLock.lock(expire);
            if (!localLocked) {
                millisToWait = millisToWait - (System.currentTimeMillis() - startMillis);
                startMillis = System.currentTimeMillis();
                if (millisToWait > 0L) {
                    /**
                     * 还没有超时
                     */
                    ThreadUtil.sleepMilliSeconds(WAIT_GAT);
                    log.info("睡眠一下，重新开始，turn:{},剩余时间：{}", turn++, millisToWait);

                    segmentIndex++;
                    if (segmentIndex >= this.segAmount) {
                        segmentIndex = 0;
                    }
                    innerLock = innerLocks[segmentIndex];
                } else {
                    log.info("抢锁超时");
                    return false;
                }
            } else {
                segmentIndexLocked = segmentIndex;
                isLocked = true;
                localLocked = true;
                thread = Thread.currentThread();
            }
        }
        return isLocked;
    }


    /**
     * 抢夺锁
     */
    @Override
    public void lock() {
        throw new IllegalStateException(
                "方法 'lock' 尚未实现!");
    }


    //释放锁
    @Override
    public void unlock() {
        if (segmentIndexLocked == NO_SEG) {
            return;
        }
        this.innerLocks[segmentIndexLocked].unlock();

        segmentIndexLocked = NO_SEG;
        thread = null;
        isLocked = false;
    }

    @Override
    public Condition newCondition() {
        throw new IllegalStateException(
                "方法 'newCondition' 尚未实现!");
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        throw new IllegalStateException(
                "方法 'lockInterruptibly' 尚未实现!");

    }


    @Override
    public boolean tryLock() {
        throw new IllegalStateException(
                "方法 'tryLock' 尚未实现!");
    }

}