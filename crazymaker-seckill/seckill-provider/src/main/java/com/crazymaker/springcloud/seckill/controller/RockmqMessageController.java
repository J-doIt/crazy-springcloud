package com.crazymaker.springcloud.seckill.controller;

import com.crazymaker.springcloud.common.exception.BusinessException;
import com.crazymaker.springcloud.common.result.RestOut;
import com.crazymaker.springcloud.common.util.JsonUtil;
import com.crazymaker.springcloud.seckill.api.dto.SeckillDTO;
import com.crazymaker.springcloud.seckill.service.impl.RedisSeckillServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/seckill/rockmq/")
@Api(tags = "Rockmq消息Demo")
@Slf4j
public class RockmqMessageController implements ApplicationContextAware {
    public static final String TOPIC_SECKILL = "Seckill_Topic";
    DefaultMQProducer producer = new DefaultMQProducer("seckill_producer");

    @Value("${rocketmq.address}")
    private String rocketmqAddress;

    /**
     * 秒杀服务实现 Bean
     */
    @Resource
    RedisSeckillServiceImpl redisSeckillServiceImpl;

    /**
     * Consumer Group,非常重要的概念，后续会慢慢补充
     */
    DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("seckill_consumer");

    {
        consumer.setMessageModel(MessageModel.CLUSTERING);
    }

    SendCallback sendCallback = new SendCallback() {

        @Override
        public void onSuccess(SendResult sendResult) {

            log.info("消息发送成功: {}", sendResult.toString());
        }

        @Override
        public void onException(Throwable e) {
            log.error("消息发送失败:", e);
            // 消息发送失败，可以持久化这条数据，后续进行补偿处理
        }
    };

    /**
     * 执行秒杀的操作
     * <p>
     * <p>
     * {
     * "exposedKey": "4b70903f6e1aa87788d3ea962f8b2f0e",
     * "newStockNum": 10000,
     * "seckillSkuId": 1157197244718385152,
     * "seckillToken": "0f8459cbae1748c7b14e4cea3d991000",
     * "userId": 37
     * }
     *
     * @return
     */
    @ApiOperation(value = "发送秒杀")
    @PostMapping("/sendSeckill/v1")
    RestOut<String> sendSeckill(@RequestBody SeckillDTO dto) {
        String content = JsonUtil.pojoToJson(dto);

        try {
            //构建消息
            Message msg = new Message(TOPIC_SECKILL /* Topic */,
                    "TagA" /* Tag */,
                    (content).getBytes(RemotingHelper.DEFAULT_CHARSET)
            );
//            msg.setKeys(dto.);
            //异步发送消息
            producer.send(msg, sendCallback);

            //同步发送消息
//            SendResult sendResult = producer.send(msg);

//            log.info("发送完成 {}", sendResult.getMsgId());
        } catch (Exception e) {
            e.printStackTrace();
            throw BusinessException.builder().errMsg(e.getMessage()).build();
        }

        return RestOut.success("发送完成");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

        startProducer();
        startConsumer();
    }

    private void startProducer() {
        //指定NameServer地址
        producer.setNamesrvAddr(rocketmqAddress); //修改为自己的
        producer.setInstanceName("Instance1");
        producer.setRetryTimesWhenSendFailed(3);
        /**
         * Producer对象在使用之前必须要调用start初始化，初始化一次即可
         * 注意：切记不可以在每次发送消息时，都调用start方法
         */
        try {
            producer.start();
            log.info("product start ...");
        } catch (MQClientException e) {
            e.printStackTrace();
        }
    }

    private void startConsumer() {
        //指定NameServer地址，多个地址以 ; 隔开
        consumer.setNamesrvAddr(rocketmqAddress); //修改为自己的
//        consumer.setNamesrvAddr("192.168.116.115:9876;192.168.116.116:9876"); //修改为自己的


/**
 * 设置Consumer第一次启动是从队列头部开始消费还是队列尾部开始消费
 * 如果非第一次启动，那么按照上次消费的位置继续消费
 */
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);

        try {
            consumer.subscribe(TOPIC_SECKILL, "*");
        } catch (MQClientException e) {
            e.printStackTrace();
            return;
        }

        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                for (int i = 0; i < msgs.size(); i++) {
                    MessageExt msg = msgs.get(i);
                    String content = new String(msg.getBody());
                    log.info("收到消息：{}", msg.getMsgId() + " " + msg.getTopic() + " " + msg.getTags() + " " + content);
                    SeckillDTO dto = JsonUtil.jsonToPojo(content, SeckillDTO.class);
                    try {
                        redisSeckillServiceImpl.executeSeckill(dto);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        try {
            consumer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }

    }
}
