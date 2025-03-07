package com.wuming.thread;


import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * CompletableFuture 是 Java 8 引入的异步编程工具，支持函数式编程模型，可显著简化复杂异步逻辑的实现。
 * 涵盖异步任务编排、链式调用、异常处理和多任务组合等核心场景
 */
public class CompletableFeatureTest {
    // 创建线程池

    final static ListeningExecutorService service = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());
    final static ExecutorService executorService = Executors.newFixedThreadPool(5);

    public static void main(String[] args) throws Exception {
        Long t1 = System.currentTimeMillis();
        // 1. 使用CompletableFuture 获取异步处理结果
        CompletableFuture<Boolean> booleanTask = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1000);
                return true;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return false;
        });
        System.out.println("BooleanTask: " + booleanTask.get());

        // 2. 链式调用
        CompletableFuture<Integer> lengthFuture = CompletableFuture.supplyAsync(() -> "Hello")
                .thenApply(s -> s.length()); // 将字符串转换为长度
        System.out.println(lengthFuture.get()); // 输出: 5

        // 3. 消费结果
        CompletableFuture.supplyAsync(() -> "Hello")
                .thenAccept(s -> System.out.println("消费结果: " + s)); // 输出: 消费结果: Hello

        // 4. 组合异步操作结果
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> "Hello")
                .thenCompose(s -> CompletableFuture.supplyAsync(() -> s + " World!"));

        System.out.println(future.get()); // 输出: Hello World!

        // 5. 并行执行并合并结果（thenCombine）
        CompletableFuture<Integer> future1 = CompletableFuture.supplyAsync(() -> 10);
        CompletableFuture<Integer> future2 = CompletableFuture.supplyAsync(() -> 20);
        CompletableFuture<Integer> combinedFuture = future1.thenCombine(future2, (a, b) -> a + b);
        System.out.println(combinedFuture.get()); // 输出: 30

        // 6. 等待所有任务完成
        CompletableFuture<String> task1 = CompletableFuture.supplyAsync(() -> "Task1");
        CompletableFuture<String> task2 = CompletableFuture.supplyAsync(() -> "Task2");
        CompletableFuture<String> task3 = CompletableFuture.supplyAsync(() -> "Task3");

        CompletableFuture<Void> allTasks = CompletableFuture.allOf(task1, task2, task3);
        allTasks.thenRun(() -> {
            System.out.println("所有任务完成");
            System.out.println("结果: " + task1.join() + ", " + task2.join() + ", " + task3.join());
        });

        //7. 任意一个任务完成即处理（anyOf）
        CompletableFuture<String> fastTask = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
            }
            return "Fast Task";
        });
        CompletableFuture<String> slowTask = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            return "Slow Task";
        });
        CompletableFuture<Object> anyTask = CompletableFuture.anyOf(fastTask, slowTask);
        anyTask.thenAccept(result -> System.out.println("最先完成的任务: " + result)); // 输出: Fast Task

        // 8. 捕获并处理异常（exceptionally）
        CompletableFuture<Integer> exceptionFeature1 = CompletableFuture.supplyAsync(() -> {
            if (true) throw new RuntimeException("模拟异常");
            return 100;
        }).exceptionally(ex -> {
            System.err.println("捕获异常: " + ex.getMessage());
            return 0; // 返回默认值
        });

        // 9. 统一处理结果或异常（handle）
        CompletableFuture<Integer> exceptionFeature2 = CompletableFuture.supplyAsync(() -> 100 / 0)
                .handle((result, ex) -> {
                    if (ex != null) {
                        System.err.println("异常: " + ex.getMessage());
                        return -1;
                    }
                    return result;
                });
        System.out.println(exceptionFeature2.get()); // 输出: -1

        //10. 链式异常传递（whenComplete）
        CompletableFuture.supplyAsync(() -> {
            throw new RuntimeException("Error Occurred");
        }).whenComplete((result, ex) -> {
            if (ex != null) {
                System.out.println("处理异常: " + ex.getMessage()); // 输出: 处理异常: Error Occurred
            }
        });


        System.out.println(future.get()); // 输出: 0


        // 不提交任务时，关闭线程池
        service.shutdown();
        // 执行时间
        System.err.println("time: " + (System.currentTimeMillis() - t1));
    }

}
