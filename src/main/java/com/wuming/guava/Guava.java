package com.wuming.guava;

import com.google.common.base.*;
import com.google.common.collect.*;
import com.google.common.primitives.Ints;
import org.junit.Test;

import java.io.InputStream;
import java.net.URL;
import java.util.*;

import static org.junit.Assert.*;

/**
 * Created by wuming on 2017/6/10.
 */
public class Guava {

    /**
     * 初始化 List or map
     */
    @Test
    public void test() {
        Map<String, Object> map1 = new HashMap();
        map1.put("a", 1);
        map1.put("b", 2);

        Map<String, Object> map2 = new HashMap<String, Object>() {{
            put("a", 1);
            put("b", 2);
        }};

        ImmutableMap<String, Object> map3 = ImmutableMap.of("a", 1, "b", 2);
        System.out.println(map3);

//        so does the list
        List<String> list1 = new ArrayList<>();
        list1.add("a");
        list1.add("b");

        List<String> list2 = new ArrayList<String>() {{
            add("a");
            add("b");
        }};

        ImmutableList<String> list3 = ImmutableList.of("a", "b");
        System.out.println(list3);

    }

    /**
     * map 操作
     */
    @Test
    public void mapCompareTest() {
        Map<String, String> leftMap = ImmutableMap.of("A", "1", "B", "2", "C", "3");
        Map<String, String> rightMap = ImmutableMap.of("A", "1", "B", "3", "C", "2", "D", "3");
        MapDifference differenceMap = Maps.difference(leftMap, rightMap);

        Boolean isEqual = differenceMap.areEqual();
        System.out.println("leftMap and rightMap are equal: " + isEqual);

        // 两个 map 中 key 都存在，但 value 不相同的 map 集合
        Map<String, MapDifference.ValueDifference<String>> entriesDiffering = differenceMap.entriesDiffering();
        System.out.println("two maps diff: " + entriesDiffering);

        for (Map.Entry<String, MapDifference.ValueDifference<String>> entry : entriesDiffering.entrySet()) {
            System.out.println("key in both map: " + entry.getKey() + "====value diff: " + entry.getValue() + "====value in left map: " + entry.getValue().leftValue() + "====value in right map: " + entry.getValue().rightValue());
        }

        // leftMap 与 rightMap key 不相同的map集合
        Map entriesOnlyOnLeft = differenceMap.entriesOnlyOnLeft();
        System.out.println("leftMap diff rightMap: " + entriesOnlyOnLeft);

        // rightMap 与 leftMap key 不相同的map集合
        Map entriesOnlyOnRight = differenceMap.entriesOnlyOnRight();
        System.out.println("rightMap diff leftMap: " + entriesOnlyOnRight);

        // leftMap 与 rightMap key 和 value 相同的map集合
        Map entriesInCommon = differenceMap.entriesInCommon();
        System.out.println("leftMap and rightMap are common: " + entriesInCommon);
    }

    /**
     * 使用 GUAVA 的 MapDifference 进行 map 比较
     */
    @Test
    public void mapCompareTest2() {
        Map<String, Object> mapA = ImmutableMap.of("A", "1", "B", "2");
        Map<String, Object> mapB = ImmutableMap.of("A", "3", "C", "4");
        compareMap(mapA, mapB);
    }

