package com.crazymaker.cloud.seata.seckill.controller;

import com.crazymaker.cloud.seata.seckill.impl.SeataStockServiceImpl;
import com.crazymaker.springcloud.common.page.PageOut;
import com.crazymaker.springcloud.common.page.PageReq;
import com.crazymaker.springcloud.common.result.RestOut;
import com.crazymaker.springcloud.seckill.api.dto.SeckillDTO;
import com.crazymaker.springcloud.seckill.api.dto.SeckillSkuDTO;
import io.seata.core.context.RootContext;
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
import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/api/seckill/sku/")
@Api(tags = "商品库存")
public class SeataSeckillStockController {
    @Resource
    SeataStockServiceImpl seckillSkuStockService;

    /**
     * 增加秒杀的商品
     *
     * @param stockCount 库存
     * @param title      标题
     * @param price      商品原价格
     * @param costPrice  价格
     * @return
     */
    @PostMapping("/add/v1")
    @ApiOperation(value = "增加秒杀商品")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "title", value = "商品名称", dataType = "string", paramType = "query", required = true, defaultValue = "秒杀商品-1"),
            @ApiImplicitParam(name = "stockCount", value = "秒杀数量", dataType = "int", paramType = "query", required = true, defaultValue = "10000", example = "10000"),
            @ApiImplicitParam(name = "price", value = "原始价格", dataType = "float", paramType = "query", required = true, defaultValue = "1000", example = "1000"),
            @ApiImplicitParam(name = "costPrice", value = "秒杀价格", dataType = "float", paramType = "query", required = true, defaultValue = "10", example = "1000")
    })
    RestOut<SeckillSkuDTO> addSeckill(
            @RequestParam(value = "title", required = true) String title,
            @RequestParam(value = "stockCount", required = true) int stockCount,
            @RequestParam(value = "price", required = true) float price,
            @RequestParam(value = "costPrice", required = true) float costPrice) {
        SeckillSkuDTO dto = seckillSkuStockService.addSeckillSku(title, stockCount, price, costPrice);
        return RestOut.success(dto).setRespMsg("增加秒杀的商品成功");

    }

    /**
     * 获取所有的秒杀商品列表
     *
     * @param pageReq 当前页 ，从1 开始,和 页的元素个数
     * @return
     */
    @PostMapping("/list/v1")
    @ApiOperation(value = "全部秒杀商品")
    RestOut<PageOut<SeckillSkuDTO>> findAll(@RequestBody PageReq pageReq) {
        PageOut<SeckillSkuDTO> page = seckillSkuStockService.findAll(pageReq);
        RestOut<PageOut<SeckillSkuDTO>> r = RestOut.success(page);
        return r;

    }

    /**
     * minusStock 秒杀库存
     *
     * @param dto 商品与库存
     * @return 商品 skuDTO
     */
    @PostMapping("/minusStock/v1")
    @ApiOperation(value = "减少秒杀库存")
    RestOut<SeckillSkuDTO> minusStock(@RequestBody SeckillDTO dto, HttpServletRequest request) {

        // 绑定 XID，自动创建分支事物
        // 异常后，整个调用链路回滚
        String keyId = request.getHeader(RootContext.KEY_XID);


        if (null != keyId) {

            log.info("RootContext.KEY_XID is {}", keyId);
            // 绑定 XID，自动创建分支事物
            RootContext.bind(keyId);
        }


        Long skuId = dto.getSeckillSkuId();

        SeckillSkuDTO skuDTO = seckillSkuStockService.minusStock(dto);

        if (null != skuDTO) {
            return RestOut.success(skuDTO).setRespMsg("减少秒杀库存成功");
        }
        return RestOut.error("未找到指定秒杀商品");
    }


}
