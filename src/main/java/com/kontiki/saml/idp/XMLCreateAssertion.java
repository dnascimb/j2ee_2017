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

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.kontiki.saml.crypto.IdPKeyPair;
import com.kontiki.saml.dao.LookerIdPDAO;
import com.kontiki.saml.dao.data.AclUserBasic;
import com.kontiki.saml.idp.xml.Constants;
import com.kontiki.saml.idp.xml.XMLUtils;

public class XMLCreateAssertion 
{
	private static final Logger logger = LoggerFactory.getLogger(XMLCreateAssertion.class);

	private static final long MINUTES_5 = 5L * 60L * 1000L;
	private static final long MINUTES_1 = 1L * 60L * 1000L;
	private static final long HOURS_4 = 4L * 60L * 60L * 1000L;
	
	private static final String ISSUER = "Kollective";

	private String destination;
	private Document document;
	private XMLUtils xmlUtils;
	
	public XMLCreateAssertion() throws ParserConfigurationException 
	{
		initDocument();
		xmlUtils = new XMLUtils();
	}	


	private void initDocument() throws ParserConfigurationException
	{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		DocumentBuilder builder = dbf.newDocumentBuilder();
		document = builder.newDocument();
	}
	

	private void setVersionAndIssueInstantAttributes(Element element, String issueInstant)
	{
		element.setAttribute("Version", "2.0");
		element.setAttribute("IssueInstant", issueInstant);
	}
	
	private Element createResponseNode(String responseID, String issueInstant, 
			String inResponseTo)
	{
		Element response = document.createElementNS(Constants.NS_SAMLP, "samlp:Response");
		response.setAttribute("xmlns:samlp", Constants.NS_SAMLP);
		response.setAttribute("xmlns:saml", Constants.NS_SAML);
		
		response.setAttribute("ID", responseID);
		setVersionAndIssueInstantAttributes(response, issueInstant);
		response.setAttribute("Destination", destination);
		response.setAttribute("InResponseTo", inResponseTo);
		
		this.document.appendChild(response);
		
		return response;
	}
	
	private void setIssuerNode(Element element, AuthRequest authRequestDocument)
	{
		Element issuer = document.createElementNS(Constants.NS_SAML, "saml:Issuer");
		Text issuerValue = document.createTextNode(ISSUER);
		issuer.appendChild(issuerValue);
		element.appendChild(issuer);
	}
	
	private void setStatusNode(Element element)
	{
		Element status = document.createElementNS(Constants.NS_SAMLP, "samlp:Status");
		Element statusCode = document.createElementNS(Constants.NS_SAMLP, "samlp:StatusCode");
		statusCode.setAttribute("Value", "urn:oasis:names:tc:SAML:2.0:status:Success");
		status.appendChild(statusCode);
		element.appendChild(status);
	}
	
	private Element createAssertionNode(Element element, String issueInstant, String assertionId)
	{
		Element assertion = document.createElementNS(Constants.NS_SAML, "saml:Assertion");
		assertion.setAttribute("xmlns:xsi", Constants.NS_XSI);
		assertion.setAttribute("xmlns:xs", Constants.NS_XS);
		setVersionAndIssueInstantAttributes(assertion, issueInstant);
		assertion.setAttribute("ID", assertionId);
		return assertion;
	}
	

	private void setSubject(Element element, AuthRequest authRequestDocument, String inResponseTo,
			String notOnOrAfter)
	{
		Element subject = document.createElementNS(Constants.NS_SAML,  "saml:Subject");

		Element nameID = document.createElementNS(Constants.NS_SAML, "saml:NameID");
		nameID.setAttribute("Format", "urn:oasis:names:tc:SAML:2.0:nameid-format:transient");
		nameID.setAttribute("SPNameQualifier", authRequestDocument.getOriginator());
		Text nameIDValue = document.createTextNode("Kollective");
		nameID.appendChild(nameIDValue);
		subject.appendChild(nameID);

		Element subjectConfirmation = document.createElementNS(Constants.NS_SAML, "saml:SubjectConfirmation");
		subjectConfirmation.setAttribute("Method", "urn:oasis:names:tc:SAML:2.0:cm:bearer");

		Element subjectConfirmationData = document.createElementNS(Constants.NS_SAML, "saml:SubjectConfirmationData");
		subjectConfirmationData.setAttribute("InResponseTo", inResponseTo);
		subjectConfirmationData.setAttribute("Recipient", destination);
		subjectConfirmationData.setAttribute("NotOnOrAfter", notOnOrAfter);
		subjectConfirmation.appendChild(subjectConfirmationData);
		
		subject.appendChild(nameID);
		subject.appendChild(subjectConfirmation);
		
		element.appendChild(subject);
	}
	
