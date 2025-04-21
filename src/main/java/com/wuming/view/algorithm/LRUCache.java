package com.wuming.view.algorithm;

import java.util.HashMap;
import java.util.Map;

/**
 * 手写双向链表
 *
 * @author manji
 * Created on 2025/4/21 14:54
 */
public class LRUCache {
    Map<Integer, DNode> map = new HashMap<>();
    DNode head, tail;
    int cap;

    public LRUCache(int capacity) {
        head = new DNode();
        tail = new DNode();
        head.next = tail;
        tail.prev = head;
        cap = capacity;
    }

    public int get(int key) {
        if (map.containsKey(key)) {
            DNode node = map.get(key);
            removeNode(node);
            addToHead(node);
            return node.val;
        } else {
            return -1;
        }
    }

    public void put(int key, int value) {
        if (map.containsKey(key)) {
            DNode node = map.get(key);
            node.val = value;
            removeNode(node);
            addToHead(node);
        } else {
            DNode newNode = new DNode();
            newNode.val = value;
            newNode.key = key;
            addToHead(newNode);
            map.put(key, newNode);
            if (map.size() > cap) {
                map.remove(tail.prev.key);
                removeNode(tail.prev);
            }
        }
    }

    public void removeNode(DNode node) {
        DNode prevNode = node.prev;
        DNode nextNode = node.next;
        prevNode.next = nextNode;
        nextNode.prev = prevNode;
    }

    public void addToHead(DNode node) {
        DNode firstNode = head.next;
        head.next = node;
        node.prev = head;
        node.next = firstNode;
        firstNode.prev = node;
    }
}


class DNode {
    DNode prev;
    DNode next;
    int val;
    int key;
}