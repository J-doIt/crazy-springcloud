package com.crazymaker.springcloud.email.service;

import com.crazymaker.springcloud.common.exception.BusinessException;
import com.crazymaker.springcloud.email.common.model.Email;

public interface IMailService {
	 /**
	  * 纯文本

	  * @param mail
	  * @throws Exception  void
	  * @Date	2017年7月20日
	  * 更新日志
	  * 2017年7月20日   首次创建
	  */
	 void send(Email mail) throws BusinessException;
	 /**
	  * 富文本

	  * @param mail
	  * @throws Exception  void
	  * @Date	2017年7月20日
	  * 更新日志
	  * 2017年7月20日   首次创建
	  *
	  */
	  void sendHtml(Email mail) throws Exception;
	 /**
	  * 模版发送 freemarker

	  * @param mail
	  * @throws Exception  void
	  * @Date	2017年7月20日
	  * 更新日志
	  * 2017年7月20日   首次创建
	  *
	  */
	  void sendFreemarker(Email mail) throws Exception;

	 /**
	  * 队列

	  * @param mail
	  * @throws Exception  void
	  * @Date	2017年8月4日
	  * 更新日志
	  * 2017年8月4日   首次创建
	  *
	  */
	 void sendQueue(Email mail) throws Exception;


}
