package com.crazymaker.mq.demo;

import com.crazymaker.springcloud.common.util.ThreadUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.*;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
public class RocketmqDelayMessageTest {
    public static final String ROCKETMQ_SERVER = "cdh2:9876;cdh2:9877";
    public static final String TOPIC_TEST = "TopicDelayed";

    //    public static final String ROCKETMQ_SERVER = "cdh2:9876";
//    public static final String ROCKETMQ_SERVER = "192.168.56.122:9876";
    //Producer端发送同步消息
   
    @Test
    public void producerDelayed() throws Exception {
        // 实例化一个生产者来产生延时消息
        DefaultMQProducer producer = new DefaultMQProducer("ExampleProducerGroup");

        producer.setNamesrvAddr(ROCKETMQ_SERVER);
             // 启动生产者
            producer.start();
            int totalMessagesToSend = 100;
            for (int i = 0; i < totalMessagesToSend; i++) {
                Message message = new Message(TOPIC_TEST, ("Java高并发 卷王 " + i).getBytes());
                // 设置延时等级3,这个消息将在10s之后发送(现在只支持固定的几个时间,详看delayTimeLevel)
                message.setDelayTimeLevel(3);
                 // 发送消息
                producer.send(message);
            }
            // 关闭生产者
            producer.shutdown();
    }

    @Test
    public void consumerDelayed() throws Exception {

        // 实例化消费者
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("ExampleConsumer");
        consumer.setNamesrvAddr(ROCKETMQ_SERVER);
        // 订阅Topics
        consumer.subscribe(TOPIC_TEST, "*");
        // 注册消息监听者
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> messages, ConsumeConcurrentlyContext context) {
                for (MessageExt msg : messages) {
                    // Print approximate delay time period
                    System.out.println("Receive msg[msgId=" + msg.getMsgId() + "]  延迟：" + (System.currentTimeMillis() - msg.getStoreTimestamp()) + " ms");

                    String content = new String(msg.getBody());
                    log.info("收到消息：{}", msg.getMsgId() + " " + msg.getTopic() + " " + msg.getTags() + " " + content);

                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        // 启动消费者
        consumer.start();
        ThreadUtil.sleepMilliSeconds(Integer.MAX_VALUE);

    }

}
