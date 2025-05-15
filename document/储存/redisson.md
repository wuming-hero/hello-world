## 什么是Redisson？
redis 是 C/S 架构，即 需要客户端和服务端才算完整。

我们常说的 redis 一般是指 redis server 服务端，原则上，redis client 可以使用任何语言实现，只要遵循RESP 协议（redis 制定的应用层通信协议）即可。

而这里 redisson 就是 java 版本的客户端实现，redission实现加锁解锁也是用lua脚本的。常见的 java 版客户端实现还有 lettuce、jedis 等。

### Redisson和Jedis、Lettuce有什么区别？

Redisson和它俩的区别就像一个用鼠标操作图形化界面，一个用命令行操作文件。

Redisson是更高层的抽象，Jedis和Lettuce是Redis命令的封装。

* Jedis是Redis官方推出的用于通过Java连接Redis客户端的一个工具包，提供了Redis的各种命令支持
* Lettuce是一种可扩展的线程安全的 Redis 客户端，通讯框架基于Netty，支持高级的 Redis 特性，比如哨兵，集群，管道，自动重新连接和Redis数据模型。Spring Boot 2.x 开始 Lettuce 已取代 Jedis 成为首选 Redis 的客户端。
* Redisson是架设在Redis基础上，通讯基于Netty的综合的、新型的中间件，企业级开发中使用Redis的最佳范本

Jedis把Redis命令封装好，Lettuce则进一步有了更丰富的Api，也支持集群等模式。
但是两者也都点到为止，只给了你操作Redis数据库的脚手架，而Redisson则是基于Redis、Lua和Netty建立起了成熟的分布式解决方案，甚至redis官方都推荐的一种工具集。

### Lua脚本是什么？
Lua脚本是redis已经内置的一种轻量小巧语言，其执行是通过redis的eval/eval sha 命令来运行，把操作封装成一个Lua脚本，如论如何都是一次执行的原子操作。

### Redisson分布式锁实现

#### RLock如何加锁？
从RLock进入，找到RedissonLock类，找到tryLock 方法再递进到干事的tryAcquireOnceAsync 方法，这是加锁的主要代码（版本不一此处实现有差别，和最新3.15.x有一定出入，但是核心逻辑依然未变。此处以3.13.6为例）
```java
private RFuture<Boolean> tryAcquireOnceAsync(long waitTime, long leaseTime, TimeUnit unit, long threadId) {
    if (leaseTime != -1L) {
        return this.tryLockInnerAsync(waitTime, leaseTime, unit, threadId, RedisCommands.EVAL_NULL_BOOLEAN);
    } else {
        // 看门狗机制
        RFuture<Boolean> ttlRemainingFuture = this.tryLockInnerAsync(waitTime, this.commandExecutor.getConnectionManager().getCfg().getLockWatchdogTimeout(), TimeUnit.MILLISECONDS, threadId, RedisCommands.EVAL_NULL_BOOLEAN);
        ttlRemainingFuture.onComplete((ttlRemaining, e) -> {
            if (e == null) {
                if (ttlRemaining) {
                    this.scheduleExpirationRenewal(threadId);
                }

            }
        });
        return ttlRemainingFuture;
    }
}
```
此处出现leaseTime时间判断的2个分支，实际上就是加锁时是否设置过期时间，未设置过期时间（-1）时则会有watchDog 的锁续约 （下文），一个注册了加锁事件的续约任务。我们先来看有过期时间tryLockInnerAsync 部分，

#### 加锁LUA脚本
```bash
-- 不存在该key时
if (redis.call('exists', KEYS[1]) == 0) then 
  -- 新增该锁并且hash中该线程id对应的count置1
  redis.call('hincrby', KEYS[1], ARGV[2], 1); 
  -- 设置过期时间
  redis.call('pexpire', KEYS[1], ARGV[1]); 
  return nil; 
end; 

-- 存在该key 并且 hash中线程id的key也存在
if (redis.call('hexists', KEYS[1], ARGV[2]) == 1) then 
  -- 线程重入次数++
  redis.call('hincrby', KEYS[1], ARGV[2], 1); 
  redis.call('pexpire', KEYS[1], ARGV[1]); 
  return nil; 
end; 
return redis.call('pttl', KEYS[1]);
```

