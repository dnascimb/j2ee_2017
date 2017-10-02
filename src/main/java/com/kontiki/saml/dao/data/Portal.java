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

public class Portal {
	
	private Integer id;
	private String internalName;
	private String displayName;
	private Integer companyId;
	private String portalURL;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getInternalName() {
		return internalName;
	}
	public void setInternalName(String internalName) {
		this.internalName = internalName;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public Integer getCompanyId() {
		return companyId;
	}
	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}
	public String getPortalURL() {
		return portalURL;
	}
	public void setPortalURL(String portalURL) {
		this.portalURL = portalURL;
	}
	@Override
	public String toString() {
		return "Portal [id=" + id + ", internalName=" + internalName + ", displayName=" + displayName + ", companyId="
				+ companyId + ", portalURL=" + portalURL + "]";
	}

	
}
