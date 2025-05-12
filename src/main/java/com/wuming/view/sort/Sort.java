package com.wuming.view.sort;

import java.util.Arrays;

/**
 * 常见的排序方式
 *
 * @author che
 * Created on 2025/5/12 20:57
 */
public class Sort {


    public static void main(String[] args) {
        int[] arr = {5,7,4,3,6,2};
        bubbleSort(arr);
        System.out.println(Arrays.toString(arr));

    }

    /**
     * 冒泡排序
     * 冒泡排序（Bubble Sort）通常是其中之一。虽然它不是最高效的排序算法之一，但它的简单性和易于理解使它成为学习排序算法的良好起点
     * 时间复杂度：O(n^2)
     * 冒泡排序（Bubble Sort）是一种简单的排序算法，它通过多次遍历待排序的元素，比较相邻元素的大小，并交换它们直到整个序列有序。冒泡排序的基本思想是将较大的元素逐渐“浮”到数组的右端，而较小的元素逐渐“沉”到数组的左端。其基本原理如下：
     *
     * 1. 从数组的第一个元素开始，比较相邻的两个元素。
     * 2. 如果前一个元素大于后一个元素（升序排序），则交换它们的位置。
     * 3. 步骤1和步骤2，直到遍历整个数组。
     * 4. 上步骤，每次遍历都将最大的元素“冒泡”到数组的末尾。
     * 5. 复以上步骤，但不包括已排序的最大元素，直到整个数组排序完成。
     * @param arr
     */
    private static void bubbleSort(int[] arr) {
        int n = arr.length;
        // 外部循环控制排序的趟数。冒泡排序的每一趟会将最大的元素"冒泡"到数组的末尾，因此需要执行 n-1 趟，其中 n 是元素的总数
        for (int i = 0; i < n - 1; i++) {
            // 内循环控制每趟比较的次数。由于每一趟都会将一个最大的元素沉到数组末尾，所以内循环次数逐渐减小。
            for (int j = 0; j < n - i - 1; j++) {
                if (arr[j] > arr[j + 1]) {
                    // 交换arr[j]和arr[j+1]
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }
            }
            System.out.println("第"+(i+1)+"趟："+Arrays.toString(arr));
        }
    }

    /**
     *
     * @param arr
     */
    private static void selectionSort(int[] arr) {

    }

    private static void insertionSort(int[] arr) {

    }

    private static void shellSort(int[] arr) {

    }

    private static void mergeSort(int[] arr) {

    }

    /**
     * 快速排序
     * @param arr
     * @param left
     * @param right
     */
    private static void quickSort(int[] arr, int left, int right) {

    }

    private static void heapSort(int[] arr) {

    }

}
