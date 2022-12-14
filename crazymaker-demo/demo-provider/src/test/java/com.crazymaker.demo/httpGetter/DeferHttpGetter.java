package com.crazymaker.demo.httpGetter;

import com.crazymaker.springcloud.common.util.HttpRequestUtil;
import lombok.extern.slf4j.Slf4j;
import rx.Observable;
import rx.functions.Func0;

@Slf4j
public class DeferHttpGetter
{
    private String url;

    String responseData = null;

    public DeferHttpGetter(String url)
    {
        this.url = url;
    }

    public Observable<String> responsableObservable()
    {

        return Observable.defer(new Func0<Observable<String>>()
        {
            public Observable<String> call()
            {

                log.info("Observable 开始执行..... ");

                /**
                 * 简单发送url请求，取得字符串结果
                 */
                try
                {
                    responseData = HttpRequestUtil.simpleGet(url);

                } catch (Exception e)
                {
                    return Observable.error(e);

                }

                log.info("Observable 执行结束！ ");
                return Observable.just(responseData);
            }
        });

    }

}
