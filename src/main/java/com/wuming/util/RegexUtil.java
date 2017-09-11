package com.wuming.util;

import java.util.Date;
import java.util.regex.Pattern;

/**
 * 2016/10/20 17:44
 *
 * @author yangyang
 */
@SuppressWarnings("unused")
public class RegexUtil {
    /**
     * 验证Email
     *
     * @param email email地址，格式：zhangsan@sina.com，zhangsan@xxx.com.cn，xxx代表邮件服务商
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean checkEmail(String email) {
        if (email == null) {
            return false;
        }
        String regex = "\\w+@\\w+\\.[a-z]+(\\.[a-z]+)?";
        return Pattern.matches(regex, email);
    }

    /**
     * 验证身份证号码
     *
     * @param idCard 居民身份证号码15位或18位，最后一位可能是数字或字母
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean checkIdCard(String idCard) {
        if (idCard == null) {
            return false;
        }
        String regex = "(^\\d{15}$)|(^\\d{18}$)|(^\\d{17}(\\d|X|x)$)";
        return Pattern.matches(regex, idCard);
    }

    /**
     * 验证手机号码（支持国际格式，+86135xxxx...（中国内地），+00852137xxxx...（中国香港））
     *
     * @param mobile 移动、联通、电信运营商的号码段
     *               <p>移动的号段：134(0-8)、135、136、137、138、139、147（预计用于TD上网卡）
     *               、150、151、152、157（TD专用）、158、159、187（未启用）、188（TD专用）</p>
     *               <p>联通的号段：130、131、132、155、156（世界风专用）、185（未启用）、186（3g）</p>
     *               <p>电信的号段：133、153、180（未启用）、189</p>
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean checkMobile(String mobile) {
        if (mobile == null) {
            return false;
        }
        String regex = "(\\+\\d+)?1[345678]\\d{9}$";
        return Pattern.matches(regex, mobile);
    }

    /**
     * 获取手机运营商
     * 中国电信号码格式验证 手机段： 133,153,180,181,189,177,1700,173
     * 中国联通号码格式验证 手机段：130,131,132,155,156,185,186,145,176,1707,1708,1709
     * 中国移动号码格式验证 手机段：134,135,136,137,138,139,150,151,152,157,158,159,182,183,184
     * ,187,188,147,178,1705
     *
     * @return
     */
    public static String getMobileCarry(String mobile) {
        String CHINA_TELECOM_PATTERN = "(^1(33|53|7[37]|8[019])\\d{8}$)|(^1700\\d{7}$)";
        String CHINA_UNICOM_PATTERN = "(^1(3[0-2]|4[5]|5[56]|7[6]|8[56])\\d{8}$)|(^170[7-9]\\d{7}$)";
        String CHINA_MOBILE_PATTERN = "(^1(3[4-9]|4[7]|5[0-27-9]|7[8]|8[2-478])\\d{8}$)|(^1705\\d{7}$)";
        if (Pattern.matches(CHINA_TELECOM_PATTERN, mobile)) {
            return "DX";
        } else if (Pattern.matches(CHINA_UNICOM_PATTERN, mobile)) {
            return "LT";
        } else if (Pattern.matches(CHINA_MOBILE_PATTERN, mobile)) {
            return "YD";
        } else {
            return null;
        }
    }

    /**
     * 验证固定电话号码
     *
     * @param phone 电话号码，格式：国家（地区）电话代码 + 区号（城市代码） + 电话号码，如：+8602085588447
     *              <p><b>国家（地区） 代码 ：</b>标识电话号码的国家（地区）的标准国家（地区）代码。它包含从 0 到 9 的一位或多位数字，
     *              数字之后是空格分隔的国家（地区）代码。</p>
     *              <p><b>区号（城市代码）：</b>这可能包含一个或多个从 0 到 9 的数字，地区或城市代码放在圆括号——
     *              对不使用地区或城市代码的国家（地区），则省略该组件。</p>
     *              <p><b>电话号码：</b>这包含从 0 到 9 的一个或多个数字 </p>
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean checkPhone(String phone) {
        if (phone == null) {
            return false;
        }
        String regex = "(\\+\\d+)?(\\d{3,4}-?)?\\d{7,8}$";
        return Pattern.matches(regex, phone);
    }

    /**
     * 验证整数（正整数和负整数）
     *
     * @param digit 一位或多位0-9之间的整数
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean checkDigit(String digit) {
        if (digit == null) {
            return false;
        }
        String regex = "-?[1-9]\\d+";
        return Pattern.matches(regex, digit);
    }

    /**
     * 验证整数 (>=0)
     *
     * @param str 一位或者多位非负整数
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean checkNumeric(String str) {
        if (str == null) {
            return false;
        }
        String regex = "\\d*";
        return Pattern.matches(regex, str);
    }

    /**
     * 验证正整数 (>0)
     *
     * @param str 一位或者多位非负整数
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean checkPositiveInteger(String str) {
        if (str == null) {
            return false;
        }
        String regex = "^\\d*[1-9]\\d*$";
        return Pattern.matches(regex, str);
    }

    /**
     * 验证整数和浮点数（正负整数和正负浮点数）
     *
     * @param decimals 一位或多位0-9之间的浮点数，如：1.23，233.30
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean checkDecimals(String decimals) {
        if (decimals == null) {
            return false;
        }
        String regex = "-?\\d+(\\.\\d+)?";
        return Pattern.matches(regex, decimals);
    }

    /**
     * 1或者2位小数
     *
     * @param decimal
     * @return
     */
    public static boolean checkDecimal(String decimal) {
        if (decimal == null) {
            return false;
        }
        String regex = "^0\\.\\d{1,2}$";
        return Pattern.matches(regex, decimal);
    }

