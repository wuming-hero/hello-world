## 原理
### 关键点解析
* SHA-1哈希计算：
使用MessageDigest计算长URL的SHA-1摘要，取前8字节（64位）以生成唯一数值。
安全性：SHA-1已不安全用于加密，但在此场景中仅用于唯一性，可接受。

* 字节转Long：
bytesToLong方法将字节数组转换为无符号的long值，避免符号位干扰。
例如：byte[0]到byte[7]的8字节表示一个64位无符号整数。

* Base62编码逻辑：
encode方法将数值逐次除以62，取余数作为字符索引。
decode方法将字符逐次转换为数值并累加。

### 冲突处理
若短码冲突（极低概率），可：
```java
public static String generateShortCodeWithRetry(String longUrl, int maxRetry) {
    for (int i = 0; i < maxRetry; i++) {
        String code = generateShortCode(longUrl + System.currentTimeMillis());
        if (isUnique(code)) return code;
    }
    throw new RuntimeException("Failed to generate unique code");
}

```

### 数据库存储

```java
CREATE TABLE short_urls (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    original_url VARCHAR(2048) NOT NULL,
    short_code VARCHAR(12) UNIQUE NOT NULL, -- 预留扩展空间
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    click_count INT DEFAULT 0
);

```
### 注意事项
* 短码长度控制：

当前方案生成的短码长度为 log62(2^64) ≈ 11位（64位数值的Base62编码）。
若需更短，可减少哈希字节数（如用前6字节生成48位数值 → Base62约8位）。

* 性能优化：

使用Redis缓存短码到URL的映射，减少数据库查询。
预生成短码池（批量生成并存储，避免实时计算）。

* 安全性：

验证原始URL合法性（防止恶意链接）。
限制短URL生成频率（防暴力破解）。

## 具体实现
### 核心算法
```java
public class Base62 {
    private static final String CHARSET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int RADIX = 62;

    // 将数值转换为Base62字符串
    public static String encode(long num) {
        if (num == 0) return String.valueOf(CHARSET.charAt(0));
        StringBuilder sb = new StringBuilder();
        while (num > 0) {
            int remainder = (int) (num % RADIX);
            sb.append(CHARSET.charAt(remainder));
            num = num / RADIX;
        }
        // 反转以获取正确顺序
        return sb.reverse().toString();
    }

    // 将Base62字符串转换为数值
    public static long decode(String str) {
        long num = 0;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            num = num * RADIX + CHARSET.indexOf(c);
        }
        return num;
    }
}

```

### 短URL生成器
```java
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ShortUrlGenerator {
    private static final int HASH_LENGTH = 8; // 使用SHA-1的前8字节

    // 生成短URL的短码
    public static String generateShortCode(String longUrl) throws Exception {
        // 1. 计算SHA-1哈希
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        byte[] hash = digest.digest(longUrl.getBytes());
        
        // 2. 取前8字节转换为long（注意无符号处理）
        long hashCode = bytesToLong(hash, 0, HASH_LENGTH);
        
        // 3. Base62编码
        return Base62.encode(hashCode);
    }

    // 将字节数组转为无符号的long（处理前8字节）
    private static long bytesToLong(byte[] bytes, int offset, int length) {
        long result = 0;
        for (int i = 0; i < length; i++) {
            result <<= 8; // 左移8位（等价于乘以256）
            result |= (bytes[offset + i] & 0xFF); // 无符号处理
        }
        return result;
    }

    // 示例：短码转回哈希值（用于验证）
    public static long decodeShortCode(String shortCode) {
        return Base62.decode(shortCode);
    }
}

```

### 测试案例

```java
public class Main {
    public static void main(String[] args) {
        try {
            String longUrl = "https://www.example.com/very-long-url-path?param=123";
            String shortCode = ShortUrlGenerator.generateShortCode(longUrl);
            System.out.println("Short Code: " + shortCode); // 输出类似 "9EaBcD12"

            // 验证解码
            long hash = ShortUrlGenerator.decodeShortCode(shortCode);
            System.out.println("Decoded Hash: " + hash);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

```