package com.scistor.tab.auth.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

/**
 * Created by liukai on 2015/9/25.
 */
public class XmlValidateUtil {

    private static final Logger log = LoggerFactory.getLogger(XmlValidateUtil.class);

    public static boolean isIllegalWorkflowXml(String workflowXml, String tag) {

        SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");

        Schema schema = null;
        try {
            InputStream baseXsd = XmlValidateUtil.class.getResourceAsStream("/workflow.xsd");
        	InputStream userDefinedXsd = XmlValidateUtil.class.getResourceAsStream(("/operator-action-0.1.xsd"));
            Source[] source = {
                    new StreamSource(baseXsd),
                    new StreamSource(userDefinedXsd)
            };

            schema = factory.newSchema(source);
        } catch (SAXException e) {
            log.error("创建 XmlValidator失败 ：" + e.toString());
            e.printStackTrace();
        }

        StringReader workflowReader = new StringReader(workflowXml);
        Source workflow = new StreamSource(workflowReader);

        Validator validator = schema.newValidator();

        try {
            validator.validate(workflow);
            return true;
        } catch (SAXException e) {
            log.error("分析模型的XML描述文件校验失败：" + e.toString());
            return false;
        } catch (IOException e) {
            log.error("分析模型的XML描述文件校验失败：" + e.toString());
            return false;
        }
    }
}

