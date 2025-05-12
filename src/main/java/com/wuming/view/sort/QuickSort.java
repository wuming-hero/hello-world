package com.wuming.view.sort;

import java.util.Arrays;

/**
 *
 * @author manji
 * Created on 2025/5/12 22:19
 */
public class QuickSort {

    public static void quickSort(int[] arr, int low, int high) {
        if (low < high) {
            // 分区并获取基准索引
            int pivotIndex = partition(arr, low, high);
            // 递归排序左半部分
            quickSort(arr, low, pivotIndex - 1);
            // 递归排序右半部分
            quickSort(arr, pivotIndex + 1, high);
        }
    }

    /**
     * 对数组进行分区操作
     *
     * <p>选择最后一个元素作为基准，将小于或等于基准的元素移动到基准的左侧，
     * 并最终将基准放置到正确的位置。
     *
     * @param arr 待分区的数组
     * @param low 数组分区的起始索引
     * @param high 数组分区的结束索引
     * @return 基准元素的最终位置索引
     */
    private static int partition(int[] arr, int low, int high) {
        int pivot = arr[high];  // 选择最后一个元素为基准
        int i = low - 1;        // 指向比基准小的元素的右边界

        for (int j = low; j < high; j++) {
            // 当前元素 <= 基准时，交换到左侧区域
            if (arr[j] <= pivot) {
                i++;
                swap(arr, i, j);
            }
        }
        // 将基准放到正确位置
        swap(arr, i + 1, high);
        return i + 1;  // 返回基准的最终位置
    }

    /**
     * 交换数组中两个指定索引的元素
     *
     * <p>该方法通过临时变量交换给定数组中索引为i和j的两个元素的值。
     *
     * @param arr 要进行元素交换的整数数组
     * @param i 要交换的第一个元素的索引
     * @param j 要交换的第二个元素的索引
     * @return 无返回值
     */
    private static void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    public static void main(String[] args) {
        int[] arr = {5, 3, 7, 6, 2, 9};
        quickSort(arr, 0, arr.length - 1);
        System.out.println(Arrays.toString(arr));  // 输出：[2, 3, 5, 6, 7, 9]
    }

}
