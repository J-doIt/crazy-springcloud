package com.crazymaker.springcloud.email.controller;

import com.crazymaker.springcloud.common.result.RestOut;
import com.crazymaker.springcloud.email.common.model.Email;
import com.crazymaker.springcloud.email.service.IMailService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/crazymaker/email/" )
@Api(tags = "邮件管理" )
public class SampleController
{

    @Autowired
    IMailService mailService;

    @PostMapping("/simple/send/v1" )
    @ApiOperation(value = "发送简单邮件" )
    public RestOut<String> simpleSend(@RequestBody Email email)
    {
        mailService.send(email);
        return RestOut.success("发送完成" );
    }

}
