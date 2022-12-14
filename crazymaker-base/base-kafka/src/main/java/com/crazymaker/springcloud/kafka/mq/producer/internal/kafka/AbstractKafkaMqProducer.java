package com.crazymaker.springcloud.kafka.mq.producer.internal.kafka;

import com.crazymaker.springcloud.common.util.ThreadUtil;
import com.crazymaker.springcloud.kafka.autoconfigure.KafkaConfig;
import com.crazymaker.springcloud.kafka.mq.producer.Producer;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.serialization.StringDeserializer;

import javax.annotation.Resource;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * 描述: 抽象生产者
 */
public abstract class AbstractKafkaMqProducer implements Producer {

    @Resource
    private KafkaConfig kafkaConfig;

    public KafkaProducer<String, byte[]> producer;

    public void init() {
        //1000毫秒后启动
        ThreadUtil.delayRun(() -> doInit(), 1000, TimeUnit.MILLISECONDS);
    }

    public void doInit() {
        Properties properties = new Properties();
        properties.put(org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        properties.put(org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.put(org.apache.kafka.clients.consumer.ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, kafkaConfig.getSessionTimeoutMs() == null ? 3000 : kafkaConfig.getSessionTimeoutMs());
        //序列化类
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");       // 键的序列化
        properties.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");  // 值的序列化
        // 确认机制
        properties.put("acks", kafkaConfig.getAcksConfig() == null ? "1" : kafkaConfig.getAcksConfig());
        //重试次数
        properties.put("retries", 0);
        // getBootstrapServer（必填 ）
        properties.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getBootstrapServer());
        // 认证（公网）
        if (kafkaConfig.getSecurityProtocol() != null) {
            properties.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, kafkaConfig.getSecurityProtocol());
        }
        if (kafkaConfig.getSaslJaasConfig() != null) {
            properties.put("sasl.jaas.config", kafkaConfig.getSaslJaasConfig());
        }
        if (kafkaConfig.getSaslMechanism() != null) {
            properties.put("sasl.mechanism", kafkaConfig.getSaslMechanism());
        }
        this.producer = new KafkaProducer<>(properties);
    }
}
