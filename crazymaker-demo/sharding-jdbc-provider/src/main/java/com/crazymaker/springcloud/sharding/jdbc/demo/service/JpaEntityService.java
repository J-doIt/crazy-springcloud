package com.crazymaker.springcloud.sharding.jdbc.demo.service;


import com.crazymaker.springcloud.sharding.jdbc.demo.entity.ConfigBean;
import com.crazymaker.springcloud.sharding.jdbc.demo.entity.Order;
import com.crazymaker.springcloud.sharding.jdbc.demo.entity.Page;
import com.crazymaker.springcloud.sharding.jdbc.demo.entity.User;

import java.util.List;

public interface JpaEntityService {


    void initEnvironment();

    void cleanEnvironment();

    void processSuccess();

    void processFailure();

    void selectAll();

    //增加订单
    void addOrder(Order dto);

    //查询全部订单
    List<Order> selectAllOrder();

    List<Order> selectAOrderByUserId(long userId);

    //增加用户
    void addUser(User dto);

    //查询全部用户
    List<User> selectAllUser();

    //查询分页用户
    public List<User> selectUser(Page page);


    void addConfigBean(ConfigBean dto);

    //查询全部用户
    List<ConfigBean> selectAllConfigBean();

    List<ConfigBean> selectConfigBean(Page page);
}
