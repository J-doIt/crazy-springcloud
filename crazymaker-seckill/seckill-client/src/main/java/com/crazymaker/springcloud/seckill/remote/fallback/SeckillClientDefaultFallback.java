package com.crazymaker.springcloud.seckill.remote.fallback;


import com.crazymaker.springcloud.common.page.PageOut;
import com.crazymaker.springcloud.common.page.PageReq;
import com.crazymaker.springcloud.common.result.RestOut;
import com.crazymaker.springcloud.seckill.api.dto.SeckillOrderDTO;
import com.crazymaker.springcloud.seckill.remote.client.SeckillOrderClient;
import org.springframework.stereotype.Component;

@Component
public class SeckillClientDefaultFallback implements SeckillOrderClient
{


    /**
     * 查询用户订单信息
     *
     * @param userId 用户id
     * @return 商品 dto
     */
    public RestOut<PageOut<SeckillOrderDTO>> userOrders(Long userId, PageReq pageReq)
    {

        return RestOut.error("远程调用失败,返回熔断后的调用结果" );
    }


}
