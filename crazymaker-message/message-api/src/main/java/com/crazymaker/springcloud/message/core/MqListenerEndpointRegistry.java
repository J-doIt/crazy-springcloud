package com.crazymaker.springcloud.message.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.event.ContextRefreshedEvent;

import java.io.IOException;

public class MqListenerEndpointRegistry implements DisposableBean, SmartLifecycle, ApplicationContextAware, ApplicationListener<ContextRefreshedEvent>
{
    protected final Log logger = LogFactory.getLog(this.getClass());
    private int phase = 2147483647;
    private ConfigurableApplicationContext applicationContext;
    private boolean contextRefreshed;
    private BeanFactory beanFactory;

    public MqListenerEndpointRegistry()
    {
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        if (applicationContext instanceof ConfigurableApplicationContext)
        {
            this.applicationContext = (ConfigurableApplicationContext) applicationContext;
        }

    }


    public void destroy()
    {
        logger.debug("MqListenerEndpointRegistry destroy!" );

        RabbitMqEndpoint.getSingleton().destroy();
    }

    public int getPhase()
    {
        return this.phase;
    }

    public boolean isAutoStartup()
    {
        return true;
    }

    public void start()
    {
        logger.debug("MqListenerEndpointRegistry starting!" );
    }


    public void stop()
    {

        logger.debug("MqListenerEndpointRegistry stopped!" );
    }

    public void stop(Runnable callback)
    {
        logger.debug("MqListenerEndpointRegistry stopped!" );

        //停止channel 连接
    }

    public boolean isRunning()
    {

        //status
        return true;
    }


    public void onApplicationEvent(ContextRefreshedEvent event)
    {
        if (event.getApplicationContext().equals(this.applicationContext))
        {
            this.contextRefreshed = true;
        }

    }


    public void setBeanFactory(BeanFactory beanFactory)
    {
        this.beanFactory = beanFactory;
    }


    public void afterPropertiesSet()
    {

        this.registerAllEndpoints();
    }


    protected void registerAllEndpoints()
    {
        // 启动任务

        RabbitMqEndpoint.getSingleton().init();
        try
        {
            RabbitMqEndpoint.getSingleton().declareQueueAndExchange();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
       /* synchronized(this.endpointDescriptors) {
            Iterator var2 = this.endpointDescriptors.iterator();

            while(var2.hasNext()) {
                RabbitListenerEndpointRegistrar.AmqpListenerEndpointDescriptor descriptor = (RabbitListenerEndpointRegistrar.AmqpListenerEndpointDescriptor)var2.next();
                this.endpointRegistry.registerListenerContainer(descriptor.endpoint, this.resolveContainerFactory(descriptor));
            }

            this.startImmediately = true;
        }*/
    }

}
