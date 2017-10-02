/**************************************************************************
*
* KOLLECTIVE CONFIDENTIAL
* __________________
*
* Copyright © 2001-2017 Kollective Technology, Inc.
* All Rights Reserved.  
*
* NOTICE:  All material contained herein (including without limitation all
* software code) is, and remains the property of
* Kollective Technology, Inc. ("Kollective") and its suppliers, if any.
* These materials are proprietary to Kollective and its suppliers and are
* covered by U.S. and foreign patents, as well as U.S. and foreign patent
* applications, as well as U.S. and foreign trade secret and copyright law.
* Dissemination of this information or reproduction of this material is
* strictly forbidden unless prior written permission is obtained
* from Kollective.
*
* Detailed license and patent information: http://kollective.com/licenses/
**************************************************************************/

package com.kontiki.saml.idp.xml;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class XMLUtils 
{
	public Document loadXML(String xml) throws Exception 
	{
		if (xml.contains("<!ENTITY")) {
			throw new Exception(
					"Detected use of ENTITY in XML, disabled to prevent XXE/XEE attacks");
		}

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);

		// Add various options explicitly to prevent XXE attacks. add try/catch around every
		// setAttribute just in case a specific parser does not support it.
		try {
			factory.setAttribute("http://xml.org/sax/features/external-general-entities",
					Boolean.FALSE);
		} catch (Throwable t) {  /* OK.  Not all parsers will support this attribute */}
		try {
			factory.setAttribute("http://xml.org/sax/features/external-parameter-entities",
					Boolean.FALSE);
		} catch (Throwable t) {  /* OK.  Not all parsers will support this attribute */}
		try {
			factory.setAttribute("http://apache.org/xml/features/disallow-doctype-decl",
					Boolean.TRUE);
		} catch (Throwable t) {  /* OK.  Not all parsers will support this attribute */}
		try {
			factory.setAttribute("http://javax.xml.XMLConstants/feature/secure-processing",
					Boolean.TRUE);
		} catch (Throwable t) {  /* OK.  Not all parsers will support this attribute */ }
		try {
			factory.setAttribute("http://apache.org/xml/features/nonvalidating/load-external-dtd",
					Boolean.FALSE);
		} catch (Throwable t) {  /* OK.  Not all parsers will support this attribute */}
		try {
			factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
		} catch (Throwable t) {  /* OK.  Not all parsers will support this attribute */}

		DocumentBuilder builder;

		try {
			builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new InputSource(new StringReader(xml)));
			// Loop through the doc and tag every element with an ID attribute as an XML ID node.
			XPath xpath = XPathFactory.newInstance().newXPath();
			XPathExpression expr = xpath.compile("//*[@ID]");
			NodeList nodeList = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
			for (int i=0; i<nodeList.getLength() ; i++) {
				Element elem = (Element) nodeList.item(i);
				Attr attr = (Attr) elem.getAttributes().getNamedItem("ID");
				elem.setIdAttributeNode(attr, true);
			}
			return doc;
		} catch (Exception e) {
//			log.error("Error executing loadXML: " + e.getMessage(), e);
		}
		return null;
	}

	public NodeList query(Document dom, String query, Node context) throws XPathExpressionException 
	{
		NodeList nodeList;

		XPath xpath = XPathFactory.newInstance().newXPath();
		xpath.setNamespaceContext(new NamespaceContext() {

			public String getNamespaceURI(String prefix) {
				String result = null;
				if (prefix.equals("samlp") || prefix.equals("samlp2"))
					result = Constants.NS_SAMLP;
				else if (prefix.equals("saml") || prefix.equals("saml2"))
					result = Constants.NS_SAML;
				else if (prefix.equals("ds"))
					result = Constants.NS_DS;
				else if (prefix.equals("xenc"))
					result = Constants.NS_XENC;
				return result;
			}

			public String getPrefix(String namespaceURI) {
				return null;
			}

			@SuppressWarnings("rawtypes")
			public Iterator getPrefixes(String namespaceURI) {
				return null;
			}
		});

		if (context == null)
			nodeList = (NodeList) xpath.evaluate(query, dom, XPathConstants.NODESET);
		else
			nodeList = (NodeList) xpath.evaluate(query, context, XPathConstants.NODESET);
		return nodeList;
	}

	public String getAttributeFromXPath(Document document, String xPathOfNode, String attribute) throws XPathExpressionException
	{
		NodeList entries = this.queryNode(document, xPathOfNode);
		if (entries.getLength() > 0) {
			String attr = entries.item(0).getAttributes().getNamedItem(attribute).getNodeValue();
			return attr;
		}
		return null;
	}

	public String getContentFromXPath(Document document, String xPathOfNode) throws XPathExpressionException
	{
		NodeList entries = this.queryNode(document, xPathOfNode);
		if (entries != null && entries.getLength() > 0) {
			String attr = entries.item(0).getTextContent();
			return attr;
		}
		return null;
	}

	protected NodeList queryNode(Document document, String assertionXpath) throws XPathExpressionException
	{

		String nameQuery = "";
		String signatureQuery = "/samlp:Response/saml:Assertion/ds:Signature/ds:SignedInfo/ds:Reference";
		NodeList nodeList = this.query(document, assertionXpath, null);
		if(nodeList != null && nodeList.getLength() > 0){
			return nodeList;
		}
		return null;
	}

	public String convertXMLToString(boolean prettyPrint, Node node) throws TransformerException 
	{
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		if (prettyPrint) {
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		} else {
			transformer.setOutputProperty(OutputKeys.INDENT, "no");
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		}
		StringWriter writer = new StringWriter();
		transformer.transform(new DOMSource(node), new StreamResult(writer));
		String output = writer.getBuffer().toString();
		try {
			writer.close();
		}
		catch (Exception e) {
			;
		}
		return output;
	}
	
	public String convertXMLToString(Node node) throws TransformerException 
	{
		return convertXMLToString(false, node);
	}
	
}
