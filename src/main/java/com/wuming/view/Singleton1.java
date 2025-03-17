package com.wuming.view;

/**
 * 饿汉模式
 *
 * 1. 线程安全
 * 2. 非延迟加载
 * 3. 性能高
 *
 * @author wuming
 * Created on 2020-11-18 07:36
 */
public class Singleton1 {

    private static final Singleton1 singleton2 = new Singleton1();

    private Singleton1() {

    }

    public static Singleton1 getInstance() {
        return singleton2;
    }
}
