package com.crazymaker.springcloud.sharding.jdbc.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.MapSessionRepository;
import org.springframework.session.SessionRepository;

import java.util.HashMap;


@Configuration
public class HelpConfig {
    //屏蔽默认的 RedisSessionRepository
    @Bean
    SessionRepository sessionRepository() {
        return new MapSessionRepository(new HashMap<>());
    }
}
