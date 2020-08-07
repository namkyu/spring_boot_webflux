package com.kyu.demo.observer;

/**
 * @Project : spring_boot_webflux
 * @Date : 2019-11-26
 * @Author : nklee
 * @Description :
 */
public interface Subject<T> {

    void registerObserver(Observer<T> observer);

    void unregisterObserver(Observer<T> observer);

    void notifyObservers(T event);
}
