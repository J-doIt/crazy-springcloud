package com.crazymaker.springcloud.common.util;

import com.alibaba.fastjson.JSONObject;
import com.crazymaker.springcloud.common.result.RestOut;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;

import static com.crazymaker.springcloud.common.constants.CommonConstant.OK;

/**
 * @author zlt
 * @date 2018/12/20
 */
public class ResponseUtil
{
    private ResponseUtil()
    {
        throw new IllegalStateException("Utility class" );
    }

    /**
     * 通过流写到前端
     *
     * @param objectMapper 对象序列化
     * @param response
     * @param msg          返回信息
     * @param httpStatus   返回状态码
     * @throws IOException
     */
    public static void responseWriter(ObjectMapper objectMapper, HttpServletResponse response, String msg, int httpStatus) throws IOException
    {
        RestOut result = RestOut.success(msg);
        responseWrite(objectMapper, response, result);
    }

    /**
     * 通过流写到前端
     *
     * @param objectMapper 对象序列化
     * @param response
     * @param obj
     */
    public static void responseSucceed(ObjectMapper objectMapper, HttpServletResponse response, Object obj) throws IOException
    {
        RestOut result = RestOut.success(obj);
        result.setRespCode(OK);
        responseWrite(objectMapper, response, result);
    }

    /**
     * 通过流写到前端
     *
     * @param objectMapper
     * @param response
     * @param msg
     * @throws IOException
     */
    public static void responseFailed(ObjectMapper objectMapper,
                                      HttpServletResponse response,
                                      String msg,
                                      int httpStatus) throws IOException
    {
        RestOut result = RestOut.error(msg);
        result.setRespCode(httpStatus);
        response.setStatus(HttpStatus.OK.value());
        responseWrite(objectMapper, response, result);
    }

    public static void responseFailed(ObjectMapper objectMapper,
                                      HttpServletResponse response,
                                      String msg) throws IOException
    {
        responseFailed(objectMapper, response, msg, HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    private static void responseWrite(ObjectMapper objectMapper, HttpServletResponse response, RestOut result) throws IOException
    {
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        try (
                Writer writer = response.getWriter()
        )
        {
            writer.write(objectMapper.writeValueAsString(result));
            writer.flush();
        }
    }

    /**
     * webflux的response返回json对象
     */
    public static Mono<Void> responseWriter(ServerWebExchange exchange, int httpStatus, String msg)
    {
        RestOut result = RestOut.success(msg);
        ServerHttpResponse response = exchange.getResponse();
        response.getHeaders().setAccessControlAllowCredentials(true);
        response.getHeaders().setAccessControlAllowOrigin("*" );
        response.setStatusCode(HttpStatus.valueOf(httpStatus));
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON_UTF8);
        DataBufferFactory dataBufferFactory = response.bufferFactory();
        DataBuffer buffer = dataBufferFactory.wrap(JSONObject.toJSONString(result).getBytes(Charset.defaultCharset()));
        return response.writeWith(Mono.just(buffer)).doOnError((error) ->
        {
            DataBufferUtils.release(buffer);
        });
    }


}
