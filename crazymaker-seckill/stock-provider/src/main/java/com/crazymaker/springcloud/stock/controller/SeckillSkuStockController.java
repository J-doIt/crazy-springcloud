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
@Api(tags = "εεεΊε­")
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
     * ηΌε­ζ°ζ?ζδ½η±»
     */
    @Resource
    RedisRepository redisRepository;


    /**
     * redis εεΈεΌιε?η°η±»
     */
    @Autowired
    RedisLockService redisLockService;


    /**
     * θ·εζζηη§ζεεεθ‘¨
     *
     * @param pageReq ε½ει‘΅ οΌδ»1 εΌε§,ε ι‘΅ηεη΄ δΈͺζ°
     * @return
     */
    @PostMapping("/list/v1")
    @ApiOperation(value = "ε¨ι¨η§ζεε")
    RestOut<PageOut<SeckillSkuDTO>> findAll(@RequestBody PageReq pageReq) {
        PageOut<SeckillSkuDTO> page = seckillSkuStockService.findAll(pageReq);
        RestOut<PageOut<SeckillSkuDTO>> r = RestOut.success(page);
        return r;

    }

    /**
     * ζ₯θ―’εεδΏ‘ζ―
     *
     * @param dto εεid
     * @return εε skuDTO
     */
    @PostMapping("/detail/v1")
    @ApiOperation(value = "η§ζεεθ―¦ζ")
    RestOut<SeckillSkuDTO> skuDetail(HttpServletRequest request, @RequestBody SeckillDTO dto) {


        if (StringUtils.isEmpty(request.getHeader(USER_IDENTIFIER))) {
            return RestOut.error("θ―·η»ε½εζ₯");
        }

        Long userId = Long.valueOf(request.getHeader(USER_IDENTIFIER));
        Boolean isUserBack = redisRepository.getBit(BLANK_USE_CACHE_PRIFIX, userId);
        if (isUserBack) {
            return RestOut.error("ζ­€ζ¬‘θ―·ζ±οΌιζ³οΌθ―·θη³»η?‘ηεθ§£ι€ιεΆ");
        }

        //εηΌε­ζε³ηδΈδΈͺει, ζε₯½ζ―ζΎε¨ζ₯ε₯ε±
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

                return RestOut.error("ζ­€ζ¬‘θ―·ζ±οΌιζ³οΌθ―·θη³»η?‘ηεθ§£ι€ιεΆ");
            }
            cacheTimeOfUserAccess = (int) redisRepository.getKeyExpireTime(cacheKeyOfUserAccess);
            redisRepository.setExpire(cacheKeyOfUserAccess, accessCount, cacheTimeOfUserAccess);
