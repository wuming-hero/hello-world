# Java接口限流的几种方式详解


## 一、限流的基本概念
接口限流（Rate Limiting）是指对接口的访问频率进行限制，防止系统因突发流量而导致资源耗尽、服务不可用的情况。通过限流，我们可以：

保护系统免受恶意攻击或突发流量冲击

合理分配系统资源，保证核心业务的稳定性

实现服务的优雅降级


## 二、常见的限流算法
### 2.1 计数器算法（固定窗口）
最简单的限流算法，在固定时间窗口内计数，超过阈值则拒绝请求。

### 2.2 滑动窗口算法
改进的计数器算法，将时间窗口划分为更小的区间，按区间滑动统计。

### 2.3 漏桶算法（Leaky Bucket）
以恒定速率处理请求，超出容量的请求被丢弃或排队。

### 2.4 令牌桶算法（Token Bucket）
系统以恒定速率向桶中添加令牌，请求需要获取令牌才能被处理。

## 三、Java实现限流方案
### 3.1 基于Guava的RateLimiter实现令牌桶
Google Guava库提供了成熟的RateLimiter实现，我们先看如何使用：
```java
import com.google.common.util.concurrent.RateLimiter;

public class GuavaRateLimiterExample {

    // 创建一个每秒允许2个请求的限流器
    private static final RateLimiter rateLimiter = RateLimiter.create(2.0);

    public static void main(String[] args) {
        // 模拟10个请求
        for (int i = 0; i < 10; i++) {
            // 尝试获取令牌，如果获取不到会阻塞直到获取成功
            double waitTime = rateLimiter.acquire();
            System.out.printf("请求%d获取令牌成功，等待时间: %.2f秒%n", i + 1, waitTime);
            processRequest(i + 1);
        }
    }

    private static void processRequest(int requestId) {
        System.out.printf("处理请求%d [时间: %s]%n", 
            requestId, 
            LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME));
    }
}
```

### 3.2 自定义计数器限流实现：
```java

import java.util.concurrent.atomic.AtomicInteger;

/**
* 固定窗口计数器限流实现
 */
public class CounterRateLimiter {

      // 时间窗口大小，单位毫秒
      private final long windowSizeInMillis;
    
      // 限流阈值
      private final int threshold;
    
      // 计数器
      private final AtomicInteger counter;
    
      // 窗口开始时间
      private volatile long windowStart;
    
      public CounterRateLimiter(long windowSizeInMillis, int threshold) {
          this.windowSizeInMillis = windowSizeInMillis;
          this.threshold = threshold;
          this.counter = new AtomicInteger(0);
          this.windowStart = System.currentTimeMillis();
      }

    /**
    * 尝试获取请求许可
    * @return true-获取成功，false-被限流
      */
      public boolean tryAcquire() {
          long currentTime = System.currentTimeMillis();
          long elapsedTime = currentTime - windowStart;
    
          // 如果超过时间窗口，重置窗口和计数器
          if (elapsedTime > windowSizeInMillis) {
              synchronized (this) {
                  // 双重检查，避免多次重置
                  if (elapsedTime > windowSizeInMillis) {
                      windowStart = currentTime;
                      counter.set(0);
                  }
              }
          }
    
          // 计数器增加并检查是否超过阈值
          return counter.incrementAndGet() <= threshold;
      }
}

    // 使用示例
    public class CounterRateLimiterExample {
        public static void main(String[] args) throws InterruptedException {
            // 创建一个1秒内最多5次请求的限流器
            CounterRateLimiter limiter = new CounterRateLimiter(1000, 5);
        
            // 模拟请求
            for (int i = 1; i <= 20; i++) {
                if (limiter.tryAcquire()) {
                    System.out.printf("请求%d 通过 [时间: %s]%n", 
                        i, 
                        LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME));
                } else {
                    System.out.printf("请求%d 被限流 [时间: %s]%n", 
                        i, 
                        LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME));
                }
    
                // 模拟请求间隔
                Thread.sleep(100);
            }
        }
    }

```

