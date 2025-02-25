# 一、简介
变量名	变量解释
loader	本次调用类所在的 ClassLoader
clazz	本次调用类的 Class 引用
method	本次调用方法反射引用
target	本次调用类的实例
params	本次调用参数列表，这是一个数组，如果方法是无参方法则为空数组
returnObj	本次调用返回的对象。当且仅当 isReturn==true 成立时候有效，表明方法调用是以正常返回的方式结束。如果当前方法无返回值 void，则值为 null
throwExp	本次调用抛出的异常。当且仅当 isThrow==true 成立时有效，表明方法调用是以抛出异常的方式结束。
isBefore	辅助判断标记，当前的通知节点有可能是在方法一开始就通知，此时 isBefore==true 成立，同时 isThrow==false 和 isReturn==false，因为在方法刚开始时，还无法确定方法调用将会如何结束。
isThrow	辅助判断标记，当前的方法调用以抛异常的形式结束。
isReturn	辅助判断标记，当前的方法调用以正常返回的形式结束。

所有变量都可以在表达式中直接使用，如果在表达式中编写了不符合 OGNL 脚本语法或者引入了不在表格中的变量，则退出命令的执行；用户可以根据当前的异常信息修正条件表达式或观察表达式

```bash
-n 5表示打印的次数，到达指定次数后自动退出
-x 4表示参数和返回值打印时候的遍历深度
-e 表示在捕获到异常时打印
-s 表示在方法执行成功后打印
```

idea 插件  https://plugins.jetbrains.com/plugin/13581-arthas-idea?spm=ata.21736010.0.0.41c87536TJ0lkK


# 二、安装/使用
```
curl -L https://alibaba.github.io/arthas/install.sh | sh
sudo ./as.sh
```


# 三、 常用命令

## 3.1 watch 命令
>观察方法的入参、返回结果以及异常堆栈，

```bash
# 查看入参和返回值，-x 指定遍历深度，最大4
watch com.test.common.action.ConsigningAction execute '{params,returnObj}' -x 4 -s

# 查看异常堆栈
watch com.test.common.action.ConsigningAction execute '{params,returnObj,throwExp}' -x 4 -e

# -n指定次数，到达指定次数后自动退出 -x 指定遍历深度，最大4
watch com.test.common.action.ConsigningAction execute '{params,returnObj,throwExp}'  -n 5  -x 4

# 当想看的数据较深时，可以通过链式调用的方式指定相应的对象
watch com.test.common.action.ConsigningAction execute '{params[1].glocOrderDO.logisticOrderDO.locOrderDO,returnObj,throwExp}'  -n 5  -x 4

# 可以通过链式调用的方式指定要watch的请求
watch com.test.common.action.ConsigningAction execute '{params[1].glocOrderDO.logisticOrderDO.locOrderDO,returnObj,throwExp}' 'params[1].glocOrderDO.outOrderId="123232"'  -n 5  -x 4
```


## 3.2 trace 命令
> 方法内部调用路径，并输出方法路径上的每个节点上耗时

参数名称	参数说明
class-pattern	类名表达式匹配
method-pattern	方法名表达式匹配
condition-express	条件表达式
[E]	开启正则表达式匹配，默认为通配符匹配
[n:]	命令执行次数，默认值为 100。
#cost	方法执行耗时
[m <arg>]	指定 Class 最大匹配数量，默认值为 50。长格式为[maxMatch <arg>]。

注：watch/stack/trace 这个三个命令都支持#cost

### 3.2.1 -n 参数
-n参数指定捕捉结果的次数。比如下面的例子里，捕捉到一次调用就退出命令。
```bash
$ trace demo.MathGame run -n 1
Press Q or Ctrl+C to abort.
Affect(class-cnt:1 , method-cnt:1) cost in 28 ms.
`---ts=2019-12-04 00:45:08;thread_name=main;id=1;is_daemon=false;priority=5;TCCL=sun.misc.Launcher$AppClassLoader@3d4eac69
    `---[0.617465ms] demo.MathGame:run()
        `---[0.078946ms] demo.MathGame:primeFactors() #24 [throws Exception]

