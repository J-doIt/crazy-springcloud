package com.crazymaker.springcloud.message.contract.constants;


/**
 * The enum Mq message type enum.
 */
public enum MqMessageTypeEnum
{
    /**
     * 生产者.
     */
    PRODUCER_MESSAGE(10, "生产者" ),
    /**
     * 消费者.
     */
    CONSUMER_MESSAGE(20, "消费者" );

    private int messageType;

    private String value;

    MqMessageTypeEnum(int messageType, String value)
    {
        this.messageType = messageType;
        this.value = value;
    }

    /**
     * Message type int.
     *
     * @return the int
     */
    public int messageType()
    {
        return messageType;
    }

    /**
     * Value string.
     *
     * @return the string
     */
    public String value()
    {
        return value;
    }

}
