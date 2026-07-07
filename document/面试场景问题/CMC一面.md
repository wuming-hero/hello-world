# java基础

## cpu性能问题怎么排查
- **工具链**：
  - `top -Hp <pid>` 查看进程内各线程 CPU 占用；
  - `perf top` / `perf record` 采样热点函数；
  - `jstack` 导出线程栈结合 `printf "%x\n" <tid>` 转换线程 ID 定位到具体代码；
  - `arthas` 的 `thread` 命令可直接显示最忙线程及堆栈。
- **常见原因**：
  - 无限循环、频繁 GC、锁竞争（偏向锁撤销/自旋）、正则回溯、序列化/反序列化瓶颈等。
- **步骤**：
  - 先确认是用户态还是内核态高 → 抓取多次 thread dump 对比 → 分析热点方法 → 优化算法或减少不必要的计算。

## gc问题怎么排查
- **监控指标**：GC 频率、停顿时间、吞吐量、晋升大小、老年代占用率。
- **工具**：
  - `jstat -gcutil <pid> <interval>`；
  - `-XX:+PrintGCDetails -Xloggc:gc.log`；
  - `MAT` / `JProfiler` 分析堆转储；
  - `arthas` 的 `dashboard` / `vmtool`。
- **常见问题**：
  - Full GC 频繁（对象过早晋升、大对象直接进入老年代、Metaspace 不足）；
  - CMS 并发失败；G1 的 Humongous Allocation 导致 mixed gc 过多。
- **解决思路**：
  - 调整堆大小、新生代比例、晋升阈值；
  - 更换 GC 算法（如 G1→ZGC）；消除内存泄漏或缓存无限制增长。

## jmap 实战使用
- **常用命令**：
    - `jmap -heap <pid>`：打印堆配置和使用情况。
    - `jmap -histo[:live] <pid>`：按类统计实例数和占用字节（加 `:live` 触发 Full GC 只统计存活对象）。
    - `jmap -dump:format=b,file=heap.hprof <pid>`：生成堆转储文件用于离线分析。
- **实战场景**：
  - 定位内存泄漏（对比两次 histo 中持续增长的类）；分析大对象分布；检查堆容量是否合理。

## jstack 排查问题使用
- **核心用法**：
    - `jstack <pid>` 导出所有线程堆栈。
    - 配合 `grep` 过滤特定状态：`BLOCKED`、`WAITING`、`RUNNABLE`。
    - 将线程 ID 转为十六进制后匹配 nid 定位到具体线程。
- **典型问题**：
    - **死锁**：jstack 会直接输出 "Found one Java-level deadlock" 及参与线程。
    - **线程阻塞**：大量 BLOCKED 表示锁竞争严重；大量 WAITING (park) 可能是连接池耗尽或异步回调未唤醒。
    - **CPU 高**：找到 RUNNABLE 且 CPU 占比高的线程，分析其堆栈中的热点方法。

## 如何防止服务雪崩、应用线程资源如何隔离、服务假死故障发现与恢复、限流设计方案
- **防雪崩**：
    - 熔断（Sentinel/Hystrix）：下游错误率超阈值快速失败。
    - 降级：返回兜底数据或空结果。
    - 限流：控制入口流量。
    - 隔离：线程池隔离、信号量隔离。
- **线程资源隔离**：
    - 业务分组使用独立线程池（Tomcat 自定义 ThreadPoolExecutor）。
    - 异步任务使用不同 ExecutorService，避免相互影响。
    - 使用舱壁模式（Bulkhead）：如 Resilience4j 的 SemaphoreBulkhead 或 ThreadPoolBulkhead。
- **服务假死故障发现与恢复**：
    - **发现**：健康检查（HTTP / TCP 端口探测）+ 线程池活跃度监控 + GC 停顿告警 + 外部探活（如 K8s liveness probe）。
    - **恢复**：自动重启（K8s restartPolicy）、手动触发 dump 后重启、限流/降级先止损再扩容。
- **限流设计方案**：
    - 固定窗口（临界问题）→ 滑动窗口（更平滑）→ 令牌桶（允许突发）→ 漏桶（恒定速率）。
    - 分布式限流：Redis+Lua 脚本实现滑动窗口或令牌桶；Sentinel 的集群限流。
    - 接入层（Nginx Lua/OpenResty）限流，应用层二次校验。

