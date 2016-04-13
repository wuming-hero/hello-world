package com.wuming.util;

import java.io.UnsupportedEncodingException;
import java.util.StringTokenizer;

public class IPUtils
{
  public static byte[] getIpByteArrayFromString(String ip)
  {
    byte[] ret = new byte[4];
    StringTokenizer st = new StringTokenizer(ip, ".");
    try
    {
      ret[0] = ((byte)(Integer.parseInt(st.nextToken()) & 0xFF));
      ret[1] = ((byte)(Integer.parseInt(st.nextToken()) & 0xFF));
      ret[2] = ((byte)(Integer.parseInt(st.nextToken()) & 0xFF));
      ret[3] = ((byte)(Integer.parseInt(st.nextToken()) & 0xFF));
    }
    catch (Exception e)
    {
      System.out.println(e.getMessage());
    }
    return ret;
  }
  
  public static void main(String[] args)
  {
    byte[] a = getIpByteArrayFromString("124.193.138.90");
    for (int i = 0; i < a.length; i++) {
      System.out.println(a[i]);
    }
    System.out.println(getIpStringFromBytes(a));
  }
  
  public static String getString(String s, String srcEncoding, String destEncoding)
  {
    try
    {
      return new String(s.getBytes(srcEncoding), destEncoding);
    }
    catch (UnsupportedEncodingException e) {}
    return s;
  }
  
  public static String getString(byte[] b, String encoding)
  {
    try
    {
      return new String(b, encoding);
    }
    catch (UnsupportedEncodingException e) {}
    return new String(b);
  }
  
  public static String getString(byte[] b, int offset, int len, String encoding)
  {
    try
    {
      return new String(b, offset, len, encoding);
    }
    catch (UnsupportedEncodingException e) {}
    return new String(b, offset, len);
  }
  
  public static String getIpStringFromBytes(byte[] ip)
  {
    StringBuffer sb = new StringBuffer();
    sb.append(ip[0] & 0xFF);
    sb.append('.');
    sb.append(ip[1] & 0xFF);
    sb.append('.');
    sb.append(ip[2] & 0xFF);
    sb.append('.');
    sb.append(ip[3] & 0xFF);
    return sb.toString();
  }
}
