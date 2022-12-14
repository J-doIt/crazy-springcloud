package com.crazymaker.springcloud.reactive.user.info.config.handler;

import com.crazymaker.springcloud.common.exception.BusinessException;
import com.crazymaker.springcloud.reactive.user.info.dto.User;
import com.crazymaker.springcloud.reactive.user.info.service.impl.JpaEntityServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Slf4j
@Component
public class UserReactiveHandler
{


    @Resource
    private JpaEntityServiceImpl jpaEntityService;


    /**
     * 得到所有用户
     *
     * @param request
     * @return
     */
    public Mono<ServerResponse> getAllUser(ServerRequest request)
    {
        log.info("方法 getAllUser 被调用了");
        return ok().contentType(APPLICATION_JSON_UTF8)
                .body(Flux.fromIterable(jpaEntityService.selectAllUser()), User.class);
    }

    /**
     * 创建用户
     *
     * @param request
     * @return
     */
    public Mono<ServerResponse> createUser(ServerRequest request)
    {
        // 2.0.0 是可以工作, 但是2.0.1 下面这个模式是会报异常
        Mono<User> user = request.bodyToMono(User.class);
        /**Mono 使用响应式的,时候都是一个流,是一个发布者,任何时候都不能调用发布者的订阅方法
         也就是不能消费它, 最终的消费还是交给我们的Springboot来对它进行消费,任何时候不能调用它的
         user.subscribe();
         不能调用block
         把异常放在统一的地方来处理
         */

        return user.flatMap(dto ->
        {
            // 校验代码需要放在这里
            if (StringUtils.isBlank(dto.getName()))
            {
                throw new BusinessException("用户名不能为空");
            }

            return ok().contentType(APPLICATION_JSON_UTF8)
                    .body(Mono.create(cityMonoSink -> cityMonoSink.success(jpaEntityService.addUser(dto))), User.class);
        });
    }

    /**
     * 根据id删除用户
     *
     * @param request
     * @return
     */
    public Mono<ServerResponse> deleteUserById(ServerRequest request)
    {
        String id = request.pathVariable("id");
        // 校验代码需要放在这里
        if (StringUtils.isBlank(id))
        {
            throw new BusinessException("id不能为空");
        }
        User dto = new User();
        dto.setUserId(Long.parseLong(id));
        return ok().contentType(APPLICATION_JSON_UTF8)
                .body(Mono.create(cityMonoSink -> cityMonoSink.success(jpaEntityService.delUser(dto))), User.class);
    }

}
