package com.wuming.thread;

/**
 * 创建两个线程：一个用于打印奇数，一个用于打印偶数。每个线程在循环中检查当前数字是否符合自己的打印条件（奇数或偶数）。
 * <p>
 * 1. 使用 synchronized 块来保证对共享变量 number 的安全访问。
 * 2. 当一个线程打印了数字后，调用 number++ 来递增数字，并使用 lock.notify() 通知另一个线程继续执行。
 * 3. 当一个线程需要等待时，调用 lock.wait()，会释放锁并等待通知。
 * 3. 通过 start() 启动两个线程，并通过 join() 确保主线程等待它们完成。
 * <p>
 * 效率：
 * 使用 wait() 和 notify() 方法确保线程只在可以继续执行时才进行计算，从而较高效地实现了奇数和偶数的交替打印。
 *
 * @author manji
 * Created on 2025/3/1 15:51
 */
public class OddEvenThreadTest {

    private static int number = 1; // 当前要打印的数字
    private static final int MAX_NUMBER = 20; // 设置最大数字
    private static final Object lock = new Object(); // 锁对象

    public static void main(String[] args) {

        // 奇数线程
        Thread oddThread = new Thread(() -> {
            while (number <= MAX_NUMBER) {
                synchronized (lock) {
                    // 打印奇数
                    if (number % 2 != 0) {
                        System.out.println(number++);
                        lock.notify(); // 通知偶数线程
                    } else {
                        try {
                            lock.wait(); // 等待偶数线程执行
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            }
        });

        // 偶数线程
        Thread evenThread = new Thread(() -> {
            while (number <= MAX_NUMBER) {
                synchronized (lock) {
                    // 打印偶数
                    if (number % 2 == 0) {
                        System.out.println(number++);
                        lock.notify(); // 通知奇数线程
                    } else {
                        try {
                            lock.wait(); // 等待奇数线程执行
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            }
        });

        // 开启线程执行
        oddThread.start();
        evenThread.start();

        // 等待线程执行完成
        try {
            oddThread.join();
            evenThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        // 2个线程执行完， 才会打印以下信息
        System.out.println("Both threads have finished.");
    }

}
