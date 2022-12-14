package com.crazymaker.springcloud.cloud.center.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

@EnableEurekaServer
@SpringBootApplication
public class EurekaServerApplication
{

    public static void main(String[] args)
    {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(EurekaServerApplication.class, args);
        Environment env = applicationContext.getEnvironment();
        String port = env.getProperty("server.port");
        String path = env.getProperty("server.servlet.context-path");
        String ip = env.getProperty("eureka.instance.ip-address");

        System.out.println("\n----------------------------------------------------------\n\t" +
                "Eureka 注册中心 is running! Access URLs:\n\t" +
                "Local: \t\thttp://"+ ip+":"+ port + "/\n\t" +
                "actuator: \thttp://"+ ip+":"+ port +  "/actuator/info\n\t" +
                "----------------------------------------------------------");
    }

}