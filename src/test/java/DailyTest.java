import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuming on 16/10/30.
 */
public class DailyTest {

    @Test
    public void test() {
        Object object = new Integer(100);
        String a = (String) object;
    }

    @Test
    public void test2() {
        BigDecimal b = new BigDecimal("1.333333");
        System.out.println(b.setScale(7));
    }

    @Test
    public void test3() throws CloneNotSupportedException {
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        List<Integer> list1 = new ArrayList<>();

    }

    public static void main(String[] args) {
        ArrayList<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        ArrayList<Integer> list1 = new ArrayList<>();
        list1 = (ArrayList<Integer>) list.clone();
        System.out.println(list1);
        list.set(0, 4);
        System.out.println(list);
        System.out.println(list1);
    }

}
