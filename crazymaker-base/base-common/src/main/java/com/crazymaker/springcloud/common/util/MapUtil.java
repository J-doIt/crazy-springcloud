package com.crazymaker.springcloud.common.util;

import org.apache.commons.beanutils.BeanUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class MapUtil
{

    //pojo转map    

    public static Map<String, Object> pojo2Map(Object obj) throws Exception
    {

        Map<String, Object> map = new HashMap<String, Object>();
        Class<?> clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields)
        {
            PropertyDescriptor pd = new PropertyDescriptor(field.getName(), clazz);
            Method getMethod = pd.getReadMethod();
            Object o = getMethod.invoke(obj);
            map.put(field.getName(), o);
        }
        return map;
    }

    //Map 转 pojo对象
    public static <T> T map2Object(
            Map<String, Object> map,
            Class<T> beanClass)
    {
        if (map == null)
            return null;

        T obj = null;
        try
        {
            obj = beanClass.newInstance();
            BeanUtils.populate(obj, map);
        } catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }


        return obj;
    }

}
