package com.wuming.view.array;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 2、如果一组字符串是通过单链表来存储的，那该如何来判断该字符串是一个回文字符串？并说明一下其时间空间复杂度是多少。
 * 回文链表如 ABBA 或 ABCBA
 *
 * @author wuming
 * Created on 2020-11-19 07:35
 */
public class LinkListView {

    /**
     * 链表的头节点
     */
    public Node head;

    public static void main(String[] args) {
        LinkListView linkListView = new LinkListView();
        linkListView.addNode("A");
        linkListView.addNode("B");
        linkListView.addNode("C");
        linkListView.addNode("D");

        // 原链表数据
        System.out.println(linkListView.head);
        // 反转后的链表数据
        Node reverseNode = linkListView.reverseNode(linkListView.head);
        System.out.println(reverseNode);
    }

    /*

     */

    /**
     * 判断一个node 是否是回文
     *
     * @param linkListView
     * @return
     */
    public boolean isValid(LinkListView linkListView) {
        // 1. 链表toString得到字符串，进行反转后，再次得到字符串，进行比较
        // 遍历需要比较的node

        List<String> nodeDataList = new ArrayList<>();
        Node temp = linkListView.head;
        while (temp.next != null) {
            nodeDataList.add(temp.data);
            temp = temp.next;
        }

        List<String> reverseDataList = new ArrayList<>();
        Node reverseNode = reverseNode(linkListView.head);
        Node reverseTemp = reverseNode;
        while (reverseTemp.next != null) {
            reverseDataList.add(reverseTemp.data);
            reverseTemp = reverseTemp.next;
        }
        // 循环对反转前和反转后的字符分别比较
        for (int i = 0; i < nodeDataList.size(); i++) {
            if (!Objects.equals(nodeDataList.get(i), reverseDataList.get(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 链表反转，
     * A B C D
     * B A C D
     * C B A D
     * D C B A
     * @param head 链表的头
     * @return
     */
    public Node reverseNode(Node head) {
        // 新建一个虚拟头节点
        Node dummyNode = new Node("a");
        dummyNode.next = head;
        // 要反转的前一个节点
        Node preNode = dummyNode.next;
        // 当前需要反转的节点
        Node curNode = dummyNode.next.next;
        while (curNode != null) {
            preNode.next = curNode.next;
            curNode.next = dummyNode.next;
            dummyNode.next = curNode;
            curNode = preNode.next;
        }
        return dummyNode.next;
    }

    /**
     * 向链表中添加数据
     *
     * @param data
     */
    public void addNode(String data) {
        Node newNode = new Node(data);
        // 实例化一个节点
        if (head == null) {
            head = newNode;
            return;
        }
        Node tmp = head;
        while (tmp.next != null) {
            tmp = tmp.next;
        }
        tmp.next = newNode;
    }

    /**
     * 声明节点
     * 直接声明为public 省掉get set
     */
    class Node {
        public String data;
        public Node next;

        public Node(String data) {
            this.data = data;
        }

        @Override
        public String toString() {
            return "Node{" +
                    "data='" + data + '\'' +
                    ", next=" + next +
                    '}';
        }
    }

}
