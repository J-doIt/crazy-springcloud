package com.crazymaker.springcloud.reactive.user.info.config;

import com.crazymaker.springcloud.reactive.user.info.config.handler.UserReactiveHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.WebFilter;

import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Configuration
public class RoutersConfig
{

    @Bean
    RouterFunction<ServerResponse> routes(UserReactiveHandler handler)
    {

        // 下面的相当于类里面的 @RequestMapping
        // 得到所有用户
        return RouterFunctions.route(GET("/user"), handler::getAllUser)
                // 创建用户
                .andRoute(POST("/user").and(accept(MediaType.APPLICATION_JSON_UTF8)), handler::createUser)
                // 删除用户
                .andRoute(DELETE("/user/{id}"), handler::deleteUserById);
    }

    @Value("${server.servlet.context-path}")
    private String contextPath;

    //处理上下文路径，没有上下文路径，此函数可以忽略
    @Bean
    public WebFilter contextPathWebFilter()
    {
        return (exchange, chain) ->
        {
            ServerHttpRequest request = exchange.getRequest();

            String requestPath = request.getURI().getPath();
            if (requestPath.startsWith(contextPath))
            {
                return chain.filter(
                        exchange.mutate()
                                .request(request.mutate().contextPath(contextPath).build())
                                .build());
            }
            return chain.filter(exchange);
        };
    }
}
