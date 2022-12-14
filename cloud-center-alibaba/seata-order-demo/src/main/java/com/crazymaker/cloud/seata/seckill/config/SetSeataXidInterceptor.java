package com.crazymaker.cloud.seata.seckill.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import io.seata.core.context.RootContext;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;

//@Component
@ConditionalOnClass({RequestInterceptor.class, GlobalTransactional.class})
@Slf4j

public class SetSeataXidInterceptor implements RequestInterceptor {
    public void apply(RequestTemplate template) {

        String currentXid = RootContext.getXID();
        if (!StringUtils.isEmpty(currentXid)) {
            log.info(" RootContext.KEY_XID is {}", currentXid);

            template.header(RootContext.KEY_XID, currentXid);
        }
    }


}