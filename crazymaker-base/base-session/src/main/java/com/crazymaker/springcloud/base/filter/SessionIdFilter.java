package com.crazymaker.springcloud.base.filter;

import com.crazymaker.springcloud.common.constants.SessionConstants;
import com.crazymaker.springcloud.common.context.SessionHolder;
import com.crazymaker.springcloud.standard.redis.RedisRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.session.Session;
import org.springframework.session.data.redis.RedisOperationsSessionRepository;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Slf4j
public class SessionIdFilter extends OncePerRequestFilter
{

    public SessionIdFilter(RedisRepository redisRepository,
                           RedisOperationsSessionRepository sessionRepository)
    {
        this.redisRepository = redisRepository;
        this.sessionRepository = sessionRepository;
    }

    /**
     *  RedisSession DAO
     */
    private RedisOperationsSessionRepository sessionRepository;

    /**
     * Redis DAO
     */
    RedisRepository redisRepository;

    /**
     * 返回true代表不执行过滤器，false代表执行
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return true;
    }

    /**
     * 返回true代表不执行过滤器，false代表执行
     */

    protected boolean shouldNotFilterOld(HttpServletRequest request)
    {

        String userIdentifier = request.getHeader(SessionConstants.USER_IDENTIFIER);
        if (StringUtils.isNotEmpty(userIdentifier))
        {
            return false;
        }

        return true;

    }

    /**
     * 将 session userIdentifier（用户 id） 转成 session id
     *
     * @param request  请求
     * @param response 响应
     * @param chain    过滤器链
     */

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException
    {
        /**
         * 从请求头中，获取  session userIdentifier（用户 id）
         */
        String userIdentifier = request.getHeader(SessionConstants.USER_IDENTIFIER);
        SessionHolder.setUserIdentifer(userIdentifier);
        /**
         * 在redis 中，根据用户 id获取缓存的 session id
         */
        String sid = redisRepository.getSessionId(userIdentifier);

        if (StringUtils.isNotEmpty(sid))
        {
            /**
             * 判断 分布式 session 是否存在
             */
            Session session = sessionRepository.findById(sid);
            if (null != session)
            {
                SessionHolder.setSid(sid);
            }
        }

        chain.doFilter(request, response);
    }
}