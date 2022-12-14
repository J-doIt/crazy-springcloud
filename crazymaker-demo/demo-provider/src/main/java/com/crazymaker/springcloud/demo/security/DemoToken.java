package com.crazymaker.springcloud.demo.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.util.Collections;


public class DemoToken extends AbstractAuthenticationToken
{
    private static final long serialVersionUID = 1981518947978158945L;

    private  String userName;
    private  String password;

    public DemoToken(String userName, String password)
    {
        super(Collections.emptyList());
        this.userName = userName;
        this.password = password;
    }

    /**
     * The credentials that prove the principal is correct.
     * This is usually a password,
     * but could be anything relevant to the <code>AuthenticationManager</code>. Callers
     * are expected to populate the credentials.
     *
     * @return the credentials that prove the identity of the <code>Principal</code>
     */
    @Override
    public Object getCredentials()
    {
        return password;
    }

    /**
     * The identity of the principal being authenticated.
     * In the case of an authentication
     * request with username and password, this would be the username. Callers are
     * expected to populate the principal for an authentication request.
     * <p>
     * The <tt>AuthenticationManager</tt> implementation will often return an
     * <tt>Authentication</tt> containing richer information as the principal for use by
     * the application. Many of the authentication providers will create a
     * {@code UserDetails} object as the principal.
     *
     * @return the <code>Principal</code> being authenticated or the authenticated
     * principal after authentication.
     */
    @Override
    public Object getPrincipal()
    {
        return userName;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }
}