`---ts=2019-12-04 00:45:09;thread_name=main;id=1;is_daemon=false;priority=5;TCCL=sun.misc.Launcher$AppClassLoader@3d4eac69
    `---[1.276874ms] demo.MathGame:run()
        `---[0.03752ms] demo.MathGame:primeFactors() #24 [throws Exception]
```
结果里的 #24，表示在 run 函数里，在源文件的第24行调用了primeFactors()函数


### 3.2.2 包含 jdk 的函数
--skipJDKMethod <value> skip jdk method trace, default value true.
默认情况下，trace 不会包含 jdk 里的函数调用，如果希望 trace jdk 里的函数，需要显式设置--skipJDKMethod false。
```bash
$ trace --skipJDKMethod false demo.MathGame run
Press Q or Ctrl+C to abort.
Affect(class-cnt:1 , method-cnt:1) cost in 60 ms.
`---ts=2019-12-04 00:44:41;thread_name=main;id=1;is_daemon=false;priority=5;TCCL=sun.misc.Launcher$AppClassLoader@3d4eac69
    `---[1.357742ms] demo.MathGame:run()
        +---[0.028624ms] java.util.Random:nextInt() #23
        +---[0.045534ms] demo.MathGame:primeFactors() #24 [throws Exception]
        +---[0.005372ms] java.lang.StringBuilder:<init>() #28
        +---[0.012257ms] java.lang.Integer:valueOf() #28
        +---[0.234537ms] java.lang.String:format() #28
        +---[min=0.004539ms,max=0.005778ms,total=0.010317ms,count=2] java.lang.StringBuilder:append() #28
        +---[0.013777ms] java.lang.Exception:getMessage() #28
        +---[0.004935ms] java.lang.StringBuilder:toString() #28
        `---[0.06941ms] java.io.PrintStream:println() #28

`---ts=2019-12-04 00:44:42;thread_name=main;id=1;is_daemon=false;priority=5;TCCL=sun.misc.Launcher$AppClassLoader@3d4eac69
    `---[3.030432ms] demo.MathGame:run()
        +---[0.010473ms] java.util.Random:nextInt() #23
        +---[0.023715ms] demo.MathGame:primeFactors() #24 [throws Exception]
        +---[0.005198ms] java.lang.StringBuilder:<init>() #28
        +---[0.006405ms] java.lang.Integer:valueOf() #28
        +---[0.178583ms] java.lang.String:format() #28
        +---[min=0.011636ms,max=0.838077ms,total=0.849713ms,count=2] java.lang.StringBuilder:append() #28
        +---[0.008747ms] java.lang.Exception:getMessage() #28
        +---[0.019768ms] java.lang.StringBuilder:toString() #28
        `---[0.076457ms] java.io.PrintStream:println() #28
```

### 3.2.3 根据调用耗时过滤
只会展示耗时大于 10ms 的调用路径，有助于在排查问题的时候，只关注异常情况
```bash
$ trace demo.MathGame run '#cost > 10'
Press Ctrl+C to abort.
Affect(class-cnt:1 , method-cnt:1) cost in 41 ms.
`---ts=2018-12-04 01:12:02;thread_name=main;id=1;is_daemon=false;priority=5;TCCL=sun.misc.Launcher$AppClassLoader@3d4eac69
    `---[12.033735ms] demo.MathGame:run()
        +---[0.006783ms] java.util.Random:nextInt()
        +---[11.852594ms] demo.MathGame:primeFactors()
        `---[0.05447ms] demo.MathGame:print()
```

### 3.2.4 trace 多个类或者多个函数
trace 命令只会 trace 匹配到的函数里的子调用，并不会向下 trace 多层。因为 trace 是代价比较贵的，多层 trace 可能会导致最终要 trace 的类和函数非常多。
可以用正则表匹配路径上的多个类和函数，一定程度上达到多层 trace 的效果。
```bash
# 用正则表匹配路径上的多个类和函数，一定程度上达到多层 trace 的效果
trace -E com.test.ClassA|org.test.ClassB method1|method2|method3

