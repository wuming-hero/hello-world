# spring-boot 自动配置的原理
## 一、底层触发机制
1. 核心注解‌
`@SpringBootApplication` 组合了 `@EnableAutoConfiguration`，激活自动配置扫描逻辑。

2. 加载流程‌
```bash
#  SpringApplication.run() 启动时触发
SpringApplication.run(MyApp.class, args)
└── refreshContext(context)
└── invokeBeanFactoryPostProcessors(beanFactory)
└── ConfigurationClassPostProcessor # 解析配置类
└── AutoConfigurationImportSelector # 加载候选配置类
```

## 二、候选配置发现机制

1. 资源定位‌
从所有依赖包的 `META-INF/spring.factories` 中读取键为 `org.springframework.boot.autoconfigure.EnableAutoConfiguration` 的配置类列表。

示例文件内容‌：

```bash
# spring-boot-autoconfigure.jar/META-INF/spring.factories
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration,\
org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
```

2. 过滤流程‌
通过 AutoConfigurationImportFilter 实现条件过滤：
* OnClassCondition：检查类路径是否存在指定类
* OnBeanCondition：检查容器是否已存在指定Bean
* OnWebApplicationCondition：判断是否Web环境

## 三、条件化配置技术
常用条件注解‌

注解名称	                      生效条件	             典型应用场景
* @ConditionalOnClass	      类路径存在指定类	         自动配置Tomcat（存在Servlet类）
* @ConditionalOnMissingBean	  容器中不存在指定Bean	     数据源自动配置（未自定义DataSource时生效）
* @ConditionalOnProperty	  配置文件存在指定属性值	 功能开关控制
* @ConditionalOnWebApplication	当前是Web应用环境	     WebMvc自动配置


配置类示例‌
```java
@Configuration
@ConditionalOnClass({Servlet.class, DispatcherServlet.class})
@ConditionalOnWebApplication(type = Type.SERVLET)
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
public class DispatcherServletAutoConfiguration {
// 自动配置DispatcherServlet及相关Bean
}
```

## 四、自动配置优先级控制

1. 加载顺序控制‌
@AutoConfigureOrder：定义全局配置顺序
@AutoConfigureBefore/@AutoConfigureAfter：指定相对于其他配置类的顺序

2. 覆盖规则‌
用户自定义Bean优先‌：通过 @Bean 显式定义的Bean会覆盖自动配置的默认Bean
属性配置覆盖‌：通过 application.properties 修改自动配置参数

##  五、调试与定制方法
1. 查看生效配置‌
启动时添加 --debug 参数，输出：

```bash
=========================
AUTO-CONFIGURATION REPORT
=========================
Positive matches:
DispatcherServletAutoConfiguration matched
- @ConditionalOnClass found required classes [...]
Negative matches:
DataSourceAutoConfiguration:
- @ConditionalOnClass did not find required class 'javax.sql.DataSource'
```

2. 手动排除配置‌

```java
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
```

3. 自定义自动配置‌

```java
// 1. 创建配置类
@Configuration
@ConditionalOnClass(MyService.class)
public class MyAutoConfiguration {
@Bean
public MyService myService() {
return new DefaultMyService();
}
}

// 2. 在resources/META-INF/spring.factories中声明
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
com.example.MyAutoConfiguration
```
## 六、核心源码解析
关键类解析‌
1. AutoConfigurationImportSelector
selectImports() 方法加载候选配置类
getAutoConfigurationEntry() 执行条件过滤

2. ConditionEvaluator
解析 @Conditional 注解判断是否满足条件

3. SpringFactoriesLoader
加载 spring.factories 的工厂机制

## 七、设计优势总结
* 按需加载‌：通过条件注解避免不必要的Bean加载
* 约定优于配置‌：通过标准化命名和路径降低配置复杂度
* 可扩展性‌：三方库可通过 spring.factories 无缝集成自动配置
* 透明化覆盖‌：用户可通过显式定义Bean或属性修改默认行为

通过条件化装配机制，Spring Boot 实现了零配置即可运行的约定式开发体验，同时保留完整的可定制性，这是其能在Java生态中脱颖而出的核心设计之一。