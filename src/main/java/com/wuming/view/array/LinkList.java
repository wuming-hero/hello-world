package com.wuming.view.array;

/**
 * 生成一个点链表类
 *
 * @author wuming
 * Created on 2020-11-18 13:01
 */
public class LinkList {
    // 节点元素
    public Node head;
    // 大小

    /**
     *
     * @return 返回节点长度
     */
    public int length() {
        int length = 0;
        Node tmp = head;
        while (tmp != null) {
            length++;
            tmp = tmp.next;
        }
        return length;
    }
    /**
     * 增加节点
     *
     * @param data
     */
    public void addNode(int data) {
        if (head == null) {
            head = new Node(data);
        } else {
            head.next = new Node(data);
        }
    }

    /**
     * 在指定位置插入数据
     *
     * @param index
     * @param newNode
     */
    public void insertNodeByIndex(int index, Node newNode) {
        if (index < 1 || index > length()) {
            throw new RuntimeException("位置" + index + "无效");
        }
        Node temp = head;
        // length = 1 代表就是head.next,第2个元素
        int length = 1;
        while (temp.next != null) {
            if (index == length++) {
                newNode.next = temp.next;
                temp.next = newNode;
                return;
            }
            temp = temp.next;
        }
    }

    /**
     * 根据索引删除
     * 1. 第0个链表关，不可删除
     * 2.
     *
     * @param index
     */
    public void deleteNodeByIndex(int index) {
        Node temp = head;
        if (index < 1 || index > length()) {
            throw new RuntimeException("位置" + index + "无效");
        }
        int length = 1;
        while (temp.next != null) {
            if (index == length++) {
                temp.next = temp.next.next;
                return;
            }
            // 遍历
            temp = temp.next;
        }
    }

    /**
     * 链表中元素排序
     */
    public void selectSortNode() {
        Node temp = head;
        // 一层循环遍历列表
        while (temp.next != null) {
            // 二层循环比较大小
            Node secondNode = temp.next;
            while (secondNode.next != null) {
                // 比较大小
                if (secondNode.data > secondNode.next.data) {
                    int tempData = secondNode.next.data;
                    secondNode.next.data = secondNode.data;
                    secondNode.data = tempData;
                }
            }
            temp = temp.next;
        }
    }


}


/**
 * 结点
 */
class Node {

    public int data;
    public Node next;

    public Node(int data) {
        this.data = data;
    }

}
