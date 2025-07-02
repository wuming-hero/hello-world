package com.wuming.view.algorithm.test;

import java.util.Arrays;
import java.util.HashMap;

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

    /**
     * 要求子数组元素相邻
     *
     * 时间复杂度 O(n)
     *
     * @param nums
     * @param target
     * @return
     */
    public static int[] twoSum(int[] nums, int target) {
        // 遍历数组，检查每对相邻元素
        for (int i = 0; i < nums.length - 1; i++) {
            // 检查当前元素与下一个元素的和是否等于目标值
            if (nums[i] + nums[i + 1] == target) {
                // 返回这两个元素的下标
                return new int[]{i, i + 1};
            }
        }
        // 如果未找到匹配的对，返回空数组
        return new int[]{};
    }

    /**
     * 不限制为相邻元素
     *
     * 时间复杂度：O(n) (遍历数组)
     *
     * @param nums
     * @param target
     * @return
     */
    public static int[] findTwoSum(int[] nums, int target) {
        // 创建哈希表存储 数值 及其 索引
        HashMap<Integer, Integer> numMap = new HashMap<>();

        // 遍历数组
        for (int i = 0; i < nums.length; i++) {
            int complement = target - nums[i];

            // 检查补数是否在哈希表中
            if (numMap.containsKey(complement)) {
                // 返回当前索引和补数索引
                return new int[]{numMap.get(complement), i};
            }

            // 将当前数值和索引存入哈希表
            numMap.put(nums[i], i);
        }

        // 未找到匹配对，返回空数组
        return new int[]{};
    }

}
