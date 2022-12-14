package com.crazymaker.springcloud.cloud.center.zuul.config;

import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

import java.util.ArrayList;
import java.util.List;

@Component
@Primary
public class DocumentationConfig implements SwaggerResourcesProvider
{
    private final RouteLocator routeLocator;

    public DocumentationConfig(RouteLocator routeLocator)
    {
        this.routeLocator = routeLocator;
    }

    /**
     * 配置所有的微服务
     *
     * @return
     */
/*    @Override
    public List<SwaggerResource> getStr() {
        List<SwaggerResource> resources = new ArrayList<>();
        List<Route> routes = routeLocator.getRoutes();
        routes.forEach(route -> {
            resources.add(swaggerResource(route.getId(), route.getFullPath().replace("**", "v2/api-docs"), "1.0"));
        });
        return resources;
    }*/

    /**
     * 配置特定的微服务
     *
     * @return
     */
    @Override
    public List<SwaggerResource> get()
    {
        List resources = new ArrayList<>();
        resources.add(swaggerResource("秒杀服务", "/seckill-provider/v2/api-docs", "1.0" ));
        resources.add(swaggerResource("库存服务", "/stock-provider/v2/api-docs", "1.0" ));
        resources.add(swaggerResource("Demo演示服务", "/demo-provider/v2/api-docs", "1.0" ));
        resources.add(swaggerResource("用户与认证", "/uaa-provider/v2/api-docs", "1.0" ));
        resources.add(swaggerResource("消息服务", "/message-provider/v2/api-docs", "1.0" ));
        resources.add(swaggerResource("管理控制台服务", "/backend-provider/v2/api-docs", "1.0" ));
        resources.add(swaggerResource("控制台代码生成", "/generate-provider/v2/api-docs", "1.0" ));
        return resources;
    }


    private SwaggerResource swaggerResource(String name, String location, String version)
    {
        SwaggerResource swaggerResource = new SwaggerResource();
        swaggerResource.setName(name);
        swaggerResource.setLocation(location);
        swaggerResource.setSwaggerVersion(version);
        return swaggerResource;
    }
}