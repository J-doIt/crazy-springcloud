package com.crazymaker.cloud.nacos.demo.consumer.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @description:远程服务的本地声明式接口
 */

@FeignClient(
        value = "service-provider-demo", path = "/provider"
)
public interface EchoClient {
    /**
     * 远程调用接口的方法:
     * 调用   demo-provider 的  REST 接口  api/demo/echo/{0}/v1
     * REST 接口 功能： 回显输入的信息
     *
     * @return echo 回显消息 JSON 响应实例
     */
    @RequestMapping(value = "/echo/{word}",
            method = RequestMethod.GET)
    String echo(@PathVariable(value = "word") String word);

}
