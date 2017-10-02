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
import java.io.IOException;
import java.util.Map;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kontiki.saml.crypto.IdPKeyPair;
import com.kontiki.saml.dao.data.AclUserBasic;
import com.kontiki.saml.idp.xml.Utils;

public class SSOHandler
{
	private static final Logger logger = LoggerFactory.getLogger(SSOHandler.class);
	
	private String SAMLResponse;
	private String destination;
	
	private String processItForLooker(String samlResponse) throws IOException, DataFormatException
	{
		// compress it
        byte[] bytes = samlResponse.getBytes();
        Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION, true);
        deflater.setInput(bytes);
        deflater.finish();
        ByteArrayOutputStream bos = new ByteArrayOutputStream(bytes.length);
        byte[] buffer = new byte[1024];
        while(!deflater.finished()) {
        	int bytesCompressed = deflater.deflate(buffer);
        	bos.write(buffer,0,bytesCompressed);
        }
       bos.close();
       byte[] compressedSamlResponse = bos.toByteArray();
       
       // base64 it
       String deflatedBase64ed = Base64.encodeBase64String(compressedSamlResponse);
//       String deflatedBase64ed = Base64.encodeBase64String(samlResponse.getBytes());
       
       // url encode it
//       String deflateBase64Encoded = URLEncoder.encode(deflatedBase64ed, "UTF-8");
       String deflateBase64Encoded = deflatedBase64ed;
       // not needed?
              
       return deflateBase64Encoded;
	}
	
	private String getSSOResponse(IdPKeyPair idpKeyPair, String data, AclUserBasic user) throws Exception
	{
		AuthRequest authRequest = new AuthRequest(data);
		Response response = new Response();
		String responseString = response.getResponse(idpKeyPair, user, authRequest);
		this.destination = response.getDestination();
		return responseString;		
	}


	public void handleSSO(IdPKeyPair idpKeyPair, SSOHandler ssoHandler, String qs, 
		AclUserBasic user) throws Exception
	{
		logger.info("Handling SSO auth request");
		Map<String, String> qsMap = Utils.qsToMap(qs);
		
		String samlResponse = this.getSSOResponse(idpKeyPair, "SAMLRequest=" + qsMap.get("samlrequest"),
			user);
		logger.info(samlResponse);
		SAMLResponse = processItForLooker(samlResponse);
	}
	
	public String getDestination() {
		return destination;
	}
	public String getSAMLResponse() {
		return SAMLResponse;
	}

	
}
