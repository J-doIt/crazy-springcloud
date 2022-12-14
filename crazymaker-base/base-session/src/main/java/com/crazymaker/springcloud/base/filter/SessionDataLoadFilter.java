package com.crazymaker.springcloud.base.filter;

import com.crazymaker.springcloud.base.service.impl.UserLoadServiceImpl;
import com.crazymaker.springcloud.common.constants.SessionConstants;
import com.crazymaker.springcloud.common.context.SessionHolder;
import com.crazymaker.springcloud.common.dto.UserDTO;
import com.crazymaker.springcloud.common.util.JsonUtil;
import com.crazymaker.springcloud.standard.redis.RedisRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static com.crazymaker.springcloud.common.context.SessionHolder.G_USER;

@Slf4j
public class SessionDataLoadFilter extends OncePerRequestFilter {


    UserLoadServiceImpl userLoadService;
    RedisRepository redisRepository;

    public SessionDataLoadFilter(UserLoadServiceImpl userLoadService, RedisRepository redisRepository) {
        this.userLoadService = userLoadService;
        this.redisRepository = redisRepository;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        //获取前面的 SessionIdFilter 过滤器加载的 session id
        String sid = SessionHolder.getSid();
        HttpSession session = request.getSession();

        /**
         * 之前的 session 不存在
         */
        if (StringUtils.isEmpty(sid) || !sid.equals(request.getSession().getId())) {
            // 取得当前的 session id
            sid = session.getId();
            // user id 和  session id  作为 key-value 保存到redis
            redisRepository.setSessionId(SessionHolder.getUserIdentifier(), sid);
            SessionHolder.setSid(sid);
        }

        /**
         *获取 session 中的用户信息
         *为空表示用户第一次发起请求，加载用户信息到 session 中
         */
        if (null == session.getAttribute(G_USER)) {
            String uid = SessionHolder.getUserIdentifier();
            UserDTO userDTO = null;

            if (SessionHolder.getSessionIDStore().equals(SessionConstants.SESSION_STORE)) {
                //用户端：装载用户端的用户信息
                userDTO = userLoadService.loadFrontEndUser(Long.valueOf(uid));

            } else {
                //管理控制台控制台：装载管理控制台的用户信息
                userDTO = userLoadService.loadBackEndUser(Long.valueOf(uid));

            }
            /**
             * 将用户信息缓存起来
             */
            session.setAttribute(G_USER, JsonUtil.pojoToJson(userDTO));
        }

        /**
         * 将Session请求，保持到  SessionHolder 的 ThreadLocal 本地变量中，方便统一获取
         */
        SessionHolder.setSession(session);
        SessionHolder.setRequest(request);
        filterChain.doFilter(request, response);
    }

    /**
     * 返回true代表不执行过滤器，false代表执行
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return true;
    }

    protected boolean shouldNotFilterOld(HttpServletRequest request) {
        if (null == SessionHolder.getUserIdentifier()) {
            return true;
        }
        return false;
    }

}