## java 并发中如何实现 a、b、c 三个线程交替执行
- **方式一**：`synchronized` + `wait/notifyAll` + 标志位。
- **方式二**：`ReentrantLock` + `Condition`（每个线程一个 Condition，精准唤醒）。
- **方式三**：`Semaphore`（每个线程一个许可，循环 acquire/release）。
- **方式四**：`CompletableFuture.thenApplyAsync` 链式调用。
- **推荐**：使用 `Thread.join()` 只能顺序执行一次；若需循环交替，用 Lock/Condition 最清晰。

## 如何记录用户在线状态、怎么避免单机热点
- **记录方案**：
    - Redis Hash：`user:online:{date}` field=userId, value=lastHeartbeat。
    - Redis Set：每个时间段一个 set 存放活跃 userId，过期时间 TTL。
    - 心跳上报 + 定时清理（惰性删除或后台扫描）。
- **避免单机热点**：
    - 本地缓存 + Redis 回源（例如 Guava Cache 存储最近 N 分钟在线用户，批量合并写 Redis）。
    - Redis Cluster 分片：按 userId 哈希分散到不同节点。
    - 读写分离：读多写少可容忍短暂不一致，使用 slave 读取。
    - 降低精度：只记录在线/离线，不记录精确时间戳，减少更新频率。

## Service Provider Interface
- **定义**：Java SPI 机制，通过 `META-INF/services/` 下以接口全限定名为文件名的文本文件，列出实现类全名，由 `ServiceLoader.load()` 加载。
- **典型应用**：JDBC Driver、日志框架（SLF4J 绑定）、Dubbo 扩展点。
- **与 Spring Factories 区别**：Spring 的 `spring.factories` 也是类似思想，但更灵活（支持按条件加载）。SPI 是 JDK 原生，适合轻量级插件化。

## 用Java实现一个滑动窗口用到什么数据结构这些
- **核心数据结构**：`Deque`（双端队列）或 `Circular Array`。
- **实现逻辑**：
    - 固定时间窗口（如 1s 内请求数 ≤ 10）：每次请求加入队尾，移除队首超过时间窗口的元素，统计队列长度。
    - 使用 `LinkedList<Long>` 存储每个请求的时间戳。
    - 更高效：使用环形数组 + 原子计数器（如 Sentinel 的 LeapArray），避免频繁创建对象。
- **变种**：滑动窗口计数器（将窗口切为多个小格子，每个格子独立计数，滑动时丢弃旧格子）。

## webflux
- **定义**：Spring WebFlux 是响应式 Web 框架，基于 Reactor 库，支持背压，运行在 Netty / Undertow 等非阻塞容器上。
- **特点**：
    - 完全异步非阻塞，适合 IO 密集型（网关、长连接推送）。
    - 编程模型：注解式（`@RestController` 返回 `Mono<T>` / `Flux<T>`）或函数式（RouterFunction）。
    - 与 Spring MVC 共存（需区分依赖）。
- **适用场景**：高并发低延迟、流式数据处理、WebSocket、Server-Sent Events。

## reactor
- **核心概念**：
    - `Publisher`（`Mono` 0~1 元素，`Flux` 0~N 元素）。
    - `Subscriber`：`onSubscribe`、`onNext`、`onError`、`onComplete`。
    - `Operator`：`map`、`flatMap`、`filter`、`zip`、`retry` 等。
    - 背压（Backpressure）：通过 `Subscription.request(n)` 控制消费速度。
- **调度器**：`Schedulers.parallel()`、`Schedulers.boundedElastic()`、`Schedulers.single()`。
- **实战注意**：避免在 operator 中执行阻塞操作；合理设置缓冲区大小；异常处理用 `onErrorResume` / `doOnError`。

---

# redis

