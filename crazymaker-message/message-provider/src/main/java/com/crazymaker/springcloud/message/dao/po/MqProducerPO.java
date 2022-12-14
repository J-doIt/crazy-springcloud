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
@Table(name = "mq_producer" )
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MqProducerPO
{
    private static final long serialVersionUID = -4064061704648362318L;

    /**
     * ID
     */
    @Id
    @GenericGenerator(name = "generator", strategy = "uuid.hex" )
    @GeneratedValue(generator = "generator" )
    @Column(name = "ID", unique = true, nullable = false, length = 32)
    private String id;

    /**
     * 生产者名称
     */
    @Column(name = "producer_name" )
    private String producerName;

    /**
     * 生产者编号
     */
    @Column(name = "producer_code" )
    private String producerCode;


    /**
     * 状态, 0生效,10,失效
     */
    @Column(name = "producer_status" )
    private Integer status;

    /**
     * 备注
     */
    @Column(name = "producer_remarks" )
    private String remarks;
}