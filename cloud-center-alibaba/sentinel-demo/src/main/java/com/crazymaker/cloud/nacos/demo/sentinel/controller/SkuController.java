package com.crazymaker.cloud.nacos.demo.sentinel.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/skus")
@Api(tags = "SkuController 演示")
@Slf4j
public class SkuController {

    /**
     * 模拟商品查询接口
     *
     * @param skuId
     * @return
     */
    @GetMapping("/detail")
    @SentinelResource(value = "querySkusInfo",
            blockHandler = "blockHandlerMethod",
            fallback = "querySkusInfoFallback")
    public String querySkusInfo(@RequestParam(value = "skuId", required = false) String skuId) {
        // 模拟调用服务出现异常
        if ("0".equals(skuId)) {
            throw new RuntimeException();
        }

        return "商品查询成功 : " + skuId;
    }

    /**
     * 接口抛出限流或降级时的处理逻辑
     * <p>
     * 注意: 方法参数、返回值要与原函数保持一致
     *
     * @return
     */
    public String blockHandlerMethod(String skuId, BlockException e) {
        log.warn("返回熔断结果", e.toString());
        return "异常次数太多，直接熔断了 , 返回 res: " + skuId;

    }

    /**
     * 运行时抛出的异常提供fallback处理
     * <p>
     * 注意: 方法参数、返回值要与原函数保持一致
     *
     * @return
     */
    public String querySkusInfoFallback(String skuId, Throwable e) {
        log.warn("返回降级结果", e.toString());
        return "商品查询异常 , 返回回退结果 : " + skuId;

    }

}