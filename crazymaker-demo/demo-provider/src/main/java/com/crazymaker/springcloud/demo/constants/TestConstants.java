package com.crazymaker.springcloud.demo.constants;

public class TestConstants
{


    /**
     * 演示用地址： demo-provider 的 REST 接口  /api/user/detail/v1
     * 根据实际的地址调整
     */
    public static final String DEMO_CLIENT_PATH =
            "http://crazydemo.com:7700/demo-provider/";

    /**
     * 演示用地址： demo-provider 的 REST 接口  /api/demo/hello/v1
     * 根据实际的地址调整
     */
    public static final String HELLO_TEST_URL =
            "http://crazydemo.com:7700/demo-provider/api/demo/hello/v1";

    /**
     * 演示用地址： uaa-provider 的 REST 接口  /api/user/detail/v1
     * 根据实际的地址调整
     */
    public static final String USER_INFO_URL =
            "http://crazydemo.com:7700/uaa-provider/api/user/detail/v1?userId=1";

    /**
     * 错误的url
     */
    public static final String ERROR_URL = "www.baidu.xinlang.com";

}
