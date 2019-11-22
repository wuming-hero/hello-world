package com.wuming.stream;

import com.google.common.collect.ImmutableList;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * 为什么不在集合类实现这些操作，而是定义了全新的Stream API？
 * 1.集合类持有的所有元素都是存储在内存中的，非常巨大的集合类会占用大量的内存，而Stream的元素却是在访问的时候才被计算出来，
 * 这种“延迟计算”的特性有点类似Clojure的lazy-seq，占用内存很少
 * 2.集合类的迭代逻辑是调用者负责，通常是for循环，而Stream的迭代是隐含在对Stream的各种操作中，例如map()
 * <p>
 * 虽然大部分情况下stream是容器调用Collection.stream()方法得到的，但stream和collections有以下不同：
 * <p>
 * 1.无存储。stream不是一种数据结构，它只是某种数据源的一个视图，数据源可以是一个数组，Java容器或I/O channel等。
 * 2.为函数式编程而生。对stream的任何修改都不会修改背后的数据源，比如对stream执行过滤操作并不会删除被过滤的元素，而是会产生一个不包含被过滤元素的新stream。
 * 3.惰式执行。stream上的操作并不会立即执行，只有等到用户真正需要结果的时候才会执行。
 * 4.可消费性。stream只能被“消费”一次，一旦遍历过就会失效，就像容器的迭代器那样，想要再次遍历必须重新生成。
 * <p>
 * 对stream的操作分为为两类，中间操作(intermediate operations)和结束操作(terminal operations)，二者特点是：
 * <p>
 * 中间操作总是会惰式执行，调用中间操作只会生成一个标记了该操作的新stream，仅此而已。
 * 结束操作会触发实际计算，计算发生时会把所有中间操作积攒的操作以pipeline的方式执行，这样可以减少迭代次数。计算完成之后stream就会失效。
 * <p>
 * 中间操作 concat() distinct() filter() flatMap() limit() map() peek() skip() sorted() parallel() sequential() unordered()
 * 结束操作 allMatch() anyMatch() collect() count() findAny() findFirst() forEach() forEachOrdered() max() min() noneMatch() reduce() toArray()
 * <p>
 * 区分中间操作和结束操作最简单的方法，就是看方法的返回值，返回值为stream的大都是中间操作，否则是结束操作。
 *
 * @author wuming
 * Created on 2018/3/19 09:29
 */
public class StreamTest {

