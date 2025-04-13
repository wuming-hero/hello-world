
## SpringBoot的启动过程：为什么springBoot打完包之后的jar/war应用，可以直接-java启动而不需要再配置一个WebServer？
一句话回答：内部带有tomcat，启动时并不执行我们应用的main函数，而是插件生成包的JarLauncher的main函数，会生成对应的tomcat。细节如下

1. 打完结果包含两个：
   * demo-0.0.1-SNAPSHOT.jar ：springboot maven插件生成的jar包，包含应用的依赖及springboot相关的类
   * demo-0.0.1-SNAPSHOT.jar.original：默认maven-jar-plguin生成的包
2. -java demo-0.0.1-SNAPSHOT.jar ：调用的main函数是JarLauncher
3. JarLauncher：
   a. 创建一个类加载器LaunchedURLClassLoader
   b. 使用这个类加载器来加载demo-0.0.1-SNAPSHOT.jar/lib下面类
   ⅰ. 之后会通过查找servlet来判断是否在web环境
   ⅱ. 然后利用工厂创建启动Embead Tomcat，初始化servlet，加载相应的资源jsp、html
   c. 同时启动一个新线程，执行应用的main函数

## SpringBoot目录结构
maven打包之后，会生成两个jar文件：
1. demo-0.0.1-SNAPSHOT.jar（spring boot maven插件生成的jar包，里面包含了应用的依赖，以及spring boot相关的类。下面称之为fat jar。）
2. demo-0.0.1-SNAPSHOT.jar.original （默认的maven-jar-plugin生成的包）


先来查看spring boot打好的包的目录结构（不重要的省略掉）：
```
├── META-INF
│   ├── MANIFEST.MF
├── application.properties
├── com
│   └── example
│       └── SpringBootDemoApplication.class
├── lib
│   ├── aopalliance-1.0.jar
│   ├── spring-beans-4.2.3.RELEASE.jar
│   ├── ...
└── org
    └── springframework
        └── boot
            └── loader
                ├── ExecutableArchiveLauncher.class
                ├── JarLauncher.class
                ├── JavaAgentDetector.class
                ├── LaunchedURLClassLoader.class
                ├── Launcher.class
                ├── MainMethodRunner.class
                ├── ...                
```

### MANIFEST.MF
```
Manifest-Version: 1.0
Start-Class: com.example.SpringBootDemoApplication
Implementation-Vendor-Id: com.example
Spring-Boot-Version: 1.3.0.RELEASE
Created-By: Apache Maven 3.3.3
Build-Jdk: 1.8.0_60
Implementation-Vendor: Pivotal Software, Inc.
Main-Class: org.springframework.boot.loader.JarLauncher
```
可以看到有Main-Class是org.springframework.boot.loader.JarLauncher ，这个是jar启动的Main函数。
还有一个Start-Class是com.example.SpringBootDemoApplication，这个是我们应用自己的Main函数。

```java
@SpringBootApplication
public class SpringBootDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootDemoApplication.class, args);
    }
}
```

### com/example 目录
这下面放的是应用的.class文件。

### lib目录
这里存放的是应用的Maven依赖的jar包文件。
比如spring-beans，spring-mvc等jar。

### org/springframework/boot/loader 目录
这下面存放的是Spring boot loader的.class文件。

##  Archive的概念
* archive即归档文件，这个概念在linux下比较常见
* 通常就是一个tar/zip格式的压缩包
* jar是zip格式

在spring boot里，抽象出了Archive的概念，一个archive可以是一个jar（JarFileArchive），也可以是一个文件目录（ExplodedArchive）。可以理解为Spring boot抽象出来的统一访问资源的层。
上面的demo-0.0.1-SNAPSHOT.jar 是一个Archive，然后demo-0.0.1-SNAPSHOT.jar里的/lib目录下面的每一个Jar包，也是一个Archive。
```java
public abstract class Archive {
    public abstract URL getUrl();
    public String getMainClass();
    public abstract Collection<Entry> getEntries();
    public abstract List<Archive> getNestedArchives(EntryFilter filter);
```

可以看到Archive有一个自己的URL，比如上面的fat jar的URL是：`jar:file:/tmp/target/demo-0.0.1-SNAPSHOT.jar!/`
还有一个getNestedArchives函数，这个实际返回的是demo-0.0.1-SNAPSHOT.jar/lib下面的jar的Archive列表。它们的URL是：
```bash
jar:file:/tmp/target/demo-0.0.1-SNAPSHOT.jar!/lib/aopalliance-1.0.jar
jar:file:/tmp/target/demo-0.0.1-SNAPSHOT.jar!/lib/spring-beans-4.2.3.RELEASE.jar
```

## JarLauncher
从MANIFEST.MF可以看到Main函数是JarLauncher，下面来分析它的工作流程。

JarLauncher类的继承结构是：

class JarLauncher extends ExecutableArchiveLauncher
class ExecutableArchiveLauncher extends Launcher

### 以demo-0.0.1-SNAPSHOT.jar创建一个Archive：
JarLauncher先找到自己所在的jar，即demo-0.0.1-SNAPSHOT.jar的路径，然后创建了一个Archive。

下面的代码展示了如何从一个类找到加载它的位置的技巧：
```java
protected final Archive createArchive() throws Exception {
     ProtectionDomain protectionDomain = getClass().getProtectionDomain();
     CodeSource codeSource = protectionDomain.getCodeSource();
     URI location = (codeSource == null ? null : codeSource.getLocation().toURI());
     String path = (location == null ? null : location.getSchemeSpecificPart());
     if (path == null) {
         throw new IllegalStateException("Unable to determine code source archive");
     }
     File root = new File(path);
     if (!root.exists()) {
         throw new IllegalStateException(
                 "Unable to determine code source archive from " + root);
     }
     return (root.isDirectory() ? new ExplodedArchive(root)
             : new JarFileArchive(root));
}
```

