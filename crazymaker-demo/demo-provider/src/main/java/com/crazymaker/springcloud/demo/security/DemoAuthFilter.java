package com.crazymaker.springcloud.demo.security;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.RequestHeaderRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class DemoAuthFilter extends OncePerRequestFilter
{
    public static final String USER_INFO = "user-info";


    private static final String AUTHORIZATION_HEAD = "token";
    //认证失败的处理器
    private AuthenticationFailureHandler failureHandler =    new AuthFailureHandler();

    //判断认证头是否存在，不存在直接跳过了
    private RequestMatcher requiresAuthenticationRequestMatcher =
            new RequestHeaderRequestMatcher(AUTHORIZATION_HEAD);

    //AuthenticationManager 接口，是认证方法的入口，接收一个Authentication对象作为参数
    //ProviderManager 是 AuthenticationManager的一个实现类,完成实际的认证
    private AuthenticationManager authenticationManager;


    @Override
    public void afterPropertiesSet()
    {
        Assert.notNull(failureHandler, "AuthenticationFailureHandler must be specified" );
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException
    {
        /**
         * 处理掉不需要过滤的
         */
        if (!requiresAuthentication(request, response))
        {

            filterChain.doFilter(request, response);
            return;
        }

        AuthenticationException failed = null;

        try
        {

            Authentication returnToken=null;
            boolean succeed=false;
            String token = request.getHeader(AUTHORIZATION_HEAD);
            String[] parts = token.split("," );
/*
            //方式一:DemoToken 认证演示
            DemoToken demoToken = new DemoToken(parts[0],parts[1]);
            returnToken = (DemoToken) this.getAuthenticationManager().authenticate(demoToken);
            succeed=demoToken.isAuthenticated();
            //方式一end*/


            //方式二:数据库 认证演示
            UserDetails userDetails = User.builder()
                    .username(parts[0])
                    .password(parts[1])
                    .authorities(USER_INFO)
                    .build();
            //创建一个用户名+密码的凭证，一般情况下，这里的密码需要明文
            Authentication userPassToken = new UsernamePasswordAuthenticationToken(userDetails,
                    userDetails.getPassword(),
                    userDetails.getAuthorities());
            //进入认证流程
            returnToken =this.getAuthenticationManager().authenticate(userPassToken);


            succeed=userPassToken.isAuthenticated();
            //方式二end



            if (succeed)
            {
                //认证成功,设置上下文令牌
                SecurityContextHolder.getContext().setAuthentication(returnToken);
                //执行后续的操作
                filterChain.doFilter(request, response);
                return;
            }
        } catch (Exception e)
        {
            logger.error("认证有误", e);
            failed = new AuthenticationServiceException("请求头认证消息格式错误",e );
        }
        if(failed == null)
        {
            failed = new AuthenticationServiceException("认证失败");
        }
        //认证失败了
        SecurityContextHolder.clearContext();
        failureHandler.onAuthenticationFailure(request, response, failed);
    }

    protected AuthenticationManager getAuthenticationManager()
    {
        return authenticationManager;
    }

    public void setAuthenticationManager(AuthenticationManager authenticationManager)
    {
        this.authenticationManager = authenticationManager;
    }

    protected boolean requiresAuthentication(HttpServletRequest request,
                                             HttpServletResponse response)
    {
        return requiresAuthenticationRequestMatcher.matches(request);

    }


}
