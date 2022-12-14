package com.crazymaker.demo.rxJava.basic;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import rx.Emitter;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

@Slf4j

public class BackpressureDemo
{
    /**
     * 演示  不使用背压
     */
    @Test
    public void testNoBackpressure() throws InterruptedException
    {
        //被观察者（ 主题 ）
        Observable observable = Observable.unsafeCreate(
                new Observable.OnSubscribe<String>()
                {
                    @Override
                    public void call(Subscriber<? super String> subscriber)
                    {
                        ////无限循环
                        for (int i = 0; ; i++)
                        {
//                            log.info("produce ->" + i);
                            subscriber.onNext(String.valueOf(i));
                        }
                    }
                });


        //订阅者（观察者）
        Action1<String> subscriber = new Action1<String>()
        {
            public void call(String s)
            {
                try
                {
                    //每消费一次，间隔50毫秒
                    Thread.sleep(50);
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                log.info("consumer ->" + s);
            }

        };

        //订阅 : observable与subscriber之间依然通过subscribe()进行关联。

        observable
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribe(subscriber);

        Thread.sleep(Integer.MAX_VALUE);
    }

    /**
     * 演示  使用背压
     */
    @Test
    public void testBackPressure() throws InterruptedException
    {
        //被观察者（ 主题 ）
        Observable observable = Observable.create(
                new Action1<Emitter<String>>()
                {
                    @Override
                    public void call(Emitter<String> emitter)
                    {
                        ////无限循环
                        for (int i = 0; ; i++)
                        {
                            //log.info("produce ->" + i);
                            emitter.onNext(String.valueOf(i));
                        }
                    }
                }, Emitter.BackpressureMode.LATEST);


        //订阅者（观察者）
        Action1<String> subscriber = new Action1<String>()
        {
            public void call(String s)
            {
                try
                {
                    //每消费一次，间隔50毫秒
                    Thread.sleep(3);
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                log.info("consumer ->" + s);
            }

        };

        //订阅 : observable与subscriber之间依然通过subscribe()进行关联。

        observable
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribe(subscriber);

        Thread.sleep(Integer.MAX_VALUE);
    }
}