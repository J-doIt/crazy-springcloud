package com.crazymaker.springcloud.standard.filter;

import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class OptionsRequestFilter extends OncePerRequestFilter
{

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException
    {
        //容许跨域的方法之一
        // response.setHeader("Access-Control-Allow-Origin", "*");
        //容许跨域的方法之二
        //解决办法通过获取Origin请求头来动态设置
        if (null == response.getHeader("Access-Control-Allow-Origin"))
        {

            String origin = request.getHeader("Origin");
            if (!StringUtils.isEmpty(origin))
            {
                response.addHeader("Access-Control-Allow-Origin", origin);
            }else
            {
                response.setHeader("Access-Control-Allow-Origin", "*");
            }

        }

        if (null == response.getHeader("Access-Control-Allow-Methods"))
        {
            response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS,HEAD,DELETE");

        }
        if (null == response.getHeader("Access-Control-Max-Age"))
        {
            response.setHeader("Access-Control-Max-Age", "3600");
        }

        if (null == response.getHeader("Access-Control-Allow-Credentials"))
        {
            response.setHeader("Access-Control-Allow-Credentials", "true");

        }
        if (null == response.getHeader("Access-Control-Allow-Headers"))
        {
            response.setHeader("Access-Control-Allow-Headers",
                    "Content-Type,x-requested-with,X-Custom-Header,Authorization,token,backend");
        }
        if (request.getMethod().equals("OPTIONS"))
        {
            return;
        }


        filterChain.doFilter(request, response);
    }

}
