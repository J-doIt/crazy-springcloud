package com.crazymaker.springcloud.standard.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 密码加密器配置类
 */
@Configuration
public class DefaultPasswordConfig
{
    /**
     * 装配一个全局的Bean，用于密码加密和匹配
     *
     * @return  BCryptPasswordEncoder 加密器实例
     */
    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }
}
