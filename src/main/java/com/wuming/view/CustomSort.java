package com.wuming.view;

import java.util.Arrays;
import java.util.Comparator;

/**
 * @author che
 * Created on 2025/6/21 10:52
 */
public class CustomSort {

    public static void main(String[] args) {
        int[] data = {0, 1, -1, 0, 1, 0, -1, -1, 0};
        // 1.计数排序
//        calSort(data);
        // 2. 自定义比较器
        Integer[] sortedData = customComparator(data);
        System.out.println(Arrays.toString(sortedData));

        // 3. 自定义排序
//        customSort(data);
        // 4. java8 stream api 排序
//        int[] sortData = streamSort(data);
//        System.out.println(Arrays.toString(sortedData));

        // 输出结果
        System.out.println(Arrays.toString(data));
    }


    /**
     * 统计元素出现次数，有序填充到新数组
     * <p>
     * 效率最高，时间复杂度为O(n)，特别适合元素种类少的情况
     *
     * @param data
     */
    public static void calSort(int[] data) {
        int count0 = 0, countNeg1 = 0, count1 = 0;

        // 统计各元素出现次数
        for (int num : data) {
            if (num == 0) {
                count0++;
            } else if (num == -1) {
                countNeg1++;
            } else {
                count1++;
            }
        }

        // 按顺序填充数组
        int index = 0;
        for (int i = 0; i < count0; i++) {
            data[index++] = 0;
        }
        for (int i = 0; i < countNeg1; i++) {
            data[index++] = -1;
        }
        for (int i = 0; i < count1; i++) {
            data[index++] = 1;
        }
    }

    /**
     * 自定义比较器
     *
     * @param data
     */
    private static Integer[] customComparator(int[] data) {
        // 转换为Integer数组以便使用自定义比较器
        Integer[] sortedData = Arrays.stream(data).boxed().toArray(Integer[]::new);

        // 自定义排序规则
        Arrays.sort(sortedData, new Comparator<Integer>() {
            @Override
            public int compare(Integer a, Integer b) {
                // 定义排序优先级：0 > -1 > 1
                int priorityA = (a == 0) ? 0 : (a == -1) ? 1 : 2;
                int priorityB = (b == 0) ? 0 : (b == -1) ? 1 : 2;
                return Integer.compare(priorityA, priorityB);
            }
        });
        return sortedData;
    }

    /**
     * 自定义排序
     * <p>
     * 展示了排序的基本原理，但效率不如内置方法
     *
     * @param data
     */
    public static void customSort(int[] data) {
        for (int i = 0; i < data.length - 1; i++) {
            for (int j = i + 1; j < data.length; j++) {
                if (getPriority(data[i]) > getPriority(data[j])) {
                    // 交换元素
                    int temp = data[i];
                    data[i] = data[j];
                    data[j] = temp;
                }
            }
        }
    }

    /**
     * 自定义排序规则
     *
     * @param num
     * @return
     */
    private static int getPriority(int num) {
        if (num == 0) return 0;
        if (num == -1) return 1;
        return 2;
    }

    /**
     * stream api自定义排序
     * <p>
     * 利用了Java 8的函数式编程特性，代码简洁但性能略低
     *
     * @param data
     * @return
     */
    public static int[] streamSort(int[] data) {
        // 使用Stream和自定义排序规则
        int[] sortedData = Arrays.stream(data)
                .boxed()
                .sorted((a, b) -> {
                    int priorityA = (a == 0) ? 0 : (a == -1) ? 1 : 2;
                    int priorityB = (b == 0) ? 0 : (b == -1) ? 1 : 2;
                    return priorityA - priorityB;
                })
                .mapToInt(Integer::intValue)
                .toArray();

        return sortedData;
    }


}
