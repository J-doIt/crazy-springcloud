package com.crazymaker.springcloud.sharding.jdbc.demo.dao.impl;

import com.crazymaker.springcloud.sharding.jdbc.demo.dao.ConfigRepository;
import com.crazymaker.springcloud.sharding.jdbc.demo.entity.ConfigBean;
import com.crazymaker.springcloud.sharding.jdbc.demo.entity.Page;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public class JpaConfigRepositoryImpl implements ConfigRepository {

    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public Long insert(final ConfigBean configBean) {
        entityManager.persist(configBean);
        return configBean.getId();
    }

    @Override
    public void delete(final Long id) {
        Query query = entityManager.createQuery("DELETE FROM ConfigEntity o WHERE o.id = ?1");
        query.setParameter(1, id);
        query.executeUpdate();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ConfigBean> selectAll() {
        return (List<ConfigBean>) entityManager.createQuery("SELECT o FROM ConfigEntity o").getResultList();
    }

    @Override
    public List<ConfigBean> selectOnePage(Page page) {

        String sql = "SELECT o FROM ConfigEntity o";

        //分页
        List<ConfigBean> lists = entityManager.createQuery(sql)
                .setFirstResult(page.getFirstResult()).setMaxResults(page.getRows())
                .getResultList();
        return lists;

    }
}
