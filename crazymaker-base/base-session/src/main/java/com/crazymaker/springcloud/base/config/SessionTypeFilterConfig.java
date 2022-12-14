package com.crazymaker.springcloud.base.config;

import com.crazymaker.springcloud.base.filter.SessionTypeFilter;
import com.crazymaker.springcloud.common.constants.SessionConstants;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SessionTypeFilterConfig
{
    @Bean
    public FilterRegistrationBean buildSessionTypeFilter()
    {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setOrder(SessionConstants.FILTER_ORDER_SESSION_PREFIX);
        filterRegistrationBean.setFilter(new SessionTypeFilter());
        filterRegistrationBean.setName("sessionTypeFilter" );
        filterRegistrationBean.addUrlPatterns("/*" );
        return filterRegistrationBean;
    }


}