package com.wuming.list;

import com.wuming.model.Person;
import com.wuming.util.ListUtil;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 现在来说说<一些不靠谱的java.util.List深复制方法>,这些不靠谱List深复制的做法在某些情况是可行的，这也是为什么有些人说这其中的一些做法是可以实现深复制的原因。
 * 哪些情况下是可行（本质上可能还是不靠谱）的呢？比如List<String>这样的情况。
 * <p>
 * 我上面使用的是List<Person>，它和List<String>的区别就在于Person类和String类的区别，Person类提供了破坏数据的2个setter方法。
 * 因此，在浅复制的情况下，源数据被修改破坏之后，使用相同引用指向该数据的目标集合中的对应元素也就发生了相同的变化。
 * 因此，在需求要求必须深复制的情况下，要是使用上面提到的方法，请确保List<T>中的T类对象是不易被外部修改和破坏的。
 *
 * @author wuming
 * Created on 2017/11/15 11:51
 */
public class ListCopy {

    /**
     * 初始化一个原list数据
     */
    Person p1 = new Person(20, "A");
    Person p2 = new Person(21, "B");
    Person p3 = new Person(22, "C");
    List<Person> srcList = new ArrayList<Person>() {{
        add(p1);
        add(p2);
        add(p3);
    }};

    /**
     * 基础数据类型List
     */
    List<String> srcList2 = new ArrayList<String>() {{
        add("A");
        add("B");
        add("C");
    }};


    /**
     * 打印列表
     *
     * @param list
     * @param <T>
     */
    public static <T> void printList(List<T> list) {
        System.out.println("---begin---");
        for (T t : list) {
            System.out.println(t);
        }
        System.out.println("---end---");
    }

    /**
     * 打印数组
     *
     * @param array
     * @param <T>
     */
    public static <T> void printArray(T[] array) {
        System.out.println("---begin---");
        for (T t : array) {
            System.out.println(t);
        }
        System.out.println("---end---");
    }

    /**
     * 循环遍历复制 和 list.addAll()方法 一样 都是浅复制
     * <p>
     * 对基本数据类型可以深复制，但对于类结构的数据还是浅复制
     * <p>
     * 上面的代码在add时候，并没有new Person()操作。因此，在srcList.get(0).setAge(100);破坏源数据时，
     * 目标集合destList中元素的输出同样受到了影响，原因是浅复制造成的。
     */
    @Test
    public void forTest() {
        List<Person> destList = new ArrayList<>(srcList.size());
        for (Person p : srcList) {
            destList.add(p);
        }
        printList(destList);
        srcList.get(0).setAge(100);
        printList(destList);

        // 基础类型可以实现深复制
        List<String> destList2 = new ArrayList<>(srcList2.size());
        destList2.addAll(srcList2);
        printList(destList2);
        srcList2.set(0, "D");
        printList(destList2);
    }

    /**
     * 使用List实现类的构造方法
     * <p>
     * 通过ArrayList的构造方法来复制集合内容，同样是浅复制，在修改了源数据集合后，目标数据集合对应内容也发生了改变。
     * 在查阅资料的过程中，看到有人说这种方式 能实现深复制，其实这是不对的。对于某些特殊的元素，程序运行的结果形似深复制，
     * 其实还是浅复制。具体一会儿再说。
     */
    @Test
    public void constructorTest() {
        List<Person> destList = new ArrayList<>(srcList);
        printList(destList);
        srcList.get(0).setAge(100);
        printList(destList);

        // 基础类型可以实现深复制
        List<String> destList2 = new ArrayList<>(srcList2);
        printList(destList2);
        srcList2.set(0, "D");
        printList(destList2);
    }

    /**
     * 使用 System.arraycopy()
     * <p>
     * 这种方式虽然比较变态，但是起码证明了System.arraycopy()方法和clone()是不能对List集合进行深复制的。
     */
    @Test
    public void arraycopyTest() {
        Person[] srcPersonArr = srcList.toArray(new Person[0]);
        Person[] destPersonArr = new Person[srcPersonArr.length];
        System.arraycopy(srcPersonArr, 0, destPersonArr, 0, srcPersonArr.length);
        printArray(destPersonArr);
        srcPersonArr[0].setAge(100);
        printArray(destPersonArr);

        // 基础类型可以实现深复制
        String[] srcStringArr = srcList2.toArray(new String[0]);
        String[] destStringArr = srcStringArr.clone();
        printArray(destStringArr);
        srcStringArr[0] = "D";
        printArray(destStringArr);
    }

    /**
     * deepCopy 测试
     *
     * @throws IOException
     * @throws ClassNotFoundException
     */
    @Test
    public void deepCopyTest() throws IOException, ClassNotFoundException {
        List<Person> srcList = new ArrayList<>();
        srcList.add(p1);
        srcList.add(p2);
        srcList.add(p3);
        List<Person> destList = ListUtil.deepCopy(srcList);
        printList(destList);
        srcList.get(0).setAge(100);
        printList(destList);
    }

}
