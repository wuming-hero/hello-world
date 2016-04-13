package com.wuming.util;

public class IPEntry
{
  public String beginIp;
  public String endIp;
  public String country;
  public String area;
  
  public IPEntry()
  {
    this.beginIp = (this.endIp = this.country = this.area = "");
  }
  
  public String toString()
  {
    return 
      this.area + "  " + this.country + "IP范围:" + this.beginIp + "-" + this.endIp;
  }
  
  public String getBeginIp()
  {
    return this.beginIp;
  }
  
  public void setBeginIp(String beginIp)
  {
    this.beginIp = beginIp;
  }
  
  public String getEndIp()
  {
    return this.endIp;
  }
  
  public void setEndIp(String endIp)
  {
    this.endIp = endIp;
  }
  
  public String getCountry()
  {
    return this.country;
  }
  
  public void setCountry(String country)
  {
    this.country = country;
  }
  
  public String getArea()
  {
    return this.area;
  }
  
  public void setArea(String area)
  {
    this.area = area;
  }
}
