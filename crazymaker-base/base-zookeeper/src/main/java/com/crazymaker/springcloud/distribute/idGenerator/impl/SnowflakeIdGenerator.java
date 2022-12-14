package com.crazymaker.springcloud.distribute.idGenerator.impl;


import com.crazymaker.springcloud.common.distribute.idGenerator.IdGenerator;

/**
 * create by 尼恩 @ 疯狂创客圈
 **/
public class SnowflakeIdGenerator implements IdGenerator {

    private SnowflakeIdWorker worker;
    private String type = "undefined";
    private boolean inited;


    public SnowflakeIdGenerator(String type) {
        this.type = type;
        worker = new SnowflakeIdWorker(type);

    }

    /**
     * 该项目的worker 节点 id
     */
    private long workerId;


    /**
     * 初始化单例
     * <p>
     * workerId 节点Id,最大8091
     *
     * @return the 单例
     */
    private synchronized void init() {
        workerId = worker.getId();
        if (workerId > MAX_WORKER_ID) {
            // zk分配的workerId过大
            throw new IllegalArgumentException("woker Id wrong: " + workerId);
        }
        inited = true;
    }

    private SnowflakeIdGenerator() {

    }


    /**
     * 开始使用该算法的时间为: 2017-01-01 00:00:00
     */
    private static final long START_TIME = 1483200000000L;

    /**
     * worker id 的bit数，最多支持8192个节点
     */
    private static final int WORKER_ID_BITS = 13;

    /**
     * 序列号，支持单节点最高每毫秒的最大ID数1024
     */
    private final static int SEQUENCE_BITS = 10;

    /**
     * 最大的 worker id ，8091
     * -1 的补码（二进制全1）右移13位, 然后取反
     */
    private final static long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);

    /**
     * 最大的序列号，1023
     * -1 的补码（二进制全1）右移10位, 然后取反
     */
    private final static long MAX_SEQUENCE = ~(-1L << SEQUENCE_BITS);

    /**
     * worker 节点编号的移位
     */
    private final static long APP_WORKER_ID_SHIFT = SEQUENCE_BITS;

    /**
     * 时间戳的移位
     */
    private final static long TIMESTAMP_LEFT_SHIFT = WORKER_ID_BITS + SEQUENCE_BITS;


    /**
     * 上次生成ID的时间戳
     */
    private long lastTimestamp = -1L;

    /**
     * 当前毫秒生成的序列
     */
    private long sequence = 0L;

    /**
     * Next id long.
     *
     * @return the nextId
     */
    public Long nextId() {
        if (!inited) {
            init();
        }
        return generateId();
    }

    /**
     * 生成唯一id的具体实现
     */
    private synchronized long generateId() {
        long current = System.currentTimeMillis();

        if (current < lastTimestamp) {
            // 如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过，出现问题返回-1
            return -1;
        }

        if (current == lastTimestamp) {
            // 如果当前生成id的时间还是上次的时间，那么对sequence序列号进行+1
            sequence = (sequence + 1) & MAX_SEQUENCE;

            if (sequence == MAX_SEQUENCE) {
                // 当前毫秒生成的序列数已经大于最大值，那么阻塞到下一个毫秒再获取新的时间戳
                current = this.nextMs(lastTimestamp);
            }
        } else {
            // 当前的时间戳已经是下一个毫秒
            sequence = 0L;
        }

        // 更新上次生成id的时间戳
        lastTimestamp = current;

        // 进行移位操作生成int64的唯一ID

        //时间戳右移动23位
        long time = (current - START_TIME) << TIMESTAMP_LEFT_SHIFT;

        //workerId 右移动10位
        long workerId = this.workerId << APP_WORKER_ID_SHIFT;

        return time | workerId | sequence;
    }

    /**
     * 阻塞到下一个毫秒
     */
    private long nextMs(long timeStamp) {
        long current = System.currentTimeMillis();
        while (current <= timeStamp) {
            current = System.currentTimeMillis();
        }
        return current;
    }


    public static void main(String[] args) {
        System.out.println("MAX_WORKER_ID = " + MAX_WORKER_ID);
        System.out.println("MAX_SEQUENCE = " + MAX_SEQUENCE);

        int SEQUENCE_BITS = 56;
        long MAX_SEQUENCE = ~(-1L << SEQUENCE_BITS);
        System.out.println("MAX_SEQUENCE = " + MAX_SEQUENCE);
        int WORKER_ID_BITS = 8;
        long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);
        System.out.println("MAX_WORKER_ID = " + MAX_WORKER_ID);

        Long workerIdPart=10L<< SEQUENCE_BITS ;
        Long   workerId =workerIdPart |  1L;
        System.out.println("workerId = " + workerId);

    }

}