# 排除掉指定的类
trace javax.servlet.Filter * --exclude-class-pattern com.demo.TestFilter
```


### 3.2.5 动态trace
> 3.3.0 版本后支持。
打开终端 1，trace 上面 demo 里的run函数，可以看到打印出 listenerId: 1：
```bash
[arthas@59161]$ trace demo.MathGame run
Press Q or Ctrl+C to abort.
Affect(class count: 1 , method count: 1) cost in 112 ms, listenerId: 1
`---ts=2020-07-09 16:48:11;thread_name=main;id=1;is_daemon=false;priority=5;TCCL=sun.misc.Launcher$AppClassLoader@3d4eac69
`---[1.389634ms] demo.MathGame:run()
`---[0.123934ms] demo.MathGame:primeFactors() #24 [throws Exception]

`---ts=2020-07-09 16:48:12;thread_name=main;id=1;is_daemon=false;priority=5;TCCL=sun.misc.Launcher$AppClassLoader@3d4eac69
`---[3.716391ms] demo.MathGame:run()
+---[3.182813ms] demo.MathGame:primeFactors() #24
`---[0.167786ms] demo.MathGame:print() #25
```

现在想要深入子函数primeFactors，可以打开一个新终端 2，使用telnet localhost 3658连接上 arthas，再 trace primeFactors时，指定listenerId。

```bash
[arthas@59161]$ trace demo.MathGame primeFactors --listenerId 1
Press Q or Ctrl+C to abort.
Affect(class count: 1 , method count: 1) cost in 34 ms, listenerId: 1
```

这时终端 2 打印的结果，说明已经增强了一个函数：Affect(class count: 1 , method count: 1)，但不再打印更多的结果。

再查看终端 1，可以发现 trace 的结果增加了一层，打印了primeFactors函数里的内容：

```bash
`---ts=2020-07-09 16:49:29;thread_name=main;id=1;is_daemon=false;priority=5;TCCL=sun.misc.Launcher$AppClassLoader@3d4eac69
`---[0.492551ms] demo.MathGame:run()
`---[0.113929ms] demo.MathGame:primeFactors() #24 [throws Exception]
`---[0.061462ms] demo.MathGame:primeFactors()
`---[0.001018ms] throw:java.lang.IllegalArgumentException() #46

`---ts=2020-07-09 16:49:30;thread_name=main;id=1;is_daemon=false;priority=5;TCCL=sun.misc.Launcher$AppClassLoader@3d4eac69
`---[0.409446ms] demo.MathGame:run()
+---[0.232606ms] demo.MathGame:primeFactors() #24
|   `---[0.1294ms] demo.MathGame:primeFactors()
`---[0.084025ms] demo.MathGame:print() #25
```

通过指定listenerId的方式动态 trace，可以不断深入。另外 watch/tt/monitor等命令也支持类似的功能。


## 3.3 stack
> 输出当前方法被调用的调用路径
很多时候我们都知道一个方法被执行，但这个方法被执行的路径非常多，或者你根本就不知道这个方法是从那里被执行了，此时你需要的是 stack 命令。

参数说明
参数名称	参数说明
class-pattern	类名表达式匹配
method-pattern	方法名表达式匹配
condition-express	条件表达式
[E]	开启正则表达式匹配，默认为通配符匹配
[n:]	执行次数限制
[m <arg>]	指定 Class 最大匹配数量，默认值为 50。长格式为[maxMatch <arg>]。

这里重点要说明的是观察表达式，观察表达式的构成主要由 ognl 表达式组成，所以你可以这样写"{params,returnObj}"，只要是一个合法的 ognl 表达式，都能被正常支持。
请参考表达式核心变量中关于该节点的描述。
特殊用法请参考：https://github.com/alibaba/arthas/issues/71
OGNL 表达式官网：https://commons.apache.org/dormant/commons-ognl/language-guide.html


### 3.3.1  stack

```bash
$ stack demo.MathGame primeFactors
Press Ctrl+C to abort.
Affect(class-cnt:1 , method-cnt:1) cost in 36 ms.
ts=2018-12-04 01:32:19;thread_name=main;id=1;is_daemon=false;priority=5;TCCL=sun.misc.Launcher$AppClassLoader@3d4eac69
@demo.MathGame.run()
at demo.MathGame.main(MathGame.java:16)
```
### 3.3.2 指定 Class 最大匹配数量
```bash
$ stack demo.MathGame primeFactors -m 1
Press Q or Ctrl+C to abort.
Affect(class count:1 , method count:1) cost in 561 ms, listenerId: 5.
ts=2022-12-25 21:07:07;thread_name=main;id=1;is_daemon=false;priority=5;TCCL=sun.misc.Launcher$AppClassLoader@b4aac2
@demo.MathGame.primeFactors()
at demo.MathGame.run(MathGame.java:46)
at demo.MathGame.main(MathGame.java:38)
```

