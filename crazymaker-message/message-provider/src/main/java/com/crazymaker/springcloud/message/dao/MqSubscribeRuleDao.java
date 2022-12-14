package com.crazymaker.springcloud.message.dao;

import com.crazymaker.springcloud.message.dao.po.MqSubscribeRulePO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by 尼恩 on 2019/7/18.
 */
@Repository
public interface MqSubscribeRuleDao extends JpaRepository<MqSubscribeRulePO, String>, JpaSpecificationExecutor<MqSubscribeRulePO>
{


    /**
     * 根据 topicCode 做查询
     *
     * @param topicCode 编码
     * @return MqSubscribeRulePO 列表
     */

    List<MqSubscribeRulePO> findAllByTopicCode(String topicCode);
}