## redis的热点key怎么解决
- **识别**：Redis 的 `hotkeys` 参数（4.0+）、`redis-cli --hotkeys`、客户端统计访问频次。
- **解决方案**：
    1. **本地缓存**：热 key 缓存在应用本地（Caffeine），设置较短过期时间，降低 Redis 压力。
    2. **散列**：将 key 加上随机后缀（如 `key_${random(100)}`），分散到多个 Redis 节点，读取时轮询或广播（需业务兼容）。
    3. **读写分离**：主节点写，从节点读（但热 key 仍可能打爆单个从节点）。
    4. **二级缓存**：Redis + 本地缓存组合，配合失效通知（Pub/Sub 或 ZooKeeper 监听）。
    5. **限流降级**：对热 key 的访问实施限流，超出部分返回默认值。

## redis的keys和scan命令区别？
- **keys**：阻塞遍历所有键，返回匹配的全部 key。
  - **缺点**：单线程阻塞，数据量大时导致 Redis 卡顿，生产禁用。
- **scan**：基于游标（cursor）的增量迭代，每次返回少量数据（count 提示，非精确数量）。
  - **优点**：不阻塞主线程，可分批处理。
  - **缺点**：可能有重复 key（需去重），无法保证完整遍历（但最终能遍历完）。
- **使用场景**：
  - scan 用于生产环境的大 key 扫描、模糊删除；
  - keys 仅可用于开发测试小数据量。

## 比如redis各种场景结构怎么存？
- **String**：缓存简单值（JSON 字符串、计数器、分布式锁）。
- **Hash**：对象属性（用户信息、商品详情），节省内存且可单独更新字段。
- **List**：消息队列（LPUSH/RPOP）、时间线（最新动态）、栈。
- **Set**：标签、共同好友（交集）、去重统计。
- **Sorted Set (ZSet)**：排行榜、延时队列（score 为时间戳）、滑动窗口计数。
- **Bitmap**：签到、日活统计（SETBIT/GETBIT）、布隆过滤器底层。
- **HyperLogLog**：基数统计（UV），误差约 0.81%。
- **Geo**：地理位置附近的人（GEOADD/GEORADIUS）。
- **Stream**：消息队列（持久化、消费者组、ACK 机制）。

## zset的value,score能不能重复，底层存储结构是什么
- **value 不能重复**（唯一），**score 可以重复**。
- **底层结构**：`ziplist`（元素少且值小时）或 `skiplist + dict`（默认）。
    - `dict`：维护 value -> score 映射，保证 O(1) 查分。
    - `skiplist`：按 score 排序，支持范围查询 O(logN)。

## zset zscan、zscore 查询成员的复杂度、跳表如何评估是否添加层级?
- **zscore**：O(1)，因为底层有 dict 直接根据 value 获取 score。
- **zscan**：O(N) 渐进式遍历（类似于 scan），但实际受限于游标返回数量。
- **跳表层级**：每个节点在插入时随机决定层数（概率 1/2 升一层，最大 32 层）。期望层高约 log₂N，保证平均查找复杂度 O(logN)。不采用平衡树是因为实现简单且并发友好。

## redis的zrank和zscore的时间复杂度是多少，为什么
- **zrank**：O(logN)，因为需要在 skiplist 中找到对应 member 并累加排名（skiplist 按 score 排序，同分时按字典序）。
- **zscore**：O(1)，直接从 dict 哈希表中获取。

## Redis集群怎么保持数据一致性
- **Redis Cluster** 使用 **异步复制**，不保证强一致性。主节点写入后立即返回成功，异步同步到从节点。
- **一致性权衡**：
    - 可通过 `WAIT` 命令等待至少 N 个从节点确认（牺牲性能）。
    - 网络分区时可能出现脑裂（主从切换导致数据丢失）。
- **保证最终一致性**：主从同步 + 故障转移后从节点补发缺失数据（通过 replication backlog）。
- **强一致性需求**：需使用 RedLock 或引入 Paxos/Raft 协议（如 Codis 使用 etcd 协调）。

## Redis 哨兵模式和集群模式的区别？如何使用哨兵模式解决集群情况下分布式锁的可靠性？
- **区别**：
    - **哨兵模式**：主从架构，哨兵负责监控、自动故障转移；客户端通过哨兵获取主节点地址；不支持数据分片。
    - **集群模式**：数据分片（16384 个 slot），每个节点管理一部分 slot；内置高可用（主从切换）；客户端直连任意节点，通过 MOVED/ASK 重定向。