	private void setConditions(Element element, AuthRequest authRequestDocument, String dNotBefore,
			String dNotOnOrAfter)
	{
		Element conditions = document.createElementNS(Constants.NS_SAML, "saml:Conditions");

		conditions.setAttribute("NotBefore", dNotBefore);
		conditions.setAttribute("NotOnOrAfter", dNotOnOrAfter);
		
		Element audienceRestriction = document.createElementNS(Constants.NS_SAML, "saml:AudienceRestriction");
		Element audience = document.createElementNS(Constants.NS_SAML, "saml:Audience");
		Text audienceData = document.createTextNode(authRequestDocument.getOriginator());
		audience.appendChild(audienceData);
		audienceRestriction.appendChild(audience);
		conditions.appendChild(audienceRestriction);
		
		element.appendChild(conditions);
	}
	
	private void setAuthnStatement(Element element, String issueInstantDate, 
		String sessionEndDate, String sessionIndex)
	{
		Element authnStatement = document.createElementNS(Constants.NS_SAML, "saml:AuthnStatement");
		authnStatement.setAttribute("AuthnInstant", issueInstantDate);
		authnStatement.setAttribute("SessionNotOnOrAfter", sessionEndDate);
		authnStatement.setAttribute("SessionIndex", sessionIndex);

		Element authnContext = document.createElementNS(Constants.NS_SAML, "saml:AuthnContext");

		Element authnContextClassRef = document.createElementNS(Constants.NS_SAML, "saml:AuthnContextClassRef");
		Text authnContextClassRefData = document.createTextNode(
				"urn:oasis:names:tc:SAML:2.0:ac:classes:Password");
		authnContextClassRef.appendChild(authnContextClassRefData);

		authnContext.appendChild(authnContextClassRef);
		authnStatement.appendChild(authnContext);
		element.appendChild(authnStatement);

	}
	
	private void writeAssertionAttribute(Element element, String attributeName, List<String> values)
	{
		Element attribute = document.createElementNS(Constants.NS_SAML, "saml:Attribute");
		attribute.setAttribute("Name", attributeName);
		attribute.setAttribute("NameFormat", "urn:oasis:names:tc:SAML:2.0:attrname-format:uri");

		for (String av : values) {
			Element attributeValue = document.createElementNS(Constants.NS_SAML, "saml:AttributeValue");
			attributeValue.setAttribute("xsi:type", "xs:string");
			Text attibuteValueData = document.createTextNode(av);
			attributeValue.appendChild(attibuteValueData);
			attribute.appendChild(attributeValue);
		}
		element.appendChild(attribute);
	}
	
	private void writeAssertionAttribute(Element element, String attributeName, String attributeValue)
	{
		List<String> avl = new ArrayList<String>();
		avl.add(new String(attributeValue));
		writeAssertionAttribute(element, attributeName, avl);
	}

	private void setAssertion(Element element, AclUserBasic user)
	{
		Element attributeStatement = document.createElementNS(Constants.NS_SAML, "saml:AttributeStatement");
		logger.debug("Applying the following user object to the assertion: " + user);
		writeAssertionAttribute(attributeStatement, "urn:oid:2.5.4.42", user.getFirstName());
		writeAssertionAttribute(attributeStatement, "urn:oid:2.5.4.4", user.getLastName());
		writeAssertionAttribute(attributeStatement, "urn:oid:2.16.840.1.113730.3.1.241", user.getDisplayName());
		writeAssertionAttribute(attributeStatement, "urn:oid:0.9.2342.19200300.100.1.1", user.getUserName());
		writeAssertionAttribute(attributeStatement, "urn:oid:0.9.2342.19200300.100.1.3", user.getEmail());
		writeAssertionAttribute(attributeStatement, "companyId", Long.toString(user.getCompanyId()));
		writeAssertionAttribute(attributeStatement, "urn:oasis:names:tc:SAML:2.0:profiles:attribute:DCE:groups", LookerIdPDAO.EVERYONE_GROUP_NAME);
		element.appendChild(attributeStatement);
	}
	
