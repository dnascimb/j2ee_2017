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
package com.kontiki.saml.crypto;

import java.io.FileReader;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.X509Certificate;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMReader;
import org.springframework.stereotype.Component;

public class IdPKeyPair {
	
	private PrivateKey privateKey;
	private X509Certificate cert;

	public IdPKeyPair() {

	}

	private Object getObjectInPEM(String file) throws Exception {
		FileReader fr = null;
		Object object = null;
		PEMReader reader = null;
		try {
			fr = new FileReader(file);
			reader = new PEMReader(fr);
			object = reader.readObject();
			return object;
		} catch (Exception e) {
			throw e;
		} finally {
			if (fr != null) {
				try {
					fr.close();
				} catch (Exception ee) {
					;
				}
			}
			if (reader != null) {
				reader.close();
			}
		}
	}

	private void initPemPrivateKey(String pemFile, String algorithm) throws Exception {
		Object object = getObjectInPEM(pemFile);
		KeyPair kp = null;
		if (object instanceof KeyPair) {
			kp = (KeyPair) object;
		}
		this.privateKey = kp.getPrivate();
	}

	private void initCertFromFile(String certFile) throws Exception {
		X509Certificate cert = null;
		Object object = getObjectInPEM(certFile);
		if (object instanceof X509Certificate) {
			cert = (X509Certificate) object;
		}
		this.cert = cert;
	}

	public void init(String certFile, String privateKeyFile) throws Exception {
		Security.addProvider(new BouncyCastleProvider());
		initCertFromFile(certFile);
		initPemPrivateKey(privateKeyFile, "RSA");
	}

	public PrivateKey getPrivateKey() {
		return privateKey;
	}

	public X509Certificate getCert() {
		return cert;
	}

}