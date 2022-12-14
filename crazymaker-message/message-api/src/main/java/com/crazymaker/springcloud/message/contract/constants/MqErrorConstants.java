package com.crazymaker.springcloud.message.contract.constants;


public class MqErrorConstants
{


    public static final String TPC10050001 = "消息的消费Topic不能为空";
    /**
     * Tpc 10050002 error code tip.
     */
    public static final String TPC10050002 = "根据消息key查找的消息为空";
    /**
     * Tpc 10050003 error code tip.
     */
    public static final String TPC10050003 = "删除消息失败,messageKey=%s";
    /**
     * Tpc 10050004 error code tip.
     */
    public static final String TPC10050004 = "消息中心接口异常,message=%s, messageKey=%s";
    /**
     * Tpc 10050005 error code tip.
     */
    public static final String TPC10050005 = "目标接口参数不能为空";
    /**
     * Tpc 10050006 error code tip.
     */
    public static final String TPC10050006 = "根据任务Id查找的消息为空";

    /**
     * Tpc 10050007 error code tip.
     */
    public static final String TPC10050007 = "消息数据不能为空";
    /**
     * Tpc 10050008 error code tip.
     */
    public static final String TPC10050008 = "消息体不能为空,messageKey=%s";
    /**
     * Tpc 10050009 error code tip.
     */
    public static final String TPC10050009 = "消息KEY不能为空";
    /**
     * Tpc 100500010 error code tip.
     */
    public static final String TPC100500010 = "Topic=%s, 无消费者订阅";
    /**
     * Tpc 100500011 error code tip.
     */
    public static final String TPC100500011 = "Mq编码转换异常, MessageKey=%s";
    /**
     * Tpc 100500012 error code tip.
     */
    public static final String TPC100500012 = "发送MQ失败, MessageKey=%s";
    /**
     * Tpc 100500013 error code tip.
     */
    public static final String TPC100500013 = "延迟级别错误, Topic=%s, MessageKey=%s";
    /**
     * Tpc 100500014 error code tip.
     */
    public static final String TPC100500014 = "MQ重试三次,仍然发送失败, Topic=%s, MessageKey=%s";
    /**
     * Tpc 100500015 error code tip.
     */
    public static final String TPC100500015 = "消息PID不能为空, messageKey=%s";
}
