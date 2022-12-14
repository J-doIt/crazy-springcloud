package com.crazymaker.cloud.nacos.demo.gateway.filter;

import com.crazymaker.springcloud.common.result.RestOut;
import com.crazymaker.springcloud.common.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

//@Component
@Slf4j
public class UserIdCheckGateWayFilter implements GatewayFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String url = exchange.getRequest().getPath().pathWithinApplication().value();
        log.info("请求URL:" + url);
        log.info("method:" + exchange.getRequest().getMethod());
       /*   String secret = exchange.getRequest().getHeaders().getFirst("secret");
        if (StringUtils.isBlank(secret))
        {
            return chain.filter(exchange);
        }*/
        //获取param 请求参数
        String uname = exchange.getRequest().getQueryParams().getFirst("uname");
        //获取header
        String userId = exchange.getRequest().getHeaders().getFirst("user-id");
        log.info("userId：" + userId);

        if (StringUtils.isBlank(userId)) {
            log.info("*****头部验证不通过，请在头部输入  user-id");
            //终止请求，直接回应
            exchange.getResponse().setStatusCode(HttpStatus.NOT_ACCEPTABLE);
//              return exchange.getResponse().setComplete();
            ServerHttpResponse serverHttpResponse = exchange.getResponse();
            serverHttpResponse.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
            RestOut<String> stringMasResponse = RestOut.error("头部异常，请带上 user-id");
            byte[] jsonBytes = JsonUtil.object2JsonBytes(stringMasResponse);
            DataBuffer buffer = serverHttpResponse.bufferFactory().wrap(jsonBytes);
            return serverHttpResponse.writeWith(Mono.just(buffer));
        }
        return chain.filter(exchange);
    }

    //   值越小，优先级越高
//    int HIGHEST_PRECEDENCE = -2147483648;
//    int LOWEST_PRECEDENCE = 2147483647;
    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }
}


