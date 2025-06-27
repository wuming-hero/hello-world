package com.wuming.view;

import java.util.Random;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * 1. 编写一个多线程生产者与消费者模式示例
 * <p>
 * 2. 设计一个方式，使得开发者在应用层接入时必须实现相关功能，包含请求体校验，业务异常处理，正常业务处理，日志摘要打印，相关功能可以通过打印日志方式实现，不需要真实实现
 *
 * @author che
 * Created on 2025/6/6 16:12
 */
public class CustomMessageQueue {


    public static void main(String[] args) {

        int maxLength = 100;

        LinkedBlockingDeque<Integer> queue = new LinkedBlockingDeque<>(100);


        /**
         * 生产者线程
         */
        Thread provider = new Thread(new Runnable() {

            public void run() {
                while (true) {
                    synchronized (queue) {
                        if (queue.size() >= maxLength) {
                            try {
                                queue.wait();
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        } else {
                            queue.add(new Random().nextInt(maxLength));
                            queue.notifyAll();
                        }

                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        });

        /**
         * 消费者线程
         */
        Thread consumer = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    synchronized (queue) {
                        if (queue.isEmpty()) {
                            try {
                                queue.wait();
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        } else {
                            Integer data = queue.poll();
                            System.out.println(data);
                            queue.notifyAll();
                        }

                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }

            }
        });

        provider.start();
        consumer.start();
    }


}
