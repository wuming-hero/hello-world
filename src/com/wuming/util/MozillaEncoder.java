package com.wuming.util;

import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;
import org.mozilla.intl.chardet.nsDetector;

import java.io.UnsupportedEncodingException;

public class MozillaEncoder
{
  private static volatile MozillaEncoder singleteon;
  
  public static MozillaEncoder getinstance()
  {
    if (singleteon == null) {
      synchronized (MozillaEncoder.class)
      {
        singleteon = new MozillaEncoder();
      }
    }
    return singleteon;
  }
  
  public static String getEncoding(byte[] content)
  {
    nsDetector det = new nsDetector(0);
    nsICharsetDetectionObserverImp nsIC = new nsICharsetDetectionObserverImp();
    det.Init(nsIC);
    det.DoIt(content, content.length, false);
    det.DataEnd();
    String encode = nsIC.getEncoding();
    if ((encode != null) && (!encode.equals("")) && (!encode.equalsIgnoreCase("utf-8"))) {
      encode = "GB2312";
    }
    return encode;
  }
  
  public static void main(String[] args)
    throws UnsupportedEncodingException
  {
    System.out.println(getEncoding("gongwenhua���Ļ�".getBytes("UTF-8")));
  }
  
  public static String getCharset(byte[] content)
  {
    CharsetDetector detector = new CharsetDetector();
    try
    {
      detector.setText(content);
    }
    catch (Exception e1)
    {
      e1.printStackTrace();
    }
    detector.enableInputFilter(true);
    CharsetMatch match = detector.detect();
    String charset = match.getName();
    return charset;
  }
}
