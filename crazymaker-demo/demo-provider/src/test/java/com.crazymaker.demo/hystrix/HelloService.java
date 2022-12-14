package com.crazymaker.demo.hystrix;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;

import static com.crazymaker.springcloud.demo.constants.TestConstants.HELLO_TEST_URL;

public class HelloService
{

    @HystrixCommand(fallbackMethod = "serviceFailure")
    public String getHelloContent()
    {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(HELLO_TEST_URL, String.class);
    }

    public String serviceFailure()
    {
        return "hello world service is not available !";
    }

    @Test
    public void testGetHelloContent() throws Exception
    {
        getHelloContent();
    }
}
