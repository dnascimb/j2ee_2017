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

import java.util.Random;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

public class CryptTools {
	private static final String ENCRYPTION_KEY_SOURCE_VALUE = "kontiki";
	private static final String PREFIX_CLIENT_AUTH_TOKEN = "cli-";
	private static final String PREFIX_VALET_KEY = "vk_";
	private static final String CHARSET = "UTF-8";

    private static final int SESSION_ID_LENGTH = 32;
    private static final int SESSION_LOG_LENGTH = 8;
    private static final String SESSION_ID_CHARS = "01234567890abcdefghijklmnopqrstuvwxyz"; // so that IDs can be non-case sensitive
	
    private static Random plainRandom = new Random(System.currentTimeMillis());
	

	public String encrypt(String plaintext) throws Exception {
		byte[] encryptionKey = DigestUtils.sha(ENCRYPTION_KEY_SOURCE_VALUE);
		byte[] plaintextBytes = plaintext.getBytes(CHARSET);
		byte[] encryptedBytes = xorByteArrayWithKey(plaintextBytes, encryptionKey);
		byte[] base64EncodedBytes = Base64.encodeBase64(encryptedBytes);
		return new String(base64EncodedBytes, CHARSET);
	}

	public String decrypt(String encryptedText) throws Exception {
		byte[] base64EncodedBytes = encryptedText.getBytes(CHARSET);
		byte[] encryptedBytes = Base64.decodeBase64(base64EncodedBytes);
		byte[] decryptionKey = DigestUtils.sha(ENCRYPTION_KEY_SOURCE_VALUE);
		byte[] decryptedBytes = xorByteArrayWithKey(encryptedBytes, decryptionKey);
		return new String(decryptedBytes, CHARSET);
	}

	private byte[] xorByteArrayWithKey(byte[] input, byte[] key) {
		byte[] output = new byte[input.length];
		// we just xor it in place; no need to create a new array.
		for (int i = 0; i < input.length; i++) {
			output[i] = (byte) (input[i] ^ key[i % key.length]);
		}
		return output;
	}
	
	public String base64decode(String encodedText) throws Exception {
		return new String(Base64.decodeBase64(encodedText.getBytes(CHARSET)), CHARSET);
	}

	public double getRandomDouble() {
		return plainRandom.nextDouble();
	}
	
	public String generateUUID() {
		return UUID.randomUUID().toString();
	}

	public String generateClientAuthToken() {
		return  PREFIX_CLIENT_AUTH_TOKEN + UUID.randomUUID().toString();
	}

    private String generateRandomKey(int length)
    {
        // for security reasons, session IDs should not be predictable. So we need to use a secure random here.
        byte[] randomBytes = new byte[length];
        plainRandom.nextBytes(randomBytes); // fills in the vector
        StringBuilder outputBuilder = new StringBuilder();
        for(byte currentByte : randomBytes)
        {
            // take each byte and squash it into the space of our character string indexes
            int intValue = (int) currentByte;
            if(intValue < 0)
                intValue *= -1;
            intValue = intValue % SESSION_ID_CHARS.length();
            outputBuilder.append(SESSION_ID_CHARS.charAt(intValue));
        }
        return outputBuilder.toString();
    }

    public String generateSessionId()
    {
    	return generateRandomKey(SESSION_ID_LENGTH);
    }

    public String generateSessionForLog()
    {
    	return generateRandomKey(SESSION_LOG_LENGTH);
    }
    
    public String generateValetKey()
    {
        return PREFIX_VALET_KEY + generateSessionId();
    }
    
	
}
