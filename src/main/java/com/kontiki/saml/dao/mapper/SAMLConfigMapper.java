package com.kontiki.saml.dao.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import com.kontiki.saml.dao.data.AccessRealm;
import com.kontiki.saml.dao.data.AuthRealmTicket;
import com.kontiki.saml.dao.data.ConfigParam;

public interface SAMLConfigMapper {
	final String getAccessRealm = "SELECT AR.*, C.KID_PREFIX  FROM ACCESS_REALM AR, COMPANY C WHERE AR.MOID =  #{moid} AND AR.COMPANY_ID = C.COMPANY_ID ";
	final String getClassConfigParamValues = "select ccpd.NAME, ccpd.DEF_VALUE, ccp.VALUE from CLASS_CONFIG cc, CLASS_IMPLEMENTATIONS ci, CLASS_CONFIG_PARAM_DEF ccpd left outer join CLASS_CONFIG_PARAM ccp on ccp.DEF_ID = ccpd.ID and ccp.CONFIG_ID = #{configId} where cc.CONFIG_ID =  #{configId}  and cc.RLM_ID = ci.ID and  ccpd.CLASS_IMPLEMENTATION_ID = ci.ID ";

	final String getServerPolicyDefByName = "SELECT DEFAULT_VALUE FROM SERVER_POLICY_DEF WHERE NAME = #{name} ";

	final String findRealmTicket = "SELECT * FROM auth_realm_ticket WHERE company_id = #{companyId} and realm_id = #{realmId} and user_name = #{userName}";
	final String insertRealmTicket = "INSERT INTO auth_realm_ticket (CREATED , EXPIRY , company_id , realm_id , ticket_id , user_name , version) " +
	        " VALUES ( #{created} , #{expiry} , #{companyId} , #{realmId} , #{ticket} , #{userName} , #{version} ) ";
	final String removeRealmTicket = "DELETE FROM auth_realm_ticket WHERE id = #{id}) ";

	final String insertClientToken = "INSERT INTO client_token (company_id , expirationTime , realm_ticket , token_key , username , web_session_id) " +
			" VALUES ( #{companyId} , #{expirationtime} , #{realmTicket} , #{tokenKey} , #{username} , #{webSessionId} ) ";

	@Select(getAccessRealm)
	@Results(value = { @Result(property = "id", column = "ID"), @Result(property = "moid", column = "MOID"),
			@Result(property = "companyId", column = "COMPANY_ID"),
			@Result(property = "loginConfigId", column = "LOGIN_CONFIG"),
			@Result(property = "kidPrefix", column = "KID_PREFIX") })
	AccessRealm getAccessRealmByMoid(@Param("moid") String moid);

	@Select(getClassConfigParamValues)
	@Results(value = { @Result(property = "name", column = "NAME"),
			@Result(property = "defValue", column = "DEF_VALUE"), @Result(property = "value", column = "VALUE") })
	List<ConfigParam> getConfigParams(@Param("configId")  Long configId);

	@Select(getServerPolicyDefByName)
	@Results(value = { @Result(column = "DEFAULT_VALUE") })
	String getServerPolicyValue(@Param("name") String name);

	@Select(findRealmTicket)
	@Results(value = { @Result(property = "id", column = "id"), @Result(property = "ticket", column = "ticket_id"),
			@Result(property = "companyId", column = "company_id"), @Result(property = "realmId", column = "realm_id"),
			@Result(property = "created", column = "CREATED"), @Result(property = "expiry", column = "EXPIRY"),
			@Result(property = "version", column = "version"), @Result(property = "userName", column = "user_name") })
	AuthRealmTicket findRealmTicketForUser(@Param("userName") String userName,@Param("companyId")  Long companyId, @Param("realmId")  Long realmId);


	@Insert(insertRealmTicket)
	@Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
	void addRealmTicket(AuthRealmTicket ticket);

	@Delete(removeRealmTicket)
	void removeRealmTicket(@Param("id") Long id);

	@Insert(insertClientToken)
	void addClientToken(@Param("companyId") Long companyId, @Param("expirationtime") Date expirationtime, @Param("realmTicket") String realmTicket, @Param("tokenKey") String tokenKey, @Param("username") String username, @Param("webSessionId") String webSessionId );
	
}
