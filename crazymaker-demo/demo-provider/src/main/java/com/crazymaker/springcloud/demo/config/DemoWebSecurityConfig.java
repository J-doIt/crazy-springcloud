package com.crazymaker.springcloud.demo.config;

import com.crazymaker.springcloud.demo.security.DemoAuthConfigurer;
import com.crazymaker.springcloud.demo.security.DemoAuthProvider;
import com.crazymaker.springcloud.demo.security.DemoAuthUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.Resource;

/**
 * 学习 SpringSecurity 时，本配置类才启用
 */

//@EnableWebSecurity
public class DemoWebSecurityConfig extends WebSecurityConfigurerAdapter
{


    protected void configure(HttpSecurity http) throws Exception
    {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers(
                        "/**/v2/api-docs",
                        "/**/swagger-resources/configuration/ui",
                        "/**/swagger-resources",
                        "/**/swagger-resources/configuration/security",
                        "/images/**",
                        "/**/swagger-ui.html",
                        "/**/webjars/**",
                        "/**/favicon.ico",
                        "/**/css/**",
                        "/**/js/**"
                )
                .permitAll()
                .anyRequest().authenticated()
                .and()

                .formLogin().disable()
                .sessionManagement().disable()
                .cors()

                .and()
                .apply(new DemoAuthConfigurer<>())
                .and()
                .sessionManagement().disable();


    }


    @Override
    public void configure(WebSecurity web) throws Exception
    {
        web.ignoring().antMatchers(
                "/**/v2/api-docs",
                "/**/swagger-resources/configuration/ui",
                "/**/swagger-resources",
                "/**/swagger-resources/configuration/security",
                "/images/**",
                "/**/swagger-ui.html",
                "/**/webjars/**",
                "/**/favicon.ico",
                "/**/css/**",
                "/**/js/**"
        );

    }


    @Override
    protected void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception
    {
        authenticationManagerBuilder.authenticationProvider(demoAuthProvider());
        authenticationManagerBuilder.authenticationProvider(daoAuthenticationProvider());
//        authenticationManagerBuilder.authenticationProvider(jwtAuthenticationProvider());
    }


    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception
    {
        return super.authenticationManagerBean();
    }

    @Bean("demoAuthProvider" )
    protected DemoAuthProvider demoAuthProvider()
    {
        return new DemoAuthProvider();
    }

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private DemoAuthUserService demoUserAuthService;

    @Bean("daoAuthenticationProvider")
    protected AuthenticationProvider daoAuthenticationProvider() throws Exception
    {
        //创建一个数据源提供者
        DaoAuthenticationProvider daoProvider = new DaoAuthenticationProvider();
        //设置加密器，使用全局的 BCryptPasswordEncoder 加密器

        daoProvider.setPasswordEncoder(passwordEncoder);
        //设置用户数据源服务
        daoProvider.setUserDetailsService(demoUserAuthService);
        return daoProvider;

    }

}
