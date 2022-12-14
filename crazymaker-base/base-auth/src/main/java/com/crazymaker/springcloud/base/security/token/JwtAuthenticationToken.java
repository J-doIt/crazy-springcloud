package com.crazymaker.springcloud.base.security.token;

import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import static com.crazymaker.springcloud.common.constants.SessionConstants.DEFAULT_SCOPE;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {
    private static final long serialVersionUID = 3981518947978158945L;

    //用户信息: 用户id 、密码
    private UserDetails userDetails;
    // 封装的 JWT 认证信息
    private DecodedJWT decodedJWT;

    public JwtAuthenticationToken(DecodedJWT jwt) {
        super(Collections.emptyList());
        this.decodedJWT = jwt;
    }

    public JwtAuthenticationToken(UserDetails userDetails,
                                  DecodedJWT jwt,
                                  Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.userDetails = userDetails;
        this.decodedJWT = jwt;
    }

    @Override
    public void setDetails(Object details) {
        super.setDetails(details);
        this.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return userDetails;
    }

    @Override
    public Object getPrincipal() {
        return decodedJWT.getSubject();
    }

    public DecodedJWT getDecodedJWT() {
        return decodedJWT;
    }


    public OAuth2AccessToken createAccessToken() {
        DefaultOAuth2AccessToken auth2Token = new DefaultOAuth2AccessToken(decodedJWT.getToken());

        long validitySeconds = decodedJWT.getExpiresAt().getTime() - System.currentTimeMillis();
        auth2Token.setExpiration(new Date(System.currentTimeMillis() + validitySeconds));


        auth2Token.setRefreshToken(null);
        auth2Token.setScope(Collections.singleton(DEFAULT_SCOPE));
        return auth2Token;
    }
}
