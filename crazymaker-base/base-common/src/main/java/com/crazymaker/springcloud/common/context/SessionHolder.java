package com.crazymaker.springcloud.common.context;

import com.crazymaker.springcloud.common.constants.SessionConstants;
import com.crazymaker.springcloud.common.dto.UserDTO;
import com.crazymaker.springcloud.common.util.JsonUtil;
import org.springframework.core.NamedThreadLocal;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Created by 尼恩@疯狂创客圈
 */
public class SessionHolder
{
    // session id  线程本地变量
    private static final ThreadLocal<String> sidLocal = new NamedThreadLocal<>("sidLocal");
    // session id store prefix 线程本地变量
    private static final ThreadLocal<String> sessionIDStore = new NamedThreadLocal<>("sessionIDStore");
    // session userIdentifier(这里为 user id) 线程本地变量
    private static final ThreadLocal<String> userIdentiferLocal = new NamedThreadLocal<>("userIdentiferLocal");
    // session 用户信息  线程本地变量
    private static final ThreadLocal<UserDTO> sessionUserLocal = new NamedThreadLocal<>("sessionUserLocal");
    // session  线程本地变量
    private static final ThreadLocal<HttpSession> sessionLocal = new NamedThreadLocal<>("sessionLocal");
    // request  线程本地变量
    private static final ThreadLocal<HttpServletRequest> requestLocal = new NamedThreadLocal<>("requestLocal");
    public static final String G_USER = "USER-CACHE";

    /**
     * 保存Request
     *
     * @param request 待保存的request
     */
    public static void setRequest(HttpServletRequest request)
    {
        if (null != requestLocal.get())
        {
            requestLocal.remove();
        }

        requestLocal.set(request);
    }


    /**
     * @return
     */
    public static HttpServletRequest getRequest()
    {
        HttpServletRequest request = requestLocal.get();
        Assert.notNull(request, "request 未设置");
        return request;

    }


    /**
     * 保存session
     *
     * @param session 待保存的session
     */
    public static void setSession(HttpSession session)
    {
        sessionLocal.set(session);
    }


    /**
     * 取得session
     *
     * @return 返回session
     */
    public static HttpSession getSession()
    {
        HttpSession session = sessionLocal.get();
        Assert.notNull(session, "session 未设置");
        return session;

    }


    /**
     * 取得session user
     *
     * @return 返回session user
     */
    public static UserDTO getSessionUser()
    {
        UserDTO UserDTO = sessionUserLocal.get();

        if (null == UserDTO)
        {

            HttpSession session = getSession();

            String valueString = (String) session.getAttribute(G_USER);

            UserDTO = JsonUtil.jsonToPojo(valueString, UserDTO.class);

            sessionUserLocal.set(UserDTO);
        }

        Assert.notNull(UserDTO, "session 未设置");
        return UserDTO;

    }


    /**
     * 清除线程局部变量
     */
    public static void clearData()
    {
        sessionUserLocal.remove();
        sessionLocal.remove();
        sessionIDStore.remove();
        userIdentiferLocal.remove();
        requestLocal.remove();
        sidLocal.remove();

    }


    /**
     * 获取session 中的userid
     *
     * @return userid
     */
    public static String getUserId()
    {
        UserDTO userDTO = getSessionUser();

        Assert.notNull(userDTO, "session user is null");

        return String.valueOf(userDTO.getUserId());
    }


    /**
     * 获取 session id 的存储前缀
     *
     * @return getSessionIDStore
     */
    public static String getSessionIDStore()
    {
        if (null != sessionIDStore.get())
        {
            return sessionIDStore.get();
        }
        return SessionConstants.SESSION_STORE;

    }

    public static void setSessionIDStore(String type)
    {

        sessionIDStore.set(type);

    }


    public static String getAuthHeader()
    {
            return SessionConstants.AUTHORIZATION_HEAD;
    }

    public static void put(String key, String s)
    {
        getSession().setAttribute(key, s);
    }

    public static String get(String key)
    {
        return (String) getSession().getAttribute(key);
    }



    public static String getUserIdentifier()
    {
        return userIdentiferLocal.get();
    }

    public static void setUserIdentifer(String userIdentifier)
    {
        userIdentiferLocal.set(userIdentifier);
    }

    public static String getSid()
    {
        return sidLocal.get();
    }

    public static void setSid(String sid)
    {
        sidLocal.set(sid);
    }
}
