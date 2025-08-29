# Java接口限流的几种方式详解


## 一、限流的基本概念
接口限流（Rate Limiting）是指对接口的访问频率进行限制，防止系统因突发流量而导致资源耗尽、服务不可用的情况。通过限流，我们可以：
* 保护系统免受恶意攻击或突发流量冲击
* 合理分配系统资源，保证核心业务的稳定性
* 实现服务的优雅降级


## 二、常见的限流算法
### 2.1 计数器算法（固定窗口）
* 原理
最简单的限流算法，在固定时间窗口内计数，超过阈值则拒绝请求。

* 问题
临界突发问题：窗口切换时可能双倍流量溢出（如59s和61s涌入2000请求）。

* 适用场景
对流量波动不敏感的简单场景（如内部员工系统）
配合其他算法作为第一层粗粒度限流

```java
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class FixedWindowRateLimiter {
    // 时间窗口大小（毫秒）
    private final long windowSizeInMillis;
    // 窗口内允许的最大请求数
    private final int maxRequestsPerWindow;
    // 当前窗口开始时间（原子变量）
    private final AtomicLong windowStart;
    // 当前窗口请求计数器（原子变量）
    private final AtomicInteger requestCount;
    
    /**
     * 创建固定窗口限流器
     * @param maxRequestsPerWindow 窗口内允许的最大请求数
     * @param windowSize 时间窗口大小
     * @param unit 时间单位
     */
    public FixedWindowRateLimiter(int maxRequestsPerWindow, long windowSize, TimeUnit unit) {
        this.maxRequestsPerWindow = maxRequestsPerWindow;
        this.windowSizeInMillis = unit.toMillis(windowSize);
        this.windowStart = new AtomicLong(System.currentTimeMillis());
        this.requestCount = new AtomicInteger(0);
    }
    
    /**
     * 尝试获取请求许可
     * @return true 允许请求，false 限流拒绝
     */
    public boolean tryAcquire() {
        long now = System.currentTimeMillis();
        long currentWindowStart = windowStart.get();
        
        // 判断是否进入新的时间窗口
        if (now - currentWindowStart > windowSizeInMillis) {
            // CAS 操作保证线程安全：重置计数器和窗口时间
            if (windowStart.compareAndSet(currentWindowStart, now)) {
                requestCount.set(0); // 新窗口重置计数器
            }
        }
        
        // 请求计数增加（原子操作）
        int updatedCount = requestCount.incrementAndGet();
        
        // 检查是否超过阈值
        if (updatedCount > maxRequestsPerWindow) {
            // 超过阈值后减少计数（避免永久阻塞后续合法请求）
            requestCount.decrementAndGet();
            return false;
        }
        return true;
    }
}
```

### 2.2 滑动窗口算法
* 原理
改进的计数器算法，将时间窗口划分为更小的区间(如10秒/格），按区间滑动统计,动态淘汰旧数据。

* 数据结构：环形队列 + 时间戳数组

* 优势：缓解固定窗口的临界问题
*  适用场景
API网关限流（如Nginx的limit_req模块）
需要平滑限流但无法接受漏桶/令牌桶复杂度的场景

```java
// Guava RateLimiter简化版实现思路
long[] timestamps = new long[10]; // 10个时间槽
int[] counts = new int[10];

public synchronized boolean allow() {
    long now = System.currentTimeMillis();
    int currentSlot = (int) (now / 1000) % 10;
    // 清理过期槽位并累加计数
    if (timestamps[currentSlot] != now / 1000) {
        timestamps[currentSlot] = now / 1000;
        counts[currentSlot] = 0;
    }
    int total = Arrays.stream(counts).sum();
    if (total >= 1000) return false;
    counts[currentSlot]++;
    return true;
}
```

### 2.3 漏桶算法（Leaky Bucket）
# 原理
以恒定速率处理请求(桶底漏水），超出容量的请求被丢弃或排队。

核心参数：
桶容量（capacity）
流出速率（rate, 如10个/秒）

* 特点
强行限制处理速度，不支持突发流量，保证绝对均匀。

* 适用场景
老旧系统保护（如数据库连接池限流）
要求输出流量绝对平稳的场景（工业控制）

```java
import java.util.concurrent.TimeUnit;

public class LeakyBucket {
    private final long capacity;      // 桶的容量（单位：水滴/请求）
    private final long leakRate;      // 漏水速率（单位：水滴/毫秒）
    private double water;            // 当前桶中的水量
    private long lastLeakTime;       // 上次漏水的时间戳（毫秒）

    /**
     * 初始化漏桶
     * @param capacity 桶的最大容量
     * @param leakRatePerSecond 每秒漏水速率（单位：水滴/秒）
     */
    public LeakyBucket(long capacity, long leakRatePerSecond) {
        this.capacity = capacity;
        this.leakRate = TimeUnit.SECONDS.toMillis(1) / leakRatePerSecond; // 每滴水的间隔毫秒数
        this.water = 0;
        this.lastLeakTime = System.currentTimeMillis();
    }

    /**
     * 尝试消费一个请求（一滴水）
     * @return 是否被接受（桶未溢出）
     */
    public synchronized boolean tryConsume() {
        leakWater();
        if (water < capacity) {
            water += 1;
            return true;
        }
        return false;
    }

    /**
     * 根据时间计算并减少桶中的水
     */
    private void leakWater() {
        long now = System.currentTimeMillis();
        long elapsedMillis = now - lastLeakTime;
        if (elapsedMillis <= 0) return;

        // 计算漏水量（基于时间差）
        double leaks = elapsedMillis / (double) leakRate;
        if (leaks > 0) {
            water = Math.max(0, water - leaks);
            lastLeakTime = now; // 更新漏水时间
        }
    }

    // 测试代码
    public static void main(String[] args) throws InterruptedException {
        // 创建漏桶：容量=10，速率=5个/秒
        LeakyBucket bucket = new LeakyBucket(10, 5);

        // 模拟20次请求
        for (int i = 1; i <= 20; i++) {
            Thread.sleep(100); // 每100ms发起一次请求

            if (bucket.tryConsume()) {
                System.out.printf("[%02d] 请求被处理 ✅ (当前水量: %.2f)\n", i, bucket.water);
            } else {
                System.out.printf("[%02d] 请求被限流 ❌ (水量溢出!)\n", i);
            }
        }
    }
}
```

### 2.4 令牌桶算法（Token Bucket）
* 原理
以固定速率生成令牌，请求需获取令牌才放行，支持突发流量（预存令牌）。
核心参数：
令牌生成速率（rate）
桶容量（burst size）

* 优势
兼具限流平滑性 + 突发流量处理能力。

* 适用场景
高并发API限流（秒杀系统、OpenAPI平台）
网络流量整形（CDN边缘节点）
微服务熔断降级（如Sentinel、Hystrix）

```java
// Guava RateLimiter原理
public class TokenBucket {
    private int tokens;
    private long lastRefillTime;
    private final int capacity;
    private final double refillRate; // tokens/second

    public synchronized boolean tryAcquire() {
        refillTokens(); // 按时间补充令牌
        if (tokens > 0) {
            tokens--;
            return true;
        }
        return false;
    }
    
    private void refillTokens() {
        long now = System.currentTimeMillis();
        double secs = (now - lastRefillTime) / 1000.0;
        tokens = (int) Math.min(capacity, tokens + secs * refillRate);
        lastRefillTime = now;
    }
}
```

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