    /**
     * 验证空白字符
     *
     * @param blankSpace 空白字符，包括：空格、\t、\n、\r、\f、\x0B
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean checkBlankSpace(String blankSpace) {
        if (blankSpace == null) {
            return false;
        }
        String regex = "\\s+";
        return Pattern.matches(regex, blankSpace);
    }

    /**
     * 验证中文
     *
     * @param chinese 中文字符
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean checkChinese(String chinese) {
        if (chinese == null) {
            return false;
        }
        String regex = "^[\u4E00-\u9FA5]+$";
        return Pattern.matches(regex, chinese);
    }

    /**
     * 验证日期（年月日）
     *
     * @param birthday 日期，格式：1992-09-03，或1992.09.03
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean checkBirthday(String birthday) {
        if (birthday == null) {
            return false;
        }
        String regex = "[1-9]{4}([-./])\\d{1,2}\\1\\d{1,2}";
        return Pattern.matches(regex, birthday);
    }

    /**
     * 验证URL地址
     *
     * @param url 格式：http://blog.csdn.net:80/xyang81/article/details/7705960? 或 http://www.csdn.net:80
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean checkURL(String url) {
        if (url == null) {
            return false;
        }
        String regex = "(https?://(w{3}\\.)?)?\\w+\\.\\w+(\\.[a-zA-Z]+)*(:\\d{1,5})?(/\\w*)*(\\??(.+=.*)?(&.+=.*)?)?";
        return Pattern.matches(regex, url);
    }

    /**
     * 匹配中国邮政编码
     *
     * @param postcode 邮政编码
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean checkPostcode(String postcode) {
        if (postcode == null) {
            return false;
        }
        String regex = "[1-9]\\d{5}";
        return Pattern.matches(regex, postcode);
    }

    /**
     * 匹配IP地址(简单匹配，格式，如：192.168.1.1，127.0.0.1，没有匹配IP段的大小)
     *
     * @param ipAddress IPv4标准地址
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean checkIpAddress(String ipAddress) {
        if (ipAddress == null) {
            return false;
        }
        String regex = "[1-9](\\d{1,2})?\\.(0|([1-9](\\d{1,2})?))\\.(0|([1-9](\\d{1,2})?))\\.(0|([1-9](\\d{1,2})?))";
        return Pattern.matches(regex, ipAddress);
    }

    /**
     * 判断信用卡时间是否过期
     *
     * @param expire_date
     * @return
     */
    public static boolean verifyExpireDate(String expire_date) {
        Integer month;
        Integer year;
        String months;
        String years;
        if (expire_date.contains("/")) {
            months = expire_date.split("/")[0];
            years = expire_date.split("/")[1];
            if (checkNumeric(months) && checkNumeric(years)) {
                month = Integer.valueOf(months);
                year = Integer.valueOf(years);
            } else {
                return false;
            }
        } else {
            months = expire_date.substring(0, 2);
            years = expire_date.substring(2);
            if (checkNumeric(months) && checkNumeric(years)) {
                month = Integer.valueOf(months);
                year = Integer.valueOf(years);
            } else {
                return false;
            }
        }
        String now = DateUtil.yearMonthToStr(new Date());
        now = now.substring(2, now.length());
        String[] date = now.split("-");
        Integer nowYear = Integer.valueOf(date[0]);
        Integer nowMonth = Integer.valueOf(date[1]);
        if (nowYear > year || (nowYear.equals(year) && nowMonth > month)) {
            return false;
        } else {
            return true;
        }
    }

}

