package com.wuming.generic;


/**
 * @author manji
 * Created on 2025/3/5 10:39
 */
public class GenericDemo {

    public static <T> T getFirst(T[] array) {
        if (array == null || array.length == 0) {
            return null;
        }
        return array[0];
    }

}
