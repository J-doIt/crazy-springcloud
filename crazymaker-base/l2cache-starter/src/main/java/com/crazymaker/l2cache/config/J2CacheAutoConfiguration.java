package com.crazymaker.l2cache.config;


import com.crazymaker.l2cache.manager.CacheChannel;
import com.crazymaker.l2cache.manager.J2CacheBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.StandardEnvironment;

import java.io.IOException;

/**
 * 启动入口
 * @author zhangsaizz
 *
 */
@EnableConfigurationProperties({J2CacheConfig.class})
@Configuration
@PropertySource(value = "${j2cache.config-location}", encoding = "UTF-8", ignoreResourceNotFound = true)
public class J2CacheAutoConfiguration {

    @Autowired
    private StandardEnvironment standardEnvironment;

    @Bean
    public J2CacheCoreConfig j2CacheCoreConfig() throws IOException{
    	J2CacheCoreConfig j2CacheCoreConfig = J2CacheCoreConfig.initFromConfig(standardEnvironment);
    	return j2CacheCoreConfig;
    }

    @Bean
    @DependsOn({"j2CacheCoreConfig"})
    public CacheChannel cacheChannel(J2CacheCoreConfig j2CacheCoreConfig) throws IOException {
    	J2CacheBuilder builder = J2CacheBuilder.init(j2CacheCoreConfig);
        return builder.getChannel();
    }

}
