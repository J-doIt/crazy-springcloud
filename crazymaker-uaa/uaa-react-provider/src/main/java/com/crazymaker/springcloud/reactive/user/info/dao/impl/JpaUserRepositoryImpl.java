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

package com.crazymaker.springcloud.reactive.user.info.dao.impl;

import com.crazymaker.springcloud.reactive.user.info.dto.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public class JpaUserRepositoryImpl
{

    @PersistenceContext
    private EntityManager entityManager;


    public Long insert(final User user)
    {
        entityManager.persist(user);
        return user.getUserId();
    }

    public void delete(final Long userId)
    {
        Query query = entityManager.createQuery("DELETE FROM UserEntity o WHERE o.userId = ?1");
        query.setParameter(1, userId);
        query.executeUpdate();
    }

    @SuppressWarnings("unchecked")
    public List<User> selectAll()
    {
        return (List<User>) entityManager.createQuery("SELECT o FROM UserEntity o").getResultList();
    }

    @SuppressWarnings("unchecked")
    public User selectOne(final Long userId)
    {
        Query query = entityManager.createQuery("SELECT o FROM UserEntity o WHERE o.userId = ?1");
        query.setParameter(1, userId);
        return (User) query.getSingleResult();
    }
}
