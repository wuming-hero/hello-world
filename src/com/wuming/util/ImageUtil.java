package com.wuming.util;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ImageUtil {
    protected static final Log _log = LogFactory.getLog(ImageUtil.class);

    public static void main(String[] args) {
        String imgFile = "d:\\123.jpg";
        String netUrl = "http://t3.qlogo.cn/mbloghead/35f3469a2fa055c25ec2/120";

        String fileFolder = "D:/weibo-image/20110120/123/456/789/";
        String fileName = "posterface.jpg";


        String meinv_url = "http://126.fm/nlC2n";
        String face_url = "http://tp4.sinaimg.cn/1427388087/180/1297002209/1";
        for (int i = 0; i < 100; i++) {
            readNetImageToLocal(meinv_url, "d:/meinv/", i + "meinv.jpg");
        }
    }

    public static String imageToBase64(String imgFile) {
        InputStream in = null;
        byte[] data = (byte[]) null;
        try {
            in = new FileInputStream(imgFile);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        BASE64Encoder encoder = new BASE64Encoder();
        String cc = encoder.encode(data);
        return cc;
    }

    public static void readImageToLabel(String netUrl) {
        Image image = null;
        try {
            URL url = new URL(netUrl);

            image = ImageIO.read(url);
        } catch (IOException localIOException) {
        }
        JFrame frame = new JFrame();
        JLabel label = new JLabel(new ImageIcon(image));
        frame.getContentPane().add(label, "Center");
        frame.pack();
        frame.setVisible(true);

        frame.setDefaultCloseOperation(3);
    }

    private static void makeDir(String fileFolder) {
        File file = new File(fileFolder);
        if ((!file.exists()) && (!file.isDirectory())) {
            file.mkdir();
        }
    }

    private static void openFileSystemDir(String dir) {
        String openDirCmdString = "cmd.exe /c start ";
        String openDirCmd = openDirCmdString + dir;
        System.out.println(openDirCmd);
        try {
            Runtime r = Runtime.getRuntime();
            Process p = r.exec(openDirCmd);
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    p.getInputStream()));
            String line = "";
            while ((line = bf.readLine()) != null) {
                System.out.println(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void readNetImageToLocal(String netUrl, String fileFolder, String fileName) {
        String filePath = fileFolder + fileName;

        List<String> list = new ArrayList();
        String supDir = StringUtils.substring(fileFolder, 0, StringUtils.indexOf(fileFolder, "/") + 1);
        String subDir = StringUtils.substring(fileFolder, StringUtils.indexOf(fileFolder, "/") + 1);
        String dir;
        for (int i = 0; i < StringUtils.countMatches(fileFolder, "/") - 1; i++) {
            dir = supDir + StringUtils.substring(subDir, 0, StringUtils.indexOf(subDir, "/") + 1);
            subDir = StringUtils.substring(subDir, StringUtils.indexOf(subDir, "/") + 1);
            list.add(dir);
            supDir = dir;
        }
        for (String s : list) {
            makeDir(s);
        }
        AbstractNetImage ani = new JpegNetImage();
        try {
            ani.getImageFromUrl(netUrl, filePath);
            System.out.println("图片下载成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static ByteArrayOutputStream readNetImageToStream(String netUrl) {

        List<String> list = new ArrayList();

        AbstractNetImage ani = new JpegNetImage();
        try {
            return ani.getImageToOutStream(netUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

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
