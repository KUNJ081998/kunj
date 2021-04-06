package com.easynet.bean;

public class ProductDetailObject {
	
	String ls_parentProducuntNm=null;
	String ls_parentProductCode=null;
	String ls_typeCodeList=null;
	
	public ProductDetailObject(){
		super();
		this.ls_parentProducuntNm = null;
		this.ls_parentProductCode = null;
		this.ls_typeCodeList = null;
	}
	
	public ProductDetailObject(String ls_parentProducuntNm, String ls_parentProductCode, String ls_typeCodeList) {
		super();
		this.ls_parentProducuntNm = ls_parentProducuntNm;
		this.ls_parentProductCode = ls_parentProductCode;
		this.ls_typeCodeList = ls_typeCodeList;
	}
	
	public String getLs_parentProducuntNm() {
		return ls_parentProducuntNm;
	}
	public void setLs_parentProducuntNm(String ls_parentProducuntNm) {
		this.ls_parentProducuntNm = ls_parentProducuntNm;
	}
	public String getLs_parentProductCode() {
		return ls_parentProductCode;
	}
	public void setLs_parentProductCode(String ls_parentProductCode) {
		this.ls_parentProductCode = ls_parentProductCode;
	}
	public String getLs_typeCodeList() {
		return ls_typeCodeList;
	}
	public void setLs_typeCodeList(String ls_typeCodeList) {
		this.ls_typeCodeList = ls_typeCodeList;
	}
	

}
