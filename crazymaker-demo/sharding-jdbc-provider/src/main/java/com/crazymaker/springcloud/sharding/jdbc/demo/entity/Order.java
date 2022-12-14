package com.crazymaker.springcloud.sharding.jdbc.demo.entity;

import java.io.Serializable;

public class Order implements Serializable {

    private static final long serialVersionUID = 661434701950670670L;

    private long orderId;

    private long userId;

    private String status = "NotPayed";

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(final long orderId) {
        this.orderId = orderId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(final long userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return String.format("order_id: %s, user_id: %s, status: %s", orderId, userId, status);
    }
}
