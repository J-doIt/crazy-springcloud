package com.crazymaker.springcloud.base.filter;

import com.crazymaker.springcloud.common.constants.SessionConstants;
import com.crazymaker.springcloud.common.context.SessionHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Slf4j
public class SessionTypeFilter extends OncePerRequestFilter {

    public SessionTypeFilter() {
    }

    /**
     * 验证请求url与配置的url是否匹配的工具类
     */
    private AntPathMatcher pathMatcher = new AntPathMatcher();

    /**
     * 返回true代表不执行过滤器，false代表执行
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return true;
    }

    protected boolean shouldNotFilterOld(HttpServletRequest request) {


        if (StringUtils.isNotEmpty(request.getHeader(SessionConstants.AUTHORIZATION_HEAD))) {
            return false;
        }


        String uri = request.getRequestURI();
        if (pathMatcher.match("/**/*.html", uri)) {
            return true;
        }
        if (pathMatcher.match("/**/*.jpg", uri)) {
            return true;
        }
        return false;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {


        String tokenFlag = request.getHeader(SessionConstants.AUTHORIZATION_HEAD);
        if (StringUtils.isNotEmpty(tokenFlag)) {
            SessionHolder.setSessionIDStore(SessionConstants.SESSION_STORE);
            chain.doFilter(request, response);
            return;
        }


        SessionHolder.setSessionIDStore(SessionConstants.SESSION_STORE);
        chain.doFilter(request, response);
        return;
    }
}