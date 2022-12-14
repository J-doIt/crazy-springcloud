package com.crazymaker.cloud.nacos.demo.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SimpleGateWayConfig {
    //手工增加一个路由配置
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder routeLocatorBuilder) {
        RouteLocatorBuilder.Builder builder = routeLocatorBuilder.routes();
        builder
                .route("path_rote_at_guigu", r -> r.path("/guonei")
                        .uri("http://news.baidu.com/guonei"))
                .route("csdn_route", r -> r.path("/csdn")
                        .uri("https://blog.csdn.net"))
                .route("csdn_route3", r -> r.path("/csdn3/**")
                        .filters(f -> f.rewritePath("/csdn3/(?<segment>.*)", "/$\\{segment}"))
                        .uri("https://blog.csdn.net/"))
                .route("blog3_rewrite_filter", r -> r.path("/blog3/**")
                        .filters(f -> f.rewritePath("/blog3/(?<segment>.*)", "/$\\{segment}"))
                        .uri("https://blog.csdn.net/"))
                .route("rewritepath_route", r -> r.path("/baidu/**")
                        .filters(f -> f.rewritePath("/baidu/(?<segment>.*)", "/$\\{segment}"))
                        .uri("http://www.baidu.com"))
                .route("path_route", r -> r.path("/get")
                        .uri("http://httpbin.org"))
                .route("host_route", r -> r.host("*.myhost.org")
                        .uri("http://httpbin.org"))
                .route("rewrite_route", r -> r.host("*.rewrite.org")
                        .filters(f -> f.rewritePath("/foo/(?<segment>.*)", "/${segment}"))
                        .uri("http://httpbin.org"))
                .route("hystrix_route", r -> r.host("*.hystrix.org")
                        .filters(f -> f.hystrix(c -> c.setName("slowcmd")))
                        .uri("http://httpbin.org"))
                .route("hystrix_fallback_route", r -> r.host("*.hystrixfallback.org")
                        .filters(f -> f.hystrix(c -> c.setName("slowcmd").setFallbackUri("forward:/hystrixfallback")))
                        .uri("http://httpbin.org"))

                .build();
        return builder.build();
    }
}
