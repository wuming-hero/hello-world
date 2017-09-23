package com.wuming.xml.jdom;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;

/**
 * @author wuming
 * Created on 2017/9/23 15:28
 */
public class JdomTest {


    /**
     * 将xml文件转换为String串
     *
     * @return
     */
    @Test
    public void xmlToString() throws JDOMException, IOException {
        String path = this.getClass().getClassLoader().getResource("file/xml/test.xml").getPath();
        SAXBuilder reader = new SAXBuilder();
        Document document = reader.build(new File(path));

        // 指定输出格式
        Format format = Format.getPrettyFormat();
        format.setEncoding("UTF-8");//设置编码格式
        XMLOutputter outputter = new XMLOutputter(format);
        StringWriter out = new StringWriter();
        outputter.output(document, out);
        System.out.println(out.toString());
    }

    /**
     * jdom操作dom节点
     *
     * @throws JDOMException
     * @throws IOException
     */
    @Test
    public void test() throws JDOMException, IOException {
        String path = this.getClass().getClassLoader().getResource("file/xml/student.xml").getPath();
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(new File(path));
        Element rootElement = doc.getRootElement();
        List list = rootElement.getChildren();
        Iterator it = list.iterator();
        while (it.hasNext()) {
            Element element = (Element) it.next();
            // 获得element属性值
            String id = element.getAttributeValue("id");
            // 获得子element的name的内容
            String name = element.getChildTextTrim("name");
            // 修改名字为name子element的值
            element.getChild("name").setText("wuming");
        }

        // 格式化输出
        Format format = Format.getPrettyFormat();
        format.setEncoding("UTF-8");
        XMLOutputter outputter = new XMLOutputter(format);
        StringWriter out = new StringWriter();
        outputter.output(doc, out);
        System.out.println(out.toString());
    }

}
