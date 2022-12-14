package com.crazymaker.springcloud.kafka.controller.dto;

import com.crazymaker.springcloud.kafka.mq.consumer.ConsumeMessage;
import com.crazymaker.springcloud.kafka.mq.consumer.MessageListener;
import com.crazymaker.springcloud.standard.context.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Slf4j
public class BeanMassageListener implements MessageListener {

    String topic;
    String beanId;
    String beanClass;
    String methodName;

    public BeanMassageListener(String topic) {
        this.topic = topic;
    }

    public BeanMassageListener(String topic, String beanClass) {
        this.topic = topic;
        this.beanClass = beanClass;
    }


    /**
     * 消息主题，表示只接受给定主题下的消息
     *
     * @return 消息主题
     */
    @Override
    public String getTopic() {
        return topic;
    }

    /**
     * 处理接收到的消息
     *
     * @param message 收到的消息
     */
    @Override
    public void consume(ConsumeMessage message) {
        Object target = null;
        Class clazz = null;
        if (StringUtils.isNotBlank(beanId)) {
            target = SpringContextUtil.getBean(beanId);
            clazz = target.getClass();
        } else if (StringUtils.isNotBlank(beanClass)) {
            try {
                clazz = Class.forName(beanClass);
                target = clazz.newInstance();
            } catch (Exception e) {
                log.error("", e);
            }

        } else {

            target = new EmptyConsumer();
            clazz = EmptyConsumer.class;
        }
        if (target == null) {
            log.error("消息订阅配置有误");
            return;
        }
        if (StringUtils.isEmpty(methodName)) {
            methodName = "process";
        }
        final Method method = getMethod(clazz, methodName);

        if (method == null) {
            log.error("消息订阅配置有误，方法配置错误");
            return;
        }
        try {
            method.invoke(target, message);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    public static Method getMethod(Class clazz, String methodName) {
        try {
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                if (method.getName().equals(methodName)) {
                    return method;
                }
            }

            return null;
        } catch (Exception e) {
        }
        return null;
    }

    public static BeanMassageListener ofDefault(String topic, String beanClass) {
        return new BeanMassageListener(topic, beanClass);
    }


    static class EmptyConsumer {


        public EmptyConsumer() {

        }

        public void process(ConsumeMessage message) {
            System.out.println("message = " + message.getValueAsString());
        }
    }
}