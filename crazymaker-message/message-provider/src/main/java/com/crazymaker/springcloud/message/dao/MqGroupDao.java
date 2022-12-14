package com.crazymaker.springcloud.message.dao;

import com.crazymaker.springcloud.message.dao.po.MqGroupPO;
import com.crazymaker.springcloud.message.dao.po.MqTopicPO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by 尼恩 on 2019/7/18.
 */
@Repository
public interface MqGroupDao extends JpaRepository<MqTopicPO, String>, JpaSpecificationExecutor<MqTopicPO>
{


    /**
     * 根据 topicCode 做查询
     *
     * @param topicCode 编码
     * @return MqGroupPO 列表
     */

    List<MqGroupPO> findAllByTopicCode(String topicCode);
}
