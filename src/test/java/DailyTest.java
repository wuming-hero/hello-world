import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Test
    public void listTest() {
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

    @Test
    public void mapTest(){
        Map<String, Object> map = new HashMap<>(1);
        map.put("a", 1);
        map.put("b", 2);
        System.out.println(map.size());
    }

}
