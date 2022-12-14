package com.crazymaker.demo.rxJava.basic;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j

public class WindowDemo
{

    /**
     * window 创建操作符 创建滑动窗口
     * 演示 window 创建操作符   创建滑动窗口
     */
    @Test
    public void simpleWindowObserverDemo()
    {

        List<Integer> srcList = Arrays.asList(10, 11, 20, 21, 30, 31);

        Observable.from(srcList)
                .window(3)
                .flatMap(o -> o.toList())
                .subscribe(list -> log.info(list.toString()));
    }

    /**
     * window 创建操作符 创建滑动窗口
     * 演示 window 创建操作符   创建滑动窗口
     */
    @Test
    public void windowObserverDemo()
    {

        List<Integer> srcList = Arrays.asList(10, 11, 20, 21, 30, 31);

        Observable.from(srcList)
                .window(3, 1)
                .flatMap(o -> o.toList())
                .subscribe(list -> log.info(list.toString()));
    }

    /**
     * window 创建操作符 创建滑动窗口
     * 演示 window 滑动窗口和归并
     */
    @Test
    public void windowMergeDemo()
    {

        List<Integer> srcList = Arrays.asList(10, 11, 20, 21, 30, 31);
        Observable.merge(
                Observable.from(srcList)
                        .window(3, 1))
                .subscribe(integer -> log.info(integer.toString()));
    }

    /**
     * window 创建操作符 创建时间窗口
     * 演示  window 创建操作符   创建时间窗口
     */
    @Test
    public void timeWindowObserverDemo() throws InterruptedException
    {

        Observable eventStream = Observable
                .interval(100, TimeUnit.MILLISECONDS);
        eventStream.window(300, TimeUnit.MILLISECONDS)
                .flatMap(o -> ((Observable<Integer>) o).toList())
                .subscribe(list -> log.info(list.toString()));

        Thread.sleep(Integer.MAX_VALUE);
    }


    /**
     * 演示  hystrix 的  健康统计 metric
     */
    @Test
    public void hystrixTimewindowDemo() throws InterruptedException
    {
        //创建Random类对象
        Random random = new Random();

        //模拟 Hystrix event 事件流，每 100ms 发送一个 0或1随机值， 随机值为 0 代表失败，机值为 1 代表成功
        Observable eventStream = Observable
                .interval(100, TimeUnit.MILLISECONDS)
                .map(i -> random.nextInt(2));

        /**
         *完成桶内0值计数的聚合函数
         */
        Func1 reduceBucketToSummary = new Func1<Observable<Integer>, Observable<Long>>()
        {
            @Override
            public Observable<Long> call(Observable<Integer> eventBucket)
            {
                Observable<List<Integer>> olist = eventBucket.toList();
                Observable<Long> countValue = olist.map(list ->
                {
                    long count = list.stream().filter(i -> i == 0).count();
                    log.info("{} '0 count:{}", list.toString(), count);
                    return count;

                });
                return countValue;
            }
        };

        /**
         *桶计数流
         */
        Observable<Long> bucketedCounterStream = eventStream
                .window(300, TimeUnit.MILLISECONDS)
                .flatMap(reduceBucketToSummary);  // 将时间桶进行聚合，统计为事件值为0 的个数

        /**
         * 滑动窗口聚合函数
         */
        Func1 reduceWindowToSummary = new Func1<Observable<Long>, Observable<Long>>()
        {
            @Override
            public Observable<Long> call(Observable<Long> eventBucket)
            {
                return eventBucket.reduce(new Func2<Long, Long, Long>()
                {
                    @Override
                    public Long call(Long bucket1, Long bucket2)
                    {
                        /**
                         * 对窗口内的桶，进行的累加
                         */
                        return bucket1 + bucket2;
                    }
                });

            }
        };
        /**
         * 桶滑动统计流
         */
        Observable bucketedRollingCounterStream = bucketedCounterStream
                .window(3, 1)
                .flatMap(reduceWindowToSummary);// 将滑动窗口进行聚合
        bucketedRollingCounterStream.subscribe(sum -> log.info("滑动窗口的和：{}", sum));
        Thread.sleep(Integer.MAX_VALUE);
    }
}