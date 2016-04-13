package com.wuming.util;

import org.mozilla.intl.chardet.nsICharsetDetectionObserver;

class nsICharsetDetectionObserverImp
  implements nsICharsetDetectionObserver
{
  String encod = "";
  
  public void Notify(String charset)
  {
    org.mozilla.intl.chardet.HtmlCharsetDetector.found = true;
    this.encod = charset;
  }
  
  public String getEncoding()
  {
    return this.encod;
  }
}