### 获取lib/下面的jar，并创建一个LaunchedURLClassLoader
JarLauncher创建好Archive之后，通过getNestedArchives函数来获取到demo-0.0.1-SNAPSHOT.jar/lib下面的所有jar文件，并创建为List。

注意上面提到，Archive都是有自己的URL的。

获取到这些Archive的URL之后，也就获得了一个URL[]数组，用这个来构造一个自定义的ClassLoader：LaunchedURLClassLoader。

创建好ClassLoader之后，再从MANIFEST.MF里读取到Start-Class，即com.example.SpringBootDemoApplication，然后创建一个新的线程来启动应用的Main函数。

```java
protected void launch(String[] args, String mainClass, ClassLoader classLoader)
            throws Exception {
     Runnable runner = createMainMethodRunner(mainClass, args, classLoader);
     Thread runnerThread = new Thread(runner);
     runnerThread.setContextClassLoader(classLoader);
     runnerThread.setName(Thread.currentThread().getName());
     runnerThread.start();
}

protected Runnable createMainMethodRunner(String mainClass, String[] args,
                                          ClassLoader classLoader) throws Exception {
      Class<?> runnerClass = classLoader.loadClass(RUNNER_CLASS);
      Constructor<?> constructor = runnerClass.getConstructor(String.class,
              String[].class);
      return (Runnable) constructor.newInstance(mainClass, args);
}
```

### LaunchedURLClassLoader
LaunchedURLClassLoader和普通的URLClassLoader的不同之处是，它提供了从Archive里加载.class的能力。
结合Archive提供的getEntries函数，就可以获取到Archive里的Resource。

## spring boot loader里的细节
从一个URL，到最终读取到URL里的内容，整个过程是比较复杂的，总结下：
* spring boot注册了一个Handler来处理"jar:"这种协议的URL
* spring boot扩展了JarFile和JarURLConnection，内部处理jar in jar的情况
* 在处理多重jar in jar的URL时，spring boot会循环处理，并缓存已经加载到的JarFile
* 对于多重jar in jar，实际上是解压到了临时目录来处理，可以参考JarFileArchive里的代码
* 在获取URL的InputStream时，最终获取到的是JarFile里的JarEntryData


### JarFile URL的扩展
Spring boot能做到以一个fat jar来启动，最重要的一点是它实现了jar in jar的加载方式。

JDK原始的JarFile URL的定义可以参考这里：

http://docs.oracle.com/javase/7/docs/api/java/net/JarURLConnection.html

原始的JarFile URL是这样子的：`jar:file:/tmp/target/demo-0.0.1-SNAPSHOT.jar!/`
jar包里的资源的URL：`jar:file:/tmp/target/demo-0.0.1-SNAPSHOT.jar!/com/example/SpringBootDemoApplication.class`
可以看到对于Jar里的资源，定义以'!/'来分隔。原始的JarFile URL只支持一个'!/'。

Spring boot扩展了这个协议，让它支持多个'!/'，就可以表示jar in jar，jar in directory的资源了。

比如下面的URL表示demo-0.0.1-SNAPSHOT.jar这个jar里lib目录下面的spring-beans-4.2.3.RELEASE.jar里面的MANIFEST.MF：

```bash
jar:file:/tmp/target/demo-0.0.1-SNAPSHOT.jar!/lib/spring-beans-4.2.3.RELEASE.jar!/META-INF/MANIFEST.MF
```

### 自定义URLStreamHandler，扩展JarFile和JarURLConnection
在构造一个URL时，可以传递一个Handler，而JDK自带有默认的Handler类，应用可以自己注册Handler来处理自定义的URL。
```java
public URL(String protocol,
           String host,
           int port,
           String file,
           URLStreamHandler handler)
    throws MalformedURLException {
    
}
```
参考：
https://docs.oracle.com/javase/8/docs/api/java/net/URL.html#URL-java.lang.String-java.lang.String-int-java.lang.String-

Spring boot通过注册了一个自定义的Handler类来处理多重jar in jar的逻辑。

这个Handler内部会用SoftReference来缓存所有打开过的JarFile。

在处理像下面这样的URL时，会循环处理'!/'分隔符，从最上层出发，先构造出demo-0.0.1-SNAPSHOT.jar这个JarFile，再构造出spring-beans-4.2.3.RELEASE.jar这个JarFile，然后再构造出指向MANIFEST.MF的JarURLConnection。
```bash
jar:file:/tmp/target/demo-0.0.1-SNAPSHOT.jar!/lib/spring-beans-4.2.3.RELEASE.jar!/META-INF/MANIFEST.MF
```

```java
//org.springframework.boot.loader.jar.Handler
public class Handler extends URLStreamHandler {
    private static final String SEPARATOR = "!/";
    private static SoftReference<Map<File, JarFile>> rootFileCache;
    @Override
    protected URLConnection openConnection(URL url) throws IOException {
        if (this.jarFile != null) {
            return new JarURLConnection(url, this.jarFile);
        }
    try {
       return new JarURLConnection(url, getRootJarFileFromUrl(url));
    }
    catch (Exception ex) {
       return openFallbackConnection(url, ex);
    }
 }

public JarFile getRootJarFileFromUrl(URL url) throws IOException {
   String spec = url.getFile();
   int separatorIndex = spec.indexOf(SEPARATOR);
   if (separatorIndex == -1) {
      throw new MalformedURLException("Jar URL does not contain !/ separator");
   }
   String name = spec.substring(0, separatorIndex);
   return getRootJarFile(name);
}
```