- **哨兵模式解决分布式锁可靠性**：
    - 哨兵本身不能解决锁的可靠性（主从异步复制可能导致锁丢失）。
    - 正确做法：使用 **RedLock** 算法（向多个独立的 Redis 实例申请锁，半数以上成功才算加锁成功）。但哨兵模式下实例间无关联，RedLock 依然有效，只是部署成本高。
    - 生产建议：若必须用 Redis 分布式锁，优先使用 Redisson 封装的 RedLock，或改用 ZooKeeper / etcd 的强一致性锁。

## redis底层原理 比如bitmap应用场景和数据结构，
- **Bitmap** 本质是 String（二进制安全），最大 512MB。操作命令：`SETBIT`、`GETBIT`、`BITCOUNT`、`BITOP`。
- **应用场景**：
    - 用户签到（每天一位，月统计 BITCOUNT）。
    - 布隆过滤器（多个 hash 函数映射位）。
    - 在线用户位图（每个 bit 代表一个用户 ID 是否在线）。
    - 连续登录天数（位运算 AND/OR）。
- **数据结构**：底层 SDS（Simple Dynamic String），按位操作，时间复杂度 O(1) 单 bit，O(N) 批量。

## 怎么排查redis慢查询
- **开启慢查询日志**：`slowlog-log-slower-than 10000`（微秒，默认 10ms），`slowlog-max-len 128`。
- **查看**：`SLOWLOG GET [n]` 获取最近 N 条慢查询，包含耗时、时间戳、命令及参数。
- **分析**：
    - 大 key 操作（`KEYS`、`SMEMBERS`、`HGETALL`、`LRANGE` 全量）。
    - 复杂命令（`SORT`、`ZUNIONSTORE`、`BITOP`）。
    - 批量命令（`MGET` 虽快但大数据量也可能慢）。
    - 网络延迟或客户端阻塞。
- **优化**：拆分大 key、使用 SCAN 替代 KEYS、减少复杂聚合、升级 Redis 版本（6.0 多线程 IO 缓解）。

---

# 分布式

## 分布式锁实现， 底层算法和数据结构
- **基于 Redis**：
    - **SETNX + EXPIRE**（原子性需用 Lua 或 SET key value NX PX 30000）。
    - **RedLock**：向多数派实例加锁，解决主从异步复制导致锁丢失。
    - **Redisson** 封装：WatchDog 自动续期，防止锁超时释放。
- **基于 ZooKeeper**：
    - 临时顺序节点 + Watch 机制，最小序号获得锁，避免羊群效应。
    - 强一致性（ZAB 协议），无锁丢失风险。
- **基于 etcd**：
    - Lease + Txn API，类似 ZK，支持续约和 watch。
- **底层数据结构**：
    - Redis：String（锁 key/value）、Lua 脚本保证原子性。
    - ZK：ZNode 树形结构，临时节点与 session 绑定。
    - etcd：MVCC 存储，Key-Value 带 Revision。

---

# mysql？事务

## 事务传播
- **Propagation**（Spring 声明式事务）：
    - `REQUIRED`：支持当前事务，没有则新建（默认）。
    - `SUPPORTS`：支持当前事务，没有则以非事务执行。
    - `MANDATORY`：必须已有事务，否则抛异常。
    - `REQUIRES_NEW`：挂起当前事务，新建独立事务。
    - `NOT_SUPPORTED`：以非事务方式执行，挂起当前事务。
    - `NEVER`：以非事务方式执行，若有事务则抛异常。
    - `NESTED`：嵌套事务，利用 savepoint 回滚到嵌套点（JDBC 驱动支持）。

## mysql mvcc原理，死锁机制
- **MVCC（多版本并发控制）**：
    - 基于 **Undo Log** 实现快照读（`SELECT ... LOCK IN SHARE MODE` 或 `FOR UPDATE` 除外）。
    - 每行记录隐藏两个字段：`DB_TRX_ID`（最后修改事务ID）、`DB_ROLL_PTR`（回滚指针指向 Undo Log 版本链）。
    - **Read View**：事务启动时生成，记录当前活跃事务列表。判断可见性：比较事务 ID 与 Read View 的 up_limit_id / low_limit_id。
    - 作用：实现 RC（每次语句生成新 ReadView）和 RR（第一次查询生成，复用）隔离级别，避免脏读、不可重复读（RR 还避免幻读）。
