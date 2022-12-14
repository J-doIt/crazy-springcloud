
package com.crazymaker.springcloud.reactive.user.info.service.impl;

import com.crazymaker.springcloud.common.util.BeanUtil;
import com.crazymaker.springcloud.reactive.user.info.dao.impl.JpaUserRepositoryImpl;
import com.crazymaker.springcloud.reactive.user.info.dto.User;
import com.crazymaker.springcloud.reactive.user.info.entity.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
@Transactional
public class JpaEntityServiceImpl
{

    @Resource
    private JpaUserRepositoryImpl userRepository;



    @Transactional
    //增加用户
    public User addUser(User dto)
    {
        User userEntity = new UserEntity();
        userEntity.setUserId(dto.getUserId());
        userEntity.setName(dto.getName());
        userRepository.insert(userEntity);
        BeanUtil.copyProperties(userEntity,dto);
        return dto;
    }

    @Transactional
    //删除用户
    public User delUser(User dto)
    {
          userRepository.delete(dto.getUserId());
          return dto;
    }

    //查询全部用户
    public List<User> selectAllUser()
    {
        log.info("方法 selectAllUser 被调用了");

        return userRepository.selectAll();
    }

    //查询一个用户
    public User selectOne(final Long userId)
    {

        log.info("方法 selectOne 被调用了");

        return userRepository.selectOne(userId);
    }

}
