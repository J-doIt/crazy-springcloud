package com.crazymaker.springcloud.reactive.user.info.start;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableAutoConfiguration(exclude =  {
        SecurityAutoConfiguration.class,
        ReactiveSecurityAutoConfiguration.class,
        //排除db的自动配置
//        ReactiveUserDetailsServiceAutoConfiguration.class,
//        DataSourceAutoConfiguration.class,
//        DataSourceTransactionManagerAutoConfiguration.class,
//        HibernateJpaAutoConfiguration.class,
        //排除redis的自动配置
        RedisAutoConfiguration.class,
        RedisRepositoriesAutoConfiguration.class
})
@SpringBootApplication(scanBasePackages = {
        "com.crazymaker.springcloud.reactive",
        "com.crazymaker.springcloud.base",
        "com.crazymaker.springcloud.seckill.remote.fallback",
        "com.crazymaker.springcloud.standard"
})

@EnableTransactionManagement(proxyTargetClass = true)


//@EnableScheduling
@EnableSwagger2
@EnableJpaRepositories(basePackages = {
        "com.crazymaker.springcloud.reactive.user.info.dao.impl",
        "com.crazymaker.springcloud.reactive.user.*.dao",
        "com.crazymaker.springcloud.base.dao"
})

//@EnableRedisRepositories(basePackages = {
//        "com.crazymaker.springcloud.user.*.redis"})

@EntityScan(basePackages = {
        "com.crazymaker.springcloud.reactive.user.info.entity",
        "com.crazymaker.springcloud.reactive.user.*.dao.po",
        "com.crazymaker.springcloud.user.*.dao.po",
        "com.crazymaker.springcloud.base.dao.po",
        "com.crazymaker.springcloud.standard.*.dao.po"})
/**
 *  启用 Eureka Client 客户端组件
*/
@EnableEurekaClient

//启动Feign
//@EnableFeignClients(basePackages =
//        {"com.crazymaker.springcloud.seckill.remote.client"},
//        defaultConfiguration = {TokenFeignConfiguration.class}
//)
@Slf4j
//@EnableHystrix
public class UAAReactiveCloudApplication
{


    public static void main(String[] args)
    {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(UAAReactiveCloudApplication.class, args);
        Environment env = applicationContext.getEnvironment();
        String port = env.getProperty("server.port");
        String path = env.getProperty("server.servlet.context-path");
        String ip = env.getProperty("eureka.instance.ip-address");

      log.info("\n----------------------------------------------------------\n\t" +
                "UAA 用户账号与认证服务 is running! Access URLs:\n\t" +
                "Local: \t\thttp://"+ ip+":"+ port +  path + "/\n\t" +
                "swagger-ui: \thttp://"+ ip+":"+ port +  path + "/doc.html\n\t" +
                "actuator: \thttp://"+ ip+":"+ port +  path + "/actuator/info\n\t" +
                "----------------------------------------------------------");
    }



}