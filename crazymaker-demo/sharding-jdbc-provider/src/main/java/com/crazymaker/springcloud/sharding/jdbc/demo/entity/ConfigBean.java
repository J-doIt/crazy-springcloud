package com.crazymaker.springcloud.sharding.jdbc.demo.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class ConfigBean implements Serializable {


    private long id;

    private String status;

}
