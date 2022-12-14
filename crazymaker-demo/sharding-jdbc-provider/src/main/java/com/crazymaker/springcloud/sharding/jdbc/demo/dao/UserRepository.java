package com.crazymaker.springcloud.sharding.jdbc.demo.dao;


import com.crazymaker.springcloud.sharding.jdbc.demo.entity.Page;
import com.crazymaker.springcloud.sharding.jdbc.demo.entity.User;

import java.util.List;

public interface UserRepository {
    Long insert(User user);

    void delete(Long userId);

    @SuppressWarnings("unchecked")
    List<User> selectAll();

    List<User> selectOnePage(Page page);


}
