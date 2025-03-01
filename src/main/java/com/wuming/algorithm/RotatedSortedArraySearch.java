package com.wuming.algorithm;

/**
 * @author manji
 * Created on 2025/3/1 17:01
 */
public class RotatedSortedArraySearch {

    public static int search(int[] nums, int target) {
        int left = 0, right = nums.length - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            // Check if the mid element is the target
            if (nums[mid] == target) {
                return mid;
            }

            // Determine which side is sorted
            if (nums[left] <= nums[mid]) {
                // Left side is sorted
                if (nums[left] <= target && target < nums[mid]) {
                    right = mid - 1; // Target is in the sorted left side
                } else {
                    left = mid + 1; // Target is in the right side
                }
            } else {
                // Right side is sorted
                if (nums[mid] < target && target <= nums[right]) {
                    left = mid + 1; // Target is in the sorted right side
                } else {
                    right = mid - 1; // Target is in the left side
                }
            }
        }

        return -1; // Target not found
    }

    public static void main(String[] args) {
        int[] arr = {4, 5, 6, 9, 1, 2}; // Rotated sorted array
        int target = 9;

        int result = search(arr, target);
        if (result != -1) {
            System.out.println("Target found at index: " + result);
        } else {
            System.out.println("Target not found.");
        }
    }

}
