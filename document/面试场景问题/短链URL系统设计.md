# 短 URL 生成系统设计详解

## 一、核心目标

把一段很长的 URL（比如 `https://www.example.com/very/long/path?with=params&and=more`）变成一段很短且唯一的字符串（比如 `abc123Xyz`），并且能通过这个短字符串快速找到原来的长 URL。

---

## 二、设计原理（为什么这么设计）

### 1. 短码从哪里来？

我们不能直接把长 URL 存进数据库然后用自增 ID 转成短码，因为那样需要每次都查库，而且自增 ID 容易暴露数据规模。更好的办法是**根据长 URL 的内容计算出一个固定长度的数字指纹**，再用一种紧凑的编码方式把它变成短字符串。

- **数字指纹**：用 SHA-1 哈希算法对长 URL 计算，得到一个 160 位的摘要。我们只取前 64 位（8 字节）作为唯一标识。虽然理论上不同 URL 可能产生相同的前 64 位（哈希碰撞），但概率极低（大约 2⁻³² 的量级），完全可以接受。
- **为什么要取 8 字节？** 8 字节正好是一个 `long` 的长度，方便转换成数值。而且 64 位数值用 Base62 编码后大约 11 个字符，足够短。
- **为什么不直接用 MD5 或 CRC？** SHA-1 的碰撞概率比 MD5 更低，而且 Java 标准库自带，方便使用。这里我们不关心密码学安全性，只关心唯一性。

### 2. 如何把 8 字节变成短字符串？

8 字节可以看作一个很大的整数（范围 0 ~ 2⁶⁴-1），但直接写数字太长了。我们需要用一种**更紧凑的进制**来表示它。

- **Base62 编码**：使用 62 个字符（0-9, A-Z, a-z）表示数字。就像十进制用 0-9，十六进制用 0-F，Base62 用 62 个字符，所以同样的数值用 Base62 表示会比十进制短得多。
- **转换过程**：把那个大整数不断除以 62，每次取余数对应的字符，最后把得到的字符反转就是最终的短码。
- **为什么不用 Base64？** Base64 包含 `+` 和 `/`，不适合放在 URL 里（会被转义）。Base62 只包含字母和数字，URL 友好。

### 3. 如何保证短码唯一？

虽然哈希碰撞概率极低，但还是要考虑万一撞了怎么办。解决办法很简单：

- 在生成短码后，检查数据库中是否已存在相同的短码。
- 如果已存在，说明发生了碰撞，此时给原始 URL 拼接一个时间戳（或其他随机因子）再重新计算哈希，直到生成一个唯一的短码为止（通常一次就够了）。

---

## 三、实现步骤（一步一步来）

### 步骤 1：计算长 URL 的数字指纹

```java
// 1. 获取 SHA-1 摘要
MessageDigest digest = MessageDigest.getInstance("SHA-1");
byte[] hash = digest.digest(longUrl.getBytes());

// 2. 取前 8 个字节（64 位）
byte[] first8Bytes = Arrays.copyOfRange(hash, 0, 8);
```

### 步骤 2：把 8 字节转成一个无符号 long

因为 Java 的 `long` 是有符号的，最高位是符号位，如果不处理，正数可能会变成负数。所以我们用位运算把字节当作无符号处理：

```java
long value = 0;
for (int i = 0; i < 8; i++) {
    value <<= 8;               // 左移 8 位，腾出空间
    value |= (first8Bytes[i] & 0xFF);  // 取字节的低 8 位（无符号）
}
```

现在 `value` 就是一个 0 ~ 2⁶⁴-1 之间的正整数。

### 步骤 3：Base62 编码

定义一个包含 62 个字符的字符串常量：

```java
private static final String BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
```

编码逻辑（类似十进制转二进制）：

```java
public static String encode(long num) {
    if (num == 0) return "0";
    StringBuilder sb = new StringBuilder();
    while (num > 0) {
        int remainder = (int)(num % 62);
        sb.append(BASE62.charAt(remainder));
        num /= 62;
    }
    return sb.reverse().toString(); // 注意反转，因为低位先被计算出来
}
```

### 步骤 4：冲突检测与重试

