package com.crazymaker.springcloud.seckill.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class SeckillSkuDTO
{

    //商品ID
    private Long id;

    //商品标题
    private String title;

    //商品标题
    private String image;

    //商品原价格
    private BigDecimal price;

    //商品秒杀价格
    private BigDecimal costPrice;

    //创建时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss" )
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8" )
    private Date createTime;

    //秒杀开始时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss" )
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8" )
    private Date startTime;

    //秒杀结束时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss" )
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8" )
    private Date endTime;


    //剩余库存数量
    private long stockCount;


    //是否开启秒杀
    private boolean exposed = false;

    //加密措施，避免用户通过抓包拿到秒杀地址
    private String md5;


}
