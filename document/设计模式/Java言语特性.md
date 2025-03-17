# 封装、抽象、继承、多态分别可以解决哪些编程问题？

## 1. 关于封装特性
封装也叫作信息隐藏或者数据访问保护。类通过暴露有限的访问接口，授权外部仅能通过类提供的方式来访问内部信息或者数据。
它需要编程语言提供权限访问控制语法来支持，例如Java中的private、protected、public关键字。

### 封装特性存在的意义：
1. 一方面是保护数据不被随意修改，提高代码的可维护性；
2. 另一方面是仅暴露有限的必要接口，提高类的易用性。

## 2. 关于抽象特性
封装主要讲如何隐藏信息、保护数据，那抽象就是讲如何隐藏方法的具体实现，让使用者只需要关心方法提供了哪些功能，不需要知道这些功能是如何实现的。
抽象可以通过接口类或者抽象类来实现，但也并不需要特殊的语法机制来支持。

### 抽象存在的意义：
1. 一方面是提高代码的可扩展性、维护性，修改实现不需要改变定义，减少代码的改动范围；
2. 另一方面，它也是处理复杂系统的有效手段，能有效地过滤掉不必要关注的信息。

## 3. 关于继承特性
继承是用来表示类之间的is-a关系，分为两种模式：单继承和多继承。单继承表示一个子类只继承一个父类，多继承表示一个子类可以继承多个父类。
为了实现继承这个特性，编程语言需要提供特殊的语法机制来支持。

### 继承存在的意义：
1. 继承主要是用来解决代码复用的问题。 
假如两个类有一些相同的属性和方法，我们就可以将这些相同的部分，抽取到父类中，让两个子类继承父类。这样，两个子类就可以重用父类中的代码，避免代码重复写多遍。不过，这一点也并不是继承所独有的，我们也可以通过其他方式来解决这个代码复用的问题，比如利用组合关系而不是继承关系。
2. 继承的概念很好理解，也很容易使用。不过，过度使用继承，继承层次过深过复杂，就会导致代码可读性、可维护性变差。
为了了解一个类的功能，我们不仅需要查看这个类的代码，还需要按照继承关系一层一层地往上查看“父类、父类的父类……”的代码。
还有，子类和父类高度耦合，修改父类的代码，会直接影响到子类。 所以，继承这个特性也是一个非常有争议的特性。很多人觉得继承是一种反模式。我们应该尽量少用，甚至不用
   
## 4. 关于多态特性
多态是指子类可以替换父类，在实际的代码运行过程中，调用子类的方法实现。多态这种特性也需要编程语言提供特殊的语法机制来实现，比如继承、接口类、duck-typing。
```java
public interface Iterator {
  boolean hasNext();
  String next();
  String remove();
}

public class Array implements Iterator {
  private String[] data;
  
  public boolean hasNext() { ... }
  public String next() { ... }
  public String remove() { ... }
  //...省略其他方法...
}

public class LinkedList implements Iterator {
  private LinkedListNode head;
  
  public boolean hasNext() { ... }
  public String next() { ... }
  public String remove() { ... }
  //...省略其他方法... 
}

public class Demo {
  private static void print(Iterator iterator) {
    while (iterator.hasNext()) {
      System.out.println(iterator.next());
    }
  }
  
  public static void main(String[] args) {
    Iterator arrayIterator = new Array();
    print(arrayIterator);
    
    Iterator linkedListIterator = new LinkedList();
    print(linkedListIterator);
  }
}
```
在这段代码中，Iterator是一个接口类，定义了一个可以遍历集合数据的迭代器。Array和LinkedList都实现了接口类Iterator。我们通过传递不同类型的实现类（Array、LinkedList）到print(Iterator iterator)函数中，支持动态的调用不同的next()、hasNext()实现。
具体点讲就是，当我们往print(Iterator iterator)函数传递Array类型的对象的时候，print(Iterator iterator)函数就会调用Array的next()、hasNext()的实现逻辑；当我们往print(Iterator iterator)函数传递LinkedList类型的对象的时候，print(Iterator iterator)函数就会调用LinkedList的next()、hasNext()的实现逻辑。

### 多态存在的意义：
1. 多态可以提高代码的扩展性和复用性，是很多设计模式、设计原则、编程技巧的代码实现基础。