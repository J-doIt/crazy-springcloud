package com.crazymaker.springcloud.sharding;

import com.crazymaker.springcloud.sharding.jdbc.demo.entity.Order;
import com.crazymaker.springcloud.sharding.jdbc.demo.entity.Page;
import com.crazymaker.springcloud.sharding.jdbc.demo.entity.User;
import com.crazymaker.springcloud.sharding.jdbc.demo.service.JpaEntityService;
import com.crazymaker.springcloud.sharding.jdbc.demo.start.ShardingJdbcDemoCloudApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ShardingJdbcDemoCloudApplication.class})
// 指定启动类
public class ShardingTest {

    @Resource
    JpaEntityService jpaEntityService;


    @Test
    public void testAddSomeUser() {

        for (int i = 0; i < 10; i++) {
            User dto = new User();

            dto.setName("user_" + i);

            //增加用户
            jpaEntityService.addUser(dto);
        }


    }

    @Test
    public void testSelectAllUser() {
        //增加用户
        List<User> all = jpaEntityService.selectAllUser();
        System.out.println(all);

    }


    @Test
    public void testSelectAll() {
        jpaEntityService.selectAll();
    }

    @Test
    public void testSelectPage() {
        Page page = new Page();
        //第1页
        page.setPage(1);
        page.setRowsTotal(10);
        page.setRows(3);
        List<User> list = jpaEntityService.selectUser(page);
        System.out.println(list);

        //第2页

        page.setPage(2);
        page.setRowsTotal(10);
        page.setRows(3);
        list = jpaEntityService.selectUser(page);
        System.out.println(list);

    }


    @Test
    public void testAddSomeOrder() {

        for (int i = 0; i < 10; i++) {
            Order dto = new Order();
            dto.setUserId(704733680467685377L);

            //增加用户
            jpaEntityService.addOrder(dto);
        }


    }

    @Test
    public void testAddUserAndOrder() {


        for (int i = 0; i < 10; i++) {

            User user = new User();

            user.setName("user_" + i);
            //增加用户
            jpaEntityService.addUser(user);

            Order dto = new Order();

            dto.setUserId(user.getUserId());

            //增加用户
            jpaEntityService.addOrder(dto);
        }


    }


    @Test
    public void testSelectAOrderByUserId() {

        long userId = 704733680467685377L;
        List<Order> all = jpaEntityService.selectAOrderByUserId(userId);
        System.out.println(all);

    }

}
