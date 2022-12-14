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
@Table(name = "mq_group" )
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MqGroupPO
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
     * group 编码
     */
    @Column(name = "group_code" )
    private String groupCode;


    /**
     * 组的名称
     */
    @Column(name = "group_name" )
    private String groupName;


    /**
     * 状态, 0生效,10,失效
     */
    @Column(name = "group_status" )
    private Integer status;

    /**
     * 备注
     */
    @Column(name = "group_remarks" )
    private String remarks;


    /**
     * 创建时间
     */
    @Column(name = "created_time" )
    private Date createdTime;
}