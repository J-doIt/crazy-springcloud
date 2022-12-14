package com.crazymaker.cloud.seata.seckill.service.impl;

import com.crazymaker.cloud.seata.seckill.feign.SeataDemoOrderFeignClient;
import com.crazymaker.cloud.seata.seckill.feign.SeataDemoStockFeignClient;
import com.crazymaker.springcloud.seckill.api.dto.SeckillDTO;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Configuration
@Slf4j
@Service
public class SeataSeckillServiceImpl {

    @Autowired
    private SeataDemoOrderFeignClient stockFeignClient;
    @Autowired
    private SeataDemoStockFeignClient orderFeignClient;

    /**
     * TM
     * 减库存，下订单
     */
    @GlobalTransactional  //开启全局事务（重点） 使用 seata 的全局事务
    public void doSeckill(@RequestBody SeckillDTO dto) {

        orderFeignClient.minusStock(dto);
        stockFeignClient.addOrder(dto);
    }
}
