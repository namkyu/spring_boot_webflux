package com.kyu.demo.observer;

/**
 * @Project : spring_boot_webflux
 * @Date : 2019-11-26
 * @Author : nklee
 * @Description :
 */
public interface Observer<T> {

    void observe(T event);

}
