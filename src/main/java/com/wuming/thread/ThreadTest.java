package com.wuming.thread;

import java.util.Random;

/**
 * Created by wuming on 2017/6/29.
 * <p>
 * 在有些应用中需要在线程运行的过程中动态地获取数据，
 * 如在下面代码的run方法中产生了3个随机数，然后通过Work类的process方法求这三个随机数的和，并通过Data类的value将结果返回。
 * 从这个例子可以看出，在返回value之前，必须要得到三个随机数。也就是说，这个 value是无法事先就传入线程类的。
 * <p>
 * 在上面代码中的process方法被称为回调函数。从本质上说，回调函数就是事件函数。
 * 在Windows API中常使用回调函数和调用API的程序之间进行数据交互。因此，调用回调函数的过程就是最原始的引发事件的过程。
 * 在这个例子中调用了process方法来获得数据也就相当于在run方法中引发了一个事件。
 */
public class ThreadTest extends Thread {

    private Work work;

    public ThreadTest(Work work) {
        this.work = work;
    }

    public static void main(String[] args) {
        Thread thread = new ThreadTest(new Work());
        thread.start();
    }

    public void run() {
        Random random = new Random();
        Data data = new Data();
        int n1 = random.nextInt(1000);
        int n2 = random.nextInt(2000);
        int n3 = random.nextInt(3000);
        work.process(data, n1, n2, n3); // 使用回调函数
        System.out.println(String.valueOf(n1) + "+" + String.valueOf(n2) + "+" + String.valueOf(n3) + "=" + data.value);
    }

}

class Data {
    public int value = 0;
}

class Work {
    public void process(Data data, Integer... numbers) {
        for (int n : numbers) {
            data.value += n;
        }
    }
}
