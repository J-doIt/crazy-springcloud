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
@Table(name = "mq_confirm" )
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MqConfirmPO
{
    /**
     * ID
     */
    @Id
    @GenericGenerator(name = "generator", strategy = "uuid.hex" )
    @GeneratedValue(generator = "generator" )
    @Column(name = "ID", unique = true, nullable = false, length = 32)
    private String id;

    /**
     * 版本号
     */
    private Integer version;

    /**
     * 消息ID  消息唯一标识
     */
    @Column(name = "message_id" )
    private Long messageId;


    /**
     * 消费者编号
     */
    @Column(name = "consumer_code" )
    private String consumerCode;

    /**
     * 消费的数次
     */
    @Column(name = "consume_count" )
    private Integer consumeCount;

    /**
     * 状态, 10 - 未确认 ; 20 - 已确认; 30 已消费
     */
    private Integer status;

    /**
     * 创建时间
     */
    @Column(name = "created_time" )
    private Date createdTime;

    /**
     * 更新时间
     */
    @Column(name = "update_time" )
    private Date updateTime;


}