package com.wuming.view.algorithm.test;

import java.util.Arrays;

/**
 * @author che
 * Created on 2025/5/30 12:41
 */
public class MergeSortedArray {

    public static void main(String[] args) {
        int nums1[] = new int[]{1, 2, 3, 0, 0, 0};
        int nums2[] = new int[]{2, 5, 6};
        merge(nums1, 3, nums2, 3);
        System.out.println(Arrays.toString(nums1));
    }

    /**
     * 合并2个正序排列的数组，合并后，还是正序的
     *
     * 该代码通过双指针从后向前遍历，避免了额外的空间开销，时间复杂度为 O(m + n)，空间复杂度为 O(1)
     *
     * @param nums1
     * @param m
     * @param nums2
     * @param n
     */
    public static void merge(int[] nums1, int m, int[] nums2, int n) {
        int p1 = m - 1;  // nums1的最后一个有效元素
        int p2 = n - 1;  // nums2的最后一个元素
        int p = m + n - 1;  // 合并后的最后一个位置

        while (p1 >= 0 && p2 >= 0) {
            if (nums1[p1] > nums2[p2]) {
                nums1[p--] = nums1[p1--];
            } else {
                nums1[p--] = nums2[p2--];
            }
        }

        // 如果nums2还有剩余元素
        while (p2 >= 0) {
            nums1[p--] = nums2[p2--];
        }
    }

}
