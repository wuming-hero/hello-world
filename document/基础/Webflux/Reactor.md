
# WebFlux简介
1. 传统的Spring MVC是基于Servlet API 的框架。
2. Spring WebFlux是一套全新的Reactive Web技术栈， 实现完全非阻塞，
* 支持Reactive Streams背压等特性， 并且运行环境不限于Servlet容器（Tomcat、Jetty、Undertow），如Netty等。

注意:
Spring WebFlux与Spring MVC可以共存，在SpringBoot中，Spring MVC优先级更高。
也就是说，共存时，使用的是Spring MVC。


# 核心类
## Reactor
Reactor 是一个支持响应式流（Reactive Streams）的轻量级JVM基础库，帮助应用高效，异步地传递消息（可以理解为优秀的语法糖）。
WebFlux 默认的响应式库（Reactive Libraries）就是Reactor库。
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
   **这不会使程序运行更快，但是可以用少量的线程承受住高负载，节省线程内存资源和减少线程上下文切换CPU耗时，进而提升吞吐量。** 

2. 函数式编程
   使用Lambda表达式和函数式接口来定义请求处理程序。
   WebFlux.Fn是一种轻量级函数式编程模型，其中函数用于路由和处理请求，契约设计为不可变。它是基于注释的编程模型的另一种选择，但在其他方面运行在相同的Reactive Core基础上。
   例如，RouterFunction相当于@RequestMapping注释。

3. 去servlet
   允许可以不基于servlet API。
   默认的Netty容器，不基于Servlet API。Servlet3.1支持了异步、非阻塞通信，因此，也可以选择使用Tomcat等容器，走Servlet API，但是，必须要使用WebFlux的框架代码。


# WebFlux 和 servlet 3.1 对比
虽然 Servlet 3.1 引入了对非阻塞 I/O 的支持，但‌它并未彻底解决传统 Servlet 模型中的阻塞问题‌。Spring WebFlux 的出现正是为了弥补这一不足，提供一个‌完全异步、非阻塞、响应式‌的 Web 开发范式。

## 为什么有了 Servlet 3.1 还需要 Spring WebFlux？

### Servlet 3.1 的非阻塞是“部分非阻塞”‌
尽管 Servlet 3.1 提供了 AsyncContext 和 ReadListener/WriteListener 等 API 实现非阻塞读写，但其核心契约（如 getParameter()、getPart()、Filter）仍然是‌同步阻塞的‌
。这意味着即使底层 I/O 非阻塞，业务逻辑仍可能阻塞线程。

### 线程模型未根本改变‌
在 Servlet 容器（如 Tomcat）中，每个请求默认绑定一个线程。即使使用异步 Servlet，若业务逻辑涉及数据库查询、HTTP 调用等阻塞操作，仍会占用线程，无法实现高并发下的资源高效利用
。

### 缺乏响应式编程原生支持‌
Servlet 是命令式、同步导向的 API，而 WebFlux 基于 Reactive Streams 规范‌（通过 Reactor 实现），支持‌背压（Backpressure）‌、‌函数式编程‌和‌事件驱动架构‌，更适合**高并发、IO 密集型场景**
。

### 运行环境更灵活‌
WebFlux 不依赖 Servlet 容器，可直接运行在 Netty、Undertow 等原生非阻塞服务器上，进一步提升性能和可扩展性
。
维度	Servlet 3.1	Spring WebFlux
‌编程模型‌	命令式、同步为主（虽支持异步）	响应式、完全异步非阻塞
‌线程模型‌	请求-线程绑定（受限于线程池大小）	事件循环 + 少量线程，支持高并发
‌阻塞问题‌	Filter、getParameter 等仍阻塞	全栈非阻塞，无阻塞契约
‌运行容器‌	依赖 Servlet 容器（Tomcat/Jetty 等，需 3.1+）	支持 Netty、Undertow、Servlet 3.1+ 容器
‌编程风格‌	注解 + 回调（AsyncContext）	注解 + ‌函数式端点‌（RouterFunction）
‌适用场景‌	传统 Web 应用、低并发服务	‌高并发、IO 密集型‌（如网关、微服务）
‌学习曲线‌	较平缓	较陡峭（需理解响应式流、背压等）
‌调试复杂度‌	简单	较复杂（异步链、线程切换）

WebFlux Demo https://blog.csdn.net/a1275302036/article/details/116492120
WebFlux响应式框架快速入门 https://blog.csdn.net/lic721/article/details/131019975