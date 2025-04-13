package com.wuming.util;

import java.security.MessageDigest;

/**
 * @author manji
 * Created on 2025/4/13 07:47
 */
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

    /**
     * 将字节数组转为无符号的long（处理前8字节）
     */
    private static long bytesToLong(byte[] bytes, int offset, int length) {
        long result = 0;
        for (int i = 0; i < length; i++) {
            result <<= 8; // 左移8位（等价于乘以256）
            result |= (bytes[offset + i] & 0xFF); // 无符号处理
        }
        // result 可能为负责，通过以下位运算将result转为无符号
        return result & 0xFFFFFFFFL;
    }

    // 示例：短码转回哈希值（用于验证）
    public static long decodeShortCode(String shortCode) {
        return Base62.decode(shortCode);
    }


    public static void main(String[] args) {
        try {
            String longUrl = "https://www.example.com/very-long-url-path?param=1234131&param2=abcdefg";
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
