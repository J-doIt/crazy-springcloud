//package com.crazymaker.springcloud.base.security.handler;
//
//import com.crazymaker.springcloud.common.constants.SessionConstants;
//import com.crazymaker.springcloud.common.result.RestOut;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
//
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
//
//public class JsonLoginSuccessHandler implements AuthenticationSuccessHandler
//{
//
//    private CustomUserAuthServiceImpl userAuthService;
//
//    public JsonLoginSuccessHandler(CustomUserAuthServiceImpl userAuthService)
//    {
//        this.userAuthService = userAuthService;
//    }
//
//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
//                                        Authentication authentication) throws IOException, ServletException
//    {
//        String token = userAuthService.saveUserLoginInfo((UserDetails) authentication.getPrincipal());
//        response.setHeader("Content-Type", "text/html;charset=utf-8" );
//        response.setHeader(SessionConstants.AUTHORIZATION_HEAD, token);
//
//        ObjectMapper mapper = new ObjectMapper();
//
//        //Result对象转Json,
//        String jsonValue = mapper.writeValueAsString(RestOut.success("登录成功" ));
//        response.getWriter().write(jsonValue);
//    }
//
//}
