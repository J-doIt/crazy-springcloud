package com.crazymaker.springcloud.user.info.start;


import com.crazymaker.springcloud.standard.config.TokenFeignConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
@EnableAutoConfiguration(exclude = {
        SecurityAutoConfiguration.class,
        ManagementWebSecurityAutoConfiguration.class
})
@SpringBootApplication(scanBasePackages = {
        "com.crazymaker.springcloud.user",
        "com.crazymaker.springcloud.base",
        "com.crazymaker.springcloud.seckill.remote.fallback",
        "com.crazymaker.springcloud.standard"
})
//@EnableScheduling
@EnableSwagger2
@EnableJpaRepositories(basePackages = {
        "com.crazymaker.springcloud.user.*.dao",
        "com.crazymaker.springcloud.base.dao"
})

//@EnableRedisRepositories(basePackages = {
//        "com.crazymaker.springcloud.user.*.redis"})

@EntityScan(basePackages = {
        "com.crazymaker.springcloud.user.*.dao.po",
        "com.crazymaker.springcloud.base.dao.po",
        "com.crazymaker.springcloud.standard.*.dao.po"})
/**
 *  启用 Eureka Client 客户端组件
*/
@EnableEurekaClient

//启动Feign
@EnableFeignClients(basePackages =
        {"com.crazymaker.springcloud.seckill.remote.client"},
        defaultConfiguration = {TokenFeignConfiguration.class}
)
@Slf4j
//@EnableHystrix
public class UAACloudApplication
{


    public static void main(String[] args)
    {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(UAACloudApplication.class, args);
        Environment env = applicationContext.getEnvironment();
        String port = env.getProperty("server.port");
        String path = env.getProperty("server.servlet.context-path");
        String ip = env.getProperty("eureka.instance.ip-address");

      log.info("\n----------------------------------------------------------\n\t" +
                "UAA 用户账号与认证服务 is running! Access URLs:\n\t" +
                "Local: \t\thttp://"+ ip+":"+ port +  path + "/\n\t" +
                "swagger-ui: \thttp://"+ ip+":"+ port +  path + "/swagger-ui.html\n\t" +
                "actuator: \thttp://"+ ip+":"+ port +  path + "/actuator/info\n\t" +
                "----------------------------------------------------------");
    }



}