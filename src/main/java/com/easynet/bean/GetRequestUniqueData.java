
package com.easynet.bean;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

@Component
@Scope(value="request",proxyMode = ScopedProxyMode.TARGET_CLASS)
public class GetRequestUniqueData {

	private long uniqueNumber;
	private String langCode; 
	
	
	  public GetRequestUniqueData() { uniqueNumber=System.nanoTime(); }
	 

	public long getUniqueNumber() {
		return uniqueNumber;
	}

	public void setUniqueNumber(long uniqueNumber) {
		this.uniqueNumber = uniqueNumber;
	}


	public String getLangCode() {
		return langCode;
	}


	public void setLangCode(String langCode) {
		this.langCode = langCode;
	}		
}
