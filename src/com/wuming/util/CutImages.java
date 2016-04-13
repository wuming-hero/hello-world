package com.wuming.util;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.*;
import java.util.Iterator;

public class CutImages
{
  private String srcpath;
  private String subpath;
  private int x;
  private int y;
  private int width;
  private int height;
  
  public CutImages() {}
  
  public CutImages(int x, int y, int width, int height, String inFile, String outFile)
  {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
    this.srcpath = inFile;
    this.subpath = outFile;
  }
  
  public void cut()
    throws IOException
  {
    FileInputStream is = null;
    ImageInputStream iis = null;
    try
    {
      is = new FileInputStream(this.srcpath);
      








      String fileName = this.srcpath.substring(this.srcpath.lastIndexOf(".") + 1, this.srcpath.length());
      
      Iterator<ImageReader> it = ImageIO.getImageReadersByFormatName(fileName);
      
      ImageReader reader = (ImageReader)it.next();
      

      iis = ImageIO.createImageInputStream(is);
      









      reader.setInput(iis, true);
      












      ImageReadParam param = reader.getDefaultReadParam();
      








      Rectangle rect = new Rectangle(this.x, this.y, this.width, this.height);
      


      param.setSourceRegion(rect);
      








      BufferedImage bi = reader.read(0, param);
      
      ImageIO.write(bi, fileName, new File(this.subpath));
    }
    finally
    {
      if (is != null) {
        is.close();
      }
      if (iis != null) {
        iis.close();
      }
    }
  }
  
  public static File scale(File file, int widths, int heights)
  {
    String filePath = file.getAbsolutePath();
    String fileName = filePath.substring(filePath.lastIndexOf(".") + 1, filePath.length());
    try
    {
      BufferedImage src = ImageIO.read(file);
      Image image = src.getScaledInstance(widths, heights, 4);
      BufferedImage tag = new BufferedImage(widths, heights, 4);
      Graphics g = tag.getGraphics();
      g.drawImage(image, 0, 0, null);
      g.dispose();
      ImageIO.write(tag, fileName, file);
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    return file;
  }
  
  public static String getPath(String file)
  {
    String name = file.substring(file.lastIndexOf("\\") + 1, file.lastIndexOf("."));
    file = file.replaceAll(name, name);
    return file;
  }
  
  public static boolean convert(int width, int height, InputStream input, OutputStream output, String format)
    throws Exception
  {
    BufferedImage inputImage = ImageIO.read(input);
    
    RenderedImage im = convert(height, height, inputImage);
    
    return ImageIO.write(im, format, output);
  }
  
  public static boolean convert(File input, File output)
    throws Exception
  {
    BufferedImage inputImage = ImageIO.read(input);
    

    int width = inputImage.getWidth();
    int height = inputImage.getHeight();
    
    RenderedImage im = convert(width, height, inputImage);
    String outputFilename = output.getName();
    String format = outputFilename.substring(
      outputFilename.lastIndexOf('.') + 1);
    
    return ImageIO.write(im, format, output);
  }
  
  public static boolean convert(int width, int height, File input, File output)
    throws Exception
  {
    BufferedImage inputImage = ImageIO.read(input);
    
    RenderedImage im = convert(width, height, inputImage);
    String outputFilename = output.getName();
    String format = outputFilename.substring(
      outputFilename.lastIndexOf('.') + 1);
    
    return ImageIO.write(im, format, output);
  }
  
  public static boolean convert(int width, int height, String inputPath, String outputPath)
    throws Exception
  {
    return convert(width, height, new File(inputPath), new File(outputPath));
  }
  
  private static BufferedImage convert(int width, int height, BufferedImage input)
    throws Exception
  {
    BufferedImage output = new BufferedImage(width, height, 
      4);
    
    Image image = input.getScaledInstance(output.getWidth(), 
      output.getHeight(), output.getType());
    
    output.createGraphics().drawImage(image, null, null);
    
    return output;
  }
  
  public static boolean equimultipleConvert(int width, int height, String input, String output)
    throws Exception
  {
    return equimultipleConvert(width, height, new File(input), new File(
      output));
  }
  
  public static boolean equimultipleConvert(int width, int height, File input, File output)
    throws Exception
  {
    BufferedImage image = ImageIO.read(input);
    if ((image.getWidth() > 0) && (image.getHeight() > 0)) {
      if (image.getWidth() / image.getHeight() >= width / height)
      {
        if (image.getWidth() > width)
        {
          height = image.getHeight() * width / image.getWidth();
        }
        else
        {
          width = image.getWidth();
          height = image.getHeight();
        }
      }
      else if (image.getHeight() > height)
      {
        width = image.getWidth() * height / image.getHeight();
      }
      else
      {
        width = image.getWidth();
        height = image.getHeight();
      }
    }
    return convert(width, height, input, output);
  }
  
  public static final void scale2(String srcImageFile, String result, int height, int width, boolean bb)
    throws Exception
  {
    try
    {
      double ratio = 0.0D;
      File f = new File(srcImageFile);
      BufferedImage bi = ImageIO.read(f);
      Image itemp = bi.getScaledInstance(width, height, 4);
      if ((bi.getHeight() > height) || (bi.getWidth() > width))
      {
        if (bi.getHeight() > bi.getWidth()) {
          ratio = new Integer(height).doubleValue() / 
            bi.getHeight();
        } else {
          ratio = new Integer(width).doubleValue() / bi.getWidth();
        }
        AffineTransformOp op = new AffineTransformOp(
          AffineTransform.getScaleInstance(ratio, ratio), null);
        itemp = op.filter(bi, null);
      }
      if (bb)
      {
        BufferedImage image = new BufferedImage(width, height, 
          4);
        Graphics2D g = image.createGraphics();
        g.setColor(Color.white);
        g.fillRect(0, 0, width, height);
        if (width == itemp.getWidth(null)) {
          g.drawImage(itemp, 0, (height - itemp.getHeight(null)) / 2, 
            itemp.getWidth(null), itemp.getHeight(null), 
            Color.white, null);
        } else {
          g.drawImage(itemp, (width - itemp.getWidth(null)) / 2, 0, 
            itemp.getWidth(null), itemp.getHeight(null), 
            Color.white, null);
        }
        g.dispose();
        itemp = image;
      }
      ImageIO.write((BufferedImage)itemp, "JPEG", new File(result));
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
  
  public int getHeight()
  {
    return this.height;
  }
  
  public void setHeight(int height)
  {
    this.height = height;
  }
  
  public String getSrcpath()
  {
    return this.srcpath;
  }
  
  public void setSrcpath(String srcpath)
  {
    this.srcpath = srcpath;
  }
  
  public String getSubpath()
  {
    return this.subpath;
  }
  
  public void setSubpath(String subpath)
  {
    this.subpath = subpath;
  }
  
  public int getWidth()
  {
    return this.width;
  }
  
  public void setWidth(int width)
  {
    this.width = width;
  }
  
  public int getX()
  {
    return this.x;
  }
  
  public void setX(int x)
  {
    this.x = x;
  }
  
  public int getY()
  {
    return this.y;
  }
  
  public void setY(int y)
  {
    this.y = y;
  }
  
  public static void main(String[] args)
    throws Exception
  {
    System.err.println(Runtime.getRuntime().maxMemory());
    System.err.println(Runtime.getRuntime().freeMemory());
    System.err.println(Runtime.getRuntime().totalMemory());
  }
}
