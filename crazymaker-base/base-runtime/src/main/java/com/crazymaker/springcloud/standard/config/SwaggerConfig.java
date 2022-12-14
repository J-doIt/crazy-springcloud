package com.crazymaker.springcloud.standard.config;

import com.crazymaker.springcloud.common.constants.SessionConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.LinkedList;

/**
 * swagger配置
 */
@Configuration
@EnableSwagger2
@Profile({"dev","sit"})
public class SwaggerConfig
{

    @Bean
    public Docket templateApi()
    {
        ParameterBuilder tokenPar = new ParameterBuilder();
        tokenPar.name(SessionConstants.AUTHORIZATION_HEAD).description("token")
                .modelRef(new ModelRef("string")).parameterType("header")
                .required(false).build();
//        ParameterBuilder adminTokenPar = new ParameterBuilder();
//        adminTokenPar.name(SessionConstants.ADMIN_AUTHORIZATION_HEAD).description("管理控制台 token 令牌" )
//                .modelRef(new ModelRef("string" )).parameterType("header" ).required(false).build();
        ParameterBuilder user = new ParameterBuilder();
        user.name(SessionConstants.USER_IDENTIFIER).description("user-id")
                .modelRef(new ModelRef("string")).parameterType("header").required(false).build();
        LinkedList<Parameter> list = new LinkedList<>();
        list.add(tokenPar.build());
//        list.add(adminTokenPar.build());
        list.add(user.build());
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .useDefaultResponseMessages(false)
                .select()

                //控制暴露出去的路径下的实例
                //如果某个接口不想暴露,可以使用以下注解
                //@ApiIgnore 这样,该接口就不会暴露在 swagger2 的页面下
                .apis(RequestHandlerSelectors.withClassAnnotation(RestController.class))
                .build()
                .directModelSubstitute(Long.class, String.class)
                .globalOperationParameters(list);
    }

    //构建 api文档的详细信息函数
    private ApiInfo apiInfo()
    {
        return new ApiInfoBuilder()
                //页面标题
                .title("疯狂创客圈 卷王社群 ：聚焦3高技术 走向技术自由")
                //描述
                .description("Zuul+Swagger2  构建  RESTful APIs")
                //条款地址
                .termsOfServiceUrl("https://www.cnblogs.com/crazymakercircle/")
                .contact(new Contact("疯狂创客圈", "https://www.cnblogs.com/crazymakercircle/", ""))
                .version("1.0")
                .build();
    }

    @Value("${server.servlet.context-path}")
    private String servletContextPath;




}
