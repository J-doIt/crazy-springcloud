package com.crazymaker.springcloud.demo.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import java.util.LinkedHashMap;
import java.util.Map;

public class DemoAuthProvider implements AuthenticationProvider
{
    public DemoAuthProvider()
    {
    }

    //模拟数据源，实际从DB中获取
    private Map<String, String> map = new LinkedHashMap<>();
    //初始化模拟的数据源，放入出两个用户
    {
        map.put("zhangsan", "123456" );
        map.put("lisi", "123456" );
    }


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException
    {

        DemoToken token = (DemoToken) authentication;

        String rawPass = map.get(token.getUserName());

        if (!token.getPassword().equals(rawPass))
        {
            token.setAuthenticated(false);
            throw new BadCredentialsException("认证有误：令牌校验失败" );
        }

        token.setAuthenticated(true);
        return token;

    }

    /**
     * 判断令牌是否被支持
     * @param authentication
     * @return
     */

    @Override
    public boolean supports(Class<?> authentication)
    {
        return authentication.isAssignableFrom(DemoToken.class);
    }

}
