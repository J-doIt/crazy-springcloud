package com.crazymaker.cloud.seata.seckill.feign;

import com.crazymaker.springcloud.common.result.RestOut;
import com.crazymaker.springcloud.seckill.api.dto.SeckillDTO;
import com.crazymaker.springcloud.seckill.api.dto.SeckillOrderDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * stock feign 客户端
 */

@FeignClient(name = "seata-order-demo", path = "/seata-order-demo/api/seckill/order/")
public interface SeataDemoOrderFeignClient {

    @RequestMapping(value = "/addOrder/v1", method = RequestMethod.POST)
    RestOut<SeckillOrderDTO> addOrder(@RequestBody SeckillDTO dto);
}