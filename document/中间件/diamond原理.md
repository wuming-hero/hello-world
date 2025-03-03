
## 一般使用方式 
1. 继承 DiamondDataCallback
2. 添加 注解 @DiamondListener(dataId = "dataId", groupId = "groupId")

### DiamondDataListener 类结构
```java
    public class DiamondDataListener implements ManagerListener{
        private String dataId;
        private String groupId;
        private Executor executor;
        // 继承DiamondDataCallback 的实例，用来处理回调
        private DiamondDataCallback diamondDataCallback;
    }
```

## 实现原理--启动过程

### 注册 diamond配置
1. DiamondAutoConfiguration 扫描每个Bean,判断是否有 @DiamondListener，如果有，则调用 Diamond#addListener注册DiamondDataListener实例
2. DiamondEnv 类在内存中维护了配置内容
```java

protected DiamondEnv(ServerListManager serverListMgr) {
    // 初始化服务端
    initServerManager(serverListMgr);
    
    # CacheData 为每一个diamond配置的对象, CacheData.content 为配置内容,CacheData.md5 为content MD5散件后的字符串    
    cacheMap = new AtomicReference<Map<String, CacheData>>(new HashMap<String, CacheData>());
    
    // 启动客户端线程
    worker = new ClientWorker(this);
}
```

### 内存中的配置和服务端同步

1. 客户端声明一个核心线程为1的调度线程池，然后固定第10ms调度一次服务端获取更新的diamond配置列表

2. 遍历更新后的diamond列表，通过ClientWorker#getServerConfig 调用服务端查询最新配置数据。


```java
ClientWorker(final DiamondEnv env) {
    this.env = env;
    // 1.定时任务线程池，线程数量1个
    executor = Executors.newScheduledThreadPool(1, new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setName("com.taobao.diamond.client.Worker." + env.serverMgr.name);
            t.setDaemon(true);
            return t;
        }
    });
    
    // 2.长轮询线程池：线程数量=监听的配置项数量/PER_TASK_CONFIG_SIZE，其中PER_TASK_CONFIG_SIZE默认是3000，可通过SystemProperties修改配置。也就是说每个线程最多负责3000个配置项的监听
    executorService = Executors.newCachedThreadPool(new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setName("com.taobao.diamond.client.Worker.longPulling" + env.serverMgr.name);
            t.setDaemon(true);
            return t;
        }
    });
    
    // 定时任务线程池，固定每10ms执行一次
    executor.scheduleWithFixedDelay(new Runnable() {
        public void run() {
            try {
                checkConfigInfo();
            } catch (Throwable e) {
                log.error(env.getName(), "DIAMOND-XXXX", "[sub-check] rotate check error", e);
            }
        }
    }, 1L, 10L, TimeUnit.MILLISECONDS);
}
```

## 服务端总结
* 配置数据变更时，服务端首先将其持久化进DB，然后向所有服务器发送dump消息，通知服务端更新本地磁盘的数据以及更新缓存数据（主要是MD5）。
* 服务端处理长轮询时，使用了异步Servlet来实现，保证了性能与实效性。
* 另外服务端从本地磁盘读文件后通过网络传输给客户端时，使用了零拷贝技术，提高IO性能。

