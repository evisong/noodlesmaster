package me.evis.mobile.util;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class XmlUtilTest extends TestCase {

	/**
	 * Note: test project and target project should be both built before 
	 * running the test case. Or there will be class/method not found 
	 * exceptions.
	 * @throws Exception 
	 */
	public void testGetXPathItemText() throws Exception {
	    InputStream is = getClass().getResourceAsStream("XmlUtilTest_data.xml");
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(is);
        
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        
        NodeList itemNodes = doc.getElementsByTagName("Item");
        if (itemNodes.getLength() > 0) {
            Assert.assertEquals("B007A8DRGE", 
                    XmlUtil.getXPathItemText(doc, xpath, "//Item/ASIN/text()"));
            Assert.assertEquals("Perl语言入门(影印版)(第6版)", 
                    XmlUtil.getXPathItemText(doc, xpath, "//Item/ItemAttributes/Title/text()"));
            Assert.assertEquals("http://ec4.images-amazon.com/images/I/51OX0kckKjL._SL160_.jpg", 
                    XmlUtil.getXPathItemText(doc, xpath, "//Item/MediumImage/URL/text()"));
            String brandOrAuthor = 
                    XmlUtil.getXPathItemText(doc, xpath, "//Item/ItemAttributes/Brand/text()");
            if (brandOrAuthor == null) {
                brandOrAuthor = XmlUtil.getXPathItemText(doc, xpath, "//Item/ItemAttributes/Author/text()");
            }
            Assert.assertEquals("南京东南大学出版社", brandOrAuthor);
            Assert.assertNotSame("", 
                    XmlUtil.getXPathItemText(doc, xpath, "//Item/EditorialReviews/EditorialReview/Content/text()"));
        }
	}

}