具体参数
```bash
// keyName
KEYS[1] = Collections.singletonList(this.getName())
// leaseTime
ARGV[1] = this.internalLockLeaseTime
// uuid+threadId组合的唯一值
ARGV[2] = this.getLockName(threadId)
```

判断该锁是否已经有对应hash表存在：
* 没有对应的hash表：则set该hash表中一个entry的key为锁名称，value为1，之后设置该hash表失效时间为leaseTime
* 存在对应的hash表：则将该lockName的value执行+1操作，也就是计算进入次数，再设置失效时间leaseTime
* 最后返回这把锁的ttl剩余时间


#### RLock如何解锁？
那解锁的步骤也肯定有对应的-1操作，再看unlock方法，同样查找方法名，一路到
```java
protected RFuture<Boolean> unlockInnerAsync(long threadId) {
    return this.commandExecutor.evalWriteAsync(this.getName(), LongCodec.INSTANCE, RedisCommands.EVAL_BOOLEAN, "if (redis.call('exists', KEYS[1]) == 0) then redis.call('publish', KEYS[2], ARGV[1]); return 1; end;if (redis.call('hexists', KEYS[1], ARGV[3]) == 0) then return nil;end; local counter = redis.call('hincrby', KEYS[1], ARGV[3], -1); if (counter > 0) then redis.call('pexpire', KEYS[1], ARGV[2]); return 0; else redis.call('del', KEYS[1]); redis.call('publish', KEYS[2], ARGV[1]); return 1; end; return nil;", Arrays.asList(this.getName(), this.getChannelName()), new Object[]{LockPubSub.unlockMessage, this.internalLockLeaseTime, this.getLockName(threadId)});
}
```

对应的LUA脚本 
```bash
-- 不存在key
if (redis.call('hexists', KEYS[1], ARGV[3]) == 0) then 
  return nil;
end;
-- 计数器 -1
local counter = redis.call('hincrby', KEYS[1], ARGV[3], -1); 
if (counter > 0) then 
  -- 过期时间重设
  redis.call('pexpire', KEYS[1], ARGV[2]); 
  return 0; 
else
  -- 删除并发布解锁消息
  redis.call('del', KEYS[1]); 
  redis.call('publish', KEYS[2], ARGV[1]); 
  return 1;
end; 
return nil;
```
脚本执行释意：
1.如果该锁不存在则返回nil；
2.如果该锁存在则将其线程的hash key计数器-1，
3.计数器counter>0，重置下失效时间，返回0；否则，删除该锁，发布解锁消息unlockMessage，返回1；

注：`其中unLock的时候使用到了Redis发布订阅PubSub完成消息通知。而订阅的步骤就在RedissonLock的加锁入口的lock方法里`

```java
long threadId = Thread.currentThread().getId();
Long ttl = this.tryAcquire(-1L, leaseTime, unit, threadId);
if (ttl != null){
    // 订阅
    RFuture<RedissonLockEntry> future = this.subscribe(threadId);
    if(interruptibly){
            this.commandExecutor.syncSubscriptionInterrupted(future);
    }else{
        this.commandExecutor.syncSubscription(future);
    }
    // 省略
}
```

当锁被其他线程占用时，通过监听锁的释放通知（在其他线程通过RedissonLock释放锁时，会通过发布订阅pub/sub功能发起通知），等待锁被其他线程释放，也是为了避免自旋的一种常用效率手段。



## Redisson 分布式锁特性
### 1. 可重入
重入就是，同一个线程多次获取同一把锁是允许的，不会造成死锁，这一点synchronized偏向锁提供了很好的思路。
synchronized的实现重入是在JVM层面，JAVA对象头MARK WORD中便藏有线程ID和计数器来对当前线程做重入判断，避免每次CAS。

基于Redis的Redisson分布式可重入锁RLockJava对象实现了java.util.concurrent.locks.Lock接口。
同时还提供了异步（Async）、反射式（Reactive）和RxJava2标准的接口。

