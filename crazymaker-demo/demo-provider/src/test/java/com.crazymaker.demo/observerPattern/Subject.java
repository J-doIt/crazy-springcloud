package com.crazymaker.demo.observerPattern;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Subject
{
    //保存订阅过自己的观察者对象
    private List<Observer> observers = new ArrayList<>();

    //观察者对象订阅
    public void add(Observer observer)
    {
        observers.add(observer);
        log.info("add an observer");
    }

    //观察者对象注销
    public void remove(Observer observer)
    {
        observers.remove(observer);
        log.info("remove an observer");
    }

    //通知所有注册的观察者对象
    public void notifyObservers(String newState)
    {
        for (Observer observer : observers)
        {
            observer.update(newState);
        }
    }
}
