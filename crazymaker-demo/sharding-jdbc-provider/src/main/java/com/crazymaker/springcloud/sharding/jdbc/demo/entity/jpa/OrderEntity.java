/*
 * Copyright 2016-2018 shardingsphere.io.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */

package com.crazymaker.springcloud.sharding.jdbc.demo.entity.jpa;

import com.crazymaker.springcloud.sharding.jdbc.demo.entity.Order;

import javax.persistence.*;

@Entity
@Table(name = "t_order")
public final class OrderEntity extends Order {

    @Id
    @Column(name = "order_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Override
    public long getOrderId() {
        return super.getOrderId();
    }

    @Column(name = "user_id")
    @Override
    public long getUserId() {
        return super.getUserId();
    }

    @Column(name = "status")
    public String getStatus() {
        return super.getStatus();
    }
}