- **死锁机制**：
    - MySQL InnoDB 自动检测死锁（`innodb_deadlock_detect=ON`），选择回滚代价较小的事务（undo 较少）。
    - 死锁发生条件：互斥、持有并等待、不可剥夺、循环等待。
    - 预防：按相同顺序加锁；减少大事务；索引优化避免锁升级；调整隔离级别（如 RC 减少 gap lock 死锁）。

## 列存储数据库 Lindorm、TableStore
- **Lindorm**（阿里云）：宽表、时序、搜索引擎一体，支持 HBase 协议、SQL 访问。适合 IoT、监控、风控等海量数据场景。
- **TableStore**（阿里云 Tablestore，原名 OTS）：NoSQL 分布式数据库，支持宽表和时序模型，自动分区、强一致/最终一致可选，适合结构化半结构化数据。
- **共性**：列式存储（按列族组织），压缩率高；水平扩展；适合 OLAP 场景的聚合分析；但与关系型数据库相比事务能力弱。

---

# spring/spring-boot

## spring-boot 自动配置的原理
- **核心注解**：`@EnableAutoConfiguration` → `@Import(AutoConfigurationImportSelector.class)`。
- **加载流程**：
    1. `AutoConfigurationImportSelector` 从 `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`（2.7+）或 `spring.factories` 读取所有 AutoConfiguration 类。
    2. 根据 `@Conditional` 系列注解（`@ConditionalOnClass`、`@ConditionalOnMissingBean`、`@ConditionalOnProperty` 等）判断是否生效。
    3. 满足条件的配置类被注册为 Bean。
- **自定义 Starter**：编写 `XXXAutoConfiguration` + 属性绑定类（`@ConfigurationProperties`） + `spring.factories` 或新的 imports 文件。

## 如何开发一个 starter-redis
- **步骤**：
    1. 新建 Maven 项目 `my-redis-spring-boot-starter`。
    2. 定义属性类 `RedisProperties` 标注 `@ConfigurationProperties(prefix = "my.redis")`。
    3. 编写自动配置类 `RedisAutoConfiguration`：注入 `JedisConnectionFactory` / `LettuceConnectionFactory`，创建 `RedisTemplate` Bean。
    4. 添加 `@ConditionalOnClass({RedisTemplate.class})` 等条件。
    5. 在 `resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` 中写入 `com.example.RedisAutoConfiguration`。
    6. 打包并引用即可。

## spring循环依赖再怎么解决
- **三级缓存机制**（DefaultSingletonBeanRegistry）：
    - `singletonObjects`（一级，完成初始化）
    - `earlySingletonObjects`（二级，提前暴露的半成品）
    - `singletonFactories`（三级，ObjectFactory 工厂）
- **流程**：A 依赖 B，B 依赖 A。
    1. A 开始创建，放入三级缓存 `singletonFactories`（lambda 表达式返回早期引用）。
    2. A 填充属性时发现依赖 B，去创建 B。
    3. B 填充属性时发现依赖 A，从三级缓存拿到 A 的 ObjectFactory，调用后得到早期引用，放入二级缓存，删除三级。
    4. B 完成初始化，放入一级缓存。
    5. A 继续完成后续初始化（如 AOP 代理），最终替换一级缓存中的对象。
- **限制**：只能解决单例 setter 注入的循环依赖；构造器注入无法解决（因为还没创建就卡住）；prototype 作用域不缓存也无法解决。

---

# kafka

## kafka 消息堆积故障恢复、kafka 消费者组如何避免partion重复分配
- **消息堆积恢复**：
    1. 增加消费者（提升消费并行度，需注意 partition 数 ≥ 消费者数）。
    2. 扩容 topic 分区（需重建 topic 或使用 Kafka 2.3+ 的 `alter partition` 功能）。
    3. 临时跳过积压消息（修改 consumer 的 `auto.offset.reset` 或 seek 到最新 offset，但会丢消息）。
    4. 优化消费逻辑（批量处理、异步提交、减少 I/O）。
    5. 排查生产者是否发送过快，限流。
