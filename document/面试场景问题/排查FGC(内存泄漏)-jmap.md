内存泄露是指：内存泄漏也称作"存储渗漏"，用动态存储分配函数动态开辟的空间，在使用完毕后未释放，结果导致一直占据该内存单元，直到程序结束。(其实说白了就是该内存空间使用完毕之后未回收)即所谓内存泄漏。

也就是说内存刚开始不会出现什么问题，但是过一段时间就会频繁的进行FGC，应用程序不会有什么响应，造成服务假死的情况

## 查看java进程号
```bash
ps ax|grep java

# 可以看到以下2行,其中 进度ID=1即为java项目启动的进程号
 1 ?        Ssl  6432:50 java -Xms3072m -Xmx3072m -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:-OmitStackTraceInFastThrow -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/data/tmp/heapdump_20260306_063829_phone-speech-server-face-774588bc6c-rnczv.hprof -DSERVER_IP=10.15.22.198 -DSERVER_NAME=pro-com-kubernetes-ntdingtone-workera3 -Dspring.cloud.zookeeper.discovery.instance-host=speech-server.aicalltext.com -Dxxl.job.executor.ip=speech-server.aicalltext.com -XX:ErrorFile=/data/tmp/hs_err_pid_20260306_063829_phone-speech-server-face-774588bc6c-rnczv.log -jar phone-speech-server.jar --app.id=phone-speech-server --apollo.meta=http://10.1.10.121:8080 --apollo.bootstrap.enabled=true --apollo.cluster=docker --apollo.bootstrap.namespaces=application,dingtone.spring-common-v2 --spring.profiles.active=pn1 --spring.cloud.zookeeper.discovery.metadata.apiVersion=face
 398223 pts/0    S+     0:00 grep java
```
## 1. 使用jstat 检查FGC状态
```bash
# 查看java进程获得进程号
jcmd 

# jstat -gc pid 间隔时间 显示次数来查看GC情况
jstat -gc 进程号 3000 30

# 示例如下 
 S0C    S1C      S0U    S1U      EC       EU        OC         OU        MC       MU       CCSC    CCSU     YGC   YGCT      FGC     FGCT  GCT   
 0.0   100352.0  0.0   100352.0 1818624.0 230400.0 1226752.0   869182.8  177368.0 166700.1 19692.0 17932.0  20643 1186.555   4      5.059 1191.614
 0.0   100352.0  0.0   100352.0 1818624.0 477184.0 1226752.0   869807.8  177368.0 166700.1 19692.0 17932.0  20643 1186.555   4      5.059 1191.614
 0.0   100352.0  0.0   100352.0 1818624.0 717824.0 1226752.0   872319.4  177368.0 166700.2 19692.0 17932.0  20643 1186.555   4      5.059 1191.614
 0.0   100352.0  0.0   100352.0 1818624.0 933888.0 1226752.0   872319.4  177368.0 166700.2 19692.0 17932.0  20643 1186.555   4      5.059 1191.614
 0.0   100352.0  0.0   100352.0 1818624.0 1129472.0 1226752.0   872319.4  177368.0 166700.2 19692.0 17932.0  20643 1186.555   4      5.059 1191.614
 0.0   100352.0  0.0   100352.0 1818624.0 1320960.0 1226752.0   872319.4  177368.0 166700.2 19692.0 17932.0  20643 1186.555   4      5.059 1191.614
 0.0   100352.0  0.0   100352.0 1818624.0 1531904.0 1226752.0   872944.5  177368.0 166700.2 19692.0 17932.0  20643 1186.555   4      5.059 1191.614
 0.0   100352.0  0.0   100352.0 1818624.0 1726464.0 1226752.0   873569.5  177368.0 166700.2 19692.0 17932.0  20644 1186.555   4      5.059 1191.614
 0.0   87040.0  0.0   87040.0 1845248.0 217088.0 1213440.0   869022.8  177368.0 166700.2 19692.0 17932.0  20644 1186.577   4      5.059 1191.636
 0.0   87040.0  0.0   87040.0 1845248.0 419840.0 1213440.0   869022.8  177368.0 166700.2 19692.0 17932.0  20644 1186.577   4      5.059 1191.636
 0.0   87040.0  0.0   87040.0 1845248.0 648192.0 1213440.0   869022.8  177368.0 166700.2 19692.0 17932.0  20644 1186.577   4      5.059 1191.636
 ...
 ```

