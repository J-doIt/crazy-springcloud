package com.crazymaker.springcloud.message.config;

import com.crazymaker.springcloud.common.context.SessionHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Configuration
//public class WebConfiguration extends WebMvcConfigurationSupport {
public class WebConfiguration implements WebMvcConfigurer
{
    protected final static Logger LOG = LoggerFactory.getLogger(WebConfiguration.class);

    @Override
    public void addCorsMappings(CorsRegistry registry)
    {
        registry.addMapping("/**" )
                .allowedOrigins("*" )
                .allowedMethods("POST", "GET", "PUT", "OPTIONS", "DELETE" )
                .exposedHeaders(SessionHolder.getSessionIDStore())
                .maxAge(3600)
                .allowCredentials(true);
    }

    // 配置静态资源的，比如html，js，css，等等
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry)
    {
        // 解决swagger无法访问
        registry.addResourceHandler("/swagger-ui.html" )
                .addResourceLocations("classpath:/META-INF/resources/" );
        // 解决swagger的js文件无法访问
        registry.addResourceHandler("/webjars/**" )
                .addResourceLocations("classpath:/META-INF/resources/webjars/" );
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry)
    {
        InterceptorRegistration registration = registry.addInterceptor(new HandlerInterceptor()
        {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
            {
//                LOG.debug("preHandle");

                return true;
            }

            @Override
            public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception
            {

//                SessionHolder.clearData();
//                LOG.debug("postHandle");
            }

            @Override
            public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception
            {
                SessionHolder.clearData();
//                LOG.debug("afterCompletion");
            }
        });
        // 配置拦截路径
        registration.addPathPatterns("/**" );
        // 配置不进行拦截的路径
        registration.excludePathPatterns("/static/**" );
    }

}
