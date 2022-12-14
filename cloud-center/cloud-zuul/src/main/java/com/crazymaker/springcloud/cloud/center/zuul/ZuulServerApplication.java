package com.crazymaker.springcloud.cloud.center.zuul;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
@Slf4j
@SpringBootApplication(
        scanBasePackages = {
                "com.crazymaker.springcloud.cloud.center.zuul",
                "com.crazymaker.springcloud.standard",
                "com.crazymaker.springcloud.base",
                "com.crazymaker.springcloud.user.info.contract"
        },

        exclude = {
//                SecurityAutoConfiguration.class,
//                SecurityFilterAutoConfiguration.class,
//                DataSourceAutoConfiguration.class,
//                HibernateJpaAutoConfiguration.class,
//                DruidDataSourceAutoConfigure.class,
//                RedisSessionFilterConfig.class
        })

@EnableScheduling
@EnableHystrix
@EnableDiscoveryClient
@EnableJpaRepositories(basePackages = {
        "com.crazymaker.springcloud.base.dao"
})
@EntityScan(basePackages = {
        "com.crazymaker.springcloud.base.dao.po",
        "com.crazymaker.springcloud.standard.*.dao.po"
})
//声明一个Zuul服务
@EnableZuulProxy
@EnableCircuitBreaker
public class ZuulServerApplication
{

    public static void main(String[] args)
    {

        ConfigurableApplicationContext applicationContext = SpringApplication.run(ZuulServerApplication.class, args);
        Environment env = applicationContext.getEnvironment();
        String port = env.getProperty("server.port");
        String name = env.getProperty("spring.application.name");

        String path = env.getProperty("server.servlet.context-path");
        if (StringUtils.isBlank(path))
        {
            path = "";
        }
        String ip = env.getProperty("eureka.instance.ip-address");

       log.info("\n----------------------------------------------------------\n\t" +
                name.toUpperCase() + " is running! Access URLs:\n\t" +
                "Local: \t\thttp://" + ip + ":" + port + path + "/\n\t" +
                "swagger-ui: \thttp://" + ip + ":" + port + path + "swagger-ui.html\n\t" +
                "actuator: \thttp://" + ip + ":" + port + path + "/actuator/info\n\t" +
                "----------------------------------------------------------");
    }

}


