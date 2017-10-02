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

package com.kontiki.saml.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.kontiki.saml.audit.AuditEvent;
import com.kontiki.saml.audit.AuditMonitor;
import com.kontiki.saml.crypto.CryptTools;
import com.kontiki.saml.dao.AclManagementDAO;
import com.kontiki.saml.dao.SAMLConfigDAO;
import com.kontiki.saml.dao.data.AclGroupBasic;
import com.kontiki.saml.dao.data.AclUserBasic;
import com.kontiki.saml.dao.data.AuthRealmTicket;
import com.kontiki.saml.dao.data.SAMLConfig;

@Controller
public class SAMLConsumer {

	private final Logger logger = LoggerFactory.getLogger(SAMLConsumer.class);

	private static final String VALET_KEY_PASS = "vk";
	private static final String REALM_TICKET_PASS = "rt";
	private static final String AGENT_PASS = "agent";
	private static final String PARAM_VALET_KEY = "valetKey";

	private static final String TEST_USER_PARAMETER = "tu";
	private static final String TEST_GROUPS_PARAMETER = "tg";
	private static final String TEST_UFN_PARAMETER = "ufn";
	private static final String TEST_ULN_PARAMETER = "uln";
	private static final String TEST_UMAIL_PARAMETER = "um";
	private static final String TEST_UDIN_PARAMETER = "udn";

	public final static String SHIBBOLETH_HANDLER_URL = "sso";

	public static String SAML_GROUP_SEPARATOR_FOR_SP = ";";

	@Autowired
	private SAMLConfigDAO samlConfigDAO;

	@Autowired
	private AclManagementDAO aclManagementDAO;

	@Autowired
	private CryptTools cryptTools;
	
	@Autowired
	private AuditMonitor auditMonitor;
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String index() {
		logger.debug("index() is executed!");
		return "index";
	}
	

