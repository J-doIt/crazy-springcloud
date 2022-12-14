package com.crazymaker.springcloud.sharding.jdbc.demo.entity;

import java.io.Serializable;

public class User implements Serializable {


    private long userId;

    private String name;


    public long getUserId() {
        return userId;
    }

    public void setUserId(final long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return String.format(" user_id: %s, name: %s", userId, name);
    }
}
