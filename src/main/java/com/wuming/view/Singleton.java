package com.wuming.view;

/**
 * 懒汉模式
 * @author wuming
 * Created on 2020-11-18 07:31
 */
public class Singleton {

    private static Singleton singleton = null;

    private Singleton(){

    }

    public static Singleton getInstance() {
        if (singleton == null) {
            singleton = new Singleton();
        }
        return singleton;
    }

}
