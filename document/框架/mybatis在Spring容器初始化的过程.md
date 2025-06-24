# 一、概述
通过前面几篇文章的学习，相信小伙伴对Mybatis的认识更加深刻了，对整体的流程应该算是比较清晰了。但是我们在项目中很少单独使用Mybatis，一般都是集成到Spring中，由Spring来帮我们完成以前很多繁琐的步骤，比如管理SqlSessionFactory、创建SqlSession，并且不需要手动调用getMapper方法去获取mapper接口，直接使用autoWired自动注入进来就好了。那么Spring到底是如何整合Mybatis的，我们有必要去了解一下。

# Spring加载MyBatis过程
首先来回顾一下，没有集成Spring的时候，Mybatis是如何使用的：
```java
public static void main(String[] args) {
    //1、读取配置文件
    String resource = "mybatis-config.xml";
    InputStream inputStream;
    SqlSession sqlSession = null;
    try {
        inputStream = Resources.getResourceAsStream(resource);
        //2、初始化mybatis，创建SqlSessionFactory类实例
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        //3、创建Session实例
        sqlSession = sqlSessionFactory.openSession();
        //4、获取Mapper接口
        UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
        //5、执行SQL操作
        User user = userMapper.getById(1L);
        System.out.println(user);
    } catch (IOException e) {
        e.printStackTrace();
    } finally {
        //6、关闭sqlSession会话
        if (null != sqlSession) {
            sqlSession.close();
        }
    }
}
```
大体步骤：

1、加载Mybatis的全局配置文件；
2、初始化mybatis，创建SqlSessionFactory类实例；
3、创建Session会话；
4、获取Mapper接口；
5、执行具体SQL；
其中加载全局配置文件、创建SqlSessionFactory、扫描mapper接口都是比较重要的，所以分析Spring加载MyBatis的过程无非也就是从这几方面入手。

我们来看看Spring要集成Mybatis需要做哪些配置：
```xml
<!--定义数据源-->
<bean id="dataSource" class="org.apache.commons.dbcp.BasicDataSource" destroy-method="close"> 
  <property name="driverClassName" value="com.mysql.jdbc.Driver"/>
  <property name="url" value="jdbc:mysql://127.0.0.1:3306/user_mybatis"/>
  <property name="username" value="root"/>
  <property name="password" value="root"/>
</bean>
 
<!--定义sqlSessionFactory-->
<bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
    <!--数据库连接池 -->
    <property name="dataSource" ref="dataSource"/>
    <!--配置mybatis全局配置文件: mybatis-config.xml -->
    <property name="configLocation" value="classpath:/mybatis-config.xml"/>
    <!--扫描entity包,使用别名,多个用;隔开 -->
    <property name="typeAliasesPackage" value="entity"/>
    <!--扫描sql配置文件:mapper需要的xml文件 -->
    <property name="mapperLocations" value="classpath:/mapper/*.xml"/>
</bean>
 
<!-- 配置mapper接口路径,并注入到spring容器中 -->
<bean class="org.mybatis.spring.mapper.MapperScannerConfigurer">
    <property name="sqlSessionFactoryBeanName" value="sqlSessionFactory"/>
    <property name="basePackage" value="com.wsh.mybatis.mybatisdemo.mapper"/>
</bean>
```



# 四、总结
本文主要总结了Spring加载MyBatis的过程，有几个关键的类：

* SqlSessionFactoryBean：实现了InitializingBean和FactoryBean接口，主要完成的工作就是构建SqlSessionFactory对象，重点关注afterPropertiesSet()方法和getObject()方法；
* MapperScannerConfigurer：实现了BeanDefinitionRegistryPostProcessor接口，主要完成的工作就是扫描我们配置的mapper接口，并注册到IOC中。重点关注postProcessBeanDefinitionRegistry方法；
* MapperFactoryBean：实现了FactoryBean接口，重点关注getObject()方法，主要完成mapper接口的获取；

Spring加载MyBatis这个过程，其实就是把MyBatis的Mapper接口转换成Bean，注入到Spring容器的过程。

https://cloud.tencent.com/developer/article/2384645