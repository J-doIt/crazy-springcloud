package com.crazymaker.springcloud.reactive.user.info.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
@ApiModel(value = "User",description = "用户")
public class User implements Serializable
{

    @ApiModelProperty("用户ID")
    private long userId;

    @ApiModelProperty("用户名称")
    private String name;


    public long getUserId()
    {
        return userId;
    }

    public void setUserId(final long userId)
    {
        this.userId = userId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    @Override
    public String toString()
    {
        return String.format(" user_id: %s, name: %s", userId, name);
    }
}
