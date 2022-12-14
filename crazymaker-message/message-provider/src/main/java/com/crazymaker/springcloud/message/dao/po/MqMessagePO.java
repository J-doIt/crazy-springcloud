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
@Table(name = "mq_message" )
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MqMessagePO
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
     * topic
     */
    @Column(name = "message_topic" )
    private String messageTopic;


    /**
     * producer
     */
    @Column(name = "producer_code" )
    private String producerCode;


    /**
     * 延时级别 1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h
     */
    @Column(name = "delay_level" )
    private Integer delayLevel;

    /**
     * 顺序类型 0有序 1无序
     */
    @Column(name = "order_type" )
    private Integer orderType = 1;

    /**
     * 消息状态
     */
    @Column(name = "message_status" )
    private Integer messageStatus;

    /**
     * 消息内容
     */
    @Column(name = "message_body" )
    private String messageBody;


    /**
     * 发送次数
     */
    @Column(name = "resend_times" )
    private Integer resendTimes;


    /**
     * 是否删除 -0 未删除 -1 已删除
     */
    private Integer yn = 0;

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