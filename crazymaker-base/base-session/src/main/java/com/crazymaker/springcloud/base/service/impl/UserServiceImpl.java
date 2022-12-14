package com.crazymaker.springcloud.base.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.crazymaker.springcloud.base.dao.UserDao;
import com.crazymaker.springcloud.base.dao.po.UserPO;
import com.crazymaker.springcloud.base.security.utils.AuthUtils;
import com.crazymaker.springcloud.common.dto.UserDTO;
import com.crazymaker.springcloud.common.exception.BusinessException;
import com.crazymaker.springcloud.common.util.JsonUtil;
import com.crazymaker.springcloud.standard.redis.RedisRepository;
import com.crazymaker.springcloud.user.info.api.dto.LoginInfoDTO;
import com.crazymaker.springcloud.user.info.api.dto.LoginOutDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.session.Session;
import org.springframework.session.data.redis.RedisOperationsSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.crazymaker.springcloud.common.context.SessionHolder.G_USER;

@Slf4j
@Service
public class UserServiceImpl
{

    //Dao Bean ，用于查询数据库用户
    @Resource
    UserDao userDao;

    //加密器
    @Resource
    private PasswordEncoder passwordEncoder;

    //缓存操作服务
    @Resource
    RedisRepository redisRepository;

    //redis 会话存储服务
    @Resource
    private RedisOperationsSessionRepository sessionRepository;


    public UserDTO getUser(Long id)
    {
        UserPO userPO = userDao.findByUserId(id);
        if (userPO != null)
        {
            UserDTO userDTO = new UserDTO();
            BeanUtils.copyProperties(userPO, userDTO);
            return userDTO;
        }

        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    public void save(UserPO user)
    {
        userDao.save(user);
    }


    @Transactional(rollbackFor = Exception.class)
    public void add(UserDTO req)
    {
        UserPO user = new UserPO();
        BeanUtils.copyProperties(req, user);

        userDao.save(user);
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id)
    {
        Optional<UserPO> optional = userDao.findById(id);
        if (!optional.isPresent())
        {
            throw new BusinessException("删除对象不存在");
        }
        UserPO user = optional.get();
        userDao.delete(user);
    }

    /**
     * 登陆 处理
     *
     * @param dto 用户名、密码
     * @return 登录成功的 dto
     */
    public LoginOutDTO login(LoginInfoDTO dto)
    {
        String username = dto.getUsername();

        //从数据库获取用户
        List<UserPO> list = userDao.findAllByUsername(username);

        if (null == list || list.size() <= 0)
        {
            throw BusinessException.builder().errMsg("用户名或者密码错误").build();
        }
        UserPO userPO = list.get(0);

        //进行密码的验证  test/123456
        //String encode = passwordEncoder.encode(dto.getPassword());
        String encoded = userPO.getPassword();
        String presentPassword = dto.getPassword();
        boolean matched = passwordEncoder.matches(presentPassword, encoded);
        if (!matched)
        {
            throw BusinessException.builder().errMsg("用户名或者密码错误").build();
        }


        //设置 session，方便SpringSecurity 进行权限验证
        return setSession(userPO);

    }

    /**
     * 1: 将 userid -> session id 作为键值对缓存起来, 防止频繁创建 session
     * 2: 将用户信息保存到分布式 session ，
     * 3：创建 JWT token , 提供给 SpringSecurity 进行权限验证
     *
     * @param userPO 用户信息
     * @return 登录的输出信息
     */
    private LoginOutDTO setSession(UserPO userPO)
    {
        if (null == userPO)
        {
            throw BusinessException.builder().errMsg("用户不存在或者密码错误").build();
        }

        /**
         *  将TOKEN保存到数据库或者缓存中
         */

        /**
         *  根据用户 id，查询之前保持的 session id，
         *  防止频繁登录的时候，session  被大量创建
         */
        String uid = String.valueOf(userPO.getUserId());
        String sid = redisRepository.getSessionId(uid);


        Session session = null;

        try
        {
            /**
             * 查找现有的session
             */
            session = sessionRepository.findById(sid);
        } catch (Exception e)
        {
            // e.printStackTrace();
            log.info("查找现有的session 失败，将创建一个新的 session");
        }

        if (null == session)
        {
            session = sessionRepository.createSession();
            //新的 session id，和用户 id一起作为 k-v 键值对进行保存
            //用户访问的时候，可以根据 用户 id 查找 session id
            sid = session.getId();
            redisRepository.setSessionId(uid, sid);
        }


        String salt = userPO.getPassword();

        // 构建JWT token
        String token = AuthUtils.buildToken(sid, salt);

        /**
         * 将用户信息缓存起来
         */
        UserDTO cacheDto = new UserDTO();
        BeanUtils.copyProperties(userPO, cacheDto);
        cacheDto.setToken(token);
        session.setAttribute(G_USER, JsonUtil.pojoToJson(cacheDto));

        LoginOutDTO outDTO = new LoginOutDTO();
        BeanUtils.copyProperties(cacheDto, outDTO);

        return outDTO;
    }


    protected boolean isOverdue(Date issueAt)
    {
        LocalDateTime issueTime = LocalDateTime.ofInstant(issueAt.toInstant(), ZoneId.systemDefault());
        return LocalDateTime.now().isAfter(issueTime);
    }


    public LoginOutDTO tokenRefresh(String token)
    {
        DecodedJWT jwt = JWT.decode(token);
        boolean overdue = isOverdue(jwt.getIssuedAt());

        if (overdue)
        {
            throw BusinessException.builder().errMsg("token已经过期,请重新登录").build();
        }

        String subject = jwt.getSubject();

        Session session = null;

        try
        {
            /**
             * 查找现有的session
             */
            session = sessionRepository.findById(subject);
        } catch (Exception e)
        {
//            e.printStackTrace();
            log.info("查找现有的session 失败，将创建一个新的");
        }
        if (null == session)
        {
            throw BusinessException.builder().errMsg("token已经过期,请重新登录").build();
        }
        String json = session.getAttribute(G_USER);
        if (StringUtils.isEmpty(json))
        {
            throw BusinessException.builder().errMsg("token已经过期,请重新登录").build();

        }

        UserDTO dto = JsonUtil.jsonToPojo(json, UserDTO.class);
        if (null == dto)
        {
            throw BusinessException.builder().errMsg("token已经过期,请重新登录").build();
        }

        UserPO userPO = userDao.findByUserId(dto.getUserId());

        if (userPO == null)
        {
            throw BusinessException.builder().errMsg("token 令牌有误").build();
        }

        String salt = userPO.getPassword();
        try
        {
            Algorithm algorithm = Algorithm.HMAC256(salt);
            JWTVerifier verifier = JWT.require(algorithm).withSubject(subject).build();
            verifier.verify(jwt.getToken());
        } catch (Exception e)
        {
            throw BusinessException.builder().errMsg("token 令牌有误").build();
        }

        return setSession(userPO);
    }
}
