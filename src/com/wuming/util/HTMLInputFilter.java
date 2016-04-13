package com.wuming.util;

import junit.framework.Assert;
import junit.framework.TestCase;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HTMLInputFilter
{
  protected static final boolean ALWAYS_MAKE_TAGS = true;
  protected static final boolean STRIP_COMMENTS = true;
  protected static final int REGEX_FLAGS_SI = 34;
  protected Map<String, List<String>> vAllowed;
  protected Map<String, Integer> vTagCounts;
  protected String[] vSelfClosingTags;
  protected String[] vNeedClosingTags;
  protected String[] vProtocolAtts;
  protected String[] vAllowedProtocols;
  protected String[] vRemoveBlanks;
  protected String[] vAllowedEntities;
  protected boolean vDebug;
  
  public HTMLInputFilter()
  {
    this(false);
  }
  
  public HTMLInputFilter(boolean debug)
  {
    this.vDebug = debug;
    
    this.vAllowed = new HashMap();
    this.vTagCounts = new HashMap();
    
    ArrayList<String> a_atts = new ArrayList();
    a_atts.add("href");
    a_atts.add("target");
    this.vAllowed.put("a", a_atts);
    
    ArrayList<String> img_atts = new ArrayList();
    img_atts.add("src");
    img_atts.add("width");
    img_atts.add("height");
    img_atts.add("alt");
    this.vAllowed.put("img", img_atts);
    
    ArrayList<String> no_atts = new ArrayList();
    this.vAllowed.put("b", no_atts);
    this.vAllowed.put("strong", no_atts);
    this.vAllowed.put("i", no_atts);
    this.vAllowed.put("em", no_atts);
    
    this.vSelfClosingTags = new String[] { "img" };
    this.vNeedClosingTags = new String[] { "a", "b", "strong", "i", "em" };
    this.vAllowedProtocols = new String[] { "http", "mailto" };
    this.vProtocolAtts = new String[] { "src", "href" };
    this.vRemoveBlanks = new String[] { "a", "b", "strong", "i", "em" };
    this.vAllowedEntities = new String[] { "amp", "gt", "lt", "quot" };
  }
  
  protected void reset()
  {
    this.vTagCounts = new HashMap();
  }
  
  protected void debug(String msg)
  {
    if (this.vDebug) {
      System.out.println(msg);
    }
  }
  
  public static String chr(int decimal)
  {
    return String.valueOf((char)decimal);
  }
  
  public static String htmlSpecialChars(String s)
  {
    s = s.replaceAll("&", "&amp;");
    s = s.replaceAll("\"", "&quot;");
    s = s.replaceAll("<", "&lt;");
    s = s.replaceAll(">", "&gt;");
    return s;
  }
  
  public synchronized String filter(String input)
  {
    reset();
    String s = input;
    
    debug("************************************************");
    debug("              INPUT: " + input);
    
    s = escapeComments(s);
    debug("     escapeComments: " + s);
    
    s = balanceHTML(s);
    debug("        balanceHTML: " + s);
    
    s = checkTags(s);
    debug("          checkTags: " + s);
    
    s = processRemoveBlanks(s);
    debug("processRemoveBlanks: " + s);
    
    s = validateEntities(s);
    debug("    validateEntites: " + s);
    
    s = htmlSpecialChars(s);
    
    debug("************************************************\n\n");
    return s;
  }
  
  protected String escapeComments(String s)
  {
    Pattern p = Pattern.compile("<!--(.*?)-->", 32);
    Matcher m = p.matcher(s);
    StringBuffer buf = new StringBuffer();
    if (m.find())
    {
      String match = m.group(1);
      m.appendReplacement(buf, "<!--" + htmlSpecialChars(match) + "-->");
    }
    m.appendTail(buf);
    
    return buf.toString();
  }
  
  protected String balanceHTML(String s)
  {
    s = regexReplace("^>", "", s);
    s = regexReplace("<([^>]*?)(?=<|$)", "<$1>", s);
    s = regexReplace("(^|>)([^<]*?)(?=>)", "$1<$2", s);
    

















    return s;
  }
  
  protected String checkTags(String s)
  {
    Pattern p = Pattern.compile("<(.*?)>", 32);
    Matcher m = p.matcher(s);
    
    StringBuffer buf = new StringBuffer();
    while (m.find())
    {
      String replaceStr = m.group(1);
      replaceStr = processTag(replaceStr);
      m.appendReplacement(buf, replaceStr);
    }
    m.appendTail(buf);
    
    s = buf.toString();
    String key;
    int ii;
    for (Iterator<String> itr = vTagCounts.keySet().iterator(); itr.hasNext();)
    {
      key = itr.next();
      s = s + "</" + key + ">";
    }
    return s;
  }
  
  protected String processRemoveBlanks(String s)
  {
    for (String tag : this.vRemoveBlanks)
    {
      s = regexReplace("<" + tag + "(\\s[^>]*)?></" + tag + ">", "", s);
      s = regexReplace("<" + tag + "(\\s[^>]*)?/>", "", s);
    }
    return s;
  }
  
  protected String regexReplace(String regex_pattern, String replacement, String s)
  {
    Pattern p = Pattern.compile(regex_pattern);
    Matcher m = p.matcher(s);
    return m.replaceAll(replacement);
  }
  
  protected String processTag(String s)
  {
    Pattern p = Pattern.compile("^/([a-z0-9]+)", 34);
    Matcher m = p.matcher(s);
    if (m.find())
    {
      String name = m.group(1).toLowerCase();
      if ((this.vAllowed.containsKey(name)) && 
        (!inArray(name, this.vSelfClosingTags)) && 
        (this.vTagCounts.containsKey(name)))
      {
        this.vTagCounts.put(name, Integer.valueOf(((Integer)this.vTagCounts.get(name)).intValue() - 1));
        return "</" + name + ">";
      }
    }
    p = Pattern.compile("^([a-z0-9]+)(.*?)(/?)$", 34);
    m = p.matcher(s);
    if (m.find())
    {
      String name = m.group(1).toLowerCase();
      String body = m.group(2);
      String ending = m.group(3);
      if (this.vAllowed.containsKey(name))
      {
        String params = "";
        
        Pattern p2 = Pattern.compile("([a-z0-9]+)=([\"'])(.*?)\\2", 34);
        Pattern p3 = Pattern.compile("([a-z0-9]+)(=)([^\"\\s']+)", 34);
        Matcher m2 = p2.matcher(body);
        Matcher m3 = p3.matcher(body);
        List<String> paramNames = new ArrayList();
        List<String> paramValues = new ArrayList();
        while (m2.find())
        {
          paramNames.add(m2.group(1));
          paramValues.add(m2.group(3));
        }
        while (m3.find())
        {
          paramNames.add(m3.group(1));
          paramValues.add(m3.group(3));
        }
        for (int ii = 0; ii < paramNames.size(); ii++)
        {
          String paramName = ((String)paramNames.get(ii)).toLowerCase();
          String paramValue = (String)paramValues.get(ii);
          if (((List)this.vAllowed.get(name)).contains(paramName))
          {
            if (inArray(paramName, this.vProtocolAtts)) {
              paramValue = processParamProtocol(paramValue);
            }
            params = params + " " + paramName + "=\"" + paramValue + "\"";
          }
        }
        if (inArray(name, this.vSelfClosingTags)) {
          ending = " /";
        }
        if (inArray(name, this.vNeedClosingTags)) {
          ending = "";
        }
        if ((ending == null) || (ending.length() < 1))
        {
          if (this.vTagCounts.containsKey(name)) {
            this.vTagCounts.put(name, Integer.valueOf(((Integer)this.vTagCounts.get(name)).intValue() + 1));
          } else {
            this.vTagCounts.put(name, Integer.valueOf(1));
          }
        }
        else {
          ending = " /";
        }
        return "<" + name + params + ending + ">";
      }
      return "";
    }
    p = Pattern.compile("^!--(.*)--$", 34);
    m = p.matcher(s);
    if (m.find())
    {
      String comment = m.group();
      
      return "";
    }
    return "";
  }
  
  protected String processParamProtocol(String s)
  {
    s = decodeEntities(s);
    Pattern p = Pattern.compile("^([^:]+):", 34);
    Matcher m = p.matcher(s);
    if (m.find())
    {
      String protocol = m.group(1);
      if (!inArray(protocol, this.vAllowedProtocols))
      {
        s = "#" + s.substring(protocol.length() + 1, s.length());
        if (s.startsWith("#//")) {
          s = "#" + s.substring(3, s.length());
        }
      }
    }
    return s;
  }
  
  protected String decodeEntities(String s)
  {
    StringBuffer buf = new StringBuffer();
    
    Pattern p = Pattern.compile("&#(\\d+);?");
    Matcher m = p.matcher(s);
    while (m.find())
    {
      String match = m.group(1);
      int decimal = Integer.decode(match).intValue();
      m.appendReplacement(buf, chr(decimal));
    }
    m.appendTail(buf);
    s = buf.toString();
    
    buf = new StringBuffer();
    p = Pattern.compile("&#x([0-9a-f]+);?");
    m = p.matcher(s);
    while (m.find())
    {
      String match = m.group(1);
      int decimal = Integer.decode(match).intValue();
      m.appendReplacement(buf, chr(decimal));
    }
    m.appendTail(buf);
    s = buf.toString();
    
    buf = new StringBuffer();
    p = Pattern.compile("%([0-9a-f]{2});?");
    m = p.matcher(s);
    while (m.find())
    {
      String match = m.group(1);
      int decimal = Integer.decode(match).intValue();
      m.appendReplacement(buf, chr(decimal));
    }
    m.appendTail(buf);
    s = buf.toString();
    
    s = validateEntities(s);
    return s;
  }
  
  protected String validateEntities(String s)
  {
    Pattern p = Pattern.compile("&([^&;]*)(?=(;|&|$))");
    Matcher m = p.matcher(s);
    if (m.find())
    {
      String one = m.group(1);
      String two = m.group(2);
      s = checkEntity(one, two);
    }
    p = Pattern.compile("(>|^)([^<]+?)(<|$)", 32);
    m = p.matcher(s);
    StringBuffer buf = new StringBuffer();
    if (m.find())
    {
      String one = m.group(1);
      String two = m.group(2);
      String three = m.group(3);
      m.appendReplacement(buf, one + two.replaceAll("\"", "&quot;") + three);
    }
    m.appendTail(buf);
    
    return s;
  }
  
  protected String checkEntity(String preamble, String term)
  {
    if (!term.equals(";")) {
      return "&amp;" + preamble;
    }
    if (isValidEntity(preamble)) {
      return "&" + preamble;
    }
    return "&amp;" + preamble;
  }
  
  protected boolean isValidEntity(String entity)
  {
    return inArray(entity, this.vAllowedEntities);
  }
  
  private boolean inArray(String s, String[] array)
  {
    for (String item : array) {
      if ((item != null) && (item.equals(s))) {
        return true;
      }
    }
    return false;
  }
  
  public static class Test
    extends TestCase
  {
    protected HTMLInputFilter vFilter;
    
    protected void setUp()
    {
      this.vFilter = new HTMLInputFilter(true);
    }
    
    protected void tearDown()
    {
      this.vFilter = null;
    }
    
    private void t(String input, String result)
    {
      Assert.assertEquals(result, this.vFilter.filter(input));
    }
    
    public void test_basics()
    {
      t("", "");
      t("hello", "hello");
    }
    
    public void test_balancing_tags()
    {
      t("<b>hello", "<b>hello</b>");
      t("<b>hello", "<b>hello</b>");
      t("hello<b>", "hello");
      t("hello</b>", "hello");
      t("hello<b/>", "hello");
      t("<b><b><b>hello", "<b><b><b>hello</b></b></b>");
      t("</b><b>", "");
    }
    
    public void test_end_slashes()
    {
      t("<img>", "<img />");
      t("<img/>", "<img />");
      t("<b/></b>", "");
    }
    
    public void test_balancing_angle_brackets()
    {
      t("<img src=\"foo\"", "<img src=\"foo\" />");
      t("i>", "");
      t("<img src=\"foo\"/", "<img src=\"foo\" />");
      t(">", "");
      t("foo<b", "foo");
      t("b>foo", "<b>foo</b>");
      t("><b", "");
      t("b><", "");
      t("><b>", "");
    }
    
    public void test_attributes()
    {
      t("<img src=foo>", "<img src=\"foo\" />");
      t("<img asrc=foo>", "<img />");
      t("<img src=test test>", "<img src=\"test\" />");
    }
    
    public void test_disallow_script_tags()
    {
      t("<script>", "");
      t("<script", "");
      t("<script/>", "");
      t("</script>", "");
      t("<script woo=yay>", "");
      t("<script woo=\"yay\">", "");
      t("<script woo=\"yay>", "");
      t("<script woo=\"yay<b>", "");
      t("<script<script>>", "");
      t("<<script>script<script>>", "script");
      t("<<script><script>>", "");
      t("<<script>script>>", "");
      t("<<script<script>>", "");
    }
    
    public void test_protocols()
    {
      t("<a href=\"http://foo\">bar</a>", "<a href=\"http://foo\">bar</a>");
      
      t("<a href=\"mailto:foo\">bar</a>", "<a href=\"mailto:foo\">bar</a>");
      t("<a href=\"javascript:foo\">bar</a>", "<a href=\"#foo\">bar</a>");
      t("<a href=\"java script:foo\">bar</a>", "<a href=\"#foo\">bar</a>");
      t("<a href=\"java\tscript:foo\">bar</a>", "<a href=\"#foo\">bar</a>");
      t("<a href=\"java\nscript:foo\">bar</a>", "<a href=\"#foo\">bar</a>");
      t("<a href=\"java" + HTMLInputFilter.chr(1) + "script:foo\">bar</a>", "<a href=\"#foo\">bar</a>");
      t("<a href=\"jscript:foo\">bar</a>", "<a href=\"#foo\">bar</a>");
      t("<a href=\"vbscript:foo\">bar</a>", "<a href=\"#foo\">bar</a>");
      t("<a href=\"view-source:foo\">bar</a>", "<a href=\"#foo\">bar</a>");
    }
    
    public void test_self_closing_tags()
    {
      t("<img src=\"a\">", "<img src=\"a\" />");
      t("<img src=\"a\">foo</img>", "<img src=\"a\" />foo");
      t("</img>", "");
    }
    
    public void test_comments()
    {
      t("<!-- a<b --->", "");
    }
  }
  
  public static void main(String[] args)
  {
    System.err.println(new HTMLInputFilter().filter("<img src='http://www.1ypg.com/Images/new-logo.png' />"));
  }
}
