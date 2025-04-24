# Dubbo 

## 什么是Dubbo
### Dubbo 提供了六大核心能力:

1. 面向接口代理的高性能 RPC 调用。
2. 智能容错和负载均衡。
3. 服务自动注册和发现。
4. 高度可扩展能力。 
5. 运行期流量调度。 
6. 可视化的服务治理与运维。

### Dubbo 帮助我们解决了什么问题呢？
1. 负载均衡：同一个服务部署在不同的机器时该调用哪一台机器上的服务。
2. 服务调用链路生成：随着系统的发展，服务越来越多，服务间依赖关系变得错踪复杂，甚至分不清哪个应用要在哪个应用之前启动，架构师都不能完整的描述应用的架构关系。Dubbo 可以为我们解决服务之间互相是如何调用的。
3. 服务访问压力以及时长统计、资源调度和治理：基于访问压力实时管理集群容量，提高集群利用率。

## Dubbo的原理

### dubbo框架设计
下图是 Dubbo 的整体设计，从下至上分为十层，各层均为单向依赖。
左边淡蓝背景的为服务消费方使用的接口，右边淡绿色背景的为服务提供方使用的接口，位于中轴线上的为双方都用到的接口。

![图片3](../../src/main/resources/static/image/dubbo/dubbo_frame.png)

* config 配置层：Dubbo 相关的配置。支持代码配置，同时也支持基于 Spring 来做配置，以 ServiceConfig, ReferenceConfig 为中心
* proxy 服务代理层：调用远程方法像调用本地的方法一样简单的一个关键，真实调用过程依赖代理类，以 ServiceProxy 为中心。
* registry 注册中心层：封装服务地址的注册与发现。
* cluster 路由层：封装多个提供者的路由及负载均衡，并桥接注册中心，以 Invoker 为中心。
* monitor 监控层：RPC 调用次数和调用时间监控，以 Statistics 为中心。
* protocol 远程调用层：封装 RPC 调用，以 Invocation, Result 为中心。
* exchange 信息交换层：封装请求响应模式，同步转异步，以 Request, Response 为中心。
* transport 网络传输层：抽象 mina 和 netty 为统一接口，以 Message 为中心。
* serialize 数据序列化层：对需要在网络传输的数据进行序列化。


### Dubbo 的 SPI 机制了解么？ 如何扩展 Dubbo 中的默认实现？

SPI（Service Provider Interface） 机制被大量用在开源项目中，它可以帮助我们动态寻找服务/功能（比如负载均衡策略）的实现。

SPI 的具体原理是这样的：
我们将接口的实现类放在配置文件中，我们在程序运行过程中读取配置文件，通过反射加载实现类。
这样，我们可以在运行的时候，动态替换接口的实现类。和 IoC 的解耦思想是类似的。

Java 本身就提供了 SPI 机制的实现。不过，Dubbo 没有直接用，而是对 Java 原生的 SPI 机制进行了增强，以便更好满足自己的需求。

#### 那我们如何扩展 Dubbo 中的默认实现呢？
比如说我们想要实现自己的负载均衡策略。

1. 第1步：我们创建对应的实现类 XxxLoadBalance 实现 LoadBalance 接口或者 AbstractLoadBalance 类。
```java
package com.xxx;

import org.apache.dubbo.rpc.cluster.LoadBalance;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.RpcException;

public class XxxLoadBalance implements LoadBalance {
    public <T> Invoker<T> select(List<Invoker<T>> invokers, Invocation invocation) throws RpcException {
        // ...
    }
}
```
2. 第2步：我们将这个实现类的路径写入到resources 目录下的 `META-INF/dubbo/org.apache.dubbo.rpc.cluster.LoadBalance`文件中即可。
```java
src
 |-main
    |-java
        |-com
            |-xxx
                |-XxxLoadBalance.java (实现LoadBalance接口)
    |-resources
        |-META-INF
            |-dubbo
                |-org.apache.dubbo.rpc.cluster.LoadBalance (纯文本文件，内容为：xxx=com.xxx.XxxLoadBalance)
```

#### dubbo 提供的其他扩展SPI
使用 IoC 容器帮助管理组件的生命周期、依赖关系注入等是很多开发框架的常用设计，Dubbo 中内置了一个轻量版本的 IoC 容器，用来管理框架内部的插件，实现包括插件实例化、生命周期、依赖关系自动注入等能力。

Dubbo 插件体系与 IoC 容器具有以下特点：

* 核心组件均被定义为插件，用户或二次开发者扩展非常简单。 在无需改造框架内核的情况下，用户可以基于自身需求扩展如负载均衡、注册中心、通信协议、路由等策略。
* 平等对待第三方扩展实现。 Dubbo 中所有内部实现和第三方实现都是平等的，用户可以基于自身业务需求替换 Dubbo 提供的原生实现。
* 插件依赖支持自动注入（IoC）。 如果插件实现依赖其他插件属性，则 Dubbo 框架会完成该依赖对象的自动注入，支持属性、构造函数等方式。
* 插件扩展实现支持 AOP 能力。 框架可以自动发现扩展类的包装类，通过包装器模式对插件进行 AOP 增强。
* 支持插件自动激活。 通过为插件实现指定激活条件（通过注解参数等），框架可在运行时自动根据当前上下文决策是否激活该插件实现。
* 支持插件扩展排序。


Dubbo 在框架中定义了非常多的扩展点，因此，当你发现官方库没法满足业务需求，想为 Dubbo 框架提供定制能力时，请优先查阅以下扩展点定义，看是否能通过提供扩展实现的方式无侵入的定制 Dubbo 框架。
![图片3](../../src/main/resources/static/image/dubbo/dubbo_spi.png)

更多Dubbo SPI 插件及详情
https://cn.dubbo.apache.org/zh-cn/overview/mannual/java-sdk/reference-manual/spi/spi-list/

https://javaguide.cn/distributed-system/rpc/dubbo.html#dubbo-%E6%9E%B6%E6%9E%84%E4%B8%AD%E7%9A%84%E6%A0%B8%E5%BF%83%E8%A7%92%E8%89%B2%E6%9C%89%E5%93%AA%E4%BA%9B