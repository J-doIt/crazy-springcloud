package com.crazymaker.springcloud.user.info.remote.client;

import com.crazymaker.springcloud.common.dto.UserDTO;
import com.crazymaker.springcloud.common.result.RestOut;
import com.crazymaker.springcloud.standard.config.FeignConfiguration;
import com.crazymaker.springcloud.user.info.remote.fallback.UserClientFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Feign 客户端接口
 * @description: 用户信息 远程调用接口
 * @date 2019年7月22日
 */

@FeignClient(value = "uaa-provider",
        configuration = FeignConfiguration.class,
//        fallback = UserClientFallback.class,
        fallbackFactory = UserClientFallbackFactory.class,
        path = "/uaa-provider/api/user")
public interface UserClient
{
      /**
     * 远程调用 RPC 方法：获取用户详细信息
     * @param userId 用户 Id
     * @return 用户详细信息
     */
    @RequestMapping(value = "/detail/v1", method = RequestMethod.GET)
    RestOut<UserDTO> detail(@RequestParam(value = "userId") Long userId);



    @RequestMapping(value = "/hello/v1", method = RequestMethod.GET)
    public String hello(@RequestParam(value = "name") String name);


}
