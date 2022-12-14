package com.crazymaker.springcloud.cloud.center.zuul.config;

import com.crazymaker.springcloud.base.security.configurer.JwtAuthConfigurer;
import com.crazymaker.springcloud.base.security.handler.JwtRefreshSuccessHandler;
import com.crazymaker.springcloud.base.security.provider.JwtAuthenticationProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.session.data.redis.RedisOperationsSessionRepository;

import javax.annotation.Resource;

@ConditionalOnWebApplication
@EnableWebSecurity()
public class ZuulWebSecurityConfig extends WebSecurityConfigurerAdapter
{

//
//    @Resource
//    private SessionIdFilter sessionSeedFilter;


    @Override
    protected void configure(HttpSecurity http) throws Exception
    {
        http.csrf().disable()
                .authorizeRequests()
//                .anyRequest().permitAll()
                .antMatchers(
                        "/**/api/user/login/v1",
                        "/**/actuator/hystrix",
                        "/**/validata/code/**",
                        "/**/validate/**",
                        "/**/oauth/**",
                        "/**/css/**",
                        "/**/images/**",
                        "/**/js/**",
                        "/**/users-anon/**",
                        "/**/login.html",
                        "/**/api/session/login/v1",
                        "/**/oauth/user/token",
                        "/**/actuator/hystrix.stream",
                        "/**/api/mock/**",
                        "/**/v2/api-docs",
                        "/**/swagger-resources/configuration/ui",
                        "/**/swagger-resources",
                        "/**/swagger-resources/configuration/security",
                        "/**/swagger-ui.html",
                        "/**/css/**",
                        "/**/js/**",
                        "/**/api/seckill/**",
                        "/**/blog/**",
                        "/blog/**",
                        "/**/images/**",
                        "/**/webjars/**",
                        "/seckill-provider/api/seckill/seglock/getSeckillResult/v1",
                        "/seckill-provider/api/crazymaker/rockmq/sendSeckill/v1",
                        "/seckill-provider/api/seckill/order/**",
                        "/demo-provider/api/demo/header/echo/v1",
                        "/demo-provider/api/demo/hello/v1",
                        "/demo-provider/api/demo/hello/v1",
                        "/**/favicon.ico",
                        "/ZuulFilter/demo"
                ).permitAll()
                .antMatchers("/image/**").permitAll()
                .antMatchers("/admin/**").hasAnyRole("ADMIN")
                .and()
                .authorizeRequests().anyRequest().authenticated()

                .and()

                .formLogin().disable()
                .sessionManagement().disable()
                .cors()
                .and()
//                .headers().addHeaderWriter(new StaticHeadersWriter(Arrays.asList(
//                new Header("Access-control-Allow-Origin", "*"),
//                new Header("Access-Control-Expose-Headers", SessionConstants.AUTHORIZATION_HEAD))))
//                .and()
//                .addFilterBefore(sessionSeedFilter, CorsFilter.class)
//                .addFilterAfter(new OptionsRequestFilter(), CorsFilter.class)
                .apply(new JwtAuthConfigurer<>()).tokenValidSuccessHandler(jwtRefreshSuccessHandler()).permissiveRequestUrls("/logout")
                .and()
                .logout().disable()
//                .addFilterBefore(new SessionDataLoadFilter(), SessionManagementFilter.class)
                .sessionManagement().disable();
//    		.sessionManagement().maximumSessions(1)
//                  .maxSessionsPreventsLogin(false)
//                  .expiredUrl("/login?expired")
//			      .sessionRegistry(sessionRegistry());


    }


    @Override
    public void configure(WebSecurity web) throws Exception
    {

        web.ignoring().antMatchers(
                "/**/api/user/login/v1",
                "/**/actuator/hystrix",
                "/**/validata/code/**",
                "/**/validate/**",
                "/**/oauth/**",
                "/**/css/**",
                "/**/images/**",
                "/**/js/**",
                "/**/users-anon/**",
                "/**/login.html",
                "/**/api/session/login/v1",
                "/**/oauth/user/token",
                "/**/actuator/hystrix.stream",
                "/**/api/mock/**",
                "/**/v2/api-docs",
                "/**/swagger-resources/configuration/ui",
                "/**/swagger-resources",
                "/**/swagger-resources/configuration/security",
                "/**/swagger-ui.html",
                "/**/css/**",
                "/**/js/**",
                "/**/images/**",
                "/**/webjars/**",
                "/**/api/seckill/**",
                "/blog/**",
                "/**/blog/**",
                "/seckill-provider/api/seckill/seglock/getSeckillResult/v1",
                "/seckill-provider/api/crazymaker/rockmq/sendSeckill/v1",
                "/seckill-provider/api/seckill/order/**",
                "/demo-provider/api/demo/header/echo/v1",
                "/demo-provider/api/demo/hello/v1",
                "/**/favicon.ico",
                "/ZuulFilter/demo"
        );
    }

    //配置认证建造者，由其负责建造 AuthenticationManager 认证管理者实例
    //所建造的 AuthenticationManager 实例，会作为 HTTP 请求共享对象
    //可以通过 http.getSharedObject(AuthenticationManager.class) 来获取
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception
    {
//        auth.authenticationProvider(daoAuthenticationProvider());
        auth.authenticationProvider(jwtAuthenticationProvider());
    }



    @Resource
    RedisOperationsSessionRepository sessionRepository;


    @DependsOn({"sessionRepository"})
    @Bean("jwtAuthenticationProvider")
    protected AuthenticationProvider jwtAuthenticationProvider()
    {
        return new JwtAuthenticationProvider(sessionRepository);
    }
    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception
    {
        return super.authenticationManagerBean();
    }


    @Bean
    protected JwtRefreshSuccessHandler jwtRefreshSuccessHandler()
    {
        return new JwtRefreshSuccessHandler();
    }


}
