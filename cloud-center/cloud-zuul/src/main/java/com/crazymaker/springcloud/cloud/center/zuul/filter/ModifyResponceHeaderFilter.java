package com.crazymaker.springcloud.cloud.center.zuul.filter;

import com.crazymaker.springcloud.common.context.SessionHolder;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;

import javax.servlet.http.HttpServletResponse;

//@Component
@Slf4j
public class ModifyResponceHeaderFilter extends ZuulFilter
{

    @Override
    public boolean shouldFilter()
    {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletResponse response = ctx.getResponse();
        String sessionSeed = response.getHeader(SessionHolder.getSessionIDStore());
        log.info("sessionSeed=" + sessionSeed);
        if (!StringUtils.isEmpty(sessionSeed))
        {
            return true;
        }
        return false;
    }

    @Override
    public Object run() throws ZuulException
    {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletResponse response = ctx.getResponse();
        String sign = response.getHeader(SessionHolder.getSessionIDStore());

        ctx.addZuulResponseHeader(SessionHolder.getSessionIDStore(), sign);


        return null;
    }

    @Override
    public String filterType()
    {
        return FilterConstants.POST_TYPE;
    }

    @Override
    public int filterOrder()
    {
        return FilterConstants.SEND_RESPONSE_FILTER_ORDER - 2;
    }
}
