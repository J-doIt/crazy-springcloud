package com.crazymaker.cloud.nacos.demo.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

//增加响应头
//@Component
public class AuthorizationFilter implements WebFilter {

    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerWebExchange mutatedExchange = exchange;

        ServerHttpResponse response = mutatedExchange.getResponse();
        String webRedirectUrl = mutatedExchange.getRequest().getURI().toString();

        LOGGER.info("[aida][method:filter][info:webRedirectUrl is {}]", webRedirectUrl);
        if (!webRedirectUrl.contains("/udata/callback")) {
            ResponseCookie.ResponseCookieBuilder responseCookieBuilder = ResponseCookie
                    .from("webRedirectUrl", webRedirectUrl)
                    .maxAge(12 * 60 * 60)
                    .path("/").httpOnly(true).secure(true);
            response.addCookie(responseCookieBuilder.build());
            ResponseCookie.ResponseCookieBuilder responseCookieBuilder2 = ResponseCookie
                    .from("webRedirectUrl2", webRedirectUrl)
                    .maxAge(12 * 60 * 60)
                    .path("/");
            response.addCookie(responseCookieBuilder2.build());
            ResponseCookie.ResponseCookieBuilder responseCookieBuilder3 = ResponseCookie
                    .from("webRedirectUrl3", webRedirectUrl)
                    .maxAge(12 * 60 * 60)
                    .path("/").httpOnly(true);
            response.addCookie(responseCookieBuilder3.build());

            ResponseCookie.ResponseCookieBuilder responseCookieBuilder4 = ResponseCookie
                    .from("webRedirectUrl4", webRedirectUrl)
                    .maxAge(12 * 60 * 60)
                    .path("/").secure(true);
            response.addCookie(responseCookieBuilder4.build());
            LOGGER.info("[aida][method:filter][info:response set cookies webRedirectUrl is {}]", webRedirectUrl);
        }
        return chain.filter(mutatedExchange);
    }
}
