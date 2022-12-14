package com.crazymaker.springcloud.seckill.controller;

import com.crazymaker.springcloud.common.constants.SessionConstants;
import com.crazymaker.springcloud.common.exception.BusinessException;
import com.crazymaker.springcloud.common.result.RestOut;
import com.crazymaker.springcloud.seckill.api.dto.SeckillDTO;
import com.crazymaker.springcloud.seckill.api.dto.SeckillOrderDTO;
import com.crazymaker.springcloud.seckill.dao.po.SeckillOrderPO;
import com.crazymaker.springcloud.seckill.service.impl.RedisSeckillServiceImpl;
import com.crazymaker.springcloud.seckill.service.impl.SeckillOrderServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;


@RestController
@RequestMapping("/api/seckill/seglock/")
@Api(tags = "秒杀练习  分段锁 版本")
public class SeckillBySegmentLockController {
    /**
     * 秒杀服务实现 Bean
     */
    @Resource
    RedisSeckillServiceImpl redisSeckillServiceImpl;


    /**
     * 获取秒杀的令牌
     */
    @ApiOperation(value = "排队获取秒杀令牌")
    @RequestMapping(value = "/token/{exposedKey}/v1", method = RequestMethod.GET)
    RestOut<String> getSeckillToken(
            @PathVariable String exposedKey, HttpServletRequest request) {
        String userIdentifier = request.getHeader(SessionConstants.USER_IDENTIFIER);
        if (null == userIdentifier) {
            throw BusinessException.builder().errMsg("用户id不能为空").build();
        }

        String result = redisSeckillServiceImpl.getSeckillToken(
                exposedKey,
                userIdentifier);
        return RestOut.success(result).setRespMsg("这是获取的结果");

    }

    /**
     * 执行秒杀的操作


     {
     "exposedKey": "4b70903f6e1aa87788d3ea962f8b2f0e",
     "newStockNum": 10000,
     "seckillSkuId": 1157197244718385152,
     "seckillToken": "0f8459cbae1748c7b14e4cea3d991000",
     "userId": 37
     }

     * @return
     */
    @ApiOperation(value = "秒杀")
    @PostMapping("/doSeckill/v1")
    RestOut<SeckillOrderDTO> executeSeckill(@RequestBody SeckillDTO dto) {
        SeckillOrderDTO orderDTO = redisSeckillServiceImpl.executeSeckill(dto);
        return RestOut.success(orderDTO).setRespMsg("秒杀成功");
    }

    @Resource
    SeckillOrderServiceImpl seckillOrderService;


    /**
     * 获取秒杀的结果

     {
     "exposedKey": "4b70903f6e1aa87788d3ea962f8b2f0e",
     "newStockNum": 10000,
     "seckillSkuId": 1157197244718385152,
     "seckillToken": "8ef40b31-0f9f-497e-9e3e-440b0c0319ef",
     "userId": 13
     }


     * @return
     */
    @ApiOperation(value = "获取秒杀的结果")
    @PostMapping("/getSeckillResult/v1")
    RestOut<SeckillOrderDTO> getSeckillResult(@RequestBody SeckillDTO dto) {
        Long userId = dto.getUserId();
        Long skuId = dto.getSeckillSkuId();
        List<SeckillOrderPO> pos = seckillOrderService.findOrderByUserIDAndSkuId(userId, skuId);
        if (null != pos && pos.size() > 0) {
            SeckillOrderDTO orderDTO = new SeckillOrderDTO();
            BeanUtils.copyProperties(pos.get(0), orderDTO);
            return RestOut.success(orderDTO).setRespMsg("查询成功");
        }
        return RestOut.error("查询失败");
    }


}
