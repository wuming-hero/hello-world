package com.wuming.view;

/**
 * 饿汉模式
 *
 * @author wuming
 * Created on 2020-11-18 07:36
 */
public class Singleton2 {

    private static final Singleton2 singleton2 = new Singleton2();

    private Singleton2() {

    }

    public static Singleton2 getInstance() {
        return singleton2;
    }
}
