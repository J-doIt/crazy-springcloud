package com.crazymaker.demo.rxJava.basic;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import rx.Observable;
import rx.functions.Func1;


@Slf4j
public class FilterOperatorDemo
{

    /**
     * 演示  filter 基本使用
     */
    @Test
    public void filterDemo()
    {

        //通过filter过滤能被5整除的数
        Observable.range(1, 20)
                .filter(new Func1<Integer, Boolean>()
                {

                    @Override
                    public Boolean call(Integer integer)
                    {
                        return integer % 5 == 0;
                    }
                })
                .subscribe(i -> log.info("filter int->" + i));
    }

    /**
     * 演示  filter 基本使用 ，lamda 形式
     */
    @Test
    public void filterDemoLamda()
    {

        //通过filter过滤能被5整除的数
        Observable.range(1, 20)
                .filter(integer -> integer % 5 == 0)
                .subscribe(i -> log.info("filter int->" + i));
    }

    /**
     * 演示  distinct 基本使用
     */
    @Test
    public void distinctDemo()
    {

        Observable.just("apple", "pair", "banana", "apple", "pair" )
                .distinct()
                .subscribe(s -> log.info("distinct s->" + s));
    }


}
