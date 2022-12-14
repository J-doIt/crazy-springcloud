package com.crazymaker.springcloud.common.page;


import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

/**
 * 对象适配器
 */
public class DataAdapter
{

    public static <F, T> PageOut<T> adapterPage(Page<F> page, Class<T> elementClass)
    {
        List<F> list = page.getContent();
        PageOut<T> pageOut = new PageOut<>();
        //调整一下，jpa 是从0开始计算的
        pageOut.setCurPage(page.getPageable().getPageNumber() + 1);
        pageOut.setPages((int) Math.ceil((double) page.getTotalElements() / page.getPageable().getPageSize()));
        pageOut.setPageSize(page.getPageable().getPageSize());
        pageOut.setCount(page.getTotalElements());
        List newList = convertList(list, elementClass);
        pageOut.setData(newList);
        return pageOut;
    }

    public static <F, T> List<T> convertList(List<F> list, Class<T> targetClass)
    {
        if (null == list)
        {
            return null;
        }
        List<T> newList = new ArrayList<>();
        list.forEach(temp ->
        {
            T obj = null;
            try
            {
                obj = targetClass.newInstance();
            } catch (Exception e)
            {
                e.printStackTrace();
            }
            BeanUtils.copyProperties(temp, obj);
            newList.add(obj);
        });
        return newList;
    }

    public static <F, T> T convert(F from, Class<T> targetClass)
    {
        T obj = null;
        try
        {
            obj = targetClass.newInstance();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        BeanUtils.copyProperties(from, obj);

        return obj;
    }
}
