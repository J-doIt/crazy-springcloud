package com.crazymaker.springcloud.seckill.service.impl;

import com.crazymaker.springcloud.common.page.DataAdapter;
import com.crazymaker.springcloud.common.page.PageOut;
import com.crazymaker.springcloud.common.page.PageReq;
import com.crazymaker.springcloud.seckill.api.dto.SeckillOrderDTO;
import com.crazymaker.springcloud.seckill.dao.SeckillOrderDao;
import com.crazymaker.springcloud.seckill.dao.po.SeckillOrderPO;
import com.crazymaker.springcloud.standard.lock.RedisLockService;
import com.crazymaker.springcloud.standard.redis.RedisRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Iterator;
import java.util.List;

@Configuration
@Slf4j
@Service
public class SeckillOrderServiceImpl {

    @Resource
    SeckillOrderDao seckillOrderDao;

    @Resource
    RedisRepository redisRepository;


    @Autowired
    RedisLockService redisLockService;


    public PageOut<SeckillOrderDTO> findOrderByUserID(Long userId, PageReq pageReq) {

        PageRequest jpaPage = PageRequest.of(pageReq.getJpaPage(), pageReq.getPageSize());


        /**
         * 创建条件对象
         */
        SeckillOrderPO checkOrder =
                SeckillOrderPO.builder().userId(userId).build();

        Page<SeckillOrderPO> page = seckillOrderDao.findAll(Example.of(checkOrder), jpaPage);

        PageOut<SeckillOrderDTO> pageData = DataAdapter.adapterPage(page, SeckillOrderDTO.class);


        return pageData;
    }

    public List<SeckillOrderPO> findOrderByUserIDAndSkuId(Long userId, Long skuId) {


        /**
         * 创建条件对象
         */
        SeckillOrderPO checkOrder =
                SeckillOrderPO.builder().userId(userId).skuId(skuId).build();

        List<SeckillOrderPO> page = seckillOrderDao.findAll(Example.of(checkOrder));


        return page;
    }

    /**
     * 根据用户id 清除所有订单
     *
     * @param userId 用户id
     * @return 结果
     */
    public String clearOrderByUserID(Long userId) {

        /**
         * 创建条件对象
         */
        SeckillOrderPO checkOrder =
                SeckillOrderPO.builder().userId(userId).build();

        List<SeckillOrderPO> page = seckillOrderDao.findAll(Example.of(checkOrder));

        Iterator<SeckillOrderPO> it = page.iterator();
        while (it.hasNext()) {
            SeckillOrderPO next = it.next();
            String skuId = "seckill:queue:" + String.valueOf(next.getSkuId());
            redisRepository.hdel(skuId, String.valueOf(userId));
            seckillOrderDao.delete(next);
        }

        return "清除成功";
    }
}
