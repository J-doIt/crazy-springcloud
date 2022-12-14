package com.crazymaker.cloud.seata.seckill.service.impl;

import com.crazymaker.springcloud.common.distribute.idGenerator.IdGenerator;
import com.crazymaker.springcloud.common.exception.BusinessException;
import com.crazymaker.springcloud.seckill.dao.SeckillOrderDao;
import com.crazymaker.springcloud.standard.hibernate.CommonSnowflakeIdGenerator;
import io.seata.rm.tcc.api.BusinessActionContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class TCCOrderServiceImpl {

    private Map<String, Statement> statementMap = new ConcurrentHashMap<>(100);
    private Map<String, Connection> connectionMap = new ConcurrentHashMap<>(100);
    @Resource
    private DataSource dataSource;

    private IdGenerator idGenerator;

    public IdGenerator getIdGenerator() {
        if (null == idGenerator) {
            idGenerator = CommonSnowflakeIdGenerator.getFromMap("tcc_order");
        }
        return idGenerator;
    }

    /**
     * 执行秒杀下单
     *
     * @param inDto
     * @return
     */
//    @Transactional //开启本地事务
    // @GlobalTransactional//不，开启全局事务（重点） 使用 seata 的全局事务
    public boolean addOrder(BusinessActionContext inDto, Long skuId, Long userId) {

        Map<String, Object> params = inDto.getActionContext();

//        long skuId = (Long) params.get("sku");
//        Long userId = (Long) params.get("user");
        Long id = getIdGenerator().nextId();
        try {
            Connection connection = dataSource.getConnection();
            connection.setAutoCommit(false);


            boolean isExist = false;
            log.info("检查是否已经下单过");
            PreparedStatement pstmt = null;
            try {
                pstmt = connection.prepareStatement("SELECT * FROM `seckill_order` WHERE `user_id` =?");
                pstmt.setLong(1, userId);
                ResultSet resultSet = pstmt.executeQuery();
                if (resultSet.next()) {
                    isExist = true;
                }
                resultSet.close();
            } finally {
                if (pstmt != null) {
                    pstmt.close();
                }
            }

            if (isExist) {
                log.info("已经下单过");

                if (null != connection) {
                 try {
                     connection.close();
                     connection.commit();
                 }catch (Throwable t)
                 {

                 }

                }
                throw BusinessException.builder().errMsg("已经秒杀过了").build();
            }
            log.info("pass:  检查是否已经下单过");

            String sql = "INSERT INTO `seckill_order`(`order_id`, `sku_id`, `status`, `user_id`)  VALUES( ?, ?, 1, ?)";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setLong(1, id);
            stmt.setLong(2, skuId);
            stmt.setLong(3, userId);
            stmt.executeUpdate();
            statementMap.put(inDto.getXid(), stmt);
            connectionMap.put(inDto.getXid(), connection);

            log.info("prepare  下单 完成");

            return true;
        } catch (SQLException e) {
            log.error("保存订单失败:", e);
            return false;
        }
    }

    public boolean commitAddOrder(BusinessActionContext dto) {
        String xid = dto.getXid();
        log.info("提交 下订单, commit, xid:{}", xid);
        PreparedStatement statement = (PreparedStatement) statementMap.get(xid);
        Connection connection = connectionMap.get(xid);
        try {
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
                log.error("下订单提交事务后归还连接池失败:", e);
            }
        }
        return true;
    }

    public boolean rollbackAddOrder(BusinessActionContext dto) {
        String xid = dto.getXid();
        log.info("回滚 下订单, rollback, xid:{}", xid);
        PreparedStatement statement = (PreparedStatement) statementMap.get(xid);
        Connection connection = connectionMap.get(xid);
        try {
            //判断一下，防止空悬挂，具备幂等性
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
                log.error("下订单回滚事务后归还连接失败:", e);
            }
        }
        return true;
    }
}
