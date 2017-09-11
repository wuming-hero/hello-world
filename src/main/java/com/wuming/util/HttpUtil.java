package com.wuming.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author wuming
 * Created on 2017/8/18 18:44
 */
public class HttpUtil {

    /**
     * http post method
     *
     * @param url
     * @param map
     * @param charset
     * @return
     */
    public static String post(String url, Map<String, String> map, String charset) {
        if (null == charset) {
            charset = "UTF-8";
        }
        String result = null;
        try {
            HttpClient httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(url);
            //设置参数
            List<NameValuePair> list = new ArrayList<>();
            Iterator iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> elem = (Map.Entry<String, String>) iterator.next();
                list.add(new BasicNameValuePair(elem.getKey(), elem.getValue()));
            }
            if (list.size() > 0) {
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, charset);
                httpPost.setEntity(entity);
            }
            HttpResponse response = httpClient.execute(httpPost);
            if (response != null) {
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    result = EntityUtils.toString(resEntity, charset);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    /**
     * http post method 超时时间 10秒
     * 返回json格式的响应code和响应结果字符串
     *
     * @param url
     * @param map
     * @param charset
     * @return
     */
    public static String post2(String url, Map<String, String> map, String charset) {
        if (null == charset) {
            charset = "UTF-8";
        }
        try {
            HttpClient httpClient = HttpClients.createDefault();
            httpClient.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 3000);
            httpClient.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 3000);
            HttpPost httpPost = new HttpPost(url);
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectTimeout(3000).setConnectionRequestTimeout(1000)
                    .setSocketTimeout(3000).build();
            httpPost.setConfig(requestConfig);
            //设置参数
            List<NameValuePair> list = new ArrayList<NameValuePair>();
            Iterator iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> elem = (Map.Entry<String, String>) iterator.next();
                list.add(new BasicNameValuePair(elem.getKey(), elem.getValue()));
            }
            if (list.size() > 0) {
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list, charset);
                httpPost.setEntity(entity);
            }
            HttpResponse response = httpClient.execute(httpPost);
            JSONObject result = new JSONObject();
            result.put("statusCode", response.getStatusLine().getStatusCode());
            if (response != null) {
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    result.put("resText", EntityUtils.toString(resEntity, charset));
                }
            }
            return result.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }


    /**
     * http post method
     *
     * @param url
     * @param param
     * @param charset
     * @return
     */
    public static String post(String url, String param, String charset) {
        if (null == charset) {
            charset = "UTF-8";
        }
        String result = null;
        try {
            HttpClient httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(new StringEntity(param, charset));
            HttpResponse response = httpClient.execute(httpPost);
            if (response != null) {
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    result = EntityUtils.toString(resEntity, charset);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    /**
     * http post 超时时间 10秒
     * 返回json格式的响应code和响应结果字符串
     *
     * @param url
     * @param param
     * @param charset
     * @return
     */
    public static String post2(String url, String param, String charset) {
        if (null == charset) {
            charset = "UTF-8";
        }
        try {
            HttpClient httpClient = HttpClients.createDefault();
            // 设置超时时间3秒
            httpClient.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 3000);
            httpClient.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 3000);
            HttpPost httpPost = new HttpPost(url);
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectTimeout(3000).setConnectionRequestTimeout(1000)
                    .setSocketTimeout(3000).build();
            httpPost.setConfig(requestConfig);
            httpPost.setEntity(new StringEntity(param, charset));
            HttpResponse response = httpClient.execute(httpPost);
            JSONObject result = new JSONObject();
            result.put("statusCode", response.getStatusLine().getStatusCode());
            if (response != null) {
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    result.put("resText", EntityUtils.toString(resEntity, charset));
                }
            }
            return result.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * http get method
     *
     * @param url
     * @param charset
     * @return
     */
    public static String get(String url, String charset) {
        if (null == charset) {
            charset = "UTF-8";
        }
        String result = null;
        try {
            HttpClient httpClient = new SSLClient();
            HttpGet httpGet = new HttpGet(url);
            HttpResponse response = httpClient.execute(httpGet);
            if (response != null) {
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    result = EntityUtils.toString(resEntity, charset);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