    /**
     * 创建一个流
     */
    @Test
    public void create() {
        // 1, 使用range方法给定一个范围来创建一个基本类型的流。
        IntStream intStream = IntStream.range(1, 100);

        // 2, 由数组创建流
        IntStream stream2 = Arrays.stream(new int[]{1, 3, 2});

        // 3, 直接给值来创建流
        Stream<String> stream = Stream.of("walker", "sun", "is", "the", "best");

        // 4, 由文件生成流
        try {
            Stream<String> lines = Files.lines(Paths.get("data.txt"), Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 5, 由函数生成流
        // 迭代
        Stream.iterate(0, n -> n + 2).limit(10).forEach(System.out::println);

        // 生成
        Stream.generate(Math::random).limit(5).forEach(System.out::println);
    }

    /********************中间操作开始**************************/

    /**
     * 返回一个只包含满足predicate条件元素的Stream
     */
    @Test
    public void filterTest() {
        // 保留长度等于3的字符串
        Stream<String> stream = Stream.of("I", "love", "you", "too");
        stream.filter(str -> str.length() == 3).forEach(str -> System.out.println(str));
    }

    /**
     * 就是对每个元素按照某种操作进行转换，转换前后Stream中元素的个数不会改变，但元素的类型取决于转换之后的类型。
     */
    @Test
    public void mapTest() {
        Stream<String> stream = Stream.of("I", "love", "you", "too");
        stream.map(str -> str.toUpperCase()).forEach(str -> System.out.println(str));
    }

    /**
     * 是对每个元素执行mapper指定的操作，并用所有mapper返回的Stream中的元素组成一个新的Stream作为最终返回结果。
     * 通俗的讲flatMap()的作用就相当于把原stream中的所有元素都"摊平"之后组成的Stream，转换前后元素的个数和类型都可能会改变
     */
    @Test
    public void flatMapTest() {
        Stream<List<Integer>> stream = Stream.of(Arrays.asList(1, 2), Arrays.asList(3, 4, 5));
        stream.flatMap(list -> list.stream()).forEach(System.out::println); // // 1,2,3,4,5
    }

    /**
     * peek方法生成一个包含原Stream的所有元素的新Stream，同时会提供一个消费函数（Consumer实例），
     * 新Stream每个元素被消费的时候都会执行给定的消费函数，并且消费函数优先执行
     */
    @Test
    public void peekTest() {
        Stream.of(1, 2, 3, 4, 5)
                .peek(integer -> System.out.println("accept:" + integer))
                .forEach(System.out::println);
        // 打印结果
        // accept:1
        //  1
        //  accept:2
        //  2
        //  accept:3
        //  3
        //  accept:4
        //  4
        //  accept:5
        //  5
    }

    /**
     * sorted方法将对原Stream进行排序，返回一个有序列的新Stream。
     * sorterd有两种变体sorted()，sorted(Comparator)，前者将默认使用Object.equals(Object)进行排序，
     * 而后者接受一个自定义排序规则函数(Comparator)，可按照意愿排序
     */
    @Test
    public void sortedTest() {
        Stream.of(5, 4, 3, 2, 1)
                .sorted()
                .forEach(System.out::println);
        // 打印结果
        // 1，2，3,4,5

        Stream.of(1, 2, 3, 4, 5)
                .sorted((x, y) -> (y - x))
                .forEach(System.out::println);
        // 打印结果
        // 5, 4, 3, 2, 1
    }

    /**
     * 中间操作 链式调用
     */
    @Test
    public void test() {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        Stream<Integer> stream = numbers.stream();
        stream.filter((x) -> {
            return x % 2 == 0;
        }).map((x) -> {
            return x * x;
        }).forEach(System.out::println);
    }

    /**
     * 过滤出偶数，并且不重复的元素。
     */
    @Test
    public void test2() {
        List<Integer> numbers = Arrays.asList(1, 2, 1, 3, 3, 2, 4);
        numbers.stream()
                .filter(i -> i % 2 == 0)
                .distinct()
                .forEach(System.out::println);

    }

    /********************中间操作结束**************************/

    /********************结束操作开始**************************/

    /**
     * count方法将返回Stream中元素的个数
     */
    @Test
    public void countTest() {
        long count = Stream.of(1, 2, 3, 4, 5).count();
        System.out.println("count:" + count);// 打印结果：count:5
    }

    /**
     * forEach方法前面已经用了好多次，其用于遍历Stream中的所元素，避免了使用for循环，让代码更简洁，逻辑更清晰
     * stream 是无序有
     */
    @Test
    public void forEachTest() {
        Stream.of(5, 4, 3, 2, 1).sorted().forEach(System.out::println);
        // 打印结果
        // 1，2，3,4,5
    }

    /**
     * forEachOrdered方法与forEach类似，都是遍历Stream中的所有元素，
     * 不同的是，如果该Stream预先设定了顺序，会按照预先设定的顺序执行（Stream是无序的），默认为元素插入的顺序
     */
    @Test
    public void forEachOrderedTest() {
        Stream.of(5, 2, 1, 4, 3).forEachOrdered(System.out::println);
        // 打印结果
        // 5, 2, 1, 4, 3
    }

    /**
     * max方法根据指定的Comparator，返回一个Optional，该Optional中的value值就是Stream中最大的元素
     * <p>
     * 注：原Stream根据比较器Comparator，进行排序(升序或者是降序)，
     * 所谓的最大值就是从新进行排序的，max就是取重新排序后的最后一个值，而min取排序后的第一个值。
     */
    @Test
    public void maxTest() {
        Optional<Integer> max = Stream.of(1, 2, 3, 4, 5).max((o1, o2) -> o2 - o1);
        System.out.println("max:" + max.get());// 打印结果：max:1
    }

    /**
     * min方法根据指定的Comparator，返回一个Optional，该Optional中的value值就是Stream中最小的元素。
     */
    @Test
    public void minTest() {
        Optional<Integer> max = Stream.of(1, 2, 3, 4, 5).max((o1, o2) -> o1 - o2);
        System.out.println("max:" + max.get());// 打印结果：min:5
    }

    /**
     * allMatch操作用于判断Stream中的元素是否全部满足指定条件。如果全部满足条件返回true，否则返回false
     */
    @Test
    public void allMatchTest() {
        boolean allMatch = Stream.of(1, 2, 3, 4)
                .allMatch(integer -> integer > 0);
        System.out.println("allMatch: " + allMatch); // 打印结果：allMatch: true
    }

    /**
     * anyMatch操作用于判断Stream中的是否有满足指定条件的元素。如果最少有一个满足条件返回true，否则返回false
     */
    @Test
    public void anyMatchTest() {
        boolean anyMatch = Stream.of(1, 2, 3, 4)
                .anyMatch(integer -> integer > 3);
        System.out.println("anyMatch: " + anyMatch); // 打印结果：anyMatch: true
    }

    /**
     * findAny操作用于获取含有Stream中的某个元素的Optional，
     * 如果Stream为空，则返回一个空的Optional。由于此操作的行动是不确定的，其会自由的选择Stream中的任何元素。
     * 在并行操作中，在同一个Stream中多次调用，可能会不同的结果。在串行调用时，Debug了几次，发现每次都是获取的第一个元素，
     * 个人感觉在串行调用时，应该默认的是获取第一个元素。
     */
    @Test
    public void findAnyTest() {
        Optional<Integer> any = Stream.of(1, 2, 3, 4).findAny();
    }

    /**
     * findFirst操作用于获取含有Stream中的第一个元素的Optional，如果Stream为空，则返回一个空的Optional。
     * 若Stream并未排序，可能返回含有Stream中任意元素的Optional
     */
    @Test
    public void findFirstTest() {
        Optional<Integer> any = Stream.of(1, 2, 3, 4).findFirst();
    }

    /**
     * noneMatch方法将判断Stream中的所有元素是否满足指定的条件，
     * 如果所有元素都不满足条件，返回true；否则，返回false
     */
    @Test
    public void noneMatchTest() {
        boolean noneMatch = Stream.of(1, 2, 3, 4, 5).noneMatch(integer -> integer > 10);
        System.out.println("noneMatch:" + noneMatch); // 打印结果 noneMatch:true

        boolean noneMatch_ = Stream.of(1, 2, 3, 4, 5).noneMatch(integer -> integer < 3);
        System.out.println("noneMatch_:" + noneMatch_); // 打印结果 noneMatch_:false
    }

    /********************中间操作结束**************************/

    /**
     * 要理解“延迟计算”，不妨创建一个无穷大小的Stream。如果要表示自然数集合，显然用集合类是不可能实现的，因为自然数有无穷多个。但是Stream可以做到。
     * 对这个Stream做任何map()、filter()等操作都是完全可以的，这说明Stream API对Stream进行转换并生成一个新的Stream并非实时计算，而是做了延迟计算。
     * <p>
     * 当然，对这个无穷的Stream不能直接调用forEach()，这样会无限打印下去。但是我们可以利用limit()变换，把这个无穷Stream变换为有限的Stream
     */
    @Test
    public void naturalTest() {
        Stream<Long> natural = Stream.generate(new NaturalSupplier());
        natural.map((x) -> {
            return x * x;
        }).limit(10).forEach(System.out::println);
    }

    /**
     * 用Stream表示Fibonacci数列，其接口比任何其他接口定义都要来得简单灵活并且高效
     * 生成斐波那契数列，完全可以用一个无穷流表示（受限Java的long型大小，可以改为BigInteger）
     */
    @Test
    public void FibonacciTest() {
        Stream<Long> fibonacci = Stream.generate(new FibonacciSupplier());
        fibonacci.limit(30).forEach(System.out::println);
    }

    /**
     * 如果想取得数列的前10项，用limit(10)，如果想取得数列的第20~30项
     * 最后通过collect()方法把Stream变为List。该List存储的所有元素就已经是计算出的确定的元素了
     */
    @Test
    public void FibonacciTest2() {
        Stream<Long> fibonacci = Stream.generate(new FibonacciSupplier());
        List<Long> list = fibonacci.skip(20).limit(10).collect(Collectors.toList());
        System.out.println("list: " + list);
    }

    /**
     * 归约
     * 将一个流中的元素反复结合运算得到一个值
     * <p>
     * 使用reduce相对于使用逐步迭代的好处在于，外部迭代改成了内部迭代，在需要实现并行执行的操时作变得简单
     */
    @Test
    public void sumTest() {
        // 使用循环求和
        List<Integer> numbers = ImmutableList.of(1, 2, 3, 4, 5);
        int sum1 = 0;
        for (int x : numbers) {
            sum1 += x;
        }
        System.out.println("sum1: " + sum1);

        // 使用流的stream API求和
        int sum2 = numbers.stream().reduce(0, (a, b) -> a + b);
        System.out.println("sum2: " + sum2);

        // 求最大值
        Optional<Integer> max = numbers.stream().reduce(Integer::max);
        System.out.println("max: " + max.get());

        // 求最小值
        Optional<Integer> min = numbers.stream().reduce(Integer::min);
        System.out.println("min: " + min.get());
    }

    @Test
    public void switchTest() {
        Stream<String> stream = Stream.of("a", "b", "c");
        // 1. Array
        String[] strArray1 = stream.toArray(String[]::new);
        String[] strArray2 = stream.toArray(strArr -> new String[strArray1.length]);

        // 2. Collection
        List<String> list1 = stream.collect(Collectors.toList());
        List<String> list2 = stream.collect(Collectors.toCollection(ArrayList::new));
        Set set1 = stream.collect(Collectors.toSet());
        Stack stack1 = stream.collect(Collectors.toCollection(Stack::new));
        // 3. String
        String str = stream.collect(Collectors.joining()).toString();
    }

}
