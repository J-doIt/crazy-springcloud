package com.crazymaker.cloud.ha.middleware.service.impl;
import com.alibaba.druid.support.json.JSONUtils;
import com.crazymaker.springcloud.common.dto.UserDTO;
import com.crazymaker.springcloud.common.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import static com.crazymaker.cloud.ha.middleware.config.HAConstant.MQ_TOPIC;

@Slf4j
@Component
public class MQConsumerServiceImpl {

    // topic需要和生产者的topic一致，consumerGroup属性是必须指定的，内容可以随意
    // selectorExpression 指的就是tag，默认为“*”，不设置的话会监听所有消息
    @Service
    @RocketMQMessageListener(topic = MQ_TOPIC, selectorExpression = "tag1", consumerGroup = "Con_Group_One")
    public class Consumer1 implements RocketMQListener<UserDTO> {
        // 监听到消息就会执行此方法
        @Override
        public void onMessage(UserDTO user) {
            log.info("监听到消息：user={}", JSONUtils.toJSONString(user));
        }
    }

    // 注意：这个ConsumerSend2和上面ConsumerSend在没有添加tag做区分时，不能共存，
    // 不然生产者发送一条消息，这两个都会去消费，如果类型不同会有一个报错，所以实际运用中最好加上tag，写这只是让你看知道就行
    @Service
    @RocketMQMessageListener(topic = MQ_TOPIC, consumerGroup = "Con_Group_Two")
    public class ConsumerSend2 implements RocketMQListener<String> {
        @Override
        public void onMessage(String str) {
            log.info("监听到消息：str={}", str);
        }
    }

	// MessageExt：扩展消息类型，
    // 不管发送的是String还是对象，都可接收，当然也可以像上面明确指定类型，
    // 当然，指定类型较方便，制定类型，控制比较细粒度
    @Service
    @RocketMQMessageListener(topic = MQ_TOPIC, selectorExpression = "tag2", consumerGroup = "Con_Group_Three")
    public class Consumer implements RocketMQListener<MessageExt> {
        @Override
        public void onMessage(MessageExt messageExt) {
            byte[] body = messageExt.getBody();
            String msg = new String(body);
            log.info("监听到消息：msg={}", msg);
        }
    }

}