## 2. 查看占用CPU高的线程
```bash
# 1. top命令查看占用CPU高的进程
top

top - 03:06:16 up 120 days, 17:49,  0 users,  load average: 1.03, 1.85, 2.51
Tasks:   6 total,   1 running,   5 sleeping,   0 stopped,   0 zombie
%Cpu(s): 11.6 us,  4.5 sy,  0.0 ni, 83.3 id,  0.0 wa,  0.0 hi,  0.5 si,  0.0 st
MiB Mem :  31722.8 total,    650.6 free,  25997.1 used,   5075.2 buff/cache
MiB Swap:      0.0 total,      0.0 free,      0.0 used.   5222.3 avail Mem 

    PID USER      PR  NI    VIRT    RES    SHR S  %CPU  %MEM     TIME+ COMMAND                                                                                                                 
      1 root      20   0   27.4g   6.8g  64040 S  58.7  21.8   6452:10 java                                                                                                                    
 393940 root      20   0    2900   1576   1576 S   0.0   0.0   0:00.00 sh                                                                                                                      
 393946 root      20   0    2900   1636   1636 S   0.0   0.0   0:00.00 sh                                                                                                                      
 394083 root      20   0    2900   1776   1776 S   0.0   0.0   0:00.00 sh                                                                                                                      
 394089 root      20   0    2900   1760   1632 S   0.0   0.0   0:00.00 sh                                                                                                                      
 399809 root      20   0    7744   3380   2868 R   0.0   0.0   0:00.07 top 

# 2. 查看进程里消耗CPU比较高的线程
top -H -p 1

# 进程ID=1下边的线程
top - 06:19:16 up 120 days, 21:02,  0 users,  load average: 1.36, 1.79, 1.65
Threads: 2557 total,   0 running, 2557 sleeping,   0 stopped,   0 zombie
%Cpu(s):  8.7 us,  4.1 sy,  0.0 ni, 86.9 id,  0.0 wa,  0.0 hi,  0.3 si,  0.0 st
MiB Mem :  31722.8 total,    672.3 free,  26062.7 used,   4987.8 buff/cache
MiB Swap:      0.0 total,      0.0 free,      0.0 used.   5156.7 avail Mem 

    PID USER      PR  NI    VIRT    RES    SHR S  %CPU  %MEM     TIME+ COMMAND                                                                                                                 
 407079 root      20   0   27.6g   6.8g  64040 S   5.2  21.9   0:37.59 RtcAudioCallbac                                                                                                         
     96 root      20   0   27.6g   6.8g  64040 S   3.6  21.9 420:40.09 tokio-runtime-w                                                                                                         
     97 root      20   0   27.6g   6.8g  64040 S   3.3  21.9 419:32.41 tokio-runtime-w                                                                                                         
 407068 root      20   0   27.6g   6.8g  64040 S   2.9  21.9   0:19.63 AudioEncoder                                                                                                            
 407076 root      20   0   27.6g   6.8g  64040 S   2.6  21.9   0:20.32 AudioEncoder                                                                                                            
 407788 root      20   0   27.6g   6.8g  64040 S   2.6  21.9   0:05.82 AudioEncoder                                                                                                            
 407794 root      20   0   27.6g   6.8g  64040 S   2.6  21.9   0:05.78 AudioEncoder                                                                                                            
    765 root      20   0   27.6g   6.8g  64040 S   2.0  21.9 170:01.19 network_thread                                                                                                          
    766 root      20   0   27.6g   6.8g  64040 S   2.0  21.9 135:10.97 worker_thread 0                                                                                                         
    769 root      20   0   27.6g   6.8g  64040 S   1.0  21.9  95:12.94 AudioDevice                                                                                                             
 407114 root      20   0   27.6g   6.8g  64040 S   1.0  21.9   0:05.00 pool-11-thread-                                                                                                         
    304 root      20   0   27.6g   6.8g  64040 S   0.7  21.9  60:15.70 WritePcmFile-2

# 3. 接下来将线程ID转换为对应的16进制进行排查
printf "%x" 407079
# 转换为十六进制结果
63627

# 4. 查看指定进程ID=1下,指定线程id=407079(十进制407079转换为十六进制=0x63627)堆栈，列出来前20行
jstack 1 | grep "nid=0x63627" -A20

"RtcAudioCallback-User-786704480615786902-222000" #139626806 prio=5 os_prio=0 tid=0x00007ccba7f90000 nid=0x63627 waiting on condition [0x00007ccf0ad8f000]
   java.lang.Thread.State: WAITING (parking)
        at sun.misc.Unsafe.park(Native Method)
        - parking to wait for  <0x000000071a92c5c8> (a java.util.concurrent.locks.AbstractQueuedSynchronizer$ConditionObject)
        at java.util.concurrent.locks.LockSupport.park(LockSupport.java:175)
        at java.util.concurrent.locks.AbstractQueuedSynchronizer$ConditionObject.await(AbstractQueuedSynchronizer.java:2039)
        at java.util.concurrent.LinkedBlockingQueue.take(LinkedBlockingQueue.java:442)
        at java.util.concurrent.ThreadPoolExecutor.getTask(ThreadPoolExecutor.java:1074)
        at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1134)
        at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
        at java.lang.Thread.run(Thread.java:750)
```

