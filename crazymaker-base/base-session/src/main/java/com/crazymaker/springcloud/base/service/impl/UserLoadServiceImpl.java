package com.crazymaker.springcloud.base.service.impl;

import com.crazymaker.springcloud.base.dao.SysUserDao;
import com.crazymaker.springcloud.base.dao.UserDao;
import com.crazymaker.springcloud.base.dao.po.SysUserPO;
import com.crazymaker.springcloud.base.dao.po.UserPO;
import com.crazymaker.springcloud.common.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

/**
 * Created by 尼恩 on 2019/7/18.
 */

@Slf4j
public class UserLoadServiceImpl
{


    private UserDao userDao;

    private SysUserDao sysUserDao;


    public UserLoadServiceImpl(UserDao userDao, SysUserDao sysUserDao)
    {
        this.userDao = userDao;
        this.sysUserDao = sysUserDao;
    }

    /**
     * 装载前端的用户
     *
     * @param userId
     * @return
     */
    public UserDTO loadFrontEndUser(Long userId)
    {
        UserPO userPO = userDao.findByUserId(userId);
        if (userPO != null)
        {
            UserDTO dto = new UserDTO();

            BeanUtils.copyProperties(userPO, dto);

            return dto;
        }
        return null;
    }

    public UserDTO loadBackEndUser(Long userId)
    {
        SysUserPO userPO = sysUserDao.getOne(userId);
        if (userPO != null)
        {
            UserDTO dto = new UserDTO();

            BeanUtils.copyProperties(userPO, dto);

            return dto;
        }
        return null;
    }


}
