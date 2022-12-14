package com.crazymaker.mq.demo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.crazymaker.springcloud.common.util.ThreadUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.junit.Test;

import java.util.List;
import java.util.Map;

@Slf4j
public class CanalRocketmqMessageTest {
//    public static final String ROCKETMQ_SERVER = "cdh2:9876;cdh2:9877";

    public static final String ROCKETMQ_SERVER = "cdh2:9876";
//    public static final String ROCKETMQ_SERVER = "192.168.56.122:9876";


    @Test
    public void consumerTest() throws Exception {


        // 实例化消费者
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("please_rename_unique_group_name");

        // 设置NameServer的地址
        consumer.setNamesrvAddr(ROCKETMQ_SERVER);

        // 订阅一个或者多个Topic，以及Tag来过滤需要消费的消息
        consumer.subscribe("canal_log", "*");
        // 注册回调实现类来处理从broker拉取回来的消息
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                System.out.printf("%s Receive New Messages: %s %n", Thread.currentThread().getName(), msgs);

                for (int i = 0; i < msgs.size(); i++) {
                    MessageExt msg = msgs.get(i);
                    String content = null;
                    try {
                        content = new String(msg.getBody(), "utf-8");
                        JSONObject jsonObject = JSON.parseObject(content);
                        processCanalData(jsonObject);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    log.info("收到消息：{}, {}", i, msg.getMsgId() + " " + msg.getTopic() + " " + msg.getTags() + " " + content);


                }
                // 标记该消息已经被成功消费
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        // 启动消费者实例
        consumer.start();
        System.out.printf("Consumer Started.%n");
        ThreadUtil.sleepMilliSeconds(Integer.MAX_VALUE);
    }

    /**
     * 转换Canal的FlatMessage中data成泛型对象
     *
     * @param jsonObject Canal发送MQ信息
     * @return 泛型对象集合
     */
    protected List<Map<String, String>> processCanalData(JSONObject jsonObject) {

        String sqlType = jsonObject.getString("type");
        JSONArray data = jsonObject.getJSONArray("data");
        System.out.println("表名为:" + jsonObject.getString("table") + ",sql类型为：" + sqlType);
        if ("UPDATE".equals(sqlType) || "INSERT".equals(sqlType)) {
            System.out.println(data);
            return null;
        }
        if ("DELETE".equals(sqlType)) {
            System.out.println(data);
        }


        return null;
    }
}
