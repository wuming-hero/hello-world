package com.wuming.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class IPSeeker
{
  private class IPLocation
  {
    public String country = this.area = "";
    public String area;
    
    public IPLocation() {}
    
    public IPLocation getCopy()
    {
      IPLocation ret = new IPLocation();
      ret.country = this.country;
      ret.area = this.area;
      return ret;
    }
  }
  
  private static final String IP_FILE = IPSeeker.class.getResource("/qqwry.dat").toString().substring(5);
  private static final int IP_RECORD_LENGTH = 7;
  private static final byte AREA_FOLLOWED = 1;
  private static final byte NO_AREA = 2;
  private Hashtable ipCache;
  private RandomAccessFile ipFile;
  private MappedByteBuffer mbb;
  private static IPSeeker instance = new IPSeeker();
  private long ipBegin;
  private long ipEnd;
  private IPLocation loc;
  private byte[] buf;
  private byte[] b4;
  private byte[] b3;
  
  public IPSeeker()
  {
    this.ipCache = new Hashtable();
    this.loc = new IPLocation();
    this.buf = new byte[100];
    this.b4 = new byte[4];
    this.b3 = new byte[3];
    try
    {
      this.ipFile = new RandomAccessFile(IP_FILE, "r");
    }
    catch (FileNotFoundException e)
    {
      System.out.println(IPSeeker.class.getResource("/qqwry.dat").toString());
      System.out.println(IP_FILE);
      System.out.println("IP地址信息文件没有找到，IP显示功能将无法使用");
      this.ipFile = null;
    }
    if (this.ipFile != null) {
      try
      {
        this.ipBegin = readLong4(0L);
        this.ipEnd = readLong4(4L);
        if ((this.ipBegin == -1L) || (this.ipEnd == -1L))
        {
          this.ipFile.close();
          this.ipFile = null;
        }
      }
      catch (IOException e)
      {
        System.out.println("IP地址信息文件格式有错误，IP显示功能将无法使用");
        this.ipFile = null;
      }
    }
  }
  
  public static IPSeeker getInstance()
  {
    return instance;
  }
  
  public List getIPEntriesDebug(String s)
  {
    List ret = new ArrayList();
    long endOffset = this.ipEnd + 4L;
    for (long offset = this.ipBegin + 4L; offset <= endOffset; offset += 7L)
    {
      long temp = readLong3(offset);
      if (temp != -1L)
      {
        IPLocation loc = getIPLocation(temp);
        if ((loc.country.indexOf(s) != -1) || (loc.area.indexOf(s) != -1))
        {
          IPEntry entry = new IPEntry();
          entry.country = loc.country;
          entry.area = loc.area;
          
          readIP(offset - 4L, this.b4);
          entry.beginIp = IPUtils.getIpStringFromBytes(this.b4);
          
          readIP(temp, this.b4);
          entry.endIp = IPUtils.getIpStringFromBytes(this.b4);
          
          ret.add(entry);
        }
      }
    }
    return ret;
  }
  
  public List getIPEntries(String s)
  {
    List ret = new ArrayList();
    try
    {
      if (this.mbb == null)
      {
        FileChannel fc = this.ipFile.getChannel();
        this.mbb = fc.map(MapMode.READ_ONLY, 0L, this.ipFile.length());
        this.mbb.order(ByteOrder.LITTLE_ENDIAN);
      }
      int endOffset = (int)this.ipEnd;
      for (int offset = (int)this.ipBegin + 4; offset <= endOffset; offset += 7)
      {
        int temp = readInt3(offset);
        if (temp != -1)
        {
          IPLocation loc = getIPLocation(temp);
          if ((loc.country.indexOf(s) != -1) || 
            (loc.area.indexOf(s) != -1))
          {
            IPEntry entry = new IPEntry();
            entry.country = loc.country;
            entry.area = loc.area;
            
            readIP(offset - 4, this.b4);
            entry.beginIp = IPUtils.getIpStringFromBytes(this.b4);
            
            readIP(temp, this.b4);
            entry.endIp = IPUtils.getIpStringFromBytes(this.b4);
            
            ret.add(entry);
          }
        }
      }
    }
    catch (IOException e)
    {
      System.out.println(e.getMessage());
    }
    return ret;
  }
  
  private int readInt3(int offset)
  {
    this.mbb.position(offset);
    return this.mbb.getInt() & 0xFFFFFF;
  }
  
  private int readInt3()
  {
    return this.mbb.getInt() & 0xFFFFFF;
  }
  
  public String getCountry(byte[] ip)
  {
    if (this.ipFile == null) {
      return "错误的IP数据库文件";
    }
    String ipStr = IPUtils.getIpStringFromBytes(ip);
    if (this.ipCache.containsKey(ipStr))
    {
      IPLocation loc = (IPLocation)this.ipCache.get(ipStr);
      return loc.country;
    }
    IPLocation loc = getIPLocation(ip);
    this.ipCache.put(ipStr, loc.getCopy());
    return loc.country;
  }
  
  public String getCountry(String ip)
  {
    return getCountry(IPUtils.getIpByteArrayFromString(ip));
  }
  
  public String getArea(byte[] ip)
  {
    if (this.ipFile == null) {
      return "错误的IP数据库文件";
    }
    String ipStr = IPUtils.getIpStringFromBytes(ip);
    if (this.ipCache.containsKey(ipStr))
    {
      IPLocation loc = (IPLocation)this.ipCache.get(ipStr);
      return loc.area;
    }
    IPLocation loc = getIPLocation(ip);
    this.ipCache.put(ipStr, loc.getCopy());
    return loc.area;
  }
  
  public String getArea(String ip)
  {
    return getArea(IPUtils.getIpByteArrayFromString(ip));
  }
  
  private IPLocation getIPLocation(byte[] ip)
  {
    IPLocation info = null;
    long offset = locateIP(ip);
    if (offset != -1L) {
      info = getIPLocation(offset);
    }
    if (info == null)
    {
      info = new IPLocation();
      info.country = "未知国家";
      info.area = "未知地区";
    }
    return info;
  }
  
  private long readLong4(long offset)
  {
    long ret = 0L;
    try
    {
      this.ipFile.seek(offset);
      ret |= this.ipFile.readByte() & 0xFF;
      ret |= this.ipFile.readByte() << 8 & 0xFF00;
      ret |= this.ipFile.readByte() << 16 & 0xFF0000;
      return ret | this.ipFile.readByte() << 24 & 0xFF000000;
    }
    catch (IOException e) {}
    return -1L;
  }
  
  private long readLong3(long offset)
  {
    long ret = 0L;
    try
    {
      this.ipFile.seek(offset);
      this.ipFile.readFully(this.b3);
      ret |= this.b3[0] & 0xFF;
      ret |= this.b3[1] << 8 & 0xFF00;
      return ret | this.b3[2] << 16 & 0xFF0000;
    }
    catch (IOException e) {}
    return -1L;
  }
  
  private long readLong3()
  {
    long ret = 0L;
    try
    {
      this.ipFile.readFully(this.b3);
      ret |= this.b3[0] & 0xFF;
      ret |= this.b3[1] << 8 & 0xFF00;
      return ret | this.b3[2] << 16 & 0xFF0000;
    }
    catch (IOException e) {}
    return -1L;
  }
  
  private void readIP(long offset, byte[] ip)
  {
    try
    {
      this.ipFile.seek(offset);
      this.ipFile.readFully(ip);
      byte temp = ip[0];
      ip[0] = ip[3];
      ip[3] = temp;
      temp = ip[1];
      ip[1] = ip[2];
      ip[2] = temp;
    }
    catch (IOException e)
    {
      System.out.println(e.getMessage());
    }
  }
  
  private void readIP(int offset, byte[] ip)
  {
    this.mbb.position(offset);
    this.mbb.get(ip);
    byte temp = ip[0];
    ip[0] = ip[3];
    ip[3] = temp;
    temp = ip[1];
    ip[1] = ip[2];
    ip[2] = temp;
  }
  
  private int compareIP(byte[] ip, byte[] beginIp)
  {
    for (int i = 0; i < 4; i++)
    {
      int r = compareByte(ip[i], beginIp[i]);
      if (r != 0) {
        return r;
      }
    }
    return 0;
  }
  
  private int compareByte(byte b1, byte b2)
  {
    if ((b1 & 0xFF) > (b2 & 0xFF)) {
      return 1;
    }
    if ((b1 ^ b2) == 0) {
      return 0;
    }
    return -1;
  }
  
  private long locateIP(byte[] ip)
  {
    long m = 0L;
    

    readIP(this.ipBegin, this.b4);
    int r = compareIP(ip, this.b4);
    if (r == 0) {
      return this.ipBegin;
    }
    if (r < 0) {
      return -1L;
    }
    long i = this.ipBegin;
    for (long j = this.ipEnd; i < j;)
    {
      m = getMiddleOffset(i, j);
      readIP(m, this.b4);
      r = compareIP(ip, this.b4);
      if (r > 0) {
        i = m;
      } else if (r < 0)
      {
        if (m == j)
        {
          j -= 7L;
          m = j;
        }
        else
        {
          j = m;
        }
      }
      else {
        return readLong3(m + 4L);
      }
    }
    m = readLong3(m + 4L);
    readIP(m, this.b4);
    r = compareIP(ip, this.b4);
    if (r <= 0) {
      return m;
    }
    return -1L;
  }
  
  private long getMiddleOffset(long begin, long end)
  {
    long records = (end - begin) / 7L;
    records >>= 1;
    if (records == 0L) {
      records = 1L;
    }
    return begin + records * 7L;
  }
  
  private IPLocation getIPLocation(long offset)
  {
    try
    {
      this.ipFile.seek(offset + 4L);
      
      byte b = this.ipFile.readByte();
      if (b == 1)
      {
        long countryOffset = readLong3();
        
        this.ipFile.seek(countryOffset);
        
        b = this.ipFile.readByte();
        if (b == 2)
        {
          this.loc.country = readString(readLong3());
          this.ipFile.seek(countryOffset + 4L);
        }
        else
        {
          this.loc.country = readString(countryOffset);
        }
        this.loc.area = readArea(this.ipFile.getFilePointer());
      }
      else if (b == 2)
      {
        this.loc.country = readString(readLong3());
        this.loc.area = readArea(offset + 8L);
      }
      else
      {
        this.loc.country = readString(this.ipFile.getFilePointer() - 1L);
        this.loc.area = readArea(this.ipFile.getFilePointer());
      }
      return this.loc;
    }
    catch (IOException e) {}
    return null;
  }
  
  private IPLocation getIPLocation(int offset)
  {
    this.mbb.position(offset + 4);
    
    byte b = this.mbb.get();
    if (b == 1)
    {
      int countryOffset = readInt3();
      
      this.mbb.position(countryOffset);
      
      b = this.mbb.get();
      if (b == 2)
      {
        this.loc.country = readString(readInt3());
        this.mbb.position(countryOffset + 4);
      }
      else
      {
        this.loc.country = readString(countryOffset);
      }
      this.loc.area = readArea(this.mbb.position());
    }
    else if (b == 2)
    {
      this.loc.country = readString(readInt3());
      this.loc.area = readArea(offset + 8);
    }
    else
    {
      this.loc.country = readString(this.mbb.position() - 1);
      this.loc.area = readArea(this.mbb.position());
    }
    return this.loc;
  }
  
  private String readArea(long offset)
    throws IOException
  {
    this.ipFile.seek(offset);
    byte b = this.ipFile.readByte();
    if ((b == 1) || (b == 2))
    {
      long areaOffset = readLong3(offset + 1L);
      if (areaOffset == 0L) {
        return "未知地区";
      }
      return readString(areaOffset);
    }
    return readString(offset);
  }
  
  private String readArea(int offset)
  {
    this.mbb.position(offset);
    byte b = this.mbb.get();
    if ((b == 1) || (b == 2))
    {
      int areaOffset = readInt3();
      if (areaOffset == 0) {
        return "未知地区";
      }
      return readString(areaOffset);
    }
    return readString(offset);
  }
  
  private String readString(long offset)
  {
    try
    {
      this.ipFile.seek(offset);
      
      int i = 0;
      for (this.buf[i] = this.ipFile.readByte(); this.buf[i] != 0;) {
        this.buf[(++i)] = this.ipFile.readByte();
      }
      if (i != 0) {
        return IPUtils.getString(this.buf, 0, i, "GBK");
      }
    }
    catch (IOException e)
    {
      System.out.println(e.getMessage());
    }
    return "";
  }
  
  private String readString(int offset)
  {
    try
    {
      this.mbb.position(offset);
      
      int i = 0;
      for (this.buf[i] = this.mbb.get(); this.buf[i] != 0; this.buf[(++i)] = this.mbb.get()) {}
      if (i != 0) {
        return IPUtils.getString(this.buf, 0, i, "GBK");
      }
    }
    catch (IllegalArgumentException e)
    {
      System.out.println(e.getMessage());
    }
    return "";
  }
  
  public String getAddress(String ip)
  {
    String country = getCountry(ip).equals(" CZ88.NET") ? "" : getCountry(ip);
    String area = getArea(ip).equals(" CZ88.NET") ? "" : getArea(ip);
    String address = country;
    return address.trim();
  }
  
  public static void main(String[] args)
  {
    IPSeeker seeker = new IPSeeker();
    System.err.println(seeker.getAddress("110.152.104.103"));
  }
}
