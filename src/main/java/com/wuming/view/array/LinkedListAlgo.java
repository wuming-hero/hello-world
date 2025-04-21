package com.wuming.view.array;

/**
 * 1) 单链表反转
 * 2) 链表中环的检测
 * 3) 两个有序的链表合并
 * 4) 删除链表倒数第n个结点
 * 5) 求链表的中间结点
 * <p>
 * Author: Zheng
 */
public class LinkedListAlgo {

    /**
     * 单链表反转
     */
    public static Node reverse(Node list) {
        Node curr = list, pre = null;
        while (curr != null) {
            Node next = curr.next;
            curr.next = pre;
            pre = curr;
            curr = next;
        }
        return pre;
    }

    /**
     * 检测环
     * 示例链表结构：1 → 2 → 3 → 4 → 5 → 3（环的入口是节点 3）
     * 通过快慢指针，如果快指针能追上慢指针，则说明有环。
     */
    public static boolean checkCircle(Node list) {
        if (list == null) return false;

        Node fast = list.next;
        Node slow = list;

        while (fast != null && fast.next != null) {
            fast = fast.next.next;
            slow = slow.next;

            if (slow == fast) return true;
        }
        return false;
    }


    /**
     * 有序链表合并
     * <p>
     * 使用一个循环来遍历两个输入链表,只要两个链表都没有遍历完，就比较当前节点的数据值大小。
     * 1. 如果node1的数据小于node2的数据，则将p的下一个节点设置为node1，并移动node1到它的下一个节点；
     * 2. 否则，将p的下一个节点设置为node2，并移动node2到它的下一个节点。
     * 3. 每次更新p的位置，使其始终指向新链表的最后一个节点。
     */
    public Node mergeTwoLists(Node node1, Node node2) {
        // 利用哨兵结点简化实现难度:这个哨兵节点的作用是简化边界条件处理，使得在后续操作中不需要特别考虑空链表的情况。然后，初始化指针p指向这个哨兵节点
        Node soldier = new Node(0);
        Node p = soldier;


        while (node1 != null && node2 != null) {
            if (node1.data < node2.data) {
                p.next = node1;
                node1 = node1.next;
            } else {
                p.next = node2;
                node2 = node2.next;
            }
            p = p.next;
        }

        // 对于遍历后多的节点，直接拼接在后面
        if (node1 != null) {
            p.next = node1;
        }
        if (node2 != null) {
            p.next = node2;
        }
        return soldier.next;
    }


    /**
     * 删除倒数第K个结点
     * 1. 使用一个循环将fast向前移动k-1次
     * 2. 当fast成功移动了k-1步后，再创建两个新的指针slow和prev，分别指向链表头部和空值。
     * 然后让fast继续前进直到它到达链表的最后一个元素。
     * 与此同时，slow也跟着fast一起前进，而prev则始终紧跟在slow后面一步的位置。
     * 这样当fast到达链表末端时，slow正好位于待删除的节点上，而prev则是它的前驱节点。
     * <p>
     * 示例链表结构：1 → 2 → 3 → 4 → 5 → 6
     *
     * @param list
     * @param k
     * @return
     */
    public static Node deleteLastKth(Node list, int k) {
        // 1. fast先走2(K-1)步，
        Node fast = list;
        int i = 1;
        while (fast != null && i < k) {
            fast = fast.next;
            ++i;
        }
        // 2. 如果完成了这k-1次移动已经到达链表末尾，则说明链表长度小于k，此时直接返回原链表不做任何操作。
        if (fast == null) return list;

        // 3. 然后fast和slow同时走，直到fast走完链表,则slow指向倒数第K个结点
        Node slow = list;
        Node prev = null;
        while (fast.next != null) {
            fast = fast.next;
            prev = slow;
            slow = slow.next;
        }

        if (prev == null) {
            list = list.next;
        } else {
            prev.next = prev.next.next;
        }
        return list;
    }

    /**
     * 求中间结点
     * 通过快慢指针，快指针走到末尾，慢指针就是中间结点
     *
     * @param list
     * @return
     */
    public static Node findMiddleNode(Node list) {
        if (list == null) return null;

        Node fast = list;
        Node slow = list;
        while (fast != null && fast.next != null) {
            fast = fast.next.next;
            slow = slow.next;
        }

        return slow;
    }

    /**
     * 打印所有数据
     *
     * @param list
     */
    public static void printAll(Node list) {
        Node p = list;
        while (p != null) {
            System.out.print(p.data + " ");
            p = p.next;
        }
        System.out.println();
    }

    public static Node createNode(int value) {
        return new Node(value, null);
    }

    public static class Node {
        private int data;
        private Node next;

        public Node(int data) {
            this.data = data;
            this.next = null;
        }

        public Node(int data, Node next) {
            this.data = data;
            this.next = next;
        }

        public int getData() {
            return data;
        }
    }

}
