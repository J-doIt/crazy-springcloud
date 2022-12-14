package com.crazymaker.cloud.nacos.demo.sentinel.controller;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
@Api(tags = "OrderController 演示")
@Slf4j
public class OrderController {

    /**
     * 限流实现方式一: 抛出异常的方式定义资源
     *
     * @param orderId
     * @return
     */
    @ApiOperation(value = "纯代码限流")
    @GetMapping("/getOrder")
    @ResponseBody
    public String getOrder(@RequestParam(value = "orderId", required = false) String orderId) {

        Entry entry = null;
        // 资源名
        String resourceName = "getOrder";
        try {
            // entry可以理解成入口登记
            entry = SphU.entry(resourceName);
            // 被保护的逻辑, 这里为订单查询接口
            return "正常的业务逻辑 OrderInfo :" + orderId;
        } catch (BlockException blockException) {
            // 接口被限流的时候, 会进入到这里
            log.warn("---getOrder1接口被限流了---, exception: ", blockException);
            return "接口限流, 返回空";
        } finally {
            // SphU.entry(xxx) 需要与 entry.exit() 成对出现,否则会导致调用链记录异常
            if (entry != null) {
                entry.exit();
            }
        }

    }

    /**
     * 订单查询接口, 使用Sentinel注解实现限流
     *
     * @param orderId
     * @return
     */
    @ApiOperation(value = "注解+代码规则限流")
    @GetMapping("/detail")
    @SentinelResource(value = "orderInfo", blockHandler = "handleFlowQpsException",
            fallback = "queryOrderInfoFallback")
    public String getOrderInfo(String orderId) {

        // 模拟接口运行时抛出代码异常
        if ("000".equals(orderId)) {
            throw new RuntimeException();
        }

        System.out.println("获取订单信息:" + orderId);
        return "return OrderInfo :" + orderId;
    }

    /**
     * 接口抛出限流或降级时的处理逻辑
     * <p>
     * 注意: 方法参数、返回值要与原函数保持一致
     *
     * @return
     */
    public String handleFlowQpsException(String orderId, BlockException e) {
        e.printStackTrace();
        return "订单查询被限流: " + orderId;
    }

    /**
     * 订单查询接口运行时抛出的异常提供fallback处理
     * <p>
     * 注意: 方法参数、返回值要与原函数保持一致
     *
     * @return
     */
    public String queryOrderInfoFallback(String orderId, Throwable e) {
        return "订单查询失败: " + orderId;
    }

}