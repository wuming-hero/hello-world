package com.wuming.view.other;

/**
 * 双重检验锁方式实现单例模式
 *
 * @author manji
 * Created on 2025/3/13 20:17
 */
public class Singleton4 {

    /**
     * static 修饰的类变量
     *
     * volatile 修饰的类变量
     * 1. 禁止指令重排序：
     * instance = new Singleton() 在 JVM 中分为三步：
     *  1. 分配内存空间
     *  2. 初始化对象
     *  3. 将引用指向内存地址
     * 若未用 volatile，步骤 2 和 3 可能被重排序，导致其他线程获取到未完全初始化的对象。
     * 2. 保证可见性：确保一个线程对 instance 的修改能立即对其他线程可见。
     */
    private static volatile Singleton4 instance;

    /**
     * 私有构造函数
     */
    private Singleton4() {

    }

    private static Singleton4 getInstance() {
        // 第一次判空：避免每次调用 getInstance() 都进入同步块，减少锁竞争的开销。
        if(instance == null) {
            synchronized (Singleton4.class) {
                // 第二次判空：防止多个线程通过第一次检查后，在同步块内重复创建实例
                if(instance == null) {
                    instance = new Singleton4();
                }
            }
        }
        return instance;
    }
}
