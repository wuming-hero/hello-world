package com.wuming.util;

import java.io.*;
import java.util.Properties;

public class PropertiesTest
{
  public static void main(String[] args)
  {
    String readfile = "d:" + File.separator + "readfile.properties";
    String writefile = "d:" + File.separator + "writefile.properties";
    String readxmlfile = "d:" + File.separator + "readxmlfile.xml";
    String writexmlfile = "d:" + File.separator + "writexmlfile.xml";
    String readtxtfile = "d:" + File.separator + "readtxtfile.txt";
    String writetxtfile = "d:" + File.separator + "writetxtfile.txt";
    

    writePropertiesFile(writefile);
  }
  
  public static void readPropertiesFile(String filename)
  {
    Properties properties = new Properties();
    try
    {
      InputStream inputStream = new FileInputStream(filename);
      properties.load(inputStream);
      inputStream.close();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    String username = properties.getProperty("username");
    String passsword = properties.getProperty("password");
    String chinese = properties.getProperty("chinese");
    try
    {
      chinese = new String(chinese.getBytes("ISO-8859-1"), "GBK");
    }
    catch (UnsupportedEncodingException e)
    {
      e.printStackTrace();
    }
    System.out.println(username);
    System.out.println(passsword);
    System.out.println(chinese);
  }
  
  public static void readPropertiesFileFromXML(String filename)
  {
    Properties properties = new Properties();
    try
    {
      InputStream inputStream = new FileInputStream(filename);
      properties.loadFromXML(inputStream);
      inputStream.close();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    String username = properties.getProperty("username");
    String passsword = properties.getProperty("password");
    String chinese = properties.getProperty("chinese");
    System.out.println(username);
    System.out.println(passsword);
    System.out.println(chinese);
  }
  
  public static void writePropertiesFile(String filename)
  {
    Properties properties = new Properties();
    try
    {
      OutputStream outputStream = new FileOutputStream(filename);
      properties.setProperty("username", "1ypg");
      properties.setProperty("password", "123");
      properties.setProperty("chinese", "中文");
      properties.store(outputStream, "author: service@ixiye.com");
      outputStream.close();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
  
  public static void writePropertiesFileToXML(String filename)
  {
    Properties properties = new Properties();
    try
    {
      OutputStream outputStream = new FileOutputStream(filename);
      properties.setProperty("username", "myname");
      properties.setProperty("password", "mypassword");
      properties.setProperty("chinese", "中文");
      properties.storeToXML(outputStream, "author: service@ixiye.com");
      outputStream.close();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
}
