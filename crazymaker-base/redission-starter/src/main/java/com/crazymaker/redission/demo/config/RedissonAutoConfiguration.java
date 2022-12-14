package com.crazymaker.redission.demo.config;

import com.crazymaker.redission.demo.RedissonLock;
import com.crazymaker.redission.demo.RedissonManager;
import com.crazymaker.redission.demo.annotation.DistributedLockHandler;
import com.crazymaker.redission.demo.entity.RedissonConfig;
import org.redisson.Redisson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * 自动配置
 * @author Yuqiang
 */
@Configuration
@ConditionalOnClass(value = Redisson.class)
@EnableConfigurationProperties(RedissonConfig.class)
public class RedissonAutoConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedissonAutoConfiguration.class);

    @Bean
    @ConditionalOnMissingBean
    @Order(value = 1)
    public RedissonManager redissonManager(RedissonConfig redissonConfig) {
        RedissonManager redissonManager =
                new RedissonManager(redissonConfig);
        LOGGER.info("[RedissonManager]组装完毕,当前连接方式:" + redissonConfig.getType() +
                ",连接地址:" + redissonConfig.getAddress());
        return redissonManager;
    }


    @Bean
    @ConditionalOnMissingBean
    @Order(value = 2)
    public RedissonLock redissonLock(RedissonManager redissonManager) {
        RedissonLock redissonLock = new RedissonLock(redissonManager);
        LOGGER.info("[RedissonLock]组装完毕");
        return redissonLock;
    }

    @Bean
    @ConditionalOnMissingBean
    @Order(value = 3)
    public DistributedLockHandler distributedLockHandler( ) {
        return new DistributedLockHandler();
    }

}