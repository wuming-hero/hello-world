package com.wuming.jsoup;

import com.google.common.collect.ImmutableMap;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Created by wuming on 2017/7/18.
 */
public class JsoupTest {

    private final static Logger log = LoggerFactory.getLogger(JsoupTest.class);

    /**
     * <span class=\\\"atwho-inserted\\\" data-atwho-at-query=\\\"@\\\" contenteditable=\\\"false\\\">
     * <span class=\\\"js-rule-model-attr\\\" data-id=\\\"1724\\\" data-name=\\\"model_施工方式\\\">施工方式</span>
     * </span>&nbsp;
     * <span class=\\\"atwho-inserted\\\" data-atwho-at-query=\\\"!\\\" contenteditable=\\\"false\\\">
     * <span class=\\\"js-rule-operation\\\" data-name=\\\"==\\\">=</span>
     * </span>&nbsp;
     * <span class=\\\"atwho-inserted\\\" contenteditable=\\\"false\\\" data-atwho-at-query=\\\"~\\\">
     * <span class=\\\"js-rule-model-attr-val\\\" data-name=\\\"&quot;热熔法&quot;\\\">热熔法</span>
     * </span>&nbsp;&nbsp;
     * <span class=\\\"atwho-inserted\\\" data-atwho-at-query=\\\"!\\\" contenteditable=\\\"false\\\">
     * <span class=\\\"js-rule-operation\\\" data-name=\\\"&amp;&amp;\\\">和</span>
     * </span>
     * <span class=\\\"atwho-inserted\\\" data-atwho-at-query=\\\"@\\\" contenteditable=\\\"false\\\">
     * <span class=\\\"js-rule-model-attr\\\" data-id=\\\"1728\\\" data-name=\\\"model_铺设方式\\\">铺设方式</span>
     * </span>&nbsp;
     * <span class=\\\"atwho-inserted\\\" data-atwho-at-query=\\\"!\\\" contenteditable=\\\"false\\\">
     * <span class=\\\"js-rule-operation\\\" data-name=\\\"==\\\">=</span>
     * </span>&nbsp;
     * <span class=\\\"atwho-inserted\\\" contenteditable=\\\"false\\\" data-atwho-at-query=\\\"~\\\">
     * <span class=\\\"js-rule-model-attr-val\\\" data-name=\\\"&quot;空铺&quot;\\\">空铺</span>
     * </span>&nbsp;&nbsp;&nbsp;
     */
    private final String htmlStr = "\"<span class=\\\"atwho-inserted\\\" data-atwho-at-query=\\\"@\\\" contenteditable=\\\"false\\\"><span class=\\\"js-rule-model-attr\\\" data-id=\\\"1724\\\" data-name=\\\"model_施工方式\\\">施工方式</span></span>&nbsp;<span class=\\\"atwho-inserted\\\" data-atwho-at-query=\\\"!\\\" contenteditable=\\\"false\\\"><span class=\\\"js-rule-operation\\\" data-name=\\\"==\\\">=</span></span>&nbsp;<span class=\\\"atwho-inserted\\\" contenteditable=\\\"false\\\" data-atwho-at-query=\\\"~\\\"><span class=\\\"js-rule-model-attr-val\\\" data-name=\\\"&quot;热熔法&quot;\\\">热熔法</span></span>&nbsp;&nbsp;<span class=\\\"atwho-inserted\\\" data-atwho-at-query=\\\"!\\\" contenteditable=\\\"false\\\"><span class=\\\"js-rule-operation\\\" data-name=\\\"&amp;&amp;\\\">和</span></span><span class=\\\"atwho-inserted\\\" data-atwho-at-query=\\\"@\\\" contenteditable=\\\"false\\\"><span class=\\\"js-rule-model-attr\\\" data-id=\\\"1728\\\" data-name=\\\"model_铺设方式\\\">铺设方式</span></span>&nbsp;<span class=\\\"atwho-inserted\\\" data-atwho-at-query=\\\"!\\\" contenteditable=\\\"false\\\"><span class=\\\"js-rule-operation\\\" data-name=\\\"==\\\">=</span></span>&nbsp;<span class=\\\"atwho-inserted\\\" contenteditable=\\\"false\\\" data-atwho-at-query=\\\"~\\\"><span class=\\\"js-rule-model-attr-val\\\" data-name=\\\"&quot;空铺&quot;\\\">空铺</span></span>&nbsp;&nbsp;&nbsp;\"";

    /**
     * 规则中引用多个模型属性的值
     */
    @Test
    public void netAmount2() {
        Map<Long, Long> modelAttrIdMap = ImmutableMap.of(1724L, 900L, 1728L, 1000L);
        // 使用Jsoup.parse()字符串后，会自动在html字符串外加上html和body标签
        Document document = Jsoup.parse(htmlStr);
        log.debug("origin resultHtml: {}", document.body().html());
        // 引用模型属性处理, 使用getElementsByAttributeValue检索所有class="js-rule-model-attr"的 dom 节点列表
        Elements modelAttrElements = document.getElementsByAttributeValue("class", "js-rule-model-attr");
        log.debug("modelAttr size: {}", modelAttrElements.size());
        for (Element element : modelAttrElements) {
            Long attrId = Long.valueOf(element.attr("data-id"));
            log.debug("origin data-id: {}", attrId);
            element.attr("data-id", String.valueOf(modelAttrIdMap.get(attrId)));
            log.debug("new data-id: {}", element.attr("data-id"));
        }
        // 替换后的内容
        String resultHtml = document.body().html();
        log.debug("new resultHtml: {}", resultHtml);
    }


    /**
     * jsoup 操作文件示例
     */
    @Test
    public void replaceHtml() {
        try {
            File file = new File(JsoupTest.class.getClassLoader().getResource("file/html.txt").getFile());
            log.debug("fileName: {}, filePath: {}", file.getName(), file.getPath());
            // document 已有html和body标签，则不会自动添加此标签
            Document doc = Jsoup.parse(file, "UTF-8");
            Element body = doc.body();
            Elements elements = body.select("div.Section1>p");
            Element firstElement = elements.first();
            String letter = firstElement.child(1).text();
            log.debug("answer text is: {}" + letter);
            // 修改答案为input 框
            String textTag = "<input type='text' value='' />";
            firstElement.child(1).html(textTag);
            log.debug("new body html: {}", body.html());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
