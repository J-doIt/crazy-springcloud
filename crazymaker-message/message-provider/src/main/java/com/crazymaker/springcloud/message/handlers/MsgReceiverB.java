package com.crazymaker.springcloud.message.handlers;

import com.crazymaker.springcloud.message.annotation.MqSubscriber;
import com.crazymaker.springcloud.message.annotation.TopicConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@MqSubscriber(topic = "test.topic.b" )
public class MsgReceiverB
{

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @TopicConsumer(group = "test.group.a" )
    public boolean processGroupA(String content)
    {
        logger.info(" MsgReceiverB processGroupA 接收处理队列A当中的消息： " + content);
        return true;
    }

    @TopicConsumer(group = "test.group.b" )
    public boolean processGroupB(String content)
    {
        logger.info(" MsgReceiverB  processGroupB 接收处理队列A当中的消息： " + content);
        return true;
    }

}