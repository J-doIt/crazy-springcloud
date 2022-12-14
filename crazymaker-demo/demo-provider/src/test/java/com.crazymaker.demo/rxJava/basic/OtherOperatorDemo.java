package com.crazymaker.demo.rxJava.basic;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import rx.Observable;

import java.util.concurrent.TimeUnit;

@Slf4j
public class OtherOperatorDemo
{

    /**
     * 演示  interval  转换
     */
    @Test
    public void intervalDemo() throws InterruptedException
    {
        Observable
                .interval(100, TimeUnit.MILLISECONDS)
                .subscribe(aLong -> log.info(aLong.toString()));

        Thread.sleep(Integer.MAX_VALUE);
    }

    /**
     * 演示   倒计时
     */
    @Test
    public void takeDemo() throws InterruptedException
    {
        Observable.interval(1, TimeUnit.SECONDS) //设置间隔执行
                .take(10) //10秒倒计时
                .map(aLong -> 10 - aLong)
                .subscribe(aLong -> log.info(aLong.toString()));
        Thread.sleep(Integer.MAX_VALUE);
    }


}