### 3.3 分布式限流实现（Redis+Lua)
通过Redis的Sorted Set来实现：
```java
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 基于Redis的分布式限流实现（滑动窗口算法）
 */
public class RedisRateLimiter {

    private final JedisPool jedisPool;
    private final String key;
    private final int maxPermits;
    private final int intervalInSeconds;

    // Lua脚本实现滑动窗口限流
    private static final String LUA_SCRIPT =
        "local key = KEYS[1]\n" +
        "local now = tonumber(ARGV[1])\n" +
        "local window = tonumber(ARGV[2])\n" +
        "local max = tonumber(ARGV[3])\n" +
        "\n" +
        "local clearBefore = now - window\n" +
        "redis.call('ZREMRANGEBYSCORE', key, 0, clearBefore)\n" +
        "\n" +
        "local current = redis.call('ZCARD', key)\n" +
        "if current >= max then\n" +
        "    return 0\n" +
        "end\n" +
        "\n" +
        "redis.call('ZADD', key, now, now)\n" +
        "redis.call('EXPIRE', key, window)\n" +
        "return 1";

    public RedisRateLimiter(JedisPool jedisPool, String key, int maxPermits, int intervalInSeconds) {
        this.jedisPool = jedisPool;
        this.key = key;
        this.maxPermits = maxPermits;
        this.intervalInSeconds = intervalInSeconds;
    }

    /**
     * 尝试获取许可
     * @return true-获取成功，false-被限流
     */
    public boolean tryAcquire() {
        try (Jedis jedis = jedisPool.getResource()) {
            long now = System.currentTimeMillis() / 1000;
            Long result = (Long) jedis.eval(
                LUA_SCRIPT, 
                1, 
                key, 
                String.valueOf(now),
                String.valueOf(intervalInSeconds),
                String.valueOf(maxPermits)
            );
            return result == 1;
        }
    }
}

// 使用示例
public class RedisRateLimiterExample {
    public static void main(String[] args) {
        // 创建Redis连接池
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        JedisPool jedisPool = new JedisPool(poolConfig, "localhost", 6379);

        // 创建限流器：10秒内最多5次请求
        RedisRateLimiter limiter = new RedisRateLimiter(jedisPool, "api:limit:user1", 5, 10);

        // 模拟请求
        for (int i = 1; i <= 20; i++) {
            if (limiter.tryAcquire()) {
                System.out.printf("请求%d 通过 [时间: %s]%n", 
                    i, 
                    LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME));
            } else {
                System.out.printf("请求%d 被限流 [时间: %s]%n", 
                    i, 
                    LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME));
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        jedisPool.close();
    }
}
```

### 3.4 SpringBoot使用AOP+Redis的计数器实现限流
```java

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {
    // 限流key
    String key() default "";

    // 限流时间窗口，单位秒
    int timeout() default 60;

    // 时间窗口内允许的最大请求数
    int max() default 100;
}

@Aspect
@Component
public class RateLimitAspect {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Around("@annotation(rateLimit)")
    public Object around(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        String key = rateLimit.key();
        if (StringUtils.isEmpty(key)) {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            key = method.getDeclaringClass().getName() + "." + method.getName();
        }

        // 使用Redis计数器实现限流
        String redisKey = "rate:limit:" + key;
        Long count = redisTemplate.opsForValue().increment(redisKey);

        if (count != null && count == 1) {
            // 第一次设置过期时间
            redisTemplate.expire(redisKey, rateLimit.timeout(), TimeUnit.SECONDS);
        }

        if (count != null && count > rateLimit.max()) {
            throw new RuntimeException("请求过于频繁，请稍后再试");
        }

        return joinPoint.proceed();
    }
}

// 使用示例
@RestController
@RequestMapping("/api")
public class ApiController {

    @RateLimit(key = "getUserInfo", max = 10, timeout = 60)
    @GetMapping("/user/{id}")
    public ResponseEntity<User> getUserInfo(@PathVariable Long id) {
        // 业务逻辑
        return ResponseEntity.ok(userService.getUserById(id));
    }
}
```

### 3.5 SpringBoot中使用拦截器实现接口限流
```java
@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private final RateLimiter rateLimiter = RateLimiter.create(10.0); // 每秒10个请求

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!rateLimiter.tryAcquire()) {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write("{\"code\":429,\"message\":\"请求过于频繁\"}");
            return false;
        }
        return true;
    }
}

// 注册拦截器
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private RateLimitInterceptor rateLimitInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(rateLimitInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/public/**");
    }
}
```

### 3.6 使用阿里巴巴的Sentinel实现
Sentinel是阿里巴巴开源的面向分布式服务架构的轻量级流量控制组件，主要以流量为切入点，从流量控制、熔断降级、系统负载保护等多个维度来帮助开发者保障微服务的稳定性。
使用示例：
```java
public class SentinelBasicExample {

    // 定义资源
    private static final String RESOURCE_NAME = "exampleResource";

    public static void main(String[] args) {
        // 初始化规则
        initFlowRules();

        // 模拟请求
        for (int i = 0; i < 15; i++) {
            // 1.5.0版本开始可以直接利用try-with-resources特性
            try (Entry entry = SphU.entry(RESOURCE_NAME)) {
                // 被保护的逻辑
                System.out.println("处理业务逻辑 " + i);
            } catch (BlockException e) {
                // 处理被流控的逻辑
                System.out.println("请求被限流 " + i);
            }
        }
    }

    private static void initFlowRules() {
        List<FlowRule> rules = new ArrayList<>();
        FlowRule rule = new FlowRule();
        rule.setResource(RESOURCE_NAME);
        rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        // 设置QPS为10
        rule.setCount(10);
        rules.add(rule);
        FlowRuleManager.loadRules(rules);
    }
}
```

## 四、总结
在实际项目中，应根据业务需求、系统架构选择合适的限流方案。

对于简单应用，Guava RateLimiter或自定义计数器可能足够；
 
对于分布式系统，则需要Redis或专业限流组件如Sentinel。


https://mp.weixin.qq.com/s/52gWE2RD0QjM9I4DyhFHEg
