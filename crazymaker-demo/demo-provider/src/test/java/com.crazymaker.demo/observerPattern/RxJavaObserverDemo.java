package com.crazymaker.demo.observerPattern;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import rx.Emitter;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Action1;


@Slf4j
public class RxJavaObserverDemo
{

    /**
     * 演示  RxJava 中的 Observer 模式
     */
    @Test
    public void rxJavaBaseUse()
    {


        //被观察者（ 主题 ）
        Observable observable = Observable.create(
                new Action1<Emitter<String>>()
                {
                    @Override
                    public void call(Emitter<String> emitter)
                    {
                        emitter.onNext("apple");
                        emitter.onNext("banana");
                        emitter.onNext("pear");
                        emitter.onCompleted();
                    }
                }, Emitter.BackpressureMode.NONE);

        //订阅者（观察者）
        Subscriber<String> subscriber = new Subscriber<String>()
        {
            @Override
            public void onNext(String s)
            {
                log.info("onNext: {}", s);
            }

            @Override
            public void onCompleted()
            {
                log.info("onCompleted");
            }

            @Override
            public void onError(Throwable e)
            {
                log.info("onError");
            }
        };

        //订阅：Observable与Subscriber之间依然通过subscribe()进行关联。
        observable.subscribe(subscriber);

    }

    /**
     * 演示  RxJava 中的 不完整观察者
     */
    @Test
    public void rxJavaActionDemo()
    {
//        Observable<String> observable = Observable.just("apple", "banana", "pear");

        Observable observable = Observable.create(
                new Action1<Emitter<String>>() {
                    @Override
                    public void call(Emitter<String> emitter) {
                        emitter.onNext("apple");
                        emitter.onNext("banana");
                        emitter.onNext("pear");
                        emitter.onCompleted();
                    }
                },Emitter.BackpressureMode.NONE);


        Action1<String> onNextAction = new Action1<String>()
        {
            @Override
            public void call(String s)
            {
                log.info(s);
            }
        };
        Action1<Throwable> onErrorAction = new Action1<Throwable>()
        {
            @Override
            public void call(Throwable throwable)
            {
                log.info("onError,Error Info is:" + throwable.getMessage());
            }
        };
        Action0 onCompletedAction = new Action0()
        {
            @Override
            public void call()
            {
                log.info("onCompleted");
            }
        };
        log.info("第1次订阅：");
        // 根据 onNextAction 来定义 onNext()
        observable.subscribe(onNextAction);
        log.info("第2次订阅：");
        // 根据 onNextAction 来定义 onNext()、根据 onErrorAction 来定义 onError()
        observable.subscribe(onNextAction, onErrorAction);
        log.info("第3次订阅：");
        // 根据 onNextAction 来定义 onNext()、根据 onErrorAction 来定义 onError()、onCompletedAction 来定义 onCompleted()
        observable.subscribe(onNextAction, onErrorAction, onCompletedAction);
    }

    /**
     * 演示  RxJava 中的 lamda表达式实现
     */
    @Test
    public void rxJavaActionLamda()
    {
        Observable<String> observable = Observable.just("apple", "banana", "pear");
        log.info("第1次订阅：");
        // 使用Action1 函数式实现，来定义 onNext回调
        observable.subscribe(s -> log.info(s));
        log.info("第2次订阅：");
        // 使用Action1 函数式实现， 来定义 onNext 回调
        // 使用Action1 函数式实现，来定义 onError 回调
        observable.subscribe(
                s -> log.info(s),
                e -> log.info("Error Info is:" + e.getMessage()));
        log.info("第3次订阅：");
        // 使用Action1 函数式实现， 来定义 onNext 回调
        // 使用Action1 函数式实现，来定义 onError 回调
        // 使用Action0 函数式实现，来定义 onCompleted 回调
        observable.subscribe(
                s -> log.info(s),
                e -> log.info("Error Info is:" + e.getMessage()),
                () -> log.info("onCompleted 弹射结束"));
    }
}
