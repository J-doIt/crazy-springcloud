package com.crazymaker.springcloud.reactive.rpc.mock;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.text.MessageFormat;
import java.util.Map;

@Slf4j
public class ReactiveRpcMethodHandler implements RpcMethodHandler
{
    // 返回flux还是mono
    // isAssignableFrom 判断类型是否某个的子类
    // instanceof 判断实例是否某个的子类
    private final boolean isFlux;

    //实际的返回值
    private final Class<?> returnValueType;

    //被Reactor封装的返回值
    private final Class<?> returnType;


    //响应式客户端
    private WebClient client;

    private WebClient.RequestBodySpec request;

    /**
     * REST URL 的前面部分，来自于远程调用 Feign 接口的类级别注解
     * 如 "http://crazydemo.com:7700/demo-provider/";
     */
    final String contextPath;

    /**
     * REST URL 的前面部分，来自于远程调用 Feign 接口的方法级别的注解
     * 如 "api/demo/hello/v1";
     */
    final String url;

    public ReactiveRpcMethodHandler(String contextPath, String url, Class<?> returnType, Class<?> returnValueType)
    {
        this.contextPath = contextPath;
        this.url = url;

        this.client = WebClient.create(contextPath);

        // 返回flux还是mono
        isFlux = returnType.isAssignableFrom(Flux.class);
        //被Reactor封装的返回值
        this.returnType = returnType;

        //实际的返回值
        this.returnValueType = returnValueType;

    }

    /**
     * 功能：组装 url，完成 REST RPC 远程调用，并且返回 JSON结果
     *
     * @param argv RPC 方法的参数
     * @return REST 接口的响应结果
     * @throws Throwable 异常
     */
    @Override
    public Object invoke(Object[] argv) throws Throwable
    {
        /**
         * 请求地址：组装 REST 接口 URL
         */
        String restUrl = contextPath + MessageFormat.format(url, argv);
        log.info("restUrl={}", restUrl);
        /**
         *    供参考：请求参数
         */

        Map<String, Object> params = null;

        /**
         * 供参考：请求方法
         */
        HttpMethod method = HttpMethod.GET;


        /**
         * 不是通过 HttpClient 组件构建请求
         * 而是通过 WebClient 组件构建请求
         */
        request = this.client
                // 请求方法
                .method(method)
                // 请求url 和 参数
//                .uri(restUrl, params)
                .uri(restUrl)
                // 媒体的类型
                .accept(MediaType.APPLICATION_JSON);

        WebClient.ResponseSpec retrieve = request.retrieve();
        // 处理异常 请求发出去之后判断一下返回码
        retrieve.onStatus(status -> status.value() == 404,
                response -> Mono.just(new RuntimeException("Not Found")));

        // 返回结果
        Object result = null;
        if (isFlux)
        {
            result = retrieve.bodyToFlux(returnValueType);
        } else
        {
            result = retrieve.bodyToMono(returnValueType);
        }

        return result;

    }


}
