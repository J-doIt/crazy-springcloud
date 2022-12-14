package com.crazymaker.springcloud.kafka.controller;

import com.crazymaker.springcloud.common.result.RestOut;
import com.crazymaker.springcloud.common.util.JsonUtil;
import com.crazymaker.springcloud.kafka.controller.dto.NewTopicDto;
import com.crazymaker.springcloud.kafka.controller.dto.SampleMsgDTO;
import com.crazymaker.springcloud.kafka.mq.admin.KafkaAdmin;
import com.crazymaker.springcloud.kafka.mq.producer.ProduceMessage;
import com.crazymaker.springcloud.kafka.mq.producer.internal.kafka.KafkaProducer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.TopicDescription;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

@RestController
@RequestMapping("/api/crazymaker/kafka/")
@Api(tags = "消息管理")
public class SampleController {

    @Resource
    KafkaProducer kafkaProducer;

    @Resource
    KafkaAdmin kafkaAdmin;


    @PostMapping("/simple/send/v1")
    @ApiOperation(value = "发送简单消息")
    public RestOut<String> simpleSend(@RequestBody SampleMsgDTO dto) {
        ProduceMessage pm = ProduceMessage.fromString(dto.getMsgTopic(), dto.getDescription());
        kafkaProducer.send(pm);
        return RestOut.success("发送完成");
    }


    @PostMapping("/topic/create/v1")
    @ApiOperation(value = "创建topic")
    public RestOut<String> createTopic(@RequestBody NewTopicDto topicDto) {
        Collection<NewTopic> newTopics = new ArrayList<>(1);
        newTopics.add(new NewTopic(topicDto.getName(), topicDto.getNumPartitions(), topicDto.getReplicationFactor()));
        kafkaAdmin.add(newTopics);

//        ProduceMessage pm=ProduceMessage.fromString(dto.getMsgTopic(),dto.getDescription());
//        kafkaProducerServer.send(pm);
        return RestOut.success("创建topic成功");
    }


    @PostMapping("/topic/delete/v1")
    @ApiOperation(value = "删除topic")
    public RestOut<String> delTopic(@RequestBody String topicName) {
        kafkaAdmin.delete(topicName);

//        ProduceMessage pm=ProduceMessage.fromString(dto.getMsgTopic(),dto.getDescription());
//        kafkaProducerServer.send(pm);
        return RestOut.success("删除topic成功");
    }

    @PostMapping("/topic/detail/v1")
    @ApiOperation(value = "查看topic")
    public RestOut<String> detailTopic(@RequestBody String topicName) {
        TopicDescription content = kafkaAdmin.descTopic(topicName);

//        ProduceMessage pm=ProduceMessage.fromString(dto.getMsgTopic(),dto.getDescription());
//        kafkaProducerServer.send(pm);
        return RestOut.success(JsonUtil.pojoToJson(content));
    }

    @PostMapping("/topic/list/v1")
    @ApiOperation(value = "查看topic list")
    public RestOut<String> detailTopicList() {
        Set<String> content = kafkaAdmin.listTopics();

//        ProduceMessage pm=ProduceMessage.fromString(dto.getMsgTopic(),dto.getDescription());
//        kafkaProducerServer.send(pm);
        return RestOut.success(content.toString());
    }


}
