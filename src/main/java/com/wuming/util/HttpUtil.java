package com.wuming.util;

import com.alibaba.fastjson.JSONObject;
import com.wuming.component.SSLClient;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * HttpClient 正确使用
 * <p>
 * 1. 如果响应内容没有完全消耗掉底层连接不能安全地重复使用，将由连接管理器执行关闭和丢弃。
 * 所以 请求后使用 EntityUtils.consume(resEntity); 方法进行释放资源
 * <p>
 * 2.EntityUtils是官方提供一个处理返回实体的工具类，toString方法负责将返回实体装换为字符串，官方是不太建议使用这个类的，
 * 除非返回数据的服务器绝对可信和返回的内容长度是有限的。官方建议是自己使用HttpEntity.getContent()或者
 * HttpEntity.writeTo(OutputStream)，需要提醒的是记得关闭底层资源
 * <p>
 * 3.respone 关闭
 *
 * @author wuming
 * Created on 2017/8/18 18:44
 */
public class HttpUtil {

    /**
     * 普通 HTTP 请求
     *
     * @param url
     * @param map     key: value 参数键值对
     * @param charset
     * @return
     */
    public static String post(String url, Map<String, String> map, String charset) {
        if (Objects.isNull(charset)) charset = "UTF-8";
        String result = null;
        try {
            CloseableHttpClient httpClient = SSLClient.build(url);
            HttpPost httpPost = new HttpPost(url);
            //设置参数
            List<NameValuePair> list = new ArrayList<>();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                list.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            if (list.size() > 0) {
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, charset);
                httpPost.setEntity(entity);
            }
            CloseableHttpResponse response = httpClient.execute(httpPost);
            if (response != null) {
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    result = EntityUtils.toString(resEntity, charset);
                }
                // 释放资源(如果响应内容没有完全消耗掉底层连接不能安全地重复使用)
                EntityUtils.consume(resEntity);
                response.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    /**
     * 普通 POST请求
     *
     * @param url
     * @param param
     * @param charset
     * @return
     */
    public static String post(String url, String param, String charset) {
        if (Objects.isNull(charset)) charset = "UTF-8";
        String result = null;
        try {
            CloseableHttpClient httpClient = SSLClient.build(url);
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(new StringEntity(param, charset));
            CloseableHttpResponse response = httpClient.execute(httpPost);
            if (response != null) {
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    result = EntityUtils.toString(resEntity, charset);
                }
                // 释放资源
                EntityUtils.consume(resEntity);
                response.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    /**
     * notify POST 请求
     * http post method 超时时间 10 秒
     *
     * @param url
     * @param map
     * @param charset
     * @return json格式的响应code和响应结果字符串
     */
    public static String post2(String url, Map<String, String> map, String charset) {
        if (Objects.isNull(charset)) charset = "UTF-8";
        try {
            CloseableHttpClient httpClient = SSLClient.build(url);
            HttpPost httpPost = new HttpPost(url);
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(10000).setConnectionRequestTimeout(10000).setSocketTimeout(10000).build();
            httpPost.setConfig(requestConfig);
            //设置参数
            List<NameValuePair> list = new ArrayList<>();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                list.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            if (list.size() > 0) {
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, charset);
                httpPost.setEntity(entity);
            }
            CloseableHttpResponse response = httpClient.execute(httpPost);
            JSONObject result = new JSONObject();
            if (response != null) {
                result.put("statusCode", response.getStatusLine().getStatusCode());
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    result.put("resText", EntityUtils.toString(resEntity, charset));
                }
                // 释放资源
                EntityUtils.consume(resEntity);
                response.close();
            }
            return result.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * notify POST 请求
     * http post 超时时间 10秒
     *
     * @param url
     * @param param   字符串参数
     * @param charset
     * @return json格式的响应code和响应结果字符串
     */
    public static String post2(String url, String param, String charset) {
        if (Objects.isNull(charset)) charset = "UTF-8";
        try {
            CloseableHttpClient httpClient = SSLClient.build(url);
            HttpPost httpPost = new HttpPost(url);
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(10000).setConnectionRequestTimeout(10000).setSocketTimeout(10000).build();
            httpPost.setConfig(requestConfig);
            httpPost.setEntity(new StringEntity(param, charset));
            CloseableHttpResponse response = httpClient.execute(httpPost);
            JSONObject result = new JSONObject();
            if (response != null) {
                result.put("statusCode", response.getStatusLine().getStatusCode());
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    result.put("resText", EntityUtils.toString(resEntity, charset));
                }
                // 释放资源
                EntityUtils.consume(resEntity);
                response.close();
            }
            return result.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * json字符串以 body 参数传参
     *
     * @param url
     * @param jsonString 请求json字符串
     * @param charset
     * @return
     */
    public static String postJson(String url, String jsonString, String charset) {
        if (Objects.isNull(charset)) charset = "UTF-8";
        String result = null;
        try {
            CloseableHttpClient httpClient = SSLClient.build(url);
            HttpPost httpPost = new HttpPost(url);
            // 以body参数传参，只需如下设置header即可
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setEntity(new StringEntity(jsonString, charset));
            CloseableHttpResponse response = httpClient.execute(httpPost);
            if (response != null) {
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    result = EntityUtils.toString(resEntity);
                }
                // 释放资源
                EntityUtils.consume(resEntity);
                response.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    /**
     * https get method
     *
     * @param url
     * @param charset
     * @return
     */
    public static String get(String url, String charset) {
        if (Objects.isNull(charset)) charset = "UTF-8";
        String result = null;
        try {
            CloseableHttpClient httpClient = SSLClient.build(url);
            HttpGet httpGet = new HttpGet(url);
            CloseableHttpResponse response = httpClient.execute(httpGet);
            if (response != null) {
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    result = EntityUtils.toString(resEntity, charset);
                }
                // 释放资源
                EntityUtils.consume(resEntity);
                response.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
