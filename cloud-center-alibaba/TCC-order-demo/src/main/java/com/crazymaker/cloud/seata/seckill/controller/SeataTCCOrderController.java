package com.crazymaker.cloud.seata.seckill.controller;

import com.crazymaker.cloud.seata.seckill.service.impl.TCCOrderServiceImpl;
import io.seata.rm.tcc.api.BusinessActionContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


@RestController
@RequestMapping("/api/tcc/order/")
@Api(tags = "秒杀练习 订单管理")
public class SeataTCCOrderController {
    @Resource
    TCCOrderServiceImpl seckillOrderService;


    /**
     * 执行秒杀的操作
     * <p>
     * <p>
     * {
     * "exposedKey": "4b70903f6e1aa87788d3ea962f8b2f0e",
     * "newStockNum": 10000,
     * "seckillSkuId": 1157197244718385152,
     * "seckillToken": "0f8459cbae1748c7b14e4cea3d991000",
     * "userId": 37
     * }
     *
     * @return
     */
    @ApiOperation(value = "下订单")
    @PostMapping("/addOrder/v1")
    boolean addOrder(@RequestBody BusinessActionContext actionContext, @RequestParam("sku_id") Long skuId, @RequestParam("uid") Long uId) {

        boolean orderDTO = seckillOrderService.addOrder(actionContext, skuId,uId);
        return orderDTO;
    }

    @ApiOperation(value = "下订单提交")
    @PostMapping("/commit/v1")
    boolean commit(@RequestBody BusinessActionContext actionContext) {
        boolean orderDTO = seckillOrderService.commitAddOrder(actionContext);
        return orderDTO;
    }

    @ApiOperation(value = "下订单回滚")
    @PostMapping("/rollback/v1")
    boolean rollback(@RequestBody BusinessActionContext actionContext) {
        boolean orderDTO = seckillOrderService.rollbackAddOrder(actionContext);
        return orderDTO;
    }


}
