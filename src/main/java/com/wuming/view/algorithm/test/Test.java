package com.wuming.view.algorithm.test;

import java.util.Arrays;

/**
 * 找到数组中2个数之和等于目标数据的子数组
 *
 * @author manji
 * Created on 2025/4/24 16:21
 */
public class Test {

    public static void main(String[] args) {
//        int nums[] = new int[]{2, 7, 11, 15};
        int nums[] = new int[]{-3, 4, 3, 5};
        int target = 2;
        int result[] = twoSum(nums, target);
        System.out.println(Arrays.toString(result));
    }

    public static int[] twoSum(int[] nums, int target) {
        for (int j = 0; j < nums.length; j++) {
            for (int k = j + 1; k < nums.length; k++) {
                if (nums[k] + nums[j] == target) {
                    return new int[]{j, k};
                }
            }
        }
        return null;
    }

}
