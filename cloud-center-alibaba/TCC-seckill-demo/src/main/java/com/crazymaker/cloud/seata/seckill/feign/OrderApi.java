package com.crazymaker.cloud.seata.seckill.feign;

import io.seata.rm.tcc.api.BusinessActionContext;
import io.seata.rm.tcc.api.LocalTCC;
import io.seata.rm.tcc.api.TwoPhaseBusinessAction;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * stock feign 客户端
 */

@FeignClient(name = "tcc-order-demo", path = "/tcc-order-demo/api/tcc/order/")
@LocalTCC
public interface OrderApi {

    @TwoPhaseBusinessAction(name = "orderApi", commitMethod = "commit", rollbackMethod = "rollback")
    @RequestMapping(value = "/addOrder/v1", method = RequestMethod.POST)
    boolean prepare(@RequestBody BusinessActionContext actionContext, @RequestParam("sku_id") Long skuId, @RequestParam("uid") Long uId);

    @RequestMapping(value = "/commit/v1", method = RequestMethod.POST)
    boolean commit(@RequestBody BusinessActionContext actionContext);

    @RequestMapping(value = "/rollback/v1", method = RequestMethod.POST)
    boolean rollback(@RequestBody BusinessActionContext actionContext);
}