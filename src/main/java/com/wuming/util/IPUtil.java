package com.wuming.util;


import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

public class IPUtil {

    /**
     * 系统的本地IP地址
     */
    public static final String LOCAL_IP;
    /**
     * 系统的本地服务器名
     */
    public static final String HOST_NAME;
    private static final Logger log = LoggerFactory.getLogger(IPUtil.class);
    private static String LOCAL_IP_STAR_STR = "192.168.";

    static {
        String ip = null;
        String hostName = null;
        try {
            hostName = InetAddress.getLocalHost().getHostName();
            InetAddress ipAddr[] = InetAddress.getAllByName(hostName);
            for (int i = 0; i < ipAddr.length; i++) {
                ip = ipAddr[i].getHostAddress();
                if (ip.startsWith(LOCAL_IP_STAR_STR)) {
                    break;
                }
            }
            if (ip == null) {
                ip = ipAddr[0].getHostAddress();
            }

        } catch (UnknownHostException e) {
            log.error("IpHelper error.");
            e.printStackTrace();
        }

        LOCAL_IP = ip;
        HOST_NAME = hostName;

    }

    /**
     * <p>
     * 获取客户端的IP地址的方法是：request.getRemoteAddr()，这种方法在大部分情况下都是有效的。
     * 但是在通过了Apache,Squid等反向代理软件就不能获取到客户端的真实IP地址了，如果通过了多级反向代理的话，
     * X-Forwarded-For的值并不止一个，而是一串IP值， 究竟哪个才是真正的用户端的真实IP呢？
     * 答案是取X-Forwarded-For中第一个非unknown的有效IP字符串。
     * 例如：X-Forwarded-For：192.168.1.110, 192.168.1.120,
     * 192.168.1.130, 192.168.1.100 用户真实IP为： 192.168.1.110
     * </p>
     *
     * @param request
     * @return
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
            if (ip.equals("127.0.0.1")) {
                /** 根据网卡取本机配置的IP */
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost();
                    ip = inet.getHostAddress();
                } catch (UnknownHostException e) {
                    log.error("IpHelper error." + e.toString());
                }
            }
        }
        /**
         * 对于通过多个代理的情况， 第一个IP为客户端真实IP,多个IP按照','分割 "***.***.***.***".length() =
         * 15
         */
        if (ip != null && ip.length() > 15) {
            if (ip.indexOf(",") > 0) {
                ip = ip.substring(0, ip.indexOf(","));
            }
        }
        return ip;
    }

    public static synchronized String getSourceFromIP(String str) {
        if (StringUtils.isBlank(str)) {
            return null;
        }
        BufferedInputStream bis = null;
        StringBuffer sBuffer = new StringBuffer();
        try {
            URL url = new URL("http://www.ip.cn/getip.php?action=getip&ip_url=" + str);
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setUseCaches(true);
            urlConn.setRequestMethod("GET");
            urlConn.setRequestProperty("Content-Type", "text/html;charset=UTF-8");
            bis = new BufferedInputStream(urlConn.getInputStream());

            byte[] tmp = new byte[2048];
            int l = 0;
            while ((l = bis.read(tmp)) != -1) {
                sBuffer.append(new String(tmp, 0, l, "GB2312"));
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                bis = null;
            }
        }
        if (StringUtils.isNotBlank(sBuffer.toString())) {
            return StringUtils.substringBetween(sBuffer.toString(), "来自：", "</p>");
        }
        return null;
    }

    /**
     * 获取外网ip
     *
     * @return 返回外网ip
     */
    public static String getOutsideIp() {
        String ip = "";
        System.out.println();
        String site;
        URL url;
        URLConnection connection;
        BufferedReader in;
        String line;
        try {
            site = "http://ipinfo.io/ip";
            url = new URL(site);
            connection = url.openConnection();
            connection.connect();
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((line = in.readLine()) != null) {
                ip += line;
            }
            if (StringUtils.isEmpty(ip)) {
                url = new URL("http://ifconfig.me/ip");
                connection = url.openConnection();
                connection.connect();
                in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while ((line = in.readLine()) != null) {
                    ip += line;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ip;
    }

}
