## jstat
(JVM statistics Monitoring)是用于监视虚拟机运行时状态信息的命令，它可以显示出虚拟机进程中的类装载、内存、垃圾收集、JIT编译等运行数据。

### 显示gc的信息，查看gc的次数，及时间
```bash
jstat -gc pid [interval] [count]

具体列名	具体描述
S0C	第一个幸存区的大小
S1C	第二个幸存区的大小
S0U	第一个幸存区的使用大小
S1U	第二个幸存区的使用大小
EC	伊甸园区的大小
EU	伊甸园区的使用大小
OC	老年代大小
OU	老年代使用大小
MC	方法区大小
MU	方法区使用大小
CCSC:压缩类空间大小
CCSU:压缩类空间使用大小
YGC	年轻代垃圾回收次数
YGCT	年轻代垃圾回收消耗时间
FGC	老年代垃圾回收次数
FGCT	老年代垃圾回收消耗时间
GCT	垃圾回收消耗总时间
```

### 老年代垃圾回收统计
```bash
# 可以查看老年代的垃圾回收统计，具体列描述如下
jstat -gcold pid [interval] [count]

具体列名	具体描述
MC	方法区大小
MU	方法区使用大小
CCSC	压缩类空间大小
CCSU	压缩类空间使用大小
OC	老年代大小
OU	老年代使用大小
YGC	年轻代垃圾回收次数
FGC	老年代垃圾回收次数
GCT	垃圾回收消耗总时间
```

### 整体垃圾回收统计
```bash
jstat -gcutil pid [interval] [count]

具体列名	具体描述
S0	第一个幸存区的使用大小
S1	第二个幸存区的使用大小
EU	伊甸园区的使用大小
OU	老年代使用大小
MU	方法区使用大小
CCSU	压缩类空间使用大小
YGC	年轻代垃圾回收次数
YGCT	年轻代垃圾回收消耗时间
FGC	老年代垃圾回收次数
FGCT	老年代垃圾回收消耗时间
GCT	垃圾回收消耗总时间
```