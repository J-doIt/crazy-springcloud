package com.crazymaker.demo.observerPattern;

import lombok.extern.slf4j.Slf4j;

@Slf4j

public class ConcreteSubject extends Subject
{


    private String state;

    public String getState()
    {
        return state;
    }

    public void change(String newState)
    {
        state = newState;
        log.info("concreteSubject state:" + newState);

        //状态发生改变，通知观察者
        notifyObservers(newState);
    }
}
