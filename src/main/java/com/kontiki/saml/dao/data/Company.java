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
package com.kontiki.saml.dao.data;

public class Company {

	private Integer companyId;
	private String name;
	private String kidPrefix;
	
	public Integer getCompanyId() {
		return companyId;
	}
	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getKidPrefix() {
		return kidPrefix;
	}
	public void setKidPrefix(String kidPrefix) {
		this.kidPrefix = kidPrefix;
	}
	@Override
	public String toString() {
		return "Company [companyId=" + companyId + ", name=" + name + ", kidPrefix=" + kidPrefix + "]";
	}
	
	
}
