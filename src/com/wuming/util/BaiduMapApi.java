package com.wuming.util;



import net.sf.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wuming on 16/1/22.
 */
public class BaiduMapApi {

    public static Map<String, Double> getLngLat(String address) {
        Map<String, Double> map = new HashMap<String, Double>();
        String url = "http://api.map.baidu.com/geocoder/v2/?ak=FEc72f64f2ea54c81422a833b1c4d02d&output=json&address=";
        String json = loadJSON(url + address);
        JSONObject obj = JSONObject.fromObject(json);
        if (obj.get("status").toString().equals("0")) {
            double lng = obj.getJSONObject("result").getJSONObject("location").getDouble("lng");
            double lat = obj.getJSONObject("result").getJSONObject("location").getDouble("lat");
            map.put("lng", lng);
            map.put("lat", lat);
            System.out.println("经度：" + lng + "---纬度：" + lat);
        } else {
            map.put("lng", 0.0);
            map.put("lat", 0.0);
            System.out.println("未找到相匹配的经纬度！");
        }
        return map;
    }

    public static String getLngLatPoint(String address) {
        String url = "http://api.map.baidu.com/geocoder/v2/?ak=FEc72f64f2ea54c81422a833b1c4d02d&output=json&address=";
        String json = loadJSON(url + address);
        JSONObject obj = JSONObject.fromObject(json);
        double lng = 0.0;
        double lat = 0.0;
        if (obj.get("status").toString().equals("0")) {
            lng = obj.getJSONObject("result").getJSONObject("location").getDouble("lng");
            lat = obj.getJSONObject("result").getJSONObject("location").getDouble("lat");

            System.out.println("经度：" + lng + "---纬度：" + lat);
        }
        String lngLatPoint = "Point(" + lng + " " + lat + ")";
        return lngLatPoint;
    }

    public static String loadJSON(String url) {
        StringBuilder json = new StringBuilder();
        try {
            URL oracle = new URL(url);
            URLConnection urlConnection = oracle.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String inputLine = null;
            while ((inputLine = in.readLine()) != null) {
                json.append(inputLine);
            }
            in.close();
        } catch (MalformedURLException e) {
        } catch (IOException e) {
        }
        return json.toString();
    }
}
