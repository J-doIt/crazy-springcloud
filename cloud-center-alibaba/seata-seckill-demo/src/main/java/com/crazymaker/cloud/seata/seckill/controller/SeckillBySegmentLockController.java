package com.crazymaker.cloud.seata.seckill.controller;

import com.crazymaker.cloud.seata.seckill.service.impl.SeataSeckillServiceImpl;
import com.crazymaker.springcloud.common.result.RestOut;
import com.crazymaker.springcloud.seckill.api.dto.SeckillDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


@RestController
@RequestMapping("/api/seckill/seglock/")
@Api(tags = "秒杀练习分布式事务 版本")
public class SeckillBySegmentLockController {


    @Resource
    SeataSeckillServiceImpl seataSeckillServiceImpl;


    /**
     * 执行秒杀的操作
     * 减库存，下订单
     * <p>
     * {
     * "exposedKey": "4b70903f6e1aa87788d3ea962f8b2f0e",
     * "newStockNum": 10000,
     * "seckillSkuId": 1247695238068177920,
     * "seckillToken": "0f8459cbae1748c7b14e4cea3d991000",
     * "userId": 37
     * }
     *
     * @return
     */
    @ApiOperation(value = "秒杀")
    @PostMapping("/doSeckill/v1")
    RestOut<SeckillDTO> doSeckill(@RequestBody SeckillDTO dto) {

        seataSeckillServiceImpl.doSeckill(dto);

        return RestOut.success(dto).setRespMsg("秒杀成功");
    }


}
