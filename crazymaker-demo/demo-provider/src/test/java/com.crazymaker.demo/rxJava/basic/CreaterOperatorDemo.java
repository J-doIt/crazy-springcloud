package com.crazymaker.demo.rxJava.basic;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import rx.Observable;

import java.util.Arrays;
import java.util.List;


@Slf4j
public class CreaterOperatorDemo
{

    /**
     * 演示  just 基本使用
     */
    @Test
    public void justDemo()
    {
        //发送一个字符串"hello world"
        Observable.just("hello world" )
                .subscribe(s -> log.info("just string->" + s));

        //逐一发送1,2,3,4这四个整数
        Observable.just(1, 2, 3, 4)
                .subscribe(i -> log.info("just int->" + i));
    }

    /**
     * 演示  from 基本使用
     */
    @Test
    public void fromDemo()
    {
        //逐一发送一个字符数组的每个元素
        String[] items = {"a", "b", "c", "d", "e", "f"};
        Observable.from(items)
                .subscribe(s -> log.info("just string->" + s));

        //逐一发送送一个字符数组的每个元素
        Integer[] array = {1, 2, 3, 4};
        List<Integer> list = Arrays.asList(array);
        Observable.from(list)
                .subscribe(i -> log.info("just int->" + i));
    }

    /**
     * 演示  range 基本使用
     */
    @Test
    public void rangeDemo()
    {

        //逐一发一组范围内的整数序列
        Observable.range(1, 10)
                .subscribe(i -> log.info("just int->" + i));
    }

}
