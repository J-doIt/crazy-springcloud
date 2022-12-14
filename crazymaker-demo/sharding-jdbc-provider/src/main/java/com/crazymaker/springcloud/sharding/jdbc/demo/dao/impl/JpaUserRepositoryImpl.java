package com.crazymaker.springcloud.sharding.jdbc.demo.dao.impl;

import com.crazymaker.springcloud.sharding.jdbc.demo.dao.UserRepository;
import com.crazymaker.springcloud.sharding.jdbc.demo.entity.Page;
import com.crazymaker.springcloud.sharding.jdbc.demo.entity.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public class JpaUserRepositoryImpl implements UserRepository {

    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public Long insert(final User user) {
        entityManager.persist(user);
        return user.getUserId();
    }

    @Override
    public void delete(final Long userId) {
        Query query = entityManager.createQuery("DELETE FROM UserEntity o WHERE o.userId = ?1");
        query.setParameter(1, userId);
        query.executeUpdate();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<User> selectAll() {
        return (List<User>) entityManager.createQuery("SELECT o FROM UserEntity o").getResultList();
    }

    @Override
    public List<User> selectOnePage(Page page) {

        String sql = "SELECT o FROM UserEntity o";

        //分页
        List<User> lists = entityManager.createQuery(sql)
                .setFirstResult(page.getFirstResult()).setMaxResults(page.getRows())
                .getResultList();
        return lists;

    }
}
