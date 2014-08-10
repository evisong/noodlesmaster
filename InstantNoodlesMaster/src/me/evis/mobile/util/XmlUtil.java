package me.evis.mobile.util;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class XmlUtil {

    private XmlUtil() {
        // No instance
    }

    public static String getXPathItemText(Document doc, XPath xpath, String query) throws XPathExpressionException {
        if (doc == null || xpath == null || query == null) {
            return null;
        }
        
        Node node = (Node) xpath.evaluate(query, doc, XPathConstants.NODE);
        String text = null;
        
        if (node != null) {
            text = node.getNodeValue();
            if (text != null) {
                text = text.trim();
            }
        }
        
        return text;
    }
}
