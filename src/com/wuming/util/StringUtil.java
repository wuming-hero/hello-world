package com.wuming.util;

import org.apache.commons.lang.*;

import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
    public static final String[] EARR_STRING = new String[0];
    public static final String[] ENCA_UTF8 = new String[]{"encoding:UTF-8"};

    public static final String EMPTY = "";
    public static final int INDEX_NOT_FOUND = -1;
    private static final int PAD_LIMIT = 8192;
    private static final String DEFAULT = "GB2312";
    private static final int CHUNK_SIZE = 2000;
    private static Pattern metaPattern = Pattern.compile("<meta\\s+([^>]*http-equiv=\"?content-type\"?[^>]*)>", 2);
    private static Pattern charsetPattern = Pattern.compile("charset=\\s*([a-z][_\\-0-9a-z]*)", 2);

    public static boolean isNum(String str) {
        if ((str == null) || (str.equals(""))) {
            return false;
        }
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    public static boolean isIPAddr(String addr) {
        if (isEmpty(addr)) {
            return false;
        }
        String[] ips = split(addr, '.');
        if (ips.length != 4) {
            return false;
        }
        try {
            int ipa = Integer.parseInt(ips[0]);
            int ipb = Integer.parseInt(ips[1]);
            int ipc = Integer.parseInt(ips[2]);
            int ipd = Integer.parseInt(ips[3]);

            return (ipa >= 0) && (ipa <= 255) && (ipb >= 0) && (ipb <= 255) && (ipc >= 0) && (ipc <= 255) && (ipd >= 0) && (ipd <= 255);
        } catch (Exception localException) {
        }
        return false;
    }

    public static List arrayToList(String[] str) {
        List list = Arrays.asList(str);
        return list;
    }

    public static String[] listToArray(List list) {
        int size = list.size();
        String[] str = (String[]) list.toArray(new String[size]);
        return str;
    }

    public static String nullToStr(String s) {
        return nullToStr(s, "");
    }

    public static String nullToStr(String s, String def) {
        if ((s == null) || (s.length() < 1)) {
            return def;
        }
        return s;
    }

    public static String getDiamond(String url) {
        try {
            URL neturl = new URL(url);
            return neturl.getHost().replace("www.", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getMainDiamond(String url) {
        return getMainDiamond(url, "");
    }

    public static String getMainDiamond(String url, String def) {
        try {
            Pattern p = Pattern.compile("(?<= http://|\\.)[^.]*?(\\.(com|cn|net|org|biz|gov|hk|info|cc|tv|it|dk|ie|co|uk|se|fr|in))+", 2);
            Matcher matcher = p.matcher(url);
            matcher.find();
            return matcher.group();
        } catch (Exception e) {
        }
        return def;
    }

    public static void main(String[] arg)
            throws UnsupportedEncodingException {
        System.err.println(getUTF8URLEncoder("拍"));
    }

    public static String getCharSetStr(String str, String oldCharSet, String newCharSet) {
        if ((str == "") || (str == null)) {
            return "";
        }
        try {
            str = new String(str.getBytes(oldCharSet), newCharSet);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String getEncoding(String src, String srcCharset, String objCharset) {
        try {
            if (src == null) {
                return "";
            }
            if (srcCharset == null) {
                srcCharset = "ISO-8859-1";
            }
            if (objCharset == null) {
                objCharset = "UTF-8";
            }
            return new String(src.getBytes(srcCharset), objCharset);
        } catch (Exception e) {
        }
        return "";
    }

    public static String getUTF8URLDecoder(String src)
            throws UnsupportedEncodingException {
        return URLDecoder.decode(src, "utf-8");
    }

    public static String getUTF8URLEncoder(String src)
            throws UnsupportedEncodingException {
        return URLEncoder.encode(src, "utf-8");
    }

    public static String getUtf8Encoding(String scr) {
        return getEncoding(scr, null, null);
    }

    public static String nullToEmpty(Object obj) {
        if (obj == null) {
            return "";
        }
        return obj.toString();
    }

    public static long getTablename(int divisor, long dividend) {
        long i = dividend % divisor;
        return i;
    }

    public static String equalsIgnoreCase(String str, String objStr, String equalsStr, String noEqualsStr) {
        if (noEqualsStr == null) {
            noEqualsStr = "";
        }
        if (equalsStr == null) {
            equalsStr = "";
        }
        if (str == null) {
            str = "";
        }
        if (objStr == null) {
            objStr = "";
        }
        if (str.equalsIgnoreCase(objStr)) {
            return equalsStr;
        }
        return noEqualsStr;
    }

    public static String getString(String[] objs) {
        if ((objs == null) || (objs.length < 1)) {
            return "";
        }
        String ids = objs[0];
        for (int i = 1; i < objs.length; i++) {
            ids = ids + "," + objs[i];
        }
        return ids;
    }

    public static String getString(String orig, String[] objs) {
        if (objs.length < 1) {
            return orig;
        }
        for (int i = 0; i < objs.length; i++) {
            if (orig.indexOf(objs[i]) == -1) {
                orig = orig + objs[i] + ",";
            }
        }
        return orig.substring(0, orig.length() - 1);
    }

    public static String encodingHtml(InputStream inputStream, String charSet)
            throws Exception {
        BufferedReader br = null;
        StringBuffer sb = new StringBuffer();
        String result = null;
        try {
            br = new BufferedReader(new InputStreamReader(inputStream, charSet));
            while ((result = br.readLine()) != null) {
                sb.append(result);
                if (sb.length() > 1000000) {
                    break;
                }
            }
            if (sb.charAt(0) - 'ï' == 0) {
                sb.delete(0, 3);
            }
            result = null;

            return new String(sb.toString().getBytes(charSet), sniffCharacterEncoding(sb.toString().getBytes(charSet)));
        } catch (Exception e) {
            throw e;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException localIOException2) {
                }
            }
            if (br != null) {
                try {
                    br.close();
                } catch (IOException localIOException3) {
                }
            }
        }
    }

    public static String[] getHtml(InputStream inputStream, String charSet)
            throws Exception {
        BufferedReader br = null;
        StringBuffer sb = new StringBuffer();
        String[] result = new String[2];
        try {
            br = new BufferedReader(new InputStreamReader(inputStream, charSet));
            while ((result[0] = br.readLine()) != null) {
                sb.append(result[0]);
                if (sb.length() > 1000000) {
                    break;
                }
            }
            if (sb.charAt(0) - 'ï' == 0) {
                sb.delete(0, 3);
            }
            result[1] = sniffCharacterEncoding(sb.toString().getBytes(charSet));
            result[0] = new String(sb.toString().getBytes(charSet), result[1]);
            return result;
        } catch (Exception e) {
            throw e;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException localIOException2) {
                }
            }
            if (br != null) {
                try {
                    br.close();
                } catch (IOException localIOException3) {
                }
            }
        }
    }

    private static String sniffCharacterEncoding(byte[] content) {
        int length = content.length < 2000 ? content.length : 2000;
        String str = "", encoding;
        try {
            encoding = MozillaEncoder.getEncoding(content);
            if ((encoding == null) || (encoding.equals(""))) {
                str = new String(content, 0, length, Charset.forName("ASCII").toString());
                Matcher metaMatcher = metaPattern.matcher(str);
                if (metaMatcher.find()) {
                    Matcher charsetMatcher = charsetPattern.matcher(metaMatcher.group(1));
                    if (charsetMatcher.find()) {
                        encoding = new String(charsetMatcher.group(1));
                    }
                }
            }
            if ((encoding == null) || (encoding.equals(""))) {
                encoding = MozillaEncoder.getCharset(content);
            }
            if ((encoding == null) || (encoding.equals(""))) {
                encoding = "GB2312";
            }
        } catch (Exception e) {
            return "GB2312";
        }
        return encoding;
    }

    public static boolean isEmpty(String str) {
        return (str == null) || (str.length() == 0);
    }

    public static boolean isNotEmpty(String str) {
        return !StringUtils.isEmpty(str);
    }

    public static boolean isBlank(String str) {
        int strLen;
        if ((str == null) || ((strLen = str.length()) == 0)) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNotBlank(String str) {
        return !StringUtils.isBlank(str);
    }

    /**
     * @deprecated
     */
    public static String clean(String str) {
        return str == null ? "" : str.trim();
    }

    public static String trim(String str) {
        return str == null ? null : str.trim();
    }

    public static String trimToNull(String str) {
        String ts = trim(str);
        return isEmpty(ts) ? null : ts;
    }

    public static String trimToEmpty(String str) {
        return str == null ? "" : str.trim();
    }

    public static String strip(String str) {
        return strip(str, null);
    }

    public static String stripToNull(String str) {
        if (str == null) {
            return null;
        }
        str = strip(str, null);
        return str.length() == 0 ? null : str;
    }

    public static String stripToEmpty(String str) {
        return str == null ? "" : strip(str, null);
    }

    public static String strip(String str, String stripChars) {
        if (isEmpty(str)) {
            return str;
        }
        str = stripStart(str, stripChars);
        return stripEnd(str, stripChars);
    }

    public static String stripStart(String str, String stripChars) {
        int strLen;
        if ((str == null) || ((strLen = str.length()) == 0)) {
            return str;
        }
        int start = 0;
        if (stripChars == null) {
            do {
                start++;
                if (start == strLen) {
                    break;
                }
            } while (Character.isWhitespace(str.charAt(start)));
        } else {
            if (stripChars.length() == 0) {
                return str;
            }
            while ((start != strLen) && (stripChars.indexOf(str.charAt(start)) != -1)) {
                start++;
            }
        }
        return str.substring(start);
    }

    public static String stripEnd(String str, String stripChars) {
        int end;
        if ((str == null) || ((end = str.length()) == 0)) {
            return str;
        }
        if (stripChars == null) {
            do {
                end--;
                if (end == 0) {
                    break;
                }
            } while (Character.isWhitespace(str.charAt(end - 1)));
        } else {
            if (stripChars.length() == 0) {
                return str;
            }
            while ((end != 0) && (stripChars.indexOf(str.charAt(end - 1)) != -1)) {
                end--;
            }
        }
        return str.substring(0, end);
    }

    public static String[] stripAll(String[] strs) {
        return stripAll(strs, null);
    }

    public static String[] stripAll(String[] strs, String stripChars) {
        int strsLen;
        if ((strs == null) || ((strsLen = strs.length) == 0)) {
            return strs;
        }
        String[] newArr = new String[strsLen];
        for (int i = 0; i < strsLen; i++) {
            newArr[i] = strip(strs[i], stripChars);
        }
        return newArr;
    }

    public static boolean equals(String str1, String str2) {
        return str1 == null ? false : str2 == null ? true : str1.equals(str2);
    }

    public static boolean equalsIgnoreCase(String str1, String str2) {
        return str1 == null ? false : str2 == null ? true : str1.equalsIgnoreCase(str2);
    }

    public static int indexOf(String str, char searchChar) {
        if (isEmpty(str)) {
            return -1;
        }
        return str.indexOf(searchChar);
    }

    public static int indexOf(String str, char searchChar, int startPos) {
        if (isEmpty(str)) {
            return -1;
        }
        return str.indexOf(searchChar, startPos);
    }

    public static int indexOf(String str, String searchStr) {
        if ((str == null) || (searchStr == null)) {
            return -1;
        }
        return str.indexOf(searchStr);
    }

    public static int ordinalIndexOf(String str, String searchStr, int ordinal) {
        if ((str == null) || (searchStr == null) || (ordinal <= 0)) {
            return -1;
        }
        if (searchStr.length() == 0) {
            return 0;
        }
        int found = 0;
        int index = -1;
        do {
            index = str.indexOf(searchStr, index + 1);
            if (index < 0) {
                return index;
            }
            found++;
        } while (found < ordinal);
        return index;
    }

    public static int indexOf(String str, String searchStr, int startPos) {
        if ((str == null) || (searchStr == null)) {
            return -1;
        }
        if ((searchStr.length() == 0) && (startPos >= str.length())) {
            return str.length();
        }
        return str.indexOf(searchStr, startPos);
    }

    public static int lastIndexOf(String str, char searchChar) {
        if (isEmpty(str)) {
            return -1;
        }
        return str.lastIndexOf(searchChar);
    }

    public static int lastIndexOf(String str, char searchChar, int startPos) {
        if (isEmpty(str)) {
            return -1;
        }
        return str.lastIndexOf(searchChar, startPos);
    }

    public static int lastIndexOf(String str, String searchStr) {
        if ((str == null) || (searchStr == null)) {
            return -1;
        }
        return str.lastIndexOf(searchStr);
    }

    public static int lastIndexOf(String str, String searchStr, int startPos) {
        if ((str == null) || (searchStr == null)) {
            return -1;
        }
        return str.lastIndexOf(searchStr, startPos);
    }

    public static boolean contains(String str, char searchChar) {
        if (isEmpty(str)) {
            return false;
        }
        return str.indexOf(searchChar) >= 0;
    }

    public static boolean contains(String str, String searchStr) {
        if ((str == null) || (searchStr == null)) {
            return false;
        }
        return str.indexOf(searchStr) >= 0;
    }

    public static boolean containsIgnoreCase(String str, String searchStr) {
        if ((str == null) || (searchStr == null)) {
            return false;
        }
        return contains(str.toUpperCase(), searchStr.toUpperCase());
    }

    public static int indexOfAny(String str, char[] searchChars) {
        if ((isEmpty(str)) || (ArrayUtils.isEmpty(searchChars))) {
            return -1;
        }
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            for (int j = 0; j < searchChars.length; j++) {
                if (searchChars[j] == ch) {
                    return i;
                }
            }
        }
        return -1;
    }

    public static int indexOfAny(String str, String searchChars) {
        if ((isEmpty(str)) || (isEmpty(searchChars))) {
            return -1;
        }
        return indexOfAny(str, searchChars.toCharArray());
    }

    public static int indexOfAnyBut(String str, char[] searchChars) {
        if ((isEmpty(str)) || (ArrayUtils.isEmpty(searchChars))) {
            return -1;
        }
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            int j = 0;
            while (searchChars[j] != ch) {
                j++;
                if (j >= searchChars.length) {
                    return i;
                }
            }
        }
        return -1;
    }

    public static int indexOfAnyBut(String str, String searchChars) {
        if ((isEmpty(str)) || (isEmpty(searchChars))) {
            return -1;
        }
        for (int i = 0; i < str.length(); i++) {
            if (searchChars.indexOf(str.charAt(i)) < 0) {
                return i;
            }
        }
        return -1;
    }

    public static boolean containsOnly(String str, char[] valid) {
        if ((valid == null) || (str == null)) {
            return false;
        }
        if (str.length() == 0) {
            return true;
        }
        if (valid.length == 0) {
            return false;
        }
        return indexOfAnyBut(str, valid) == -1;
    }

    public static boolean containsOnly(String str, String validChars) {
        if ((str == null) || (validChars == null)) {
            return false;
        }
        return containsOnly(str, validChars.toCharArray());
    }

    public static boolean containsNone(String str, char[] invalidChars) {
        if ((str == null) || (invalidChars == null)) {
            return true;
        }
        int strSize = str.length();
        int validSize = invalidChars.length;
        for (int i = 0; i < strSize; i++) {
            char ch = str.charAt(i);
            for (int j = 0; j < validSize; j++) {
                if (invalidChars[j] == ch) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean containsNone(String str, String invalidChars) {
        if ((str == null) || (invalidChars == null)) {
            return true;
        }
        return containsNone(str, invalidChars.toCharArray());
    }

    public static int indexOfAny(String str, String[] searchStrs) {
        if ((str == null) || (searchStrs == null)) {
            return -1;
        }
        int sz = searchStrs.length;


        int ret = 2147483647;

        int tmp = 0;
        for (int i = 0; i < sz; i++) {
            String search = searchStrs[i];
            if (search != null) {
                tmp = str.indexOf(search);
                if (tmp != -1) {
                    if (tmp < ret) {
                        ret = tmp;
                    }
                }
            }
        }
        return ret == 2147483647 ? -1 : ret;
    }

    public static int lastIndexOfAny(String str, String[] searchStrs) {
        if ((str == null) || (searchStrs == null)) {
            return -1;
        }
        int sz = searchStrs.length;
        int ret = -1;
        int tmp = 0;
        for (int i = 0; i < sz; i++) {
            String search = searchStrs[i];
            if (search != null) {
                tmp = str.lastIndexOf(search);
                if (tmp > ret) {
                    ret = tmp;
                }
            }
        }
        return ret;
    }

    public static String substring(String str, int start) {
        if (str == null) {
            return null;
        }
        if (start < 0) {
            start = str.length() + start;
        }
        if (start < 0) {
            start = 0;
        }
        if (start > str.length()) {
            return "";
        }
        return str.substring(start);
    }

    public static String substring(String str, int start, int end) {
        if (str == null) {
            return null;
        }
        if (end < 0) {
            end = str.length() + end;
        }
        if (start < 0) {
            start = str.length() + start;
        }
        if (end > str.length()) {
            end = str.length();
        }
        if (start > end) {
            return "";
        }
        if (start < 0) {
            start = 0;
        }
        if (end < 0) {
            end = 0;
        }
        return str.substring(start, end);
    }

    public static String left(String str, int len) {
        if (str == null) {
            return null;
        }
        if (len < 0) {
            return "";
        }
        if (str.length() <= len) {
            return str;
        }
        return str.substring(0, len);
    }

    public static String right(String str, int len) {
        if (str == null) {
            return null;
        }
        if (len < 0) {
            return "";
        }
        if (str.length() <= len) {
            return str;
        }
        return str.substring(str.length() - len);
    }

    public static String mid(String str, int pos, int len) {
        if (str == null) {
            return null;
        }
        if ((len < 0) || (pos > str.length())) {
            return "";
        }
        if (pos < 0) {
            pos = 0;
        }
        if (str.length() <= pos + len) {
            return str.substring(pos);
        }
        return str.substring(pos, pos + len);
    }

    public static String substringBefore(String str, String separator) {
        if ((isEmpty(str)) || (separator == null)) {
            return str;
        }
        if (separator.length() == 0) {
            return "";
        }
        int pos = str.indexOf(separator);
        if (pos == -1) {
            return str;
        }
        return str.substring(0, pos);
    }

    public static String substringAfter(String str, String separator) {
        if (isEmpty(str)) {
            return str;
        }
        if (separator == null) {
            return "";
        }
        int pos = str.indexOf(separator);
        if (pos == -1) {
            return "";
        }
        return str.substring(pos + separator.length());
    }

    public static String substringBeforeLast(String str, String separator) {
        if ((isEmpty(str)) || (isEmpty(separator))) {
            return str;
        }
        int pos = str.lastIndexOf(separator);
        if (pos == -1) {
            return str;
        }
        return str.substring(0, pos);
    }

    public static String substringAfterLast(String str, String separator) {
        if (isEmpty(str)) {
            return str;
        }
        if (isEmpty(separator)) {
            return "";
        }
        int pos = str.lastIndexOf(separator);
        if ((pos == -1) || (pos == str.length() - separator.length())) {
            return "";
        }
        return str.substring(pos + separator.length());
    }

    public static String substringBetween(String str, String tag) {
        return substringBetween(str, tag, tag);
    }

    public static String substringBetween(String str, String open, String close) {
        if ((str == null) || (open == null) || (close == null)) {
            return null;
        }
        int start = str.indexOf(open);
        if (start != -1) {
            int end = str.indexOf(close, start + open.length());
            if (end != -1) {
                return str.substring(start + open.length(), end);
            }
        }
        return null;
    }

    public static String[] substringsBetween(String str, String open, String close) {
        if ((str == null) || (isEmpty(open)) || (isEmpty(close))) {
            return null;
        }
        int strLen = str.length();
        if (strLen == 0) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
        int closeLen = close.length();
        int openLen = open.length();
        List list = new ArrayList();
        int pos = 0;
        while (pos < strLen - closeLen) {
            int start = str.indexOf(open, pos);
            if (start < 0) {
                break;
            }
            start += openLen;
            int end = str.indexOf(close, start);
            if (end < 0) {
                break;
            }
            list.add(str.substring(start, end));
            pos = end + closeLen;
        }
        if (list.size() > 0) {
            return (String[]) list.toArray(new String[list.size()]);
        }
        return null;
    }

    /**
     * @deprecated
     */
    public static String getNestedString(String str, String tag) {
        return substringBetween(str, tag, tag);
    }

    /**
     * @deprecated
     */
    public static String getNestedString(String str, String open, String close) {
        return substringBetween(str, open, close);
    }

    public static String[] split(String str) {
        return split(str, null, -1);
    }

    public static String[] split(String str, char separatorChar) {
        return splitWorker(str, separatorChar, false);
    }

    public static String[] split(String str, String separatorChars) {
        return splitWorker(str, separatorChars, -1, false);
    }

    public static String[] split(String str, String separatorChars, int max) {
        return splitWorker(str, separatorChars, max, false);
    }

    public static String[] splitByWholeSeparator(String str, String separator) {
        return splitByWholeSeparator(str, separator, -1);
    }

    public static String[] splitByWholeSeparator(String str, String separator, int max) {
        if (str == null) {
            return null;
        }
        int len = str.length();
        if (len == 0) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
        if ((separator == null) || ("".equals(separator))) {
            return split(str, null, max);
        }
        int separatorLength = separator.length();

        ArrayList substrings = new ArrayList();
        int numberOfSubstrings = 0;
        int beg = 0;
        int end = 0;
        while (end < len) {
            end = str.indexOf(separator, beg);
            if (end > -1) {
                if (end > beg) {
                    numberOfSubstrings++;
                    if (numberOfSubstrings == max) {
                        end = len;
                        substrings.add(str.substring(beg));
                    } else {
                        substrings.add(str.substring(beg, end));


                        beg = end + separatorLength;
                    }
                } else {
                    beg = end + separatorLength;
                }
            } else {
                substrings.add(str.substring(beg));
                end = len;
            }
        }
        return (String[]) substrings.toArray(new String[substrings.size()]);
    }

    public static String[] splitPreserveAllTokens(String str) {
        return splitWorker(str, null, -1, true);
    }

    public static String[] splitPreserveAllTokens(String str, char separatorChar) {
        return splitWorker(str, separatorChar, true);
    }

    private static String[] splitWorker(String str, char separatorChar, boolean preserveAllTokens) {
        if (str == null) {
            return null;
        }
        int len = str.length();
        if (len == 0) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
        List list = new ArrayList();
        int i = 0;
        int start = 0;
        boolean match = false;
        boolean lastMatch = false;
        while (i < len) {
            if (str.charAt(i) == separatorChar) {
                if ((match) || (preserveAllTokens)) {
                    list.add(str.substring(start, i));
                    match = false;
                    lastMatch = true;
                }
                i++;
                start = i;
            } else {
                lastMatch = false;

                match = true;
                i++;
            }
        }
        if ((match) || ((preserveAllTokens) && (lastMatch))) {
            list.add(str.substring(start, i));
        }
        return (String[]) list.toArray(new String[list.size()]);
    }

    public static String[] splitPreserveAllTokens(String str, String separatorChars) {
        return splitWorker(str, separatorChars, -1, true);
    }

    public static String[] splitPreserveAllTokens(String str, String separatorChars, int max) {
        return splitWorker(str, separatorChars, max, true);
    }

    private static String[] splitWorker(String str, String separatorChars, int max, boolean preserveAllTokens) {
        if (str == null) {
            return null;
        }
        int len = str.length();
        if (len == 0) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
        List list = new ArrayList();
        int sizePlus1 = 1;
        int i = 0;
        int start = 0;
        boolean match = false;
        boolean lastMatch = false;
        if (separatorChars == null) {
            while (i < len) {
                if (Character.isWhitespace(str.charAt(i))) {
                    if ((match) || (preserveAllTokens)) {
                        lastMatch = true;
                        if (sizePlus1++ == max) {
                            i = len;
                            lastMatch = false;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                    }
                    i++;
                    start = i;
                } else {
                    lastMatch = false;

                    match = true;
                    i++;
                }
            }
        } else if (separatorChars.length() == 1) {
            final char sep = separatorChars.charAt(0);
            while (i < len) {
                if (str.charAt(i) == sep) {
                    if ((match) || (preserveAllTokens)) {
                        lastMatch = true;
                        if (sizePlus1++ == max) {
                            i = len;
                            lastMatch = false;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                    }
                    i++;
                    start = i;
                } else {
                    lastMatch = false;

                    match = true;
                    i++;
                }
            }
        } else {
            while (i < len) {
                char sep;
                if (separatorChars.indexOf(str.charAt(i)) >= 0) {
                    if ((match) || (preserveAllTokens)) {
                        lastMatch = true;
                        if (sizePlus1++ == max) {
                            i = len;
                            lastMatch = false;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                    }
                    i++;
                    start = i;
                } else {
                    lastMatch = false;

                    match = true;
                    i++;
                }
            }
        }
        if ((match) || ((preserveAllTokens) && (lastMatch))) {
            list.add(str.substring(start, i));
        }
        return (String[]) list.toArray(new String[list.size()]);
    }

    /**
     * @deprecated
     */
    public static String concatenate(Object[] array) {
        return join(array, null);
    }

    public static String join(Object[] array) {
        return join(array, null);
    }

    public static String join(Object[] array, char separator) {
        if (array == null) {
            return null;
        }
        return join(array, separator, 0, array.length);
    }

    public static String join(Object[] array, char separator, int startIndex, int endIndex) {
        if (array == null) {
            return null;
        }
        int bufSize = endIndex - startIndex;
        if (bufSize <= 0) {
            return "";
        }
        bufSize *= ((array[startIndex] == null ? 16 : array[startIndex].toString().length()) + 1);
        StringBuffer buf = new StringBuffer(bufSize);
        for (int i = startIndex; i < endIndex; i++) {
            if (i > startIndex) {
                buf.append(separator);
            }
            if (array[i] != null) {
                buf.append(array[i]);
            }
        }
        return buf.toString();
    }

    public static String join(Object[] array, String separator) {
        if (array == null) {
            return null;
        }
        return join(array, separator, 0, array.length);
    }

    public static String join(Object[] array, String separator, int startIndex, int endIndex) {
        if (array == null) {
            return null;
        }
        if (separator == null) {
            separator = "";
        }
        int bufSize = endIndex - startIndex;
        if (bufSize <= 0) {
            return "";
        }
        bufSize *= ((array[startIndex] == null ? 16 : array[startIndex].toString().length()) + separator.length());

        StringBuffer buf = new StringBuffer(bufSize);
        for (int i = startIndex; i < endIndex; i++) {
            if (i > startIndex) {
                buf.append(separator);
            }
            if (array[i] != null) {
                buf.append(array[i]);
            }
        }
        return buf.toString();
    }

    public static String join(Iterator iterator, char separator) {
        if (iterator == null) {
            return null;
        }
        if (!iterator.hasNext()) {
            return "";
        }
        Object first = iterator.next();
        if (!iterator.hasNext()) {
            return ObjectUtils.toString(first);
        }
        StringBuffer buf = new StringBuffer(256);
        if (first != null) {
            buf.append(first);
        }
        while (iterator.hasNext()) {
            buf.append(separator);
            Object obj = iterator.next();
            if (obj != null) {
                buf.append(obj);
            }
        }
        return buf.toString();
    }

    public static String join(Iterator iterator, String separator) {
        if (iterator == null) {
            return null;
        }
        if (!iterator.hasNext()) {
            return "";
        }
        Object first = iterator.next();
        if (!iterator.hasNext()) {
            return ObjectUtils.toString(first);
        }
        StringBuffer buf = new StringBuffer(256);
        if (first != null) {
            buf.append(first);
        }
        while (iterator.hasNext()) {
            if (separator != null) {
                buf.append(separator);
            }
            Object obj = iterator.next();
            if (obj != null) {
                buf.append(obj);
            }
        }
        return buf.toString();
    }

    public static String join(Collection collection, char separator) {
        if (collection == null) {
            return null;
        }
        return join(collection.iterator(), separator);
    }

    public static String join(Collection collection, String separator) {
        if (collection == null) {
            return null;
        }
        return join(collection.iterator(), separator);
    }

    /**
     * @deprecated
     */
    public static String deleteSpaces(String str) {
        if (str == null) {
            return null;
        }
        return CharSetUtils.delete(str, new String[]{" \t\r\n\b"});
    }

    public static String deleteWhitespace(String str) {
        if (isEmpty(str)) {
            return str;
        }
        int sz = str.length();
        char[] chs = new char[sz];
        int count = 0;
        for (int i = 0; i < sz; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                chs[(count++)] = str.charAt(i);
            }
        }
        if (count == sz) {
            return str;
        }
        return new String(chs, 0, count);
    }

    public static String removeStart(String str, String remove) {
        if ((isEmpty(str)) || (isEmpty(remove))) {
            return str;
        }
        if (str.startsWith(remove)) {
            return str.substring(remove.length());
        }
        return str;
    }

    public static String removeEnd(String str, String remove) {
        if ((isEmpty(str)) || (isEmpty(remove))) {
            return str;
        }
        if (str.endsWith(remove)) {
            return str.substring(0, str.length() - remove.length());
        }
        return str;
    }

    public static String remove(String str, String remove) {
        if ((isEmpty(str)) || (isEmpty(remove))) {
            return str;
        }
        return replace(str, remove, "", -1);
    }

    public static String remove(String str, char remove) {
        if ((isEmpty(str)) || (str.indexOf(remove) == -1)) {
            return str;
        }
        char[] chars = str.toCharArray();
        int pos = 0;
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] != remove) {
                chars[(pos++)] = chars[i];
            }
        }
        return new String(chars, 0, pos);
    }

    public static String replaceOnce(String text, String repl, String with) {
        return replace(text, repl, with, 1);
    }

    public static String replace(String text, String repl, String with) {
        return replace(text, repl, with, -1);
    }

    public static String replace(String text, String repl, String with, int max) {
        if ((isEmpty(text)) || (isEmpty(repl)) || (with == null) || (max == 0)) {
            return text;
        }
        int start = 0;
        int end = text.indexOf(repl, start);
        if (end == -1) {
            return text;
        }
        int replLength = repl.length();
        int increase = with.length() - replLength;
        increase = increase < 0 ? 0 : increase;
        increase *= (max > 64 ? 64 : max < 0 ? 16 : max);
        StringBuffer buf = new StringBuffer(text.length() + increase);
        while (end != -1) {
            buf.append(text.substring(start, end)).append(with);
            start = end + replLength;
            max--;
            if (max == 0) {
                break;
            }
            end = text.indexOf(repl, start);
        }
        buf.append(text.substring(start));
        return buf.toString();
    }

    public static String replaceChars(String str, char searchChar, char replaceChar) {
        if (str == null) {
            return null;
        }
        return str.replace(searchChar, replaceChar);
    }

    public static String replaceChars(String str, String searchChars, String replaceChars) {
        if ((isEmpty(str)) || (isEmpty(searchChars))) {
            return str;
        }
        if (replaceChars == null) {
            replaceChars = "";
        }
        boolean modified = false;
        int replaceCharsLength = replaceChars.length();
        int strLength = str.length();
        StringBuffer buf = new StringBuffer(strLength);
        for (int i = 0; i < strLength; i++) {
            char ch = str.charAt(i);
            int index = searchChars.indexOf(ch);
            if (index >= 0) {
                modified = true;
                if (index < replaceCharsLength) {
                    buf.append(replaceChars.charAt(index));
                }
            } else {
                buf.append(ch);
            }
        }
        if (modified) {
            return buf.toString();
        }
        return str;
    }

    /**
     * @deprecated
     */
    public static String overlayString(String text, String overlay, int start, int end) {
        return start + overlay.length() + text.length() - end + 1 + text.substring(0, start) + overlay +
                text.substring(end);
    }

    public static String overlay(String str, String overlay, int start, int end) {
        if (str == null) {
            return null;
        }
        if (overlay == null) {
            overlay = "";
        }
        int len = str.length();
        if (start < 0) {
            start = 0;
        }
        if (start > len) {
            start = len;
        }
        if (end < 0) {
            end = 0;
        }
        if (end > len) {
            end = len;
        }
        if (start > end) {
            int temp = start;
            start = end;
            end = temp;
        }
        return len + start - end + overlay.length() + 1 + str.substring(0, start) + overlay + str.substring(end);
    }

    public static String chomp(String str) {
        if (isEmpty(str)) {
            return str;
        }
        if (str.length() == 1) {
            char ch = str.charAt(0);
            if ((ch == '\r') || (ch == '\n')) {
                return "";
            }
            return str;
        }
        int lastIdx = str.length() - 1;
        char last = str.charAt(lastIdx);
        if (last == '\n') {
            if (str.charAt(lastIdx - 1) == '\r') {
                lastIdx--;
            }
        } else if (last != '\r') {
            lastIdx++;
        }
        return str.substring(0, lastIdx);
    }

    public static String chomp(String str, String separator) {
        if ((isEmpty(str)) || (separator == null)) {
            return str;
        }
        if (str.endsWith(separator)) {
            return str.substring(0, str.length() - separator.length());
        }
        return str;
    }

    /**
     * @deprecated
     */
    public static String chompLast(String str) {
        return chompLast(str, "\n");
    }

    /**
     * @deprecated
     */
    public static String chompLast(String str, String sep) {
        if (str.length() == 0) {
            return str;
        }
        String sub = str.substring(str.length() - sep.length());
        if (sep.equals(sub)) {
            return str.substring(0, str.length() - sep.length());
        }
        return str;
    }

    /**
     * @deprecated
     */
    public static String getChomp(String str, String sep) {
        int idx = str.lastIndexOf(sep);
        if (idx == str.length() - sep.length()) {
            return sep;
        }
        if (idx != -1) {
            return str.substring(idx);
        }
        return "";
    }

    /**
     * @deprecated
     */
    public static String prechomp(String str, String sep) {
        int idx = str.indexOf(sep);
        if (idx != -1) {
            return str.substring(idx + sep.length());
        }
        return str;
    }

    /**
     * @deprecated
     */
    public static String getPrechomp(String str, String sep) {
        int idx = str.indexOf(sep);
        if (idx != -1) {
            return str.substring(0, idx + sep.length());
        }
        return "";
    }

    public static String chop(String str) {
        if (str == null) {
            return null;
        }
        int strLen = str.length();
        if (strLen < 2) {
            return "";
        }
        int lastIdx = strLen - 1;
        String ret = str.substring(0, lastIdx);
        char last = str.charAt(lastIdx);
        if ((last == '\n') &&
                (ret.charAt(lastIdx - 1) == '\r')) {
            return ret.substring(0, lastIdx - 1);
        }
        return ret;
    }

    /**
     * @deprecated
     */
    public static String chopNewline(String str) {
        int lastIdx = str.length() - 1;
        if (lastIdx <= 0) {
            return "";
        }
        char last = str.charAt(lastIdx);
        if (last == '\n') {
            if (str.charAt(lastIdx - 1) == '\r') {
                lastIdx--;
            }
        } else {
            lastIdx++;
        }
        return str.substring(0, lastIdx);
    }

    /**
     * @deprecated
     */
    public static String escape(String str) {
        return StringEscapeUtils.escapeJava(str);
    }

    public static String repeat(String str, int repeat) {
        if (str == null) {
            return null;
        }
        if (repeat <= 0) {
            return "";
        }
        int inputLength = str.length();
        if ((repeat == 1) || (inputLength == 0)) {
            return str;
        }
        if ((inputLength == 1) && (repeat <= 8192)) {
            return padding(repeat, str.charAt(0));
        }
        int outputLength = inputLength * repeat;
        switch (inputLength) {
            case 1:
                char ch = str.charAt(0);
                char[] output1 = new char[outputLength];
                for (int i = repeat - 1; i >= 0; i--) {
                    output1[i] = ch;
                }
                return new String(output1);
            case 2:
                char ch0 = str.charAt(0);
                char ch1 = str.charAt(1);
                char[] output2 = new char[outputLength];
                for (int i = repeat * 2 - 2; i >= 0; i--) {
                    output2[i] = ch0;
                    output2[(i + 1)] = ch1;
                    i--;
                }
                return new String(output2);
        }
        StringBuffer buf = new StringBuffer(outputLength);
        for (int i = 0; i < repeat; i++) {
            buf.append(str);
        }
        return buf.toString();
    }

    private static String padding(int repeat, char padChar)
            throws IndexOutOfBoundsException {
        if (repeat < 0) {
            throw new IndexOutOfBoundsException("Cannot pad a negative amount: " + repeat);
        }
        char[] buf = new char[repeat];
        for (int i = 0; i < buf.length; i++) {
            buf[i] = padChar;
        }
        return new String(buf);
    }

    public static String rightPad(String str, int size) {
        return rightPad(str, size, ' ');
    }

    public static String rightPad(String str, int size, char padChar) {
        if (str == null) {
            return null;
        }
        int pads = size - str.length();
        if (pads <= 0) {
            return str;
        }
        if (pads > 8192) {
            return rightPad(str, size, String.valueOf(padChar));
        }
        return str.concat(padding(pads, padChar));
    }

    public static String rightPad(String str, int size, String padStr) {
        if (str == null) {
            return null;
        }
        if (isEmpty(padStr)) {
            padStr = " ";
        }
        int padLen = padStr.length();
        int strLen = str.length();
        int pads = size - strLen;
        if (pads <= 0) {
            return str;
        }
        if ((padLen == 1) && (pads <= 8192)) {
            return rightPad(str, size, padStr.charAt(0));
        }
        if (pads == padLen) {
            return str.concat(padStr);
        }
        if (pads < padLen) {
            return str.concat(padStr.substring(0, pads));
        }
        char[] padding = new char[pads];
        char[] padChars = padStr.toCharArray();
        for (int i = 0; i < pads; i++) {
            padding[i] = padChars[(i % padLen)];
        }
        return str.concat(new String(padding));
    }

    public static String leftPad(String str, int size) {
        return leftPad(str, size, ' ');
    }

    public static String leftPad(String str, int size, char padChar) {
        if (str == null) {
            return null;
        }
        int pads = size - str.length();
        if (pads <= 0) {
            return str;
        }
        if (pads > 8192) {
            return leftPad(str, size, String.valueOf(padChar));
        }
        return padding(pads, padChar).concat(str);
    }

    public static String leftPad(String str, int size, String padStr) {
        if (str == null) {
            return null;
        }
        if (isEmpty(padStr)) {
            padStr = " ";
        }
        int padLen = padStr.length();
        int strLen = str.length();
        int pads = size - strLen;
        if (pads <= 0) {
            return str;
        }
        if ((padLen == 1) && (pads <= 8192)) {
            return leftPad(str, size, padStr.charAt(0));
        }
        if (pads == padLen) {
            return padStr.concat(str);
        }
        if (pads < padLen) {
            return padStr.substring(0, pads).concat(str);
        }
        char[] padding = new char[pads];
        char[] padChars = padStr.toCharArray();
        for (int i = 0; i < pads; i++) {
            padding[i] = padChars[(i % padLen)];
        }
        return new String(padding).concat(str);
    }

    public static String center(String str, int size) {
        return center(str, size, ' ');
    }

    public static String center(String str, int size, char padChar) {
        if ((str == null) || (size <= 0)) {
            return str;
        }
        int strLen = str.length();
        int pads = size - strLen;
        if (pads <= 0) {
            return str;
        }
        str = leftPad(str, strLen + pads / 2, padChar);
        str = rightPad(str, size, padChar);
        return str;
    }

    public static String center(String str, int size, String padStr) {
        if ((str == null) || (size <= 0)) {
            return str;
        }
        if (isEmpty(padStr)) {
            padStr = " ";
        }
        int strLen = str.length();
        int pads = size - strLen;
        if (pads <= 0) {
            return str;
        }
        str = leftPad(str, strLen + pads / 2, padStr);
        str = rightPad(str, size, padStr);
        return str;
    }

    public static String upperCase(String str) {
        if (str == null) {
            return null;
        }
        return str.toUpperCase();
    }

    public static String lowerCase(String str) {
        if (str == null) {
            return null;
        }
        return str.toLowerCase();
    }

    public static String capitalize(String str) {
        int strLen;
        if ((str == null) || ((strLen = str.length()) == 0)) {
            return str;
        }
        return strLen + Character.toTitleCase(str.charAt(0)) + str.substring(1);
    }

    /**
     * @deprecated
     */
    public static String capitalise(String str) {
        return capitalize(str);
    }

    public static String uncapitalize(String str) {
        int strLen;
        if ((str == null) || ((strLen = str.length()) == 0)) {
            return str;
        }
        return strLen + Character.toLowerCase(str.charAt(0)) + str.substring(1);
    }

    /**
     * @deprecated
     */
    public static String uncapitalise(String str) {
        return uncapitalize(str);
    }

    public static String swapCase(String str) {
        int strLen;
        if ((str == null) || ((strLen = str.length()) == 0)) {
            return str;
        }
        StringBuffer buffer = new StringBuffer(strLen);

        char ch = '\000';
        for (int i = 0; i < strLen; i++) {
            ch = str.charAt(i);
            if (Character.isUpperCase(ch)) {
                ch = Character.toLowerCase(ch);
            } else if (Character.isTitleCase(ch)) {
                ch = Character.toLowerCase(ch);
            } else if (Character.isLowerCase(ch)) {
                ch = Character.toUpperCase(ch);
            }
            buffer.append(ch);
        }
        return buffer.toString();
    }

    /**
     * @deprecated
     */
    public static String capitaliseAllWords(String str) {
        return WordUtils.capitalize(str);
    }

    public static int countMatches(String str, String sub) {
        if ((isEmpty(str)) || (isEmpty(sub))) {
            return 0;
        }
        int count = 0;
        int idx = 0;
        while ((idx = str.indexOf(sub, idx)) != -1) {
            count++;
            idx += sub.length();
        }
        return count;
    }

    public static boolean isAlpha(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if (!Character.isLetter(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isAlphaSpace(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if ((!Character.isLetter(str.charAt(i))) && (str.charAt(i) != ' ')) {
                return false;
            }
        }
        return true;
    }

    public static boolean isAlphanumeric(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if (!Character.isLetterOrDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isAlphanumericSpace(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if ((!Character.isLetterOrDigit(str.charAt(i))) && (str.charAt(i) != ' ')) {
                return false;
            }
        }
        return true;
    }

    public static boolean isAsciiPrintable(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if (!CharUtils.isAsciiPrintable(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNumeric(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNumericSpace(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if ((!Character.isDigit(str.charAt(i))) && (str.charAt(i) != ' ')) {
                return false;
            }
        }
        return true;
    }

    public static boolean isWhitespace(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static String defaultString(String str) {
        return str == null ? "" : str;
    }

    public static String defaultString(String str, String defaultStr) {
        return str == null ? defaultStr : str;
    }

    public static String defaultIfEmpty(String str, String defaultStr) {
        return StringUtils.isEmpty(str) ? defaultStr : str;
    }

    public static String reverse(String str) {
        if (str == null) {
            return null;
        }
        return new StringBuffer(str).reverse().toString();
    }

    public static String reverseDelimited(String str, char separatorChar) {
        if (str == null) {
            return null;
        }
        String[] strs = split(str, separatorChar);
        ArrayUtils.reverse(strs);
        return join(strs, separatorChar);
    }

    /**
     * @deprecated
     */
    public static String reverseDelimitedString(String str, String separatorChars) {
        if (str == null) {
            return null;
        }
        String[] strs = split(str, separatorChars);
        ArrayUtils.reverse(strs);
        if (separatorChars == null) {
            return join(strs, ' ');
        }
        return join(strs, separatorChars);
    }

    public static String abbreviate(String str, int maxWidth) {
        return abbreviate(str, 0, maxWidth);
    }

    public static String abbreviate(String str, int offset, int maxWidth) {
        if (str == null) {
            return null;
        }
        if (maxWidth < 4) {
            throw new IllegalArgumentException("Minimum abbreviation width is 4");
        }
        if (str.length() <= maxWidth) {
            return str;
        }
        if (offset > str.length()) {
            offset = str.length();
        }
        if (str.length() - offset < maxWidth - 3) {
            offset = str.length() - (maxWidth - 3);
        }
        if (offset <= 4) {
            return str.substring(0, maxWidth - 3) + "...";
        }
        if (maxWidth < 7) {
            throw new IllegalArgumentException("Minimum abbreviation width with offset is 7");
        }
        if (offset + (maxWidth - 3) < str.length()) {
            return "..." + abbreviate(str.substring(offset), maxWidth - 3);
        }
        return "..." + str.substring(str.length() - (maxWidth - 3));
    }

    public static String difference(String str1, String str2) {
        if (str1 == null) {
            return str2;
        }
        if (str2 == null) {
            return str1;
        }
        int at = indexOfDifference(str1, str2);
        if (at == -1) {
            return "";
        }
        return str2.substring(at);
    }

    public static int indexOfDifference(String str1, String str2) {
        if (str1 == str2) {
            return -1;
        }
        if ((str1 == null) || (str2 == null)) {
            return 0;
        }
        int i = 0;
        for (; (i < str1.length()) && (i < str2.length()); i++) {
            if (str1.charAt(i) != str2.charAt(i)) {
                break;
            }
        }
        if ((i < str2.length()) || (i < str1.length())) {
            return i;
        }
        return -1;
    }

    public static int getLevenshteinDistance(String s, String t) {
        if ((s == null) || (t == null)) {
            throw new IllegalArgumentException("Strings must not be null");
        }
        int n = s.length();
        int m = t.length();
        if (n == 0) {
            return m;
        }
        if (m == 0) {
            return n;
        }
        int[] p = new int[n + 1];
        int[] d = new int[n + 1];
        for (int i = 0; i <= n; i++) {
            p[i] = i;
        }
        for (int j = 1; j <= m; j++) {
            char t_j = t.charAt(j - 1);
            d[0] = j;
            for (int i = 1; i <= n; i++) {
                int cost = s.charAt(i - 1) == t_j ? 0 : 1;

                d[i] = Math.min(Math.min(d[(i - 1)] + 1, p[i] + 1), p[(i - 1)] + cost);
            }
            int[] _d = p;
            p = d;
            d = _d;
        }
        return p[n];
    }
}
