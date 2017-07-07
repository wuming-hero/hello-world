import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("a", 1);
        map.put("d", 2);
        map.put("b", 3);
        System.out.println(map);
        Map<String, Object> map2 = new HashMap<>();
        map2.put("d", 4);
        map2.put("a", 2);
        map2.put("b", 4);
        map.putAll(map2);
        System.out.println(map);
    }

}
