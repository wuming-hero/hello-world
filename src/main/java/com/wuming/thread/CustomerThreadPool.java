package com.wuming.thread;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.wuming.model.Student;
import com.wuming.util.RandomUtil;
import com.wuming.util.ThreadUtil;

import java.util.List;
import java.util.concurrent.*;

/**
 * 自定义线程池
 * 1. 避免过度配置：过高的核心线程数会导致频繁上下文切换，降低吞吐量。
 * 2. 队列选择：
 * 无界队列（如 LinkedBlockingQueue）：可能导致内存溢出。
 * 同步队列（如 SynchronousQueue）：适合任务处理速度快的场景。
 * 3. 拒绝策略：根据业务需求选择
 * AbortPolicy（默认）
 * CallerRunsPolicy 如果线程池满，则将新提交的任务交给调用者所在的线程去执行
 *
 * 问题：
 * 1.如果线程池中执行任务的线程异常，发生异常的线程会销毁吗？其他任务还能正常执行吗？
 * 1.1 线程池中执行任务的线程异常，并不会影响其他任务的执行，而且execute()提交任务，直接打印了异常信息
 * 1.2 如果运行中的线程池有线程执行异常，会调用workers.remove()移除当前线程，并调用addWorker()重新创建新的线程
 * 所以在业务代码中，请捕获子任务中的异常，否则会导致线程池中的工作线程频繁销毁、创建，造成资源浪费，违背了线程复用的设计原则
 *
 * @author manji
 * Created on 2021/12/7 15:18
 */
public class CustomerThreadPool {

    /**
     * 线程池-服务详情
     * <p>
     * 自定义线程池名称
     */
    private static final ThreadFactory customerThreadFactory = new ThreadFactoryBuilder().setNameFormat("customerThreadPool-%d").build();
    /**
     * 线程池设置策略：
     * <p>
     * N=CPU的核数，
     * U=目标CPU使用率（0<=U<=1），一般为50%及以下
     * W/C(IO等待比) = 线程等待时间/线程CPU使用时间。（IO等待比 或 阻塞系数:是设计线程池大小（尤其是IO密集型任务）的关键参数）
     * 一个合理的线程池数量=N * U * (1 + W/C)
     * <p>
     * 1. CPU密集型(批量数据处理)
     * N=4 U=100%(CPU满载) W/C=1(估值)
     * 核心线程数 = 4 * 50% * (1 + 1) = 4(一般设置为5即 corePoolSize + 1, +1 是为防止线程因页缺失等短暂停顿导致 CPU 闲置)
     * 理论最大max size=8 = 4 * 100% * (1 + 1) = 8
     * <p>
     * 2.IO密集型(等待数据库查询、外部API响应)
     * N=4 U=100%(CPU满载) W/C=9(估值)
     * 核心线程数 = 4 * 50% * (1 + 9) = 20
     * 理论最大max size=40 = 4 * 100% * (1 + 9) = 40
     */
    private static final ExecutorService customerExecutorPool = new ThreadPoolExecutor(5, 8, 1000L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(256), customerThreadFactory, new ThreadPoolExecutor.AbortPolicy());

    public static void main(String[] args) throws InterruptedException {
        // 假设5个任务
        int threadNum = 5;
        CountDownLatch countDownLatch = new CountDownLatch(threadNum);
        CyclicBarrier cyclicBarrier = new CyclicBarrier(threadNum);
        // 1. 初始化数据
        List<Student> studentList = Lists.newArrayList();
        for (int i = 0; i < threadNum; i++) {
            Student student = new Student();
            student.setAge(RandomUtil.randomNumber(1, 99));
            customerExecutorPool.execute(() -> {
                try {
                    //当前线程阻塞，直到所有线程都准备就绪
                    cyclicBarrier.await();
                    System.out.println( "当前线程: "+ Thread.currentThread().getName() + " 开始工作啦");
                    //模拟业务代码
                    getStudentAge(student);

                    System.out.println( "当前线程: "+ Thread.currentThread().getName() + " 结束工作啦");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                } finally {
                    countDownLatch.countDown();
                }
            });
        }

        // 提交完任务，关闭线程池
        customerExecutorPool.shutdown();

        //主线程等待工作线程全部结束
        countDownLatch.await();
        System.out.println("全部线程工作完成");
    }

    /**
     * mock 执行任务
     *
     * @param student
     * @return
     */
    private static Integer getStudentAge(Student student) {
        return student.getAge();
    }


}
