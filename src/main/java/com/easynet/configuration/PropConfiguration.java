package com.easynet.configuration;


import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.lang.Nullable;

import com.easynet.bean.GetRequestUniqueData;

@Configuration
@PropertySource("classpath:responsecode_mapping.properties")
public class PropConfiguration {

	@Autowired
	Environment environment;
	
	@Autowired
    private MessageSource messageSource;
	
	@Autowired
	GetRequestUniqueData getRequestUniqueData;
	
	
	public String getPropertyValue(String keyName,String defaultValue) {
		return environment.getProperty(keyName, defaultValue);
	}
	
	/**
	 *This method return the value of given response code.
	 *@param keyName -response code name with api short name.
	 *@param defaultValue -default value if key not found.
	 *@return This method return the response code value.<br>
	 *		  value of key found other than 99 or 999 then return default 999.  
	 * 
	 * **/
	public String getResponseCode(String keyName) {
		String ls_value="";
		String ls_returnValue="";
		
		ls_value=getPropertyValue(keyName,"999");
		if("99".equals(ls_value) || "999".equals(ls_value)) {
			ls_returnValue=ls_value;
		}else {
			ls_returnValue="999";
		}
				
		return ls_returnValue;		
	}
	
	/**
	 *This method return the respective language message of given response code.
	 *@param keyName 		-response code name with api short name.
	 *@param defaultValue 	-default value if key not found.
	 *@param object			-this is vararg type object. <br>
	 *		1.1st Argument is appendMessage Value.		
	 *@return This method return the message if key not found then return default message.
	 * 
	 * **/
	public String getMessageOfResCode(String keyName,String defaultValue,@Nullable Object ... object) {
		String ls_returnValue="";
		String ls_appendMsgValue=null;
		
		String ls_langCode=getRequestUniqueData.getLangCode();
		
		if(ls_langCode==null || "".equals(ls_langCode)) {
			ls_langCode="EN";
		}
		
		ls_returnValue=messageSource.getMessage(keyName,null,defaultValue,new Locale(ls_langCode));
			
		if(object!=null && object.length > 0) {
			ls_appendMsgValue=String.valueOf(object[0]);		
		}
		
		if(ls_appendMsgValue!=null && !"".equals(ls_appendMsgValue) && keyName.trim().endsWith("999")){
			
			if(ls_returnValue.trim().endsWith("."))
			{
				ls_returnValue=ls_returnValue.substring(0, ls_returnValue.length() -1 );
				ls_returnValue=ls_returnValue.trim()+" "+ls_appendMsgValue+".";
			}
		}
		
		return ls_returnValue;		
	}
}
