package com.crazymaker.springcloud.standard.config;

import com.crazymaker.springcloud.common.constants.SessionConstants;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * Feign Configuration
 */
@Configuration
public class TokenFeignConfiguration implements RequestInterceptor
{
    protected final static Logger LOG = LoggerFactory.getLogger(TokenFeignConfiguration.class);


    @Override
    public void apply(RequestTemplate template)
    {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if(null==attributes)
        {
            return;
        }
        HttpServletRequest request = attributes.getRequest();

        String token = request.getHeader(SessionConstants.AUTHORIZATION_HEAD);

        if (null != token)
        {
            template.header(SessionConstants.AUTHORIZATION_HEAD, new String[]{token});
        }



        String sessionSeed = request.getHeader(SessionConstants.USER_IDENTIFIER);
        if (null != sessionSeed)
        {
            template.header(SessionConstants.USER_IDENTIFIER, new String[]{sessionSeed});
        }

    }
}
