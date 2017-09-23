package com.wuming.xml.dom4j;

import com.wuming.util.XmlUtil;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.List;

/**
 * @author wuming
 * Created on 2017/9/23 14:51
 */
public class Dom4jTest {

    /**
     * 读取xml文件为map或json
     */
    @Test
    public void readXmlToMap() throws DocumentException {
        SAXReader reader = new SAXReader();
        Document document = reader.read(XmlUtil.class.getClassLoader().getResource("file/xml/test.xml").getPath());
        // 通过asXML方法将document转换为xml字符串
        System.out.println("asXML: " + document.asXML());
        String str = XmlUtil.xml2Json(document.asXML());
        System.out.println("jsonStr: " + str);
    }

    /**
     * 操作节点属性
     *
     * @throws DocumentException
     */
    @Test
    public void test() throws Exception {
        // 创建SAXReader对象
        SAXReader reader = new SAXReader();
        // 读取文件 转换成Document
        String filePath = this.getClass().getClassLoader().getResource("file/xml/student.xml").getPath();
        Document document = reader.read(new File(filePath));
        // 获取根节点元素对象
        Element root = document.getRootElement();
        System.out.println("-------添加属性前------");
        // 获取第一个名为student的节点
        Element student1Element = root.element("student");
        // 遍历
        listNodes(student1Element);
        // 获取其属性
        Attribute idAttribute = student1Element.attribute("id");
        // 删除其id属性
        student1Element.remove(idAttribute);
        // 为其添加name新属性
        student1Element.addAttribute("name", "这是student1节点的新属性");

        // 添加phone节点
        Element phoneElement = student1Element.addElement("phone");
        // 为phone节点设置值
        phoneElement.setText("131xxxxxxxx");
        System.out.println("-------添加属性后------");
        listNodes(student1Element);
        // 写入修改后的内容到文件
        writerDocumentToFile(document);
    }

    /**
     * document写入新的文件
     *
     * @param document
     * @throws Exception
     */
    public void writerDocumentToFile(Document document) throws Exception {
        // 使用的是OutputFormat.createPrettyPrint(),输出文档时进行了排版格式化
        OutputFormat format = OutputFormat.createPrettyPrint();
        // 设置编码
        format.setEncoding("UTF-8");
        // XMLWriter 指定输出文件以及格式
        String filePath = this.getClass().getClassLoader().getResource("file/xml/blank.xml").getPath();
        XMLWriter writer = new XMLWriter(new OutputStreamWriter(new FileOutputStream(new File(filePath)), "UTF-8"), format);
        // 写入新文件
        writer.write(document);
        writer.flush();
        writer.close();
    }

    // 遍历当前节点下的所有节点
    public void listNodes(Element node) {
        System.out.println("当前节点的名称：" + node.getName());
        // 首先获取当前节点的所有属性节点
        List<Attribute> attrList = node.attributes();
        // 遍历属性节点
        for (Attribute attribute : attrList) {
            System.out.println("属性" + attribute.getName() + ":" + attribute.getValue());
        }
        // 如果当前节点内容不为空，则输出
        if (!(node.getTextTrim().equals(""))) {
            System.out.println(node.getName() + "：" + node.getText());
        }

        // 同时递归迭代当前节点下面的所有子节点
        Iterator<Element> iterator = node.elementIterator();
        while (iterator.hasNext()) {
            Element e = iterator.next();
            listNodes(e);
        }
    }

}
