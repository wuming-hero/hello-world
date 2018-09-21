package com.wuming.util;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
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
            element2Map(root, xmlMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return xmlMap;
    }

    /**
     * 使用递归调用将多层级xml转为map
     *
     * @param rootElement
     * @param map
     */
    private static void element2Map(Element rootElement, Map<String, Object> map) {
        //获得当前节点的子节点
        List<Element> elements = rootElement.getChildren();
        if (elements.size() == 0) {
            // 没有子节点说明当前节点是叶子节点，直接取值
            map.put(rootElement.getName(), rootElement.getTextNormalize());
        } else if (elements.size() == 1) {
            // 只有一个子节点说明不用考虑list的情况，继续递归
            Map<String, Object> tempMap = new HashMap<>();
            element2Map(elements.get(0), tempMap);
            map.put(rootElement.getName(), tempMap);
        } else {
            // 循环处理当前节点的同级节点
            for (Element element : elements) {
                String key = element.getName();
                Namespace namespace = elements.get(0).getNamespace();
                // 查询与当前Dom名相同的Dom节点
                List<Element> sameElementList = rootElement.getChildren(key, namespace);
                // 如果同名的数目大于1则表示要构建list
                if (sameElementList.size() > 1) {
                    List<Map> list = new ArrayList<>();
                    for (Element sameElement : sameElementList) {
                        Map<String, Object> sameTempMap = new HashMap<>();
                        element2Map(sameElement, sameTempMap);
                        list.add(sameTempMap);
                    }
                    map.put(key, list);
                } else {
                    // 非list的数据处理
                    Element currentElement = sameElementList.get(0); // 当前DOM节点
                    // 如果当前DOM节点没有子DOM节点，直接取值
                    if (currentElement.getChildren().size() == 0) {
                        map.put(key, currentElement.getText());
                    } else {
                        // 递归调用
                        Map<String, Object> sameTempMap = new HashMap<>();
                        element2Map(currentElement, sameTempMap);
                        map.put(key, sameTempMap);
                    }
                }
            }
        }
    }

}
