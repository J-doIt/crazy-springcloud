package com.crazymaker.springcloud.email.repository;

import com.crazymaker.springcloud.email.entity.EmailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 邮件管理
 * 创建时间	2017年9月9日
 */
public interface MailRepository extends JpaRepository<EmailEntity, Integer>
{
	
}
