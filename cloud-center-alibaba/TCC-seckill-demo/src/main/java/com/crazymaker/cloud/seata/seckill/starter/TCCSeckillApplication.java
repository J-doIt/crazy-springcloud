package com.crazymaker.cloud.seata.seckill.starter;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableAutoConfiguration(exclude = {
        SecurityAutoConfiguration.class,
        ManagementWebSecurityAutoConfiguration.class
})
@EnableSwagger2
@EnableDiscoveryClient
@Slf4j
@EnableFeignClients(basePackages =
        {"com.crazymaker.cloud.seata.seckill.feign"})
@SpringBootApplication(scanBasePackages =
        {
                "com.crazymaker.springcloud.stock",
                "com.crazymaker.springcloud.seckill",
                "com.crazymaker.cloud.seata.seckill",
                "com.crazymaker.springcloud.standard",
                "com.crazymaker.springcloud.base",
                "com.crazymaker.springcloud.message",
                "com.crazymaker.springcloud.user.info.remote.fallback"
        }, exclude = {
        //        SeataFeignClientAutoConfiguration.class,

        SecurityAutoConfiguration.class
})
@EnableScheduling
@EnableJpaRepositories(basePackages = {
        "com.crazymaker.springcloud.stock.dao",
        "com.crazymaker.springcloud.seckill.dao",
        "com.crazymaker.springcloud.base.dao"
})
@EntityScan(basePackages = {
        "com.crazymaker.springcloud.seckill.dao.po",
        "com.crazymaker.springcloud.stock.dao.po",
        "com.crazymaker.springcloud.base.dao.po",
        "com.crazymaker.springcloud.standard.*.dao.po"
})
public class TCCSeckillApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(TCCSeckillApplication.class, args);

        Environment env = applicationContext.getEnvironment();
        String port = env.getProperty("server.port");
        String name = env.getProperty("spring.application.name");

        String path = env.getProperty("server.servlet.context-path");
        if (StringUtils.isBlank(path)) {
            path = "";
        }
        String ip = env.getProperty("spring.cloud.client.ip-address");

        System.out.println("\n----------------------------------------------------------\n\t" +
                name.toUpperCase() + " is running! Access URLs:\n\t" +
                "Local: \t\thttp://" + ip + ":" + port + path + "/\n\t" +
                "swagger-ui: \thttp://" + ip + ":" + port + path + "/swagger-ui.html\n\t" +
                "actuator: \thttp://" + ip + ":" + port + path + "/actuator/info\n\t" +
                "----------------------------------------------------------");
    }
}