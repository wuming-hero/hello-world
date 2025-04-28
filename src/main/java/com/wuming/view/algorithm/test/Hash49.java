package com.wuming.view.algorithm.test;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 给你一个字符串数组，请你将 字母异位词 组合在一起。可以按任意顺序返回结果列表。
 * <p>
 * 字母异位词 是由重新排列源单词的所有字母得到的一个新单词。
 * <p>
 * 示例 1:
 * <p>
 * 输入: strs = ["eat", "tea", "tan", "ate", "nat", "bat"]
 * 输出: [["bat"],["nat","tan"],["ate","eat","tea"]]
 * 示例 2:
 * <p>
 * 输入: strs = [""]
 * 输出: [[""]]
 * 示例 3:
 * <p>
 * 输入: strs = ["a"]
 * 输出: [["a"]]
 *
 * @author manji
 * Created on 2025/4/24 17:16
 */
public class Hash49 {

    public static void main(String[] args) {
        String[] strs = new String[]{"eat", "tea", "tan", "ate", "nat", "bat"};
        strs = new String[]{"a"};
        System.out.println(groupAnagrams(strs));
    }

    public static List<List<String>> groupAnagrams(String[] strs) {
        Map<String, List<String>> map = new HashMap<>();
        for (String str : strs) {
            // 将字符串排序后作为key
            char[] chars = str.toCharArray();
            Arrays.sort(chars);
            String unionKey = String.valueOf(chars);
            // 使用key hash作为分组
            if (map.containsKey(unionKey)) {
                map.get(unionKey).add(str);
            } else {
                List<String> value = new ArrayList<>();
                value.add(str);
                map.put(unionKey, value);
            }
        }

        return new ArrayList<>(map.values());
    }

}
