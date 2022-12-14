package com.crazymaker.redission.demo.entity;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 读取redis配置信息，封装到当前实体中
 * @author Yuqiang
 */
@Component
@ConfigurationProperties("application.redisson.server")
@Data
public class RedissonConfig {

    /**
     * redis主机地址，ip：port，有多个用半角逗号分隔
     */
    private String address;

    /**
     * 连接类型，支持standalone-单机节点，sentinel-哨兵，cluster-集群，masterslave-主从
     */
    private String type;

    /**
     * redis 连接密码
     */
    private String password;

    /**
     * 选取那个数据库
     */
    private int database;

    /**
     * 端口
     */
    private String port;

}
