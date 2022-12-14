package com.crazymaker.springcloud.message.core;

import com.crazymaker.springcloud.message.config.MqConfigProperties;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.util.Assert;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeoutException;

public class RabbitMqEndpoint
{


    private MqConfigProperties configProperties;
    Set<String> groupSet = new TreeSet<>();
    Set<String> topicSet = new TreeSet<>();

    private ConnectionFactory factory = null;// 连接工厂
    private Connection connection = null;
    private Channel channel = null;

    private static RabbitMqEndpoint singleton;

    Map<HandlerKey, Handler> handlerMap = new LinkedHashMap<>();

    public RabbitMqEndpoint(MqConfigProperties configProperties)
    {
        this.configProperties = configProperties;
    }

    /**
     * 增加消息监听器
     *
     * @param topicName 主题
     * @param groupName 消费组
     * @param bean      消费bean
     * @param method    消费方法
     * @return false 失败 true 成功
     */
    public boolean addConsumer(String topicName,
                               String groupName,
                               Object bean,
                               Method method)
    {
        HandlerKey key = new HandlerKey(topicName, groupName);
        if (handlerMap.containsKey(key))
        {
            return false;
        }
        groupSet.add(groupName);
        topicSet.add(topicName);
        Handler handler = new Handler(bean, method, null);
        handlerMap.put(key, handler);
        return true;
    }

    public void destroy()
    {

        try
        {
            channel.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        } catch (TimeoutException e)
        {
            e.printStackTrace();
        }
        try
        {
            connection.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }


    }

    @AllArgsConstructor
    @Data
    private static class HandlerKey
    {
        String topicName;
        String groupName;

        @Override
        public boolean equals(Object o)
        {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            HandlerKey that = (HandlerKey) o;
            return Objects.equals(groupName, that.groupName) &&
                    Objects.equals(topicName, that.topicName);
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(groupName, topicName);
        }
    }

    @AllArgsConstructor
    @Data
    private static class Handler
    {
        Object bean;
        Method method;
        Consumer consumer;

        public void buildConsumer(Channel channel)
        {
            consumer = new DefaultConsumer(channel)
            {
                @Override
                public void handleDelivery(String consumerTag,
                                           Envelope envelope,
                                           AMQP.BasicProperties properties, byte[] body) throws
                        IOException
                {
                    String receive = new String(body, "UTF-8" );
                    try
                    {
                        boolean consumed = (boolean) method.invoke(bean, receive);

                        if (consumed)
                        {
//消息确认
                            channel.basicAck(envelope.getDeliveryTag(), false);
                        }
                    } catch (IllegalAccessException e)
                    {
                        e.printStackTrace();
                    } catch (InvocationTargetException e)
                    {
                        e.printStackTrace();
                    }

                }
            };
        }
    }

    /**
     * 初始化
     */
    public void init()
    {
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

    /**
     * 善后处理
     */

    public void declareQueueAndExchange() throws IOException
    {
        Assert.notNull(channel, "开启了消息服务，但是与消息服务器的连接失败" );

        Iterator<String> i = topicSet.iterator();
        while (i.hasNext())
        {
            String topicName = i.next();
            // 声明一个topic交换类型
            channel.exchangeDeclare(topicName, "topic" );
        }

        i = groupSet.iterator();
        while (i.hasNext())
        {
            String groupName = i.next();
            // 当声明队列

            channel.queueDeclare(groupName, false, false, false, null);
        }

        Iterator<HandlerKey> keyIterator = handlerMap.keySet().iterator();
        while (keyIterator.hasNext())
        {
            HandlerKey handlerKey = keyIterator.next();

            //绑定
            channel.queueBind(handlerKey.groupName, handlerKey.topicName, handlerKey.groupName);
        }

        handlerMap.keySet().stream().forEach(key ->
        {
            Handler handler = handlerMap.get(key);

            handler.buildConsumer(channel);

            try
            {
                channel.basicConsume(key.groupName, false, handler.consumer);
            } catch (IOException e)
            {
                e.printStackTrace();
            }

        });
    }


    public static void setSingleton(RabbitMqEndpoint e)
    {
        singleton = e;
    }

    public static RabbitMqEndpoint getSingleton()
    {
        if (null != singleton)
        {
            return singleton;
        }

        Assert.notNull(singleton, "RabbitMqEndpoint bean must be specified" );

        return singleton;
    }

}
