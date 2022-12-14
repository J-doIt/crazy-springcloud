package com.crazymaker.demo.hystrix;

import com.crazymaker.springcloud.common.util.HttpRequestUtil;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixThreadPoolKey;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import rx.Observable;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static com.crazymaker.springcloud.demo.constants.TestConstants.HELLO_TEST_URL;

@Slf4j
public class HystryxCommandExcecuteDemo
{


    public static final int COUNT = 50;


    /**
     * 测试url 是否可达
     *
     * @throws Exception
     */
    @Test
    public void testUrl() throws IOException
    {
        String responseData = HttpRequestUtil.simpleGet(HELLO_TEST_URL);
        log.info(responseData);
    }

    /**
     * 测试HttpGetterCommand
     */
    @Test
    public void testHttpGetterCommand() throws Exception
    {

        /**
         *  构造配置实例
         */
        HystrixCommand.Setter setter = HystrixCommand.Setter
                .withGroupKey(HystrixCommandGroupKey.Factory.asKey("group-1"))
                .andCommandKey(HystrixCommandKey.Factory.asKey("command-1"))
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("threadPool-1"));
        String result =
                new HttpGetterCommand(HELLO_TEST_URL, setter).execute();
        log.info("result={}", result);

    }

    /**
     * 测试同步执行
     */
    @Test
    public void testExecute() throws Exception
    {

        /**
         * 使用统一配置类
         */
        HystrixCommand.Setter setter = SetterDemo.buildSetter(
                "group-1",
                "testCommand",
                "testThreadPool");
        /**
         * 循环 5 次
         */
        for (int i = 0; i < COUNT; i++)
        {
            String result =
                    new HttpGetterCommand(HELLO_TEST_URL, setter).execute();
            log.info("result={}", result);
        }
        Thread.sleep(Integer.MAX_VALUE);
    }


    @Test
    public void testQueue() throws Exception
    {
        /**
         * 使用统一配置类
         */
        HystrixCommand.Setter setter = SetterDemo.buildSetter(
                "group-1",
                "testCommand",
                "testThreadPool");

        List<Future<String>> flist = new LinkedList<>();

        /**
         * 同时发起5个异步的请求
         */
        for (int i = 0; i < COUNT; i++)
        {
            Future<String> future =
                    new HttpGetterCommand(HELLO_TEST_URL, setter).queue();

            flist.add(future);

        }
        /**
         * 阻塞处理异步请求的结果
         */
        Iterator<Future<String>> it = flist.iterator();
        while (it.hasNext())
        {
            Future<String> future = it.next();
            String result = future.get(10, TimeUnit.SECONDS);
            log.info("result={}", result);
        }
        Thread.sleep(Integer.MAX_VALUE);
    }


    @Test
    public void testToObservable() throws Exception
    {
        /**
         * 使用统一配置类
         */
        HystrixCommand.Setter setter = SetterDemo.buildSetter(
                "group-1",
                "testCommand",
                "testThreadPool");

        for (int i = 0; i < COUNT; i++)
        {
            Thread.sleep(2);

            new HttpGetterCommand(HELLO_TEST_URL, setter)
                    .toObservable()
                    .subscribe(result -> log.info("result={}", result),
                            error -> log.error("error={}", error)
                    );
        }
        Thread.sleep(Integer.MAX_VALUE);
    }


    @Test
    public void testObserve() throws Exception
    {
        /**
         * 使用统一配置类
         */
        HystrixCommand.Setter setter = SetterDemo.buildSetter(
                "group-1",
                "testCommand",
                "testThreadPool");

        Observable<String> observe = new HttpGetterCommand(HELLO_TEST_URL, setter)
                .observe();
        Thread.sleep(1000);
        log.info("订阅尚未开始！");
        //订阅3次
        observe.subscribe(result -> log.info("onNext result={}", result), error -> log.error("onError error={}", error));

        observe.subscribe(result -> log.info("onNext result ={}", result), error -> log.error("onError error={}", error));
        observe.subscribe(
                result -> log.info("onNext result={}", result),
                error -> log.error("onError error ={}", error),
                () -> log.info("onCompleted called")
        );
        Thread.sleep(Integer.MAX_VALUE);

    }


}