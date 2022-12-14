package com.crazymaker.demo.rxJava.basic;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

@Slf4j

public class SchedulerDemo
{
    /**
     * 演示  Schedulers 基本使用
     */
    @Test
    public void testScheduler() throws InterruptedException
    {
        //被观察者（ 主题 ）
        Observable observable = Observable.unsafeCreate(
                new Observable.OnSubscribe<String>()
                {
                    @Override
                    public void call(Subscriber<? super String> subscriber)
                    {
                        for (int i = 0; i < 5; i++)
                        {
                            log.info("produce ->" + i);
                            subscriber.onNext(String.valueOf(i));

                        }
                        subscriber.onCompleted();
                    }
                });

        //订阅 Observable与Subscriber之间依然通过subscribe()进行关联。

        observable
                //使用 具有线程缓存机制 的可复用线程
                .subscribeOn(Schedulers.io())
                //每执行一个任务时创建一个新的线程
                .observeOn(Schedulers.newThread())
                .subscribe(s ->
                {
                    log.info("consumer ->" + s);
                });

        Thread.sleep(Integer.MAX_VALUE);
    }
}