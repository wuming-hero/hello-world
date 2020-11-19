package com.wuming.util;

import com.google.common.base.Throwables;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.zip.*;

/**
 * 字符串压缩方法，结果并不理想
 *
 * @author wuming
 * Created on 2020-07-31 15:01
 */
@Slf4j
public class ZipUtil {

    // 压缩
    public static String compress(String str) throws IOException {
        if (str == null || str.length() == 0) {
            return str;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(out);
        gzip.write(str.getBytes());
        gzip.close();
        return out.toString("UTF-8");
    }

    // 解压缩
    public static String uncompress(String str) throws IOException {
        if (str == null || str.length() == 0) {
            return str;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(str.getBytes("UTF-8"));
        GZIPInputStream gunzip = new GZIPInputStream(in);
        byte[] buffer = new byte[256];
        int n;
        while ((n = gunzip.read(buffer)) >= 0) {
            out.write(buffer, 0, n);
        }
        // toString()使用平台默认编码，也可以显式的指定如toString(&quot;GBK&quot;)
        return out.toString();
    }

    public static final String compress2(String paramString) throws UnsupportedEncodingException {
        if (paramString == null)
            return null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        ZipOutputStream zipOutputStream = null;
        String arrayOfByte;
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            zipOutputStream = new ZipOutputStream(byteArrayOutputStream);
            zipOutputStream.putNextEntry(new ZipEntry("0"));
            zipOutputStream.write(paramString.getBytes());
            zipOutputStream.closeEntry();
            arrayOfByte = byteArrayOutputStream.toString("UTF-8");
        } catch (IOException localIOException5) {
            arrayOfByte = null;
        } finally {
            if (zipOutputStream != null)
                try {
                    zipOutputStream.close();
                } catch (IOException localIOException6) {

                }
            if (byteArrayOutputStream != null)
                try {
                    byteArrayOutputStream.close();
                } catch (IOException localIOException7) {
                }
        }
        return arrayOfByte;
    }

    public static final String decompress2(String paramArrayOfByte) {
        if (paramArrayOfByte == null) {
            return null;
        }
        ByteArrayOutputStream byteArrayOutputStream = null;
        ByteArrayInputStream byteArrayInputStream = null;
        ZipInputStream zipInputStream = null;
        String str;
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            byteArrayInputStream = new ByteArrayInputStream(paramArrayOfByte.getBytes("UTF-8"));
            zipInputStream = new ZipInputStream(byteArrayInputStream);
            ZipEntry localZipEntry = zipInputStream.getNextEntry();
            byte[] arrayOfByte = new byte[1024];
            int i = -1;
            while ((i = zipInputStream.read(arrayOfByte)) != -1)
                byteArrayOutputStream.write(arrayOfByte, 0, i);

            str = byteArrayOutputStream.toString();
        } catch (IOException localIOException7) {
            log.error(Throwables.getStackTraceAsString(localIOException7));
            str = null;
        } finally {
            if (zipInputStream != null)
                try {
                    zipInputStream.close();
                } catch (IOException localIOException8) {
                }
            if (byteArrayInputStream != null)
                try {
                    byteArrayInputStream.close();
                } catch (IOException localIOException9) {
                }
            if (byteArrayOutputStream != null)
                try {
                    byteArrayOutputStream.close();
                } catch (IOException localIOException10) {
                }
        }
        return str;
    }

    // 测试方法
    public static void main(String[] args) throws IOException {

        //测试字符串
        String str = "%5B%7B%22lastUpdateTime%22%3A%222011-10-28+9%3A39%3A41%22%2C%22smsList%22%3A%5B%7B%22liveState%22%3A%221";

        str = "igNhsm0rIuzxKh2NkHlLDSzecL89ek3l5i+ASLKDlKyUxJGRSh5YsK7BDY6MOH3G1tX2zpa/1Je0wHfKnEfAltk6a8UoeL7yC4jNte40QrbOSIagrB8e8oAJF4VtNOP7AgvI1bzkucfgrBtP31C+ntwKcIIdz3W9smDkGnhyqH8pGcf4LUEUxhpxIbMLjKot7m7oqMhSs6oW3wi4b0g2dWXZAvveZTFIOxPDNy80UvkKXBA4+daIutpiA6iNdp78/3bvIznR0T/Jjlv5l3NA6ocQuk6qagb4JcdYP1na7bcBkeYP2muH9wX2bNy1ebPGRToz9WeG7r/GGAmND11qF31PudZuNP7avCuuVcNjzYj4jxaxbkr8UGnxVPxZSWFXTWL+zmfaeglp2nLPyp3003Wye/gToKl96EcmmQr2khqMSBQBBMZtkYEyl1cvjAzrLG6YYbz41he/r9yWVQpa9+1hlpR1NsTzi7d9ExknBoqApRM9gSGrMOAE66bH5gAHIhDJySq4iPHVDc9dWHUqlqrGdvKEHVUdSMi14A9Z1m5/qCVCkLgSdQuqKSoQN52lsx+Cd+GQvG++B8yM56wI3JargFizfEkh1vn7TE+BcmZgBgYRMfBQBbU5m3gdzoqElqxYTKR4Z/6XD1zpIugFhUwO9igWFB/Xx73qUgpG7mqB+oxnwrUIvNB/rODCCHGDDMqZhh0IrHGNhY+OKgGfMjc9UxViOld4XFmap2Kz3K1Y1UaZ+udWAkklXR5tqwsdeb/XpZ/eFsoT03iq5CzFtQY6tqIDmBPFWj45wGUYsTYI0m2xvp+XDGHJKwXDEwNJ7F6BuNt0vSA1NkvLLGBhkTfdlbPxS0woLaYwyzE/M21mTckF3ya6G400tEFVN4qszFim9nkhvIRuQQWazfFaDQLBHzlr0WHR73f4lnOJ/HaqY147/MBO+gT5ItQTvVgiHbOHhSkjOlqqz0G4nWot18OfAIX9dqMDdbKBsN6pNqR7M7IhzlILe5acoOvzBn8IliaTdvG/NNgZVu17WnwD6SEpIaT8dbbnTJQIN/KqE/qbftqfuItycrHlGH0qQ30NAy0FGRJuoSjN+Vl5lBjbk8VTaALh8GkBtnvSDhE85/Sxml2vbII6tkn1DS5ilNI39RTjmLGlFhzaYfQWO3VespQ1NLAty99fxAVpI4lmTkjo1TWmR+UVID6gTGAuGopEjke4vt6JrFwM8MpJg82BOUWISqSMlqnOvb2UTE2phjT9oVq+Eh84HI/maNtQU1FnMEaQKiKKJFSxWtY1rloyc/gO3PY+EoclWS/xATbbgOz5JJKQH81Eqwi4+NVNooiVajxbE5Ucs3i9AZiEi6kH6w==159617702679851d5e4a4a49095afcc5917ec00d99";

//        System.out.println("原长度：" + str.length());
//        String compressed = compress(str);
//        System.out.println("压缩后：" + compressed.length() + "----" + compressed);
//        System.out.println("解压缩：" + uncompress(compressed));

        String compressStr = compress2(str);
        System.out.println("压缩后：" + compressStr.length() + "----" + compressStr);
        System.out.println("解压缩：" + decompress2(compressStr));
    }

}
