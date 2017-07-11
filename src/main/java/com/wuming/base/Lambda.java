package com.wuming.base;

import org.junit.Test;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by wuming on 2017/6/8.
 * <p>
 * 1.函数式接口：函数式接口是Java 8为支持Lambda表达式新发明的，函数式接口具有两个主要特征，
 * 是一个接口，这个接口具有唯一的一个抽像方法，我们将满足这两个特性的接口称为函数式接口。
 * （在函数式接口中可以提供多个抽像方法，但这些抽像方法限制了范围，只能是Object类型里的已有的public方法）
 * 2.方法引用
 * TODO
 *
 * http://www.cnblogs.com/WJ5888/p/4618465.html
 * http://www.cnblogs.com/WJ5888/p/4667086.html
 * http://www.blogjava.net/wangxinsh55/archive/2014/12/25/421826.html
 */
public class Lambda {

    /**
     * 表达式简化多线程 Runnable() 接口
     *
     * @throws InterruptedException
     */
    @Test
    public void test2() throws InterruptedException {
        String name = "wuming";
        new Thread(() -> System.out.println(name)).start();
    }

    /**
     * 表达式在排序接口中使用
     */
    @Test
    public void comparatorTest() {
        String[] array = {"banana", "pear", "apple"};
        System.out.println("before sort: " + Arrays.asList(array));
        Arrays.sort(array); // 字典升序
        System.out.println("after sort: " + Arrays.asList(array));

        // lambda 表达式写法
        Arrays.sort(array, (v1, v2) -> v1.compareToIgnoreCase(v2));
        // 函数引用写法
        Arrays.sort(array, String::compareToIgnoreCase);
        System.out.println(Arrays.asList(array));

        // 自定义排序方式，根据字符串的长度由短到长排序
        // 1.7及以前版本写法
        Arrays.sort(array, new Comparator<String>() {
            @Override
            public int compare(String v1, String v2) {
                return Integer.compare(v1.length(), v2.length());
            }
        });

        // 针对于函数式接口，可以使用lambda表达式简化
        Arrays.sort(array, (v1, v2) -> Integer.compare(v1.length(), v2.length()));
        // 函数式引用写法
        Arrays.sort(array, Comparator.comparingInt(String::length));
        System.out.println("after sort2: " + Arrays.asList(array));
    }

    /**
     * 函数式引用
     * @param args
     */
    public static void main(String[] args) {
        //使用 Lambda 表达式，输出： 16: send email
        start((id, task) -> id + ": " + task);
        //或者
        Machine m1 = (id, task) -> id + ": " + task;
        m1.doSomething(16, "send email");

        //使用方法引用，输出： Hello 16: send email
        start(Lambda::hello);
        //或者
        Machine m2 = Lambda::hello;
        m2.doSomething(16, "send email");
    }

    private static void start(Machine machine){
        String result = machine.doSomething(16, "send email");
        System.out.println(result);
    }

    public static String hello(int id, String task){
        return "Hello " + id +": " + task;
    }

}

@FunctionalInterface
interface Machine {
    public String doSomething(int id, String task);
}