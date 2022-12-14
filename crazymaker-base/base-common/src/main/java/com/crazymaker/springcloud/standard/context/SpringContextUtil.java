package com.crazymaker.springcloud.standard.context;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Created by 尼恩 on 2019/7/18.
 */
@Component
public class SpringContextUtil
        implements ApplicationContextAware
{
    /**
     * 上下文对象实例
     */
    private static org.springframework.context.ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(org.springframework.context.ApplicationContext applicationContext) throws BeansException
    {
        this.applicationContext = applicationContext;
    }

    /**
     * 获取applicationContext
     *
     * @return
     */
    public static org.springframework.context.ApplicationContext getApplicationContext()
    {
        return applicationContext;
    }

    /**
     * 通过name获取 Bean.
     *
     * @param name
     * @return
     */
    public static Object getBean(String name)
    {
        return getApplicationContext().getBean(name);
    }

    /**
     * 通过class获取Bean.
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T getBean(Class<T> clazz)
    {
        return getApplicationContext().getBean(clazz);
    }


    /**
     * 获取getBeanDefinitionNames.
     *
     * @return getBeanDefinitionNames
     */
    public static List<String> getBeanDefinitionNames()
    {
        String[] names = getApplicationContext().getBeanDefinitionNames();
        return Arrays.asList(names);

    }

    /**
     * 通过name,以及Clazz返回指定的Bean
     *
     * @param name
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T getBean(String name, Class<T> clazz)
    {
        return getApplicationContext().getBean(name, clazz);
    }
}