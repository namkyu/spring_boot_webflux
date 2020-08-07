package com.kyu.demo.observer;

/**
 * @Project : spring_boot_webflux
 * @Date : 2019-11-26
 * @Author : nklee
 * @Description :
 */
public class ConcreteObserverB implements Observer<String> {

    @Override
    public void observe(String event) {
        System.out.println("Observer B : " + event);
    }
}
