package com.wuming.util;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EncryptUtil
{
  public static synchronized String encryptBASE64(String str)
  {
    if ((str == null) || ("".equals(str.trim())))
    {
      System.err.println("EncryptUtil.encryptBASE64()==>>>>参数为空!");
      return null;
    }
    return new BASE64Encoder().encodeBuffer(str.getBytes());
  }
  
  public static synchronized String decryptBASE64(String str)
  {
    if ((str == null) || ("".equals(str.trim())))
    {
      System.err.println("EncryptUtil.decryptBASE64()==>>>>参数为空!");
      return null;
    }
    try
    {
      byte[] tmp = new BASE64Decoder().decodeBuffer(str);
      return new String(tmp);
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    return null;
  }
  
  public static synchronized String encryptMD5(String str)
  {
    if ((str == null) || ("".equals(str.trim())))
    {
      System.err.println("EncryptUtil.encryptMD5()==>>>>参数为空!");
      return null;
    }
    char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
    try
    {
      MessageDigest md5 = MessageDigest.getInstance("MD5");
      md5.update(str.getBytes());
      byte[] tmp = md5.digest();
      
      char[] chStr = new char[32];
      
      int k = 0;
      for (int i = 0; i < 16; i++)
      {
        byte bTmp = tmp[i];
        
        chStr[(k++)] = hexDigits[(bTmp >>> 4 & 0xF)];
        
        chStr[(k++)] = hexDigits[(bTmp & 0xF)];
      }
      return new String(chStr);
    }
    catch (NoSuchAlgorithmException e)
    {
      e.printStackTrace();
    }
    return null;
  }
}
