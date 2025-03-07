package com.wuming.thread;


import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * CompletableFuture 通过函数式组合能力，可优雅解决复杂异步流程编排问题，是Java现代异步编程的核心工具
 * <p>
 * 1. 避免阻塞：尽量使用回调（thenAccept/thenApply）替代 get() 方法。
 * 2. 线程池管理：为不同业务场景分配独立线程池，避免资源竞争。
 * 3. 异常兜底：每个链式操作后添加异常处理（exceptionally 或 handle）。
 * 4. 超时控制：对关键任务设置超时，防止无限等待。
 * 5. 资源释放：任务完成后及时关闭线程池。
 */
public class CompletableFeatureTest2 {
    // 创建线程池

    public static void main(String[] args) throws Exception {
        final ExecutorService orderExecutor = Executors.newFixedThreadPool(5);
        final ExecutorService stockExecutor = Executors.newFixedThreadPool(5);
        // 异步获取用户信息
        CompletableFuture<String> userFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println("查询用户信息...");
            return "User123";
        }, orderExecutor);

        // 异步获取订单信息（依赖用户信息）
        CompletableFuture<String> orderFuture = userFuture.thenCompose(user ->
                CompletableFuture.supplyAsync(() -> {
                    System.out.println("用户 " + user + " 的订单查询...");
                    return "Order456";
                }, orderExecutor)
        );

        // 并行查询库存
        CompletableFuture<Integer> stockFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println("查询库存...");
            return 100;
        }, stockExecutor);

        // 合并结果
        CompletableFuture<String> combinedFuture = orderFuture.thenCombine(stockFuture,
                (order, stock) -> "订单: " + order + ", 库存: " + stock);

        // 最终处理
        combinedFuture.thenAccept(result ->
                System.out.println("最终结果: " + result)
        ).exceptionally(ex -> {
            System.err.println("流程失败: " + ex.getMessage());
            return null;
        });
        // 等待异步任务完成
        combinedFuture.get();

        orderExecutor.shutdown();
        stockExecutor.shutdown();
    }

}
