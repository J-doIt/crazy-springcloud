package com.crazymaker.demo.hystrix;

import com.netflix.hystrix.HystrixCommand;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import static com.crazymaker.springcloud.demo.constants.TestConstants.ERROR_URL;
import static com.crazymaker.springcloud.demo.constants.TestConstants.HELLO_TEST_URL;

@Slf4j
public class HystryxCommandOtherTester
{


    /**
     * 测试：线程池满了
     */

    @Test
    public void testThreadPool() throws Exception
    {

        /**
         * 使用自定义的配置函数
         */
        HystrixCommand.Setter setter = SetterDemo.buildSetter(
                "group-1",
                "testCommand",
                "testThreadPool");

        for (int i = 1; i <= 10; i++)
        {

            new HttpGetterCommand(HELLO_TEST_URL, setter)
                    .toObservable()
                    .subscribe(s -> log.info(" result:{}", s));
        }

        Thread.sleep(Integer.MAX_VALUE);

    }

    /**
     * 测试:失败率导致快速失败
     */
    @Test
    public void testFastFallback() throws Exception
    {

        /**
         * 使用统一配置
         */
        HystrixCommand.Setter setter = SetterDemo.buildSetter(
                "group-1",
                "testCommand",
                "testThreadPool");

        for (int i = 1; i <= 46; i++)
        {
            if (i % 15 == 1)
            {
                Thread.sleep(2);

                for (int j = 1; j <= 46; j++)
                {

                    new HttpGetterCommand(ERROR_URL, setter)
                            .toObservable()
                            .subscribe(s -> log.info(" onNext ::" + s));
                }

            }

            new HttpGetterCommand(HELLO_TEST_URL, setter)
                    .toObservable()
                    .subscribe(s -> log.info(" onNext ::" + s));
        }

        Thread.sleep(Integer.MAX_VALUE);
    }
}