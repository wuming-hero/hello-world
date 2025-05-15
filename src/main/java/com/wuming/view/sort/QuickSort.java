package com.wuming.view.sort;

import java.util.Arrays;

/**
 * 1. 快速排序：
 * 稳定性：不稳定
 * 时间复杂度：
 * 最佳：O (nlogn )
 * 最差：O(n^2)：这种情况发生在每次选择的基准都是当前数组的最大或最小值，导致每次分区后只减少一个元素。这通常会在已经排序好的数组上发生，如果总是选择第一个或最后一个元素作为基准。
 * 平均：O(nlogn)
 * 空间复杂度：O(logn)
 *
 *
 * 2. 实现原理
 * 选择基准
 * 2.1 选择最后一个数作为基准
 * 2.2 定义了一个pointer变量，它指向分区后应该放置小于等于pivot值的位置，初始为low(指向数组最左边位置)
 *
 *
 * 3. 遍历重排元素
 * 3.1 从 low 开始遍历到 high - 1， 如果当前元素小于或等于pivot，则将该元素与pointer位置的元素互换，并将pointer向前移动一位。
 * 3.2 这一步确保了所有小于等于pivot的元素都被移到了pointer左侧。
 *
 * 4. 将pivot置于中间
 * 遍历完后pointer左边的都是小于等于pivot，右边的(包括pointer)都是大于pivot，此时应该将pivot和pointer交换，使得pivot移到正确位置
 *
 * 5. 使用递归划分子数组
 *
 * 原文链接：https://blog.csdn.net/weixin_64451672/article/details/145160145
 *
 * 快排图文详解 https://blog.csdn.net/weixin_64451672/article/details/145160145
 * @author manji
 * Created on 2025/5/12 22:19
 */
public class QuickSort {

    /**
     * 快速排序
     * 1. 选取一个基准元素，通常选择数组的最后一个元素
     * 2. 基于基准元素将数组分为两个子数组：小于基准的元素和大于基准的元素
     * @param arr
     * @param low
     * @param high
     */
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
        // 选择最后一个元素为基准(可以随机选择)
        int pivot = arr[high];
        // 定义了一个pointer变量，它指向分区后应该放置小于等于pivot值的位置，初始为low
        int pointer = low;

        for (int j = low; j < high; j++) {
            // 当前元素 <= 基准时，交换到左侧区域
            if (arr[j] <= pivot) {
                swap(arr, pointer, j);
                pointer++;
            }
        }
        // 将基准放到正确位置
        swap(arr, pointer, high);
        return pointer;  // 返回基准的最终位置
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
