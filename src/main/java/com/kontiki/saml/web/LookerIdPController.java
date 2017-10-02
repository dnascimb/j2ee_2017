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

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.kontiki.saml.crypto.CryptTools;
import com.kontiki.saml.crypto.IdPKeyPair;
import com.kontiki.saml.dao.LookerIdPDAO;
import com.kontiki.saml.dao.data.AclUserBasic;
import com.kontiki.saml.dao.data.ClientToken;
import com.kontiki.saml.dao.data.Company;
import com.kontiki.saml.idp.SSOHandler;
import com.kontiki.saml.idp.xml.Utils;

@Controller
@RequestMapping("/idp/sso")
public class LookerIdPController {

	private final Logger logger = LoggerFactory.getLogger(LookerIdPController.class);

	protected static final String STATUS = "status";
	protected static final String PRESSO = "presso";
	protected static final String AUTH = "auth";
	protected static final String POSTSSO = "postsso";
	protected static final String ITEM = "item";
	protected static final String RESPONSE = "samlResponse";
	protected static final String DESTINATION = "destination";
	private static final String COOKIE_NAME_CT = "idpct";
	private static final String COOKIE_NAME_CP = "idpcp";
	public static final String SAVED_REQUEST_DATA_PARAMETER = "srd";
	private static final String PIPE_ESCAPE_STRING = "__PIPE__";
	public static final String SESSION_ID_COOKIE_NAME = "sid";
	protected static final String IDENTITY_MANAGER_URL_PROPERTY = "kontikiIdentityManagerUrl";

	private static final boolean COOKIE_HTTP_ONLY = true;
	private static final int COOKIE_MAX_AGE = -1;
	private static final boolean COOKIE_SECURE = false;
	private static final String PRESSO_VAR = "_ct";

	@Autowired
	private IdPKeyPair idpKeyPair;
	@Autowired
	private CryptTools cryptTools;
	@Autowired
	private LookerIdPDAO lookerIdPDAO;


	private String getDefaultView() {
		return "idp/default";
	}

	private String getAuthView() {
		return "idp/auth";
	}

	private String getNoCompanyView() {
		return "idp/nocompany";
	}

