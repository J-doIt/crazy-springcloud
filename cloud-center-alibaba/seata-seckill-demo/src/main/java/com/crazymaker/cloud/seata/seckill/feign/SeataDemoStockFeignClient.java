package com.crazymaker.cloud.seata.seckill.feign;

import com.crazymaker.springcloud.common.result.RestOut;
import com.crazymaker.springcloud.seckill.api.dto.SeckillDTO;
import com.crazymaker.springcloud.seckill.api.dto.SeckillSkuDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * stock feign 客户端
 */

@FeignClient(name = "seata-stock-demo", path = "/seata-stock-demo/api/seckill/sku/")
public interface SeataDemoStockFeignClient {
    /**
     * minusStock 秒杀库存
     *
     * @param dto 商品与库存
     * @return 商品 skuDTO
     */
    @RequestMapping(value = "/minusStock/v1", method = RequestMethod.POST)
    RestOut<SeckillSkuDTO> minusStock(@RequestBody SeckillDTO dto);


}
