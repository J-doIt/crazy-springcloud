package com.crazymaker.springcloud.seckill.service.impl;

import com.crazymaker.springcloud.common.exception.BusinessException;
import com.crazymaker.springcloud.common.util.UUIDUtil;
import com.crazymaker.springcloud.seckill.api.constant.SeckillConstants;
import com.crazymaker.springcloud.seckill.api.dto.SeckillDTO;
import com.crazymaker.springcloud.seckill.api.dto.SeckillOrderDTO;
import com.crazymaker.springcloud.seckill.dao.SeckillOrderDao;
import com.crazymaker.springcloud.seckill.dao.SeckillSegmentStockDao;
import com.crazymaker.springcloud.seckill.dao.SeckillSkuDao;
import com.crazymaker.springcloud.seckill.dao.po.SeckillOrderPO;
import com.crazymaker.springcloud.standard.lock.JedisMultiSegmentLock;
import com.crazymaker.springcloud.standard.lock.RedisLockService;
import com.crazymaker.springcloud.standard.redis.RedisRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static com.crazymaker.springcloud.standard.lock.RedisLockService.SEGMENT_DEFAULT;
import static com.crazymaker.springcloud.standard.lock.RedisLockService.getDefaultRequestId;
import static com.crazymaker.springcloud.standard.lua.ScriptHolder.getSeckillScript;

@Configuration
@Slf4j
@Service
public class RedisSeckillServiceImpl {

    /**
     * 秒杀商品的 DAO 数据操作类
     */
    @Resource
    SeckillSkuDao seckillSkuDao;
    /**
     * 秒杀订单的 DAO 数据操作类
     */
    @Resource
    SeckillOrderDao seckillOrderDao;

    @Resource
    SeckillSegmentStockDao seckillSegmentStockDao;

    /**
     * redis 分布式锁实现类
     */
    @Autowired
    RedisLockService redisLockService;

    /**
     * 缓存数据操作类
     */
    @Resource
    RedisRepository redisRepository;


    /**
     * 获取秒杀令牌
     *
     * @param exposedKey 秒杀id
     * @param userId     用户id
     * @return 令牌信息
     */
    public String getSeckillToken(String exposedKey, String userId) {


        String token = UUIDUtil.uuid();
        Long res = redisRepository.executeScript(
                getSeckillScript(), Collections.singletonList("setToken"),
                exposedKey,
                userId,
                token
        );

        if (res == 2) {
            throw BusinessException.builder().errMsg("秒杀商品没有找到").build();
        }

        if (res == 4) {
            throw BusinessException.builder().errMsg("库存不足,稍后再来").build();
        }

        if (res == 5) {
            throw BusinessException.builder().errMsg("已经排队过了").build();
        }


        if (res != 1) {
            throw BusinessException.builder().errMsg("排队失败,未知错误").build();

        }
        return token;
    }


    /**
     * 执行秒杀下单
     *
     * @param inDto
     * @return
     */
    public SeckillOrderDTO executeSeckill(SeckillDTO inDto) {
        String exposedKey = inDto.getExposedKey();
        String cacheSkuId = redisRepository.getStr("seckill:sku:" + exposedKey);
        if (null == cacheSkuId) {
            throw BusinessException.builder().errMsg("秒杀没有开始或者已经结束").build();
        }
        long skuId = Long.parseLong(cacheSkuId);
        Long userId = inDto.getUserId();

        Long res = redisRepository.executeScript(
                getSeckillScript(), Collections.singletonList("checkToken"),
                inDto.getExposedKey(),//        String.valueOf(inDto.getSeckillSkuId()),
                String.valueOf(inDto.getUserId()),
                inDto.getSeckillToken()
        );

        if (res != 5) {
            throw BusinessException.builder().errMsg("请提前排队").build();
        }


        SeckillOrderDTO dto = null;

        /**
         * 获取分布式锁
         */
        String lockKey = "seckill:segmentLock:" + String.valueOf(skuId);
        String requestId = getDefaultRequestId();
        JedisMultiSegmentLock lock = redisLockService.getSegmentLock(lockKey, requestId, SEGMENT_DEFAULT);
        boolean hasError = false;
        boolean locked = false;
        try {

            /**
             * 执行秒杀，秒杀前先抢到分布式锁
             */
            locked = lock.tryLock(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw BusinessException.builder().errMsg("秒杀失败").build();

        }
        if (locked) {
            try {


                /**
                 * 创建订单对象
                 */
                SeckillOrderPO order =
                        SeckillOrderPO.builder()
                                .skuId(skuId).userId(userId).build();


                Date nowTime = new Date();
                order.setCreateTime(nowTime);
                order.setStatus(SeckillConstants.ORDER_VALID);

                /**
                 * 创建重复性检查的订单对象
                 */
                SeckillOrderPO checkOrder =
                        SeckillOrderPO.builder()
                                .skuId(order.getSkuId())
                                .userId(order.getUserId()).build();

                //记录秒杀订单信息
                long insertCount = seckillOrderDao.count(Example.of(checkOrder));

                //唯一性判断：skuId,id 保证一个用户只能秒杀一件商品
                if (insertCount >= 1) {
                    //重复秒杀
                    log.error("重复秒杀");
                    throw BusinessException.builder().errMsg("重复秒杀").build();
                }
                int stockLeft = seckillSegmentStockDao.sumStockCountById(skuId);
                if (stockLeft <= 0) {
                    //库存不够
                    log.error("库存不够");
                    throw BusinessException.builder().errMsg("库存不够").build();
                }


//                Optional<SeckillSkuPO> optional = seckillSkuDao.findById(order.getSkuId());
//                if (!optional.isPresent()) {
//                    //秒杀不存在
//                    throw BusinessException.builder().errMsg("秒杀不存在").build();
//                }
//
//
//                //查询库存
//                SeckillSkuPO sku = optional.get();
//                if (sku.getStockCount() <= 0) {
//                    //重复秒杀
//                    throw BusinessException.builder().errMsg("秒杀商品被抢光").build();
//                }

//                order.setMoney(sku.getCostPrice());


                /**
                 * 进入秒杀事务
                 * 执行秒杀逻辑：1.减分段库存 （这里没有扣减总的库存哦）；  2.下秒杀订单
                 */
                doInTransaction(order, lock);


                dto = new SeckillOrderDTO();
                BeanUtils.copyProperties(order, dto);


            } catch (Exception e) {
                e.printStackTrace();
                hasError = true;

            } finally {
                /**
                 * 释放分布式锁
                 */
                lock.unlock();
            }

        }

        if (!locked || hasError) {
            throw BusinessException.builder().errMsg("执行秒杀，发生异常").build();
        }

        return dto;

    }

    @Transactional
    public void doInTransaction(SeckillOrderPO order, JedisMultiSegmentLock lock) {
        long skuId = order.getSkuId();
        /**
         * 插入秒杀订单
         */
        seckillOrderDao.save(order);

        //减分段库存
        int segment = lock.getSegmentIndexLocked();
//                seckillSkuDao.updateStockCountById(order.getSkuId());
        seckillSegmentStockDao.decreaseStock(skuId, segment);


        //减库存

        seckillSkuDao.decreaseStockCountById(skuId);
    }


}
