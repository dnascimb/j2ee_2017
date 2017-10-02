package com.kontiki.saml.dao.data;

import java.util.Date;

public class AuthRealmTicket {

	private Long id;
	private String ticket;
	private Long companyId;
	private Long realmId;
	private Date created;
	private Date expiry;
	private Long version;
	private String userName;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTicket() {
		return ticket;
	}
	public void setTicket(String ticket) {
		this.ticket = ticket;
	}
	public Long getCompanyId() {
		return companyId;
	}
	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
	public Long getRealmId() {
		return realmId;
	}
	public void setRealmId(Long realmId) {
		this.realmId = realmId;
	}
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	public Date getExpiry() {
		return expiry;
	}
	public void setExpiry(Date expiry) {
		this.expiry = expiry;
	}
	public Long getVersion() {
		return version;
	}
	public void setVersion(Long version) {
		this.version = version;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	@Override
	public String toString() {
		return "AuthRealmTicket [id=" + id + ", ticket=" + ticket + ", companyId=" + companyId + ", realmId=" + realmId
				+ ", created=" + created + ", expiry=" + expiry + ", version=" + version + ", userName=" + userName
				+ "]";
	}
	


}