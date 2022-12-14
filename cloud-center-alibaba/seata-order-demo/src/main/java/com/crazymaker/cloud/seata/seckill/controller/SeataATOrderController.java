package com.crazymaker.cloud.seata.seckill.controller;

        import com.crazymaker.cloud.seata.seckill.service.impl.SeckillOrderServiceImpl;
        import com.crazymaker.springcloud.common.page.PageOut;
        import com.crazymaker.springcloud.common.page.PageReq;
        import com.crazymaker.springcloud.common.result.RestOut;
        import com.crazymaker.springcloud.seckill.api.dto.SeckillDTO;
        import com.crazymaker.springcloud.seckill.api.dto.SeckillOrderDTO;
        import com.crazymaker.springcloud.seckill.dao.po.SeckillOrderPO;
        import io.swagger.annotations.Api;
        import io.swagger.annotations.ApiOperation;
        import org.springframework.beans.BeanUtils;
        import org.springframework.web.bind.annotation.GetMapping;
        import org.springframework.web.bind.annotation.PathVariable;
        import org.springframework.web.bind.annotation.PostMapping;
        import org.springframework.web.bind.annotation.RequestBody;
        import org.springframework.web.bind.annotation.RequestMapping;
        import org.springframework.web.bind.annotation.RestController;

        import javax.annotation.Resource;
        import java.util.List;


@RestController
@RequestMapping("/api/seckill/order/")
@Api(tags = "秒杀练习 订单管理")
public class SeataATOrderController {
    @Resource
    SeckillOrderServiceImpl seckillOrderService;


    /**
     * 查询用户订单信息
     *
     * @param userId 用户id
     * @return 商品 dto
     */
    @PostMapping("/user/{id}/list/v1")
    @ApiOperation(value = "查询用户订单信息")
    RestOut<PageOut<SeckillOrderDTO>> userOrders(
            @PathVariable(value = "id") Long userId, @RequestBody PageReq pageReq) {
        PageOut<SeckillOrderDTO> dto = seckillOrderService.findOrderByUserID(userId, pageReq);
        if (null != dto) {
            return RestOut.success(dto).setRespMsg("查询成功");
        }
        return RestOut.error("查询失败");
    }

    /**
     * 查询用户的订单信息
     *
     * @param userId 用户id
     * @param skuId  商品id
     * @return 商品 dto
     */
    @GetMapping("/{userId}/{skuId}/v1")
    @ApiOperation(value = "查询用户订单信息")
    RestOut<SeckillOrderDTO> userOrders(
            @PathVariable(value = "userId") Long userId,
            @PathVariable(value = "skuId") Long skuId,
            @RequestBody PageReq pageReq) {
        List<SeckillOrderPO> pos = seckillOrderService.findOrderByUserIDAndSkuId(userId, skuId);
        if (null != pos && pos.size() > 0) {
            SeckillOrderDTO orderDTO = new SeckillOrderDTO();
            BeanUtils.copyProperties(pos.get(0), orderDTO);
            return RestOut.success(orderDTO).setRespMsg("查询成功");
        }
        return RestOut.error("查询失败");
    }


    /**
     * 清除用户订单信息
     *
     * @param dto 含有  用户id的dto
     * @return 操作结果
     */
    @PostMapping("/user/clear/v1")
    @ApiOperation(value = "清除用户订单信息")
    RestOut<String> userOrdersClear(@RequestBody SeckillDTO dto) {
        Long userId = dto.getUserId();
        String result = seckillOrderService.clearOrderByUserID(userId);

        return RestOut.success(result).setRespMsg("处理完成");
    }


    /**
     * 执行秒杀的操作
     * <p>
     * <p>
     * {
     * "exposedKey": "4b70903f6e1aa87788d3ea962f8b2f0e",
     * "newStockNum": 10000,
     * "seckillSkuId": 1157197244718385152,
     * "seckillToken": "0f8459cbae1748c7b14e4cea3d991000",
     * "userId": 37
     * }
     *
     * @return
     */
    @ApiOperation(value = "下订单")
    @PostMapping("/addOrder/v1")
    RestOut<SeckillOrderDTO> addOrder(@RequestBody SeckillDTO dto) {
        SeckillOrderDTO orderDTO = seckillOrderService.addOrder(dto);
        return RestOut.success(orderDTO).setRespMsg("下订单成功");
    }
}
