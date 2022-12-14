package com.crazymaker.springcloud.seckill.service.impl;

import com.crazymaker.springcloud.common.distribute.rateLimit.RateLimitService;
import com.crazymaker.springcloud.common.exception.BusinessException;
import com.crazymaker.springcloud.common.util.Encrypt;
import com.crazymaker.springcloud.seckill.api.constant.SeckillConstants;
import com.crazymaker.springcloud.seckill.api.dto.SeckillOrderDTO;
import com.crazymaker.springcloud.seckill.api.dto.SimpleOrderDTO;
import com.crazymaker.springcloud.seckill.dao.SeckillOrderDao;
import com.crazymaker.springcloud.seckill.dao.SeckillSkuDao;
import com.crazymaker.springcloud.seckill.dao.po.SeckillOrderPO;
import com.crazymaker.springcloud.seckill.dao.po.SeckillSkuPO;
import com.crazymaker.springcloud.standard.lock.RedisLockService;
import com.crazymaker.springcloud.standard.lock.impl.ZkLockServiceImpl;
import com.crazymaker.springcloud.standard.redis.RedisRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Configuration
@Slf4j
@Service
public class SeckillServiceImpl {

    @Resource
    SeckillSkuDao seckillSkuDao;

    @Resource
    SeckillOrderDao seckillOrderDao;

    @Resource(name = "zkRateLimitServiceImpl")
    RateLimitService zkRateLimitServiceImpl;


    @Autowired
    RedisLockService redisLockService;

    @Resource
    ZkLockServiceImpl zkLockServiceImpl;

    @Resource
    RedisRepository redisRepository;

    @Value("${distribute.redis.seckillSucess.sha1}")
    private String seckillSucessSha1;

    @Value("${distribute.redis.seckillFailed.sha1}")
    private String seckillFailedSha1;


    /**
     * 秒杀的分布式控制
     * Spring默认只对运行期异常进行事务的回滚操作
     * 对于受检异常Spring是不进行回滚的
     * 所以对于需要进行事务控制的方法尽可能将可能抛出的异常都转换成运行期异常
     *
     * @param skuId 商品id
     * @param money  钱
     * @param userId 用户id
     * @param md5    校验码
     * @return
     */

    public SeckillOrderDTO executeSeckillV1(
            long skuId, BigDecimal money, long userId, String md5) {
        if (md5 == null || !md5.equals(Encrypt.getMD5(String.valueOf(skuId)))) {
            throw BusinessException.builder().errMsg("秒杀的链接被重写过了").build();
        }

        /**
         * Zookeeper 限流计数器 增加数量
         */
        Boolean isLimited = zkRateLimitServiceImpl.tryAcquire(String.valueOf(skuId));
        if (isLimited) {     //秒杀异常
            throw BusinessException.builder().errMsg("秒杀异常").build();

        }

        /**
         * 创建订单对象
         */
        SeckillOrderPO order =
                SeckillOrderPO.builder().skuId(skuId).userId(userId).build();


        //执行秒杀逻辑：1.减库存；2.储存秒杀订单
        Date nowTime = new Date();
        order.setCreateTime(nowTime);
        order.setMoney(money);
        order.setStatus(SeckillConstants.ORDER_VALID);


        /**
         * 创建分布式锁
         */
        InterProcessMutex lock =
                zkLockServiceImpl.getZookeeperLock(String.valueOf(skuId));

        try {
            /**
             * 获取分布式锁
             */
            lock.acquire(1, TimeUnit.SECONDS);
            /**
             * 执行秒杀，带事务
             */
            doSeckill(order);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                /**
                 * 释放分布式锁
                 */
                lock.release();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }


        SeckillOrderDTO dto = new SeckillOrderDTO();
        BeanUtils.copyProperties(order, dto);


        return dto;

    }


