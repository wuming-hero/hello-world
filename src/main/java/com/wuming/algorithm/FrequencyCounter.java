package com.wuming.algorithm;

import java.util.Arrays;
import java.util.Comparator;

/**
 *
 * 一个整数队列长度 300 ，值范围在 [1-100]之间，统计出每个值出现的次数，按出现次数降序排列，
 * 如果出现次数为0，则不统计。
 *
 * 这个程序的实现效率高，且能在 O(n + k log k) 的时间复杂度内完成统计与排序，其中 n 是队列的长度（300），k 是可能的值数量（100）。
 *
 * @author manji
 * Created on 2025/3/1 18:04
 */
public class FrequencyCounter {

    public static void main(String[] args) {
        int[] queue = new int[300];

        // 随机填充整数队列，值范围在 [1-100] 之间
        for (int i = 0; i < queue.length; i++) {
            queue[i] = (int) (Math.random() * 100) + 1; // 生成 1 到 100 之间的随机数
        }

        // 统计每个值的出现次数
        int[] frequency = new int[101]; // 用于统计 1 到 100 的频率
        for (int num : queue) {
            frequency[num]++;
        }

        // 创建一个数组保存非零频率值及其出现次数
        int[][] countPairs = new int[100][2];
        int index = 0;
        for (int value = 1; value <= 100; value++) {
            if (frequency[value] > 0) {
                countPairs[index][0] = value;        // 数值
                countPairs[index][1] = frequency[value]; // 出现次数
                index++;
            }
        }

        // 只保留有效的出现次数
        countPairs = Arrays.copyOf(countPairs, index);

        // 按出现次数降序排序
        Arrays.sort(countPairs, new Comparator<int[]>() {
            public int compare(int[] a, int[] b) {
                return Integer.compare(b[1], a[1]); // 降序比较
            }
        });

        // 输出结果
        System.out.println("Value | Count");
        System.out.println("--------------");
        for (int[] pair : countPairs) {
            System.out.printf("  %d   |   %d%n", pair[0], pair[1]);
        }
    }

}
