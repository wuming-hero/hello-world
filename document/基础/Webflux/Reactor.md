
# WebFlux简介
1. 传统的Spring MVC是基于Servlet API 的框架。
2. Spring WebFlux是一套全新的Reactive Web技术栈， 实现完全非阻塞，
* 支持Reactive Streams背压等特性，
* 并且运行环境不限于Servlet容器（Tomcat、Jetty、Undertow），如Netty等。

注意:
Spring WebFlux与Spring MVC可以共存，在SpringBoot中，Spring MVC优先级更高。
也就是说，共存时，使用的是Spring MVC。


# 核心类
## Reactor
Reactor 是一个支持响应式流（Reactive Streams）的轻量级JVM基础库，帮助应用高效，异步地传递消息（可以理解为优秀的语法糖）。
WebFlux默认的响应式库（Reactive Libraries）就是Reactor库。
* Reactor有Mono和Flux两个核心类，这两个类都实现了Publisher接口，提供丰富操作符。Flux对象实现发布者，返回 N 个元素；Mono实现发布者，返回 0 或者1个元素
* Flux 和 Mono 都是数据流的发布者，使用 Flux 和 Mono 都可以发出三种数据信号：元素值，错误信号，完成信号 错误信号和完成信号都代表终止信号，终止信号用于告诉订阅者数据流结束了。错误信号终止数据流同时把错误信息传递给订阅者

## 1.2 Mono和Flux
Reactor提供了2种返回类 型，Mono和Flux。
* Mono：0…1个数据
* Flux ：0…N个数据

## 1.3 三种信号特点
* 错误信号和完成信号都是终止信号，不能共存的
* 如果没有发送任何元素值，而是直接发送错误或者完成信号，表示是空数据流
* 如果没有错误信号，没有完成信号，表示是无限数据流


# WebFlux的特点
1. 异步和非阻塞
   支持了应用层的异步和底层的IO非阻塞。
   在应用层，利用Reactive Stream定义的异步组件，实现了异步调用。
   在底层，默认使用了Netty的NIO，实现了（同步）非阻塞。
   这不会使程序运行更快，但是可以用少量的线程承受住高负载，节省线程内存资源和减少线程上下文切换CPU耗时，进而提升吞吐量。

2. 函数式编程
   使用Lambda表达式和函数式接口来定义请求处理程序。
   WebFlux.Fn是一种轻量级函数式编程模型，其中函数用于路由和处理请求，契约设计为不可变。它是基于注释的编程模型的另一种选择，但在其他方面运行在相同的Reactive Core基础上。
   例如，RouterFunction相当于@RequestMapping注释。

3. 去servlet
   允许可以不基于servlet API。
   默认的Netty容器，不基于Servlet API。Servlet3.1支持了异步、非阻塞通信，因此，也可以选择使用Tomcat等容器，走Servlet API，但是，必须要使用WebFlux的框架代码。


WebFlux Demo https://blog.csdn.net/a1275302036/article/details/116492120
WebFlux响应式框架快速入门 https://blog.csdn.net/lic721/article/details/131019975