package com.crazymaker.demo.drinkTea;

import lombok.extern.slf4j.Slf4j;
import rx.schedulers.Schedulers;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
public class DrinkTea
{
    /**
     *烧水工作
     */
    static   HotWaterJob hotJob = new HotWaterJob();

    /**
     * 清洗工作
     */
    static   WashCupJob washJob = new WashCupJob();

    /**
     *    主线程同步等待计算器
     */
    private static CountDownLatch     downLatch = new CountDownLatch(2);


    public static void main(String args[]) throws InterruptedException
    {
        log.info("开始准备..... ");

        /**
         * 订阅 烧水 可观察对象
         */
        hotJob.observable()
                .subscribeOn(Schedulers.io())
                .subscribe(
                        result -> hotFinished(result),
                        throwAble -> log.error("烧水失败，没茶喝了!"));
        /**
         * 订阅 清洗 可观察对象
         */
        washJob.observable()
                .subscribeOn(Schedulers.io())
                .subscribe(
                        result -> washFinished(result),
                        throwAble -> log.error("清洗茶杯失败，没茶喝了!"));

        /**
         * 等待
         */
        doDrink();
    }

    /**
     * 清洗完成的回调
     */
    private static void washFinished(Boolean result)
    {
        if (! result)
        {
            log.error("杯子清洗失败");
            return;
        }
        log.info("杯子清洗完成");
        downLatch.countDown();
    }
    /**
     * 烧水完成的回调
     */
    private static void hotFinished(boolean result)
    {
        if (! result)
        {
            log.error("烧水失败");
            return;
        }
        log.info("水烧好了");
        downLatch.countDown();
  }

    /**
     * 等待烧水和清洗
     */
    public static void doDrink() throws InterruptedException
    {

        downLatch.await(100, TimeUnit.SECONDS);
        if (hotJob.isWaterOk() && washJob.isCupOk())
        {
            log.info("泡茶喝");
        } else
        {
            log.error("喝茶失败");
        }

    }
}
