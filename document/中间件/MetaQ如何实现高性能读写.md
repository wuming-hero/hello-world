## 一、存储层优化（核心）
### 顺序写磁盘（CommitLog）
所有消息顺序追加到统一的 CommitLog 文件（类似日志结构合并树 LSM）。
顺序写磁盘速度远超随机写（SSD 顺序写可达 600MB/s，随机写仅 100MB/s）。

### 内存映射（mmap）技术
通过 MappedByteBuffer 将磁盘文件映射到内存，读写操作直接操作内存：
MappedFile mappedFile = new MappedFile(filePath, fileSize);
ByteBuffer byteBuffer = mappedFile.getMappedByteBuffer();
避免内核态与用户态数据拷贝，减少 CPU 开销。

### 页缓存（PageCache）利用
消息写入先进入 PageCache，由操作系统异步刷盘（默认异步刷盘模式）。
读取时优先从 PageCache 获取，命中则无需访问磁盘。

### 文件预分配与冷热分离
提前分配固定大小文件（默认 1GB），避免动态扩展开销。
冷数据（历史消息）与热数据（新消息）物理分离，减少磁盘寻址。

## 二、网络与线程模型

### Netty NIO 网络框架
基于 Netty 实现高并发网络通信，单机支持 10 万+ 连接。
Reactor 多线程模型分离 I/O 与业务处理。

### 线程隔离与负载均衡
// Broker 端线程池配置
SendMessageExecutor sendExecutor = new SendMessageExecutor(32, 100000); // 发送线程池
PullMessageExecutor pullExecutor = new PullMessageExecutor(32, 100000); // 拉取线程池
独立线程池处理发送/拉取请求，避免资源竞争。

## 三、零拷贝（Zero-Copy）技术

### 消费消息零拷贝
消费者拉取消息时，直接通过 FileChannel.transferTo() 将数据从磁盘文件传输到网络：
fileChannel.transferTo(position, size, socketChannel);
避免内核态到用户态的数据拷贝，降低 CPU 与内存占用。

### 索引文件轻量化
ConsumeQueue（消费队列）仅存储消息偏移量（20字节/条），内存占用小。
通过偏移量直接定位 CommitLog 中的消息。

## 四、分布式扩展性
### 分区（Queue）负载均衡
每个 Topic 拆分为多个 Queue，分散到不同 Broker。
生产/消费时自动负载均衡（默认轮询策略）。

### Broker 主从架构
主节点处理写请求，从节点异步复制数据（支持同步双写）。
读写分离：从节点可处理读请求（配置 brokerRole=SLAVE）。

## 五、异步化与批量处理
### 生产者批量发送
SendResult sendResult = producer.send(msgs); // 批量发送消息集合
单次 RPC 发送多条消息，减少网络开销。

### Broker 组提交（Group Commit）
积累多个消息写入请求后批量刷盘（默认配置 flushInterval=500ms）。

### 消费者批量拉取
consumer.setPullBatchSize(32); // 单次拉取最大消息数

## 六、性能数据参考
场景	吞吐量
* 同步刷盘	约 3万 TPS
* 异步刷盘（默认）	10万+ TPS
* 单机 Queue 数扩展	线性提升（最高支持 2.4万 Queue）

## 七、调优建议
* 启用异步刷盘
flushDiskType=ASYNC_FLUSH  # broker.conf

* 调整内存映射参数
mappedFileSizeCommitLog=1073741824  # CommitLog 文件大小（1GB）

* 增加队列数
// 创建 Topic 时指定队列数
admin.createTopic("TopicTest", 16);

* 堆外内存优化
transientStorePoolEnable=true  # 启用堆外内存缓存池

## 总结
RocketMQ 通过 顺序写 + mmap + 零拷贝 突破磁盘 I/O 瓶颈，结合分布式队列负载均衡 和 异步批量处理，实现百万级消息吞吐。
其设计哲学是：用顺序写代替随机写，用内存操作代替磁盘 I/O，用批量处理代替单次操作。

