package com.wuming.util;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.DoubleStream;

/**
 * Created by wuming on 16/1/22.
 */
public class GaoDeMapApi {

    public static Map<String, String> getLngLat(String address) {
        Map<String, String> map = new HashMap<>();
        String url = "http://restapi.amap.com/v3/geocode/geo?key=363f73cc6d1cd03f815332fc5ed430e9&s=rsv3&city=35&address=";
        String json = loadJSON(url + address);
        JSONObject obj = JSON.parseObject(json);
//        System.out.println("retOb: " + obj);
        if (obj.getString("status").equals("1")) {
            JSONObject dataObj = (JSONObject) (obj.getJSONArray("geocodes").get(0));
            String location = dataObj.getString("location");
//            System.out.println("经度，纬度：" + location);
            List<String> lngLatList = Splitters.COMMA.splitToList(location);
            map.put("lng", lngLatList.get(0));
            map.put("lat", lngLatList.get(1));
            return map;
        } else {
            map.put("lng", "0.0");
            map.put("lat", "0.0");
//            System.out.println("未找到相匹配的经纬度！");
        }
        return map;
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
