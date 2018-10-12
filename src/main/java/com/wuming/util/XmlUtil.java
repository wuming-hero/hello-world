package com.wuming.util;

import org.dom4j.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 解析xml的工具类
 * 1、将多层级xml解析为Map
 * 2、将多层级xml解析为Json
 *
 * @author wuming
 * Created on 2017/9/22 20:39
 */
public class XmlUtil {

    /**
     * 将xml字符串解析为Json格式
     *
     * @param xmlStr
     * @return
     */
    public static String xml2Json(String xmlStr) {
        Map<String, Object> xmlMap = xml2map(xmlStr);
        return JsonMapper.nonDefaultMapper().toJson(xmlMap);
    }

    /**
     * 将xml字符串解析为Map格式
     *
     * @param xmlStr
     * @return
     * @throws DocumentException
     */
    public static Map<String, Object> xml2map(String xmlStr) {
        Map<String, Object> xmlMap = new HashMap<>();
        try {
            Document doc = DocumentHelper.parseText(xmlStr);
            Element rootElement = doc.getRootElement();
            element2Map(rootElement.elements(), xmlMap);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return xmlMap;
    }

    /**
     * 使用递归调用将多层级xml转为map
     *
     * @param map
     */
    private static void element2Map(List<Element> elements, Map<String, Object> map) {
        // 循环处理当前节点的同级节点
        for (Element element : elements) {
            String key = element.getName();
            // 查询与当前Dom名相同的Dom节点
            List<Element> sameElementList = element.getParent().elements(new QName(key, element.getNamespace()));
            // 如果同名的数目大于1则表示要构建list
            if (sameElementList.size() > 1) {
                List<Map> list = new ArrayList<>();
                for (Element sameElement : sameElementList) {
                    Map<String, Object> sameTempMap = new HashMap<>();
                    element2Map(sameElement.elements(), sameTempMap);
                    list.add(sameTempMap);
                }
                map.put(key, list);
            } else {
                // 如果当前DOM节点没有子DOM节点，直接取值
                if (element.elements().size() == 0) {
                    map.put(key, element.getText());
                } else {
                    // 递归调用
                    Map<String, Object> sameTempMap = new HashMap<>();
                    element2Map(element.elements(), sameTempMap);
                    map.put(key, sameTempMap);
                }
            }
        }
    }

}
