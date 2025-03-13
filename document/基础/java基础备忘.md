## 语法糖
语法糖（Syntactic sugar） 代指的是编程语言为了方便程序员开发程序而设计的一种特殊语法，这种语法对编程语言的功能并没有影响。
实现相同的功能，基于语法糖写出来的代码往往更简单简洁且更易阅读。
举个例子，Java 中的 for-each 就是一个常用的语法糖，其原理其实就是基于普通的 for 循环和迭代器。

```java
String[] strs = {"JavaGuide", "公众号：JavaGuide", "博客：https://javaguide.cn/"};
for (String s : strs) {
    System.out.println(s);
}
```

不过，JVM 其实并不能识别语法糖，Java 语法糖要想被正确执行，需要先通过编译器进行解糖，也就是在程序编译阶段将其转换成 JVM 认识的基本语法。
这也侧面说明，`Java 中真正支持语法糖的是 Java 编译器而不是 JVM` 。
如果你去看`com.sun.tools.javac.main.JavaCompiler`的源码，你会发现在`compile()`中有一个步骤就是调用`desugar()`，这个方法就是负责解语法糖的实现的。

### Java 中有哪些常见的语法糖？
Java 中最常用的语法糖主要有泛型、自动拆装箱、变长参数、枚举、内部类、增强 for 循环、try-with-resources 语法、lambda 表达式等。

java 语法糖 https://javaguide.cn/java/basis/syntactic-sugar.html
