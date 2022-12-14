package com.crazymaker.springcloud.base.security.provider;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.crazymaker.springcloud.base.security.token.JwtAuthenticationToken;
import com.crazymaker.springcloud.common.constants.SessionConstants;
import com.crazymaker.springcloud.common.dto.UserDTO;
import com.crazymaker.springcloud.common.util.JsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.www.NonceExpiredException;
import org.springframework.session.Session;
import org.springframework.session.data.redis.RedisOperationsSessionRepository;

import java.util.Calendar;

import static com.crazymaker.springcloud.common.context.SessionHolder.G_USER;

public class JwtAuthenticationProvider implements AuthenticationProvider {

    private RedisOperationsSessionRepository sessionRepository;

    public JwtAuthenticationProvider(RedisOperationsSessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        //判断JWT令牌是否过期
        DecodedJWT jwt = ((JwtAuthenticationToken) authentication).getDecodedJWT();
        if (jwt.getExpiresAt().before(Calendar.getInstance().getTime())) {
            throw new NonceExpiredException("认证过期");
        }

        //取得 session id
        String sid = jwt.getSubject();

        //取得令牌字符串，用于验证是否重复登录
        String presentToken = jwt.getToken();

        //获取 session
        Session session = null;

        try {
            session = sessionRepository.findById(sid);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (null == session) {
            throw new NonceExpiredException("还没有登录,请登录系统！");
        }

        String json = session.getAttribute(G_USER);
        if (StringUtils.isBlank(json)) {
            throw new NonceExpiredException("认证有误,请重新登录");
        }

        //取得session 中的用户信息
        UserDTO userDTO = JsonUtil.jsonToPojo(json, UserDTO.class);
        if (null == userDTO) {
            throw new NonceExpiredException("认证有误,请重新登录");
        }

        //判断是否在其他地方已经登录
        if (null == presentToken || !presentToken.equals(userDTO.getToken())) {
            throw new NonceExpiredException("您已经在其他的地方登陆!");
        }

        String userID = String.valueOf(userDTO.getUserId());


        try {
            //用户密码的密文作为 JWT 的加密盐
            String encryptSalt = userDTO.getPassword();
            Algorithm algorithm = Algorithm.HMAC256(encryptSalt);
            //创建验证器
            JWTVerifier verifier = JWT.require(algorithm)
                    .withSubject(sid)
                    .build();
            //进行JWT token进行验证
            verifier.verify(presentToken);
        } catch (Exception e) {
            throw new BadCredentialsException("认证有误：令牌校验失败,请重新登录", e);
        }

        UserDetails userDetails = User.builder()
                .username(userID)
                .password(userDTO.getPassword())
                .authorities(SessionConstants.USER_INFO)
                .build();


        //返回认证通过的token，包含用户的 id 等信息
//        JwtAuthenticationToken passedToken =
//                new JwtAuthenticationToken(userDetails, jwt, userDetails.getAuthorities());
//        passedToken.setAuthenticated(true);
        //    passedToken.setAuthenticated(true);

        JwtAuthenticationToken passedToken = (JwtAuthenticationToken) authentication;
        passedToken.setAuthenticated(true);
        passedToken.setDetails(userDetails);
        return passedToken;
    }


    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.isAssignableFrom(JwtAuthenticationToken.class);
    }

}
