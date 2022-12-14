package com.crazymaker.springcloud.common.constants;

/**
 * Created by 尼恩 on 2019/7/17.
 */
public class SessionConstants
{

    /**
     * 认证头部字段
     */

    public static final String AUTHORIZATION_HEAD = "token";
//    public static final String ADMIN_AUTHORIZATION_HEAD = "Authorization";

    /**
     * token请求头名称
     */
    public static final String TOKEN_HEADER = AUTHORIZATION_HEAD;

    /**
     * 用户id，作为session的身份标识
     */
    public static final String USER_IDENTIFIER = "USER-ID";//redisSessionId 的身份标识

    /**
     * 用户端的 sessionId 键值前缀
     */
    public static final String SESSION_STORE = "FRONT-USER-END";//redisSessionId 的身份标识

    /**
     * 管理控制台的 sessionId 键值前缀
     */
//    public static final String ADMIN_SESSION_STORE = "BACK-ADMIN-END";//redisSessionId 的身份标识
    /**
     * 存于redis中的sessionId前缀
     */
    public static final String REDIS_SESSION_KEY_PREFIX = "SESSION_KEY";

    /**
     * session 的过期时间  单位 s
     */
    public static final int SESSION_TIME_OUT = 3600 * 9;
//    public static final int SESSION_TIME_OUT = 3600 * 24 * 10;
    /**
     * 缓存client的redis key，这里是hash结构存储
     */
    public static final String CACHE_CLIENT_KEY = "oauth_client_details";
    /**
     * 默认token过期时间
     */
    public static final Integer ACCESS_TOKEN_VALIDITY_SECONDS = SESSION_TIME_OUT / 10;
    /**
     * redis中授权token对应的key
     */
    public static final String REDIS_TOKEN_AUTH = "auth:";

    public static final String USER_INFO = "user-info";

    /**
     * redis中应用对应的token集合的key
     */
    public static final String REDIS_CLIENT_ID_TO_ACCESS = "client_id_to_access:";
    /**
     * redis中用户名对应的token集合的key
     */
    public static final String REDIS_UNAME_TO_ACCESS = "uname_to_access:";
    public static final String BACK_END_HEADER = "backend";
    public static final String SYS_ROLES_JSON = "SYS_ROLES_JSON";


    /**
     * 默认生成图形验证码宽度
     */
    public static final String DEFAULT_IMAGE_WIDTH = "100";

    /**
     * 默认生成图像验证码高度
     */
    public static final String DEFAULT_IMAGE_HEIGHT = "35";

    /**
     * 默认生成图形验证码长度
     */
    public static final String DEFAULT_IMAGE_LENGTH = "4";

    /**
     * 默认生成图形验证码过期时间
     */
    public static final int DEFAULT_IMAGE_EXPIRE = 6000;

    /**
     * 边框颜色，合法值： r,g,b (and optional alpha) 或者 white,black,blue.
     */
    public static final String DEFAULT_COLOR_FONT = "blue";

    /**
     * 图片边框
     */
    public static final String DEFAULT_IMAGE_BORDER = "no";

    /**
     * 默认图片间隔
     */
    public static final String DEFAULT_CHAR_SPACE = "5";

    /**
     * 默认保存code的前缀
     */
    public static final String DEFAULT_CODE_KEY = "DEFAULT_CODE_KEY";

    /**
     * 验证码文字大小
     */
    public static final String DEFAULT_IMAGE_FONT_SIZE = "30";

    public static final String BEARER_TYPE = "Bearer";
    public static final String OAUTH2_TYPE = "OAuth2";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String TOKEN_TYPE = "token_type";
    public static final String EXPIRES_IN = "expires_in";
    public static final String REFRESH_TOKEN = "refresh_token";
    public static final String DEFAULT_SCOPE = "scope";

    /**
     * rsa公钥
     */
    public static final String RSA_PUBLIC_KEY = "pubkey.txt";

    public static final String ID = "ID";
    public static final int OPTIONS_REQUEST_ORDER = 1;
    public static final int FILTER_ORDER_SESSION_PREFIX = 2;
    public static final int FILTER_ORDER_BUILD_SESSION_ID = 3;
    public static final int FILTER_ORDER_SESSION_REPOSITORY = 4;
    public static final int FILTER_ORDER_SESSION_DATA_LOAD = 5;
}
