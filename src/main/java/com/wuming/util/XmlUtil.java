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
        String jsonStr = JsonMapper.nonDefaultMapper().toJson(xmlMap);
        return jsonStr;
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
            element2Map(xmlMap, rootElement);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return xmlMap;
    }

    /**
     * 使用递归调用将多层级xml转为map
     *
     * @param map
     * @param rootElement
     */
    private static void element2Map(Map<String, Object> map, Element rootElement) {
        //获得当前节点的子节点
        List<Element> elements = rootElement.elements();
        if (elements.size() == 0) {
            // 没有子节点说明当前节点是叶子节点，直接取值
            map.put(rootElement.getName(), rootElement.getText());
        } else if (elements.size() == 1) {
            // 只有一个子节点说明不用考虑list的情况，继续递归
            Map<String, Object> tempMap = new HashMap<>();
            element2Map(tempMap, elements.get(0));
            map.put(rootElement.getName(), tempMap);
        } else {
            // 循环处理当前节点的同级节点
            for (Element element : elements) {
                String key = element.getName();
                Namespace namespace = elements.get(0).getNamespace();
                // 查询与当前Dom名相同的Dom节点
                List<Element> sameElementList = rootElement.elements(new QName(key, namespace));
                // 如果同名的数目大于1则表示要构建list
                if (sameElementList.size() > 1) {
                    List<Map> list = new ArrayList<>();
                    for (Element sameElement : sameElementList) {
                        Map<String, Object> sameTempMap = new HashMap<>();
                        element2Map(sameTempMap, sameElement);
                        list.add(sameTempMap);
                    }
                    map.put(key, list);
                } else {
                    // 非list的数据处理
                    Element currentElement = sameElementList.get(0); // 当前DOM节点
                    // 如果当前DOM节点没有子DOM节点，直接取值
                    if (currentElement.elements().size() == 0) {
                        map.put(key, currentElement.getText());
                    } else {
                        // 递归调用
                        Map<String, Object> sameTempMap = new HashMap<>();
                        element2Map(sameTempMap, currentElement);
                        map.put(key, sameTempMap);
                    }
                }
            }
        }
    }

}
