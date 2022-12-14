package com.crazymaker.springcloud.message.contract.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * The class Tpc mq message dto.
 *
 * @author paascloud.net @gmail.com
 */
@Data
public class MqSimpleMessageDTO implements Serializable
{

    private static final long serialVersionUID = -6980935654952282538L;


    /**
     * 主题
     */
    private String messageTopic;


    /**
     * 消息内容
     */
    private String messageBody;


}