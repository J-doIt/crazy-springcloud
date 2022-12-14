package com.crazymaker.springcloud.seckill.service;

import com.crazymaker.springcloud.seckill.dao.SeckillSegmentStockDao;
import com.crazymaker.springcloud.seckill.dao.po.SeckillSegmentStockPO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class TestSkuStockService {

    @Resource
    SeckillSegmentStockDao seckillSegmentStockDao;

    public void initStockAmount(Long skuId, int stockAmount, int segment) {
        int avRawStockCount = stockAmount / segment;
        for (int i = 0; i < segment; i++) {
            SeckillSegmentStockPO po = new SeckillSegmentStockPO();
            po.setSkuId(skuId);
            po.setSegmentIndex(i);
            po.setRawStockCount(avRawStockCount);
            //最后一个
            if (i == segment - 1) {
                po.setRawStockCount(avRawStockCount + stockAmount - avRawStockCount * segment);
            }
            po.setStockCount(po.getRawStockCount());
            seckillSegmentStockDao.save(po);
        }

    }

    public void decreaseStock(Long skuId, int segment) {
        seckillSegmentStockDao.decreaseStock(skuId, segment);
    }

    public int sumStockCountById(Long skuId) {
        return seckillSegmentStockDao.sumStockCountById(skuId);
    }
}
