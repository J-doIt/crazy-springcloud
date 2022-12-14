package com.crazymaker.springcloud.kafka.controller.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class NewTopicDto implements Serializable {
    private String name;
    private int numPartitions;
    private short replicationFactor;
}