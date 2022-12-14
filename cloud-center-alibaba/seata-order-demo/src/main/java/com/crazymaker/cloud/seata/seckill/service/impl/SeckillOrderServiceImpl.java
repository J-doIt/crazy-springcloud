package com.crazymaker.cloud.seata.seckill.service.impl;

import com.crazymaker.springcloud.common.exception.BusinessException;
import com.crazymaker.springcloud.common.page.DataAdapter;
import com.crazymaker.springcloud.common.page.PageOut;
import com.crazymaker.springcloud.common.page.PageReq;
import com.crazymaker.springcloud.seckill.api.constant.SeckillConstants;
import com.crazymaker.springcloud.seckill.api.dto.SeckillDTO;
import com.crazymaker.springcloud.seckill.api.dto.SeckillOrderDTO;
import com.crazymaker.springcloud.seckill.dao.SeckillOrderDao;
import com.crazymaker.springcloud.seckill.dao.po.SeckillOrderPO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Configuration
@Slf4j
@Service
public class SeckillOrderServiceImpl {

    @Resource
    SeckillOrderDao seckillOrderDao;


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
            seckillOrderDao.delete(next);
        }

        return "清除成功";
    }


    /**
     * 执行秒杀下单
     *
     * @param inDto
     * @return
     */
    @Transactional //开启本地事务
    // @GlobalTransactional//不，开启全局事务（重点） 使用 seata 的全局事务
    public SeckillOrderDTO addOrder(SeckillDTO inDto) {

        long skuId = inDto.getSeckillSkuId();
        Long userId = inDto.getUserId();


        /**
         * 创建订单对象
         */
        SeckillOrderPO order =
                SeckillOrderPO.builder()
                        .skuId(skuId).userId(userId).build();


        Date nowTime = new Date();
        order.setCreateTime(nowTime);
        order.setStatus(SeckillConstants.ORDER_VALID);


        SeckillOrderDTO dto = null;

        /**
         * 创建重复性检查的订单对象
         */
        SeckillOrderPO checkOrder =
                SeckillOrderPO.builder().skuId(
                        order.getSkuId()).userId(order.getUserId()).build();

        //记录秒杀订单信息
        long insertCount = seckillOrderDao.count(Example.of(checkOrder));

        //唯一性判断：skuId,id 保证一个用户只能秒杀一件商品
        if (insertCount >= 1) {
            //重复秒杀
            log.error("重复秒杀");
            throw BusinessException.builder().errMsg("重复秒杀").build();
        }


        /**
         * 插入秒杀订单
         */
        seckillOrderDao.save(order);


        dto = new SeckillOrderDTO();
        BeanUtils.copyProperties(order, dto);


        return dto;

    }

}
