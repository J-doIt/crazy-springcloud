package com.crazymaker.cloud.seata.seckill.controller;

import com.crazymaker.cloud.seata.seckill.impl.SeataStockServiceImpl;
import com.crazymaker.springcloud.common.page.PageOut;
import com.crazymaker.springcloud.common.page.PageReq;
import com.crazymaker.springcloud.common.result.RestOut;
import com.crazymaker.springcloud.seckill.api.dto.SeckillSkuDTO;
import io.seata.rm.tcc.api.BusinessActionContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("/api/tcc/sku/")
@Api(tags = "商品库存")
public class SeataTCCStockController {
    @Resource
    SeataStockServiceImpl seckillSkuStockService;




    /**
     * minusStock 秒杀库存
     *
     * @return 商品 skuDTO
     */
    @PostMapping("/minusStock/v1")
    @ApiOperation(value = "减少秒杀库存")
    boolean minusStock(@RequestBody BusinessActionContext actionContext,@RequestParam("sku_id") Long skuId, @RequestParam("uid") Long uId) {

        boolean result = seckillSkuStockService.minusStock(actionContext, skuId,uId);
        return result;
    }


    @ApiOperation(value = "提交")
    @PostMapping("/commit/v1")
    boolean commit(@RequestBody BusinessActionContext actionContext) {
        boolean result = seckillSkuStockService.commit(actionContext);
        return result;
    }

    @ApiOperation(value = "回滚")
    @PostMapping("/rollback/v1")
    boolean rollback(@RequestBody BusinessActionContext actionContext) {
        boolean result = seckillSkuStockService.rollback(actionContext);
        return result;
    }


}
