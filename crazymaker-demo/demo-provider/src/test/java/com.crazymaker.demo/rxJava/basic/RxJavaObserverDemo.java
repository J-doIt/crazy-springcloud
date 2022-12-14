package com.crazymaker.demo.rxJava.basic;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Action1;

import java.util.Arrays;
import java.util.List;


@Slf4j
public class RxJavaObserverDemo
{


    /**
     * 演示  from 创建操作符的使用
     */
    @Test
    public void rxJavaFromTest()
    {
        formObserverDemo();
    }

    //from 创建操作符 ， 可将其他种类的对象和数据类型转换为 Observable。
    private void formObserverDemo()
    {
        List<String> fruitList = Arrays.asList("apple", "orange");
        Observable.from(fruitList).subscribe(new Action1<String>()
        {
            @Override
            public void call(String fruit)
            {
                log.info("fruit = {}", fruit);
            }
        });
        log.info("--------  演示 链式调用 ---------");
        Observable.from(fruitList)
                .subscribe(fruit -> log.info("fruit = {}", fruit));
    }

    @Test
    public void doTest()
    {


        //被观察者（ 主题 ）
        Observable observable = Observable.unsafeCreate(
                new Observable.OnSubscribe<Integer>()
                {
                    @Override
                    public void call(Subscriber<? super Integer> subscriber)
                    {
                        for (int i = 0; i < 5; i++)
                        {
                            log.info("produce ->" + i);
                            subscriber.onNext(i);

                        }
                        subscriber.onCompleted();
                    }
                });


        observable
                // 1. 当Observable每发送1次数据事件就会调用1次
                .doOnEach(

                        new Action1<Integer>()
                        {
                            @Override
                            public void call(Integer sum)
                            {
                                log.info(" =============doOnEach: {} ", sum);
                            }
                        }

                )
                // 2. 执行Next事件前调用
                .doOnNext(new Action1<Integer>()
                {
                    @Override
                    public void call(Integer integer)
                    {
                        System.out.println("============doOnNext: " + integer);
                    }
                })

                // 4. Observable正常发送事件完毕后调用
                .doOnCompleted(new Action0()
                {
                    public void call()
                    {
                        System.out.println("=============doOnCompleted: ");
                    }
                })
                // 5. Observable发送错误事件时调用
                .doOnError(new Action1<Throwable>()
                {
                    @Override
                    public void call(Throwable throwable)
                    {
                        System.out.println("=========doOnError: " + throwable.getMessage());
                    }
                })
                // 6. 观察者订阅时调用
                .doOnSubscribe(new Action0()
                {
                    public void call()
                    {
                        System.out.println("==========doOnSubscribe: ");
                    }
                })
                // 7. Observable发送事件完毕后调用，无论正常发送完毕 / 异常终止
                .doAfterTerminate(
                        new Action0()
                        {
                            public void call()
                            {
                                System.out.println("==========doAfterTerminate: ");
                            }
                        }
                )
                // 8. 最后执行
                .doOnTerminate(new Action0()
                {
                    public void call()
                    {
                        System.out.println("==========doOnTerminate: ");
                    }
                })
                .subscribe(i -> log.info("i = {}", i));
    }


}
