package com.wuming.thread;

/**
 * Author: wuming
 * Created on 2017/7/25 10:22
 */
public class SynchronizedTest {

    public static void main(String[] args) {
        Foo foo1 = new Foo();
        Foo foo2 = new Foo();
        new Thread(foo1, "foo1").start();
        new Thread(foo2, "foo2").start();
    }


}

/**
 * 使用synchronized 关键字，达到在不同的对象实例上实现锁
 * 无论synchronized关键字加在方法上还是对象上，它取得的锁都是对象，而不是把一段代码或函数当作锁
 * 同步方法很可能还会被其他线程的对象访问
 * test1()和test2()的效果是一样的。
 */
class Foo implements Runnable {

    private static byte[] lock = new byte[0];

    @Override
    public void run() {
        test1();
    }

    /**
     * 这时synchronized锁定的是哪个对象呢？它锁定的是调用这个同步方法对象。
     * 也就是说，当一个对象f1在不同的线程中执行这个同步方法时，它们之间会形成互斥，达到同步的效果。
     * 但是这个对象所属的Class所产生的另一对象f2却可以任意调用这个被加了synchronized关键字的方法,
     * f1  f2 可以同时访问方法。这样达不到同步的效果
     */
    public synchronized void test1() {
        System.out.println("----synchronized method----");
    }

    public void test2() {
        synchronized (this) {
            System.out.println(this.getClass().getName() + "----synchronized this----");
        }
    }

    /**
     * 如果要保持即使不同对象也只能有一个线程来访问可以创建一个特殊的instance变量（它得是一个对象）来充当锁
     * （用byte数组对象比Object Object lock = new Object() 高效  注意这个得是static的，让不同对象竞争同一个 byte数组对象的锁）
     * 像method3 这样，谁拿到这个锁谁就可以运行它所控制的那段代码
     */
    public void test3() {
        synchronized (lock) {
            System.out.printf("----synchronized lock----");
        }
    }


}
