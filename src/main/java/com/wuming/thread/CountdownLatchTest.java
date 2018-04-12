package com.wuming.thread;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * CountDownLatch是一个同步辅助类，犹如倒计时计数器，创建对象时通过构造方法设置初始值，
 * 调用CountDownLatch对象的await()方法则处于等待状态，调用countDown()方法就将计数器减1，当计数到达0时，则所有等待者或单个等待者开始执行。
 *
 * @author wuming
 * Created on 2018/3/21 10:18
 */
public class CountdownLatchTest {

    /**
     * 该程序用来模拟发送命令与执行命令，主线程代表指挥官，新建3个线程代表战士，战士一直等待着指挥官下达命令。
     * 若指挥官没有下达命令，则战士们都必须等待。一旦命令下达，战士们都去执行自己的任务，指挥官处于等待状态，
     * 战士们任务执行完毕则报告给指挥官，指挥官则结束等待。
     */
    public static void main(String[] args) {
        ExecutorService service = Executors.newCachedThreadPool(); //创建一个线程池
        final CountDownLatch cdOrder = new CountDownLatch(1);//指挥官的命令，设置为1，指挥官一下达命令，则cutDown,变为0，战士们执行任务
        final CountDownLatch cdAnswer = new CountDownLatch(3);//因为有三个战士，所以初始值为3，每一个战士执行任务完毕则cutDown一次，当三个都执行完毕，变为0，则指挥官停止等待。
        for (int i = 0; i < 3; i++) {
            Runnable runnable = new Runnable() {
                public void run() {
                    try {
                        System.out.println("线程" + Thread.currentThread().getName() + "正准备接受命令");
                        cdOrder.await(); //战士们都处于等待命令状态

                        System.out.println("线程" + Thread.currentThread().getName() + "已接受命令");
                        Thread.sleep((long) (Math.random() * 10000));

                        System.out.println("线程" + Thread.currentThread().getName() + "回应命令处理结果");
                        cdAnswer.countDown(); //任务执行完毕，返回给指挥官，cdAnswer减1。
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            service.execute(runnable);//为线程池添加任务
        }

        try {
            Thread.sleep((long) (Math.random() * 10000));

            System.out.println("线程" + Thread.currentThread().getName() + "即将发布命令");
            cdOrder.countDown(); //发送命令，cdOrder减1，处于等待的战士们停止等待转去执行任务。

            System.out.println("线程" + Thread.currentThread().getName() + "已发送命令，正在等待结果");
            cdAnswer.await(); //命令发送后指挥官处于等待状态，一旦cdAnswer为0时停止等待继续往下执行

            System.out.println("线程" + Thread.currentThread().getName() + "已收到所有响应结果");
        } catch (Exception e) {
            e.printStackTrace();
        }
        service.shutdown(); // 任务结束，停止线程池的所有线程
    }
}
