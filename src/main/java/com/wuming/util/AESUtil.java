package com.wuming.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;

/**
 * @author wuming
 * Created on 2019-06-25 14:33
 */
public class AESUtil {

    /**
     * 密钥算法
     */
    private static final String ALGORITHM = "AES";
    /**
     * 加解密算法/工作模式/填充方式
     */
    private static final String ALGORITHM_STR = "AES/ECB/PKCS5Padding";

    /**
     * AES加密
     * <p>
     * 加密:先采用 AES/ECB/PKCS5Padding，再采用 base64 编码
     *
     * @param data   明文
     * @param hexKey 密钥
     * @return
     * @throws Exception
     */
    public static String encrypt(String data, String hexKey) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM_STR); // 创建密码器
        Key key = new SecretKeySpec(hexKey.getBytes(StandardCharsets.UTF_8), ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化
        return Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes(StandardCharsets.UTF_8)));
    }

    /**
     * AES解密
     * <p>
     * 解密:先采用 base64 解码，再采用 AES/ECB/PKCS5Padding 解密
     *
     * @param base64Data 密文
     * @param hexKey     密钥
     * @return
     * @throws Exception
     */
    public static String decrypt(String base64Data, String hexKey) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM_STR);
        Key key = new SecretKeySpec(hexKey.getBytes(StandardCharsets.UTF_8), ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return new String(cipher.doFinal(Base64.getDecoder().decode(base64Data)), StandardCharsets.UTF_8);
    }

}
