package com.wuming.view.sort;

/**
 * 算法原理
 * 1. 比较与交换‌：
 * 从数组首元素开始，依次比较相邻的两个元素。
 * 若前一个元素大于后一个元素（升序排序），则交换它们的位置。
 * 2. 多轮遍历‌：
 * 每轮遍历会将当前未排序部分的最大值“冒泡”到数组末尾。
 * 需进行 n-1 轮遍历（n 为数组长度），每轮减少一个已排序元素。
 * <p>
 * 时间复杂度‌：
 * 最优情况（已排序数组）：O(n)（通过优化可提前终止）。
 * 最差/平均情况：O(n²)。
 * <p>
 * 空间复杂度‌：O(1)（原地排序）。
 * ‌稳定性‌：稳定（相同元素不改变相对位置）。
 * <p>
 * 优化版本
 * 可添加标志位，若某轮未发生交换则提前终止排序。
 *
 * @author che
 * Created on 2025/6/4 13:38
 */
public class BubbleSort {

    // 基础版本
    public static void bubbleSortBasic(int[] arr) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - 1 - i; j++) {
                if (arr[j] > arr[j + 1]) {
                    // 交换相邻元素
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }
            }
        }
    }

    // 优化版本（添加交换标志）
    public static void bubbleSortOptimized(int[] arr) {
        int n = arr.length;
        boolean swapped;
        for (int i = 0; i < n - 1; i++) {
            swapped = false;
            for (int j = 0; j < n - 1 - i; j++) {
                if (arr[j] > arr[j + 1]) {
                    // 交换相邻元素
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                    swapped = true;
                }
            }
            if (!swapped) break; // 本轮无交换则提前终止
        }
    }

    public static void main(String[] args) {
        int[] arr = {64, 34, 25, 12, 22, 11, 90};
        bubbleSortOptimized(arr);
        System.out.println("排序结果:");
        for (int num : arr) {
            System.out.print(num + " ");
        }
    }

}
