package com.crazymaker.springcloud.kafka.autoconfigure;

import com.crazymaker.springcloud.kafka.mq.admin.KafkaAdmin;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * 描述: Spring Boot Starter初始化
 * 1. 将EnableConfigurationProperties 加载到Spring上下文的容器中
 * 2. 当配置文件存在“com.crazymaker.springcloud.mq.kafka.bootstrap-server”时新建对象
 *
 * @author wangpengpeng
 * @date 2020-07-02 12:49
 */
@Configuration
@EnableConfigurationProperties(KafkaConfig.class)
@ConditionalOnProperty(prefix = "mq.kafka", name = "bootstrap-server")
public class KafkaAutoConfiguration {

    @Bean
    KafkaConfig kafkaProperties() {
        return new KafkaConfig();
    }


    @Bean
    public KafkaAdmin kafkaAdmin(KafkaConfig kafkaConfig) {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getBootstrapServer());
        return new KafkaAdmin(configs);
    }

    @Value("${spring.application.name}")
    private String applicationName;
}
