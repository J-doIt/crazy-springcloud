package com.crazymaker.springcloud.cloud.center.zuul.filter;

import com.crazymaker.springcloud.common.constants.SessionConstants;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
@Slf4j
public class ModifyRequestHeaderFilter extends ZuulFilter
{
    /**
     * 根据条件去判断是否需要路由，是否需要执行该过滤器
     */
    @Override
    public boolean shouldFilter()
    {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();

        /***如果请求已经被其他的过滤器终止，则本过滤器也不做处理*/
        if (!ctx.sendZuulResponse())
        {
            return false;
        }

        /**
         * 存在用户端 认证 token
         */
        String token = request.getHeader(SessionConstants.AUTHORIZATION_HEAD);
        if (!StringUtils.isEmpty(token))
        {
            return true;
        }

        return false;
    }

    /**
     * 调用上游微服务之前，修改请求头，加上 USER—ID 头
     *
     * @return
     * @throws ZuulException
     */
    @Override
    public Object run() throws ZuulException
    {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        //认证成功，请求的  "USER-ID" （USER_IDENTIFIER） 属性被设置
        String sessionSeed = (String) request.getAttribute(SessionConstants.USER_IDENTIFIER);
        //代理请求加上 "USER-ID" 头
        if (StringUtils.isNotBlank(sessionSeed))
        {
            ctx.addZuulRequestHeader(SessionConstants.USER_IDENTIFIER, sessionSeed);
        }
        return null;
    }

    @Override
    public String filterType()
    {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder()
    {
        return 1;
    }

}
