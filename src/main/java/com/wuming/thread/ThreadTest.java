package com.wuming.thread;

import org.junit.Test;

import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Created by wuming on 2017/6/29.
 * 在使用多线程的时候有时候我们会使用 java.util.concurrent.Executors的线程池，
 * 当多个线程异步执行的时候，我们往往不好判断是否线程池中所有的子线程都已经执行完毕，
 * 但有时候这种判断却很有用，例如我有个方法的功能是往一个文件异步地写入内容，
 * 我需要在所有的子线程写入完毕后在文件末尾写“---END---”及关闭文件流等，
 * 这个时候我就需要某个标志位可以告诉我是否线程池中所有的子线程都已经执行完毕，我使用这种方式来判断。
 */
public class ThreadTest {


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
