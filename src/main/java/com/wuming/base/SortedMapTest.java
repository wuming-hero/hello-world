package com.wuming.base;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by PP on 2016/4/21.
 */
public class SortedMapTest {

    public static void main(String args[]) {
        SortedMap<String, String> map = new TreeMap<>();   //通过子类实例化接口对象
        map.put("D", "DDDDD");
        map.put("A", "AAAAA");
        map.put("C", "CCCCC");
        map.put("B", "BBBBB");

        System.out.println("第一个元素的key:" + map.firstKey());
        System.out.println("key对于的值为:" + map.get(map.firstKey()));

        System.out.println("最后一个元素的key:" + map.lastKey());
        System.out.println("key对于的值为:" + map.get(map.lastKey()));

        System.out.println("返回小于指定范围的集合（键值小于“C”）");
        for (Map.Entry<String, String> me : map.headMap("C").entrySet()) {
            System.out.println("\t|- " + me.getKey() + "-->" + me.getValue());
        }
        System.out.println("返回大于指定范围的集合（键值大于等于“C”）");
        for (Map.Entry<String, String> me : map.tailMap("C").entrySet()) {
            System.out.println("\t|- " + me.getKey() + "-->" + me.getValue());
        }
        System.out.println("返回部分集合（键值“B”和“D”之间,包括B不包括D）");
        for (Map.Entry<String, String> me : map.subMap("B", "D").entrySet()) {
            System.out.println("\t|- " + me.getKey() + "-->" + me.getValue());
        }
    }
}
