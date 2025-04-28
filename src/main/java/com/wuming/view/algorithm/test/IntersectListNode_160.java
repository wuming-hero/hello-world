package com.wuming.view.algorithm.test;

/**
 * 给你两个单链表的头节点 headA 和 headB ，请你找出并返回两个单链表相交的起始节点。如果两个链表不存在相交节点，返回 null 。
 * <p>
 * 图示两个链表在节点 c1 开始相交：
 * <p>
 * 输入：intersectVal = 8, listA = [4,1,8,4,5], listB = [5,6,1,8,4,5], skipA = 2, skipB = 3
 * 输出：Intersected at '8'
 * <p>
 * 时间复杂度 O(a+b) ： 最差情况下（即 ∣a−b∣=1 , c=0 ），此时需遍历 a+b 个节点。
 * 空间复杂度 O(1) ： 节点指针 A , B 使用常数大小的额外空间。
 *
 * @author manji
 * Created on 2025/4/27 21:43
 */
public class IntersectListNode_160 {

    public ListNode getIntersectionNode(ListNode headA, ListNode headB) {
        ListNode A = headA, B = headB;
        while (A != B) {
            A = A != null ? A.next : headB;
            B = B != null ? B.next : headA;
        }
        return A;
    }


    public static class ListNode {

        private int val;
        private ListNode next;

        public ListNode(int val) {
            this.val = val;
        }

        public ListNode(int val, ListNode next) {
            this.val = val;
            this.next = next;
        }
    }


}
