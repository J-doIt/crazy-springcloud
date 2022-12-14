package com.crazymaker.springcloud.message.service;

public interface MqMessageService
{


    /**
     * 按照 topic   发送消息
     *
     * @param body
     * @param topic
     */
    void sendMessage(String body, String topic);


}
