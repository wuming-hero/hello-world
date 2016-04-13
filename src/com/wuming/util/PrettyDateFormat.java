package com.wuming.util;

import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PrettyDateFormat
  extends SimpleDateFormat
{
  private static final long serialVersionUID = 3740476016364838854L;
  private Pattern pattern = Pattern.compile("('*)(#{1,2}|@)");
  private FormatType formatType = FormatType.DEAFULT;
  private SimpleDateFormat simpleDateFormat;
  
  private static enum FormatType
  {
    DEAFULT,  TIME,  DAY;
  }
  
  public PrettyDateFormat(String format, String fullFormat)
  {
    super(fullFormat);
    Matcher m = this.pattern.matcher(format);
    while (m.find()) {
      if (m.group(1).length() % 2 == 0) {
        if ("@".equals(m.group(2)))
        {
          if (this.formatType == FormatType.DAY) {
            throw new IllegalArgumentException("#和@模式字符不能同时使用.");
          }
          this.formatType = FormatType.TIME;
        }
        else
        {
          if (this.formatType == FormatType.TIME) {
            throw new IllegalArgumentException("#和@模式字符不能同时使用.");
          }
          this.formatType = FormatType.DAY;
        }
      }
    }
    this.simpleDateFormat = new SimpleDateFormat(format.replace("'", "''"));
  }
  
  public Object parseObject(String source, ParsePosition pos)
  {
    throw new UnsupportedOperationException("暂时还不支持的操作");
  }
  
  public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition pos)
  {
    if (this.formatType == FormatType.DEAFULT) {
      return super.format(date, toAppendTo, pos);
    }
    long curTime = System.currentTimeMillis();
    
    long diffDay = 0L;
    long diffSecond = 0L;
    if (this.formatType == FormatType.TIME)
    {
      diffSecond = (curTime - date.getTime()) / 1000L;
      if ((diffSecond < 0L) || (diffSecond >= 86400L)) {
        return super.format(date, toAppendTo, pos);
      }
    }
    if (this.formatType == FormatType.DAY)
    {
      Calendar curDate = new GregorianCalendar();
      curDate.setTime(new Date(curTime));
      curDate.set(11, 23);
      curDate.set(12, 59);
      curDate.set(13, 59);
      curDate.set(14, 999);
      diffDay = (curDate.getTimeInMillis() - date.getTime()) / 86400000L;
      if ((diffDay < 0L) || (diffDay > 2L)) {
        return super.format(date, toAppendTo, pos);
      }
    }
    StringBuffer sb = new StringBuffer();
    Matcher m = this.pattern.matcher(this.simpleDateFormat.format(date));
    if (m.find())
    {
      String group2 = m.group(2);
      String replacement = "";
      do
      {
        if ("@".equals(group2))
        {
          if (diffSecond < 60L) {
            replacement = 
              diffSecond + "秒前";
          } else if (diffSecond < 3600L) {
            replacement = diffSecond / 60L + "分钟前";
          } else if (diffSecond < 86400L) {
            replacement = diffSecond / 3600L + "小时前";
          }
        }
        else if (diffDay == 0L) {
          replacement = group2.length() == 2 ? "今天" : "";
        } else if (diffDay == 1L) {
          replacement = "昨天";
        } else {
          replacement = "前天";
        }
        m.appendReplacement(sb, replacement);
      } while (m.find());
      m.appendTail(sb);
    }
    return toAppendTo.append(sb.toString());
  }
  
  public static void format(long curTime, String format, String fullFormat)
  {
    System.out.println("    format: " + format);
    System.out.println("fullFormat: " + fullFormat);
    System.out.println();
    
    Date date2 = new Date(curTime - 30000L);
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    System.out.print(sdf.format(date2) + " 格式化为 : ");
    System.out.println(new PrettyDateFormat(format, fullFormat).format(date2));
    
    date2 = new Date(curTime - 21600000L);
    System.out.print(sdf.format(date2) + " 格式化为 : ");
    System.out.println(new PrettyDateFormat(format, fullFormat).format(date2));
    
    date2 = new Date(curTime - 72000000L);
    System.out.print(sdf.format(date2) + " 格式化为 : ");
    System.out.println(new PrettyDateFormat(format, fullFormat).format(date2));
    
    date2 = new Date(curTime - 194400000L);
    System.out.print(sdf.format(date2) + " 格式化为 : ");
    System.out.println(new PrettyDateFormat(format, fullFormat).format(date2));
    
    date2 = new Date(curTime - 280800000L);
    System.out.print(sdf.format(date2) + " 格式化为 : ");
    System.out.println(new PrettyDateFormat(format, fullFormat).format(date2));
    System.out.println("========================================================");
  }
  
  public static void main(String[] args)
  {
    long curTime = System.currentTimeMillis();
    
    format(curTime, "#a H点", "yy-MM-dd a H点");
    
    format(curTime, "##a H点", "yy-MM-dd a H点");
    
    format(curTime, "# HH:mm:dd", "yy-MM-dd HH:mm:dd");
    
    format(curTime, "# a HH:mm:dd", "yy-MM-dd HH:mm:dd");
    
    format(curTime, "## HH:mm", "yy-MM-dd a HH:mm");
    
    format(curTime, "## a HH:mm", "yy-MM-dd a HH:mm");
    
    format(curTime, "##", "yyyy-MM-dd");
    
    format(curTime, "@", "yyyy-MM-dd HH:mm:ss");
  }
}
