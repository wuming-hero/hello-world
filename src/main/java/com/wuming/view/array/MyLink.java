package com.wuming.view.array;

/**
 * @author wuming
 * Created on 2020-11-18 19:00
 */
public class MyLink {

    Node head = null;
    // 头节点
    /**
     * 链表中的节点，data代表节点的值，next是指向下一个节点的引用
     *
     * @author zjn
     *
     */
    class Node {
        Node next = null;
        // 节点的引用，指向下一个节点
        int data;
        // 节点的对象，即内容
        public Node(int data) {
            this.data = data;
        }

        @Override
        public String toString() {
            return "Node{" +
                    "next=" + next +
                    ", data=" + data +
                    '}';
        }
    }


    /**
     * 向链表中插入数据
     *
     * @param d
     */
    public void addNode(int d) {
        Node newNode = new Node(d);
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
     *
     * @param index:删除第index个节点
     * @return
     */
    public Boolean deleteNode(int index) {
        if (index < 1 || index > length()) {
            return false;
        }
        if (index == 1) {
            head = head.next;
            return true;
        }
        int i = 1;
        Node preNode = head;
        Node curNode = preNode.next;
        while (curNode != null) {
            if (i == index) {
                preNode.next = curNode.next;
                return true;
            }
            preNode = curNode;
            curNode = curNode.next;
            i++;
        }
        return false;
    }
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
     * 在不知道头指针的情况下删除指定节点
     *
     * @param n
     * @return
     */
    public Boolean deleteNode11(Node n) {
        if (n == null || n.next == null)
            return false;
        int tmp = n.data;
        n.data = n.next.data;
        n.next.data = tmp;
        n.next = n.next.next;
        System.out.println("删除成功！");
        return true;
    }
    public void printList() {
        Node tmp = head;
        while (tmp != null) {
            System.out.println(tmp.data);
            tmp = tmp.next;
        }
    }

    /**
     * 链表反转
     *
     * @param head
     * @return
     */
    public Node reverseIteratively(Node head) {
        Node pReversedHead = head;
        Node pNode = head;
        Node pPrev = null;
        while (pNode != null) {
            Node pNext = pNode.next;
            if (pNext == null) {
                pReversedHead = pNode;
            }
            pNode.next = pPrev;
            pPrev = pNode;
            pNode = pNext;
        }
        this.head = pReversedHead;
        return this.head;
    }

    /**
     *  1.就地反转法
     * @param head
     * @return
     */
    public Node reverseList1(Node head) {
        if (head == null) {
            return head;
        }
        Node dummy = new Node(-1);
        dummy.next = head;
        Node prev = dummy.next;
        Node pCur = prev.next;
        while (pCur != null) {
            prev.next = pCur.next;
            pCur.next = dummy.next;
            dummy.next = pCur;
            pCur = prev.next;
        }
        return dummy.next;
    }

    /**
     * 查找单链表的中间节点
     *
     * @return
     */
    public Node searchMid() {
        Node p = this.head, q = this.head;
        while (p != null && p.next != null && p.next.next != null) {
            p = p.next.next;
            q = q.next;
        }
        System.out.println("Mid:" + q.data);
        return q;
    }

    public static void main(String[] args) {
        MyLink list = new MyLink();
        list.addNode(5);
        list.addNode(3);
        list.addNode(1);
        list.addNode(2);
        list.addNode(55);
        list.addNode(36);
        list.addNode(37);
//        System.out.println("linkLength:" + list.length());
//        System.out.println("head.data:" + list.head.data);
//        list.printList();
//        list.deleteNode(4);
//        System.out.println("After deleteNode(4):");
//        list.printList();
//        Node midNod = list.searchMid();
//        System.out.printf("" + midNod);

        Node reverseNode = list.reverseIteratively(list.head);
        System.out.println("" + reverseNode);

        Node reverseNode2 = list.reverseList1(list.head);
        System.out.println("" + reverseNode2);
    }


}