## 3. 查看内存镜像
可以通过jmap导出dump转储文件，但是不推荐使用，导出的时候服务将会不响应请求，如果导出的文件很大，则可能造成服务长时间假死。
推荐使用`jmap -histo pid`来查看那些对象占用的内存大
```bash
# 列出来存活对象占用的内存大小，并且只显示前20行
jmap -histo:live 97598|head -20

# 使用jmap导出内存镜像，通常来讲这个文件会比较大，一般都会有好几个G
jmap -dump:format=b,file=hprof <java进程PID>

# 手动触发指定进程ID=1的dump
jmap -dump:format=b,file=/data/tmp/heapdump_manual_$(date +%Y%m%d_%H%M%S).hprof 1
```

## 4. 使用MAT(Memory Analyzer Tool) 分析内存镜像

MAT 下载地址 https://eclipse.dev/mat/download/previous/
1.11.0(2020.12) 支持JDK1.8
1.12/13.0(2022.6) 需要JDk 11 及以上
1.14/15.0(2023.12) 需要JDK 17 及以上
1.16.0(2024.12) 需要JDK 21 及以上


VM 内存分析工具 MAT-入门篇 https://zhuanlan.zhihu.com/p/713927690
MAT DUMP分析 https://donglin.blog.csdn.net/article/details/155240467
MAT内存分析工具使用 https://cloud.tencent.com/developer/article/1131627
JVM性能调优工具 https://blog.csdn.net/weixin_49435563/article/details/135411924

## 5. btrace跟踪具体的代码
但通常来说仅仅靠MAT可能还不能直接定位到具体应用代码中哪个部分造成的问题，例如MAT有可能看到是某个线程创建了很大的ArrayList，
但这样是不足以解决问题的，所以通常还需要借助btrace来定位到具体的代码。

Btrace 地址 https://github.com/btraceio/btrace

https://blog.csdn.net/weixin_49435563/article/details/135411924

