package com.crazymaker.cloud.ha.middleware.controller;

import com.crazymaker.cloud.ha.middleware.service.impl.MQProducerServiceImpl;
import com.crazymaker.springcloud.common.dto.UserDTO;
import com.crazymaker.springcloud.common.result.RestOut;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags = "rocketmq-spring-boot-starter Demo")
@Slf4j
@RequestMapping("/api/rocketmq")
public class RocketMQController {

    @Autowired
    private MQProducerServiceImpl mqProducerService;

    @GetMapping("/sendSomeThing")
    public void sendSomeThing() {

        mqProducerService.sendSomeThing();
    }
    @GetMapping("/send")
    public void send() {
        UserDTO user = UserDTO.builder().userId(10001L).username("卷王1").build();
        mqProducerService.send(user);
        user = UserDTO.builder().userId(10002L).username("卷王2").build();
        mqProducerService.send(user);
        user = UserDTO.builder().userId(10003L).username("卷王3").build();
        mqProducerService.send(user);
        user = UserDTO.builder().userId(10004L).username("卷王4").build();
        mqProducerService.send(user);
    }

    @GetMapping("/sendTag")
    public RestOut<SendResult> sendTag() {
        SendResult sendResult = mqProducerService.sendTagMsg("带有tag的字符消息");
        return RestOut.success(sendResult);
    }

}