	@RequestMapping(value = "/consumer", method = RequestMethod.GET)
	public ModelAndView consumeSAML(HttpServletRequest request,
			@RequestParam(required = true, value = "r") String realmMoid,
			@RequestParam(required = false, value = "f") String format,
			@RequestParam(required = false, value = "u") String urn,
			@RequestParam(required = false, value = "g") String gotoUrl) throws Exception {

		ModelAndView model = new ModelAndView();
		model.setViewName("index");
		AuditEvent samlLoginAttemptResult = AuditEvent.SAML_ERROR;
		String sessionLog = cryptTools.generateSessionForLog();

		SAMLConfig sconfig = samlConfigDAO.loadSAMLConfigForRealm(sessionLog, realmMoid);

		logger_info(sessionLog, "format: " +  format);
		logger_info(sessionLog, "realm moid: " + realmMoid);
		String samlUser = getAttributeFromAssertion(request, TEST_USER_PARAMETER, sconfig.getSamlAttributeForUser());
		AuthRealmTicket realmTicket = null;
		String errorMessage = null;
		logger_info(sessionLog, "SAML user: " + samlUser);
		if (StringUtils.isEmpty(samlUser)) {
			errorMessage = "Got an empty user from SAML; Mandatory attribute " + sconfig.getSamlAttributeForUser()	+ " absent or empty. Ignoring assertion!";
			logger_error(sessionLog, errorMessage);
			model.addObject("errorMessage", errorMessage);
			model.setViewName("error");
		} else {
			String delimitedGroups = getAttributeFromAssertion(request, TEST_GROUPS_PARAMETER,
					sconfig.getSamlAttributeForGroups());
			String userDisplayName = null;
			String userFirstName = null;
			String userLastName = null;
			String userEmail = null;
			List<String> samlGroupNames = null;
			if (!StringUtils.isEmpty(delimitedGroups) || !StringUtils.isEmpty(sconfig.getSamlAttributeForGroups())) {
				logger_info(sessionLog, "SAML groups: " + delimitedGroups);
				samlGroupNames = new ArrayList<String>();
				String[] xmlGroups = delimitedGroups.split(SAML_GROUP_SEPARATOR_FOR_SP);
				String delimiter = sconfig.getIdpDelimiterForGroups();
				for (String xmlGroup : xmlGroups) {
					if (!StringUtils.isEmpty(delimiter)) {
						for (String s : xmlGroup.split(delimiter)) {
							samlGroupNames.add(s);
						}
					} else {
						samlGroupNames.add(xmlGroup);
					}
				}
				List<String> samlUsers = new ArrayList<String>();
				samlUsers.add(samlUser);

				AclUserBasic aclUserBasic = aclManagementDAO.findUserByName(samlUser, sconfig.getCompanyId());
				if (aclUserBasic == null) {
					aclUserBasic = new AclUserBasic();
					aclUserBasic.setCreated(new Date());
				}
				userDisplayName = getAttributeFromAssertion(request, TEST_UDIN_PARAMETER,
						sconfig.getSamlAttributeForUserDisplayName());
				userFirstName = getAttributeFromAssertion(request, TEST_UFN_PARAMETER,
						sconfig.getSamlAttributeForUserFirstName());
				userLastName = getAttributeFromAssertion(request, TEST_ULN_PARAMETER,
						sconfig.getSamlAttributeForUserLastName());
				userEmail = getAttributeFromAssertion(request, TEST_UMAIL_PARAMETER,
						sconfig.getSamlAttributeForUserEmail());
				fixUserForSAML(aclUserBasic, userDisplayName, userFirstName, userLastName, userEmail);
				aclManagementDAO.saveUser(aclUserBasic, sessionLog);

				List<Long> samlGroupsIds = new ArrayList<Long>();
				for (String samlGroupName : samlGroupNames) {
					AclGroupBasic aclGroupBasic = aclManagementDAO.findGroupByName(samlGroupName,
							sconfig.getCompanyId());
					if (aclGroupBasic == null) {
						aclGroupBasic = new AclGroupBasic();
						aclGroupBasic.setCreated(new Date());
					}
					fixGroupForSAML(aclGroupBasic, samlGroupName);
					aclManagementDAO.saveGroup(aclGroupBasic, sessionLog);
					samlGroupsIds.add(aclGroupBasic.getId());
				}
				aclManagementDAO.updateUserMembership(aclUserBasic, samlGroupsIds, sconfig.getCompanyId(), sessionLog);
			}

			realmTicket = samlConfigDAO.findRealmTicketForUser(samlUser, sconfig);
			if (!StringUtils.isEmpty(format)) {
				if (format.equalsIgnoreCase(VALET_KEY_PASS)) {
					String valetKey = samlConfigDAO.prepareValetKey(realmTicket);
					StringBuilder url = new StringBuilder(gotoUrl);
					int i = url.indexOf(PARAM_VALET_KEY);
					if (i >= 0) {
						int ei = url.indexOf("&", i);
						if (ei > 0) {
							String es = url.substring(ei + 1);
							url.setLength(i);
							url.append(es);
						} else {
							url.setLength(i);
						}
					}
					if (url.charAt(url.length() - 1) == '/' || url.charAt(url.length() - 1) == '?'
							|| url.charAt(url.length() - 1) == '&') {
						url.setLength(url.length() - 1);
					}
					url.append(url.indexOf("?") >= 0 ? "&" : "?").append(PARAM_VALET_KEY).append("=").append(valetKey);
					model.setViewName("redirect:" + url.toString());
					samlLoginAttemptResult = AuditEvent.SAML_SUCCESS;
				} else if (format.equalsIgnoreCase(AGENT_PASS)) {
					model.setViewName("agent");
					model.addObject("urn", urn);
					model.addObject("realm", realmMoid);
					model.addObject("realmTicket", realmTicket.getTicket());
					samlLoginAttemptResult = AuditEvent.SAML_SUCCESS;
				} else if (format.equalsIgnoreCase(REALM_TICKET_PASS)) {
					StringBuilder url = new StringBuilder(gotoUrl);
					url.append(url.indexOf("?") >= 0 ? "&" : "?").append(REALM_TICKET_PASS).append("=").append(realmTicket.getTicket());
					model.setViewName("redirect:" + url.toString());
					samlLoginAttemptResult = AuditEvent.SAML_SUCCESS;
				}
			}
			logger_info(sessionLog, "view: " + model.getViewName());
		}
		auditMonitor.reportSamlLoginAttempt(samlLoginAttemptResult, sessionLog , sconfig.getCompanyKid(), model.getViewName(), samlUser, realmTicket != null ? realmTicket.getTicket() :  null, errorMessage);
		return model;
	}

	private void logger_info(String sessionId, String msg) {
		logger.info(sessionId + "|" + msg);
	}
	
	private void logger_error(String sessionId, String msg) {
		logger.error(sessionId + "|" + msg);
	}


	private String getAttributeFromAssertion(HttpServletRequest request, String testAttributeName,
			String attributeName) {
		String attribute = request.getParameter(testAttributeName);
		if (StringUtils.isEmpty(attribute)) {
			attribute = attributeName;
		}
		if (!StringUtils.isEmpty(attribute)) {
			Object obj = request.getAttribute(attribute);
			if (obj != null) {
				return obj.toString();
			}
		}
		return null;
	}

	private void fixUserForSAML(AclUserBasic aclUserBasic, String displayName, String firstName, String lastName,
			String email) {
		aclUserBasic.setDisplayName(StringUtils.isEmpty(displayName) ? aclUserBasic.getUserName() : displayName);
		aclUserBasic.setFirstName(StringUtils.isEmpty(firstName) ? aclUserBasic.getUserName() : firstName);
		aclUserBasic.setLastName(StringUtils.isEmpty(lastName) ? "" : lastName);
		aclUserBasic.setEmail(StringUtils.isEmpty(email) ? "" : email);
		aclUserBasic.setLastUpdated(new Date());
		aclUserBasic.setMissing(false);
	}

	private void fixGroupForSAML(AclGroupBasic aclGroupBasic, String displayName) throws Exception {
		aclGroupBasic.setActive(true);
		aclGroupBasic.setLastUpdated(new Date());
		aclGroupBasic.setVirtual(false);
	}
}