### 2. 看门狗
看门狗自动续期机制：加锁时是否设置过期时间，未设置过期时间（-1）时则会有watchDog的锁续约。
`当一个线程持有了一把锁，由于并未设置超时时间leaseTime，Redisson默认配置了30S，并开启watchDog机制：每10S对该锁进行一次续约，维持30S的超时时间，直到任务完成再删除锁。`

锁自动续期，如果业务超长，运行期间自动给锁续期上新的30s，不用担心业务时间长，锁自动过期被删除的造成的死锁问题。加锁的业务只要运行完成，就不会给当前锁续期，即使不手动解锁，锁默认在30s以后删除。

RLock是Redisson分布式锁的最核心接口，继承了concurrent包的Lock接口和自己的RLockAsync接口，RLockAsync的返回值都是RFuture，是Redisson执行异步实现的核心逻辑，也是Netty发挥的主要阵地。

代码入口 ：RedissonLock.this.scheduleExpirationRenewal(threadId)

流程概括：
1. A、B线程争抢一把锁，A获取到后，B阻塞
2. B线程阻塞时并非主动CAS，而是PubSub方式订阅该锁的广播消息
3. A操作完成释放了锁，B线程收到订阅消息通知
4. B被唤醒开始继续抢锁，拿到锁


### 公平锁 RedissonFairLock
Redis里没有AQS，但是有List和zSet，看看Redisson是怎么实现公平的。
```java
RLock fairLock = redissonClient.getFairLock(lockName);

fairLock.lock();
```

#### 公平锁加锁步骤
通过以上Lua，可以发现，lua操作的关键结构是列表（list）和有序集合（zSet）。

```bash
-- 1.死循环清除过期key
while true do 
  -- 获取头节点
    local firstThreadId2 = redis.call('lindex', KEYS[2], 0);
    -- 首次获取必空跳出循环
  if firstThreadId2 == false then 
    break;
  end;
  -- 清除过期key
  local timeout = tonumber(redis.call('zscore', KEYS[3], firstThreadId2));
  if timeout <= tonumber(ARGV[4]) then
    redis.call('zrem', KEYS[3], firstThreadId2);
    redis.call('lpop', KEYS[2]);
  else
    break;
  end;
end;

-- 2.不存在该锁 && （不存在线程等待队列 || 存在线程等待队列而且第一个节点就是此线程ID)，加锁部分主要逻辑
if (redis.call('exists', KEYS[1]) == 0) and 
  ((redis.call('exists', KEYS[2]) == 0)  or (redis.call('lindex', KEYS[2], 0) == ARGV[2])) then
  -- 弹出队列中线程id元素，删除Zset中该线程id对应的元素
  redis.call('lpop', KEYS[2]);
  redis.call('zrem', KEYS[3], ARGV[2]);
  local keys = redis.call('zrange', KEYS[3], 0, -1);
  -- 遍历zSet所有key，将key的超时时间(score) - 当前时间ms
  for i = 1, #keys, 1 do 
    redis.call('zincrby', KEYS[3], -tonumber(ARGV[3]), keys[i]);
  end;
    -- 加锁设置锁过期时间
  redis.call('hset', KEYS[1], ARGV[2], 1);
  redis.call('pexpire', KEYS[1], ARGV[1]);
  return nil;
end;

-- 3.线程存在，重入判断
if redis.call('hexists', KEYS[1], ARGV[2]) == 1 then
  redis.call('hincrby', KEYS[1], ARGV[2],1);
  redis.call('pexpire', KEYS[1], ARGV[1]);
  return nil;
end;

-- 4.返回当前线程剩余存活时间
local timeout = redis.call('zscore', KEYS[3], ARGV[2]);
    if timeout ~= false then
  -- 过期时间timeout的值在下方设置，此处的减法算出的依旧是当前线程的ttl
  return timeout - tonumber(ARGV[3]) - tonumber(ARGV[4]);
end;

-- 5.尾节点剩余存活时间
local lastThreadId = redis.call('lindex', KEYS[2], -1);
local ttl;
-- 尾节点不空 && 尾节点非当前线程
if lastThreadId ~= false and lastThreadId ~= ARGV[2] then
  -- 计算队尾节点剩余存活时间
  ttl = tonumber(redis.call('zscore', KEYS[3], lastThreadId)) - tonumber(ARGV[4]);
else
  -- 获取lock_name剩余存活时间
  ttl = redis.call('pttl', KEYS[1]);
end;

-- 6.末尾排队
-- zSet 超时时间（score），尾节点ttl + 当前时间 + 5000ms + 当前时间，无则新增，有则更新
-- 线程id放入队列尾部排队，无则插入，有则不再插入
local timeout = ttl + tonumber(ARGV[3]) + tonumber(ARGV[4]);
if redis.call('zadd', KEYS[3], timeout, ARGV[2]) == 1 then
  redis.call('rpush', KEYS[2], ARGV[2]);
end;
return ttl;
```

