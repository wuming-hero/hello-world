package com.wuming.view;

/**
 * 这种方式同样利用了 classloader 机制来保证初始化 instance 时只有一个线程,
 * Singleton 类被装载了，instance 不一定被初始化。因为 SingletonHolder 类没有被主动使用，
 * 只有通过显式调用 getInstance 方法时，才会显式装载 SingletonHolder 类，从而实例化 instance。
 * @author wuming
 * Created on 2020-11-18 08:03
 */
public class Singleton4 {

    private static class SingletonHolder {
        private static final Singleton4 singleton4 =  new Singleton4();

    }

    private Singleton4() {

    }

    public Singleton4 getInstance() {
        return SingletonHolder.singleton4;
    }

}
