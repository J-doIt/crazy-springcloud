package com.crazymaker.springcloud.standard.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "system.basic" )
public class SysBaseProperties
{


    private String encryptPassword = "5qtb0+Z/H1c=";
    private String secret = "123456";
    private String sessionPrefix = "app";


}

