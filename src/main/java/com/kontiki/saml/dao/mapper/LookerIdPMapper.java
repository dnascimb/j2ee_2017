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

package com.kontiki.saml.dao.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import com.kontiki.saml.dao.data.AclUserBasic;
import com.kontiki.saml.dao.data.Analytics;
import com.kontiki.saml.dao.data.ClientToken;
import com.kontiki.saml.dao.data.Company;
import com.kontiki.saml.dao.data.Portal;

public interface LookerIdPMapper {
	final String getClientTokenByTokenKey = "SELECT id, token_key, company_id, username FROM client_token WHERE token_key =  #{clientToken}";

	@Select(getClientTokenByTokenKey)
	@Results(value = { @Result(property = "id", column = "id"), @Result(property = "tokenKey", column = "token_key"),
			@Result(property = "companyId", column = "company_id"),
			@Result(property = "username", column = "username") })
	ClientToken getClientTokenByTokenKey(@Param("clientToken") String clientToken);

	final String getClientTokenFromSessionId = "SELECT id, token_key, company_id, username FROM client_token WHERE web_session_id = #{sessionId} AND expirationTime > NOW order by expirationTime desc";

	@Select(getClientTokenFromSessionId)
	@Results(value = { @Result(property = "id", column = "id"), @Result(property = "tokenKey", column = "token_key"),
			@Result(property = "companyId", column = "company_id"),
			@Result(property = "username", column = "username") })
	ClientToken getClientTokenFromSessionId(@Param("sessionId") String sessionId);

	final String getCompanyByPrimaryKey = "SELECT COMPANY_ID, NAME, KID_PREFIX FROM COMPANY WHERE COMPANY_ID = #{companyId}";

	@Select(getCompanyByPrimaryKey)
	@Results(value = { @Result(property = "companyId", column = "COMPANY_ID"),
			@Result(property = "name", column = "NAME"), @Result(property = "kidPrefix", column = "KID_PREFIX") })
	Company getCompanyByPrimaryKey(@Param("companyId") Integer companyId);

	final String getAclUserBasicByUserNameAndCompanyId = "SELECT ID, COMPANY_ID, USERNAME, DISPLAY_NAME, FIRST_NAME, LAST_NAME, EMAIL FROM ACL_USER_BASIC WHERE USERNAME = #{username} AND COMPANY_ID = #{companyId}";

	@Select(getAclUserBasicByUserNameAndCompanyId)
	@Results(value = { @Result(property = "id", column = "ID"), @Result(property = "companyId", column = "COMPANY_ID"),
			@Result(property = "userName", column = "USERNAME"),
			@Result(property = "displayName", column = "DISPLAY_NAME"),
			@Result(property = "firstName", column = "FIRST_NAME"),
			@Result(property = "lastName", column = "LAST_NAME"), @Result(property = "email", column = "EMAIL") })
	AclUserBasic getAclUserBasicByUserNameAndCompanyId(@Param("username") String username,
			@Param("companyId") Integer companyId);

	final String getAnalyticsPermissionsByAclUser = "SELECT count(id) FROM acl_permissions_map WHERE principal_id = #{user_id} AND company_id = #{company_id} AND "
			+ "principal_type = 'user' AND (role = 1 OR role = 14);";

	@Select(getAnalyticsPermissionsByAclUser)
	// @Results(value = { @Result(property = "id", column = "id"),
	// @Result(property = "companyId", column = "company_id") })
	Integer getAnalyticsPermissionsByAclUser(@Param("user_id") Long userId, @Param("company_id") Long companyId);

	final String getAnalyticsByCompanyId = "SELECT id, company_id, analytics_url FROM analytics WHERE COMPANY_ID = #{companyId}";

	@Select(getAnalyticsByCompanyId)
	@Results(value = { @Result(property = "id", column = "id"), @Result(property = "companyId", column = "company_id"),
			@Result(property = "analyticsURL", column = "analytics_url") })
	Analytics getAnalyticsByCompanyId(@Param("companyId") Integer companyId);

	final String getFirstPortalByCompanyId = "SELECT id, company_id FROM portal WHERE company_id = #{companyId} LIMIT 1";

	@Select(getFirstPortalByCompanyId)
	@Results(value = { @Result(property = "id", column = "id"), @Result(property = "companyId", column = "company_id"),
			@Result(property = "internalName", column = "internal_name"),
			@Result(property = "displayName", column = "display_name"),
			@Result(property = "portalURL", column = "portal_url") })
	Portal getFirstPortalByCompanyId(@Param("companyId") Integer companyId);

	final String getServerPolicyDefDefaultValueByName = "SELECT DEFAULT_VALUE FROM SERVER_POLICY_DEF WHERE NAME = #{name}";

	@Select(getServerPolicyDefDefaultValueByName)
	String getServerPolicyDefDefaultValueByName(@Param("name") String name);

}
