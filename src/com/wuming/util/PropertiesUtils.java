package com.wuming.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertiesUtils
{
  private Properties propertie;
  private FileInputStream inputFile;
  private FileOutputStream outputFile;
  
  public PropertiesUtils()
  {
    this.propertie = new Properties();
  }
  
  public PropertiesUtils(String filePath)
  {
    this.propertie = new Properties();
    try
    {
      this.inputFile = new FileInputStream(filePath);
      this.propertie.load(this.inputFile);
      this.inputFile.close();
    }
    catch (FileNotFoundException ex)
    {
      System.out.println("读取属性文件--->失败！- 原因：文件路径错误或者文件不存在");
      ex.printStackTrace();
    }
    catch (IOException ex)
    {
      System.out.println("装载文件--->失败!");
      ex.printStackTrace();
    }
  }
  
  public String getValue(String key)
  {
    if (this.propertie.containsKey(key))
    {
      String value = this.propertie.getProperty(key);
      return value;
    }
    return "";
  }
  
  public String getValue(String fileName, String key)
  {
    try
    {
      String value = "";
      this.inputFile = new FileInputStream(fileName);
      this.propertie.load(this.inputFile);
      this.inputFile.close();
      if (this.propertie.containsKey(key)) {
        return this.propertie.getProperty(key);
      }
      return value;
    }
    catch (FileNotFoundException e)
    {
      e.printStackTrace();
      return "";
    }
    catch (IOException e)
    {
      e.printStackTrace();
      return "";
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
    return "";
  }
  
  public void clear()
  {
    this.propertie.clear();
  }
  
  public void setValue(String key, String value)
  {
    this.propertie.setProperty(key, value);
  }
  
  public void saveFile(String fileName, String description)
  {
    try
    {
      this.outputFile = new FileOutputStream(fileName);
      this.propertie.store(this.outputFile, description);
      this.outputFile.close();
    }
    catch (FileNotFoundException e)
    {
      e.printStackTrace();
    }
    catch (IOException ioe)
    {
      ioe.printStackTrace();
    }
  }
  
  public static void main(String[] args)
  {
    PropertiesUtils pu = new PropertiesUtils("./config/system.properties");
    
    String admin = pu.getValue("system.admin");
    String password = pu.getValue("system.password");
    
    System.out.println("admin = " + admin);
    System.out.println("password = " + password);
    
    PropertiesUtils pu_ = new PropertiesUtils();
    
    String admin_ = pu_.getValue("./config/system.properties", "system.admin");
    String password_ = pu_.getValue("./config/system.properties", "system.password");
    
    System.out.println("admin_ = " + admin_);
    System.out.println("password_ = " + password_);
  }
}
