# MongoDB 是什么？
MongoDB 是一个基于 分布式文件存储 的开源 NoSQL 数据库系统，由 C++ 编写的。MongoDB 提供了 面向文档 的存储方式，操作起来比较简单和容易，支持“无模式”的数据建模，可以存储比较复杂的数据类型，是一款非常流行的文档类型数据库 。

## 优点： 
* 在高负载的情况下，MongoDB 天然支持水平扩展和高可用，可以很方便地添加更多的节点/实例，以保证服务性能和可用性。
* 在许多场景下，MongoDB 可以用于代替传统的关系型数据库或键/值存储方式，皆在为 Web 应用提供可扩展的高可用高性能数据存储解决方案。

## 存储引擎
借助 WiredTiger 存储引擎（ MongoDB 3.2 后的默认存储引擎），MongoDB 支持对所有集合和索引进行压缩。 压缩以额外的 CPU 为代价最大限度地减少存储使用。

默认情况下，WiredTiger 使用 Snappy 压缩算法（谷歌开源，旨在实现非常高的速度和合理的压缩，压缩比 3 ～ 5 倍）对所有集合使用块压缩，对所有索引使用前缀压缩。

WiredTiger 日志也会被压缩，默认使用的也是 Snappy 压缩算法。如果日志记录小于或等于 128 字节，WiredTiger 不会压缩该记录。



## 事务
MongoDB 单文档原生支持原子性，也具备事务的特性。当谈论 MongoDB 事务的时候，通常指的是 多文档 。
* MongoDB 4.0 加入了对多文档 ACID 事务的支持，但只支持复制集部署模式下的 ACID 事务，也就是说事务的作用域限制为一个副本集内。
* MongoDB 4.2 引入了 分布式事务 ，增加了对分片集群上多文档事务的支持，并合并了对副本集上多文档事务的现有支持。

NoSQL 数据的时候也说过，NoSQL 数据库通常不支持事务，为了可扩展和高性能进行了权衡。不过，也有例外，MongoDB 就支持事务。
与关系型数据库一样，MongoDB 事务同样具有 ACID 特性：
* 原子性（Atomicity）：事务是最小的执行单位，不允许分割。事务的原子性确保动作要么全部完成，要么完全不起作用；
* 一致性（Consistency）：执行事务前后，数据保持一致，例如转账业务中，无论事务是否成功，转账者和收款人的总额应该是不变的；
* 隔离性（Isolation）：并发访问数据库时，一个用户的事务不被其他事务所干扰，各并发事务之间数据库是独立的。**WiredTiger 存储引擎支持读未提交（ read-uncommitted ）、读已提交（ read-committed ）和快照（ snapshot ）隔离，MongoDB 启动时默认选快照隔离。** 在不同隔离级别下，一个事务的生命周期内，可能出现脏读、不可重复读、幻读等现象。
* 持久性（Durability）：一个事务被提交之后。它对数据库中数据的改变是持久的，即使数据库发生故障也不应该对其有任何影响。


mongoDB 使用文档 https://www.mongodb.com/zh-cn/docs/manual/core/document/#std-label-bson-document-format

原文链接：https://javaguide.cn/database/mongodb/mongodb-questions-01.html

https://javaguide.cn/database/mongodb/mongodb-questions-01.html
https://javaguide.cn/database/mongodb/mongodb-questions-02.html#%E5%88%86%E7%89%87%E9%9B%86%E7%BE%A4