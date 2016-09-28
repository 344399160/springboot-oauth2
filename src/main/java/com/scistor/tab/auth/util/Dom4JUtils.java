package com.scistor.tab.auth.util;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;

public class Dom4JUtils {

    private static final Logger log = LoggerFactory.getLogger(Dom4JUtils.class);

    /**
     * 获取xml根元素的name属性
     * @param file
     * @return
     */
    public static String getRootAttributeValue(String file) {
        SAXReader saxReader = new SAXReader();
        Document document = null;

        try {
            InputStream inputStream = new ByteArrayInputStream(file.getBytes("UTF-8"));
            document = saxReader.read(inputStream);
            // 获取根元素
            Element root = document.getRootElement();
            String attributeValue = root.attributeValue("name");
            log.debug("attributeValue:" + attributeValue);
            return attributeValue;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }
    
    public static String formatXML(Document document) throws Exception {
        StringWriter strWtr = new StringWriter();
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding("UTF-8");
        XMLWriter xmlWriter = new XMLWriter(strWtr, format);
        xmlWriter.write(document);
        String result = strWtr.toString();
        return result;
    }
}
