package com.wuming.thread;

import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

/**
 * new Thread的弊端
 * a. 每次new Thread新建对象性能差。
 * b. 线程缺乏统一管理，可能无限制新建线程，相互之间竞争，及可能占用过多系统资源导致死机或oom。
 * c. 缺乏更多功能，如定时执行、定期执行、线程中断。
 * 线程池的好处
 * a. 重用存在的线程，减少对象创建、消亡的开销，性能佳。
 * b. 可有效控制最大并发线程数，提高系统资源的使用率，同时避免过多资源竞争，避免堵塞。
 * c. 提供定时执行、定期执行、单线程、并发数控制等功能。
 * <p>
 * Java通过Executors提供四种线程池，分别为：
 * newCachedThreadPool创建一个可缓存线程池，如果线程池长度超过处理需要，可灵活回收空闲线程，若无可回收，则新建线程。
 * newFixedThreadPool 创建一个定长线程池，可控制线程最大并发数，超出的线程会在队列中等待。
 * newScheduledThreadPool 创建一个定时线程池，支持定时及周期性任务执行。
 * newSingleThreadExecutor 创建一个单线程化的线程池，它只会用唯一的工作线程来执行任务，保证所有任务按照指定顺序(FIFO, LIFO, 优先级)执行。
 */
public class ExecutorTest {

    /**
     * 我有很多更新各种数据的task，我希望如果其中一个task失败，其它的task就不需要执行了。
     * 那我就需要catch Future.get抛出的异常，然后终止其它task的执行
     *
     * @param args
     */
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        List<Future<String>> resultList = new ArrayList<>();

        // 创建10个任务并执行
        for (int i = 0; i < 10; i++) {
            // 使用ExecutorService执行Callable类型的任务，并将结果保存在future变量中
            Future<String> future = executorService.submit(new TaskWithResult(i));
            // 将任务执行结果存储到List中
            resultList.add(future);
        }
        executorService.shutdown();

        // 遍历任务的结果
        for (Future<String> fs : resultList) {
            try {
                System.out.println(fs.get()); // 打印各个线程（任务）执行的结果
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                // 如果某个线程运行出错抛出异常，直接终止所有线程的执行，并且使用return语句直接返回，终止线程外的逻辑。
                executorService.shutdownNow();
                e.printStackTrace();
                return;
            }
        }
    }

    /**
     * 当调用ExecutorService.shutdown方法的时候，线程池不再接收任何新任务，
     * 但此时线程池并不会立刻退出，直到添加到线程池中的任务都已经处理完成，才会退出。
     * <p>
     * 在调用shutdown方法后我们可以在一个死循环里面用isTerminated方法判断是否线程池中的所有线程已经执行完毕，
     * 如果子线程都结束了，我们就可以做关闭流等后续操作了。
     *
     * @throws IOException
     * @throws InterruptedException
     */
    @Test
    public void test() throws IOException, InterruptedException {
        String filePath = this.getClass().getClassLoader().getResource("file/test.txt").getPath();
        System.out.println(filePath);
        final File file = new File(filePath);
        final OutputStream os = new FileOutputStream(file);
        final OutputStreamWriter writer = new OutputStreamWriter(os);
        final Semaphore semaphore = new Semaphore(10);
        ExecutorService exec = Executors.newCachedThreadPool();

        final long start = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            final int num = i;
            Runnable task = () -> {
                try {
                    semaphore.acquire();
                    writer.write(String.valueOf(num) + "\n");
                    semaphore.release();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            };
            exec.submit(task);
        }
        exec.shutdown();

        // 可以使用以下死循环每隔1s判断是否执行完，返回true时，则跳出死循环
        while (true) {
            if (exec.isTerminated()) {
                writer.write("---END---\n");
                writer.close();
                System.out.println("所有的子线程都结束了！");
                break;
            }
            Thread.sleep(1000);
        }

        // 以上死循环改进，可以直接使用以下这行代码代替,
        exec.awaitTermination(1, TimeUnit.HOURS);

        final long end = System.currentTimeMillis();
        System.out.println((end - start) / 1000);

    }

}

class TaskWithResult implements Callable<String> {
    private int id;

    public TaskWithResult(int id) {
        this.id = id;
    }

    public String call() throws Exception {
        System.out.println("call()方法被自动调用,干活！！！    " + Thread.currentThread().getName());
        if (new Random().nextBoolean())
            throw new Exception("Meet error in task." + Thread.currentThread().getName());
        // 一个模拟耗时的操作
        Thread.sleep(3 * 1000);
        // 任务运行结果
        return "call()方法被自动调用，任务的结果是：" + id + "    " + Thread.currentThread().getName();
    }
}
