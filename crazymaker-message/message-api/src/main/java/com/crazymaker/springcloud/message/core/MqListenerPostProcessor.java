package com.crazymaker.springcloud.message.core;


import com.crazymaker.springcloud.message.annotation.MqSubscriber;
import com.crazymaker.springcloud.message.annotation.TopicConsumer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class MqListenerPostProcessor implements BeanPostProcessor, Ordered, BeanFactoryAware, BeanClassLoaderAware, EnvironmentAware, SmartInitializingSingleton
{
    public static final int INIT_ORDER = 2147483647;
    private final Log logger = LogFactory.getLog(this.getClass());
    private BeanFactory beanFactory;
    private final MqListenerEndpointRegistry registry = new MqListenerEndpointRegistry();
    private final ConcurrentMap<Class<?>, TypeMetadata> typeCache = new ConcurrentHashMap();

    public int getOrder()
    {
        return INIT_ORDER;
    }

    public MqListenerPostProcessor()
    {
    }


    public void setBeanFactory(BeanFactory beanFactory)
    {
        this.beanFactory = beanFactory;

        RabbitMqEndpoint endpoint = beanFactory.getBean(RabbitMqEndpoint.class);

        endpoint.setSingleton(endpoint);
    }

    public void setBeanClassLoader(ClassLoader classLoader)
    {
    }

    public void setEnvironment(Environment environment)
    {
        String property = (String) environment.getProperty("spring.rabbitmq.emptyStringArguments", String.class);


    }

    public void afterSingletonsInstantiated()
    {
        this.registry.setBeanFactory(this.beanFactory);


        logger.info(" to set sth !" );
        //todo destroy Container

        this.registry.afterPropertiesSet();
        this.typeCache.clear();
    }

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException
    {
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException
    {
        Class<?> targetClass = AopUtils.getTargetClass(bean);
        MqListenerPostProcessor.TypeMetadata metadata =
                this.typeCache.computeIfAbsent(targetClass, this::buildMetadata);
        HandlerMethod[] handlerMethods = metadata.handlerMethods;
        int length = handlerMethods.length;

        for (int i = 0; i < length; ++i)
        {
            HandlerMethod lm = handlerMethods[i];
            TopicConsumer[] annotations = lm.annotations;
            int var10 = annotations.length;

            for (int j = 0; j < var10; ++j)
            {
                TopicConsumer topicConsumer = annotations[j];
                this.processListener(metadata, topicConsumer, lm.method, bean, beanName);
            }
        }


        return bean;
    }

    private MqListenerPostProcessor.TypeMetadata buildMetadata(Class<?> targetClass)
    {

        Collection<MqSubscriber> classLevelListeners = this.findHandlerAnnotations(targetClass);
        boolean hasClassLevelListeners = classLevelListeners.size() > 0;
        List<HandlerMethod> methods = new ArrayList();
        ReflectionUtils.doWithMethods(targetClass, (method) ->
        {
            Collection<TopicConsumer> listenerAnnotations = this.findHandlerAnnotations(method);
            if (listenerAnnotations.size() > 0)
            {
                methods.add(new HandlerMethod(method, (TopicConsumer[]) listenerAnnotations.toArray(new TopicConsumer[listenerAnnotations.size()])));
            }
        }, ReflectionUtils.USER_DECLARED_METHODS);

        if (methods.isEmpty())
        {
            return MqListenerPostProcessor.TypeMetadata.EMPTY;
        }

        HandlerMethod[] handlers = (HandlerMethod[]) methods.toArray(new HandlerMethod[methods.size()]);
        MqSubscriber[] listeners = (MqSubscriber[]) classLevelListeners.toArray(new MqSubscriber[classLevelListeners.size()]);

        return new MqListenerPostProcessor.TypeMetadata(handlers, listeners);

    }

    private Collection<MqSubscriber> findHandlerAnnotations(Class<?> clazz)
    {
        Set<MqSubscriber> listeners = new HashSet();
        MqSubscriber ann = (MqSubscriber) AnnotationUtils.findAnnotation(clazz, MqSubscriber.class);
        if (ann != null)
        {
            listeners.add(ann);
        }
        return listeners;
    }

    private Collection<TopicConsumer> findHandlerAnnotations(Method method)
    {
        Set<TopicConsumer> listeners = new HashSet();
        TopicConsumer ann = AnnotationUtils.findAnnotation(method, TopicConsumer.class);
        if (ann != null)
        {
            listeners.add(ann);
        }
        return listeners;
    }


    protected void processListener(TypeMetadata metadata, TopicConsumer handler, Method method, Object bean, String beanName)
    {
        Method methodToUse = this.checkProxy(method, bean);
        logger.debug("processListener" );

        boolean flag = RabbitMqEndpoint.getSingleton().addConsumer(metadata.topic(), handler.group(), bean, methodToUse);
        if (!flag)
        {
            logger.error("增加了重复监听器，请排查 topic:" + metadata.topic() + "  group:" + handler.group());
        }
    }


    private Method checkProxy(Method method, Object bean)
    {
        if (AopUtils.isJdkDynamicProxy(bean))
        {
            try
            {
                method = bean.getClass().getMethod(method.getName(), method.getParameterTypes());
                Class<?>[] proxiedInterfaces = ((Advised) bean).getProxiedInterfaces();
                Class[] interfaces = proxiedInterfaces;
                int length = proxiedInterfaces.length;
                int i = 0;

                while (i < length)
                {
                    Class iface = interfaces[i];

                    try
                    {
                        method = iface.getMethod(method.getName(), method.getParameterTypes());
                        break;
                    } catch (NoSuchMethodException var9)
                    {
                        ++i;
                    }
                }
            } catch (SecurityException var10)
            {
                ReflectionUtils.handleReflectionException(var10);
            } catch (NoSuchMethodException var11)
            {
                throw new IllegalStateException(String.format("@MqSubscriber method '%s' found on bean target class '%s', but not found in any interface(s) for bean JDK proxy. Either pull the method up to an interface or switch to subclass (CGLIB) proxies by setting proxy-target-class/proxyTargetClass attribute to 'true'", method.getName(), method.getDeclaringClass().getSimpleName()));
            }
        }

        return method;
    }


    private static class HandlerMethod
    {
        final Method method;
        final TopicConsumer[] annotations;

        HandlerMethod(Method method, TopicConsumer[] annotations)
        {
            this.method = method;
            this.annotations = annotations;
        }
    }

    private static class TypeMetadata
    {
        final HandlerMethod[] handlerMethods;
        final MqSubscriber[] classAnnotations;
        static final TypeMetadata EMPTY = new TypeMetadata();

        private TypeMetadata()
        {
            this.handlerMethods = new HandlerMethod[0];
            this.classAnnotations = new MqSubscriber[0];
        }

        TypeMetadata(HandlerMethod[] multiMethods, MqSubscriber[] listeners)
        {
            this.handlerMethods = multiMethods;
            this.classAnnotations = listeners;
        }

        public String topic()
        {
            if (classAnnotations == null || classAnnotations.length < 1)
            {
                return null;
            }
            return classAnnotations[0].topic();
        }
    }


}
