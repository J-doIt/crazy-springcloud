package com.crazymaker.demo.observerPattern;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ObserverA implements Observer
{

    //观察者状态
    private String observerState;

    @Override
    public void update(String newState)
    {
        //更新观察者状态，让它与目标状态一致
        observerState = newState;
        log.info("目前的观察者的状态为：" + observerState);
    }
}

