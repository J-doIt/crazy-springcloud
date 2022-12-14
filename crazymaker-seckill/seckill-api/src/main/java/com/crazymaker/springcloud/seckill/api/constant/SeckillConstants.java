package com.crazymaker.springcloud.seckill.api.constant;

public class SeckillConstants
{
    //订单状态， -1:无效 0:成功 1:已付款
    public static final short ORDER_INVALID = -1;
    public static final short ORDER_VALID = 1;
    public static final short ORDER_PAYED = 2;

    //秒杀的限流阈值
    public static final int MAX_ENTER = 50;
    public static final int PER_SECKOND_ENTER = 2;

}
