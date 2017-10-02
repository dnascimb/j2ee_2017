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

import java.util.HashMap;
import java.util.Map;

public class Utils 
{
	public static Map<String, String> qsToMap(String qs)
	{
		Map<String, String> qsMap = new HashMap<String, String>();
		String [] nvs = qs.split("&");
		for (String nv : nvs) {
			String [] nvp = nv.split("=");
			qsMap.put(nvp[0].toLowerCase(), nvp[1]);
		}
		return qsMap;
	}


}
