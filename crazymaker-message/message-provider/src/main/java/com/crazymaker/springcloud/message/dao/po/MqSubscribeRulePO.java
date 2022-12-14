package com.crazymaker.springcloud.message.dao.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;


@Entity
@Table(name = "mq_subscribe_rule" )
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MqSubscribeRulePO
{

    private static final long serialVersionUID = 6227704457895628954L;

    /**
     * ID
     */
    @Id
    @GenericGenerator(name = "generator", strategy = "uuid.hex" )
    @GeneratedValue(generator = "generator" )
    @Column(name = "ID", unique = true, nullable = false, length = 32)
    private String id;

    /**
     * 消费者 编码
     */
    @Column(name = "subscribe_rule_name" )
    private String subscribeRuleName;


    /**
     * 组的编码
     */
    @Column(name = "group_code" )
    private String groupCode;

    /**
     * TOPIC 编码
     */
    @Column(name = "topic_code" )
    private String topicCode;

    /**
     * 状态, 0生效,10,失效
     */
    @Column(name = "rule_status" )
    private Integer status;

    /**
     * 备注
     */
    @Column(name = "rule_remarks" )
    private String remarks;


    /**
     * 创建时间
     */
    @Column(name = "created_time" )
    private Date createdTime;
}