# CP使用率高诊断

## 1. top 命令查看占用高的进程
```bash
top

top - 11:45:48 up 103 days, 17:32,  0 users,  load average: 0.43, 0.41, 0.40
Tasks:  33 total,   1 running,  31 sleeping,   0 stopped,   1 zombie
%Cpu(s):  7.8 us,  0.0 sy,  0.0 ni, 91.5 id,  0.0 wa,  0.0 hi,  0.0 si,  0.7 st
KiB Mem :  8388608 total,   843852 free,  6015668 used,  1529088 buff/cache
KiB Swap:        0 total,        0 free,        0 used.  1934520 avail Mem 

   PID USER      PR  NI    VIRT    RES    SHR S  %CPU %MEM     TIME+ COMMAND                                                                                                                                                    
  5587 admin     20   0 1534296  76140  52728 S  11.6  0.9   4:38.47 ilogtail                                                                                                                                                   
  9316 admin     20   0 9977684   5.2g  16972 S   9.3 65.3  16481:00 java      
```

## 2. 查看当前进程下占用高的线程
```bash
top -H -p <pid>
```
示例
```bash
top -H -p 9316

top - 11:46:28 up 103 days, 17:32,  0 users,  load average: 0.47, 0.42, 0.41
Threads: 572 total,   0 running, 572 sleeping,   0 stopped,   0 zombie
%Cpu(s):  7.3 us,  1.8 sy,  0.0 ni, 90.3 id,  0.1 wa,  0.0 hi,  0.0 si,  0.5 st
KiB Mem :  8388608 total,   842576 free,  6015888 used,  1530144 buff/cache
KiB Swap:        0 total,        0 free,        0 used.  1934300 avail Mem 

   PID USER      PR  NI    VIRT    RES    SHR S %CPU %MEM     TIME+ COMMAND                                                                                                                                                     
 12146 admin     20   0 9977684   5.2g  16972 S  1.7 65.3   2255:05 java                                                                                                                                                        
 12108 admin     20   0 9977684   5.2g  16972 S  1.3 65.3   2247:41 java                                                                                                                                                        
 12345 admin     20   0 9977684   5.2g  16972 S  1.3 65.3 905:16.69 java     

```

## 3. 将Linux中10进制的线程号转换16进制
因为java中查看线程号使用的是16进制我们需要将linux中的线程号转换为java中能够使用的16进制
```bash
printf "%x" <tid>
```
示例
```
[admin@track]
$printf "%x" 12146
2f72

```

## 4. 查看线程运行状态
```bash
jstack <pid> | grep <hex_tid>
# jstack默认会显示当前进程下的所有线程堆栈信息，所有我们只需要显示我们运行的消费CPU最高的代码即可，使用如下命令就可以显示当前线程后面CPU消耗很高的线程的堆栈信息
jstack <pid> | grep <hex_tid> -A20
```

```bash
示例
[admin@track /]
$/opt/taobao/java/bin/jstack 9316 | grep 2f72
"sentinel-time-tick-thread" #345 daemon prio=5 os_prio=0 tid=0x00007f39d4505800 nid=0x2f72 sleeping[0x00007f3948046000]
```


https://blog.csdn.net/weixin_49435563/article/details/135411924