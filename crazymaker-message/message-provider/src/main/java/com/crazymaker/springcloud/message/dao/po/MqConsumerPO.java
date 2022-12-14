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

@Entity
@Table(name = "mq_consumer" )
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MqConsumerPO
{

    private static final long serialVersionUID = 9104188440392558541L;

    /**
     * ID
     */
    @Id
    @GenericGenerator(name = "generator", strategy = "uuid.hex" )
    @GeneratedValue(generator = "generator" )
    @Column(name = "ID", unique = true, nullable = false, length = 32)
    private String id;

    /**
     * 消费者编号
     */
    @Column(name = "consumer_code" )
    private String consumerCode;


    /**
     * 消费者名称
     */
    @Column(name = "consumer_name" )
    private String consumerName;


    /**
     * 状态, 0生效,10,失效
     */
    @Column(name = "consumer_status" )
    private Integer status;

    /**
     * 备注
     */
    @Column(name = "consumer_remarks" )
    private String remarks;
}