package com.crazymaker.redission.demo;

import com.crazymaker.redission.demo.constant.RedisConnectionType;
import com.crazymaker.redission.demo.entity.RedissonConfig;
import com.crazymaker.redission.demo.strategy.RedissonConfigService;
import com.crazymaker.redission.demo.strategy.impl.ClusterConfigImpl;
import com.crazymaker.redission.demo.strategy.impl.MasterslaveConfigImpl;
import com.crazymaker.redission.demo.strategy.impl.SentineConfigImpl;
import com.crazymaker.redission.demo.strategy.impl.StandaloneConfigImpl;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.config.Config;
import org.springframework.stereotype.Component;



/**
 * @Description: Redisson核心配置，用于提供初始化的redisson实例
 * @author Yuqiang
 */
@Slf4j
@Component
public class RedissonManager {


    private Config config = new Config();

    private Redisson redisson = null;

    public RedissonManager() {
    }

    public RedissonManager(RedissonConfig redissonConfig) {
        try {
            //通过不同部署方式获得不同cofig实体
            config = RedissonConfigFactory.getInstance().createConfig(redissonConfig);
            redisson = (Redisson) Redisson.create(config);
        } catch (Exception e) {
            log.error("Redisson init error", e);
            throw new IllegalArgumentException("please input correct configurations," +
                    "connectionType must in standalone/sentinel/cluster/masterslave");
        }
    }

    public Redisson getRedisson() {
        return redisson;
    }

    /**
     * Redisson连接方式配置工厂
     * 双重检查锁
     */
    static class RedissonConfigFactory {

        private RedissonConfigFactory() {
        }

        private static volatile RedissonConfigFactory factory = null;

        public static RedissonConfigFactory getInstance() {
            if (factory == null) {
                synchronized (Object.class) {
                    if (factory == null) {
                        factory = new RedissonConfigFactory();
                    }
                }
            }
            return factory;
        }


        /**
         * 根据连接类型获取对应连接方式的配置,基于策略模式
         *
         * @param redissonConfig redis连接信息
         * @return Config
         */
        Config createConfig(RedissonConfig redissonConfig) {
            Preconditions.checkNotNull(redissonConfig);
            Preconditions.checkNotNull(redissonConfig.getAddress(), "redisson.lock.server.address cannot be NULL!");
            Preconditions.checkNotNull(redissonConfig.getType(), "redisson.lock.server.password cannot be NULL");
            Preconditions.checkNotNull(redissonConfig.getDatabase(), "redisson.lock.server.database cannot be NULL");
            String connectionType = redissonConfig.getType();
            //声明配置上下文
            RedissonConfigService redissonConfigService = null;
            if (connectionType.equals(RedisConnectionType.STANDALONE.getConnection_type())) {
                redissonConfigService = new StandaloneConfigImpl();
            } else if (connectionType.equals(RedisConnectionType.SENTINEL.getConnection_type())) {
                redissonConfigService = new SentineConfigImpl();
            } else if (connectionType.equals(RedisConnectionType.CLUSTER.getConnection_type())) {
                redissonConfigService = new ClusterConfigImpl();
            } else if (connectionType.equals(RedisConnectionType.MASTERSLAVE.getConnection_type())) {
                redissonConfigService = new MasterslaveConfigImpl();
            } else {
                throw new IllegalArgumentException("创建Redisson连接Config失败！当前连接方式:" + connectionType);
            }
            return redissonConfigService.createRedissonConfig(redissonConfig);
        }
    }

}


