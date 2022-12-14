package com.crazymaker.demo.hystrix;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.HystrixThreadPoolProperties;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.crazymaker.springcloud.demo.constants.TestConstants.ERROR_URL;
import static com.crazymaker.springcloud.demo.constants.TestConstants.HELLO_TEST_URL;

@Slf4j
public class IsolationStrategyDemo
{
    /**
     * 测试:线程池隔离
     */
    @Test
    public void testThreadPoolIsolationStrategy() throws Exception
    {

        /**
         * RPC 线程池1
         */
        HystrixCommand.Setter rpcPool1_Setter = HystrixCommand.Setter
                .withGroupKey(HystrixCommandGroupKey.Factory.asKey("group1"))
                .andCommandKey(HystrixCommandKey.Factory.asKey("command1"))
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("threadPool1"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withExecutionTimeoutInMilliseconds(5000)  //配置时间上限
                ).andThreadPoolPropertiesDefaults(
                        HystrixThreadPoolProperties.Setter()
                                .withCoreSize(10)    // 配置线程池里的线程数
                                .withMaximumSize(10)
                );


        /**
         * RPC 线程池2
         */
        HystrixCommand.Setter rpcPool2_Setter = HystrixCommand.Setter
                .withGroupKey(HystrixCommandGroupKey.Factory.asKey("group2"))
                .andCommandKey(HystrixCommandKey.Factory.asKey("command2"))
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("threadPool2"))
                .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withExecutionTimeoutInMilliseconds(5000)  //配置时间上限
                ).andThreadPoolPropertiesDefaults(
                        HystrixThreadPoolProperties.Setter()
                                .withCoreSize(10)    // 配置线程池里的线程数
                                .withMaximumSize(10)
                );
        /**
         * 访问一个错误连接，让 threadpool1 耗尽
         */
        for (int j = 1; j <= 5; j++)
        {


            new HttpGetterCommand(ERROR_URL, rpcPool1_Setter)
                    .toObservable()
                    .subscribe(s -> log.info(" result:{}", s));
        }
        /**
         * 访问一个正确连接，观察threadpool2是否正常
         */
        for (int j = 1; j <= 5; j++)
        {

            new HttpGetterCommand(HELLO_TEST_URL, rpcPool2_Setter)
                    .toObservable()
                    .subscribe(s -> log.info(" result:{}", s));
        }
        Thread.sleep(Integer.MAX_VALUE);

    }


    /**
     * 测试: 信号量隔离
     */
    @Test
    public void testSemaphoreIsolationStrategy() throws Exception
    {
        /**
         *配置为信号量隔离
         */

        HystrixCommandProperties.Setter commandProperties = HystrixCommandProperties.Setter()
                .withExecutionIsolationStrategy(
                        //隔离策略为信号量隔离
                        HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE
                )
                //HystrixCommand.run() 方法允许的最大请求数
                .withExecutionIsolationSemaphoreMaxConcurrentRequests(4)
                //HystrixCommand.getFallback()方法的允许最大请求数目
                .withFallbackIsolationSemaphoreMaxConcurrentRequests(4);
        /**
         * 命令的配置实例
         */
        HystrixCommand.Setter setter = HystrixCommand.Setter
                .withGroupKey(HystrixCommandGroupKey.Factory.asKey("group1"))
                .andCommandKey(HystrixCommandKey.Factory.asKey("command1"))
                .andCommandPropertiesDefaults(commandProperties
                );

        /**
         * 模拟WEB容器的 IO线程池
         */
        ExecutorService mock_IO_threadPool = Executors.newFixedThreadPool(50);

        /**
         *  模拟WEB容器的并发50
         */
        for (int j = 1; j <= 50; j++)
        {
            mock_IO_threadPool.submit(() ->
            {
                /**
                 * RPC 调用
                 */
                new HttpGetterCommand(HELLO_TEST_URL, setter)
                        .toObservable()
                        .subscribe(s -> log.info(" result:{}", s));
            });
        }
        Thread.sleep(Integer.MAX_VALUE);
    }
}