- **避免 partition 重复分配**：
    - 消费者组 rebalance 会导致分区重新分配，期间消费暂停。
    - 避免方法：
        - 使用静态成员（`group.instance.id`）减少因消费者心跳超时触发的 rebalance。
        - 增大 `session.timeout.ms` 和 `heartbeat.interval.ms`。
        - 使用 Cooperative Rebalancing（Kafka 2.4+），逐步迁移分区而非全部停止。
        - 合理规划消费者数量（≤ 分区数），避免频繁增减消费者。

## Kafka 全局一致性怎么保证，springBoot怎么使用websocket以及原理，Mysql B树B+树的区别执行机制查询效率，设计模式有用过哪些 做下介绍，SPI即
- **Kafka 全局一致性**：Kafka 本身不提供跨分区全局顺序，只保证单分区内有序。要全局顺序需将所有消息发往同一分区（牺牲并行）。若需强一致性，可使用事务（`transactional.id` + 幂等生产者）实现 exactly-once 语义。
- **Spring Boot 使用 WebSocket**：
    - 引入 `spring-boot-starter-websocket`。
    - 实现 `WebSocketHandler` 或使用 `@MessageMapping` + `SimpMessagingTemplate`（STOMP 协议）。
    - 原理：基于 HTTP 升级握手（Upgrade: websocket），之后建立全双工 TCP 长连接，通过帧（frame）传输数据。
- **MySQL B树 vs B+树**：
    - B树：非叶子节点也存储数据，范围查询需多次 IO。
    - B+树：非叶子节点只存索引，叶子节点存数据并形成链表，范围查询只需扫描叶子链表，IO 次数更少，查询效率稳定（高度低）。InnoDB 使用 B+树作为聚簇索引。
- **设计模式举例**：
    - **策略模式**：支付渠道选择（不同算法封装）。
    - **模板方法**：JdbcTemplate 的执行流程。
    - **观察者**：Spring 事件监听（ApplicationEvent）。
    - **工厂**：BeanFactory。
    - **代理**：AOP 动态代理。
    - **责任链**：Filter 链、Netty Pipeline。
- **SPI**：前面已答。

## kafka的分区和具体应用场景怎么保证不丢数据
- **生产者侧**：
    - `acks=all`（等待所有 ISR 副本确认）。
    - `retries` 重试 + `enable.idempotence=true`（幂等）。
    - `max.in.flight.requests.per.connection=1`（防止乱序，但影响吞吐）。
- **Broker 侧**：
    - `min.insync.replicas=2`（至少 2 个副本同步才返回成功）。
    - `unclean.leader.election.enable=false`（不允许非 ISR 副本成为 leader）。
    - 使用多副本（replication-factor ≥ 3）。
- **消费者侧**：
    - 手动提交 offset（`commitSync` 或 `commitAsync` + 回调重试）。
    - 先处理业务再提交 offset。
    - 关闭自动提交（`enable.auto.commit=false`）。
- **场景**：金融交易、订单系统等要求严格不丢的场景，需配合事务（`transactional.id`）实现 exactly-once。

## kafka的rebalance，你们生产环境上有没有遇到过，怎么避免
- **遇到过**：消费者组内成员变动（新增/退出/超时）引发 stop-the-world 式的 rebalance，导致消费中断几分钟。
- **避免措施**：
    - 使用静态成员（Static Group Membership）：`group.instance.id` 固定，即使重启也不会触发 rebalance（需 Kafka 2.3+）。
    - 增大 `session.timeout.ms`（如 45s）和 `heartbeat.interval.ms`（15s），减少误判。
    - 使用 Cooperative Sticky Assignor（`partition.assignment.strategy=org.apache.kafka.clients.consumer.CooperativeStickyAssignor`），实现增量 rebalance。
    - 避免消费者数量频繁变化，滚动发布时先停掉一半消费者，待稳定后再停另一半。

---

# 具体场景：

