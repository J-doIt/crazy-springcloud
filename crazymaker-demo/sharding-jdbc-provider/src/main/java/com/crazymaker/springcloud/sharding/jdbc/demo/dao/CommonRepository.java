package com.crazymaker.springcloud.sharding.jdbc.demo.dao;

import java.util.List;

public interface CommonRepository<T> {

    void createTableIfNotExists();

    void dropTable();

    void truncateTable();

    Long insert(T entity);

    void delete(Long id);

    List<T> selectAll();
}
