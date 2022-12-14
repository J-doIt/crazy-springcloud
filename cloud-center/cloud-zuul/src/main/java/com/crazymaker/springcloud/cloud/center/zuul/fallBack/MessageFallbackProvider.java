package com.crazymaker.springcloud.cloud.center.zuul.fallBack;

import com.crazymaker.springcloud.common.result.RestOut;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.filters.route.FallbackProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Component
public class MessageFallbackProvider implements FallbackProvider
{

    @Override
    public String getRoute()
    {
        //设置熔断的服务名
        //如果是所有服务则设置为*
        return "message-provider";
    }

    @Override
    public ClientHttpResponse fallbackResponse(String route, Throwable cause)
    {

        log.error(cause.getCause() != null ? cause.getCause().getMessage() : cause.getMessage());
        return new ClientHttpResponse()
        {
            @Override
            public HttpStatus getStatusCode() throws IOException
            {
                return HttpStatus.INTERNAL_SERVER_ERROR;
            }

            @Override
            public int getRawStatusCode() throws IOException
            {
                return HttpStatus.INTERNAL_SERVER_ERROR.value();
            }

            @Override
            public String getStatusText() throws IOException
            {
                return RestOut.error("网关调用有误" ).toString();
            }

            @Override
            public void close()
            {

            }

            @Override
            public InputStream getBody() throws IOException
            {
                return new ByteArrayInputStream(getStatusText().getBytes());
            }

            @Override
            public HttpHeaders getHeaders()
            {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                return headers;
            }
        };
    }
}
