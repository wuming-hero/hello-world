

## jstack 查看JVM线程快照
jstack命令可以定位线程出现长时间卡顿的原因，例如死锁，死循环
```bash
jstack [-l] <pid> (连接运行中的进程)
option参数解释:
-F 当使用jstack <pid>无响应时，强制输出线程堆栈。 
-m 同时输出java和本地堆栈(混合模式)
-l 额外显示锁信息
```

https://blog.csdn.net/weixin_49435563/article/details/135411924
https://juejin.cn/post/6844904152850497543