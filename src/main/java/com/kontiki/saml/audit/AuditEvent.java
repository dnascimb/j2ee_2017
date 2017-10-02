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

package com.kontiki.saml.audit;

public enum AuditEvent {

	SAML_SUCCESS(new String[] {"user", "view", "rt"}, "SAML assertion has been verified and accepted"),
	SAML_ERROR(new String[] {"user", "view", "rt", "message"}, "SAML assertion has been denied"),

	SAML_REALM_NOT_FOUND(new String[] {"realm"}, "requested realm moid coul dnot be found in the system"),
	
	SAML_USER_ADDED(new String[] {"id", "name"}, "new saml user has been added to the system"),
	SAML_USER_UPDATED(new String[] {"id", "name"}, "saml user has been updated into the system"),
	SAML_GROUP_ADDED(new String[] {"id", "name"}, "new saml group has been added to the system"),
	SAML_GROUP_UPDATED(new String[] {"id", "name"}, "saml group has been updated into the system"),
	SAML_MEMBERSHIP_ADDED(new String[] {"userid", "groupid"}, "new user group membership has been added to the system"),
	SAML_MEMBERSHIP_REMOVED(new String[] {"userid", "groupid"}, "user group membership has been removed from the system"),
    ;
	
	
    private String[] paramemeters;
    private String description;

    private AuditEvent()  {
    }

    private AuditEvent(String[] paramemeters)  {
    	this(paramemeters, null);
    }
    
    private AuditEvent(String[] paramemeters, String description)  {
    	this.paramemeters = paramemeters;
    	this.description = description;
    }

	public String[] getParamemeters() {
		return paramemeters;
	}

	public String getDescription() {
		return description;
	}
    
    

	
}
