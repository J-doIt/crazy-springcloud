package com.crazymaker.cloud.nacos.demo.sentinel.starter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableAutoConfiguration(exclude = {
        SecurityAutoConfiguration.class,
        ManagementWebSecurityAutoConfiguration.class,
        //排除db的自动配置
        DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class,
        //排除redis的自动配置
        RedisAutoConfiguration.class,
        RedisRepositoriesAutoConfiguration.class
})
@EnableSwagger2
@EnableDiscoveryClient
@Slf4j
@SpringBootApplication(
        scanBasePackages =
                {
                        "com.crazymaker.cloud",
                        "com.crazymaker.springcloud.standard"
                })
//启动Feign
@EnableFeignClients(basePackages =
        {"com.crazymaker.cloud.nacos.demo.consumer.client"})
public class SentinelDomeProviderApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = null;
        try {
            applicationContext = SpringApplication.run(SentinelDomeProviderApplication.class, args);
            System.out.println("Server startup done.");
        } catch (Exception e) {
            log.error("服务启动报错", e);
            return;
        }

        Environment env = applicationContext.getEnvironment();
        String port = env.getProperty("server.port");
        String path = env.getProperty("server.servlet.context-path");
        System.out.println("\n----------------------------------------------------------\n\t" +
                "Application is running! Access URLs:\n\t" +
                "Local: \t\thttp://localhost:" + port + path + "/index.html\n\t" +
                "swagger-ui: \thttp://localhost:" + port + path + "/swagger-ui.html\n\t" +
                "----------------------------------------------------------");

    }
}