//            redisRepository.setObject(cacheKeyOfUserAccess, accessCount);

        }

        Long skuId = dto.getSeckillSkuId();

        //εηΌε­ζε³ηδΈδΈͺει
        String cacheKey = SECKILLSKU_CACHE_PRIFIX + skuId;
        int cacheTime = 2* 60 * 60;


        String skuCache = redisRepository.getStr(cacheKey);


        SeckillSkuDTO skuDTO = null;
        if (StringUtils.isNoneEmpty(skuCache)) {
            skuDTO = SeckillDTO.fromJson(skuCache);
            if (null == skuDTO)
                return RestOut.error("ζͺζΎε°ζε?η§ζεε");
            else
                return RestOut.success(skuDTO).setRespMsg("ζ₯ζΎζε");
        }


        //δ½Ώη¨redis εεΈεΌιοΌθ§£ε³ηΌε­ε»η©Ώ

        String lockKey = SKU_CACHE_PUT_Lock_PRIFIX + skuId;

        String requestId = uuid();

        Lock lock = redisLockService.getLock(lockKey, requestId);
        boolean locked = false;
        try {
            locked = lock.tryLock(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            lock.unlock();
            e.printStackTrace();
            return RestOut.error("η§ζεεθ?Ώι?ε€ͺηΉεΏ");
        }
        if (locked) {

            try {

                skuCache = redisRepository.getStr(cacheKey);

                if (StringUtils.isNoneEmpty(skuCache)) {
                    skuDTO = SeckillDTO.fromJson(skuCache);
                    if (null == skuDTO)
                        return RestOut.error("ζͺζΎε°ζε?η§ζεε");
                    else
                        return RestOut.success(skuDTO).setRespMsg("ζ₯ζΎζε");
                }


                skuDTO = seckillSkuStockService.detail(skuId);

                skuCache = SeckillDTO.toJson(skuDTO);

                //θ§£ε³ι²ζ­’εζΆε€§ιηkeyθΏζ

                int finalCacheTime = cacheTime + RandomUtil.randInMod(30 * 60);

                redisRepository.set(cacheKey, skuCache, finalCacheTime);


                if (null != skuDTO) {

                    return RestOut.success(skuDTO).setRespMsg("ζ₯ζΎζε");
                }

            } finally {
                lock.unlock();
            }

        }


        return RestOut.error("ζͺζΎε°ζε?η§ζεε");
    }

    /**
     * ε ι€εεδΏ‘ζ―
     *
     * @param dto εεid
     * @return εε skuDTO
     */
    @PostMapping("/delete/v1")
    @ApiOperation(value = "ε ι€εεδΏ‘ζ―")
    RestOut<SeckillSkuDTO> deleteSku(@RequestBody SeckillDTO dto) {
        Long skuId = dto.getSeckillSkuId();

        seckillSkuStockService.delete(skuId);


        return RestOut.error("ε ι€εεδΏ‘ζ― ok");
    }


    /**
     * θ?Ύη½?η§ζεΊε­
     *
     * @param dto εεδΈεΊε­
     * @return εε skuDTO
     */
    @PutMapping("/stock/v1")
    @ApiOperation(value = "θ?Ύη½?η§ζεΊε­")
    RestOut<SeckillSkuDTO> setStock(@RequestBody SeckillDTO dto) {
        Long skuId = dto.getSeckillSkuId();
        int stock = dto.getNewStockNum();

        SeckillSkuDTO skuDTO = seckillSkuStockService.setNewStock(skuId, stock);

        if (null != skuDTO) {
            return RestOut.success(skuDTO).setRespMsg("θ?Ύη½?η§ζεΊε­ζε");
        }
        return RestOut.error("ζͺζΎε°ζε?η§ζεε");
    }


    /**
     * ε’ε η§ζηεε
     *
     * @param stockCount εΊε­
     * @param title      ζ ι’
     * @param price      εεεδ»·ζ Ό
     * @param costPrice  δ»·ζ Ό
     * @return
     */
    @PostMapping("/add/v1")
    @ApiOperation(value = "ε’ε η§ζεε")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "title", value = "εεεη§°", dataType = "string", paramType = "query", required = true, defaultValue = "η§ζεε-1"),
            @ApiImplicitParam(name = "stockCount", value = "η§ζζ°ι", dataType = "int", paramType = "query", required = true, defaultValue = "10000", example = "10000"),
            @ApiImplicitParam(name = "price", value = "εε§δ»·ζ Ό", dataType = "float", paramType = "query", required = true, defaultValue = "1000", example = "1000"),
            @ApiImplicitParam(name = "costPrice", value = "η§ζδ»·ζ Ό", dataType = "float", paramType = "query", required = true, defaultValue = "10", example = "1000")
    })
    RestOut<SeckillSkuDTO> addSeckill(
            @RequestParam(value = "title", required = true) String title,
            @RequestParam(value = "stockCount", required = true) int stockCount,
            @RequestParam(value = "price", required = true) float price,
            @RequestParam(value = "costPrice", required = true) float costPrice) {
        SeckillSkuDTO dto = seckillSkuStockService.addSeckillSku(title, stockCount, price, costPrice);
        return RestOut.success(dto).setRespMsg("ε’ε η§ζηεεζε");

    }


    /**
     * ζ΄ι²εεη§ζ
     * <p>
     * {
     * "exposedKey": "4b70903f6e1aa87788d3ea962f8b2f0e",
     * "newStockNum": 10000,
     * "seckillSkuId": 1157197244718385152,
     * "seckillToken": "0f8459cbae1748c7b14e4cea3d991000",
     * "userId": 37
     * }
     *
     * @param dto εεid
     * @return εε skuDTO
     */
    @PostMapping("/expose/v1")
    @ApiOperation(value = "ζ΄ι²εεη§ζ")
    RestOut<SeckillSkuDTO> expose(@RequestBody SeckillDTO dto) {
        Long skuId = dto.getSeckillSkuId();

        SeckillSkuDTO skuDTO = seckillSkuStockService.detail(skuId);

        if (null == skuDTO) {
            return RestOut.error("ζͺζΎε°ζε?η§ζεε");
        }

        //εε§εη§ζηιζ΅ε¨
        rateLimitService.initLimitKey(
                "seckill",
                String.valueOf(skuId),
                10000000,//ζ»ζ° SeckillConstants.MAX_ENTER,
                1000// 100/s  SeckillConstants.PER_SECKOND_ENTER
        );


        //ζ΄ι²η§ζ
        skuDTO = seckillSkuStockService.exposeSeckillSku(skuId);

        return RestOut.success(skuDTO).setRespMsg("η§ζεΌε―ζε");


    }

}
