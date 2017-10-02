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

package com.kontiki.saml.dao;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kontiki.saml.audit.AuditMonitor;
import com.kontiki.saml.crypto.CryptTools;
import com.kontiki.saml.dao.data.AccessRealm;
import com.kontiki.saml.dao.data.AuthRealmTicket;
import com.kontiki.saml.dao.data.ConfigParam;
import com.kontiki.saml.dao.data.SAMLConfig;
import com.kontiki.saml.dao.mapper.SAMLConfigMapper;
import com.kontiki.saml.exceptions.ResourceNotFoundException;

@Service
public class SAMLConfigDAO extends CustomDAOSupport {

	private static final Logger logger = LoggerFactory.getLogger(SAMLConfigDAO.class);

	private static final int TIMEOUT_VALET_KEY_SECONDS = 5 * 60; // 5 min
	
	private static final String PARAM_REALM_TICKET_RANDOM_PERCENT = "realmTicketRandomPercent";
	private static final String PARAM_REALM_TICKET_VERSION = "RealmTicketVersion";

	@Autowired
	private CryptTools cryptTools;

	@Autowired
	private AuditMonitor auditMonitor;
	

	public SAMLConfig loadSAMLConfigForRealm(String sessionId, String realmMoid) {
		SAMLConfigMapper scmap = this.getSqlSession().getMapper(SAMLConfigMapper.class);

		AccessRealm realm = scmap.getAccessRealmByMoid(realmMoid);
		SAMLConfig result = null;

		if (realm != null) {
			result = new SAMLConfig();
			result.setRealmId(realm.getId());
			result.setCompanyId(realm.getCompanyId());
			result.setCompanyKid(realm.getKidPrefix());
			List<ConfigParam> configParams = scmap.getConfigParams(realm.getLoginConfigId());
			if (configParams != null) {
				for (ConfigParam configParam : configParams) {
					result.readConfigParam(configParam, cryptTools, logger);
				}
			}
			try {
				result.setRealmTicketRandomPercent(Integer.parseInt(scmap.getServerPolicyValue(PARAM_REALM_TICKET_RANDOM_PERCENT)));
			} catch (Exception e) {
				logger.error(sessionId +"|Invalid configuration for " + PARAM_REALM_TICKET_RANDOM_PERCENT + " :" + e.getMessage(), e);
			}			
			try {
				result.setRealmTicketVersion(Integer.parseInt(scmap.getServerPolicyValue(PARAM_REALM_TICKET_VERSION)));
			} catch (Exception e) {
				logger.error(sessionId +"|Invalid configuration for " + PARAM_REALM_TICKET_VERSION + " :" + e.getMessage(), e);
			}			
			return result;
		} else {
			logger.error(sessionId +"|Realm not found: " + realmMoid);
			auditMonitor.reportMissingRealm(sessionId, realmMoid);
			throw new ResourceNotFoundException("Realm not found: " + realmMoid);
		}
	}

	public AuthRealmTicket findRealmTicketForUser(String userName, SAMLConfig sconfig) {
		SAMLConfigMapper scmap = this.getSqlSession().getMapper(SAMLConfigMapper.class);

		AuthRealmTicket realmTicket = scmap.findRealmTicketForUser(userName, sconfig.getCompanyId(), sconfig.getRealmId());
		Date now = new Date();
		if (realmTicket != null) {
			Date expTime = realmTicket.getExpiry();
			if (expTime == null || now.after(expTime)) {
				scmap.removeRealmTicket(realmTicket.getId());
				realmTicket =  null;
			}			
		}
		if (realmTicket == null) {
			realmTicket = new AuthRealmTicket();
			realmTicket.setTicket(cryptTools.generateUUID());
			realmTicket.setCompanyId(sconfig.getCompanyId());
			realmTicket.setRealmId(sconfig.getRealmId());
			realmTicket.setCreated(now);
			realmTicket.setUserName(userName);
			realmTicket.setVersion(Long.valueOf(sconfig.getRealmTicketVersion()));
	        realmTicket.setExpiry(new Date(System.currentTimeMillis() + randomizeTimeoutSeconds(sconfig) * 1000));
			scmap.addRealmTicket(realmTicket);
		}
		return realmTicket;
	}
	
	public long randomizeTimeoutSeconds(SAMLConfig sconfig) {
		long seconds = sconfig.getTicketTimeout();
		long realmTicketRandomPercent = sconfig.getRealmTicketRandomPercent();
		return (seconds * (100 - realmTicketRandomPercent) / 100) + (int) (seconds * realmTicketRandomPercent / 100 * cryptTools.getRandomDouble());
	}
	
	public String prepareValetKey(AuthRealmTicket ticket) {
		SAMLConfigMapper scmap = this.getSqlSession().getMapper(SAMLConfigMapper.class);
		String valetKey = cryptTools.generateValetKey();
		String tokenKey = cryptTools.generateSessionId();
		Date expirationtime = new Date(System.currentTimeMillis() + ( TIMEOUT_VALET_KEY_SECONDS * 1000));
		scmap.addClientToken(ticket.getCompanyId(), expirationtime, ticket.getTicket(), tokenKey, ticket.getUserName(), valetKey);
		return valetKey;
	}
	

}
