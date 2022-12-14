package com.crazymaker.springcloud.base.security.handler;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class JwtRefreshSuccessHandler implements AuthenticationSuccessHandler {

    private static final int tokenRefreshInterval = 300;  //刷新间隔5分钟


    public JwtRefreshSuccessHandler() {
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
//        DecodedJWT auth = ((JwtAuthenticationToken) authentication).getDecodedJWT();
//        boolean shouldRefresh = shouldTokenRefresh(auth.getIssuedAt());
//        if (shouldRefresh) {
//            String newToken = gempUserService.saveUserLoginInfo((UserDetails) authentication.getPrincipal());
//            response.setHeader(SessionConstants.AUTHORIZATION_HEAD, newToken);
//        }
    }

    protected boolean shouldTokenRefresh(Date issueAt) {
        LocalDateTime issueTime = LocalDateTime.ofInstant(issueAt.toInstant(), ZoneId.systemDefault());
        return LocalDateTime.now().minusSeconds(tokenRefreshInterval).isAfter(issueTime);
    }

}
