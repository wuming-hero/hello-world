package com.wuming.generic;


/**
 * 在 java 中泛型只是一个占位符，必须在传递类型后才能使用。
 * <p>
 * 类在实例化时才能真正的传递类型参数，由于静态方法的加载先于类的实例化，也就是说类中的泛型还没有传递真正的类型参数，静态的方法的加载就已经完成了，所以静态泛型方法是没有办法使用类上声明的泛型的，只能使用自己声明的 <E>
 *
 * @author manji
 * Created on 2025/3/5 10:39
 */
public class GenericDemo {


    /**
     * 静态泛型方法
     *
     * @param array
     * @param <T>
     * @return
     */
    public static <T> T getFirst(T[] array) {
        if (array == null || array.length == 0) {
            return null;
        }
        return array[0];
    }

}
