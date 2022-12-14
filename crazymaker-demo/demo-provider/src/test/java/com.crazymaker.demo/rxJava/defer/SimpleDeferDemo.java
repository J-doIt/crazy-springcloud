package com.crazymaker.demo.rxJava.defer;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import rx.Observable;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class SimpleDeferDemo
{


    /**
     * 演示 defer 延迟创建操作符
     */
    @Test
    public void deferDemo()
    {
        AtomicInteger foo = new AtomicInteger(100);
        Observable observable = Observable.just(foo.get());
        /**
         * 延迟创建
         */
        Observable dObservable = Observable.defer(() -> Observable.just(foo.get()));

        /**
         * 修改对象的值
         */
        foo.set(200);
        /**
         * 有观察者订阅
         */
        observable.subscribe(integer -> log.info("just emit {}", String.valueOf(integer)));
        /**
         * 有观察者订阅
         */
        dObservable.subscribe(integer -> log.info("defer just emit {}", String.valueOf(integer)));

    }
}
