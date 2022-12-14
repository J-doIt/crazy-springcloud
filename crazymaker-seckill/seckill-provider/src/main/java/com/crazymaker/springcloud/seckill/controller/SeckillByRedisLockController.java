package com.crazymaker.springcloud.seckill.controller;

import com.crazymaker.springcloud.common.constants.SessionConstants;
import com.crazymaker.springcloud.common.exception.BusinessException;
import com.crazymaker.springcloud.common.result.RestOut;
import com.crazymaker.springcloud.seckill.api.dto.SeckillDTO;
import com.crazymaker.springcloud.seckill.api.dto.SeckillOrderDTO;
import com.crazymaker.springcloud.seckill.service.impl.RedisSeckillServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;


@RestController
@RequestMapping("/api/seckill/redis/")
@Api(tags = "秒杀练习  RedisLock 版本")
public class SeckillByRedisLockController {
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
     *
     * @return
     */
    @ApiOperation(value = "秒杀")
    @PostMapping("/do/v1")
    RestOut<SeckillOrderDTO> executeSeckill(@RequestBody SeckillDTO dto) {
        SeckillOrderDTO orderDTO = redisSeckillServiceImpl.executeSeckill(dto);
        return RestOut.success(orderDTO).setRespMsg("秒杀成功");
    }


}
