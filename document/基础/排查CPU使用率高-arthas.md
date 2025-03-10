# arthas

## 1.运行arthas
```
java -jar arthas-boot.jar
```
## 2. 使用dashboard来查看全局的性能监控
```bash
dashboard

```

## 3. 使用thread查看具体线程内容
```bash
thread 343

```

## 4. 使用jad反编译代码
```bash
$ jad com.alibaba.csp.sentinel.util.TimeUtil

ClassLoader:                                                                                                                                                          
+-sentinel's ModuleClassLoader                                                                                                                                        

Location:                                                                                                                                                             
/home/admin/track/.default/deploy/taobao-hsf.sar/plugins/sentinel/lib/sentinel-core-3.9.33-fix-context.jar                                                            

       /*
        * Decompiled with CFR.
        */
       package com.alibaba.csp.sentinel.util;
       
       import java.util.concurrent.TimeUnit;
       
       public final class TimeUtil {
           public static final String USE_CACHED_TIME_PROP_KEY = "sentinel.useCachedTime";
           private static volatile long currentTimeMillis;
           private static boolean useCachedTime;
       
           public static long currentTimeMillis() {
/*70*/         if (!useCachedTime) {
/*71*/             return System.currentTimeMillis();
               }
/*73*/         return currentTimeMillis;
           }
       
           static {
/*35*/         useCachedTime = true;
               try {
/*39*/             String v = System.getProperty(USE_CACHED_TIME_PROP_KEY);
/*40*/             if ("false".equalsIgnoreCase(v)) {
/*41*/                 useCachedTime = false;
                   }
/*44*/             if (useCachedTime) {
/*45*/                 currentTimeMillis = System.currentTimeMillis();
                       Thread daemon = new Thread(new Runnable(){
       
                           @Override
                           public void run() {
                               while (true) {
/*50*/                             currentTimeMillis = System.currentTimeMillis();
                                   try {
/*52*/                                 TimeUnit.MILLISECONDS.sleep(1L);
                                   }
                                   catch (Throwable throwable) {
                                   }
                               }
                           }
                       });
/*59*/                 daemon.setDaemon(true);
/*60*/                 daemon.setName("sentinel-time-tick-thread");
/*61*/                 daemon.start();
                   }
               }
               catch (Throwable t) {
/*64*/             System.err.println("Failed to initialize Sentinel TimeUtil tick thread");
/*65*/             t.printStackTrace();
               }
           }
       }
```
### 4.1 使用jad反编译出源码&&导出到文件
```bash
jad --source-only com.alibaba.csp.sentinel.util.TimeUtil > /tmp/TimeUtil.java
```

## 5. 使用vi命令修改源代码

## 6. 热编译生效改动

### 6.1 我们需要用替换类的类加载器对于我们修改后的原代码进行编译
```bash
$ sc -d com.alibaba.csp.sentinel.util.TimeUtil | grep classLoaderHash
 classLoaderHash   b63a1dc
```

### 6.2 通过类加载器将我们的类进行编译
```bash
mc -c b63a1dc /tmp/TimeUtil.java -d /tmp
```

### 6.3 redefine 重新加载新类
编译后就可以在不重启的情况下加载新类，使用redefine命令重新加载新编译好的 TimeUtil.class

```bash
redefine /tmp/com/heima/test/handler/TimeUtil.class

```
注：redefine的局限
1. 不允许新增加 field/method
2. 正在跑的函数，没有退出不能生效，比如while(true)方法内，上面代码lmada表达式内的不会生效




https://blog.csdn.net/weixin_49435563/article/details/135411924
