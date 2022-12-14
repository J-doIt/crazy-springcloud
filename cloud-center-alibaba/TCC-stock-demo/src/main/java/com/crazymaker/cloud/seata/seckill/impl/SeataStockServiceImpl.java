package com.crazymaker.cloud.seata.seckill.impl;

import com.crazymaker.springcloud.common.exception.BusinessException;
import com.crazymaker.springcloud.common.page.DataAdapter;
import com.crazymaker.springcloud.common.page.PageOut;
import com.crazymaker.springcloud.common.page.PageReq;
import com.crazymaker.springcloud.seckill.api.dto.SeckillSkuDTO;
import com.crazymaker.springcloud.seckill.dao.SeckillSegmentStockDao;
import com.crazymaker.springcloud.seckill.dao.SeckillSkuDao;
import com.crazymaker.springcloud.seckill.dao.po.SeckillSegmentStockPO;
import com.crazymaker.springcloud.seckill.dao.po.SeckillSkuPO;
import io.seata.rm.tcc.api.BusinessActionContext;
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
import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;


@Configuration
@Slf4j
@Service
public class SeataStockServiceImpl {

    private Map<String, Statement> statementMap = new ConcurrentHashMap<>(100);
    private Map<String, Connection> connectionMap = new ConcurrentHashMap<>(100);
    @Resource
    private DataSource dataSource;



    /**
     * 执行秒杀下单
     *
     * @param inDto
     * @param skuId
     * @return
     */
//    @Transactional
    public boolean minusStock(BusinessActionContext inDto, Long skuId, Long userId) {
        Map<String, Object> params = inDto.getActionContext();

        try {
            log.info("减库存, prepare, xid:{}", inDto.getXid());

            Connection connection = dataSource.getConnection();
            connection.setAutoCommit(false);

            int stock = 0;

            PreparedStatement pstmt = null;
            try {
                pstmt = connection.prepareStatement("SELECT `sku_id` , `stock_count` FROM `seckill_sku` WHERE `sku_id`=?");
                pstmt.setLong(1, skuId);
                ResultSet resultSet = pstmt.executeQuery();
                if (resultSet.next()) {
                    stock = resultSet.getInt("stock_count");
                }
                resultSet.close();
            } finally {
                if (pstmt != null) {
                    pstmt.close();
                }
            }

            if (stock<=0) {
                log.info("减库存, prepare 失败, xid:{}", inDto.getXid());

                if (null != connection) {
                    connection.close();
                    connection.commit();
                }
                throw BusinessException.builder().errMsg("库存不够").build();
            }


            String sql = "UPDATE `seckill_sku` SET  `stock_count` = `stock_count` -1 WHERE `sku_id` = ?;";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setLong(1, skuId);
            stmt.executeUpdate();
            statementMap.put(inDto.getXid(), stmt);
            connectionMap.put(inDto.getXid(), connection);

        } catch (SQLException e) {
            log.error("库存失败:", e);
            return false;
        }
        return true;


    }

    public boolean commit(BusinessActionContext dto) {

        String xid = dto.getXid();
        log.info("减库存, commit, xid:{}", xid);
        PreparedStatement statement = (PreparedStatement) statementMap.get(xid);
        Connection connection = connectionMap.get(xid);
        try {
            //判断一下，具备幂等性
            if (null != connection) {
                connection.commit();
            }
        } catch (SQLException e) {
            log.error("提交失败:", e);
            return false;
        } finally {
            try {
                statementMap.remove(xid);
                connectionMap.remove(xid);
                if (null != statement) {
                    statement.close();
                }
                if (null != connection) {
                    connection.close();
                }
            } catch (SQLException e) {
                log.error("减库存回滚事务后归还连接失败:", e);
            }
        }
        return true;
    }


    public boolean rollback(BusinessActionContext dto) {
        String xid = dto.getXid();
        log.info("减库存, rollback, xid:{}", xid);
        PreparedStatement statement = (PreparedStatement) statementMap.get(xid);
        Connection connection = connectionMap.get(xid);
        try {
            //防止空回滚
            if (null != connection) {
                connection.rollback();
            }
        } catch (SQLException e) {
            log.error("回滚失败:", e);
            return false;
        } finally {
            try {
                statementMap.remove(xid);
                connectionMap.remove(xid);
                if (null != statement) {
                    statement.close();
                }
                if (null != connection) {
                    connection.close();
                }
            } catch (SQLException e) {
                log.error("减库存提交事务后归还连接池失败:", e);
            }
        }
        return true;
    }

}
