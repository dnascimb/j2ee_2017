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

import java.util.Date;

public class AclGroupBasic {
	private Long id;
	private Long companyId;
	private String groupName;
	private Boolean virtual = false;
	private Date lastUpdated;
	private Boolean active;
	private Date created;

	public Long getId() {
		return id;
	}

	public void setId(Long value) {
		id = value;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String name) {
		this.groupName = name;
	}

	public Date getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Boolean getVirtual() {
		return virtual;
	}

	public void setVirtual(Boolean virtual) {
		this.virtual = virtual;
	}

	@Override
	public String toString() {
		return "AclGroupBasic [id=" + id + ", companyId=" + companyId + ", groupName=" + groupName + ", virtual="
				+ virtual + ", lastUpdated=" + lastUpdated + ", active=" + active + "]";
	}
}
