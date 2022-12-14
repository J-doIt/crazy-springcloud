package com.crazymaker.springcloud.user.main;

import lombok.extern.java.Log;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Log
public class PasswordTest
{

    public static void main(String[] args)
    {
        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        String encode = passwordEncoder.encode("123456" );
        log.info("加密后的密码:" + encode);
        log.info("bcrypt密码对比:" + passwordEncoder.matches("password", encode));
        String md5Password = "{MD5}88e2d8cd1e92fd5544c8621508cd706b";
        //MD5加密前的密码为:password
        log.info("MD5密码对比:" + passwordEncoder.matches("password", encode));
    }
}
