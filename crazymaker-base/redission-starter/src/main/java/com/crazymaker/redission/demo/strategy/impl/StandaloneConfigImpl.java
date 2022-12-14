package com.crazymaker.redission.demo.strategy.impl;


import com.crazymaker.redission.demo.codec.FastjsonCodec;
import com.crazymaker.redission.demo.constant.GlobalConstant;
import com.crazymaker.redission.demo.entity.RedissonConfig;
import com.crazymaker.redission.demo.strategy.RedissonConfigService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.config.Config;
import org.springframework.util.StringUtils;

/**
 * 单机部署 Redisson配置
 * @author Yuqiang
 */
@Slf4j
public class StandaloneConfigImpl implements RedissonConfigService {

    @Override
    public Config createRedissonConfig(RedissonConfig redissonConfig) {
        Config config = new Config();
        try {
            String address = redissonConfig.getAddress();
            String password = redissonConfig.getPassword();
            int database = redissonConfig.getDatabase();
            String redisAddr = GlobalConstant.REDIS_CONNECTION_PREFIX.getConstant_value() + address;
            config.useSingleServer().setAddress(redisAddr);
            config.useSingleServer().setDatabase(database);
            //密码可以为空
            if (!StringUtils.isEmpty(password)) {
                config.useSingleServer().setPassword(password);
            }
            log.info("初始化[单机部署]方式Config,redisAddress:" + address);

//            config.setCodec( new FstCodec());
            config.setCodec( new FastjsonCodec());
        } catch (Exception e) {
            log.error("单机部署 Redisson init error", e);
        }
        return config;
    }
}
