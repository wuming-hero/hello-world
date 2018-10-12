package com.wuming.util;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 不算根节点，最多只能正确处理1级xml文档
 * 如果有2级及以上，则1级key对的value的值为2级及以上的xml字符串
 */
public class XmlUtil2 {

    static final SAXBuilder saxBuilder = new SAXBuilder();

    /**
     * 标准的XML字符串转为map对象
     * 支持多层级
     *
     * @param xml
     * @return
     */
    public static Map<String, Object> parseXml(String xml) {
        Map<String, Object> xmlMap = new HashMap<>();
        try {
            Document doc = saxBuilder.build(new StringReader(xml));
            Element root = doc.getRootElement();
            element2Map(root.getChildren(), xmlMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return xmlMap;
    }

    /**
     * 使用递归调用将多层级xml转为map
     *
     * @param map
     */
    private static void element2Map(List<Element> elementList, Map<String, Object> map) {
        // 循环处理当前节点的同级节点
        for (Element element : elementList) {
            String key = element.getName();
            // 查询与当前Dom名相同的Dom节点
            List<Element> sameElementList = element.getParentElement().getChildren(key, element.getNamespace());
            // 如果同名的数目大于1则表示要构建list
            if (sameElementList.size() > 1) {
                List<Map> list = new ArrayList<>();
                for (Element sameElement : sameElementList) {
                    Map<String, Object> sameTempMap = new HashMap<>();
                    element2Map(sameElement.getChildren(), sameTempMap);
                    list.add(sameTempMap);
                }
                map.put(key, list);
            } else {
                // 如果当前DOM节点没有子DOM节点，直接取值
                if (element.getChildren().size() == 0) {
                    map.put(key, element.getText());
                } else {
                    // 递归调用
                    Map<String, Object> sameTempMap = new HashMap<>();
                    element2Map(element.getChildren(), sameTempMap);
                    map.put(key, sameTempMap);
                }
            }
        }
    }

}
