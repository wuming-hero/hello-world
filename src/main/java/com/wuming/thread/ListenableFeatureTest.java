package com.wuming.thread;


import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

/**
 * ListenableFuture 是 Google Guava 库提供的一个增强型 Future 接口，用于更灵活地处理异步任务的结果。
 * 通过支持回调机制，它允许开发者在任务完成后自动触发后续处理逻辑，避免了传统 Future 需要轮询或阻塞的问题。
 *
 */
public class ListenableFeatureTest {
    // 创建线程池
    final static ListeningExecutorService service = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());

    public static void main(String[] args) throws Exception {
        Long t1 = System.currentTimeMillis();
        // 任务1
        ListenableFuture<Boolean> booleanTask = service.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return true;
            }
        });

        Futures.addCallback(booleanTask, new FutureCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                System.err.println("BooleanTask: " + result);
            }

            @Override
            public void onFailure(Throwable t) {
            }
        });

        // 任务2
        ListenableFuture<String> stringTask = service.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return "Hello World";
            }
        });

        Futures.addCallback(stringTask, new FutureCallback<String>() {
            @Override
            public void onSuccess(String result) {
                System.err.println("StringTask: " + result);
            }

            @Override
            public void onFailure(Throwable t) {
            }
        });

        // 任务3
        ListenableFuture<Integer> integerTask = service.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return new Random().nextInt(100);
            }
        });

        Futures.addCallback(integerTask, new FutureCallback<Integer>() {
            @Override
            public void onSuccess(Integer result) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.err.println("IntegerTask: " + result);
            }

            @Override
            public void onFailure(Throwable t) {
            }
        });

        // 不提交任务时，关闭线程池
        service.shutdown();
        // 执行时间
        System.err.println("time: " + (System.currentTimeMillis() - t1));
    }

}
