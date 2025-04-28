# Spring Boot
Spring Boot 通过简单的步骤就可以创建一个 Spring 应用。 Spring Boot 为 Spring 整合第三方框架提供了开箱即用功能。 Spring Boot 的核心思想是约定大于配置。

## Spring Boot 解决的问题
* 搭建后端框架时需要手动添加 Maven 配置，涉及很多 XML 配置文件，增加了搭建难度和时间成本。
* 将项目编译成 war 包，部署到 Tomcat 中，项目部署依赖 Tomcat，这样非常不方便。 
* 应用监控做的比较简单，通常都是通过一个没有任何逻辑的接口来判断应用的存活状态。

## Spring Boot 优点
* 自动装配: Spring Boot 会根据某些规则对所有配置的 Bean 进行初始化。可以减少了很多重复性的工作。 比如使用 MongoDB 时，只需加入 MongoDB 的 Starter 包，然后配置 的连接信息，就可以直接使用 Mongo- Template 自动装配来操作数据库了。简化了 Maven Jar 包的依赖，降低了烦琐配置的出错几率。 
* 内嵌容器:Spring Boot 应用程序可以不用部署到外部容器中，比如 Tomcat。 应用程序可以直接通过 Maven 命令编译成可执行的 jar 包，通过 java-jar 命令启动即可，非常方便。

## 应用监控
Spring Boot 中自带监控功能 Actuator，可以实现对程序内部运行情况进行监控，
比如 Bean 加载情况、环境变量、日志信息、线程信息等。当然也可以自定义跟业务相关的监控，通过Actuator 的 端点信息进行暴露。

spring-boot-starter-web //用于快速构建基于 Spring MVC 的 Web 项目。 
spring-boot-starter-data-redis //用于快速整合并操作 Redis。 
spring-boot-starter-data-mongodb //用于对 MongoDB 的集成。 
spring-boot-starter-data-jpa //用于操作 MySQL。

### Spring Boot Admin(将 actuator 提供的数据进行可视化)
* 显示应用程序的监控状态、查看 JVM 和线程信息 
* 应用程序上下线监控
* 可视化的查看日志、动态切换日志级别
* HTTP 请求信息跟踪等实用功能

# SpringCloud