    private boolean compareMap(Map<String, Object> leftMap, Map<String, Object> rightMap) {
        MapDifference<String, Object> difference = Maps.difference(leftMap, rightMap);
        //获取所有不同点
        Map<String, MapDifference.ValueDifference<Object>> differenceMap = difference.entriesDiffering();
        Iterator diffIterator = differenceMap.entrySet().iterator();
        if (diffIterator.hasNext()) {
            Map.Entry entry = (Map.Entry) diffIterator.next();

            MapDifference.ValueDifference<Object> valueDifference = (MapDifference.ValueDifference<Object>) entry.getValue();
            System.out.println("left: " + valueDifference.leftValue());
            System.out.println("right: " + valueDifference.rightValue());

            //处理结果是否为map,则递归执行比较规则
            if (valueDifference.leftValue() instanceof Map && valueDifference.rightValue() instanceof Map) {
                boolean equal = compareMap((Map<String, Object>) valueDifference.leftValue(), (Map<String, Object>) valueDifference.rightValue());
                if (!equal) {
                    return false;
                }
            }
            //如果处理结果为list，则通过list方式处理  - 若list中值相同，但是顺序不同，则认为两个list相同
            if (valueDifference.leftValue() instanceof List && valueDifference.rightValue() instanceof List) {
                boolean equal = ((List) valueDifference.leftValue()).containsAll((List) valueDifference.rightValue());
                if (!equal) {
                    return false;
                }
            }
            //如果处理最终结果为字符串,则停止比较
            if (valueDifference.leftValue() instanceof String && valueDifference.rightValue() instanceof String) {
                return false;
            }
        }
        //若B中有A中不存在的值，则认为不同
        Map<String, Object> onlyOnRightMap = difference.entriesOnlyOnRight();
        if (onlyOnRightMap != null && !onlyOnRightMap.isEmpty()) {
            return false;
        }

        return true;
    }

    /**
     * 字符串特殊处理
     */
    @Test
    public void intsTest() {
        int a = 1;
        int b = 2;
        int ret = Ints.compare(a, b);
        System.out.println(ret);

        assertEquals("89983", CharMatcher.DIGIT.retainFrom("some text 89983 and more"));
        assertEquals("some text  and more", CharMatcher.DIGIT.removeFrom("some text 89983 and more"));

        int[] array = {1, 2, 3, 4, 5};
        int[] array2 = {7, 8};
        int c = 4;
        boolean contains = Ints.contains(array, c);
        int indexOf = Ints.indexOf(array, c);
        int max = Ints.max(array);
        int min = Ints.min(array);
        int[] concat = Ints.concat(array, array2);

    }

    /**
     * 数组操作
     */
    @Test
    public void joinerTest() {
        int[] numbers = {1, 2, 3, 4, 5};
        String numbersAsString = Joiner.on(";").join(Ints.asList(numbers));
        System.out.println(numbersAsString);
        String numbersAsStringDirectly = Ints.join(";", numbers);
        System.out.println(numbersAsStringDirectly);

        Iterable split = Splitter.on(";").split(numbersAsString);
        System.out.println(split);

        String testString = "foo , what,,,more,";
        Iterable<String> split1 = Splitter.on(",").omitEmptyStrings().trimResults().split(testString);
        System.out.println(split1);
    }

    /**
     * set 交集、补集、并集
     */
    @Test
    public void setTest() {
        ImmutableSet<Integer> setA = ImmutableSet.of(1, 2, 3);
        ImmutableSet<Integer> setB = ImmutableSet.of(3, 4, 5);

        System.out.println(setA);

        Set<Integer> union = Sets.union(setA, setB);
        System.out.println("union:" + union.size());
        for (Integer item : union)
            System.out.println(item);

        Set<Integer> difference = Sets.difference(setA, setB);
        System.out.println("difference:");
        for (Integer item : difference)
            System.out.println(item);

        Set<Integer> intersection = Sets.intersection(setA, setB);
        System.out.println("intersection:");
        Set<Integer> set = new HashSet<>();
        set.add(10);
        set.add(11);
        intersection.addAll(set);
        for (Integer item : intersection)
            System.out.println(item);
    }

    @Test
    public void argsCheckTest() {
        Integer count = 0;
        // 检查参数是否为 null，为 null 的话抛空指针异常，否则返回原值
        count = Preconditions.checkNotNull(count);
        if (count <= 0) {
            throw new IllegalArgumentException("must be positive: " + count);
        }

        Preconditions.checkArgument(count > 0, "must be positive: " + count);


    }

