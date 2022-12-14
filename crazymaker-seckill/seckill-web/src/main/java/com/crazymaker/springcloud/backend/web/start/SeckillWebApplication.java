package com.crazymaker.springcloud.backend.web.start;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(scanBasePackages = {
//        "com.crazymaker.springcloud.user",
//        "com.crazymaker.springcloud.seckill.remote.fallback",
//        "com.crazymaker.springcloud.standard"
}, exclude = {SecurityAutoConfiguration.class})
public class SeckillWebApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(SeckillWebApplication.class, args);
    }
}
