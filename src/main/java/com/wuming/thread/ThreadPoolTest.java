package com.wuming.thread;

import java.util.concurrent.*;

/**
 * @author wuming
 * Created on 2020-12-04 18:40
 */
public class ThreadPoolTest {

    //线程池的核心线程数量
    private static final int CORE_POOL_SIZE = 5;
    //线程池的最大线程数
    private static final int MAX_POOL_SIZE = 10;
    //阻塞队列的容量
    private static final int QUEUE_CAPACITY = 100;
    //当线程数大于核心线程数时，多余的空闲线程存活的最长时间
    private static final Long KEEP_ALIVE_TIME = 1L;

    public static int threadNum = 5;

    public static void main(String[] args) throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(threadNum);
        CyclicBarrier cyclicBarrier = new CyclicBarrier(threadNum);

        //使用阿里巴巴推荐的创建线程池的方式
        //通过ThreadPoolExecutor构造函数自定义参数创建
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                CORE_POOL_SIZE,
                MAX_POOL_SIZE,
                KEEP_ALIVE_TIME,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(QUEUE_CAPACITY),

                //线程池达到饱和之后的拒绝策略，
                // 调用执行自己的线程运行任务，也就是直接在调用execute方法的线程中运行被拒绝的任务
                new ThreadPoolExecutor.CallerRunsPolicy());


        for (int i = 0; i < threadNum; i++) {
            //简洁的Lambda表达式
            executor.execute(() -> {
                try {
                    //当前线程阻塞，直到所有线程都准备就绪
                    cyclicBarrier.await();
                    System.out.println( "当前线程: "+ Thread.currentThread().getName() + " 开始工作啦");
                    //模拟业务代码
                    Thread.sleep(1000);
                    System.out.println( "当前线程: "+ Thread.currentThread().getName() + " 结束工作啦");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }

                countDownLatch.countDown();

            });
        }
        //主线程等待工作线程全部结束
        countDownLatch.await();
        //关闭线程池
        executor.shutdown();

        System.out.println("全部线程工作完成");
    }

}
