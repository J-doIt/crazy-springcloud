package com.crazymaker.springcloud.seckill.remote.client;

import com.crazymaker.springcloud.common.page.PageOut;
import com.crazymaker.springcloud.common.page.PageReq;
import com.crazymaker.springcloud.common.result.RestOut;
import com.crazymaker.springcloud.seckill.api.dto.SeckillOrderDTO;
import com.crazymaker.springcloud.seckill.remote.fallback.SeckillClientDefaultFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @description:对外部模块提供秒杀的远程调用
 * @date 2019年7月22日
 */


@FeignClient(value = "seckill-provider", path = "/api/order/", fallback = SeckillClientDefaultFallback.class)
public interface SeckillOrderClient
{
    /**
     * 查询用户订单信息
     *
     * @param userId 用户id
     * @return 商品 dto
     */
    @RequestMapping(value = "/user/{id}/list/v1", method = RequestMethod.POST)
    RestOut<PageOut<SeckillOrderDTO>> userOrders(
            @PathVariable(value = "id" ) Long userId, @RequestBody PageReq pageReq);


}
