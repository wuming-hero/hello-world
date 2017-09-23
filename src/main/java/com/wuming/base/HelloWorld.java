package com.wuming.base;

import org.junit.Test;

/**
 * Created by PP on 2016/3/26.
 * 在Java中，对于对象==是比较两个对象的地址。
 */
public class HelloWorld {

    @Test
    public void idTest() {
        System.out.println("HelloWorld!");
        String a = "com/wuming";
        String str = new String("com/wuming");
        String string = "com/wuming";
        System.out.println(a + "----内存地址----" + System.identityHashCode(a));
        System.out.println(str + "----内存地址----" + System.identityHashCode(str));
        System.out.println(string + "----内存地址----" + System.identityHashCode(string));

        int b = Integer.parseInt("11");
        int c = 10;
        System.out.println(b + "----内存地址----" + System.identityHashCode(b));
        System.out.println(c + "----内存地址----" + System.identityHashCode(c));
    }

    /**
     * 1.当Integer的值落在-128~127之间时，如Integer i1 = 2; Integer i2 = 2;此时JVM首先检查是否已存在值为2的Integer对象
     * 2.对于显式的new Integer(2),JVM将直接分配新空间
     * Java的数学计算是在内存栈里操作的，Java会对i5、i6进行拆箱操作，其实比较的是基本类型（40=40+0），他们的值相同，因此结果为True
     */
    @Test
    public void boxTest() {
        Integer i1 = 40;
        Integer i2 = 40;
        Integer i3 = 0;
        Integer i4 = new Integer(40);
        Integer i5 = new Integer(40);
        Integer i6 = new Integer(0);

        System.out.println("i1=i2\t" + (i1 == i2));
        System.out.println("i1=i2+i3\t" + (i1 == i2 + i3));
        System.out.println("i4=i5\t" + (i4 == i5));
        System.out.println("i4=i5+i6\t" + (i4 == i5 + i6));
    }

    /**
     * 1.对于使用字面量赋值方式。JVM为了节省空间，s2会首先查找JVM中是否有"cat"的字符串常量。如果已经存在，则直接返回该引用，而无需重新创建对象。
     * 2.对象new创建方式，JVM将分配新空间。
     */
    @Test
    public void foo() {
        Integer i1 = 2;
        Integer i2 = 2;
        Integer i3 = new Integer(2);
        System.out.println("i1 = i2? " + (i1 == i2));  //true
        System.out.println("i1 = i3? " + (i1 == i3));  //false

        Integer i4 = 1000;
        Integer i5 = 1000;
        System.out.println("i4 = i5? " + (i4 == i5)); //false

        String s1 = "cat";
        String s2 = "cat";
        String s3 = new String("cat");
        System.out.println("s1 = s2? " + (s1 == s2));  //true
        System.out.println("s1 = s3? " + (s1 == s3));  //false
    }

}













