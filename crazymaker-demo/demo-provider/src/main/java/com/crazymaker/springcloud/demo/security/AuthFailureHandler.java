package com.crazymaker.springcloud.demo.security;

import com.crazymaker.springcloud.common.result.RestOut;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        response.setHeader("Content-Type", "text/html;charset=utf-8");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());

        ObjectMapper mapper = new ObjectMapper();

        //Result对象转Json,
        String jsonValue = mapper.writeValueAsString(RestOut.error(exception.getMessage()));
        response.getWriter().write(jsonValue);

    }

}
