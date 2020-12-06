package com.wuming.concurrent;

import org.apache.commons.lang3.RandomUtils;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Semaphore，是JDK1.5的java.util.concurrent并发包中提供的一个并发工具类，所谓Semaphore即 信号量 的意思。
 * 这个叫法并不能很好地表示它的作用，更形象的说法应该是许可证管理器。
 * <p>
 * 从概念上将，Semaphore包含一组许可证。
 * 如果有需要的话，每个acquire()方法都会阻塞，直到获取一个可用的许可证。
 * 每个release()方法都会释放持有许可证的线程，并且归还Semaphore一个可用的许可证。
 * 然而，实际上并没有真实的许可证对象供线程使用，Semaphore只是对可用的数量进行管理维护。
 * <p>
 * <p>
 * 详解文档 https://blog.csdn.net/hanchao5272/article/details/79780045
 *
 * @author wuming
 * Created on 2020-11-28 10:40
 */
public class SemaphoreDemo {

    static Semaphore semaphore = new Semaphore(2, true);

    public static void main(String[] args) throws InterruptedException {
        //101班的学生
        Thread[] students101 = new Thread[5];
        for (int i = 0; i < 20; i++) {
            //前10个同学都在耐心的等待打饭
            if (i < 10) {
                new Thread(new Student("打饭学生" + i, SemaphoreDemo.semaphore, 0)).start();
            } else if (i >= 10 && i < 15) {//这5个学生没有耐心打饭，只会等1000毫秒
                new Thread(new Student("泡面学生" + i, SemaphoreDemo.semaphore, 1)).start();
            } else {//这5个学生没有耐心打饭
                students101[i - 15] = new Thread(new Student("聚餐学生" + i, SemaphoreDemo.semaphore, 2));
                students101[i - 15].start();
            }
        }
        //
        Thread.sleep(5000);
        for (int i = 0; i < 5; i++) {
            students101[i].interrupt();
        }
    }

    /**
     * 通过内部类定义学生类
     */
    static class Student implements Runnable {

        private static final Logger LOGGER = Logger.getLogger("student");

        //学生姓名
        private String name;
        //打饭许可
        private Semaphore semaphore;
        /**
         * 打饭方式
         * 0    一直等待直到打到饭
         * 1    等了一会不耐烦了，回宿舍吃泡面了
         * 2    打饭中途被其他同学叫走了，不再等待
         */
        private int type;

        public Student(String name, Semaphore semaphore, int type) {
            this.name = name;
            this.semaphore = semaphore;
            this.type = type;
        }

        /**
         * <p>打饭</p>
         *
         * @author hanchao 2018/3/31 19:49
         **/
        @Override
        public void run() {
            //根据打饭情形分别进行不同的处理
            switch (type) {
                //打饭时间
                //这个学生很有耐心，它会一直排队直到打到饭
                case 0:
                    //排队
                    semaphore.acquireUninterruptibly();
                    //进行打饭
                    try {
                        Thread.sleep(RandomUtils.nextLong(1000, 3000));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //将打饭机会让后后面的同学
                    semaphore.release();
                    //打到了饭
                    LOGGER.info(name + " 终于打到了饭.");
                    break;

                //这个学生没有耐心，等了1000毫秒没打到饭，就回宿舍泡面了
                case 1:
                    //排队
                    try {
                        //如果等待超时，则不再等待，回宿舍吃泡面
                        if (semaphore.tryAcquire(RandomUtils.nextInt(6000, 16000), TimeUnit.MILLISECONDS)) {
                            //进行打饭
                            try {
                                Thread.sleep(RandomUtils.nextLong(1000, 3000));
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            //将打饭机会让后后面的同学
                            semaphore.release();
                            //打到了饭
                            LOGGER.info(name + " 终于打到了饭.");
                        } else {
                            //回宿舍吃泡面
                            LOGGER.info(name + " 回宿舍吃泡面.");
                        }
                    } catch (InterruptedException e) {
                        //e.printStackTrace();
                    }
                    break;

                //这个学生也很有耐心，但是他们班突然宣布聚餐，它只能放弃打饭了
                case 2:
                    //排队
                    try {
                        semaphore.acquire();
                        //进行打饭
                        try {
                            Thread.sleep(RandomUtils.nextLong(1000, 3000));
                        } catch (InterruptedException e) {
                            //e.printStackTrace();
                        }
                        //将打饭机会让后后面的同学
                        semaphore.release();
                        //打到了饭
                        LOGGER.info(name + " 终于打到了饭.");
                    } catch (InterruptedException e) {
                        //e.printStackTrace();
                        //被叫去聚餐，不再打饭
                        LOGGER.info(name + " 全部聚餐，不再打饭.");
                    }
                    break;
                default:
                    break;
            }
        }

    }

}