	private final Element sign(String ID, Element container, DigestMethod digestMethod,
			String signatureMethod, X509Certificate certificate,
			PrivateKey privateKey) throws Exception {

		try {
			XMLSignatureFactory signatureFactory = XMLSignatureFactory.getInstance("DOM");

			List<Transform> transformList = new ArrayList<Transform>();
			transformList.add(signatureFactory.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null));
			transformList.add(signatureFactory.newTransform(CanonicalizationMethod.EXCLUSIVE, (TransformParameterSpec) null));

			Reference reference = signatureFactory.newReference("#" + container.getAttribute("ID"), digestMethod, transformList, null,
				null);

			CanonicalizationMethod canonicalizationMethod = signatureFactory.newCanonicalizationMethod(CanonicalizationMethod.EXCLUSIVE,
				(C14NMethodParameterSpec) null);

			SignedInfo signedInfo = signatureFactory.newSignedInfo(canonicalizationMethod, 
				signatureFactory.newSignatureMethod(signatureMethod, null),	Collections.singletonList(reference));

			KeyInfoFactory keyInfoFactory = signatureFactory.getKeyInfoFactory();

			X509Data x509Data = keyInfoFactory.newX509Data(Collections.singletonList(certificate));
			KeyInfo keyInfo = keyInfoFactory.newKeyInfo(Collections.singletonList(x509Data));

			DOMSignContext domSignContext = new DOMSignContext(privateKey, container);

			// See: https://github.com/difi/oxalis/issues/42
			container.setIdAttribute("ID", true);

			XMLSignature xmlSignature = signatureFactory.newXMLSignature(signedInfo, keyInfo);
			domSignContext.putNamespacePrefix("http://www.w3.org/2000/09/xmldsig#", "ds");
			xmlSignature.sign(domSignContext);
			placeSignatureAfterIssuer(container);

			return container;

		} catch (Exception e) {
			throw e;
		}
	}

    private void placeSignatureAfterIssuer(final Element container) throws DOMException 
    { 
        NodeList nodes = container.getChildNodes(); 
        List<Node> movingNodes = new ArrayList<Node>(); 
 
        for (int i = 1; i < nodes.getLength() - 1; i++) { 
            movingNodes.add(nodes.item(i)); 
        } 
 
        for (Node node : movingNodes) { 
            container.removeChild(node); 
        } 
 
        for (Node node : movingNodes) { 
            container.appendChild(node); 
        } 
    }
    
	public final Element sign(String ID, Element container, X509Certificate certificate,
			PrivateKey privateKey) throws Exception 
	{
		try {

			XMLSignatureFactory signatureFactory = XMLSignatureFactory.getInstance("DOM");
			DigestMethod digestMethod = signatureFactory.newDigestMethod(
					DigestMethod.SHA1, null);
			return sign(ID, container, digestMethod, SignatureMethod.RSA_SHA1,
					certificate, privateKey);

		} catch (Exception ex) {
			throw ex;
		}
	}

    public void create(Element itemElement, String elementID, X509Certificate certificate, PrivateKey privateKey) throws Exception
    {
    	sign(elementID, itemElement, certificate, privateKey);
    }

	private void doTheSign(IdPKeyPair idpKeyPair, String responseID, String assertionId) throws Exception
	{
		String xml = xmlUtils.convertXMLToString(false, document);
		document = xmlUtils.loadXML(xml);
		Element response = null;
		Element assertion = null;

		NodeList nl = document.getElementsByTagNameNS(Constants.NS_SAMLP, "Response");
		if (nl != null && nl.getLength() > 0) {
			response = (Element)nl.item(0);
		}
		
		nl = document.getElementsByTagNameNS(Constants.NS_SAML, "Assertion");
		if (nl != null && nl.getLength() > 0) {
			assertion = (Element)nl.item(0);
		}		
		this.create(assertion, assertionId, idpKeyPair.getCert(), idpKeyPair.getPrivateKey());
		response.appendChild(assertion);

		this.create(response, responseID, idpKeyPair.getCert(), idpKeyPair.getPrivateKey());
	}
	
	public void fillSAMLAssertion(IdPKeyPair idpKeyPair, AclUserBasic user, AuthRequest authRequestDocument) throws Exception
	{
		String responseID = "_" + UUID.randomUUID().toString();
		SimpleDateFormat simpleDf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		Date issueInstantDate = new Date(); 
		simpleDf.setTimeZone(TimeZone.getTimeZone("UTC"));
		String issueInstant = simpleDf.format(issueInstantDate);
		this.destination = authRequestDocument.getAttributeFromXPath("/samlp:AuthnRequest", "AssertionConsumerServiceURL");
		String inResponseTo = authRequestDocument.getAttributeFromXPath("/samlp:AuthnRequest","ID");
		Date dNotBefore = new Date(issueInstantDate.getTime() - MINUTES_1);
		Date dNotOnOrAfter = new Date(issueInstantDate.getTime() + MINUTES_5);

		// create the root element node
		Element response = createResponseNode(responseID, issueInstant, inResponseTo);
		
		setIssuerNode(response, authRequestDocument);
		
		setStatusNode(response);

		String assertionId ="_"+UUID.randomUUID().toString();
		Element assertion = createAssertionNode(response, issueInstant, assertionId);
		setIssuerNode(assertion, authRequestDocument);

		setSubject(assertion, authRequestDocument, inResponseTo, simpleDf.format(dNotOnOrAfter));

		setConditions(assertion, authRequestDocument, simpleDf.format(dNotBefore), 
				simpleDf.format(dNotOnOrAfter));
			
		Date sessionEndDate = new Date(issueInstantDate.getTime() + HOURS_4);
		String sessionIndex ="_"+UUID.randomUUID().toString();
		setAuthnStatement(assertion, simpleDf.format(issueInstantDate), 
				simpleDf.format(sessionEndDate), sessionIndex);

		String debugStr = xmlUtils.convertXMLToString(false, response);
		debugStr = xmlUtils.convertXMLToString(false, assertion);
		setAssertion(assertion, user);
		debugStr = xmlUtils.convertXMLToString(false, assertion);
		logger.info(debugStr);
		response.appendChild(assertion);

		doTheSign(idpKeyPair, responseID, assertionId);
	}
	
	public String getDestination() {
		return destination;
	}
	
	public String getSAMLResponseXML() throws TransformerException
	{
		return xmlUtils.convertXMLToString(false, document);
	}

}
