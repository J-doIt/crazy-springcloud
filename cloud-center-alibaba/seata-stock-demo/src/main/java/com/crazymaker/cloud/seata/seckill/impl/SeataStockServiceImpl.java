package com.crazymaker.cloud.seata.seckill.impl;

import com.crazymaker.springcloud.common.exception.BusinessException;
import com.crazymaker.springcloud.common.page.DataAdapter;
import com.crazymaker.springcloud.common.page.PageOut;
import com.crazymaker.springcloud.common.page.PageReq;
import com.crazymaker.springcloud.seckill.api.dto.SeckillDTO;
import com.crazymaker.springcloud.seckill.api.dto.SeckillSkuDTO;
import com.crazymaker.springcloud.seckill.dao.SeckillSegmentStockDao;
import com.crazymaker.springcloud.seckill.dao.SeckillSkuDao;
import com.crazymaker.springcloud.seckill.dao.po.SeckillSegmentStockPO;
import com.crazymaker.springcloud.seckill.dao.po.SeckillSkuPO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@Configuration
@Slf4j
@Service
public class SeataStockServiceImpl {

    @Resource
    SeckillSkuDao seckillSkuDao;

    @Resource
    SeckillSegmentStockDao seckillSegmentStockDao;


    /**
     * 获取所有的秒杀商品列表
     *
     * @param pageReq 当前页 ，从1 开始,和 页的元素个数
     * @return
     */
    public PageOut<SeckillSkuDTO> findAll(PageReq pageReq) {
        Example<SeckillSkuPO> example = Example.of(new SeckillSkuPO());
        Page<SeckillSkuPO> page = seckillSkuDao.findAll(example, PageRequest.of(pageReq.getJpaPage(), pageReq.getPageSize()));

        PageOut<SeckillSkuDTO> pageData = DataAdapter.adapterPage(page, SeckillSkuDTO.class);

        return pageData;

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
    public SeckillSkuDTO addSeckillSku(String title, int stockCount,
                                       float price, float costPrice) {
        //获取系统时间
        Date nowTime = new Date();


        SeckillSkuPO po = new SeckillSkuPO();
        po.setCostPrice(BigDecimal.valueOf(costPrice));
        po.setPrice(BigDecimal.valueOf(price));
        po.setTitle(title);
        po.setStockCount(stockCount);
        po.setRawStockCount(stockCount);

        po.setCreateTime(nowTime);
        po.setStartTime(DateUtils.addMonths(nowTime, -1));
        po.setEndTime(DateUtils.addMonths(nowTime, 1));

        seckillSkuDao.saveAndFlush(po);
        SeckillSkuDTO dto = new SeckillSkuDTO();
        BeanUtils.copyProperties(po, dto);

        return dto;
    }

    //初始化分段库存
    public void initSegmentStockAmount(Long skuId, int stockAmount, int segment) {
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

    private Specification<SeckillSkuPO> getValidSpecification() {
        return new Specification<SeckillSkuPO>() {
            @Override
            public Predicate toPredicate(Root<SeckillSkuPO> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                //获取系统时间
                Date nowTime = new Date();
                List<Predicate> predicates = new ArrayList<>();
                predicates.add(cb.greaterThanOrEqualTo(root.get("endTime"), nowTime));
                predicates.add(cb.lessThanOrEqualTo(root.get("startTime"), nowTime));
                predicates.add(cb.greaterThan(root.get("stockCount"), 0));

                // and到一起的话所有条件就是且关系，or就是或关系
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }

    //   @CacheEvict(cacheNames = {"seckill"}, key = "'seckillsku:' + #skuId")
    @Transactional
    public SeckillSkuDTO setNewStock(Long skuId, Integer stock) {
        Optional<SeckillSkuPO> optional = seckillSkuDao.findById(skuId);

        if (optional.isPresent()) {
            SeckillSkuPO po = optional.get();
            po.setStockCount(stock);
            po.setRawStockCount(stock);
            seckillSkuDao.save(po);


            SeckillSkuDTO dto = new SeckillSkuDTO();

            BeanUtils.copyProperties(po, dto);
            return dto;
        }
        return null;
    }


    @Transactional
    public SeckillSkuDTO decreaseStockCountById(Long skuId) {
        Optional<SeckillSkuPO> optional = seckillSkuDao.findById(skuId);

        if (optional.isPresent()) {
            SeckillSkuPO po = optional.get();
            po.setStockCount(po.getStockCount() - 1);
            seckillSkuDao.decreaseStockCountById(po.getId());

            SeckillSkuDTO dto = new SeckillSkuDTO();
            BeanUtils.copyProperties(po, dto);
            return dto;
        }
        return null;
    }


    /**
     * 执行秒杀下单
     *
     * @param inDto
     * @return
     */
    @Transactional
    public SeckillSkuDTO minusStock(SeckillDTO inDto) {

        long skuId = inDto.getSeckillSkuId();


        Optional<SeckillSkuPO> optional = seckillSkuDao.findById(skuId);

        if (!optional.isPresent()) {
            throw BusinessException.builder().errMsg("商品不存在").build();
        }

        SeckillSkuPO po = optional.get();
        if (po.getStockCount() <= 0) {
            throw BusinessException.builder().errMsg("库存不够").build();
        }
        seckillSkuDao.decreaseStockCountById(skuId);
//        po.setStockCount(po.getStockCount() - 1);
        SeckillSkuDTO dto = new SeckillSkuDTO();
        dto.setStockCount(po.getStockCount() - 1);
        BeanUtils.copyProperties(po, dto);
        return dto;


    }


}
