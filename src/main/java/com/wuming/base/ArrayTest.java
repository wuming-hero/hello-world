package com.wuming.base;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.util.Arrays;

/**
 * 打印一个Java数组的一些方法
 *
 * @author wuming
 * Created on 2017/9/23 17:22
 */
public class ArrayTest {

    /**
     * 这是打印数组的最简单方法
     * Arrays.toString ()
     *
     * @since JDK 1.5
     */
    @Test
    public void test() {
        // 1.一维数组
        String[] names = {"Georgianna", "Tenn", "Simon", "Tom"};
        System.out.println(Arrays.asList(names));

        // array
        String[] arrayStr = new String[]{"Java", "Node", "Python", "Ruby"};
        System.out.println(Arrays.toString(arrayStr)); // Output : [Java, Node, Python, Ruby]

        int[] arrayInt = {1, 3, 5, 7, 9};
        System.out.println(Arrays.toString(arrayInt));  // Output : [1, 3, 5, 7, 9]

        // 2d array, need Arrays.deepToString
        String[][] deepArrayStr = new String[][]{{"yiibai1", "yiibai2"}, {"yiibai3", "yiibai4"}};
        System.out.println(Arrays.toString(deepArrayStr));  // Output : [[Ljava.lang.String;@23fc625e, [Ljava.lang.String;@3f99bd52]
        System.out.println(Arrays.deepToString(deepArrayStr));  // Output : [[yiibai1, yiibai2], [yiibai3, yiibai4]

        int[][] deepArrayInt = new int[][]{{1, 3, 5, 7, 9}, {2, 4, 6, 8, 10}};
        System.out.println(Arrays.toString(deepArrayInt)); // Output : [[I@3a71f4dd, [I@7adf9f5f]
        System.out.println(Arrays.deepToString(deepArrayInt)); // Output : [[1, 3, 5, 7, 9], [2, 4, 6, 8, 10]]

    }

    /**
     * 可以将其转换成流并打印出来
     *
     * @since JDK 1.8
     */
    @Test
    public void test1() {

        // array
        String[] arrayStr = new String[]{"Java", "Node", "Python", "Ruby"};
        Arrays.stream(arrayStr).forEach(System.out::println);

        int[] arrayInt = {1, 3, 5, 7, 9};
        Arrays.stream(arrayInt).forEach(System.out::println);

        //2d array
        String[][] deepArrayStr = new String[][]{{"yiibai1", "yiibai2"}, {"yiibai3", "yiibai4"}};
        Arrays.stream(deepArrayStr).flatMap(x -> Arrays.stream(x)).forEach(System.out::println);

        int[][] deepArrayInt = new int[][]{{1, 3, 5, 7, 9}, {2, 4, 6, 8, 10}};
        Arrays.stream(deepArrayInt).flatMapToInt(x -> Arrays.stream(x)).forEach(System.out::println);
    }

    /**
     * 借助Jackson实现
     *
     * @throws JsonProcessingException
     */
    @Test
    public void test2() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String[] arrayStr = new String[]{"Java", "Node", "Python", "Ruby"};
        String arrStr = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(arrayStr);
        System.out.println(arrStr);

        int[] arrayInt = {1, 3, 5, 7, 9};
        arrStr = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(arrayInt);
        System.out.println(arrStr);

        //2d array
        String[][] deepArrayStr = new String[][]{{"yiibai1", "yiibai2"}, {"yiibai3", "yiibai4"}};
        arrStr = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(deepArrayStr);
        System.out.println(arrStr);

        int[][] deepArrayInt = new int[][]{{1, 3, 5, 7, 9}, {2, 4, 6, 8, 10}};
        arrStr = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(deepArrayInt);
        System.out.println(arrStr);
    }

}
