package com.wuming.util;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import java.io.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import org.apache.commons.codec.binary.Base64;

public class CryptoUtil {
    /**
     * 数字签名函数入口
     *
     * @param plainBytes
     *            待签名明文字节数组
     * @param privateKey
     *            签名使用私钥
     * @param signAlgorithm
     *            签名算法
     * @return 签名后的字节数组
     * @throws Exception
     */
    public static byte[] digitalSign(byte[] plainBytes, PrivateKey privateKey, String signAlgorithm) throws Exception {
        try {
            Signature signature = Signature.getInstance(signAlgorithm);
            signature.initSign(privateKey);
            signature.update(plainBytes);
            byte[] signBytes = signature.sign();

            return signBytes;
        } catch (NoSuchAlgorithmException e) {
            throw new Exception(String.format("数字签名时没有[%s]此类算法", signAlgorithm));
        } catch (InvalidKeyException e) {
            throw new Exception("数字签名时私钥无效");
        } catch (SignatureException e) {
            throw new Exception("数字签名时出现异常");
        }
    }

    /**
     * 验证数字签名函数入口
     *
     * @param plainBytes
     *            待验签明文字节数组
     * @param signBytes
     *            待验签签名后字节数组
     * @param publicKey
     *            验签使用公钥
     * @param signAlgorithm
     *            签名算法
     * @return 验签是否通过
     * @throws Exception
     */
    public static boolean verifyDigitalSign(byte[] plainBytes, byte[] signBytes, PublicKey publicKey, String signAlgorithm) throws Exception {
        boolean isValid = false;
        try {
            Signature signature = Signature.getInstance(signAlgorithm);
            signature.initVerify(publicKey);
            signature.update(plainBytes);
            isValid = signature.verify(signBytes);
            return isValid;
        } catch (NoSuchAlgorithmException e) {
            throw new Exception(String.format("验证数字签名时没有[%s]此类算法", signAlgorithm));
        } catch (InvalidKeyException e) {
            throw new Exception("验证数字签名时公钥无效");
        } catch (SignatureException e) {
            throw new Exception("验证数字签名时出现异常");
        }
    }

