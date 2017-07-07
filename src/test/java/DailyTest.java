import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by wuming on 16/10/30.
 * update master
 */
public class DailyTest {

    /**
     * list.clone() 方法
     */
    @Test
    public void listTest() {
        ArrayList<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        ArrayList<Integer> list1 = (ArrayList<Integer>) list.clone();
        System.out.println(list1);
        list.set(0, 4);
        System.out.println(list);
        System.out.println(list1);
    }

    /**
     * key的hash 值总是不出现预想的那样
     * HashMap 和LinkedHashMap 总是一样的顺序
     */
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
