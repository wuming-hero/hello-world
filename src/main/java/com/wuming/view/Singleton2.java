package com.wuming.view;

/**
 * 懒汉模式
 * <p>
 * 1. 线程安全
 * 2. 延迟加载
 * 3. 性能低
 *
 * @author wuming
 * Created on 2020-11-18 07:31
 */
public class Singleton2 {

    private static Singleton2 singleton = null;

    /**
     * 私有构造器，防止实例化
     */
    private Singleton2() {

    }

    /**
     * 因为使用了synchronized关键字，所以性能会比较低
     * @return
     */
    public synchronized static Singleton2 getInstance() {
        if (singleton == null) {
            singleton = new Singleton2();
        }
        return singleton;
    }

}