    public SeckillOrderDTO executeSeckillV3(SimpleOrderDTO inDto) {
        long skuId = inDto.getSkuId();
        String md5 = inDto.getMd5();
        Long userId = inDto.getUserId();
        if (md5 == null || !md5.equals(Encrypt.getMD5(String.valueOf(skuId)))) {
            log.error("秒杀的链接被重写过了");
            throw BusinessException.builder().errMsg("秒杀的链接被重写过了").build();
        }
        /**
         * 创建订单对象
         */
        SeckillOrderPO order =
                SeckillOrderPO.builder().skuId(skuId).userId(userId).build();


        //执行秒杀逻辑：1.减库存；2.储存秒杀订单
        Date nowTime = new Date();
        order.setCreateTime(nowTime);
        order.setStatus(SeckillConstants.ORDER_VALID);

        SeckillOrderDTO dto = null;
        /**
         * 执行秒杀，带事务
         */
        try {
            doSeckillV3(order);
        } catch (Throwable e) {
            List<String> redisKeys = new ArrayList<>();
            redisKeys.add(String.valueOf(inDto.getSkuId()));
            redisKeys.add(String.valueOf(inDto.getUserId()));
            RedisRepository.singleton().evalSha(seckillFailedSha1, redisKeys);
            //捕获了异常之后，再次向上抛出
            log.error("seckill error:", inDto.getSkuId(), "|", inDto.getUserId());
            throw e;
        }

        dto = new SeckillOrderDTO();
        BeanUtils.copyProperties(order, dto);


        return dto;

    }

    @Transactional
    public void doSeckill(SeckillOrderPO order) {
        /**
         * 创建重复性检查的订单对象
         */
        SeckillOrderPO checkOrder =
                SeckillOrderPO.builder().skuId(order.getSkuId()).userId(order.getUserId()).build();

        //记录秒杀订单信息
        long insertCount = seckillOrderDao.count(Example.of(checkOrder));

        //唯一性判断：skuId,id 保证一个用户只能秒杀一件商品
        if (insertCount >= 1) {
            //重复秒杀
            log.error("重复秒杀");
            throw BusinessException.builder().errMsg("重复秒杀").build();
        }


        Optional<SeckillSkuPO> optional = seckillSkuDao.findById(order.getSkuId());
        if (!optional.isPresent()) {
            //秒杀不存在
            throw BusinessException.builder().errMsg("秒杀不存在").build();
        }


        //查询库存
        SeckillSkuPO sku = optional.get();
        if (sku.getStockCount() <= 0) {
            //重复秒杀
            throw BusinessException.builder().errMsg("秒杀商品被抢光").build();
        }

        order.setMoney(sku.getCostPrice());

        seckillOrderDao.save(order);

        //减库存

        seckillSkuDao.decreaseStockCountById(order.getSkuId());
    }

    @Transactional
    public void doSeckillV3(SeckillOrderPO order) {
        /**
         * 创建重复性检查的订单对象
         */
        SeckillOrderPO checkOrder =
                SeckillOrderPO.builder().skuId(order.getSkuId()).userId(order.getUserId()).build();

        //记录秒杀订单信息
        long insertCount = seckillOrderDao.count(Example.of(checkOrder));

        //唯一性判断：skuId,id 保证一个用户只能秒杀一件商品
        if (insertCount >= 1) {
            //重复秒杀
            log.error("重复秒杀");
            throw BusinessException.builder().errMsg("重复秒杀").build();
        }


        Optional<SeckillSkuPO> optional = seckillSkuDao.findById(order.getSkuId());
        if (!optional.isPresent()) {
            //秒杀不存在
            throw BusinessException.builder().errMsg("秒杀不存在").build();
        }


        //查询库存
        SeckillSkuPO sku = optional.get();
        if (sku.getStockCount() <= 0) {
            //重复秒杀
            throw BusinessException.builder().errMsg("秒杀商品被抢光").build();
        }


        //减库存
        seckillSkuDao.decreaseStockCountById(order.getSkuId());


        order.setMoney(sku.getCostPrice());

        seckillOrderDao.save(order);

        List<String> redisKeys = new ArrayList<>();
        redisKeys.add(String.valueOf(order.getSkuId()));
        redisKeys.add(String.valueOf(order.getUserId()));
        RedisRepository.singleton().evalSha(seckillSucessSha1, redisKeys);

    }


}
