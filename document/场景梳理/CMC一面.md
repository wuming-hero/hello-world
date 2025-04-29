cmc java一面
* cpu性能问题怎么排查，gc问题怎么排查，redis的热点key怎么解决
* 如何防止服务雪崩、应用线程资源如何隔离、服务假死故障发现与恢复、限流设计方案。
* java 并发中如何实现 a、b、c 三个线程交替执行
* kafka 消息堆积故障恢复、kafka 消费者组如何避免partion重复分配
* spring-boot 自动配置的原理、如何开发一个 starter-redis：
* 如何记录用户在线状态、怎么避免单机热点
* zset scanScore 复杂度、query Member 复杂度、跳表如何评估是否添加层级每个问题会追问下
* mysql mmvc原理，死锁机制，spring循环依赖再怎么解决，redis的keys和scan命令区别，怎么排查redis慢查询，哨兵模式和集群模式的区别
* 用Java实现一个滑动窗口用到什么数据结构这些
* Kafka 全局一致性怎么保证，springBoot怎么使用websocket以及原理，Mysql B树B+树的区别执行机制查询效率，设计模式有用过哪些 做下介绍，SPI即
* Service Provider Interface

## 场景
* 比如redis各种场景结构怎么存，
* 事务传播，
* 分布式锁实现， 底层算法和数据结构，
* redis会问一下底层原理 比如bitmap应用场景和数据结构，
* Redis集群怎么保持数据一致性
* kafka的分区和具体应用场景怎么保证不丢数据

一面：
1. 如果将一个月的K线数据预存在redis中，你认为应该怎么存，假设五分钟一个柱子
2. 如果有1千万个用户和1千万个视频，我需要记录用户对视频的点赞或是否订阅的状态，怎么实现
3. 假如我要实现一个长度只有100的分布式队列，基于LRU的规则进行元素剔除，怎么实现
4. kafka的rebalance，你们生产环境上有没有遇到过，怎么避免
5. redis的zrank和zscore的时间复杂度是多少，为什么
6. zset的value,score能不能重复，底层存储结构是什么
7. 1亿行数据如何统计某些属性的出现次数