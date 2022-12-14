package com.crazymaker.springcloud.sharding.jdbc.demo.dao;


import com.crazymaker.springcloud.sharding.jdbc.demo.entity.ConfigBean;
import com.crazymaker.springcloud.sharding.jdbc.demo.entity.Page;

import java.util.List;

public interface ConfigRepository {
    Long insert(ConfigBean user);

    void delete(Long userId);

    @SuppressWarnings("unchecked")
    List<ConfigBean> selectAll();

    List<ConfigBean> selectOnePage(Page page);


}
