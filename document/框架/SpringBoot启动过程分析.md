
```java
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

## @SpringBootApplication注解
简单讨论下主类上的@SpringBootApplication注解。我们先看这个注解的定义：
```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(excludeFilters = @Filter(type = FilterType.CUSTOM, classes = TypeExcludeFilter.class))
public @interface SpringBootApplication {
    ...
}
```

这里可以看出，这个@SpringBootApplication是@SpringBootConfiguration, @EnableAutoConfiguration, @ComponentScan三个注解的组合注解。
如果不用@SpringBootApplication组合注解，换成@Configuration + @EnableAutoConfiguration + @ComponentScan三个注解，效果是一样的。
为什么组合？是因为这三个注解常放一起用，组合成一个用起来更方便。那这三个注解分别什么意义呢？
* @SpringBootConfiguration：包装了@Configuration，告诉容器该类是一个拥有bean定义和依赖项的配置类。Spring Boot做到抛弃xml，使用纯Java类做配置，@Configuration立了大功。
* @ComponentScan：自动扫描，相当于Spring中的<context:component-scan>, 可以使用basePackages指定要扫描的包，并可以指定扫描的条件。默认扫描@ComponentScan注解所在类(一般是入口类Application)的同级类和同级目录下的类，所以我们一般会把入口类Application放在源码包(src)的第一层目录，保证src目录下的所有类都被扫描到。
* @EnableAutoConfiguration：自动配置，根据依赖的jar包进行最大化的默认配置。

这其中，@EnableAutoConfiguration最体现Spring Boot新特性，他是Spring Boot自动配置的源头。

## Main方法
Spring Boot启动的重头戏都在main方法里。入口类Application中的public static void main(String[] args)方法，这是一个标准的Java程序入口。
在这个main方法中，执行了SpringApplication类的静态run方法，并将一个Application类和main方法参数args作为参数传递进去。

我们看SpringApplication类的静态run方法： 
```java
public static ConfigurableApplicationContext run(Object[] sources, String[] args) {
    return new SpringApplication(sources).run(args);
}
```
先构造一个SpringApplication实例，然后调用该实例的run方法。那我们就分两步来看，先看SpringApplication实例的构造过程，再看run方法的执行过程。

### SpringApplication实例的构造过程
SpringApplication的构造方法只有一个方法initialize，所有的构造过程都在该方法里。
```java
public SpringApplication(Object... sources) {
    initialize(sources);
}
```
接下开看这个关键的initialize方法做了什么事情。

#### Step4. 查找并加载所有可用的ApplicationListener
ApplicationListener是应用事件监听器，监听应用事件(ApplicationEvent)。这是典型的Listenr模式，跟事件绑定，一旦发生某个事件，则做某个事情。
ApplicationListener可以监听某一个事件，也可以监听某一些事件，也可以监听所有事件，如清除容器缓存监听器ClearCachesApplicationListener只监听ContextRefreshedEvent事件，而日志监听器LoggingApplicationListener就绑定了所有事件。
如需定制ApplicationListener，新增扩展类覆写onApplicationEvent方法即可，参数为需要监听的事件类型。
```java
public interface ApplicationListener<E extends ApplicationEvent> extends EventListener {
    void onApplicationEvent(E event);
}
```

整个查找并加载ApplicationListener的过程和ApplicationContextInitializer一模一样，遍历classpath下所有spring.factories文件来查找ApplicationListener下的类，然后实例化。

明白了ApplicationContextInitializer和ApplicationListener的查找过程，我们再实际地找一个“META-INF/spring.factories“文件看看，就更清楚了。我们以org/springframework/boot/spring-boot/1.4.3.RELEASE/spring-boot-1.4.3.RELEASE-sources.jar下的spring.factories为例
```bash
# Application Context Initializers
org.springframework.context.ApplicationContextInitializer=\
org.springframework.boot.context.ConfigurationWarningsApplicationContextInitializer,\
org.springframework.boot.context.ContextIdApplicationContextInitializer,\
org.springframework.boot.context.config.DelegatingApplicationContextInitializer,\
org.springframework.boot.web.context.ServerPortInfoApplicationContextInitializer

# Application Listeners
org.springframework.context.ApplicationListener=\
org.springframework.boot.ClearCachesApplicationListener,\
org.springframework.boot.builder.ParentContextCloserApplicationListener,\
org.springframework.boot.context.FileEncodingApplicationListener,\
org.springframework.boot.context.config.AnsiOutputApplicationListener,\
org.springframework.boot.context.config.ConfigFileApplicationListener,\
org.springframework.boot.context.config.DelegatingApplicationListener,\
org.springframework.boot.context.logging.ClasspathLoggingApplicationListener,\
org.springframework.boot.context.logging.LoggingApplicationListener,\
org.springframework.boot.liquibase.LiquibaseServiceLocatorApplicationListener
```

我们可以看到spring.factories以k-v的形式组织的，所以当查找ApplicationContextInitializer时就会根据org.springframework.context.ApplicationContextInitializer这个key，拿到值并转化为list返回，这里有4个初始化器；当查找ApplicationListener时就会根据org.springframework.context.ApplicationListener这个key，拿到值并转化为list返回，这里有9个监听器。