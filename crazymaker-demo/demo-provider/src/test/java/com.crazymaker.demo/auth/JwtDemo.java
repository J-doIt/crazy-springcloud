package com.crazymaker.demo.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.crazymaker.springcloud.common.constants.SessionConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.junit.Test;

import java.util.Date;

@Slf4j
public class JwtDemo
{

    @Test
    public void testBaseJWT()
    {
        try
        {
            /**
             * JWT 的演示内容
             */
            String subject = "session id";
            /**
             * 签名的加密盐
             */
            String salt = "user password";

            /**
             * 签名的加密算法
             */
            Algorithm algorithm = Algorithm.HMAC256(salt);

            //签发时间. 注意：尽量比当前时间稍微提前一点，防止验证时间相隔太短，导致验证不通过
            long start = System.currentTimeMillis() - 60000;
            //过期时间. 在签发时间的基础上，加上一个有效时长
            Date end = new Date(start + SessionConstants.SESSION_TIME_OUT * 1000);  //设置过期
            /**
             * 获取编码后的 JWT 令牌
             */
            String token = JWT.create()
                    .withSubject(subject)
                    .withIssuedAt(new Date(start))
                    .withExpiresAt(end)
                    .sign(algorithm);

            log.info("token=" + token);
            // 编码后输出demo为：
            // token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzZXNzaW9uIGlkIiwiZXhwIjoxNTc5MzE1NzE3LCJpYXQiOjE1Nzg0NTE3MTd9.iANh9Fa0B_6H5TQ11bLCWcEpmWxuCwa2Rt6rnzBWteI

            //以·点分割 编码后的令牌
            String[] parts = token.split("\\.");

            /**
             * 对第一部分和第二部分进行解码
             * 解码后的第一部分：header 头部
             */
            String headerJson;
            headerJson = StringUtils.newStringUtf8(Base64.decodeBase64(parts[0]));
            log.info("parts[0]=" + headerJson);
            // 解码后的第一部分输出的示例为：
            // parts[0]={"typ":"JWT","alg":"HS256"}

            /**
             * 解码后的第二部分：payload 负载
             */
            String payloadJson;
            payloadJson = StringUtils.newStringUtf8(Base64.decodeBase64(parts[1]));
            log.info("parts[1]=" + payloadJson);
            //输出的示例为：
            //解码后第二部分 parts[1]={"sub":"session id","exp":1579315535,"iat":1578451535}


        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 测试私有声明
     */
    @Test
    public void testJWTWithClaim()
    {
        try
        {
            String subject = "session id";
            String salt = "user password";
            /**
             * 签名的加密算法
             */
            Algorithm algorithm = Algorithm.HMAC256(salt);
            //签发时间. 注意：尽量比当前时间稍微提前一点，防止验证时间相隔太短，导致验证不通过
            long start = System.currentTimeMillis() - 60000;
            //过期时间. 在签发时间的基础上，加上一个有效时长
            Date end = new Date(start + SessionConstants.SESSION_TIME_OUT * 1000);  //设置过期

            /**
             * JWT 建造者
             */
            JWTCreator.Builder builder = JWT.create();
            /**
             * 增加私有声明
             */
            builder.withClaim("uid", "123...");
            builder.withClaim("user_name", "admin");
            builder.withClaim("nick_name", "管理员");
            /**
             * 获取编码后的 JWT 令牌
             */
            String token = builder
                    .withSubject(subject)
                    .withIssuedAt(new Date(start))
                    .withExpiresAt(end)
                    .withClaim("attribute1","value1")
                    .withClaim("attribute2","value2")
                    .sign(algorithm);
            log.info("token=" + token);

            //以·点分割. 这里需要转义
            String[] parts = token.split("\\.");

            String payloadJson;

            /**
             *  解码 payload
             */
            payloadJson = StringUtils.newStringUtf8(Base64.decodeBase64(parts[1]));
            log.info("parts[1]=" + payloadJson);
            //输出demo为：parts[1]=
            // {"uid":"123...","sub":"session id","user_name":"admin","nick_name":"管理员","exp":1579317358,"iat":1578453358}

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