    /**
     * 获取RSA公钥对象
     *
     * @param filePath
     *            RSA公钥路径
     * @param keyAlgorithm
     *            密钥算法
     * @return RSA公钥对象
     * @throws Exception
     */
    public static PublicKey getRSAPublicKeyByFileSuffix(String filePath, String keyAlgorithm) throws Exception {
        InputStream in = null;
        try {
            in = new FileInputStream(filePath);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            StringBuilder sb = new StringBuilder();
            String readLine = null;
            while ((readLine = br.readLine()) != null) {
                if (readLine.charAt(0) == '-') {
                    continue;
                } else {
                    sb.append(readLine);
                    sb.append('\r');
                }
            }
            X509EncodedKeySpec pubX509 = new X509EncodedKeySpec(Base64.decodeBase64(sb.toString()));
            KeyFactory keyFactory = KeyFactory.getInstance(keyAlgorithm);
            PublicKey pubKey = keyFactory.generatePublic(pubX509);

            return pubKey;
        } catch (FileNotFoundException e) {
            throw new Exception("公钥路径文件不存在");
        } catch (IOException e) {
            throw new Exception("读取公钥异常");
        } catch (NoSuchAlgorithmException e) {
            throw new Exception(String.format("生成密钥工厂时没有[%s]此类算法", keyAlgorithm));
        } catch (InvalidKeySpecException e) {
            throw new Exception("生成公钥对象异常");
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
            }
        }
    }

    /**
     * 获取RSA私钥对象
     *
     * @param filePath
     *            RSA私钥路径
     * @param keyAlgorithm
     *            密钥算法
     * @return RSA私钥对象
     * @throws Exception
     */
    @SuppressWarnings("deprecation")
    public static PrivateKey getRSAPrivateKeyByFileSuffix(String filePath, String keyAlgorithm) throws Exception {
        InputStream in = null;
        try {
            in = new FileInputStream(filePath);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            StringBuilder sb = new StringBuilder();
            String readLine = null;
            while ((readLine = br.readLine()) != null) {
                if (readLine.charAt(0) == '-') {
                    continue;
                } else {
                    sb.append(readLine);
                    sb.append('\r');
                }
            }
            PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64.decodeBase64(sb.toString()));
            KeyFactory keyFactory = KeyFactory.getInstance(keyAlgorithm);
            PrivateKey priKey = keyFactory.generatePrivate(priPKCS8);

            return priKey;
        } catch (FileNotFoundException e) {
            throw new Exception("私钥路径文件不存在");
        } catch (IOException e) {
            throw new Exception("读取私钥异常");
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("生成私钥对象异常");
        } catch (InvalidKeySpecException e) {
            throw new Exception("生成私钥对象异常");
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
            }
        }
    }

    /**
     * RSA加密
     *
     * @param plainBytes
     *            明文字节数组
     * @param publicKey
     *            公钥
     * @param keyLength
     *            密钥bit长度
     * @param reserveSize
     *            padding填充字节数，预留11字节
     * @param cipherAlgorithm
     *            加解密算法，一般为RSA/ECB/PKCS1Padding
     * @return 加密后字节数组，不经base64编码
     * @throws Exception
     */
    public static byte[] RSAEncrypt(byte[] plainBytes, PublicKey publicKey, int keyLength, int reserveSize, String cipherAlgorithm) throws Exception {
        int keyByteSize = keyLength / 8; // 密钥字节数
        int encryptBlockSize = keyByteSize - reserveSize; // 加密块大小=密钥字节数-padding填充字节数
        int nBlock = plainBytes.length / encryptBlockSize;// 计算分段加密的block数，向上取整
        if ((plainBytes.length % encryptBlockSize) != 0) { // 余数非0，block数再加1
            nBlock += 1;
        }

        try {
            Cipher cipher = Cipher.getInstance(cipherAlgorithm);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            // 输出buffer，大小为nBlock个keyByteSize
            ByteArrayOutputStream outbuf = new ByteArrayOutputStream(nBlock * keyByteSize);
            // 分段加密
            for (int offset = 0; offset < plainBytes.length; offset += encryptBlockSize) {
                int inputLen = plainBytes.length - offset;
                if (inputLen > encryptBlockSize) {
                    inputLen = encryptBlockSize;
                }

                // 得到分段加密结果
                byte[] encryptedBlock = cipher.doFinal(plainBytes, offset, inputLen);
                // 追加结果到输出buffer中
                outbuf.write(encryptedBlock);
            }

            outbuf.flush();
            outbuf.close();
            return outbuf.toByteArray();
        } catch (NoSuchAlgorithmException e) {
            throw new Exception(String.format("没有[%s]此类加密算法", cipherAlgorithm));
        } catch (NoSuchPaddingException e) {
            throw new Exception(String.format("没有[%s]此类填充模式", cipherAlgorithm));
        } catch (InvalidKeyException e) {
            throw new Exception("无效密钥");
        } catch (IllegalBlockSizeException e) {
            throw new Exception("加密块大小不合法");
        } catch (BadPaddingException e) {
            throw new Exception("错误填充模式");
        } catch (IOException e) {
            throw new Exception("字节输出流异常");
        }
    }

    /**
     * RSA解密
     *
     * @param encryptedBytes
     *            加密后字节数组
     * @param privateKey
     *            私钥
     * @param keyLength
     *            密钥bit长度
     * @param reserveSize
     *            padding填充字节数，预留11字节
     * @param cipherAlgorithm
     *            加解密算法，一般为RSA/ECB/PKCS1Padding
     * @return 解密后字节数组，不经base64编码
     * @throws Exception
     */
    public static byte[] RSADecrypt(byte[] encryptedBytes, PrivateKey privateKey, int keyLength, int reserveSize, String cipherAlgorithm) throws Exception {
        int keyByteSize = keyLength / 8; // 密钥字节数
        int decryptBlockSize = keyByteSize - reserveSize; // 解密块大小=密钥字节数-padding填充字节数
        int nBlock = encryptedBytes.length / keyByteSize;// 计算分段解密的block数，理论上能整除

        try {
            Cipher cipher = Cipher.getInstance(cipherAlgorithm);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);

            // 输出buffer，大小为nBlock个decryptBlockSize
            ByteArrayOutputStream outbuf = new ByteArrayOutputStream(nBlock * decryptBlockSize);
            // 分段解密
            for (int offset = 0; offset < encryptedBytes.length; offset += keyByteSize) {
                // block大小: decryptBlock 或 剩余字节数
                int inputLen = encryptedBytes.length - offset;
                if (inputLen > keyByteSize) {
                    inputLen = keyByteSize;
                }

                // 得到分段解密结果
                byte[] decryptedBlock = cipher.doFinal(encryptedBytes, offset, inputLen);
                // 追加结果到输出buffer中
                outbuf.write(decryptedBlock);
            }

            outbuf.flush();
            outbuf.close();
            return outbuf.toByteArray();
        } catch (NoSuchAlgorithmException e) {
            throw new Exception(String.format("没有[%s]此类解密算法", cipherAlgorithm));
        } catch (NoSuchPaddingException e) {
            throw new Exception(String.format("没有[%s]此类填充模式", cipherAlgorithm));
        } catch (InvalidKeyException e) {
            throw new Exception("无效密钥");
        } catch (IllegalBlockSizeException e) {
            throw new Exception("解密块大小不合法");
        } catch (BadPaddingException e) {
            throw new Exception("错误填充模式");
        } catch (IOException e) {
            throw new Exception("字节输出流异常");
        }
    }




    private static String RSA_CONFIGURATION = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";
    private static String RSA_PROVIDER = "BC";

    public static byte[] decrypt(byte[] encryptedBytes, PrivateKey privateKey, int keyLength, int reserveSize) throws Exception {
//        Cipher c = Cipher.getInstance(RSA_CONFIGURATION, RSA_PROVIDER);
//        c.init(Cipher.DECRYPT_MODE, key, new OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA1,
//                PSource.PSpecified.DEFAULT));
//        byte[] decodedBytes = c.doFinal(Base64.decode(encryptedString.getBytes("UTF-8"), Base64.DEFAULT));
//        clearText = new String(decodedBytes, "UTF-8");
        int keyByteSize = keyLength / 8; // 密钥字节数
        int decryptBlockSize = keyByteSize - reserveSize; // 解密块大小=密钥字节数-padding填充字节数
        int nBlock = encryptedBytes.length / keyByteSize;// 计算分段解密的block数，理论上能整除

        try {
            Cipher c = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
            c.init(Cipher.DECRYPT_MODE, privateKey, new OAEPParameterSpec("SHA-256",
                    "MGF1", MGF1ParameterSpec.SHA256, PSource.PSpecified.DEFAULT));

            // 输出buffer，大小为nBlock个decryptBlockSize
            ByteArrayOutputStream outbuf = new ByteArrayOutputStream(nBlock * decryptBlockSize);
            // 分段解密
            for (int offset = 0; offset < encryptedBytes.length; offset += keyByteSize) {
                // block大小: decryptBlock 或 剩余字节数
                int inputLen = encryptedBytes.length - offset;
                if (inputLen > keyByteSize) {
                    inputLen = keyByteSize;
                }

                // 得到分段解密结果
                byte[] decryptedBlock = c.doFinal(encryptedBytes, offset, inputLen);
                // 追加结果到输出buffer中
                outbuf.write(decryptedBlock);
            }

            outbuf.flush();
            outbuf.close();
            return outbuf.toByteArray();
        } catch (NoSuchAlgorithmException e) {
            throw new Exception(String.format("没有[%s]此类解密算法", "RSA"));
        } catch (NoSuchPaddingException e) {
            throw new Exception(String.format("没有[%s]此类填充模式", "RSA"));
        } catch (InvalidKeyException e) {
            throw new Exception("无效密钥");
        } catch (IllegalBlockSizeException e) {
            throw new Exception("解密块大小不合法");
        } catch (BadPaddingException e) {
            throw new Exception("错误填充模式");
        } catch (IOException e) {
            throw new Exception("字节输出流异常");
        }
//            byte[] plainTextBytes = c.doFinal(encryptedBytes);
//            String plainText = new String(plainTextBytes);
//
//            System.out.println(plainText);
//            return plainText;
    }



    public static void main(String[] args) {

        try {
            final PublicKey yhPubKey = CryptoUtil.getRSAPublicKeyByFileSuffix("/Users/wuming/Downloads/rsa_public_key_2048.pem", "RSA");
            final PrivateKey hzfPriKey = CryptoUtil.getRSAPrivateKeyByFileSuffix("/Users/wuming/Downloads/pkcs8_rsa_private_key_2048.pem", "RSA");
            StringBuffer s = new StringBuffer();
            for(int i=0;i<600;i++){
                s.append("1");
            }
//            System.out.println(s.length() + "" + s);

//            String plainXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>...";
            String plainXML = s.toString();

            byte[] signData = CryptoUtil.digitalSign(plainXML.getBytes("UTF-8"), hzfPriKey, "SHA1WithRSA");// 签名
//            byte[] encrtptData = CryptoUtil.RSAEncrypt(plainXML.getBytes("UTF-8"), yhPubKey, 2048, 11, "RSA/ECB/PKCS1Padding");// 加密
            //确定写出文件的位置
//            File file = new File("/Users/wuming/Downloads/encryData.txt");
//            FileOutputStream fos = new FileOutputStream(file);
//            fos.write(encrtptData);
//            System.out.println("写入成功");
//            fos.close();
            File file = new File("/Users/wuming/Downloads/encryData(10).txt");
            long fileSize = file.length();

            FileInputStream fi = new FileInputStream(file);
            byte[] encrtptData = new byte[(int) fileSize];
            int offset = 0;
            int numRead = 0;
            while (offset < encrtptData.length
                    && (numRead = fi.read(encrtptData, offset, encrtptData.length - offset)) >= 0) {
                offset += numRead;
            }
            // 确保所有数据均被读取
            if (offset != encrtptData.length) {
                throw new IOException("Could not completely read file "
                        + file.getName());
            }
            fi.close();
//            byte[] decryptData = CryptoUtil.decrypt(encrtptData, hzfPriKey, 2048, 11);

            byte[] decryptData = CryptoUtil.RSADecrypt(encrtptData, hzfPriKey, 2048, 11, "RSA/ECB/PKCS1Padding");// 解密

            System.out.println(decryptData.length + "----------" + new String(decryptData, "UTF-8"));
//            boolean verifySign = CryptoUtil.verifyDigitalSign(decryptData, signData, yhPubKey, "SHA1WithRSA");// 验签

//            System.out.println(verifySign);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
