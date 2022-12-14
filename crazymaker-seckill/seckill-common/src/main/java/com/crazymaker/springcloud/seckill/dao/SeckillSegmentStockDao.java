package com.crazymaker.springcloud.seckill.dao;

import com.crazymaker.springcloud.seckill.dao.po.SeckillSegmentStockPO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

/**
 * Created by 尼恩 on 2019/7/18.
 */
@Repository
public interface SeckillSegmentStockDao extends
        JpaRepository<SeckillSegmentStockPO, Long>, JpaSpecificationExecutor<SeckillSegmentStockPO>
{

    @Transactional
    @Modifying
    @Query("update SeckillSegmentStockPO  s set s.stockCount = s.stockCount-1  where s.skuId = :skuId and s.segmentIndex = :segmentIndex" )
    int decreaseStock(@Param("skuId") Long skuId, @Param("segmentIndex") Integer segmentIndex);


    @Transactional
    @Modifying
    @Query("delete  from  SeckillSegmentStockPO s where s.skuId = :skuId" )
    int deleteStockBySku(@Param("skuId") Long skuId);

    @Query("select sum(s.stockCount) from SeckillSegmentStockPO s where s.skuId = :skuId " )
    int sumStockCountById(@Param("skuId") Long skuId);


}
