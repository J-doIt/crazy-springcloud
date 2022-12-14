package com.crazymaker.springcloud.user.info.remote.fallback;


import com.crazymaker.springcloud.common.dto.UserDTO;
import com.crazymaker.springcloud.common.result.RestOut;
import com.crazymaker.springcloud.user.info.remote.client.UserClient;
import org.springframework.stereotype.Component;

/**
 *  Feign 客户端接口的 fallback 回退处理类
 */
@Component
public class UserClientFallback implements UserClient
{
    @Override
    public RestOut<UserDTO> detail(Long id)
    {
        return RestOut.error("FailBack：user detail rest 服务调用失败" );
    }

    @Override
    public String hello(String name)
    {
        return "FailBack：user hello  api 调用失败";
    }
}
