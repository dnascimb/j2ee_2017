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

import org.springframework.stereotype.Service;

import com.kontiki.saml.dao.data.AclUserBasic;
import com.kontiki.saml.dao.data.Analytics;
import com.kontiki.saml.dao.data.ClientToken;
import com.kontiki.saml.dao.data.Company;
import com.kontiki.saml.dao.data.Portal;
import com.kontiki.saml.dao.mapper.LookerIdPMapper;

@Service
public class LookerIdPDAO extends CustomDAOSupport {

	public static String EVERYONE_GROUP_NAME = "-Everyone-";
	
	public ClientToken getClientTokenByTokenKey(String key) {
		LookerIdPMapper map = this.getSqlSession().getMapper(LookerIdPMapper.class);
		ClientToken result = map.getClientTokenByTokenKey(key);
		return result;
	}
	
	public ClientToken getClientTokenFromSessionId(String key) {
		LookerIdPMapper map = this.getSqlSession().getMapper(LookerIdPMapper.class);
		ClientToken result = map.getClientTokenFromSessionId(key);
		return result;
	}

	public Company getCompanyByPrimaryKey(Integer key) {
		LookerIdPMapper map = this.getSqlSession().getMapper(LookerIdPMapper.class);
		Company result = map.getCompanyByPrimaryKey(key);
		return result;
	}
	
	public AclUserBasic getAclUserBasicByUserNameAndCompanyId(String username, Integer companyId) {
		LookerIdPMapper map = this.getSqlSession().getMapper(LookerIdPMapper.class);
		AclUserBasic result = map.getAclUserBasicByUserNameAndCompanyId(username, companyId);
		return result;
	}
	
	public Integer getAnalyticsPermissionsByAclUser(AclUserBasic user) {
		LookerIdPMapper map = this.getSqlSession().getMapper(LookerIdPMapper.class);
		Integer result = 0;
		if(user != null && user.getId() != 0 && user.getCompanyId() != 0)
			result = map.getAnalyticsPermissionsByAclUser(user.getId(), user.getCompanyId());
		return result;
	}
	
	public Analytics getAnalyticsByCompanyId(Integer key) {
		LookerIdPMapper map = this.getSqlSession().getMapper(LookerIdPMapper.class);
		Analytics result = map.getAnalyticsByCompanyId(key);
		return result;
	}

	public Portal getFirstPortalByCompanyId(Integer key) {
		LookerIdPMapper map = this.getSqlSession().getMapper(LookerIdPMapper.class);
		Portal result = map.getFirstPortalByCompanyId(key);
		return result;
	}
	
	public String getServerPolicyDefDefaultValueByName(String name) {
		LookerIdPMapper map = this.getSqlSession().getMapper(LookerIdPMapper.class);
		String result = map.getServerPolicyDefDefaultValueByName(name);
		return result;
	}
	
}

