package com.crazymaker.demo.rxJava.defer;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.schedulers.Schedulers;

@Slf4j
public class ValueHolderTester
{
    @Data
    public class ValueHolder
    {

        private String value;

        public Observable<String> observable()
        {
            return Observable.just(value);
        }
    }


    @Data
    public class ValueHolderDefered
    {

        private String value;

        public Observable<String> observable()
        {
            return Observable.defer(new Func0<Observable<String>>()
            {
                public Observable<String> call()
                {
                    return Observable.just(value);
                }
            });
        }
    }

    @Test
    public void testNoDefer() throws InterruptedException
    {
        ValueHolder value = new ValueHolder();
        Observable<String> observable = value.observable();
        value.setValue(String.valueOf(100));
        observable
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<String>()
                {

                    @Override
                    public void call(String s)
                    {
                        log.info(" no Defer ,value 的新值为->" + s);
                    }
                });

        Thread.sleep(Integer.MAX_VALUE);
    }


    @Test
    public void testDefer() throws InterruptedException
    {
        ValueHolderDefered value = new ValueHolderDefered();
        Observable<String> observable = value.observable();
        value.setValue(String.valueOf(100));
        observable
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<String>()
                {

                    @Override
                    public void call(String s)
                    {
                        log.info("with Defer ,value 的新值为->" + s);
                    }
                });

        Thread.sleep(Integer.MAX_VALUE);
    }
}
