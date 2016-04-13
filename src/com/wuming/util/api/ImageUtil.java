package com.wuming.util.api;

import com.wuming.util.Base64;

import java.io.*;

/**
 * Created by wuming on 16/2/22.
 */
public class ImageUtil {

    /**
     * fileData base64编码的图片字符串,以"data:image/jpeg;base64,"开头
     * @param fileData
     * @param filePath
     * @throws IOException
     */
    public static void uploadImage(String fileData, String filePath) throws IOException {
        byte[] fileBytes = Base64.decode(fileData);
        BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(fileBytes));
        String path = filePath.substring(0, filePath.lastIndexOf("/"));
        File saveDir = new File(path);// path1为存放的路径
        if (!saveDir.exists()) {// 如果不存在文件夹，则自动生成
            saveDir.mkdirs();
        }
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] buffer = new byte[1024];
        int byteread = 0;
        while ((byteread = bis.read(buffer)) != -1) {
            bos.write(buffer, 0, byteread); // 文件写操作
        }
        bos.flush();
        bos.close();
        bis.close();
    }
}
