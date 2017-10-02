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

package com.kontiki.saml.idp;

import java.io.ByteArrayOutputStream;
import java.net.URLDecoder;
import java.util.zip.Inflater;

import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.kontiki.saml.idp.xml.XMLUtils;

public class AuthRequest 
{
	private final Logger logger = LoggerFactory.getLogger(AuthRequest.class);

	private Document document;
	private String originator;
	private XMLUtils xmlUtils;
	
	public AuthRequest(String authRequest) throws Exception 
	{
		xmlUtils = new XMLUtils();
		String xmlStr = this.decodeSAMLRequest(authRequest);
		logger.info(xmlStr);
		this.document = xmlUtils.loadXML(xmlStr);
		
		if(document == null){
			throw new Exception("SAML Response could not be processed, invalid or empty SAML");
		}
		String xp = "/samlp:AuthnRequest/saml:Issuer";
		this.originator = xmlUtils.getContentFromXPath(this.document, xp);
		if (originator == null) {
			this.originator = xmlUtils.getAttributeFromXPath(this.document, "/samlp:AuthnRequest", "AssertionConsumerServiceURL");
		}
		if (originator == null) {
			originator = "Looker";
		}
	}

	private String decodeSAMLRequest(String encodedSAMLRequest)
			throws RuntimeException
	{
		Base64 base64Encoder = new Base64();
		try {
			encodedSAMLRequest = encodedSAMLRequest.substring("SAMLRequest=".length());
			encodedSAMLRequest = URLDecoder.decode(encodedSAMLRequest, "UTF-8");
			byte[] compressed = base64Encoder.decode(encodedSAMLRequest);
			
			Inflater inflater = new Inflater(true);
			ByteArrayOutputStream outputStream = null;
			if (inflater.needsInput()) {
				inflater.setInput(compressed);
				outputStream = new ByteArrayOutputStream(compressed.length);
				byte[] buffer = new byte[1024];
				while (!inflater.finished()) {
					int count = inflater.inflate(buffer); // returns the generated code... index  
				    outputStream.write(buffer, 0, count);
				}
			}
			inflater.end();
			outputStream.close();
			String outputString = outputStream.toString("UTF-8");
			return outputString;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}	

	public String getAttributeFromXPath(String xPathOfNode, String attribute) throws XPathExpressionException
	{
		return xmlUtils.getAttributeFromXPath(document, xPathOfNode, attribute);
	}

	public String getOriginator() {
		return originator;
	}
	
	
}