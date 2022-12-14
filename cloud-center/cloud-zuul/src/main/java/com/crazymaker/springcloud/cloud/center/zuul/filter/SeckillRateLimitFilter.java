package com.crazymaker.springcloud.cloud.center.zuul.filter;


import com.crazymaker.springcloud.common.distribute.rateLimit.RateLimitService;
import com.crazymaker.springcloud.standard.ratelimit.RedisRateLimitImpl;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@ConditionalOnBean(RedisRateLimitImpl.class)
@Component
public class SeckillRateLimitFilter extends ZuulFilter
{

    /**
     * Redis 限流服务实例
     */
    @Resource(name = "redisRateLimitImpl")
    RateLimitService redisRateLimitImpl;

    @Override
    public String filterType()
    {
//		pre：路由之前
//		routing：路由之时
//		post： 路由之后
//		error：发送错误调用
        return "pre";
    }

    /**
     * 过滤的顺序
     */
    @Override
    public int filterOrder()
    {
        return 0;
    }

    /**
     * 这里可以写逻辑判断，是否要过滤，true为永远过滤。
     */
    @Override
    public boolean shouldFilter()
    {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        /**
         * 如果请求已经被其他的过滤器终止，则本过滤器也不做处理
         **/
        if (!ctx.sendZuulResponse())
        {
            return false;
        }
        /**
         * 对秒杀令牌进行限流
         */
        if (request.getRequestURI().startsWith("/seckill-provider/api/seckill/redis/token/v1"))
        {
            return true;
        }

        return false;
    }

    /**
     * 过滤器的具体逻辑
     */
    @Override
    public Object run()
    {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();

        String skuId = request.getParameter("skuId");
        if (skuId != null)
        {
            String cacheKey = "seckill:" + skuId;
            Boolean limited = redisRateLimitImpl.tryAcquire(cacheKey);

            if (limited)
            {
                /**
                 * 被限流后的降级
                 */
                String msg = "参与抢购的人太多，请稍后再试一试";
                fallback(ctx, msg);
                return null;
            }

            return null;
        } else
        {
            /**
             * 参数输入错误时的降级处理
             */
            String msg = "必须输入抢购的商品";
            fallback(ctx, msg);
            return null;
        }

    }

    /**
     * 被限流后的降级处理
     *
     * @param ctx
     * @param msg
     */
    private void fallback(RequestContext ctx, String msg)
    {
        ctx.setSendZuulResponse(false);
        try
        {
            ctx.getResponse().setContentType("text/html;charset=utf-8");
            ctx.getResponse().getWriter().write(msg);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}