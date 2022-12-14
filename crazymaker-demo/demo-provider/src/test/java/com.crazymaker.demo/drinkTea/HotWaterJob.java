package com.crazymaker.demo.drinkTea;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import rx.Observable;
import rx.functions.Func0;
@Data
@Slf4j
public class HotWaterJob
{

    /**
     * 烧水完成的标志位
     */
    private boolean waterOk;

    /**
     * 烧水的操作
     */
    public void doHotWater() throws InterruptedException
    {
        log.info("洗好水壶");
        log.info("灌上凉水");
        log.info("放在火上");

        //线程睡眠一段时间，代表烧水中
        Thread.sleep(500);
        log.info("水开了");
        log.info("烧水工作，运行结束.");

        waterOk = true;

    }

    /**
     *  获取烧水 可观察的对象（Observable）
     */
    public Observable<Boolean> observable()
    {
        return Observable.defer(new Func0<Observable<Boolean>>()
        {
            public Observable<Boolean> call()
            {
                try
                {
                    doHotWater();
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                    log.info(" 发生异常被中断.");
                    waterOk = false;
                    return Observable.error(e);
                }
                return Observable.just(waterOk);
            }
        });

    }
}
