package com.crazymaker.cloud.seata.seckill.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import io.seata.core.context.RootContext;
import io.seata.spring.annotation.GlobalTransactional;
import org.apache.commons.lang.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;

//@Component
@ConditionalOnClass({RequestInterceptor.class, GlobalTransactional.class})
public class SetSeataXidInterceptor implements RequestInterceptor {
    public void apply(RequestTemplate template) {

        String currentXid = RootContext.getXID();
        if (!StringUtils.isEmpty(currentXid)) {
            template.header(RootContext.KEY_XID, currentXid);
        }
    }


}