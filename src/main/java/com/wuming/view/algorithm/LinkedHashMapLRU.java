package com.wuming.view.algorithm;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

/**
 * 基于linkedHashMap LRU 实现
 *
 * @author manji
 * Created on 2025/4/21 14:52
 */
public class LinkedHashMapLRU {
    private LinkedHashMap<Integer, Integer> cache;
    private int capacity; //容量大小

    public LinkedHashMapLRU(int capacity) {
        cache = new LinkedHashMap<>(capacity);
        this.capacity = capacity;
    }

    public int get(int key) { //缓存中不存在此key，直接返回
        if (!cache.containsKey(key)) {
            return -1;
        }
        int res = cache.get(key);
        cache.remove(key); //先从链表中删除
        cache.put(key, res); //再把该节点放到链表末尾处
        return res;
    }

    public void put(int key, int value) {
        if (cache.containsKey(key)) {
            cache.remove(key); //已经存在，在当前链表移除
        }
        if (capacity == cache.size()) {
            //cache已满，删除链表头位置
            Set<Integer> keySet = cache.keySet();
            Iterator<Integer> iterator = keySet.iterator();
            cache.remove(iterator.next());
        }
        cache.put(key, value); //插入到链表末尾
    }
}
