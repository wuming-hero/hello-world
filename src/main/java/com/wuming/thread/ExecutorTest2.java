package com.wuming.thread;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 自定义线程池
 *
 * @author manji
 * Created on 2021/12/7 15:18
 */
public class ExecutorTest2 {

    /**
     * 线程池-服务详情
     * <p>
     * 自定义线程池名称
     */
    private static final ThreadFactory customerThreadFactory = new ThreadFactoryBuilder()
            .setNameFormat("customerThreadPool-%d").build();
    /**
     * 线程池设置策略：
     * <p>
     * N=CPU的核数，U=目标CPU使用率（0<=U<=1），W/C = 线程等待时间/线程CPU使用时间。一个合理的线程池数量=N * U * ( 1 + W/C)
     * <p>
     * N=4 U=100% W/C=9(估值)   理论最大max size=36；
     */
    private static final ExecutorService customerExecutorPool = new ThreadPoolExecutor(10, 36,
            1000L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(256), customerThreadFactory, new ThreadPoolExecutor.AbortPolicy());

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            customerExecutorPool.execute(new Runnable() {
                @Override
                public void run() {
                    System.out.println("-----" + Thread.currentThread().getName());
                }
            });
        }
    }

}
