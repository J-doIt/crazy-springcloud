package com.crazymaker.springcloud.sharding;


import com.crazymaker.springcloud.sharding.jdbc.demo.entity.ConfigBean;
import com.crazymaker.springcloud.sharding.jdbc.demo.entity.Page;
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

public class ConfigTest {

    @Resource
    JpaEntityService jpaEntityService;


    @Test
    public void testAddSomeConfigBean() {

        for (int i = 0; i < 10; i++) {
            ConfigBean dto = new ConfigBean();

            dto.setStatus("UN_KNOWN" + i);

            //增加 数据
            jpaEntityService.addConfigBean(dto);
        }


    }

    @Test
    public void testSelectAllConfigBean() {
        List<ConfigBean> all = jpaEntityService.selectAllConfigBean();
        System.out.println(all);

    }



    @Test
    public void testSelectPage() {
        Page page = new Page();
        //第1页
        page.setPage(1);
        page.setRowsTotal(10);
        page.setRows(3);
        List<ConfigBean> list = jpaEntityService.selectConfigBean(page);
        System.out.println(list);

        //第2页

        page.setPage(2);
        page.setRowsTotal(10);
        page.setRows(3);
        list = jpaEntityService.selectConfigBean(page);
        System.out.println(list);

    }

}
