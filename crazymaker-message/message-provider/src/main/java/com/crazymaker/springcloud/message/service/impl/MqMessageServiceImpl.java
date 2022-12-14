package com.crazymaker.springcloud.message.service.impl;

import com.crazymaker.springcloud.common.exception.BusinessException;
import com.crazymaker.springcloud.message.contract.constants.MqErrorConstants;
import com.crazymaker.springcloud.message.core.MqSender;
import com.crazymaker.springcloud.message.dao.MqMessageDao;
import com.crazymaker.springcloud.message.dao.MqSubscribeRuleDao;
import com.crazymaker.springcloud.message.dao.po.MqMessagePO;
import com.crazymaker.springcloud.message.dao.po.MqSubscribeRulePO;
import com.crazymaker.springcloud.message.service.MqMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The class Mq message service.
 */
@Slf4j
@Service
public class MqMessageServiceImpl implements MqMessageService
{

    @Resource
    private MqMessageDao mqMessageDao;

    @Resource
    private MqSubscribeRuleDao mqSubscribeRuleDao;

    @Resource
    private MqSender producer;

    private void checkMessage(MqMessagePO mqMessagePO)
    {
        if (null == mqMessagePO)
        {
            throw BusinessException.builder().errMsg(MqErrorConstants.TPC10050007).build();
        }

    }

    Map<String, List<MqSubscribeRulePO>> topicSubscribed = new LinkedHashMap<>();

    @Override
    public void sendMessage(String body, String topic)
    {

        List<MqSubscribeRulePO> list = topicSubscribed.get(topic);
        if (list == null)
        {
            list = mqSubscribeRuleDao.findAllByTopicCode(topic);
            topicSubscribed.put(topic, list);
        }
        list.stream().forEach(po ->
        {

            directSendGroupMessage(body, topic, po.getGroupCode());
        });


    }

    /**
     * 按照 topic  group 发送消息
     *
     * @param body  body
     * @param topic topic
     * @param group group
     */
    public void directSendGroupMessage(String body, String topic, String group)
    {
        producer.sendMessage(body, topic, group);
    }


}
