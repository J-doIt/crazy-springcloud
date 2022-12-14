package com.crazymaker.cloud.nacos.demo.gateway.config;

import com.crazymaker.cloud.nacos.demo.gateway.filter.UserIdCheckGateWayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;


@Component
public class UserIdCheckGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {
    @Override
    public GatewayFilter apply(Object config) {
        return new UserIdCheckGateWayFilter();
    }
}