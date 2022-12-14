package com.crazymaker.springcloud.standard.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "http.connection" )
public class HttpConnectionProperties
{
    // 设置保持连接的时长,这里为60s*10
    private int keepAliveTimeout = 900 * 1000 * 10;
    // 设置连接建立的超时时间,默认为30s
    private int connectTimeout = 30 * 1000;
    // 设置读取数据的超时时间,默认为30s
    private int socketTimeout = 30 * 1000;
    // 设置从连接池获取连接的超时时间,默认为30s
    private int connectionRequestTimeout = 30 * 1000;

    // 最大连接数
    private int maxTotal = 10000;
    // 每个路由的最大连接数
    private int defaultMaxPerRoute = 1000;

    //在从连接池获取连接时，连接不活跃多长时间后需要进行一次验证
    private int validateAfterInactivity = 10 * 1000;

    //空闲监测
    private int idleCheckGap = 6 * 1000;


    /**
     * tomcat 的自定义配置
     */

    // 当客户端发送超过10000个请求,请求个数超过这个数，强制关闭掉socket链接
    private int tomcatMaxAlive = 10000;

    //tomcat 设置最大连接数
    private int tomcatMaxConnections = 10000;

    //tomcat 设置最大线程数
    private int tomcatMaxThreads = 200;
    //
    private int tomcatConnectionTimeout = 30000;
    //accept-count：最大排队数
    private int tomcatAcceptCount = 1000;

}


