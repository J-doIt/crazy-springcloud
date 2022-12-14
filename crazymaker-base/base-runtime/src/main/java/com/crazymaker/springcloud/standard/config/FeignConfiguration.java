package com.crazymaker.springcloud.standard.config;

import com.crazymaker.springcloud.common.constants.SessionConstants;
import feign.Logger;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * FeignConfiguration
 */
@Configuration
public class FeignConfiguration implements RequestInterceptor
{

    /**
     * 配置 PRC 时的请求头部与参数
     *
     * @param template 请求模板
     */
    @Override
    public void apply(RequestTemplate template)
    {
        /**
         * 取得请求上下文属性
         */
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (null == attributes)
        {
            return;
        }
        HttpServletRequest request = attributes.getRequest();

        /**
         * 获取令牌
         */
        String token = request.getHeader(SessionConstants.AUTHORIZATION_HEAD);
        if (null != token)
        {
            token = StringUtils.removeStart(token, "Bearer ");
            /**
             * 设置令牌
             */
            template.header(SessionConstants.AUTHORIZATION_HEAD, new String[]{token});
        }


        String userIdentifier = request.getHeader(SessionConstants.USER_IDENTIFIER);
        if (null != userIdentifier)
        {
            template.header(SessionConstants.USER_IDENTIFIER, new String[]{userIdentifier});
        }

    }

    /**
     * 配置负载均衡策略
     */
//    @Bean
//    public IRule ribbonRule()
//    {
//        /**
//         * 配置为线性轮询策略
//         */
//        return new RoundRobinRule();
//    }

    /**
     * 配置 Feign 日志等级
     */
    @Bean
    Logger.Level feignLoggerLevel()
    {
        /**需要根据实际情况选择合适的的Feign日志level
         * BASIC ：记录请求方法、URL、响应状态代码、执行时间。
         * NONE ：没有日志
         */
        return Logger.Level.NONE;
    }


}
