package com.wuming.guava;

import com.google.common.collect.*;
import org.junit.Test;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by wuming on 2017/6/14.
 * Multimap 也支持一系列强大的视图功能：
 * 1.asMap() 把自身Multimap<K, V>映射成Map<K, Collection<V>>视图。这个Map视图支持remove和修改操作，但是不支持put和putAll。
 * 严格地来讲，当你希望传入参数是不存在的key，而且你希望返回的是null而不是一个空的可修改的集合的时候就可以调用asMap().get(key)。
 * （你可以强制转型asMap().get(key)的结果类型－对SetMultimap的结果转成Set，对ListMultimap的结果转成List型－但是直接把ListMultimap转成Map<K, List<V>>是不行的。）
 * 2.entries() 视图是把Multimap里所有的键值对以Collection<Map.Entry<K, V>>的形式展现。
 * 3.keySet() 视图是把Multimap的键集合作为视图
 * 4.keys() 视图返回的是个Multiset，这个Multiset是以不重复的键对应的个数作为视图。这个Multiset可以通过支持移除操作而不是添加操作来修改Multimap。
 * 5.values() 视图能把Multimap里的所有值“平展”成一个Collection<V>。这个操作和Iterables.concat(multimap.asMap().values())很相似，只是它返回的是一个完整的Collection。
 * <p>
 * 尽管Multimap的实现用到了Map，但Multimap<K, V>不是Map<K, Collection<V>>。因为两者有明显区别：
 * 1.Multimap.get(key) 一定返回一个非null的集合。但这不表示Multimap使用了内存来关联这些键，相反，返回的集合只是个允许添加元素的视图。
 * 2.如果你喜欢像Map那样当不存在键的时候要返回null，而不是Multimap那样返回空集合的话，可以用asMap()返回的视图来得到Map<K, Collection<V>>。
 * （这种情况下，你得把返回的Collection<V>强转型为List或Set）。
 * 3.Multimap.containsKey(key) 只有在这个键存在的时候才返回true。
 * 4.Multimap.entries() 返回的是Multimap所有的键值对。但是如果需要key-collection的键值对，那就得用asMap().entries()。
 * 5.Multimap.size() 返回的是entries的数量，而不是不重复键的数量。如果要得到不重复键的数目就得用Multimap.keySet().size()。
 */
public class MapTest {

    /**
     * 这里有一点你可能会疑惑，就是为何get方法返回的是一个collection而不是list，这是因为前者会更加有用。
     * 如果你需要基于multimap直接操作list或者set，那么可以使用在定义类型的时候使用子类名称：ListMultimap，SetMultimap和SortedSetMultimap。
     * 例如：
     * ListMultimap<String,String> myMultimap = ArrayListMultimap.create();
     * List<string> myValues = myMultimap.get("myKey");  // Returns a List, not a Collection
     */
    @Test
    public void multiMapTest() {
        Multimap<String, String> multimap = ArrayListMultimap.create();
        // Adding some key/value
        multimap.put("Fruits", "Banana");
        multimap.put("Fruits", "Apple");
        multimap.put("Fruits", "Pear");
        multimap.put("Fruits", "Pear");
        multimap.put("Vegetables", "Carrot");

        // Getting the size
        System.out.println(multimap.size()); // 5
        System.out.println(multimap.values().size()); // 5
        System.out.println(multimap.keySet().size()); // 2

        // Getting values
        Collection<String> fruits = multimap.get("Fruits");
        System.out.println(fruits); //  [Banana, Apple, Pear, Pear]
        System.out.println(ImmutableSet.copyOf(fruits));// [Banana, Apple, Pear]

        Collection<String> vegetables = multimap.get("Vegetables");
        System.out.println(vegetables); // [Carrot]

        // Iterating over entire Multimap
        for (String value : multimap.values()) {
            System.out.println(value);
        }

        for (Map.Entry<String, String> entry : multimap.entries()) {
            System.out.println(entry.getKey() + "----" + entry.getValue());
        }

        // Removing a single value
        multimap.remove("Fruits", "Pear");
        System.out.println(multimap.get("Fruits")); // [Banana, Apple, Pear]

        // Remove all values for a key
        multimap.removeAll("Fruits");
        System.out.println(multimap.get("Fruits")); // [] (Empty Collection!)

    }

    /**
     * multiMap 另一种使用场景
     * 比如有一个文章数据的map,如果要按照type分组生成一个List
     */
    @Test
    public void test() {
        Map<String, String> mapA = ImmutableMap.of("type", "blog", "id", "292", "author", "john");
        Map<String, String> mapB = ImmutableMap.of("type", "blog", "id", "295", "author", "green");
        Map<String, String> mapC = ImmutableMap.of("type", "novel", "id", "293", "author", "smith");
        List<Map<String, String>> listOfMaps = ImmutableList.of(mapA, mapB, mapC);

        Multimap<String, Map<String, String>> partitionedMap = Multimaps.index(listOfMaps, item -> item.get("type"));
        // {blog=[{type=blog, id=292, author=john}, {type=blog, id=295, author=green}], novel=[{type=novel, id=293, author=smith}]}
        System.out.println(partitionedMap);
    }

    /**
     * 用来统计多值出现的频率
     */
    @Test
    public void multiMapTest2() {
        Multimap<Integer, String> siblings = ArrayListMultimap.create();
        siblings.put(0, "Kenneth");
        siblings.put(1, "Joe");
        siblings.put(2, "John");
        siblings.put(3, "Jerry");
        siblings.put(3, "Jay");
        siblings.put(5, "Janet");

        for (int i = 0; i < 6; i++) {
            int freq = siblings.get(i).size();
            System.out.printf("%d siblings frequency %d\n", i, freq);
        }
    }

    /**
     * 借助 BiMap 实现 map key-value 的反转
     */
    @Test
    public void biMapTest() {
        // 普通代码实现
        Map<Integer, String> NUMBER_TO_NAME = Maps.newHashMap();
        Map<String, Integer> NAME_TO_NUMBER = Maps.newHashMap();
        ;
        NUMBER_TO_NAME.put(1, "Hydrogen");
        NUMBER_TO_NAME.put(2, "Helium");
        NUMBER_TO_NAME.put(3, "Lithium");

        /* reverse the map programatically so the actual mapping is not repeated */
        for (Integer number : NUMBER_TO_NAME.keySet()) {
            NAME_TO_NUMBER.put(NUMBER_TO_NAME.get(number), number);
        }

        System.out.println(NUMBER_TO_NAME.get(1));
        System.out.println(NAME_TO_NUMBER.get("Hydrogen"));

        // 借助 BiMap 实现
        BiMap<Integer, String> NUMBER_TO_NAME_BIMAP = HashBiMap.create();
        NUMBER_TO_NAME_BIMAP.put(1, "Hydrogen");
        NUMBER_TO_NAME_BIMAP.put(2, "Helium");
        NUMBER_TO_NAME_BIMAP.put(3, "Lithium");

        System.out.println(NUMBER_TO_NAME_BIMAP.get(1));
        System.out.println(NUMBER_TO_NAME_BIMAP.inverse().get("Hydrogen"));

    }


}
