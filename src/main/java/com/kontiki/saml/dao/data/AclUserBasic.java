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

import org.springframework.util.StringUtils;

public class AclUserBasic {

	private Long id;
	private Long companyId;
	private String userName;
	private String displayName;
	private Date created;
	private Date lastUpdated;
	private String firstName;
	private String lastName;
	private String email;
	private boolean missing;

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

	public String getUserName() {
		return userName;
	}

	public void setUserName(String name) {
		this.userName = name;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public Date getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public boolean isMissing() {
		return missing;
	}

	public void setMissing(boolean missing) {
		this.missing = missing;
	}

	@Override
	public String toString() {
		return "AclUserBasic [id=" + id + ", companyId=" + companyId + ", userName=" + userName + ", displayName="
				+ displayName + ", created=" + created + ", lastUpdated=" + lastUpdated + ", firstName=" + firstName
				+ ", lastName=" + lastName + ", email=" + email + ", missing=" + missing + "]";
	}
	
	
}
