package com.crazymaker.springcloud.sharding.jdbc.demo.controller;

import com.crazymaker.springcloud.common.result.RestOut;
import com.crazymaker.springcloud.sharding.jdbc.demo.entity.Order;
import com.crazymaker.springcloud.sharding.jdbc.demo.entity.User;
import com.crazymaker.springcloud.sharding.jdbc.demo.service.JpaEntityService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;


@RestController
@RequestMapping("/api/sharding/")
@Api(tags = "sharding jdbc 演示")
public class ShardingJdbcController {

    @Resource
    JpaEntityService jpaEntityService;


    @PostMapping("/order/add/v1")
    @ApiOperation(value = "插入订单")
    public RestOut<Order> orderAdd(@RequestBody Order dto) {
        jpaEntityService.addOrder(dto);

        return RestOut.success(dto);
    }


    @PostMapping("/order/list/v1")
    @ApiOperation(value = "查询订单")
    public RestOut<List<Order>> listAll() {
        List<Order> list = jpaEntityService.selectAllOrder();

        return RestOut.success(list);
    }


    @PostMapping("/user/add/v1")
    @ApiOperation(value = "插入用户")
    public RestOut<User> userAdd(@RequestBody User dto) {
        jpaEntityService.addUser(dto);

        return RestOut.success(dto);
    }


    @PostMapping("/user/list/v1")
    @ApiOperation(value = "查询用户")
    public RestOut<List<User>> listAllUser() {
        List<User> list = jpaEntityService.selectAllUser();

        return RestOut.success(list);
    }


}