## 1. 如果将一个月的K线数据预存在redis中，你认为应该怎么存，假设五分钟一个柱子
- **数据量估算**：一个月 ≈ 30天 × 24小时 × 12根/小时 = 8640 根 K 线。若同时存多个股票/币种，总量可控。
- **存储方案**：
    - **Key 设计**：`kline:{symbol}:{date}`（如 `kline:BTCUSDT:20260703`），value 使用 **ZSet**，score 为时间戳（毫秒），member 为 JSON 字符串（open/high/low/close/volume）。
    - 或者用 **List** 按时间顺序 push，但 ZSet 支持按时间范围查询（ZRANGEBYSCORE）。
    - 若需要快速获取最新一根，可用单独的 String Key `kline:latest:{symbol}` 存储。
- **过期策略**：设置 TTL 略大于一个月（如 35 天），让 Redis 自动淘汰旧数据。
- **查询优化**：使用 pipeline 批量获取多个 symbol 的某段时间 K 线。

## 2. 如果有1千万个用户和1千万个视频，我需要记录用户对视频的点赞或是否订阅的状态，怎么实现
- **方案一：Redis Bitmap**
    - 每个视频一个 bitmap，位偏移为用户 ID（需将用户 ID 映射为连续整数，如从 0 开始）。
    - 优点：极省内存（1 千万用户占 1.19 MB/视频，1 千万视频共约 11.9 TB，太大不可行）。
    - 改进：只存储热门视频的 bitmap，冷门用其他方式。
- **方案二：Redis Hash + 分段**
    - Key：`like:{videoId}`，field：`userId`，value：1/0。
    - 每个 Hash 最多存几万 field（避免 big key），可按 userId 模 1000 分片成多个 Hash。
- **方案三：MySQL 分库分表**
    - 按 videoId 哈希分表（如 256 张表），建联合主键 (video_id, user_id)，加索引。
    - 点赞/取消点赞直接 update 状态字段。
    - 适合精确查询某个用户是否点赞某视频，但批量统计稍慢。
- **方案四：ClickHouse / Druid** 列式存储，适合离线分析和实时统计。
- **推荐**：混合使用——实时点赞用 Redis Hash 分片，定期刷入 MySQL 持久化，统计用 ClickHouse。

## 3. 假如我要实现一个长度只有100的分布式队列，基于LRU的规则进行元素剔除，怎么实现
- **思路**：固定容量 + LRU 淘汰，需要分布式环境下共享状态。
- **实现方式**：
    - 使用 **Redis ZSet**：member 为元素内容，score 为访问时间戳（毫秒）。
    - 入队（添加元素）：`ZADD queue:xxx timestamp member`。
    - 出队（获取最早元素）：`ZRANGE queue:xxx 0 0 WITHSCORES` + `ZREM`。
    - 淘汰：每次操作后检查 `ZCARD`，若 > 100，则删除 score 最小的（最久未访问）元素：`ZREMRANGEBYRANK queue:xxx 0 (ZCARD-100)`。
    - 访问更新（LRU）：当元素被命中时，更新其 score 为当前时间戳。
- **注意**：ZSet 操作不是原子的，需要用 Lua 脚本封装以保证一致性。
- **替代方案**：使用 LinkedHashMap 实现本地 LRU，但分布式下需同步，不如 Redis 简单。

## 4.1亿行数据如何统计某些属性的出现次数
- **场景**：统计日志中某字段（如 IP、URL）的出现次数。
- **方案一：MapReduce / Spark**
    - 使用 Hadoop 或 Spark 的 `groupBy` + `count`，适合离线批处理。
- **方案二：Flink 实时流**
    - 数据流入 Flink，使用 `keyBy` + `sum` 或 `AggregateFunction`，结果存入 Redis/MySQL。
- **方案三：Redis HyperLogLog**（近似统计）
    - 只适合 UV 类去重计数，不适合精确次数。
- **方案四：Redis Hash 计数器**
    - 每个属性作为 field，值递增（`HINCRBY`）。但 1 亿行数据可能产生百万级 field，单个 Hash 过大，需按属性哈希分片。
- **方案五：数据库（ClickHouse / TiDB）**
    - ClickHouse 的 AggregatingMergeTree 引擎非常适合此类聚合查询。
- **推荐**：若实时性要求高，使用 Flink + Redis Hash 分片；若离线，使用 Spark 一次性计算落盘。