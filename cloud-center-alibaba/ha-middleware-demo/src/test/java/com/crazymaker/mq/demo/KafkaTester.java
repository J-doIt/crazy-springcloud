package com.crazymaker.mq.demo;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.Test;

import java.util.Collections;
import java.util.Properties;

public class KafkaTester {

    public static String msg = "";//定义的消息

    public static String TOPIC = "test";//定义主题
    //kafka地址，多个地址用逗号分割
//    public static final String     KAFKA_SERVER="192.168.23.76:9092,192.168.23.77:9092";
//    public static final String KAFKA_SERVER = "192.168.56.122:9092";
    public static final String KAFKA_SERVER = "cdh1:9092";

    static {

        String word = "疯狂创客圈高并发研究社群";

        for (int i = 0; i < 1000; i++) {
            msg += word;
        }
    }

    @Test
    public void produceOne() {
        Properties propsMap = new Properties();
        propsMap.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_SERVER);
        propsMap.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        propsMap.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(propsMap);

        try {

            ProducerRecord<String, String> record = new ProducerRecord<String, String>(TOPIC, msg);
            kafkaProducer.send(record);
            System.out.println("消息发送成功,编号:" + msg);

        } finally {
            kafkaProducer.close();
        }

    }

    @Test
    public void produceMany() {
        Properties propsMap = new Properties();
        propsMap.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_SERVER);
        propsMap.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        propsMap.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(propsMap);

        try {
            int count = 0;
            while (true) {
                ProducerRecord<String, String> record = new ProducerRecord<String, String>(TOPIC, msg);
                kafkaProducer.send(record);
                count++;
                if (count % 1024 == 0)
                    System.out.println("消息发送成功,编号:" + count);
                if (count > 1024 * 10) break;
//                ThreadUtil.sleepMilliSeconds(500);
            }
        } finally {
            kafkaProducer.close();
        }

    }

    @Test
    public void consumerTest() {
        Properties p = new Properties();
        p.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_SERVER);
        p.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        p.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        p.put(ConsumerConfig.GROUP_ID_CONFIG, "consumer_group_1");

        KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<String, String>(p);
        kafkaConsumer.subscribe(Collections.singletonList(TOPIC));// 订阅消息

        while (true) {
            ConsumerRecords<String, String> records = kafkaConsumer.poll(1000);
            for (ConsumerRecord<String, String> record : records) {
                System.out.println(String.format("topic:%s,offset:%d,消息:%s", //
                        record.topic(), record.offset(), record.value()));
            }
        }
    }
}
