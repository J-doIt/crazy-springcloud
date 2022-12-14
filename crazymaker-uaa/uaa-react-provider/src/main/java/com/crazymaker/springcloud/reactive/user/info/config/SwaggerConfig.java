package com.crazymaker.springcloud.reactive.user.info.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.util.UriComponentsBuilder;
import springfox.documentation.PathProvider;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.paths.DefaultPathProvider;
import springfox.documentation.spring.web.paths.Paths;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebFlux;

@Configuration
@EnableSwagger2WebFlux
public class SwaggerConfig
{


    @Bean
    public Docket createRestApi()
    {
//        return new Docket(DocumentationType.OAS_30)
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .pathMapping(servletContextPath)  //注意webflux没有context-path配置，如果不加这句话的话，接口测试时路径没有前缀

                .select()
                .apis(RequestHandlerSelectors.basePackage("com.crazymaker.springcloud.reactive.user.info.controller"))
                .paths(PathSelectors.any())
                .build();

    }
    @Value("${server.servlet.context-path}")
    private String servletContextPath;

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

    /**
     * 重写 PathProvider ,解决 context-path 重复问题
     * @return
     */
    @Order(Ordered.HIGHEST_PRECEDENCE)
    @Bean
    public PathProvider pathProvider() {
        return new DefaultPathProvider() {
            @Override
            public String getOperationPath(String operationPath) {
                operationPath = operationPath.replaceFirst(servletContextPath, "/");
                UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromPath("/");
                return Paths.removeAdjacentForwardSlashes(uriComponentsBuilder.path(operationPath).build().toString());
            }

            @Override
            public String getResourceListingPath(String groupName, String apiDeclaration) {
                apiDeclaration = super.getResourceListingPath(groupName, apiDeclaration);
                return apiDeclaration;
            }
        };
    }
}
