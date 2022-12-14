package com.crazymaker.springcloud.seckill.main;

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
        log.info("bcrypt密码对比:" + passwordEncoder.matches("123456", encode));
        String md5Password = "{MD5}88e2d8cd1e92fd5544c8621508cd706b";
        //MD5加密前的密码为:password
        log.info("MD5密码对比:" + passwordEncoder.matches("password", encode));
        log.info("2 MD5密码对比:" + passwordEncoder.matches("123456", "{bcrypt}$2a$10$tcShLjUU6bToviu5cxr/tu/BgQQtA6ycKfLPirtuR7pIgqJepPxu2" ));
    }
}