```java
public String createShortCode(String longUrl) {
    for (int i = 0; i < 5; i++) {  // 最多尝试 5 次
        String code = generateShortCode(longUrl + System.nanoTime()); // 加时间戳避免碰撞
        if (!isCodeExists(code)) {  // 查询数据库或缓存
            saveMapping(code, longUrl);
            return code;
        }
    }
    throw new RuntimeException("无法生成唯一短码");
}
```

### 步骤 5：存储与查询

数据库表设计：

```sql
CREATE TABLE url_mapping (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    short_code VARCHAR(16) NOT NULL UNIQUE,
    original_url TEXT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_short_code ON url_mapping(short_code);
```

为了提高查询性能，可以在 Redis 中缓存 `short_code -> original_url` 的映射，并设置合理的过期时间。

---

## 四、完整代码示例

```java
import java.security.MessageDigest;
import java.util.*;

public class ShortUrlService {

    private static final String BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int HASH_BYTES = 8;

    // 生成短码（不带冲突处理）
    public static String generateShortCode(String longUrl) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] hash = md.digest(longUrl.getBytes());
        long numericHash = bytesToLong(hash, 0, HASH_BYTES);
        return base62Encode(numericHash);
    }

    // 带冲突处理的生成
    public static String createUniqueShortCode(String longUrl) {
        for (int i = 0; i < 5; i++) {
            String candidate = generateShortCode(longUrl + System.nanoTime());
            if (!isCodeUsed(candidate)) {
                saveToDb(candidate, longUrl);
                return candidate;
            }
        }
        throw new RuntimeException("无法生成唯一短码");
    }

    // 字节数组转无符号 long
    private static long bytesToLong(byte[] bytes, int offset, int len) {
        long val = 0;
        for (int i = 0; i < len; i++) {
            val <<= 8;
            val |= (bytes[offset + i] & 0xFF);
        }
        return val;
    }

    // Base62 编码
    public static String base62Encode(long num) {
        if (num == 0) return "0";
        StringBuilder sb = new StringBuilder();
        while (num > 0) {
            sb.append(BASE62.charAt((int)(num % 62)));
            num /= 62;
        }
        return sb.reverse().toString();
    }

    // 模拟数据库检查
    private static boolean isCodeUsed(String code) {
        // 实际查询数据库或 Redis
        return false;
    }

    private static void saveToDb(String code, String url) {
        // 保存到数据库
    }
}
```

---

## 五、常见问题 FAQ

**Q1：为什么不用自增 ID 直接转 Base62？**

自增 ID 需要依赖数据库生成，每次生成都要写库，性能差。而且自增 ID 会暴露总数量，容易被爬虫遍历。哈希方式不需要实时查询数据库就能生成短码，更适合高并发场景。

**Q2：短码长度能再短一点吗？**

可以。如果你只需要 8 位短码，那么可以只取哈希的前 6 字节（48 位），这样 Base62 编码后大约 8 个字符。但碰撞概率会稍微升高，不过仍然很低（约 2⁻²⁴）。也可以使用更长的哈希（如 10 字节）得到更长的短码，但没必要。

**Q3：短码能被反向还原成长 URL 吗？**

不能。哈希是不可逆的，所以我们必须在数据库中存储映射关系。当用户访问短链接时，服务器通过短码查询数据库得到原始 URL，然后 302 重定向过去。

**Q4：如果同一个长 URL 生成多次，会得到不同的短码吗？**

会的。因为我们加了时间戳来避免碰撞，所以即使是同一个长 URL，每次生成的短码都可能不同。这其实没问题，因为短码只是一个索引，多个短码可以指向同一个长 URL。如果你想对同一个长 URL 始终返回相同的短码，可以先查询数据库是否存在，存在则直接返回已有的短码。

---

## 六、总结

整个系统的设计思路可以概括为：

1. **输入**：长 URL
2. **处理**：
    - 计算 SHA-1 哈希 → 取前 8 字节 → 转成 long 整数
    - 用 Base62 编码该整数 → 得到短码
    - 检测冲突（几乎不会发生），若冲突则加盐重试
3. **输出**：短码
4. **存储**：短码 → 长 URL 的映射（数据库 + 缓存）
5. **访问**：用户访问短链接 → 服务器根据短码查到长 URL → 302 重定向

这个方案简单、高效、可靠，被业界广泛使用（比如 TinyURL、Bitly 的早期版本都采用了类似思路）。