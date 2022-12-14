package com.crazymaker.springcloud.user.info.remote.fallback;

import com.crazymaker.springcloud.common.dto.UserDTO;
import com.crazymaker.springcloud.common.result.RestOut;
import com.crazymaker.springcloud.user.info.remote.client.UserClient;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 *  Feign 客户端接口的 回退处理工厂类
 */

@Slf4j
@Component
public class UserClientFallbackFactory implements FallbackFactory<UserClient>
{
    /**
     *  创建 UserClient 客户端的回退处理实例
     */
    @Override
    public UserClient create(final Throwable cause) {
        log.error("RPC 异常了，回退!",cause);
        /**
         * 创建一个  UserClient 客户端接口的匿名回退实例
         */
        return new UserClient() {
            /**
             *  方法: 获取用户信息 RPC 失败后的回退方法
             */
            @Override
            public RestOut<UserDTO> detail(Long userId)
            {
                return RestOut.error("FallbackFactory fallback：user detail rest 服务调用失败" );
            }

            @Override
            public String hello(String name)
            {
                return "FailBack：user hello  api 调用失败";
            }
        };
    }
}