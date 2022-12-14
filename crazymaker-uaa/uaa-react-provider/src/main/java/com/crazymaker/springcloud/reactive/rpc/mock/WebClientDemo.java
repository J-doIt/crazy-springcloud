package com.crazymaker.springcloud.reactive.rpc.mock;

import com.alibaba.fastjson.JSONObject;
import com.crazymaker.springcloud.common.result.RestOut;
import com.crazymaker.springcloud.common.util.ThreadUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.io.IOException;

@Slf4j
public class WebClientDemo
{

    String baseUrl = "http://crazydemo.com:7700/demo-provider/";

    /**
     * 测试用例
     */
    @Test
    public void testCreate() throws IOException
    {

        //响应式客户端
        WebClient client = null;

        WebClient.RequestBodySpec request = null;

        //方式一：极简创建
        client = WebClient.create(baseUrl);

        //方式二：使用builder创建
        client = WebClient.builder()
                .baseUrl("https://api.github.com")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .defaultHeader(HttpHeaders.USER_AGENT, "Spring 5 WebClient")
                .build();
        String restUrl = baseUrl + "api/demo/hello/v1";
        /**
         * 是通过 WebClient 组件构建请求
         */
        request = client
                // 请求方法
                .method(HttpMethod.GET)
                // 请求url 和 参数
//                .uri(restUrl, params)
                .uri(restUrl)
                // 媒体的类型
                .accept(MediaType.APPLICATION_JSON);

        WebClient.ResponseSpec retrieve = request.retrieve();
        // 处理异常 请求发出去之后判断一下返回码
        retrieve.onStatus(status -> status.value() == 404,
                response -> Mono.just(new RuntimeException("Not Found")));

        ParameterizedTypeReference<RestOut<JSONObject>> parameterizedTypeReference = new ParameterizedTypeReference<RestOut<JSONObject>>()
        {
        };
        // 返回流
        Mono<RestOut<JSONObject>> resp = retrieve.bodyToMono(parameterizedTypeReference);
        // 订阅结果
        resp.subscribe(responseData ->
        {
            log.info(responseData.toString());
        }, e ->
        {
            log.info("error:" + e.getMessage());
        });
        //主线程等待， 一切都是为了查看到异步结果
        ThreadUtil.sleepSeconds(1000);
    }

    /**
     * 测试用例: 发送get请求
     */
    @Test
    public void testGet() throws IOException
    {
        String restUrl = baseUrl + "api/demo/hello/v1";

        Mono<String> resp = WebClient.create()
                .method(HttpMethod.GET)
                .uri(restUrl)
                .cookie("token", "jwt_token")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve().bodyToMono(String.class);

        // 订阅结果
        resp.subscribe(responseData ->
        {
            log.info(responseData.toString());
        }, e ->
        {
            log.info("error:" + e.getMessage());
        });
        //主线程等待， 一切都是为了查看到异步结果
        ThreadUtil.sleepSeconds(1000);
    }

    @Data
    @AllArgsConstructor
    public class LoginInfoDTO
    {
        String username;
        String password;
    }

    /**
     * 测试用例： 发送post 请求 mime为 application/json
     */
    @Test
    public void testJSONParam()
    {
        String restUrl = baseUrl + "api/demo/post/demo/v2";
        LoginInfoDTO dto = new LoginInfoDTO("lisi", "123456");
        Mono<LoginInfoDTO> personMono = Mono.just(dto);

        Mono<String> resp = WebClient.create().post()
                .uri(restUrl)
                .contentType(MediaType.APPLICATION_JSON)
//                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(personMono, LoginInfoDTO.class)
                .retrieve().bodyToMono(String.class);

        // 订阅结果
        resp.subscribe(responseData ->
        {
            log.info(responseData.toString());
        }, e ->
        {
            log.info("error:" + e.getMessage());
        });
        //主线程等待， 一切都是为了查看到异步结果
        ThreadUtil.sleepSeconds(1000);
    }

    /**
     * 测试用例： 发送post 请求
     * mime 类型为   application/x-www-form-urlencoded
     */
    @Test
    public void testFormParam()
    {
        String restUrl = baseUrl + "api/demo/post/demo/v1";

        MultiValueMap<String, String> formData = new LinkedMultiValueMap();
        formData.add("username", "zhangsan");
        formData.add("password", "123456");
        Mono<String> resp = WebClient.create().post()
                .uri(restUrl)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .retrieve().bodyToMono(String.class);

        // 订阅结果
        resp.subscribe(responseData ->
        {
            log.info(responseData.toString());
        }, e ->
        {
            log.info("error:" + e.getMessage());
        });
        //主线程等待， 一切都是为了查看到异步结果
        ThreadUtil.sleepSeconds(1000);
    }

    /**
     * 测试用例： 上传文件
     */
    @Test
    public void testUploadFile()
    {
        String restUrl = baseUrl + "/api/file/upload/v1";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        HttpEntity<ClassPathResource> entity =
                new HttpEntity<>(new ClassPathResource("logback-spring.xml"), headers);
        MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
        parts.add("file", entity);
        Mono<String> resp = WebClient.create().post()
                .uri(restUrl)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(parts))
                .retrieve().bodyToMono(String.class);
        log.info("result:{}", resp.block());
    }

    /**
     * 测试用例： Exchange
     */
    @Test
    public void testExchange()
    {
        String baseUrl = "http://localhost:8081";
        WebClient webClient = WebClient.create(baseUrl);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("username", "u123");
        map.add("password", "p123");

        Mono<ClientResponse> loginMono = webClient.post().uri("login").syncBody(map).exchange();
        ClientResponse response = loginMono.block();
        if (response.statusCode() == HttpStatus.OK) {
            Mono<RestOut> resultMono = response.bodyToMono(RestOut.class);
            resultMono.subscribe(result -> {
                if (result.isSuccess()) {
                    ResponseCookie sidCookie = response.cookies().getFirst("sid");
                    Mono<LoginInfoDTO> usersMono = webClient.get().uri("users").cookie(sidCookie.getName(), sidCookie.getValue()).retrieve().bodyToMono(LoginInfoDTO.class);
                    usersMono.subscribe(System.out::println);
                }
            });
        }
    }
    /**
     * 测试用例： 错误处理
     */
    @Test
    public void testFormParam4xx()
    {
        WebClient webClient = WebClient.builder()
                .baseUrl("https://api.github.com")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/vnd.github.v3+json")
                .defaultHeader(HttpHeaders.USER_AGENT, "Spring 5 WebClient")
                .build();
        WebClient.ResponseSpec responseSpec = webClient.method(HttpMethod.GET)
                .uri("/user/repos?sort={sortField}&direction={sortDirection}",
                        "updated", "desc")
                .retrieve();
        Mono<String> mono = responseSpec
                .onStatus(e -> e.is4xxClientError(), resp ->
                {
                    log.error("error:{},msg:{}", resp.statusCode().value(), resp.statusCode().getReasonPhrase());
                    return Mono.error(new RuntimeException(resp.statusCode().value() + " : " + resp.statusCode().getReasonPhrase()));
                })
                .bodyToMono(String.class)
                .doOnError(WebClientResponseException.class, err ->
                {
                    log.info("ERROR status:{},msg:{}", err.getRawStatusCode(), err.getResponseBodyAsString());
                    throw new RuntimeException(err.getMessage());
                })
                .onErrorReturn("fallback");
        String result = mono.block();
        System.out.print(result);
    }
}