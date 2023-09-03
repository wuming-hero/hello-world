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

//    public static void main(String[] args) {
//        for (int i = 0; i < 10; i++) {
//            customerExecutorPool.execute(new Runnable() {
//                @Override
//                public void run() {
//                    System.out.println("-----" + Thread.currentThread().getName());
//                }
//            });
//        }
//    }



    public static void main(String[] args) {
        List<Callable<Integer>> callableList = Lists.newArrayList();
        List<Student> studentList = Lists.newArrayList();
        for (int i = 0; i < 5; i++) {
            Student student = new Student();
            student.setAge(RandomUtil.randomNumber(1, 99));
            studentList.add(student);
        }
        // 2.1 构建下单参数
        for (Student student : studentList) {
            callableList.add(() -> {
                try {
                    return getStudentAge(student);
                } catch (Throwable t){
                    System.out.println("error");
                    return null;
                }
            });
        }
        List<Integer> integerList = ThreadUtil.run(callableList, customerExecutorPool, 3 * 1000L);
        System.out.println(integerList);

    }

    private static Integer getStudentAge(Student student) {
        return student.getAge();
    }


}
