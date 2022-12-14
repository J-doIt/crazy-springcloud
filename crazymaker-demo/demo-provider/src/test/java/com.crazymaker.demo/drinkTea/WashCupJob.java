package com.crazymaker.demo.drinkTea;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import rx.Observable;
import rx.functions.Func0;
@Data
@Slf4j
public class WashCupJob
{
    /**
     * 清洗完成的标志位
     */
    private boolean cupOk;
    /**
     * 清洗的操作
     */
    public void doWash() throws InterruptedException
    {
        log.info("洗茶壶");
        log.info("洗茶杯");
        log.info("拿茶叶");
        //线程睡眠一段时间，代表清洗中
        Thread.sleep(500);
        log.info("洗完了");
        log.info(" 清洗工作  运行结束.");
        cupOk = true;

    }
    /**
     *  获取清洗的 可观察的对象（Observable）
     */
    public Observable<Boolean> observable()
    {
        return Observable.defer(new Func0<Observable<Boolean>>()
        {
            public Observable<Boolean> call()
            {
                try
                {
                    doWash();
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                    log.info(" 清洗工作 发生异常被中断.");
                    cupOk = false;
                    return Observable.error(e);
                }

                return Observable.just(cupOk);

            }
        });

    }
}
