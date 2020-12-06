package com.wuming.view.array;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 2、如果一组字符串是通过单链表来存储的，那该如何来判断该字符串是一个回文字符串？并说明一下其时间空间复杂度是多少。
 * 回文链表如 ABBA 或 ABCBA
 * <p>
 * 链表学习
 * https://www.jianshu.com/p/9a4561d42e9a
 * https://www.jianshu.com/p/21b4b8d7d31b
 * <p>
 * https://www.jb51.net/article/128730.htm
 * https://www.cnblogs.com/whgk/p/6589920.html
 * https://www.jianshu.com/p/9a4561d42e9a
 * <p>
 * 链表反转总结篇
 * https://www.cnblogs.com/mwl523/p/10749144.html
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
        linkListView.addNode("C");
        linkListView.addNode("B");
        linkListView.addNode("A");

        // 原链表数据
        System.out.println(linkListView.head);
//        // 反转后的链表数据
//        Node reverseNode = linkListView.reverseNode(linkListView.head);
//        System.out.println(reverseNode);

//        System.out.println(linkListView.head);

        Boolean flag = isValidLink(linkListView.head);
        System.out.println("是回文链表：" + flag);

    }

    /*

     */

    /**
     * 判断链表是否是回文链表
     * <p>
     * 使用快慢指针判断是否是回文
     * 快指针每次走二步，慢指针每次走一步，循环结束后，当节点数量为奇数时，slow来到了中间位置，当节点数量为偶数时，slow来到了中间位置（虚线）的前一个
     *
     * @param head
     * @return
     */
    public static boolean isValidLink(Node head) {
        Node fast = head;
        Node slow = head;
        // 通过遍历快指针循环链表
        while (fast.next != null && fast.next.next != null) {
            fast = fast.next.next;
            slow = slow.next;
        }

        // 这里的slow就表示中间节点，记录slow的位置
        Node middle = slow;
        // 记录中间节点的下一个节点位置，该节点及其以后的节点即为后半部分链表
        Node help = slow.next;
        // 中间节点指向null
        slow.next = null;

        // 从help这个节点反转链表（后半部分链表进行反转）,pre即反转后的节点
        Node pre = null;
        Node next = null;
        // 循环结束后，pre就是最后一个节点
        while (help != null) {
            next = help.next;
            help.next = pre;
            pre = help;
            help = next;
        }

        // 判断是否是回文
        boolean flag = true;
        Node first = head;
        Node second = pre;
        while (first != null && second != null) {
            // 如果有一个不相等，则非回文链表
            if (!Objects.equals(first.data, second.data)) {
                flag = false;
                break;
            }
            first = first.next;
            second = second.next;
        }
        System.out.println(flag);

        // 将链表恢复为原来的结构（再次将后半部分链表反转）
        Node help_restore = pre;
        Node pre_restore = null;
        Node next_restore = null;
        while (help_restore != null) {
            next_restore = help_restore.next;
            help_restore.next = pre_restore;
            pre_restore = help_restore;
            help_restore = next_restore;
        }
        middle.next = pre_restore;
        return flag;
    }

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
     *
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
