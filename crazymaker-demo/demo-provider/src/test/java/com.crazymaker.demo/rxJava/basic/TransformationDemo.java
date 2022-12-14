package com.crazymaker.demo.rxJava.basic;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func2;

@Slf4j
public class TransformationDemo
{


    /**
     * 演示  map  转换
     */
    @Test
    public void intervalDemo()
    {
        Observable.range(1, 4)
                .map(i -> i * i)
                .subscribe(i -> log.info(i.toString()));
    }


    /**
     * 演示  flapMap  转换
     */
    @Test
    public void flapMapDemo()
    {
        /**
         * 注意 flatMap 中的 just 所创建的是一个新的流
         */
        Observable.range(1, 4)
                .flatMap(i -> Observable.just(i * i, i * i + 1))
                .subscribe(i -> log.info(i.toString()));
    }

    /**
     * 演示  一个稍微复杂的 flapMap  转换
     */
    @Test
    public void flapMapDemo2()
    {
        Observable.range(1, 4)
                .flatMap(i -> Observable.range(1, i).toList())
                .subscribe(list -> log.info(list.toString()));
    }


    /**
     * 演示   Scan 扫描操作符
     */
    @Test
    public void scanDemo()
    {
        /**
         * 定义一个 accumulator 累积函数
         */
        Func2<Integer, Integer, Integer> accumulator = new Func2<Integer, Integer, Integer>()
        {
            @Override
            public Integer call(Integer input1, Integer input2)
            {
                log.info(" {} + {} = {}  ", input1, input2, input1 + input2);
                return input1 + input2;
            }
        };


        /**
         * 使用scan 进行流扫描
         */
        Observable.range(1, 5)
                .scan(accumulator)
                .subscribe(new Action1<Integer>()
                {
                    @Override
                    public void call(Integer sum)
                    {
                        log.info(" 累加的结果: {} ", sum);
                    }
                });
    }


}