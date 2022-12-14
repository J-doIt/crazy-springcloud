package com.crazymaker.springcloud.base.dao.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * @author zlt
 * 用户实体
 */

@Entity
@Table(name = "SYS_USER" )
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SysUserPO implements Serializable
{
    private static final long serialVersionUID = -5886012896705137070L;

    //用户ID
    @Id
    @GenericGenerator(
            name = "snowflakeIdGenerator",
            strategy = "com.crazymaker.springcloud.standard.hibernate.CommonSnowflakeIdGenerator" )
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "snowflakeIdGenerator" )
    @Column(name = "USER_ID", unique = true, nullable = false, length = 8)
    private Long id;


    //创建时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss" )
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8" )
    @Column(name = "CREATE_TIME" )
    private Date createTime;

    //创建时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss" )
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8" )
    @Column(name = "UPDATE_TIME" )
    private Date updateTime;

    @Column(name = "USER_NAME" )
    private String username;
    @Column(name = "PASSWORD" )
    private String password;
    @Column(name = "NICK_NAME" )
    private String nickname;
    @Column(name = "HEAD_IMG_URL" )
    private String headImgUrl;
    @Column(name = "MOBILE" )
    private String mobile;
    @Column(name = "SEX" )
    private Integer sex;
    @Column(name = "ENABLED" )
    private Boolean enabled;
    @Column(name = "TYPE" )
    private String type;
    @Column(name = "OPEN_ID" )
    private String openId;
    @Column(name = "IS_DEL" )
    private Boolean isDel;

}
