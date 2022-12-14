package com.crazymaker.demo.httpGetter;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import rx.Observable;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static com.crazymaker.springcloud.demo.constants.TestConstants.HELLO_TEST_URL;

@Slf4j
public class HttpGetterTester
{

    /**
     * 测试 正常的创建操作符
     *
     * @throws InterruptedException
     */
    @Test
    public void testNoDefer() throws InterruptedException
    {
        AsynHttpGetter asynHttpGetter = new AsynHttpGetter(HELLO_TEST_URL);

        for (int i = 0; i < 100; i++)
        {
            Observable<String> observable = asynHttpGetter.responceObservable();
            log.info("test starting....");
            observable
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Action1<String>()
                    {
                        @Override
                        public void call(String s)
                        {
                            log.info("asynHttpGetter s == " + s);
                        }
                    });
        }
        Thread.sleep(Integer.MAX_VALUE);
    }

    /**
     * 测试 延迟创建操作符
     *
     * @throws InterruptedException
     */
    @Test
    public void testDefer() throws InterruptedException
    {


        DeferHttpGetter deferHttpGetter = new DeferHttpGetter(HELLO_TEST_URL);

        for (int i = 0; i < 100; i++)
        {
            Observable<String> observable = deferHttpGetter.responsableObservable();
            log.info("test starting....");
            observable
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Action1<String>()
                    {
                        @Override
                        public void call(String s)
                        {
                            log.info("testHttpGetter s == " + s);
                        }
                    });
        }
        Thread.sleep(Integer.MAX_VALUE);
    }


}
