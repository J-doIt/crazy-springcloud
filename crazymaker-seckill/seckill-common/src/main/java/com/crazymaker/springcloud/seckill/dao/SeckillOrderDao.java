package com.crazymaker.springcloud.seckill.dao;

import com.crazymaker.springcloud.seckill.dao.po.SeckillOrderPO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Created by 尼恩 on 2019/7/18.
 */
@Repository
public interface SeckillOrderDao extends JpaRepository<SeckillOrderPO, Long>, JpaSpecificationExecutor<SeckillOrderPO>
{


}
