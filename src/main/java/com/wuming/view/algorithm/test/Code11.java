package com.wuming.view.algorithm.test;


/**
 * 给定一个长度为 n 的整数数组 height 。有 n 条垂线，第 i 条线的两个端点是 (i, 0) 和 (i, height[i]) 。
 *
 * 找出其中的两条线，使得它们与 x 轴共同构成的容器可以容纳最多的水。
 *
 * 返回容器可以储存的最大水量。
 *
 * 说明：你不能倾斜容器。
 *
 * 输入：[1,8,6,2,5,4,8,3,7]
 * 输出：49
 * 解释：图中垂直线代表输入数组 [1,8,6,2,5,4,8,3,7]。在此情况下，容器能够容纳水（表示为蓝色部分）的最大值为 49。
 *
 * @author manji
 * Created on 2025/4/24 20:48
 */
public class Code11 {

    public static void main(String[] args) {
        int[] nums = new int[] {1, 8, 6, 2, 5, 4, 8, 3, 7};
//        int[] nums = new int[]{1, 1};
        System.out.println(maxArea(nums));
    }

    public static int maxArea(int[] nums) {
        if (nums == null || nums.length < 2) {
            return 0;
        }
        int left = 0, right = nums.length - 1;
        int maxArea = 0;
        while (left < right) {
            // 面积的一边Y由最小边决定
            // 面积的另一边X由2个指针之间的距离决定
            int temp = Math.min(nums[left], nums[right]) * (right - left);
            maxArea = Math.max(temp, maxArea);
            if(nums[left] < nums[right]) {
                left++;
            } else {
                right--;
            }
        }
        return maxArea;
    }

}
