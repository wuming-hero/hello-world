package com.wuming.base;

/**
 * Created by wuming on 2017/4/27.
 * 当超类对象引用变量引用子类对象时，被引用对象的类型而不是引用变量的类型决定了调用谁的成员方法，
 * 但是这个被调用的方法必须是在超类中定义过的，也就是说被子类覆盖的方法。
 * （但是如果强制把超类转换成子类的话，就可以调用子类中新添加而超类没有的方法了。）
 */
public class A {

    /**
     * 实际上这里涉及方法调用的优先问题
     * 优先级由高到低依次为：this.show(O)、super.show(O)、this.show((super)O)、super.show((super)O)
     *
     * @param args
     */
    public static void main(String[] args) {
        A a1 = new A();
        A a2 = new B();
        B b = new B();
        C c = new C();
        D d = new D();

        System.out.println(a1.show(b)); //① A and A
        System.out.println(a1.show(c)); //②  A and A
        System.out.println(a1.show(d)); //③  A and D

        /**
         * 比如④，a2.show(b)，a2是一个引用变量，类型为A，则this为a2，b是B的一个实例，
         * 于是它到类A里面找show(B obj)方法，没有找到，于是到A的super(超类)找，而A没有超类，
         * 因此转到第三优先级this.show((super)O)，this仍然是a2，这里O为B，(super)O即(super)B即A，
         * 因此它到类A里面找show(A obj)的方法，类A有这个方法，但是由于a2引用的是类B的一个对象，
         * B覆盖了A的show(A obj)方法，因此最终锁定到类B的show(A obj)，输出为"B and A”
         */
        System.out.println(a2.show(b)); //④ A and A(错)   B and A
        System.out.println(a2.show(c)); //⑤  A and A(错)   B and A
        System.out.println(a2.show(d)); //⑥  A and D

        System.out.println(b.show(b)); //⑦  B and B
        /**
         * 再比如⑧，b.show(c)，b是一个引用变量，类型为B，则this为b，c是C的一个实例，
         * 于是它到类B找show(C obj)方法，没有找到，转而到B的超类A里面找，A里面也没有，
         * 因此也转到第三优先级this.show((super)O)，this为b，O为C，(super)O即(super)C即B，
         * 因此它到B里面找show(B obj)方法，找到了，由于b引用的是类B的一个对象，
         * 因此直接锁定到类B的show(B obj)，输出为"B and B”
         */
        System.out.println(b.show(c)); //⑧ B and B
        System.out.println(b.show(d)); //⑨ A and D
    }

    public String show(D obj) {
        return ("A and D");
    }

    public String show(A obj) {
        return ("A and A");
    }
}

class B extends A {
    public String show(B obj) {
        return ("B and B");
    }

    public String show(A obj) {
        return ("B and A");
    }
}

class C extends B {
}

class D extends B {
}
