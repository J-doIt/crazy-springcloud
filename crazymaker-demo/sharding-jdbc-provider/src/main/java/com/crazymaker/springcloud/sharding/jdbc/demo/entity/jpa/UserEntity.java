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

import com.crazymaker.springcloud.sharding.jdbc.demo.entity.User;

import javax.persistence.*;

@Entity
@Table(name = "t_user")
public final class UserEntity extends User {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Override
    public long getUserId() {
        return super.getUserId();
    }

    @Column(name = "name")
    public String getName() {
        return super.getName();
    }
}
