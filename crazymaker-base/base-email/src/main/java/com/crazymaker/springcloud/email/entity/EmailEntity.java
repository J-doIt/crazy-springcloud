package com.crazymaker.springcloud.email.entity;

import com.crazymaker.springcloud.email.common.model.Email;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;

@Entity
@Table(name = "foundation_email")
public class EmailEntity implements Serializable
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false)
    public Long getId()
    {
        return id;
    }

    private static final long serialVersionUID = 1L;

    /**
     * 自增主键
     */
    private Long id;

    /**
     * 接收人邮箱(多个逗号分开)
     */
    private String receiveEmail;

    /**
     * 主题
     */
    private String subject;

    /**
     * 发送内容
     */
    private String content;

    /**
     * 模板
     */
    private String template;

    /**
     * 发送时间
     */
    private Timestamp sendTime;


    public EmailEntity()
    {
        super();
    }

    public EmailEntity(Email mail)
    {
        this.receiveEmail = Arrays.toString(mail.getEmail());
        this.subject = mail.getSubject();
        this.content = mail.getContent();
        this.template = mail.getTemplate();
        this.sendTime = new Timestamp(new Date().getTime());
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public void setReceiveEmail(String receiveEmail)
    {
        this.receiveEmail = receiveEmail;
    }

    @Column(name = "receive_email", nullable = false, length = 500)
    public String getReceiveEmail()
    {
        return receiveEmail;
    }

    public void setSubject(String subject)
    {
        this.subject = subject;
    }

    @Column(name = "subject", nullable = false, length = 100)
    public String getSubject()
    {
        return subject;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    @Column(name = "content", nullable = false, length = 65535, columnDefinition = "TEXT")
    public String getContent()
    {
        return content;
    }

    public void setTemplate(String template)
    {
        this.template = template;
    }

    @Column(name = "template", nullable = false, length = 100)
    public String getTemplate()
    {
        return template;
    }

    public void setSendTime(Timestamp sendTime)
    {
        this.sendTime = sendTime;
    }

    @Column(name = "send_time", nullable = false, length = 19)
    public Timestamp getSendTime()
    {
        return sendTime;
    }

}
