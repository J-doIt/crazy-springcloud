package com.crazymaker.springcloud.seckill.api.dto;

import com.crazymaker.springcloud.common.result.RestOut;
import com.crazymaker.springcloud.common.util.JsonUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 秒杀 dto
 * 说明： 秒杀商品表和主商品表不同
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SeckillDTO implements Serializable
{
    //秒杀用户的用户ID
    @ApiModelProperty(value = "用户ID",example = "1")
    private Long userId;


    //秒杀商品，和订单是一对多的关系
    @ApiModelProperty(value = "秒杀商品ID",example = "1224036923438268416")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long seckillSkuId;

    //秒杀令牌（排队码）
    @ApiModelProperty(value = "秒杀令牌（排队码）",example = "\"1156308907673518080\"")
    private String seckillToken;

    //暴露地址
    @ApiModelProperty(value = "暴露地址",example = "\"1156308907673518080\"")
    private String exposedKey;

    //秒杀库存
    // 使用场景：（修改库存时用到）
    @ApiModelProperty(value = "秒杀库存",example = "10000")
    @JsonFormat(shape=JsonFormat.Shape.STRING)
    private Integer newStockNum;

    public static SeckillSkuDTO fromJson(String skuCache) {
        return JsonUtil.jsonToPojoFast(skuCache,SeckillSkuDTO.class);
    }

    public static String toJson(SeckillSkuDTO skuDTO) {
        return JsonUtil.pojoToJsonFast(skuDTO);
    }
}
