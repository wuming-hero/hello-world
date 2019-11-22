package com.wuming.util;

import com.wuming.component.AbstractNetImage;
import com.wuming.component.JpegNetImage;
import org.apache.commons.lang3.StringUtils;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.image.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 图片裁剪、压缩、加水印
 */
public class ImageUtils {
    public static String IMAGE_TYPE_GIF = "gif";
    public static String IMAGE_TYPE_JPG = "jpg";
    public static String IMAGE_TYPE_JPEG = "jpeg";
    public static String IMAGE_TYPE_BMP = "bmp";
    public static String IMAGE_TYPE_PNG = "png";
    public static String IMAGE_TYPE_PSD = "psd";

    public static final void scale(String srcImageFile, String result, int scale, boolean flag) {
        try {
            BufferedImage src = ImageIO.read(new File(srcImageFile));
            int width = src.getWidth();
            int height = src.getHeight();
            if (flag) {
                width *= scale;
                height *= scale;
            } else {
                width /= scale;
                height /= scale;
            }
            Image image = src.getScaledInstance(width, height, 4);
            BufferedImage tag = new BufferedImage(width, height, 4);
            Graphics g = tag.getGraphics();
            g.drawImage(image, 0, 0, null);
            g.dispose();
            ImageIO.write(tag, "JPEG", new File(result));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static final void scale2(String srcImageFile, String result, int height, int width, boolean bb) {
        try {
            double ratio = 0.0D;
            File f = new File(srcImageFile);
            BufferedImage bi = ImageIO.read(f);
            Image itemp = bi.getScaledInstance(width, height, 4);
            if ((bi.getHeight() > height) || (bi.getWidth() > width)) {
                if (bi.getHeight() > bi.getWidth()) {
                    ratio = new Integer(height).doubleValue() / bi.getHeight();
                } else {
                    ratio = new Integer(width).doubleValue() / bi.getWidth();
                }
                AffineTransformOp op = new AffineTransformOp(AffineTransform.getScaleInstance(ratio, ratio), null);
                itemp = op.filter(bi, null);
            }
            if (bb) {
                BufferedImage image = new BufferedImage(width, height, 4);
                Graphics2D g = image.createGraphics();
                g.setColor(Color.white);
                g.fillRect(0, 0, width, height);
                if (width == itemp.getWidth(null)) {
                    g.drawImage(itemp, 0, (height - itemp.getHeight(null)) / 2, itemp.getWidth(null),
                            itemp.getHeight(null), Color.white, null);
                } else {
                    g.drawImage(itemp, (width - itemp.getWidth(null)) / 2, 0, itemp.getWidth(null),
                            itemp.getHeight(null), Color.white, null);
                }
                g.dispose();
                itemp = image;
            }
            ImageIO.write((BufferedImage) itemp, "JPEG", new File(result));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static final void cut(String srcImageFile, String result, int x, int y, int width, int height) {
        try {
            BufferedImage bi = ImageIO.read(new File(srcImageFile));
            int srcWidth = bi.getHeight();
            int srcHeight = bi.getWidth();
            if ((srcWidth > 0) && (srcHeight > 0)) {
                Image image = bi.getScaledInstance(srcWidth, srcHeight, 1);

                ImageFilter cropFilter = new CropImageFilter(x, y, width, height);
                Image img = Toolkit.getDefaultToolkit()
                        .createImage(new FilteredImageSource(image.getSource(), cropFilter));
                BufferedImage tag = new BufferedImage(width, height, 1);
                Graphics g = tag.getGraphics();
                g.drawImage(img, 0, 0, width, height, null);
                g.dispose();

                ImageIO.write(tag, "JPEG", new File(result));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static final void cut2(String srcImageFile, String descDir, int rows, int cols) {
        try {
            if ((rows <= 0) || (rows > 20)) {
                rows = 2;
            }
            if ((cols <= 0) || (cols > 20)) {
                cols = 2;
            }
            BufferedImage bi = ImageIO.read(new File(srcImageFile));
            int srcWidth = bi.getHeight();
            int srcHeight = bi.getWidth();
            if ((srcWidth > 0) && (srcHeight > 0)) {
                Image image = bi.getScaledInstance(srcWidth, srcHeight, 1);
                int destWidth = srcWidth;
                int destHeight = srcHeight;
                if (srcWidth % cols == 0) {
                    destWidth = srcWidth / cols;
                } else {
                    destWidth = (int) Math.floor(srcWidth / cols) + 1;
                }
                if (srcHeight % rows == 0) {
                    destHeight = srcHeight / rows;
                } else {
                    destHeight = (int) Math.floor(srcWidth / rows) + 1;
                }
                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < cols; j++) {
                        ImageFilter cropFilter = new CropImageFilter(j * destWidth, i * destHeight, destWidth,
                                destHeight);
                        Image img = Toolkit.getDefaultToolkit()
                                .createImage(new FilteredImageSource(image.getSource(), cropFilter));
                        BufferedImage tag = new BufferedImage(destWidth, destHeight, 1);
                        Graphics g = tag.getGraphics();
                        g.drawImage(img, 0, 0, null);
                        g.dispose();

                        ImageIO.write(tag, "JPEG", new File(descDir + "_r" + i + "_c" + j + ".jpg"));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static final void cut3(String srcImageFile, String descDir, int destWidth, int destHeight) {
        try {
            if (destWidth <= 0) {
                destWidth = 200;
            }
            if (destHeight <= 0) {
                destHeight = 150;
            }
            BufferedImage bi = ImageIO.read(new File(srcImageFile));
            int srcWidth = bi.getHeight();
            int srcHeight = bi.getWidth();
            if ((srcWidth > destWidth) && (srcHeight > destHeight)) {
                Image image = bi.getScaledInstance(srcWidth, srcHeight, 1);
                int cols = 0;
                int rows = 0;
                if (srcWidth % destWidth == 0) {
                    cols = srcWidth / destWidth;
                } else {
                    cols = (int) Math.floor(srcWidth / destWidth) + 1;
                }
                if (srcHeight % destHeight == 0) {
                    rows = srcHeight / destHeight;
                } else {
                    rows = (int) Math.floor(srcHeight / destHeight) + 1;
                }
                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < cols; j++) {
                        ImageFilter cropFilter = new CropImageFilter(j * destWidth, i * destHeight, destWidth,
                                destHeight);
                        Image img = Toolkit.getDefaultToolkit()
                                .createImage(new FilteredImageSource(image.getSource(), cropFilter));
                        BufferedImage tag = new BufferedImage(destWidth, destHeight, 1);
                        Graphics g = tag.getGraphics();
                        g.drawImage(img, 0, 0, null);
                        g.dispose();

                        ImageIO.write(tag, "JPEG", new File(descDir + "_r" + i + "_c" + j + ".jpg"));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static final void convert(String srcImageFile, String formatName, String destImageFile) {
        try {
            File f = new File(srcImageFile);
            f.canRead();
            f.canWrite();
            BufferedImage src = ImageIO.read(f);
            ImageIO.write(src, formatName, new File(destImageFile));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static final void gray(String srcImageFile, String destImageFile) {
        try {
            BufferedImage src = ImageIO.read(new File(srcImageFile));
            ColorSpace cs = ColorSpace.getInstance(1003);
            ColorConvertOp op = new ColorConvertOp(cs, null);
            src = op.filter(src, null);
            ImageIO.write(src, "JPEG", new File(destImageFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static final void pressText(String pressText, String srcImageFile, String destImageFile, String fontName,
                                       int fontStyle, Color color, int fontSize, int x, int y, float alpha) {
        try {
            File img = new File(srcImageFile);
            Image src = ImageIO.read(img);
            int width = src.getWidth(null);
            int height = src.getHeight(null);
            BufferedImage image = new BufferedImage(width, height, 1);
            Graphics2D g = image.createGraphics();
            g.drawImage(src, 0, 0, width, height, null);
            g.setColor(color);
            g.setFont(new Font(fontName, fontStyle, fontSize));
            g.setComposite(AlphaComposite.getInstance(10, alpha));

            g.drawString(pressText, (width - getLength(pressText) * fontSize) / 2 + x, (height - fontSize) / 2 + y);
            g.dispose();
            ImageIO.write(image, "JPEG", new File(destImageFile));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static final void pressText2(String pressText, String srcImageFile, String destImageFile, String fontName,
                                        int fontStyle, Color color, int fontSize, int x, int y, float alpha) {
        try {
            File img = new File(srcImageFile);
            Image src = ImageIO.read(img);
            int width = src.getWidth(null);
            int height = src.getHeight(null);
            BufferedImage image = new BufferedImage(width, height, 1);
            Graphics2D g = image.createGraphics();
            g.drawImage(src, 0, 0, width, height, null);
            g.setColor(color);
            g.setFont(new Font(fontName, fontStyle, fontSize));
            g.setComposite(AlphaComposite.getInstance(10, alpha));

            g.drawString(pressText, (width - getLength(pressText) * fontSize) / 2 + x, (height - fontSize) / 2 + y);
            g.dispose();
            ImageIO.write(image, "JPEG", new File(destImageFile));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static final void pressImage(String pressImg, String srcImageFile, String destImageFile, int x, int y,
                                        float alpha) {
        try {
            File img = new File(srcImageFile);
            Image src = ImageIO.read(img);
            int wideth = src.getWidth(null);
            int height = src.getHeight(null);
            BufferedImage image = new BufferedImage(wideth, height, 1);
            Graphics2D g = image.createGraphics();
            g.drawImage(src, 0, 0, wideth, height, null);

            Image src_biao = ImageIO.read(new File(pressImg));
            int wideth_biao = src_biao.getWidth(null);
            int height_biao = src_biao.getHeight(null);
            g.setComposite(AlphaComposite.getInstance(10, alpha));
            g.drawImage(src_biao, x, y, wideth_biao, height_biao, null);

            g.dispose();
            ImageIO.write(image, "JPEG", new File(destImageFile));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static final void circleImage(String srcImageFile, String distImageFile) {
        BufferedImage bi1 = null;
        try {
            bi1 = ImageIO.read(new File(srcImageFile));
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        // 根据需要是否使用 BufferedImage.TYPE_INT_ARGB
        BufferedImage image = new BufferedImage(bi1.getWidth(), bi1.getHeight(), BufferedImage.TYPE_INT_RGB);

        Ellipse2D.Double shape = new Ellipse2D.Double(0, 0, bi1.getWidth(), bi1.getHeight());

        Graphics2D g2 = image.createGraphics();
        image = g2.getDeviceConfiguration().createCompatibleImage(bi1.getWidth(), bi1.getHeight(),
                Transparency.TRANSLUCENT);
        g2 = image.createGraphics();
        g2.setComposite(AlphaComposite.Clear);
        g2.fill(new Rectangle(image.getWidth(), image.getHeight()));
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, 1.0f));
        g2.setClip(shape);
        // 使用 setRenderingHint 设置抗锯齿
        g2.drawImage(bi1, 0, 0, null);
        g2.dispose();

        try {
            ImageIO.write(image, "PNG", new File(distImageFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static final int getLength(String text) {
        int length = 0;
        for (int i = 0; i < text.length(); i++) {
            if ((text.charAt(i) + "").getBytes().length > 1) {
                length += 2;
            } else {
                length++;
            }
        }
        return length / 2;
    }

    /**
     * 读取图片路径，返回图片base64字符串
     *
     * @param imgFile
     * @return
     */
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
        return encoder.encode(data);
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
            BufferedReader bf = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = "";
            while ((line = bf.readLine()) != null) {
                System.out.println(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 下载网络图片
     *
     * @param netUrl
     * @param fileFolder
     * @param fileName
     */
    public static void readNetImageToLocal(String netUrl, String fileFolder, String fileName) {
        String filePath = fileFolder + fileName;
        java.util.List<String> list = new ArrayList();
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
        AbstractNetImage abstractNetImage = new JpegNetImage();
        try {
            abstractNetImage.getImageFromUrl(netUrl, filePath);
            System.out.println("图片下载成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 把网络图片读取成流
     *
     * @param netUrl
     * @return
     */
    public static ByteArrayOutputStream readNetImageToStream(String netUrl) {
        List<String> list = new ArrayList();
        AbstractNetImage abstractNetImage = new JpegNetImage();
        try {
            return abstractNetImage.getImageToOutStream(netUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * fileData base64编码的图片字符串（不包含图片格式信息）
     *
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
}
