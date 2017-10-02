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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuditMonitor {

	private static final Logger logger = LoggerFactory.getLogger(AuditMonitor.class);
	private static final char SEPARATOR_CHAR = ' ';
	private static final char KV_CHAR = '=';
	private static final int VALUE_CHAR_LIMITS = 65000;

	private StringBuilder initAuditString(AuditEvent event, String sessionKey) {
		if (event != null && sessionKey != null) {
			StringBuilder result = new StringBuilder();
			result.append(event.name());
			result.append(SEPARATOR_CHAR);
			result.append(sessionKey);
			return result;
		}
		return null;
	}

	private void prepareAuditKVLong(StringBuilder sb, String key, Long value) {
		if (value != null) {
			prepareAuditKVString(sb, key, value.toString());
		}
	}
	
	private void prepareAuditKVString(StringBuilder sb, String key, String value) {
		if (sb != null && key != null && value != null) {
			sb.append(SEPARATOR_CHAR);
			sb.append(key);
			sb.append(KV_CHAR);
			if (value.length() > VALUE_CHAR_LIMITS) {
				value = value.substring(0, VALUE_CHAR_LIMITS);
			}
			sb.append(value.trim().replaceAll(" ", "_"));
		}
	}

	public void reportSamlLoginAttempt(AuditEvent event, String sessionId, String company, String viewName, String samlUser, String realmTicket, String message) {
		StringBuilder sb = initAuditString(event, sessionId);
		if (sb != null) {
			prepareAuditKVString(sb, "company", company);
			prepareAuditKVString(sb, "user", samlUser);
			prepareAuditKVString(sb, "view", viewName);
			prepareAuditKVString(sb, "rt", realmTicket);
			prepareAuditKVString(sb, "message", message);
			logger.info(sb.toString());
		}
	}

	public void reportMissingRealm(String sessionId, String realmMoid) {
		StringBuilder sb = initAuditString(AuditEvent.SAML_REALM_NOT_FOUND, sessionId);
		if (sb != null) {
			prepareAuditKVString(sb, "realm", realmMoid);
			logger.info(sb.toString());
		}
	}

	public void reportUserAdded(String sessionId, String user, Long id) {
		StringBuilder sb = initAuditString(AuditEvent.SAML_USER_ADDED, sessionId);
		if (sb != null) {
			prepareAuditKVLong(sb, "id", id);
			prepareAuditKVString(sb, "name", user);
			logger.info(sb.toString());
		}
	}
	
	public void reportUserUpdated(String sessionId, String user, Long id) {
		StringBuilder sb = initAuditString(AuditEvent.SAML_USER_UPDATED, sessionId);
		if (sb != null) {
			prepareAuditKVLong(sb, "id", id);
			prepareAuditKVString(sb, "name", user);
			logger.info(sb.toString());
		}
	}

	public void reportGroupAdded(String sessionId, String group, Long id) {
		StringBuilder sb = initAuditString(AuditEvent.SAML_GROUP_ADDED, sessionId);
		if (sb != null) {
			prepareAuditKVLong(sb, "id", id);
			prepareAuditKVString(sb, "name", group);
			logger.info(sb.toString());
		}
	}

	public void reportGroupUpdated(String sessionId, String group, Long id) {
		StringBuilder sb = initAuditString(AuditEvent.SAML_GROUP_UPDATED, sessionId);
		if (sb != null) {
			prepareAuditKVLong(sb, "id", id);
			prepareAuditKVString(sb, "name", group);
			logger.info(sb.toString());
		}
	}

	public void reportMembershipAdded(String sessionId, Long userid, Long groupid) {
		StringBuilder sb = initAuditString(AuditEvent.SAML_MEMBERSHIP_ADDED, sessionId);
		if (sb != null) {
			prepareAuditKVLong(sb, "userid", userid);
			prepareAuditKVLong(sb, "groupid", groupid);
			logger.info(sb.toString());
		}
	}
	
	public void reportMembershipRemoved(String sessionId, Long userid, Long groupid) {
		StringBuilder sb = initAuditString(AuditEvent.SAML_MEMBERSHIP_REMOVED, sessionId);
		if (sb != null) {
			prepareAuditKVLong(sb, "userid", userid);
			prepareAuditKVLong(sb, "groupid", groupid);
			logger.info(sb.toString());
		}
	}

	public static void main(String[] args) {
		StringBuilder sb = new StringBuilder("||Num||Type||Parameters||Description||\n");
		int i = 0;
		for (AuditEvent e : AuditEvent.values()) {
			i++;
			sb.append("|");
			sb.append(i);
			sb.append("|");
			sb.append(e.name());
			sb.append("|");
			String[] params = e.getParamemeters();
			if (params != null) {
				for (int j = 0; j < params.length; j++) {
					if (j > 0)
						sb.append("\n");
					sb.append("* ");
					sb.append(params[j]);
				}
			}
			sb.append("|");
			sb.append(e.getDescription());
			sb.append("|\n");
		}
		System.out.println(sb);

	}


}
