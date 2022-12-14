package com.crazymaker.springcloud.cloud.center.zuul.filter;


import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

/**
 * 演示过滤器： 黑名单过滤
 */
@Slf4j
@Component
public class DemoFilter extends ZuulFilter
{

    /**
     * 示例所使用的黑名单：实际使用场景，需要从数据库或者其他来源获取
     */
    static List<String> blackList = Arrays.asList("foo", "bar", "test");

    /**
     * 过滤的执行类型
     */
    @Override
    public String filterType()
    {
//		pre：路由之前
//		routing：路由之时
//		post： 路由之后
//		error：发送错误调用
        return FilterConstants.PRE_TYPE;
    }

    /**
     * 过滤的执行次序
     */
    @Override
    public int filterOrder()
    {
        return 0;
    }

    /**
     * 这里可以写判断逻辑，是否要执行过滤，true为跳过
     */
    @Override
    public boolean shouldFilter()
    {

        /***获取上下文*/

        RequestContext ctx = RequestContext.getCurrentContext();

        /***如果请求已经被其他的过滤器终止，则本过滤器也不做处理*/
        if (!ctx.sendZuulResponse())
        {
            return false;
        }
        /**
         *获取请求
         */
        HttpServletRequest request = ctx.getRequest();


        /**
         *返回true 表示需要执行过滤器的run方法
         */
        if (request.getRequestURI().startsWith("/blog/demo"))
        {
            return true;
        }

        /**
         *返回false  表示需要跳过run方法
         */
        return false;
    }

    /**
     * 过滤器的具体逻辑
     * 通过请求判断用户名称
     */
    @Override
    public Object run()
    {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();

        /**
         * 对用户名称进行判断：
         * 如果用户名称在黑名单中，则不再转发给后端的服务提供者
         */

        String username = request.getParameter("username");
        if (username != null && blackList.contains(username))
        {
            log.info(username + " is forbidden:" + request.getRequestURL().toString());

            /**
             * 终止后续的访问流程
             */
            ctx.setSendZuulResponse(false);
            try
            {
                ctx.getResponse().setContentType("text/html;charset=utf-8");
                ctx.getResponse().getWriter().write("对不起，您已经进入黑名单");
            } catch (Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }
        return null;
    }

}