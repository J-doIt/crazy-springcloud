package com.crazymaker.springcloud.sharding.jdbc.demo.dao;

import com.crazymaker.springcloud.sharding.jdbc.demo.entity.jpa.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface SearchOrderRepository extends JpaRepository<OrderEntity, Long>, JpaSpecificationExecutor {
    /**
     * 根据用户查询 order
     *
     * @return
     */
    @Query(value = "SELECT a.* FROM `t_order` a left join `t_user` b on a.user_id=b.user_id  where  a.user_id=?1", nativeQuery = true)
    List<OrderEntity> selectOrderOfUserId(long userId);

    /**
     * 根据用户 删除 order
     *
     * @return
     */

    @Transactional(rollbackOn = Exception.class)
    @Modifying
    @Query(value = "delete from log where  `timestamp` <= curdate() - interval 3 month", nativeQuery = true)
    void deleteLogOfThreeMonth();
}