### 3.3.3 据条件表达式来过滤
```bash
$ stack demo.MathGame primeFactors 'params[0]<0' -n 2
Press Ctrl+C to abort.
Affect(class-cnt:1 , method-cnt:1) cost in 30 ms.
ts=2018-12-04 01:34:27;thread_name=main;id=1;is_daemon=false;priority=5;TCCL=sun.misc.Launcher$AppClassLoader@3d4eac69
@demo.MathGame.run()
at demo.MathGame.main(MathGame.java:16)

ts=2018-12-04 01:34:30;thread_name=main;id=1;is_daemon=false;priority=5;TCCL=sun.misc.Launcher$AppClassLoader@3d4eac69
@demo.MathGame.run()
at demo.MathGame.main(MathGame.java:16)
```

Command execution times exceed limit: 2, so command will exit. You can set it with -n option.

### 3.3.4 据执行时间来过滤
```bash
$ stack demo.MathGame primeFactors '#cost>5'
Press Ctrl+C to abort.
Affect(class-cnt:1 , method-cnt:1) cost in 35 ms.
ts=2018-12-04 01:35:58;thread_name=main;id=1;is_daemon=false;priority=5;TCCL=sun.misc.Launcher$AppClassLoader@3d4eac69
@demo.MathGame.run()
at demo.MathGame.main(MathGame.java:16)
```

## 3.4 monitor
> 方法执行监控
对匹配 class-pattern／method-pattern／condition-express的类、方法的调用进行监控。

monitor 命令是一个非实时返回命令.

实时返回命令是输入之后立即返回，而非实时返回的命令，则是不断的等待目标 Java 进程返回信息，直到用户输入 Ctrl+C 为止。

服务端是以任务的形式在后台跑任务，植入的代码随着任务的中止而不会被执行，所以任务关闭后，不会对原有性能产生太大影响，而且原则上，任何 Arthas 命令不会引起原有业务逻辑的改变。

监控的维度说明
监控项	说明
timestamp	时间戳
class	Java 类
method	方法（构造方法、普通方法）
total	调用次数
success	成功次数
fail	失败次数
rt	平均 RT
fail-rate	失败率
参数说明
方法拥有一个命名参数 [c:]，意思是统计周期（cycle of output），拥有一个整型的参数值

参数名称	参数说明
class-pattern	类名表达式匹配
method-pattern	方法名表达式匹配
condition-express	条件表达式
[E]	开启正则表达式匹配，默认为通配符匹配
[c:]	统计周期，默认值为 120 秒
[b]	在方法调用之前计算 condition-express
[m <arg>]	指定 Class 最大匹配数量，默认值为 50。长格式为[maxMatch <arg>]。

### 3.4.1 使用参考
```bash
$ monitor -c 5 demo.MathGame primeFactors
Press Ctrl+C to abort.
Affect(class-cnt:1 , method-cnt:1) cost in 94 ms.
timestamp            class          method        total  success  fail  avg-rt(ms)  fail-rate
-----------------------------------------------------------------------------------------------
2018-12-03 19:06:38  demo.MathGame  primeFactors  5      1        4     1.15        80.00%

timestamp            class          method        total  success  fail  avg-rt(ms)  fail-rate
-----------------------------------------------------------------------------------------------
2018-12-03 19:06:43  demo.MathGame  primeFactors  5      3        2     42.29       40.00%

timestamp            class          method        total  success  fail  avg-rt(ms)  fail-rate
-----------------------------------------------------------------------------------------------
2018-12-03 19:06:48  demo.MathGame  primeFactors  5      3        2     67.92       40.00%

timestamp            class          method        total  success  fail  avg-rt(ms)  fail-rate
-----------------------------------------------------------------------------------------------
2018-12-03 19:06:53  demo.MathGame  primeFactors  5      2        3     0.25        60.00%

timestamp            class          method        total  success  fail  avg-rt(ms)  fail-rate
-----------------------------------------------------------------------------------------------
2018-12-03 19:06:58  demo.MathGame  primeFactors  1      1        0     0.45        0.00%

timestamp            class          method        total  success  fail  avg-rt(ms)  fail-rate
-----------------------------------------------------------------------------------------------
2018-12-03 19:07:03  demo.MathGame  primeFactors  2      2        0     3182.72     0.00%
```