	private String getNoPrivilegesView() {
		return "idp/norights";
	}

	
	/**
	 * This function returns a page that indicates the module is operational (status page)
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView defaultMethod(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> model = new HashMap<String, Object>();
		String view = getDefaultView();
		model.put(ITEM, STATUS);
		model.put(STATUS, "Operational");
		return new ModelAndView(view, model);
	}

	/**
	 * This function is responsible for persisting a cookie to the user's machine which will be 
	 * read later to determine attributes about the user (username/company).
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/presso", method = RequestMethod.GET)
	public ModelAndView handlePreSSO(HttpServletRequest request, HttpServletResponse response) throws Exception {
		logger.info("Processing " + PRESSO + " request");
		Map<String, String> qsMap = Utils.qsToMap(request.getQueryString());
		String clientToken = null;
		
		// look for the clientToken variable from the request
		if (qsMap != null) {
			for (Map.Entry<String, String> me : qsMap.entrySet()) {
				if (me.getKey().equalsIgnoreCase(PRESSO_VAR)) {
					clientToken = me.getValue();
					logger.debug("");
					break;
				}
			}
		}
		
		// construct a cookie with the ClientToken and add it to the response for storage on the user's machine
		if (!StringUtils.isEmpty(clientToken)) {
			Cookie cookie = new Cookie(COOKIE_NAME_CT, clientToken);
			cookie.setHttpOnly(COOKIE_HTTP_ONLY);
			cookie.setMaxAge(COOKIE_MAX_AGE);
			cookie.setSecure(COOKIE_SECURE);
			response.addCookie(cookie);

			ClientToken ctObject = lookerIdPDAO.getClientTokenByTokenKey(clientToken);
			if (ctObject != null) {
				Company company = lookerIdPDAO.getCompanyByPrimaryKey(ctObject.getCompanyId());
				if (company != null) {
					Cookie cookiecp = new Cookie(COOKIE_NAME_CP, company.getKidPrefix());
					cookiecp.setHttpOnly(COOKIE_HTTP_ONLY);
					cookiecp.setMaxAge(Integer.MAX_VALUE);
					cookiecp.setSecure(COOKIE_SECURE);
					response.addCookie(cookiecp);
				}
			}
		}

		String lookerURL = lookerIdPDAO.getServerPolicyDefDefaultValueByName("lookerURL");
		logger.debug("Redirecting to Looker URL");
		return new ModelAndView("redirect:" + lookerURL);
	}

	
	/**
	 * This function is responsible for handling authentication requests from Looker. It looks on the request 
	 * for a cookie containing ClientToken information or company information (for authentication via GLS). 
	 * If neither pieces of information are found, it will return a soft error page to the user. 
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/auth", method = RequestMethod.GET)
	public ModelAndView handleAuth(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> model = new HashMap<String, Object>();
		String view = "";

		logger.debug("Processing Looker " + AUTH + " request");
		view = getAuthView();
		SSOHandler ssoHandler = new SSOHandler();
		Cookie[] cookies = request.getCookies();
		AclUserBasic user = null;
		String companyPrefix = null;
		
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equalsIgnoreCase(COOKIE_NAME_CT)) {
					String clientToken = cookie.getValue();
					logger.debug("ClientToken from cookie: " + clientToken);
					if (!StringUtils.isBlank(clientToken)) {
						// get client token object from client token key
						ClientToken token = lookerIdPDAO.getClientTokenByTokenKey(clientToken);
						
						if (token != null) {
							logger.debug("ClientToken entry from database: " + token);
							user = lookerIdPDAO.getAclUserBasicByUserNameAndCompanyId(token.getUsername(),
									token.getCompanyId());
							logger.debug("User information obtained from ClientToken: " + user);
						}
						
						if (user != null && StringUtils.isBlank(user.getUserName()))
							user = null;

					}
				} else if (cookie.getName().equalsIgnoreCase(COOKIE_NAME_CP)) {
					companyPrefix = cookie.getValue();
					logger.debug("Company prefix obtained from cookie: " + companyPrefix);
				}
			}
		}
		
		if (user == null) {
			logger.debug("Unable to retrieve user information from cookie or no cookie present");
			
			if (!StringUtils.isBlank(companyPrefix)) {
				// no cookie - initiate login
				String pathInfo = request.getPathInfo();
				if (pathInfo == null) {
					logger.error("There should have been a request path, but there wasn't one. Redirecting to error view.");
					view = this.getNoCompanyView();
					return new ModelAndView(view, model);
				}
				
				String identityURL = lookerIdPDAO.getServerPolicyDefDefaultValueByName(IDENTITY_MANAGER_URL_PROPERTY) + "/identity/login/general/";
				
				StringBuilder srd = new StringBuilder();
				srd.append(request.getScheme()).append("://").append(request.getServerName());
				srd.append(':').append(request.getServerPort());
				srd.append(request.getServletPath()).append(pathInfo.replace(AUTH, POSTSSO));
				srd.append('?').append(request.getQueryString());
				
				String serialized = serialize(srd.toString(), srd.toString());
				
				StringBuilder idUrl = new StringBuilder();
				idUrl.append(identityURL).append(companyPrefix).append('?');
				idUrl.append(SAVED_REQUEST_DATA_PARAMETER).append('=').append(serialized);
				logger.debug("Redirecting to: " + idUrl);
				return new ModelAndView("redirect:" + idUrl);
			} else {
				logger.debug("No user or company information found via the cookies. Redirecting to error view.");
				view = this.getNoCompanyView();
				return new ModelAndView(view, model);
			}
		} else if (!hasPermission(user)) {
			logger.debug("User " + user.getEmail() + " does not have appropriate privileges. Redirecting to error view.");
			view = this.getNoPrivilegesView();
			return new ModelAndView(view, model);
		}

		logger.debug("User " + user.getEmail() + " has appropriate privileges. Initiating SSO process.");
		ssoHandler.handleSSO(idpKeyPair, ssoHandler, request.getQueryString(), user);
		model.put(RESPONSE, ssoHandler.getSAMLResponse());
		model.put(DESTINATION, ssoHandler.getDestination());
		model.put(ITEM, AUTH);

		return new ModelAndView(view, model);
	}

	/**
	 * This method is the endpoint for validating whether the user has analytics permissions after having been authenticated
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/postsso", method = RequestMethod.GET)
	public ModelAndView handlePostSSO(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> model = new HashMap<String, Object>();
		String view = "";

		logger.debug("Processing " + POSTSSO + " request");
		AclUserBasic user = getUserFromRequest(request);
		logger.debug("User in request: " + user);
		if (user != null) {
			if (!hasPermission(user)) {
				logger.debug("User " + user.getEmail() + " does not have appropriate privileges. Redirecting to error view.");
				view = this.getNoPrivilegesView();
				return new ModelAndView(view, model);
			} else {
				logger.debug("User " + user.getEmail() + " has appropriate privileges. Initiating SSO process.");
				view = getAuthView();
				SSOHandler ssoHandler = new SSOHandler();
				ssoHandler.handleSSO(idpKeyPair, ssoHandler, request.getQueryString(), user);
				model.put(RESPONSE, ssoHandler.getSAMLResponse());
				model.put(DESTINATION, ssoHandler.getDestination());
				model.put(ITEM, AUTH);
			}
		} else {
			logger.debug("User not found - rejecting request");
			response.sendError(HttpServletResponse.SC_FORBIDDEN);
		}

		return new ModelAndView(view, model);
	}

	/**
	 * This method is used to retrieve the user associated to the session
	 * identifier stored in a cookie on the HttpServletRequest object.
	 * 
	 * @param request
	 * @return AclUserBasic object representing a user
	 */
	private AclUserBasic getUserFromRequest(HttpServletRequest request) {
		AclUserBasic user = null;

		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (SESSION_ID_COOKIE_NAME.equalsIgnoreCase(cookie.getName())) {
					String sessionId = cookie.getValue();
					if (sessionId != null) {
						// get client token from sessionId
						ClientToken token = lookerIdPDAO.getClientTokenFromSessionId(sessionId);
						if (token != null) {
							// get user from client token
							user = lookerIdPDAO.getAclUserBasicByUserNameAndCompanyId(token.getUsername(),
									token.getCompanyId());
						}
					}
				}
			}
		}

		return user;
	}

	/**
	 * This method indicates whether a user has appropriate permissions for
	 * access to analytics.
	 * 
	 * @param user
	 * @return boolean indicating whether the user has appropriate permissions
	 *         for access to analytics
	 */
	private boolean hasPermission(AclUserBasic user) {
		Integer permissions = lookerIdPDAO.getAnalyticsPermissionsByAclUser(user);
		if (permissions > 0)
			return true;
		else
			return false;
	}

	public String serialize(String fullUrl, String uri) {
		try {
			StringBuilder dataBuilder = new StringBuilder();
			dataBuilder.append(escape(fullUrl) + "|");
			dataBuilder.append(escape(uri));
			return cryptTools.encrypt(dataBuilder.toString());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private String escape(String input) {
		return input.replaceAll("[|]", PIPE_ESCAPE_STRING);
	}

}
