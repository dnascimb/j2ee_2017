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

package com.kontiki.saml.dao.data;

import org.slf4j.Logger;

import com.kontiki.saml.crypto.CryptTools;

public class SAMLConfig  {

	// SAML related
	private Long realmId;
	private Long companyId;
	private String companyKid;
	private String _IdpUrl;
	private int _IdpWindowHeight;
	private int _IdpWindowWidth;
	private String _IdpDelimiterForGroups;
	private String _samlAttributeForUser;
	private String _samlAttributeForGroups;
	private String samlAttributeForUserFirstName;
	private String samlAttributeForUserLastName;
	private String samlAttributeForUserEmail;
	private String samlAttributeForUserDisplayName;
	private int _ticketTimeout = 300;
	private int _realmTicketIndex = 0;
	private int realmTicketRandomPercent = 50;
	private int realmTicketVersion = 3;
	
	

    public void readConfigParam(ConfigParam configParam, CryptTools cryptTools, Logger _log) {
		
		String paramName = configParam.getName();
		String curValue = configParam.getValue();
		String defValue = configParam.getDefValue();
		String value = curValue;
		if (value == null) {
			value = defValue;
		}
		if (paramName.equals("_IdpUrl")) {
			try {
				_IdpUrl = cryptTools.base64decode(value);
			} catch (Exception e) {
				_log.error("Invalid " + paramName + " " + value + " :" + e);
			}			
		} else if (paramName.equals("_IdpWindowWidth")) {
			try {
				_IdpWindowWidth = Integer.parseInt(value);
			} catch (Exception e) {
				_log.error("Invalid " + paramName + " " + value + " :" + e);
			}
		} else if (paramName.equals("_IdpWindowHeight")) {
			try {
				_IdpWindowHeight = Integer.parseInt(value);
			} catch (Exception e) {
				_log.error("Invalid " + paramName + " " + value + " :" + e);
			}
		} else if (paramName.equals("_IdpDelimiterForGroups")) {
			try {
				_IdpDelimiterForGroups = value;
			} catch (Exception e) {
				_log.error("Invalid " + paramName + " " + value + " :" + e);
			}
		} else if (paramName.equals("_samlAttributeForUser")) {
			try {
				_samlAttributeForUser = cryptTools.base64decode(value);
			} catch (Exception e) {
				_log.error("Invalid " + paramName + " " + value + " :" + e);
			}
		} else if (paramName.equals("_samlAttributeForGroups")) {
			try {
				_samlAttributeForGroups = cryptTools.base64decode(value);
			} catch (Exception e) {
				_log.error("Invalid " + paramName + " " + value + " :" + e);
			}
		} else if (paramName.equals("samlAttributeForUFN")) {
			try {
				samlAttributeForUserFirstName = cryptTools.base64decode(value);
			} catch (Exception e) {
				_log.error("Invalid " + paramName + " " + value + " :" + e);
			}
		} else if (paramName.equals("samlAttributeForULN")) {
			try {
				samlAttributeForUserLastName = cryptTools.base64decode(value);
			} catch (Exception e) {
				_log.error("Invalid " + paramName + " " + value + " :" + e);
			}
		} else if (paramName.equals("samlAttributeForUEmail")) {
			try {
				samlAttributeForUserEmail = cryptTools.base64decode(value);
			} catch (Exception e) {
				_log.error("Invalid " + paramName + " " + value + " :" + e);
			}
		} else if (paramName.equals("samlAttributeForUDName")) {
			try {
				samlAttributeForUserDisplayName = cryptTools.base64decode(value);
			} catch (Exception e) {
				_log.error("Invalid " + paramName + " " + value + " :" + e);
			}
		} else if (paramName.equals("_ticketTimeout")) {
            try {
                _ticketTimeout = Integer.parseInt(value);
            } catch (Exception e) {
            	_log.error("Invalid " + paramName + " " + value + " :" + e);
            }
		} else if (paramName.equals("realmTicketIndex")) {
			try {
				_realmTicketIndex = Integer.parseInt(value);
			} catch (Exception e) {
				_log.error("Invalid " + paramName + " " + value + " :" + e);
			}			
		}
	}


	public String getIdpUrl() 
	{
		return _IdpUrl;
	}

	public void setIdpUrl(String idpUrl) 
	{
		this._IdpUrl = idpUrl;
	}

	public int getIdpWindowHeight() 
	{
		return _IdpWindowHeight;
	}

	public void setIdpWindowHeight(int idpWindowHeight) 
	{
		this._IdpWindowHeight = idpWindowHeight;
	}

	public int getIdpWindowWidth() 
	{
		return _IdpWindowWidth;
	}

	public void setIdpWindowWidth(int idpWindowWidth) 
	{
		this._IdpWindowWidth = idpWindowWidth;
	}
	
	public String getSamlAttributeForUser() {
		return _samlAttributeForUser;
	}

	public void setSamlAttributeForUser(String samlAttributeForUser) {
		this._samlAttributeForUser = samlAttributeForUser;
	}

	public String getSamlAttributeForGroups() {
		return  _samlAttributeForGroups;
	}

	public void setSamlAttributeForGroups(String _samlAttributeForGroups) {
		this._samlAttributeForGroups = _samlAttributeForGroups;
	}		
	
	public String getSamlAttributeForUserFirstName() {
		return samlAttributeForUserFirstName;
	}

	public void setSamlAttributeForUserFirstName(
			String samlAttributeForUserFirstName) {
		this.samlAttributeForUserFirstName = samlAttributeForUserFirstName;
	}

	public String getSamlAttributeForUserLastName() {
		return samlAttributeForUserLastName;
	}

	public void setSamlAttributeForUserLastName(String samlAttributeForUserLastName) {
		this.samlAttributeForUserLastName = samlAttributeForUserLastName;
	}

	public String getSamlAttributeForUserEmail() {
		return samlAttributeForUserEmail;
	}

	public void setSamlAttributeForUserEmail(String samlAttributeForUserEmail) {
		this.samlAttributeForUserEmail = samlAttributeForUserEmail;
	}

	public String getSamlAttributeForUserDisplayName() {
		return samlAttributeForUserDisplayName;
	}

	public void setSamlAttributeForUserDisplayName(
			String samlAttributeForUserDisplayName) {
		this.samlAttributeForUserDisplayName = samlAttributeForUserDisplayName;
	}

	public String getIdpDelimiterForGroups() {
		return _IdpDelimiterForGroups;
	}

	public void setIdpDelimiterForGroups(String idpDelimiterForGroups) {
		this._IdpDelimiterForGroups = idpDelimiterForGroups;
	}


	public Long getCompanyId() {
		return companyId;
	}


	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}


	public String getCompanyKid() {
		return companyKid;
	}


	public void setCompanyKid(String companyKid) {
		this.companyKid = companyKid;
	}


	public Long getRealmId() {
		return realmId;
	}


	public void setRealmId(Long realmId) {
		this.realmId = realmId;
	}


	public int getTicketTimeout() {
		return _ticketTimeout;
	}

	public int getRealmTicketIndex() {
		return _realmTicketIndex;
	}


	public int getRealmTicketRandomPercent() {
		return realmTicketRandomPercent;
	}


	public void setRealmTicketRandomPercent(int realmTicketRandomPercent) {
		this.realmTicketRandomPercent = realmTicketRandomPercent;
	}


	public int getRealmTicketVersion() {
		return realmTicketVersion;
	}


	public void setRealmTicketVersion(int realmTicketVersion) {
		this.realmTicketVersion = realmTicketVersion;
	}

}
