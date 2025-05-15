package com.wuming.view.algorithm;

/**
 * 二分查找
 * <p>
 * 给你一个搜索的目标值。如果在数组中找到，返回它的索引值，否则返回- 1
 *
 * @author manji
 * Created on 2025/3/1 16:47
 */
public class BinarySearch {
    /**
     * 二分查找（二分查找要求数组是已排序的，对于排序数组，采用二分查找可以显著提高查找效率。）
     * <p>
     * 1.定义 left 和 right 变量表示搜索区间的边界。在每次迭代中计算中间元素的索引，比较中间元素与目标值。
     * 2. 根据比较结果缩小搜索范围，直到找到目标值或范围为空。
     *
     * @param arr
     * @param target
     * @return
     */
    public static int binarySearch(int[] arr, int target) {
        int left = 0;
        int right = arr.length - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2; // 计算中间索引

            if (arr[mid] == target) {
                return mid; // 找到目标值，返回索引
            }
            if (arr[mid] < target) {
                left = mid + 1; // 搜索右半边
            } else {
                right = mid - 1; // 搜索左半边
            }
        }
        return -1; // 未找到目标值，返回 -1
    }

    public static void main(String[] args) {
        int[] arr = {1, 2, 3, 4, 5, 6}; // 数组必须是已排序的
        int target = 3;

        int index = binarySearch(arr, target);
        if (index != -1) {
            System.out.println("Target found at index: " + index);
        } else {
            System.out.println("Target not found.");
        }
    }

}
