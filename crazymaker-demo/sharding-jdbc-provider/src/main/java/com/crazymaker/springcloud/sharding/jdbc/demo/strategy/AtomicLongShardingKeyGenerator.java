package com.crazymaker.springcloud.sharding.jdbc.demo.strategy;

import lombok.Data;
import org.apache.shardingsphere.spi.keygen.ShardingKeyGenerator;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;

// 单机版 AtomicLong 类型的ID生成器
@Data
public class AtomicLongShardingKeyGenerator implements ShardingKeyGenerator {

    private AtomicLong atomicLong = new AtomicLong(0);
    private Properties properties = new Properties();

    @Override
    public Comparable<?> generateKey() {
        return atomicLong.incrementAndGet();
    }

    @Override
    public String getType() {

        //声明类型
        return "AtomicLong";
    }
}