    /**
     * 针对集合中只有一个元素的情况:
     * 这个主要是用来替换Set.iterator.next()或 List.get(0), 而且在测试中使用非常方便, 如果出现0个或者2+则直接抛出异常
     */
    @Test
    public void iterableTest() {
        List<String> list = new ArrayList<>();
        list.add("a");
//        list.add("b"); // 会抛异常
        String value = Iterables.getOnlyElement(list);
        System.out.println("value " + value);

        List<String> listA = Arrays.asList("a", "b", "c");
        List<String> listB = Arrays.asList("A", "B", "C");

        Iterable<String> iterable = Iterables.concat(listA, listB);
        Iterator iterator = iterable.iterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }

        for (String item : iterable) {
            System.out.println(item);
        }

    }

    /**
     * 字符串操作
     */
    @Test
    public void StringsTest() {
        assertEquals("test", Strings.emptyToNull("test"));
        assertEquals(" ", Strings.emptyToNull(" "));
        assertNull(Strings.emptyToNull(""));
        assertNull(Strings.emptyToNull(null));

        assertFalse(Strings.isNullOrEmpty("test"));
        assertFalse(Strings.isNullOrEmpty(" "));
        assertTrue(Strings.isNullOrEmpty(""));
        assertTrue(Strings.isNullOrEmpty(null));

        assertEquals("test", Strings.nullToEmpty("test"));
        assertEquals(" ", Strings.nullToEmpty(" "));
        assertEquals("", Strings.nullToEmpty(""));
        assertEquals("", Strings.nullToEmpty(null));

        assertEquals("Ralph_____", Strings.padEnd("Ralph", 10, '_'));
        assertEquals("Bob_______", Strings.padEnd("Bob", 10, '_'));

        assertEquals("_____Ralph", Strings.padStart("Ralph", 10, '_'));
        assertEquals("_______Bob", Strings.padStart("Bob", 10, '_'));

        assertEquals("xyxyxyxyxy", Strings.repeat("xy", 5));
    }

    /**
     * Throwables 的用法(将检查异常转换成未检查异常):
     */
    @Test
    public void exceptionTest() {
        try {
            URL url = new URL("http://ociweb.com");
            final InputStream in = url.openStream();
            // read from the input stream
            in.close();
        } catch (Throwable t) {
            throw Throwables.propagate(t);
        }
    }

    @Test
    public void functionTest() {
        Function<String, Integer> strlen = new Function<String, Integer>() {
            public Integer apply(String from) {
                Preconditions.checkNotNull(from);
                return from.length();
            }
        };

        List<String> from = Lists.newArrayList("abc", "defg", "hijkl");
        List<Integer> to = Lists.transform(from, strlen);
        for (int i = 0; i < from.size(); i++) {
            System.out.printf("%s has length %d\n", from.get(i), to.get(i));
        }
    }

    /**
     * 这种转换是在访问元素的时候才进行, 下面的例子可以说明
     */
    @Test
    public void functionTest2() {
        Function<String, Boolean> isPalindrome = new Function<String, Boolean>() {
            public Boolean apply(String from) {
                Preconditions.checkNotNull(from);
                return new StringBuilder(from).reverse().toString().equals(from);
            }
        };

        List<String> from = Lists.newArrayList("rotor", "radar", "hannah", "level", "botox");
        List<Boolean> to = Lists.transform(from, isPalindrome);
        for (int i = 0; i < from.size(); i++) {
            System.out.printf("%s is %s a palindrome\n", from.get(i), to.get(i) ? " " : " NOT ");
        }

        // changes in the "from" list are reflected in the "to" list
        System.out.printf("\nnow replace hannah with megan...\n\n");
        from.set(2, "megan");

        for (int i = 0; i < from.size(); i++) {
            System.out.printf("%s is %s a palindrome\n", from.get(i), to.get(i) ? " " : " NOT ");
        }
    }


}
