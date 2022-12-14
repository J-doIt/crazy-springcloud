package com.crazymaker.springcloud.seckill.controller;

import com.crazymaker.springcloud.common.result.RestOut;
import com.crazymaker.springcloud.seckill.api.dto.SeckillOrderDTO;
import com.crazymaker.springcloud.seckill.service.impl.SeckillServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;


@RestController
@RequestMapping("/api/seckill/zk/")
@Api(tags = "秒杀练习 ZkLock 版本")
public class SeckillByZkLockController {
    @Resource
    SeckillServiceImpl seckillService;

    /**
     * 执行秒杀的操作
     *
     * @param skuId 商品id
     * @param money  钱
     * @param userId 用户id
     * @param md5    校验码
     * @return
     */
    @ApiOperation(value = "秒杀")
    @PostMapping("/do/v1")
    RestOut<SeckillOrderDTO> executeSeckill(
            @RequestParam(value = "skuId", required = true) long skuId,
            @RequestParam(value = "money", required = true) BigDecimal money,
            @RequestParam(value = "id", required = true) long userId,
            @RequestParam(value = "md5", required = true) String md5) {
        SeckillOrderDTO dto = seckillService.executeSeckillV1(skuId, money, userId, md5);
        return RestOut.success(dto).setRespMsg("秒杀成功");

    }


}
