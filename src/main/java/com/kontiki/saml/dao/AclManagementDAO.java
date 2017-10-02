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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kontiki.saml.audit.AuditMonitor;
import com.kontiki.saml.dao.data.AclGroupBasic;
import com.kontiki.saml.dao.data.AclUserBasic;
import com.kontiki.saml.dao.mapper.AclMapper;

@Service
public class AclManagementDAO extends CustomDAOSupport {

	private static final Logger logger = LoggerFactory.getLogger(AclManagementDAO.class);

	@Autowired
	private AuditMonitor auditMonitor;
	
	public AclUserBasic findUserByName(String userName, Long companyId) {
		AclMapper aclmap = this.getSqlSession().getMapper(AclMapper.class);
		return aclmap.getUserByName(userName, companyId);
	}

	public void saveUser(AclUserBasic user, String sessionId) {
		if (user == null) return;
		AclMapper aclmap = this.getSqlSession().getMapper(AclMapper.class);
		if (user.getId() != null) {
			aclmap.updateUser(user);
			auditMonitor.reportUserUpdated(sessionId, user.getUserName(), user.getId());
		} else {
			aclmap.addUser(user);
			auditMonitor.reportUserAdded(sessionId, user.getUserName(), user.getId());
		}
	}

	public AclGroupBasic findGroupByName(String groupName, Long companyId) {
		AclMapper aclmap = this.getSqlSession().getMapper(AclMapper.class);
		return aclmap.getGroupByName(groupName, companyId);
	}

	public void saveGroup(AclGroupBasic group, String sessionId) {
		if (group == null) return;
		AclMapper aclmap = this.getSqlSession().getMapper(AclMapper.class);
		if (group.getId() != null) {
			aclmap.updateGroup(group);
			auditMonitor.reportGroupUpdated(sessionId, group.getGroupName(), group.getId());
		} else {
			aclmap.addGroup(group);
			auditMonitor.reportGroupAdded(sessionId, group.getGroupName(), group.getId());
		}
	}

	public boolean updateUserMembership(AclUserBasic aclUserBasic, List<Long> samlGroupsIds, Long companyId, String sessionId) {
		if (aclUserBasic == null || samlGroupsIds == null) return false;
		AclMapper aclmap = this.getSqlSession().getMapper(AclMapper.class);
		boolean membershipChanged = false;
		Long userId = aclUserBasic.getId();
		Long[] existingGroupIds = aclmap.findNoVirtualGroupsInMembershipForUser(userId , companyId);
		for (Long groupId : existingGroupIds) {
			if (samlGroupsIds.contains(groupId)) {
				// it has already in DB - just remove it from the list
				samlGroupsIds.remove(groupId);
			} else {
				// it is not in SAML assertion - let's remove it from DB
				aclmap.removeMembership(companyId, groupId, userId);
				auditMonitor.reportMembershipRemoved(sessionId, userId, groupId);
		        membershipChanged = true;
			}
		}
		if (samlGroupsIds.size() > 0) {
			for (Long groupId : samlGroupsIds) {
				// 	it is not in DB - let's add it
				aclmap.addMembership(companyId, groupId, userId);
				auditMonitor.reportMembershipAdded(sessionId, userId, groupId);
				membershipChanged = true;
			}
		}
		return membershipChanged;
	}
	
}
