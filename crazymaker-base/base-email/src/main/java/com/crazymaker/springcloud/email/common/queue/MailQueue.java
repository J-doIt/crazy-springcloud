package com.crazymaker.springcloud.email.common.queue;

import com.crazymaker.springcloud.email.common.model.Email;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 邮件队列

 * 创建时间	2017年8月4日
 *
 */
public class MailQueue {
	 //队列大小
    static final int QUEUE_MAX_SIZE   = 1000;

    static BlockingQueue<Email> blockingQueue = new LinkedBlockingQueue<Email>(QUEUE_MAX_SIZE);
    
    /**
     * 私有的默认构造子，保证外界无法直接实例化
     */
    private MailQueue(){};
    /**
     * 类级的内部类，也就是静态的成员式内部类，该内部类的实例与外部类的实例
     * 没有绑定关系，而且只有被调用到才会装载，从而实现了延迟加载
     */
    private static class SingletonHolder{
        /**
         * 静态初始化器，由JVM来保证线程安全
         */
		private  static MailQueue queue = new MailQueue();
    }
    //单例队列
    public static MailQueue getMailQueue(){
        return SingletonHolder.queue;
    }
    //生产入队
    public  void  produce(Email mail) throws InterruptedException {
    	blockingQueue.put(mail);
    }
    //消费出队
    public Email consume() throws InterruptedException {
        return blockingQueue.take();
    }
    // 获取队列大小
    public int size() {
        return blockingQueue.size();
    }
}
