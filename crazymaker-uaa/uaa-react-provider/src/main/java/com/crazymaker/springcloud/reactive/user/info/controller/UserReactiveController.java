package com.crazymaker.springcloud.reactive.user.info.controller;

import com.crazymaker.springcloud.common.result.RestOut;
import com.crazymaker.springcloud.reactive.user.info.dto.User;
import com.crazymaker.springcloud.reactive.user.info.service.impl.JpaEntityServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

/**
 * Mono 和 Flux 适用于两个场景，即：
 * Mono：实现发布者，并返回 0 或 1 个元素，即单对象。
 * Flux：实现发布者，并返回 N 个元素，即 List 列表对象。
 * 有人会问，这为啥不直接返回对象，比如返回 City/Long/List。
 * 原因是，直接使用 Flux 和 Mono 是非阻塞写法，相当于回调方式。
 * 利用函数式可以减少了回调，因此会看不到相关接口。这恰恰是 WebFlux 的好处：集合了非阻塞 + 异步
 */
@Slf4j
@Api(value = "用户信息、基础学习DEMO", tags = {"用户信息DEMO"})
@RestController
@RequestMapping("/api/user")
public class UserReactiveController
{

        @ApiOperation(value = "回显测试", notes = "提示接口使用者注意事项", httpMethod = "GET")
        @RequestMapping(value = "/hello")
        @ApiImplicitParams({
                @ApiImplicitParam(paramType = "query", dataType="string",dataTypeClass = String.class, name = "name",value = "名称", required = true)})
        public Mono<RestOut<String>> hello(@RequestParam(name = "name") String name)
        {
            log.info("方法 hello 被调用了");

            return  Mono.just(RestOut.succeed("hello " + name));
        }


        @Resource
        JpaEntityServiceImpl jpaEntityService;


        @PostMapping("/add/v1")
        @ApiOperation(value = "插入用户" )
        @ApiImplicitParams({
//                @ApiImplicitParam(paramType = "body", dataType="java.lang.Long", name = "userId", required = false),
//                @ApiImplicitParam(paramType = "body", dataType="用户", name = "dto", required = true)
                @ApiImplicitParam(paramType = "body",dataTypeClass = User.class, dataType="User", name = "dto",  required = true),
        })
//    @ApiImplicitParam(paramType = "body", dataType="com.crazymaker.springcloud.reactive.user.info.dto.User",  required = true)
        public Mono<User> userAdd(@RequestBody User dto)
        {
            //命令式写法
//        jpaEntityService.delUser(dto);

            //响应式写法
            return Mono.create(cityMonoSink -> cityMonoSink.success(jpaEntityService.addUser(dto)));
        }


        @PostMapping("/del/v1")
        @ApiOperation(value = "响应式的删除")
        @ApiImplicitParams({
                @ApiImplicitParam(paramType = "body", dataType="User",dataTypeClass = User.class,name = "dto",  required = true),
        })
        public Mono<User> userDel(@RequestBody User dto)
        {
            //命令式写法

//        jpaEntityService.delUser(dto);

            //响应式写法

            return Mono.create(cityMonoSink -> cityMonoSink.success(jpaEntityService.delUser(dto)));
        }

        @PostMapping("/list/v1")
        @ApiOperation(value = "查询用户")
        public Flux<User> listAllUser()
        {
            log.info("方法 listAllUser 被调用了");

            //命令式写法 改为响应式 以下语句，需要在流中执行
//        List<User> list = jpaEntityService.selectAllUser();
            //响应式写法
            Flux<User> userFlux = Flux.fromIterable(jpaEntityService.selectAllUser());
            return userFlux;
        }

        @PostMapping("/detail/v1")
        @ApiOperation(value = "响应式的查看")
        @ApiImplicitParams({
                @ApiImplicitParam(paramType = "body", dataTypeClass = User.class,dataType="User", name = "dto",  required = true),
        })
        public Mono<User> getUser(@RequestBody User dto)
        {
            log.info("方法 getUser 被调用了");

            //构造流
            Mono<User> userMono = Mono.justOrEmpty(jpaEntityService.selectOne(dto.getUserId()));
            return userMono;
        }

        @PostMapping("/detail/v2")
        @ApiOperation(value = "命令式的查看")
        @ApiImplicitParams({
                @ApiImplicitParam(paramType = "body", dataType="User",dataTypeClass = User.class, name = "dto",  required = true),
        })        public RestOut<User> getUserV2(@RequestBody User dto)
        {
            log.info("方法 getUserV2 被调用了");

            User user = jpaEntityService.selectOne(dto.getUserId());
            return RestOut.success(user);
        }

    }