其中list维护了一个等待的线程队列redisson_lock_queue:{xxx}，
zSet维护了一个线程超时情况的有序集合redisson_lock_timeout:{xxx}，尽管lua较长，但是可以拆分为6个步骤

1. 队列清理
保证队列中只有未过期的等待线程
   
2. 首次加锁
hset加锁，pexpire过期时间
3. 重入判断
此处同可重入锁lua
   
4. 返回ttl

5. 计算尾节点ttl
初始值为锁的剩余过期时间
6. 末尾排队
ttl + 2 * currentTime + waitTime是score的默认值计算公式


## 3. 阻塞lock 和 非阻塞 tryLock

### 1. 阻塞方式 
在阻塞方式中，线程在尝试获取锁时，如果锁已被其他线程占用，那么当前线程会被阻塞，一直等到锁被释放后才能继续执行。
在阻塞模式下，线程可能会等待相当长的时间，直到获取到锁。

```java
ReentrantLock lock = new ReentrantLock();

// 阻塞方式获取锁
lock.lock();
try {
    // 执行需要锁保护的代码
} finally {
    lock.unlock();
}
```

当锁被其他线程占用时，该线程通过监听锁的释放通知（在其他线程通过RedissonLock释放锁时，会通过发布订阅pub/sub功能发起通知），等待锁被其他线程释放，也是为了避免自旋的一种常用效率手段。
该线程在while循环体内循环监听其他线程是否释放锁，释放后则该线程被唤醒继续抢锁，直到获取到锁

### 2. 非阻塞方式
在非阻塞方式中，线程尝试获取锁时，如果锁已被其他线程占用，当前线程不会被阻塞，而是立即返回一个结果，告知是否成功获取锁。
非阻塞方式下，线程不会等待，而是可以继续执行其他操作。
```java
ReentrantLock lock = new ReentrantLock();

// 非阻塞方式尝试获取锁
if (lock.tryLock()) {
    try {
        // 执行需要锁保护的代码
    } finally {
        lock.unlock();
    }
} else {
    // 未获取到锁的处理逻辑
}
```

tryLock()方法可以设置等待时间，如果在等待时间内没有获取到锁，则返回false。
例如：boolean isLocked = lock.tryLock(10, TimeUnit.SECONDS);


分布式架构下推荐使用Redisson分布式锁，因为直接使用lua脚本使用原始的redis分布式锁存，
1. 需要设计成可重入锁(同一个线程多次获取同一把锁是允许的，不会造成死锁)、
2. 需要设置自动续期、
3. 设置符合重试机制，要造各种轮子(一是死锁问题、二是锁没有自动续期机制)，而Redisson就完美解决了原始redis分布式锁的不足，它已经实现了这些功能。


Redission锁
https://zhuanlan.zhihu.com/p/654525980
最强分布式锁工具：Redisson
https://mp.weixin.qq.com/s?__biz=MzU0OTE4MzYzMw==&mid=2247545794&idx=2&sn=88a3b1c73372006b49a43a6c133a10c3&chksm=fbb1ba3cccc6332ae1f5e609ab5e37c32fe972b7ba3a18b1a92735c5f3e7c9300e2318ca2280&scene=27

Redisson使用 https://www.cnblogs.com/bu-huo/p/18597052