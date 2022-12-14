package com.crazymaker.springcloud.demo.security;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.logout.LogoutFilter;

public class DemoAuthConfigurer<T extends DemoAuthConfigurer<T, B>, B extends HttpSecurityBuilder<B>> extends AbstractHttpConfigurer<T, B>
{

    private DemoAuthFilter authFilter = new DemoAuthFilter();


    @Override
    public void configure(B http) throws Exception
    {
        authFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));

        DemoAuthFilter filter = postProcess(authFilter);
        http.addFilterBefore(filter, LogoutFilter.class);
    }

}
