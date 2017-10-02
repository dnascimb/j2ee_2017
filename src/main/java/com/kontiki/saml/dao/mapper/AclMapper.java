package com.kontiki.saml.dao.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.kontiki.saml.dao.data.AclGroupBasic;
import com.kontiki.saml.dao.data.AclUserBasic;

public interface AclMapper {

	final String getUserByName = "SELECT * FROM ACL_USER_BASIC WHERE COMPANY_ID = #{companyId} and USERNAME = #{username} ";
	final String insertUser = "INSERT INTO ACL_USER_BASIC (COMPANY_ID, CREATED, DISPLAY_NAME, EMAIL , FIRST_NAME , LAST_NAME , LAST_UPDATED , MISSING , USERNAME) "
			+ " VALUES ( #{companyId} , #{created} , #{displayName} , #{email} , #{firstName} , #{lastName} , #{lastUpdated} , #{missing} , #{userName})";
	final String updateUser = "UPDATE ACL_USER_BASIC SET COMPANY_ID = #{companyId} , CREATED = #{created} , DISPLAY_NAME = #{displayName} , EMAIL = #{email} , "
			+ "FIRST_NAME = #{firstName} , LAST_NAME = #{lastName} , LAST_UPDATED = #{lastUpdated} , MISSING = #{missing} , USERNAME = #{userName} "
			+ "WHERE ID = #{id}";

	final String getGroupByName = "SELECT * FROM ACL_GROUP_BASIC WHERE COMPANY_ID = #{companyId} and GROUPNAME = #{groupname} ";
	final String insertGroup = "INSERT INTO ACL_GROUP_BASIC (ACTIVE , COMPANY_ID , CREATED , GROUPNAME , IS_VIRTUAL , LAST_UPDATED) "
			+ "VALUES ( #{active} , #{companyId} , #{created} , #{groupName} , #{virtual} , #{lastUpdated})";
	final String updateGroup = "UPDATE ACL_GROUP_BASIC SET ACTIVE = #{active} , COMPANY_ID = #{companyId} , CREATED = #{created} , "
			+ "GROUPNAME = #{groupName} , IS_VIRTUAL = #{virtual} , LAST_UPDATED = #{lastUpdated}  WHERE ID = #{id}";
	
	final String findNoVirtualGroupsInMembershipForUser = "select M.GROUP_ID from ACL_MEMBERSHIP M, ACL_GROUP_BASIC G WHERE M.USER_ID = #{userId}  AND M.COMPANY_ID = #{companyId} AND M.GROUP_ID = G.ID AND G.IS_VIRTUAL = 0 AND G.ACTIVE = 1";
	final String insertMembership = "INSERT INTO ACL_MEMBERSHIP (COMPANY_ID , CREATED , GROUP_ID , UPDATED , USER_ID) " +
	        " VALUES ( #{companyId} , NOW() , #{groupId} , NOW() , #{userId}) ";
	final String removeMembership = "DELETE FROM ACL_MEMBERSHIP WHERE COMPANY_ID = #{companyId} AND GROUP_ID = #{groupId} AND USER_ID = #{userId}) ";
	
	@Select(getUserByName)
	@Results(value = { @Result(property = "id", column = "ID"), @Result(property = "companyId", column = "COMPANY_ID"),
			@Result(property = "userName", column = "USERNAME"),
			@Result(property = "displayName", column = "DISPLAY_NAME"),
			@Result(property = "firstName", column = "FIRST_NAME"),
			@Result(property = "lastName", column = "LAST_NAME"), @Result(property = "email", column = "EMAIL"),
			@Result(property = "created", column = "CREATED"),
			@Result(property = "lastUpdated", column = "LAST_UPDATED"),
			@Result(property = "missing", column = "MISSING") })
	AclUserBasic getUserByName(@Param("username") String username, @Param("companyId") Long companyId);

	@Insert(insertUser)
	@Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "ID")
	void addUser(AclUserBasic user);

	@Update(updateUser)
	void updateUser(AclUserBasic user);

	@Select(getGroupByName)
	@Results(value = { @Result(property = "id", column = "ID"), @Result(property = "companyId", column = "COMPANY_ID"),
			@Result(property = "groupName", column = "GROUPNAME"), @Result(property = "created", column = "CREATED"),
			@Result(property = "lastUpdated", column = "LAST_UPDATED"),
			@Result(property = "virtual", column = "IS_VIRTUAL"), @Result(property = "active", column = "ACTIVE") })
	AclGroupBasic getGroupByName(@Param("groupname") String groupname, @Param("companyId") Long companyId);

	@Insert(insertGroup)
	@Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "ID")
	void addGroup(AclGroupBasic user);

	@Update(updateGroup)
	void updateGroup(AclGroupBasic user);

	@Select(findNoVirtualGroupsInMembershipForUser)
	@Results(value = { @Result(column = "GROUP_ID") })
	Long[] findNoVirtualGroupsInMembershipForUser(@Param("userId") Long userId, @Param("companyId") Long companyId);

	@Insert(insertMembership)
	void addMembership(@Param("companyId") Long companyId,@Param("groupId") Long groupId,@Param("userId") Long userId);

	@Delete(removeMembership)
	void removeMembership(@Param("companyId") Long companyId,@Param("groupId") Long groupId,@Param("userId") Long userId);
	
}
