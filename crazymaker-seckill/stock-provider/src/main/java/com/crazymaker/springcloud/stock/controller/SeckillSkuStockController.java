package com.crazymaker.springcloud.stock.controller;

import cn.hutool.json.JSONUtil;
import com.crazymaker.springcloud.common.page.PageOut;
import com.crazymaker.springcloud.common.page.PageReq;
import com.crazymaker.springcloud.common.result.RestOut;
import com.crazymaker.springcloud.common.util.RandomUtil;
import com.crazymaker.springcloud.seckill.api.dto.SeckillDTO;
import com.crazymaker.springcloud.seckill.api.dto.SeckillSkuDTO;
import com.crazymaker.springcloud.standard.lock.RedisLockService;
import com.crazymaker.springcloud.standard.ratelimit.RedisRateLimitImpl;
import com.crazymaker.springcloud.standard.redis.RedisRepository;
import com.crazymaker.springcloud.stock.service.impl.SeckillSkuStockServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import static com.crazymaker.springcloud.common.constants.SessionConstants.USER_IDENTIFIER;
import static com.crazymaker.springcloud.common.util.UUIDUtil.uuid;


@RestController
@RequestMapping("/api/seckill/sku/")
@Api(tags = "商品库存")
public class SeckillSkuStockController {
    public static final String SKU_CACHE_PUT_Lock_PRIFIX = "sku_cache_put_lock:";
    public static final String SECKILLSKU_CACHE_PRIFIX = "seckillsku:";
    public static final String USERACCESS_CACHE_PRIFIX = "UserAccess:";
    public static final String BLANK_USE_CACHE_PRIFIX = "Black-USER";
    @Resource
    SeckillSkuStockServiceImpl seckillSkuStockService;


    @Resource(name = "redisRateLimitImpl")
    RedisRateLimitImpl rateLimitService;

    /**
     * 缓存数据操作类
     */
    @Resource
    RedisRepository redisRepository;


    /**
     * redis 分布式锁实现类
     */
    @Autowired
    RedisLockService redisLockService;


    /**
     * 获取所有的秒杀商品列表
     *
     * @param pageReq 当前页 ，从1 开始,和 页的元素个数
     * @return
     */
    @PostMapping("/list/v1")
    @ApiOperation(value = "全部秒杀商品")
    RestOut<PageOut<SeckillSkuDTO>> findAll(@RequestBody PageReq pageReq) {
        PageOut<SeckillSkuDTO> page = seckillSkuStockService.findAll(pageReq);
        RestOut<PageOut<SeckillSkuDTO>> r = RestOut.success(page);
        return r;

    }

