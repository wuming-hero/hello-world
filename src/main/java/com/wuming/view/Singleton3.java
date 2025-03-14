package com.wuming.view;

/**
 * 静态内部类方式
 * 利用了 classloader 机制来保证初始化instance 时只有一个线程,同时实现延迟加载。
 * 静态内部类在第一次被引用时才加载，这时候才会创建实例。这种方式不需要同步，而且由JVM保证线程安全
 * <p>
 * 1. Singleton 类被装载了，instance 不一定被初始化。因为 SingletonHolder 类没有被主动使用，
 * 只有通过显式调用 getInstance 方法时，才会显式装载 SingletonHolder 类，从而实例化 instance。
 * <p>
 * 优点：
 * 1. 线程安全
 * 2. 延迟加载
 * 3. 性能高
 *
 * @author wuming
 * Created on 2020-11-18 08:03
 */
public class Singleton3 {

    private Singleton3() {

    }

    public static Singleton3 getInstance() {
        return SingletonHolder.singleton4;
    }

    /**
     * 静态内部类
     */
    private static class SingletonHolder {
        private static final Singleton3 singleton4 = new Singleton3();

    }

}
