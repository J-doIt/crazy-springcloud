package com.crazymaker.springcloud.seckill.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单
 * 说明： 秒杀商品表和主商品表不同
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SeckillOrderDTO implements Serializable
{

    //订单ID
    private Long id;


    //支付金额
    private BigDecimal money;


    //秒杀用户的用户ID
    private Long userId;

    //创建时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss" )
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8" )
    private Date createTime;


    //支付时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss" )
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8" )
    private Date payTime;


    //秒杀商品，和订单是一对多的关系
    private Long skuId;

    //订单状态， -1:无效 0:成功 1:已付款
    private Short status;

}
