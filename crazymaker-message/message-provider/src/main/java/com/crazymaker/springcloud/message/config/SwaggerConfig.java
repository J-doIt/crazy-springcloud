package com.crazymaker.springcloud.message.config;

import com.google.common.collect.Lists;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * swagger配置
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig
{

    @Bean
    public Docket templateApi()
    {
        ParameterBuilder tokenPar = new ParameterBuilder();
        tokenPar.name("token" ).description("token令牌" )
                .modelRef(new ModelRef("string" )).parameterType("header" ).required(false).build();
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .useDefaultResponseMessages(false)
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(RestController.class))
                .build().globalOperationParameters(Lists.newArrayList(tokenPar.build()));
    }

    private ApiInfo apiInfo()
    {
        return new ApiInfoBuilder()
                .title("秒杀 Rest API 文档" )
                .version("1.0" )
                .build();
    }
}
