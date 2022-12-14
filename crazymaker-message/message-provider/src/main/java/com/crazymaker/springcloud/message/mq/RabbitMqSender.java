package com.crazymaker.springcloud.message.mq;


import com.crazymaker.springcloud.message.config.MqConfigProperties;
import com.crazymaker.springcloud.message.core.CustomMqExceptionHandler;
import com.crazymaker.springcloud.message.core.MqSender;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.io.IOException;

/**
 * The class Rocket mq producer.
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RabbitMqSender implements MqSender
{
    private MqConfigProperties configProperties;

    private ConnectionFactory factory = null;// 连接工厂
    private Connection connection = null;
    private Channel channel = null;


    public RabbitMqSender(MqConfigProperties configProperties)
    {
        this.configProperties = configProperties;
        init();

    }


    public void sendMessage(String body, String topic, String group)
    {

        try
        {
            channel.basicPublish(topic, group, null, body.getBytes());
        } catch (IOException e)
        {
            e.printStackTrace();
        }


    }

    /**
     * 初始化
     */
    public void init()
    {
        if (channel != null)
        {
            return;
        }

        try
        {
            factory = new ConnectionFactory();
            factory.setExceptionHandler(new CustomMqExceptionHandler());
            factory.setHost(configProperties.getHost());
            factory.setPort(Integer.parseInt(configProperties.getPort()));
            factory.setUsername(configProperties.getUser());
            factory.setPassword(configProperties.getPassword());
            // 获取连接
            connection = factory.newConnection();
            channel = connection.createChannel();

        } catch (Exception e)
        {
            e.printStackTrace();
        }

        Assert.notNull(connection, "开启了消息消费，但是没有连接到服务器" );
    }

}
