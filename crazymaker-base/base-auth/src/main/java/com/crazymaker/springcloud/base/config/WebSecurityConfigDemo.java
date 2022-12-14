package com.crazymaker.springcloud.base.config;

import com.crazymaker.springcloud.base.security.configurer.JwtAuthConfigurer;
import com.crazymaker.springcloud.base.security.handler.JwtRefreshSuccessHandler;
import com.crazymaker.springcloud.base.security.provider.JwtAuthenticationProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.session.data.redis.RedisOperationsSessionRepository;

import javax.annotation.Resource;

//@EnableWebSecurity
public class WebSecurityConfigDemo extends WebSecurityConfigurerAdapter {


    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers(
                        "/**/api/user/login/v1",
                        "/**/v2/api-docs",
                        "/**/swagger-resources/configuration/ui",
                        "/**/swagger-resources",
                        "/**/swagger-resources/configuration/security",
//                "/api/user/say/hello/v1",
//                "/api/user/add/v1",
//                "/api/user/speed/test/v1",
//                "/api/user/*/detail/v1",
                        "/images/**",
                        "/swagger-ui.html",
                        "/webjars/**",
                        "/**/favicon.ico",
                        "/**/css/**",
                        "/**/js/**")
                .permitAll()
                .anyRequest().authenticated()

//                .antMatchers("/image/**").permitAll()
//                .antMatchers("/admin/**").hasAnyRole("ADMIN")
                .and()

                .formLogin().disable()
                .sessionManagement().disable()
                .cors()
                .and()

//                .addFilterAfter(new OptionsRequestFilter(), CorsFilter.class)
                .apply(new JwtAuthConfigurer<>()).tokenValidSuccessHandler(jwtRefreshSuccessHandler()).permissiveRequestUrls("/logout")
//                .and()
//                .logout()
//		        .logoutUrl("/logout")   //默认就是"/logout"
//                .addLogoutHandler(tokenClearLogoutHandler())
//                .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler())
                .and()
                .sessionManagement().disable();


    }


    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(
                "/**/api/user/login/v1",
                "/**/v2/api-docs",
                "/**/swagger-resources/configuration/ui",
                "/**/swagger-resources",
                "/**/swagger-resources/configuration/security",
//                "/api/user/say/hello/v1",
//                "/api/user/add/v1",
//                "/api/user/speed/test/v1",
//                "/api/user/*/detail/v1",
                "/images/**",
                "/swagger-ui.html",
                "/webjars/**",
                "/**/favicon.ico",
                "/**/css/**",
                "/**/js/**"
        );

    }


    @Resource
    RedisOperationsSessionRepository sessionRepository;


    @DependsOn({"sessionRepository"})
    @Bean("jwtAuthenticationProvider")
    protected AuthenticationProvider jwtAuthenticationProvider() {
        return new JwtAuthenticationProvider(sessionRepository);
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.authenticationProvider(daoAuthenticationProvider());
        auth.authenticationProvider(jwtAuthenticationProvider());
    }


    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }


//    @Resource
//    private CustomUserAuthServiceImpl userAuthService;

//
//    @Bean("daoAuthenticationProvider")
//    protected AuthenticationProvider daoAuthenticationProvider() throws Exception
//    {
//        //这里会默认使用BCryptPasswordEncoder比对加密后的密码，注意要跟createUser时保持一致
//        DaoAuthenticationProvider daoProvider = new DaoAuthenticationProvider();
//        daoProvider.setUserDetailsService(userDetailsService());
//        return daoProvider;
//    }


    @Bean
    protected JwtRefreshSuccessHandler jwtRefreshSuccessHandler() {
        return new JwtRefreshSuccessHandler();
    }


//    @Override
//    protected UserDetailsService userDetailsService()
//    {
//        return new CustomUserAuthServiceImpl();
//    }
//

//    @Bean
//    protected JsonLoginSuccessHandler jsonLoginSuccessHandler()
//    {
//        return new JsonLoginSuccessHandler(userAuthService);
//    }


//    @Bean
//    protected TokenClearLogoutHandler tokenClearLogoutHandler()
//    {
//        return new TokenClearLogoutHandler(userAuthService);
//    }

//    @Bean
//    protected CorsConfigurationSource corsConfigurationSource()
//    {
//        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.setAllowedOrigins(Arrays.asList("*"));
//        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "HEAD", "OPTION"));
//        configuration.setAllowedHeaders(Arrays.asList("*"));
//        configuration.addExposedHeader(SessionConstants.AUTHORIZATION_HEAD);
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//        return source;
//    }

}
