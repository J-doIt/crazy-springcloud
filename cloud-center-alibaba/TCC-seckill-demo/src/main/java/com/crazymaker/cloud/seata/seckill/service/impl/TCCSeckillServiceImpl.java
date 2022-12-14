package com.crazymaker.cloud.seata.seckill.service.impl;

import com.crazymaker.cloud.seata.seckill.feign.OrderApi;
import com.crazymaker.cloud.seata.seckill.feign.StockApi;
import com.crazymaker.springcloud.seckill.api.dto.SeckillDTO;
import io.seata.core.context.RootContext;
import io.seata.rm.tcc.api.BusinessActionContext;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Slf4j
@Service
public class TCCSeckillServiceImpl {

    @Autowired
    private OrderApi orderApi;
    @Autowired
    private StockApi stockApi;

    /**
     * 减库存，下订单
     */
    //开启全局事务（重点） 使用 seata 的全局事务
    @GlobalTransactional
    public boolean doSeckill(@RequestBody SeckillDTO dto) {

        String xid = RootContext.getXID();
        log.info("------->分布式操作开始");
        BusinessActionContext actionContext = new BusinessActionContext();
        actionContext.setXid(xid);
        Long skuId = dto.getSeckillSkuId();
        Long uId = dto.getUserId();

        //远程方法 扣减库存
        log.info("------->扣减库存开始storage中");
        boolean result = stockApi.prepare(actionContext, skuId, uId);
        if (!result) {
            throw new RuntimeException("扣减库存失败");
        }
        result = orderApi.prepare(actionContext, skuId, uId);

        if (!result) {
            throw new RuntimeException("保存订单失败");
        }

        log.info("------->分布式下订单操作完成");
//        throw new RuntimeException("调用2阶段提交的rollback方法");
        return true;
    }
}
