package com.wuming.util;

import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class FileIOUtil
{
  public static Map<String, String> readFile2Map(String path, String regular)
    throws IOException
  {
    if (StringUtils.isBlank(regular)) {
      return null;
    }
    if (StringUtils.isBlank(path)) {
      return null;
    }
    Map<String, String> map = new HashMap();
    File file = new File(path);
    FileReader fReader = new FileReader(file);
    BufferedReader bReader = new BufferedReader(fReader);
    String temp = null;
    String[] tempArr = (String[])null;
    while ((temp = bReader.readLine()) != null)
    {
      if (StringUtils.isNotBlank(temp))
      {
        tempArr = temp.split(regular);
        if (tempArr.length > 1)
        {
          String username = tempArr[0];
          String password = tempArr[1];
          if ((StringUtils.isNotBlank(username)) && (StringUtils.isNotBlank(password))) {
            map.put(username.trim(), password.trim());
          }
        }
      }
      temp = null;
      tempArr = (String[])null;
    }
    bReader.close();
    fReader.close();
    file = null;
    return map;
  }
  
  public static String readFile2Str(String path)
  {
    if (StringUtils.isBlank(path)) {
      return null;
    }
    FileInputStream fis = null;
    InputStreamReader isr = null;
    StringBuilder str = null;
    try
    {
      fis = new FileInputStream(path);
      isr = new InputStreamReader(fis);
      BufferedReader br = new BufferedReader(isr);
      str = new StringBuilder();
      String temp = null;
      while ((temp = br.readLine()) != null)
      {
        str.append(temp);
        temp = null;
      }
    }
    catch (FileNotFoundException e)
    {
      e.printStackTrace();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    finally
    {
      if (fis != null)
      {
        try
        {
          fis.close();
        }
        catch (IOException e)
        {
          e.printStackTrace();
        }
        fis = null;
      }
      if (isr != null)
      {
        try
        {
          isr.close();
        }
        catch (IOException e)
        {
          e.printStackTrace();
        }
        isr = null;
      }
    }
    return str.toString();
  }
  
  public static String convertInputStream2Str(InputStream is, String encoding)
  {
    if (is == null) {
      return null;
    }
    if (StringUtils.isBlank(encoding)) {
      return null;
    }
    StringBuffer sb = null;
    BufferedReader br = null;
    try
    {
      br = new BufferedReader(new InputStreamReader(is, encoding));
      
      sb = new StringBuffer(100);
      String tempbf=null;
      while ((tempbf = br.readLine()) != null)
      {
        sb.append(tempbf);
      }
      return sb.toString();
    }
    catch (UnsupportedEncodingException e)
    {
      e.printStackTrace();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    finally
    {
      if (br != null)
      {
        try
        {
          br.close();
        }
        catch (IOException e)
        {
          e.printStackTrace();
        }
        br = null;
      }
    }
    return null;
  }
  
  public static boolean writeString2File(String content, String path)
    throws Exception
  {
    if (StringUtils.isBlank(content)) {
      return false;
    }
    if (StringUtils.isBlank(path)) {
      return false;
    }
    File file = new File(path);
    FileWriter fileWriter = null;
    try
    {
      fileWriter = new FileWriter(file);
      fileWriter.write(content);
      return true;
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    finally
    {
      if (fileWriter != null)
      {
        try
        {
          fileWriter.close();
        }
        catch (IOException e)
        {
          e.printStackTrace();
        }
        fileWriter = null;
      }
      if (file != null) {
        file = null;
      }
    }
    return false;
  }
}
