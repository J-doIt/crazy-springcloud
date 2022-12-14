package com.crazymaker.springcloud.email.start;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication(scanBasePackages = {
        "com.crazymaker.springcloud",
}, exclude = {SecurityAutoConfiguration.class,
        RedisRepositoriesAutoConfiguration.class,
        MongoRepositoriesAutoConfiguration.class
})
@EnableSwagger2
@EnableFeignClients()
@EnableJpaRepositories(basePackages = { "com.crazymaker.springcloud" })
@EntityScan({"com.crazymaker.springcloud.email.entity","com.crazymaker.springcloud.kafka.entity"})
public class EmailApplication   extends SpringBootServletInitializer
{


    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application)
    {
        setRegisterErrorPageFilter(false);
        return application.sources(EmailApplication.class);
    }

    public static void main(String[] args)
    {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(EmailApplication.class, args);
        Environment env = applicationContext.getEnvironment();
        String port = env.getProperty("server.port");
        String path = env.getProperty("server.servlet.context-path");

        System.out.println("\n----------------------------------------------------------\n\t" +
                "EmailApplication is running! Access URLs:\n\t" +
                "Local: \t\thttp://localhost:" + port + path + "/\n\t" +
                "swagger-ui: \thttp://localhost:" + port + path + "/swagger-ui.html\n\t" +
                "actuator: \thttp://localhost:" + port + path + "/actuator/info\n\t" +
                "----------------------------------------------------------");
    }

}
