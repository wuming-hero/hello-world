内存泄露是指：内存泄漏也称作"存储渗漏"，用动态存储分配函数动态开辟的空间，在使用完毕后未释放，结果导致一直占据该内存单元，直到程序结束。(其实说白了就是该内存空间使用完毕之后未回收)即所谓内存泄漏。

也就是说内存刚开始不会出现什么问题，但是过一段时间就会频繁的进行GC，应用程序不会有什么响应，造成服务假死的情况

## 1. 使用jstat 检查FGC状态
```bash
# 查看java进程获得进程号
jcmd 

# jstat -gc pid 间隔时间 显示次数来查看GC情况
jstat -gc 进程号 3000 30
```
示例
```bash

```

## 2. 查看战胜CPU的线程
```bash
# top命令查询进度
top

# 查看进程消耗CPU比较高的线程
top -H -p 97598

# 接下来将线程ID转换为对应的16进制进行排查
printf "%x" 97600
jstack 97598|grep 17d40 -A20
```

## 3. 查看内存镜像
可以通过jmap导出dump转储文件，但是不推荐使用，导出的时候服务将会不响应请求，如果导出的文件很大，则可能造成服务长时间假死。
推荐使用`jmap -histo pid`来查看那些对象占用的内存大
```bash
# 列出来存活对象占用的内存大小，并且只显示前20行
jmap -histo:live 97598|head -20

# 使用jmap导出内存镜像，通常来讲这个文件会比较大，一般都会有好几个G
jmap -dump:format=b,file=hprof <java进程PID>
```

## 4. 使用MAT(Memory Analyzer Tool) 分析内存镜像

MAT 下载地址 https://eclipse.dev/mat/download/

## 5. btrace跟踪具体的代码
但通常来说仅仅靠MAT可能还不能直接定位到具体应用代码中哪个部分造成的问题，例如MAT有可能看到是某个线程创建了很大的ArrayList，但这样是不足以解决问题的，所以通常还需要借助btrace来定位到具体的代码。

Btrace 地址 https://github.com/btraceio/btrace

https://blog.csdn.net/weixin_49435563/article/details/135411924