### 3.4.2 指定 Class 最大匹配数量
```bash
$ monitor -c 1 -m 1 demo.MathGame primeFactors
Press Q or Ctrl+C to abort.
Affect(class count:1 , method count:1) cost in 384 ms, listenerId: 6.
timestamp            class          method        total  success  fail  avg-rt(ms)  fail-rate
-----------------------------------------------------------------------------------------------
2022-12-25 21:12:58  demo.MathGame  primeFactors  1      1        0     0.18        0.00%

timestamp            class          method        total  success  fail  avg-rt(ms)  fail-rate
-----------------------------------------------------------------------------------------------
2022-12-25 21:12:59  demo.MathGame  primeFactors  0      0        0     0.00       0.00%
```

### 3.4.3 计算条件表达式过滤统计结果(方法执行完毕之后)
```bash
monitor -c 5 demo.MathGame primeFactors "params[0] <= 2"
Press Q or Ctrl+C to abort.
Affect(class count: 1 , method count: 1) cost in 19 ms, listenerId: 5
timestamp            class          method         total  success  fail  avg-rt(ms)  fail-rate
-----------------------------------------------------------------------------------------------
2020-09-02 09:42:36  demo.MathGame  primeFactors    5       3       2      0.09       40.00%

timestamp            class          method         total  success  fail  avg-rt(ms)  fail-rate
----------------------------------------------------------------------------------------------
2020-09-02 09:42:41  demo.MathGame  primeFactors    5       2       3      0.11       60.00%

timestamp            class          method         total  success  fail  avg-rt(ms)  fail-rate
----------------------------------------------------------------------------------------------
2020-09-02 09:42:46  demo.MathGame  primeFactors    5       1       4      0.06       80.00%

timestamp            class          method         total  success  fail  avg-rt(ms)  fail-rate
----------------------------------------------------------------------------------------------
2020-09-02 09:42:51  demo.MathGame  primeFactors    5       1       4      0.12       80.00%

timestamp            class          method         total  success  fail  avg-rt(ms)  fail-rate
----------------------------------------------------------------------------------------------
2020-09-02 09:42:56  demo.MathGame  primeFactors    5       3       2      0.15       40.00%
```

### 3.4.4 计算条件表达式过滤统计结果(方法执行完毕之前)
```bash
monitor -b -c 5 com.test.testes.MathGame primeFactors "params[0] <= 2"
Press Q or Ctrl+C to abort.
Affect(class count: 1 , method count: 1) cost in 21 ms, listenerId: 4
timestamp            class          method         total  success  fail  avg-rt(ms)  fail-rate
----------------------------------------------------------------------------------------------
2020-09-02 09:41:57  demo.MathGame  primeFactors    1       0        1      0.10      100.00%

timestamp            class          method         total  success  fail  avg-rt(ms)  fail-rate
----------------------------------------------------------------------------------------------
2020-09-02 09:42:02  demo.MathGame  primeFactors    3       0        3      0.06      100.00%

timestamp            class          method         total  success  fail  avg-rt(ms)  fail-rate
----------------------------------------------------------------------------------------------
2020-09-02 09:42:07  demo.MathGame  primeFactors    2       0        2      0.06      100.00%

timestamp            class          method         total  success  fail  avg-rt(ms)  fail-rate
----------------------------------------------------------------------------------------------
2020-09-02 09:42:12  demo.MathGame  primeFactors    1       0        1      0.05      100.00%

timestamp            class          method         total  success  fail  avg-rt(ms)  fail-rate
----------------------------------------------------------------------------------------------
2020-09-02 09:42:17  demo.MathGame  primeFactors    2       0        2      0.10      100.00%
```

