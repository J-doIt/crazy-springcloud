package com.crazymaker.springcloud.base.security.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.crazymaker.springcloud.common.constants.SessionConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.UnapprovedClientAuthenticationException;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 认证授权相关工具类
 */
@Slf4j
public class AuthUtils {
    private AuthUtils() {
        throw new IllegalStateException("Utility class");
    }

    private static final String BASIC_ = "Basic ";

    /**
     * 获取requet(head/param)中的token
     *
     * @param request
     * @return
     */
    public static String extractToken(HttpServletRequest request) {
        String token = extractHeaderToken(request);
        if (token == null) {
            token = request.getParameter(OAuth2AccessToken.ACCESS_TOKEN);
            if (token == null) {
                log.debug("Token not found in request parameters.  Not an OAuth2 request.");
            }
        }
        return token;
    }

    /**
     * 解析head中的token
     *
     * @param request
     * @return
     */
    private static String extractHeaderToken(HttpServletRequest request) {
        Enumeration<String> headers = request.getHeaders(SessionConstants.TOKEN_HEADER);
        while (headers.hasMoreElements()) {
            String value = headers.nextElement();
            if ((value.toLowerCase().startsWith(OAuth2AccessToken.BEARER_TYPE))) {
                String authHeaderValue = value.substring(OAuth2AccessToken.BEARER_TYPE.length()).trim();
                int commaIndex = authHeaderValue.indexOf(',');
                if (commaIndex > 0) {
                    authHeaderValue = authHeaderValue.substring(0, commaIndex);
                }
                return authHeaderValue;
            }
        }
        return null;
    }

    /**
     * *从header 请求中的clientId:clientSecret
     */
    public static String[] extractClient(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith(BASIC_)) {
            throw new UnapprovedClientAuthenticationException("请求头中client信息为空");
        }
        return extractHeaderClient(header);
    }

    /**
     * 从header 请求中的clientId:clientSecret
     *
     * @param header header中的参数
     */
    public static String[] extractHeaderClient(String header) {
        byte[] base64Client = header.substring(BASIC_.length()).getBytes(StandardCharsets.UTF_8);
        byte[] decoded = Base64.getDecoder().decode(base64Client);
        String clientStr = new String(decoded, StandardCharsets.UTF_8);
        String[] clientArr = clientStr.split(":");
        if (clientArr.length != 2) {
            throw new RuntimeException("Invalid basic authentication token");
        }
        return clientArr;
    }

    /**
     * 获取登陆的用户名
     */
    public static String getUsername(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        String username = null;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else if (principal instanceof String) {
            username = (String) principal;
        }
        return username;
    }


    /**
     * @param subject
     * @param salt    jwt header和jwt payload  加密用的盐
     * @return
     */
    public static String buildToken(String subject, String salt) {
        Algorithm algorithm = Algorithm.HMAC256(salt);
        //签发时间. 注意：尽量比当前时间稍微提前一点，防止验证时间相隔太短，导致验证不通过
        long start = System.currentTimeMillis() - 60000;
        //过期时间. 在签发时间的基础上，加上一个时长
        Date end = new Date(start + SessionConstants.SESSION_TIME_OUT * 1000);  //设置过期
        return JWT.create()
                .withSubject(subject)
                .withIssuedAt(new Date(start))
                .withExpiresAt(end)
                .sign(algorithm);

    }


    public static String buildToken(String subject, String salt, Map<String, String> claims) {
        Algorithm algorithm = Algorithm.HMAC256(salt);
        //签发时间. 注意：尽量比当前时间稍微提前一点，防止验证时间相隔太短，导致验证不通过
        long start = System.currentTimeMillis() - 60000;
        //过期时间. 在签发时间的基础上，加上一个时长
        Date end = new Date(start + SessionConstants.SESSION_TIME_OUT * 1000);  //设置过期


        JWTCreator.Builder builder = JWT.create();
        Iterator<Map.Entry<String, String>> it = claims.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> next = it.next();
            builder.withClaim(next.getKey(), next.getValue());

        }

        return builder.withSubject(subject)
                .withIssuedAt(new Date(start))
                .withExpiresAt(end)
                .sign(algorithm);


    }
}
