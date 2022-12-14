package com.crazymaker.cloud.seata.seckill.config;

import io.seata.core.context.RootContext;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * WebConfiguration
 */
//@Configuration
@Slf4j
public class SeataConfiguration implements WebMvcConfigurer {
    protected final static Logger LOG = LoggerFactory.getLogger(SeataConfiguration.class);
//

    //增加拦截器，
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        InterceptorRegistration registration = registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//                LOG.debug("preHandle");

// 绑定 XID，自动创建分支事物
                // 异常后，整个调用链路回滚
                String keyId = request.getHeader(RootContext.KEY_XID);


                if (null != keyId) {

                    log.info("RootContext.KEY_XID is {}", keyId);
                    // 绑定 XID，自动创建分支事物
                    RootContext.bind(keyId);
                }


                return true;
            }

            @Override
            public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

//                SessionHolder.clearData();
//                LOG.debug("postHandle");
            }

            //请求处理完成的回调方法
            @Override
            public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
//                SessionHolder.clearData();
//                LOG.debug("afterCompletion");
            }
        });
        // 配置拦截路径
        registration.addPathPatterns("/**");
        // 配置不进行拦截的路径
        registration.excludePathPatterns("/static/**");
    }

}
