package com.crazymaker.demo.httpGetter;

import com.crazymaker.springcloud.common.util.HttpRequestUtil;
import lombok.extern.slf4j.Slf4j;
import rx.Observable;
import rx.Subscriber;

import java.io.IOException;

@Slf4j
public class AsynHttpGetter
{
    private String url;

    String responseData = null;

    public AsynHttpGetter(String url)
    {
        this.url = url;
    }


    public Observable<String> responceObservable()
    {

        return Observable.unsafeCreate(new Observable.OnSubscribe<String>()
        {
            @Override
            public void call(Subscriber<? super String> subscriber)
            {
                log.info("Observable 开始执行..... ");

                /**
                 * 简单发送url请求，取得字符串结果
                 */
                try
                {
                    responseData = HttpRequestUtil.simpleGet(url);
                } catch (IOException e)
                {
                    e.printStackTrace();
                }

                subscriber.onNext(responseData);
                subscriber.onCompleted();

                log.info("Observable 执行结束！ ");
            }
        });

    }
}
