# 以如何开发一个 starter-redis 为例

1. 项目结构与规范 xxx-spring-boot-starter
2. 编辑当前项目pom文件，引入redis核心依赖
3. 配置属性类‌
   定义 @ConfigurationProperties 类读取用户配置：
```java
@ConfigurationProperties(prefix = "spring.data.redis")
public class RedisExtProperties {
    private String host = "localhost";
    private int port = 6379;
    private int database = 0;
    private Duration timeout = Duration.ofSeconds(30);
    // 其他属性和getter/setter
}

```

4. redis自动配置类
```java
@Configuration
@EnableConfigurationProperties(RedisExtProperties.class)
@ConditionalOnClass(RedisConnectionFactory.class)
public class RedisAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public LettuceConnectionFactory redisConnectionFactory(
            RedisExtProperties properties) {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(properties.getHost());
        config.setPort(properties.getPort());
        config.setDatabase(properties.getDatabase());
        return new LettuceConnectionFactory(config);
    }

    @Bean
    @ConditionalOnMissingBean
    public RedisTemplate<String, Object> redisTemplate(
            RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }
}

```
5. 工具类封装
```java
public class RedisExtUtils {
    private final RedisTemplate<String, Object> redisTemplate;

    public RedisExtUtils(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean setIfAbsent(String key, Object value, Duration ttl) {
        return Boolean.TRUE.equals(
            redisTemplate.opsForValue()
                .setIfAbsent(key, value, ttl)
        );
    }
}

```
6. 在 resources/META-INF下配置spring.factories 配置
```java
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
  com.example.redis.autoconfigure.RedisAutoConfiguration
```
