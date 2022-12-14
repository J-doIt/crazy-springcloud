package com.crazymaker.springcloud.standard.config;


import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.HystrixThreadPoolProperties;
import com.netflix.hystrix.strategy.HystrixPlugins;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestVariable;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestVariableLifecycle;
import com.netflix.hystrix.strategy.eventnotifier.HystrixEventNotifier;
import com.netflix.hystrix.strategy.executionhook.HystrixCommandExecutionHook;
import com.netflix.hystrix.strategy.metrics.HystrixMetricsPublisher;
import com.netflix.hystrix.strategy.properties.HystrixPropertiesStrategy;
import com.netflix.hystrix.strategy.properties.HystrixProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
public class FeignHystrixConcurrencyStrategy extends HystrixConcurrencyStrategy
{
    Logger log = LoggerFactory.getLogger(this.getClass());

    private HystrixConcurrencyStrategy delegate;

    public FeignHystrixConcurrencyStrategy()
    {
        try
        {
            this.delegate = HystrixPlugins.getInstance().getConcurrencyStrategy();
            if (this.delegate instanceof FeignHystrixConcurrencyStrategy)
            {
                log.error("死循环..." );
                return;
            }

            HystrixPlugins ins = HystrixPlugins.getInstance();
            HystrixCommandExecutionHook commandExecutionHook = ins.getCommandExecutionHook();

            HystrixEventNotifier eventNotifier = ins.getEventNotifier();
            HystrixMetricsPublisher metricsPublisher = ins.getMetricsPublisher();
            HystrixPropertiesStrategy propertiesStrategy = ins.getPropertiesStrategy();
            this.logCurrentStateOfHystrixPlugins(eventNotifier, metricsPublisher, propertiesStrategy);

            HystrixPlugins.reset();
            HystrixPlugins instance = HystrixPlugins.getInstance();
            instance.registerConcurrencyStrategy(this);
            instance.registerCommandExecutionHook(commandExecutionHook);
            instance.registerEventNotifier(eventNotifier);
            instance.registerMetricsPublisher(metricsPublisher);
            instance.registerPropertiesStrategy(propertiesStrategy);
        } catch (Exception e)
        {
            log.error("Failed to register Sleuth Hystrix Concurrency Strategy", e);
        }
    }

    private void logCurrentStateOfHystrixPlugins(HystrixEventNotifier eventNotifier,
                                                 HystrixMetricsPublisher metricsPublisher,
                                                 HystrixPropertiesStrategy propertiesStrategy)
    {
        if (log.isDebugEnabled())
        {
            log.debug("Current Hystrix plugins configuration is [" + "concurrencyStrategy ["
                    + this.delegate + "]," + "eventNotifier [" + eventNotifier + "]," + "metricPublisher ["
                    + metricsPublisher + "]," + "propertiesStrategy [" + propertiesStrategy + "]," + "]" );
            log.debug("Registering Sleuth Hystrix Concurrency Strategy." );
        }
    }

    @Override
    public <T> Callable<T> wrapCallable(Callable<T> callable)
    {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        return new WrappedCallable<>(callable, requestAttributes);
    }

    @Override
    public ThreadPoolExecutor getThreadPool(HystrixThreadPoolKey threadPoolKey,
                                            HystrixProperty<Integer> corePoolSize,
                                            HystrixProperty<Integer> maximumPoolSize,
                                            HystrixProperty<Integer> keepAliveTime,
                                            TimeUnit unit, BlockingQueue<Runnable> workQueue)
    {
        return this.delegate.getThreadPool(threadPoolKey, corePoolSize, maximumPoolSize, keepAliveTime,
                unit, workQueue);
    }

    @Override
    public ThreadPoolExecutor getThreadPool(HystrixThreadPoolKey threadPoolKey,
                                            HystrixThreadPoolProperties threadPoolProperties)
    {
        return this.delegate.getThreadPool(threadPoolKey, threadPoolProperties);
    }

    @Override
    public BlockingQueue<Runnable> getBlockingQueue(int maxQueueSize)
    {
        return this.delegate.getBlockingQueue(maxQueueSize);
    }

    @Override
    public <T> HystrixRequestVariable<T> getRequestVariable(HystrixRequestVariableLifecycle<T> rv)
    {
        return this.delegate.getRequestVariable(rv);
    }

    static class WrappedCallable<T> implements Callable<T>
    {
        private final Callable<T> target;
        private final RequestAttributes requestAttributes;

        public WrappedCallable(Callable<T> target, RequestAttributes requestAttributes)
        {
            this.target = target;
            this.requestAttributes = requestAttributes;
        }

        @Override
        public T call() throws Exception
        {
            try
            {
                RequestContextHolder.setRequestAttributes(requestAttributes);
                return target.call();
            } finally
            {
                RequestContextHolder.resetRequestAttributes();
            }
        }
    }
}