    /**
     * 查询商品信息
     *
     * @param dto 商品id
     * @return 商品 skuDTO
     */
    @PostMapping("/detail/v1")
    @ApiOperation(value = "秒杀单品详情")
    RestOut<SeckillSkuDTO> skuDetail(HttpServletRequest request, @RequestBody SeckillDTO dto) {


        if (StringUtils.isEmpty(request.getHeader(USER_IDENTIFIER))) {
            return RestOut.error("请登录再来");
        }

        Long userId = Long.valueOf(request.getHeader(USER_IDENTIFIER));
        Boolean isUserBack = redisRepository.getBit(BLANK_USE_CACHE_PRIFIX, userId);
        if (isUserBack) {
            return RestOut.error("此次请求，非法，请联系管理员解除限制");
        }

        //和缓存有关的三个变量, 最好是放在接入层
        String cacheKeyOfUserAccess = USERACCESS_CACHE_PRIFIX + userId;
        int cacheTimeOfUserAccess = 600;
        int maxAccess = 5;
        int accessCount = 1;
        Object accessCache = redisRepository.getObject(cacheKeyOfUserAccess);
        if (null == accessCache) {
            redisRepository.setExpire(cacheKeyOfUserAccess, accessCount, cacheTimeOfUserAccess);
        } else {


            accessCount = Integer.valueOf(accessCache.toString()) + 1;

            if (accessCount > maxAccess) {
                redisRepository.setBit(BLANK_USE_CACHE_PRIFIX, userId, true);

                return RestOut.error("此次请求，非法，请联系管理员解除限制");
            }
            cacheTimeOfUserAccess = (int) redisRepository.getKeyExpireTime(cacheKeyOfUserAccess);
            redisRepository.setExpire(cacheKeyOfUserAccess, accessCount, cacheTimeOfUserAccess);
//            redisRepository.setObject(cacheKeyOfUserAccess, accessCount);

        }

        Long skuId = dto.getSeckillSkuId();

        //和缓存有关的三个变量
        String cacheKey = SECKILLSKU_CACHE_PRIFIX + skuId;
        int cacheTime = 2* 60 * 60;


        String skuCache = redisRepository.getStr(cacheKey);


        SeckillSkuDTO skuDTO = null;
        if (StringUtils.isNoneEmpty(skuCache)) {
            skuDTO = SeckillDTO.fromJson(skuCache);
            if (null == skuDTO)
                return RestOut.error("未找到指定秒杀商品");
            else
                return RestOut.success(skuDTO).setRespMsg("查找成功");
        }


        //使用redis 分布式锁，解决缓存击穿

        String lockKey = SKU_CACHE_PUT_Lock_PRIFIX + skuId;

        String requestId = uuid();

        Lock lock = redisLockService.getLock(lockKey, requestId);
        boolean locked = false;
        try {
            locked = lock.tryLock(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            lock.unlock();
            e.printStackTrace();
            return RestOut.error("秒杀商品访问太繁忙");
        }
        if (locked) {

            try {

                skuCache = redisRepository.getStr(cacheKey);

                if (StringUtils.isNoneEmpty(skuCache)) {
                    skuDTO = SeckillDTO.fromJson(skuCache);
                    if (null == skuDTO)
                        return RestOut.error("未找到指定秒杀商品");
                    else
                        return RestOut.success(skuDTO).setRespMsg("查找成功");
                }


                skuDTO = seckillSkuStockService.detail(skuId);

                skuCache = SeckillDTO.toJson(skuDTO);

                //解决防止同时大量的key过期

                int finalCacheTime = cacheTime + RandomUtil.randInMod(30 * 60);

                redisRepository.set(cacheKey, skuCache, finalCacheTime);


                if (null != skuDTO) {

                    return RestOut.success(skuDTO).setRespMsg("查找成功");
                }

            } finally {
                lock.unlock();
            }

        }


        return RestOut.error("未找到指定秒杀商品");
    }

    /**
     * 删除商品信息
     *
     * @param dto 商品id
     * @return 商品 skuDTO
     */
    @PostMapping("/delete/v1")
    @ApiOperation(value = "删除商品信息")
    RestOut<SeckillSkuDTO> deleteSku(@RequestBody SeckillDTO dto) {
        Long skuId = dto.getSeckillSkuId();

        seckillSkuStockService.delete(skuId);


        return RestOut.error("删除商品信息 ok");
    }


    /**
     * 设置秒杀库存
     *
     * @param dto 商品与库存
     * @return 商品 skuDTO
     */
    @PutMapping("/stock/v1")
    @ApiOperation(value = "设置秒杀库存")
    RestOut<SeckillSkuDTO> setStock(@RequestBody SeckillDTO dto) {
        Long skuId = dto.getSeckillSkuId();
        int stock = dto.getNewStockNum();

        SeckillSkuDTO skuDTO = seckillSkuStockService.setNewStock(skuId, stock);

        if (null != skuDTO) {
            return RestOut.success(skuDTO).setRespMsg("设置秒杀库存成功");
        }
        return RestOut.error("未找到指定秒杀商品");
    }


    /**
     * 增加秒杀的商品
     *
     * @param stockCount 库存
     * @param title      标题
     * @param price      商品原价格
     * @param costPrice  价格
     * @return
     */
    @PostMapping("/add/v1")
    @ApiOperation(value = "增加秒杀商品")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "title", value = "商品名称", dataType = "string", paramType = "query", required = true, defaultValue = "秒杀商品-1"),
            @ApiImplicitParam(name = "stockCount", value = "秒杀数量", dataType = "int", paramType = "query", required = true, defaultValue = "10000", example = "10000"),
            @ApiImplicitParam(name = "price", value = "原始价格", dataType = "float", paramType = "query", required = true, defaultValue = "1000", example = "1000"),
            @ApiImplicitParam(name = "costPrice", value = "秒杀价格", dataType = "float", paramType = "query", required = true, defaultValue = "10", example = "1000")
    })
    RestOut<SeckillSkuDTO> addSeckill(
            @RequestParam(value = "title", required = true) String title,
            @RequestParam(value = "stockCount", required = true) int stockCount,
            @RequestParam(value = "price", required = true) float price,
            @RequestParam(value = "costPrice", required = true) float costPrice) {
        SeckillSkuDTO dto = seckillSkuStockService.addSeckillSku(title, stockCount, price, costPrice);
        return RestOut.success(dto).setRespMsg("增加秒杀的商品成功");

    }


    /**
     * 暴露商品秒杀
     * <p>
     * {
     * "exposedKey": "4b70903f6e1aa87788d3ea962f8b2f0e",
     * "newStockNum": 10000,
     * "seckillSkuId": 1157197244718385152,
     * "seckillToken": "0f8459cbae1748c7b14e4cea3d991000",
     * "userId": 37
     * }
     *
     * @param dto 商品id
     * @return 商品 skuDTO
     */
    @PostMapping("/expose/v1")
    @ApiOperation(value = "暴露商品秒杀")
    RestOut<SeckillSkuDTO> expose(@RequestBody SeckillDTO dto) {
        Long skuId = dto.getSeckillSkuId();

        SeckillSkuDTO skuDTO = seckillSkuStockService.detail(skuId);

        if (null == skuDTO) {
            return RestOut.error("未找到指定秒杀商品");
        }

        //初始化秒杀的限流器
        rateLimitService.initLimitKey(
                "seckill",
                String.valueOf(skuId),
                10000000,//总数 SeckillConstants.MAX_ENTER,
                1000// 100/s  SeckillConstants.PER_SECKOND_ENTER
        );


        //暴露秒杀
        skuDTO = seckillSkuStockService.exposeSeckillSku(skuId);

        return RestOut.success(skuDTO).setRespMsg("秒杀开启成功");


    }

}
