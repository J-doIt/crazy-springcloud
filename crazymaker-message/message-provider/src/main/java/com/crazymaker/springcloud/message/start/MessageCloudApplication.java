package com.crazymaker.springcloud.message.start;


import com.crazymaker.springcloud.standard.config.TokenFeignConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication(scanBasePackages =
        {"com.crazymaker.springcloud.message",
                "com.crazymaker.springcloud.standard",
                "com.crazymaker.springcloud.user.info.contract.fallbak"
        })
@EnableScheduling
@EnableJpaRepositories(basePackages = {"com.crazymaker.springcloud.message.dao"})
@EntityScan(basePackages = {"com.crazymaker.springcloud.message.dao.po", "com.crazymaker.springcloud.standard.*.dao.po"})

@EnableFeignClients(basePackages = "com.crazymaker.springcloud.user.info.api.client",
        defaultConfiguration = {TokenFeignConfiguration.class})
@EnableSwagger2
@EnableHystrix
@EnableCircuitBreaker
@EnableEurekaClient
public class MessageCloudApplication extends SpringBootServletInitializer
{

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application)
    {
        setRegisterErrorPageFilter(false);
        return application.sources(MessageCloudApplication.class);
    }


    public static void main(String[] args)
    {
        SpringApplication.run(MessageCloudApplication.class, args);
    }

}
