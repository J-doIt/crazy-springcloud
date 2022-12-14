package com.crazymaker.cloud.nacos.demo.gateway.handler;

import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import com.alibaba.csp.sentinel.adapter.gateway.sc.exception.SentinelGatewayBlockExceptionHandler;
import com.alibaba.csp.sentinel.util.function.Supplier;
import com.crazymaker.springcloud.common.result.RestOut;
import com.crazymaker.springcloud.common.util.JsonUtil;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.result.view.ViewResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @ClassName SentinelGatewayBlockExceptionHandlerEX
 * @Description 异常处理器，定制异常信息
 */
public class SentinelGatewayBlockExceptionHandlerEX extends SentinelGatewayBlockExceptionHandler {
    private List<ViewResolver> viewResolvers;
    private List<HttpMessageWriter<?>> messageWriters;
    private final Supplier<ServerResponse.Context> contextSupplier = () ->
    {
        return new ServerResponse.Context() {
            @Override
            public List<HttpMessageWriter<?>> messageWriters() {
                return SentinelGatewayBlockExceptionHandlerEX.this.messageWriters;
            }

            @Override
            public List<ViewResolver> viewResolvers() {
                return SentinelGatewayBlockExceptionHandlerEX.this.viewResolvers;
            }
        };
    };

    public SentinelGatewayBlockExceptionHandlerEX(List<ViewResolver> viewResolvers, ServerCodecConfigurer serverCodecConfigurer) {
        super(viewResolvers, serverCodecConfigurer);
        this.viewResolvers = viewResolvers;
        this.messageWriters = serverCodecConfigurer.getWriters();
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        if (exchange.getResponse().isCommitted()) {
            return Mono.error(ex);
        } else {
            return this.handleBlockedRequest(exchange, ex).flatMap((response) ->
            {
                return this.writeResponse(response, exchange);
            });
        }
    }

    private Mono<ServerResponse> handleBlockedRequest(ServerWebExchange exchange, Throwable throwable) {
        return GatewayCallbackManager.getBlockHandler().handleRequest(exchange, throwable);
    }

    private Mono<Void> writeResponse(ServerResponse response, ServerWebExchange exchange) {
        ServerHttpResponse serverHttpResponse = exchange.getResponse();
        serverHttpResponse.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        RestOut<String> stringMasResponse = RestOut.error("对不起，被限流了");
        byte[] jsonBytes = JsonUtil.object2JsonBytes(stringMasResponse);
        DataBuffer buffer = serverHttpResponse.bufferFactory().wrap(jsonBytes);
        return serverHttpResponse.writeWith(Mono.just(buffer));


//        return response.writeTo(exchange, (ServerResponse.Context) this.contextSupplier.get());
    }
}