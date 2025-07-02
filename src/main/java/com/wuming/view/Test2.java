package com.wuming.view;

/**
 * @author che
 * Created on 2025/5/26 16:40
 */
public class Test2 {

    public static void main(String[] args) {

        Node node = new Node(1);
        node.next = new Node(2);
        node.next.next = new Node(3);
//        reverse(node);
        System.out.println(node.toString());

        Node reversedNode = reverse2(node);
        System.out.println(reversedNode.toString());
    }

    public static void reverse(Node head) {
        Node current = head;
        Node pre = null;
        while (current != null) {
            Node next = current.next;
            current.next = pre;
            pre = current;
            current = next;
        }
    }


    /**
     * 单链表反转
     */
    public static Node reverse2(Node list) {
        Node curr = list, pre = null;
        while (curr != null) {
            Node next = curr.next;
            curr.next = pre;
            pre = curr;
            curr = next;
        }
        return pre;
    }


    public static class Node {
        private int value;
        private Node next;

        public Node(int value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "Node{" +
                    "value=" + value +
                    '}';
        }
    }

}

