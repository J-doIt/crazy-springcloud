/**
 * Copyright (c) 2015-2017, Winter Lau (javayou@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.crazymaker.l2cache.cluster;

import com.crazymaker.l2cache.manager.CacheProviderHolder;
import com.crazymaker.l2cache.manager.Command;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Properties;

/**
 * 使用 RocketMQ 实现集群内节点的数据通知（用于对数据一致性要求特别严格的场景）
 *
 * @author Winter Lau (javayou@gmail.com)
 */
public class RocketMQClusterPolicy implements ClusterPolicy, MessageListenerConcurrently {

    private static final Logger log = LoggerFactory.getLogger(RocketMQClusterPolicy.class);

    private int LOCAL_COMMAND_ID = Command.genRandomSrc(); //命令源标识，随机生成，每个节点都有唯一标识

    private CacheProviderHolder holder;
    private String hosts;
    private String topic;
    private DefaultMQProducer producer;
    private DefaultMQPushConsumer consumer;

    public RocketMQClusterPolicy(Properties props) {
        this.hosts = props.getProperty("hosts");
        String groupName = props.getProperty("name", "j2cache");
        this.topic = props.getProperty("topic", "j2cache");

        this.producer = new DefaultMQProducer(groupName);
        this.producer.setNamesrvAddr(this.hosts);

        this.consumer = new DefaultMQPushConsumer(groupName);
        this.consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        this.consumer.setNamesrvAddr(this.hosts);
        this.consumer.setMessageModel(MessageModel.BROADCASTING);
    }

    @Override
    public boolean isLocalCommand(Command cmd) {
        return cmd.getSrc() == LOCAL_COMMAND_ID;
    }

    /**
     * 删除本地某个缓存条目
     *
     * @param region 区域名称
     * @param keys   缓存键值
     */
    public void evict(String region, String... keys) {
        holder.getLevel1Cache(region).evict(keys);
    }

    /**
     * 清除本地整个缓存区域
     *
     * @param region 区域名称
     */
    public void clear(String region) {
        holder.getLevel1Cache(region).clear();
    }

    @Override
    public void connect(Properties props, CacheProviderHolder holder) {
        this.holder = holder;
        try {
            this.producer.start();
            publish(Command.join());

            this.consumer.subscribe(this.topic, "*");
            this.consumer.registerMessageListener(this);
            this.consumer.start();
        } catch (MQClientException e) {
            log.error("Failed to start producer", e);
        }
    }

    @Override
    public void publish(Command cmd) {
        cmd.setSrc(LOCAL_COMMAND_ID);
        Message msg = new Message(topic, "", "", cmd.json().getBytes());
        try {
            this.producer.send(msg, 100000);
        } catch (Exception e) {
            log.error("Failed to publish {} to RocketMQ", cmd.json(), e);
        }
    }

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext context) {
        for (MessageExt msg : list) {
            handleCommand(Command.parse(new String(msg.getBody())));
        }
        return null;
    }

    @Override
    public void disconnect() {
        try {
            publish(Command.quit());
        } finally {
            this.producer.shutdown();
            this.consumer.shutdown();
        }
    }
}
