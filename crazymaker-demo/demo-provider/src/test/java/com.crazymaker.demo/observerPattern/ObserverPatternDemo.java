package com.crazymaker.demo.observerPattern;

public class ObserverPatternDemo
{
    public static void main(String[] args)
    {
        //创建主题
        ConcreteSubject mConcreteSubject = new ConcreteSubject();
        //创建两个观察者
        Observer observerA = new ObserverA();
        Observer ObserverB = new ObserverA();
        //主题订阅
        mConcreteSubject.add(observerA);
        mConcreteSubject.add(ObserverB);
        //主题状态变更
        mConcreteSubject.change("倒计时结束，开始秒杀");
    }
}
