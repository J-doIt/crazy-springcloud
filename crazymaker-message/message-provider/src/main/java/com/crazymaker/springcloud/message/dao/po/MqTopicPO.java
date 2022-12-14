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
@Table(name = "mq_topic" )
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MqTopicPO
{
    private static final long serialVersionUID = 5642946024630652202L;

    /**
     * ID
     */
    @Id
    @GenericGenerator(name = "generator", strategy = "uuid.hex" )
    @GeneratedValue(generator = "generator" )
    @Column(name = "ID", unique = true, nullable = false, length = 32)
    private String id;

    /**
     * 主题编码
     */
    @Column(name = "topic_code" )
    private String topicCode;

    /**
     * 主题名称
     */
    @Column(name = "topic_name" )
    private String topicName;

    /**
     * MQ类型, 10 rocketmq 20 kafka
     */
    @Column(name = "mq_type" )
    private Integer mqType;

    /**
     * 消息类型, 10 无序消息, 20 无序消息
     */
    @Column(name = "msg_type" )
    private Integer msgType;

    /**
     * 状态, 0生效,10,失效
     */
    @Column(name = "topic_status" )
    private Integer status;

    /**
     * 备注
     */
    @Column(name = "topic_remarks" )
    private String remarks;


    /**
     * 创建时间
     */
    @Column(name = "created_time" )
    private Date createdTime;
}