package com.kontiki.saml.dao.data;

public class ConfigParam {

	private String name;
	private String defValue;
	private String value;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDefValue() {
		return defValue;
	}
	public void setDefValue(String defValue) {
		this.defValue = defValue;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return "ConfigParam [name=" + name + ", defValue=" + defValue + ", value=" + value + "]";
	}


}