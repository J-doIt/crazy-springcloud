package com.crazymaker.springcloud.message.dao;

import com.crazymaker.springcloud.message.dao.po.MqMessagePO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Created by 尼恩 on 2019/7/18.
 */
public interface MqMessageDao extends JpaRepository<MqMessagePO, String>, JpaSpecificationExecutor<MqMessagePO>
{


}
