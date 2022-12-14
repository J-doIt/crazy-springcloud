package com.crazymaker.mq.demo;

import com.crazymaker.springcloud.common.util.ThreadUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.MessageSelector;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.*;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class RocketmqTranMessageTest {
    public static final String ROCKETMQ_SERVER = "cdh2:9876;cdh2:9877";
    private static final String TOPIC_TRAN= "Topic_Tran";
    private static final String TAG_A = "tag_Tran";
    private static final String GROUP = "Tran_TEST_GROUP";

    //    public static final String ROCKETMQ_SERVER = "cdh2:9876";
//    public static final String ROCKETMQ_SERVER = "192.168.56.122:9876";
    //Producer端发送同步消息
    @Test
    public void produceTran() throws Exception {
        TransactionListener transactionListener = new TransactionListenerImpl();
        TransactionMQProducer producer = new TransactionMQProducer(GROUP);
        ExecutorService executorService = new ThreadPoolExecutor(2, 5, 100, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(2000), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("client-transaction-msg-check-thread");
                return thread;
            }
        });
        producer.setExecutorService(executorService);

        //设置事务消息监听
        producer.setTransactionListener(transactionListener);
        // 设置NameServer的地址
        producer.setNamesrvAddr(ROCKETMQ_SERVER);
        producer.start();
        String[] tags = new String[] {"TagA", "TagB", "TagC", "TagD", "TagE"};
        for (int i = 0; i < 10; i++) {
            try {
                Message msg = new Message(TOPIC_TRAN, tags[i % tags.length], "id_" + i,
                                ("Java高并发 卷王 " + i).getBytes(RemotingHelper.DEFAULT_CHARSET));
                SendResult sendResult = producer.sendMessageInTransaction(msg, null);


                System.out.printf("%s%n", sendResult);
                Thread.sleep(10);
            } catch (MQClientException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        for (int i = 0; i < 100000; i++) {
            Thread.sleep(1000);
        }
        producer.shutdown();
    }


    //消费者和普通消费者一样
    @Test
    public void consumer() throws Exception {


        // 实例化消费者
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(GROUP);

        // 设置NameServer的地址
        consumer.setNamesrvAddr(ROCKETMQ_SERVER);

        // 订阅一个或者多个Topic，以及Tag来过滤需要消费的消息
        consumer.subscribe(TOPIC_TRAN, "*");
        // 注册回调实现类来处理从broker拉取回来的消息
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                System.out.printf("%s Receive New Messages: %s %n", Thread.currentThread().getName(), msgs);

                for (int i = 0; i < msgs.size(); i++) {
                    MessageExt msg = msgs.get(i);
                    String content = new String(msg.getBody());
                    log.info("收到消息：{}, {}", i, msg.getMsgId() + " " + msg.getTopic() + " " + msg.getTags() + " " + content);
                    try {
                        //消费者的业务代码
//                        redisSeckillServiceImpl.executeSeckill(dto);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                // 标记该消息已经被成功消费
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        // 启动消费者实例
        consumer.start();
        System.out.printf("Consumer Started.%n");
        ThreadUtil.sleepMilliSeconds(Integer.MAX_VALUE);
    }


    static class TransactionListenerImpl implements TransactionListener {
        private AtomicInteger transactionIndex = new AtomicInteger(0);
        private ConcurrentHashMap<String, Integer> localTrans = new ConcurrentHashMap<>();

        //执行业务，保存本地事务，下订单的操作
        @Override
        public LocalTransactionState executeLocalTransaction(Message msg, Object arg) {
            int value = transactionIndex.getAndIncrement();
            int status = value % 3;
            localTrans.put(msg.getTransactionId(), status);
            log.info("执行本地事务：{}, {}", msg.getTransactionId(), status);

//            return LocalTransactionState.COMMIT_MESSAGE;
            return LocalTransactionState.UNKNOW;
        }

        //这里查询本地事务状态 ，比如本地订单是否操作成功
        @Override
        public LocalTransactionState checkLocalTransaction(MessageExt msg) {
            Integer status = localTrans.get(msg.getTransactionId());
            log.info("检查 LocalTransaction ：{}, {}", msg.getTransactionId(), status);

            if (null != status) {
                switch (status) {
                    case 0:
                        return LocalTransactionState.UNKNOW;
                    case 1:
                        return LocalTransactionState.COMMIT_MESSAGE;
                    case 2:
                        return LocalTransactionState.ROLLBACK_MESSAGE;
                }
            }
            return LocalTransactionState.COMMIT_MESSAGE;
        }
    }
}
