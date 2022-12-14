package com.crazymaker.springcloud.standard.config;

import com.crazymaker.springcloud.common.distribute.idGenerator.IdGenerator;
import com.crazymaker.springcloud.common.distribute.rateLimit.RateLimitService;
import com.crazymaker.springcloud.distribute.idGenerator.impl.SnowflakeIdGenerator;
import com.crazymaker.springcloud.distribute.lock.LockService;
import com.crazymaker.springcloud.distribute.zookeeper.ZKClient;
import com.crazymaker.springcloud.standard.hibernate.SnowflakeIdGeneratorFactory;
import com.crazymaker.springcloud.standard.lock.impl.ZkLockServiceImpl;
import com.crazymaker.springcloud.standard.rateLimit.impl.ZkRateLimitServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Configuration
@ConditionalOnProperty(prefix = "zookeeper", name = "address" )
public class CustomedZookeeperAutoConfiguration
{
    @Value("${zookeeper.address}" )
    private String zkAddress;

    /**
     * 自定义的ZK客户端bean
     *
     * @return
     */
    @Bean(name = "zKClient" )
    public ZKClient zKClient()
    {
        return new ZKClient(zkAddress);
    }

    /**
     * 获取 ZK 限流器的 bean
     */
    @Bean
    @DependsOn("zKClient" )
    public RateLimitService zkRateLimitServiceImpl()
    {
        return new ZkRateLimitServiceImpl();
    }

    /**
     * 获取 ZK 分布式锁的 bean
     */

    @Bean
    @DependsOn("zKClient" )
    public LockService zkLockServiceImpl()
    {
        return new ZkLockServiceImpl();
    }


    /**
     * 获取通用的分布式ID 生成器 工程
     */
    @Bean
    @DependsOn("zKClient" )
    public SnowflakeIdGeneratorFactory snowflakeIdGeneratorFactory()
    {
        return new SnowflakeIdGeneratorFactory();
    }


    /**
     * 获取秒杀商品的分布式ID 生成器
     */
    @Bean
    @DependsOn("zKClient" )
    public IdGenerator seckillSkuIdentityGenerator()
    {
        return new SnowflakeIdGenerator("seckillSkuIdentityGenerator" );
    }


    /**
     * 获取秒杀订单的分布式ID 生成器
     */
    @Bean
    @DependsOn("zKClient" )
    public IdGenerator seckillOrderIdentityGenerator()
    {
        return new SnowflakeIdGenerator("seckillOrderIdentityGenerator" );
    }

}
