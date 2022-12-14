package com.crazymaker.springcloud.kafka.controller.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class SampleMsgDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主题@@true@@false
     */
    private String msgTopic;

    /**
     * 内容@@true@@false
     */
    private String description;

}
