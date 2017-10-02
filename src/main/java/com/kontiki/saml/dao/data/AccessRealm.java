package com.kontiki.saml.dao.data;

public class AccessRealm {

	private Long id;
	private String moid;
	private Long companyId;
	private Long loginConfigId;
	private String kidPrefix;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMoid() {
		return moid;
	}

	public void setMoid(String moid) {
		this.moid = moid;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public Long getLoginConfigId() {
		return loginConfigId;
	}

	public void setLoginConfigId(Long loginConfigId) {
		this.loginConfigId = loginConfigId;
	}
	
	public String getKidPrefix() {
		return kidPrefix;
	}

	public void setKidPrefix(String kidPrefix) {
		this.kidPrefix = kidPrefix;
	}

	@Override
	public String toString() {
		return "AccessRealm [id=" + id + ", moid=" + moid + ", companyId=" + companyId + ", loginConfigId="
				+ loginConfigId + "]";
	}


}