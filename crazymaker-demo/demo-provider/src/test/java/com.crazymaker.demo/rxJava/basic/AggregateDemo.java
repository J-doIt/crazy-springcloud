package com.crazymaker.demo.rxJava.basic;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func2;

@Slf4j
public class AggregateDemo
{

    /**
     * 演示   count 计数操作符
     */
    @Test
    public void countDemo()
    {
        String[] items = {"one", "two", "three","fore"};
        Integer count = Observable
                .from(items)
                .count()
                .toBlocking().single();
        log.info("计数的结果为 {}",count);
    }

    /**
     * 演示   reduce 扫描操作符
     */
    @Test
    public void reduceDemo()
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
         * 使用 reduce 规约操作符
         */
        Observable.range(1, 5)
                .reduce(accumulator)
                .subscribe(new Action1<Integer>()
                {
                    @Override
                    public void call(Integer sum)
                    {
                        log.info(" 规约的结果: {} ", sum);
                    }
                });
    }


}