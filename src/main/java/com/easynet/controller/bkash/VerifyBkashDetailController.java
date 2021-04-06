package com.easynet.controller.bkash;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.easynet.configuration.PropConfiguration;
import com.easynet.controller.ApiController.Bkash.BkashCustomerKycWapper;
import com.easynet.impl.LoggerImpl;
import com.easynet.util.common;

@Component
public class VerifyBkashDetailController {

	@Autowired
	BkashCustomerKycWapper bkashCustomerKycWapper;
	
	@Autowired
	PropConfiguration propConfiguration;
	
	static Logger logger=LoggerFactory.getLogger(VerifyBkashDetailController.class);
	 
	public String verifyBkashMobileNo(String requestData) {
		String ls_responseData="";
		JSONObject apiResponseJsonObj=null;
		String ls_statucCd="";
		String ls_responseCOde="";
		String ls_actualErrMsg="";
		LoggerImpl	loggerImpl=null;
		String ls_langResCodeMsg="";
		
		try {	
			loggerImpl =new LoggerImpl();
		
			loggerImpl.info(logger,"Calling Bkash API for validate mobile Number.", "IN:verifyBkashMobileNo");
			//call the API to get the details
			ls_responseData=bkashCustomerKycWapper.getBkashCustomerKyc(requestData);
			
			if (ls_responseData.trim().substring(0, 1).equals("[")) {
				apiResponseJsonObj=new JSONArray(ls_responseData).getJSONObject(0);
			}else {
				apiResponseJsonObj = new JSONObject(ls_responseData);                  
			}
			
			//check the status of API and response code.
			ls_statucCd=apiResponseJsonObj.getString("STATUS");
			ls_responseCOde=apiResponseJsonObj.getString("RESPONSECODE");
			
			if(ls_statucCd!=null && "0".equals(ls_statucCd)) {
				
				if(ls_responseCOde!=null && "100".equals(ls_responseCOde)) {
						
					//return the success message
					JSONObject jsonResponseObj=new JSONObject();
					JSONObject jsonSubResponseObj=new JSONObject();
					JSONArray jsonResponseArray=new JSONArray(); 
					JSONArray jsonResponseMainArray=new JSONArray(); 
	
					jsonResponseObj.put("STATUS", "0");
					jsonResponseObj.put("MESSAGE", "Mobile Number successfully verified.");
					jsonResponseObj.put("COLOR", "G");				
	
					jsonSubResponseObj.put("TITLE", "Alert.");
					jsonSubResponseObj.put("MESSAGE", "Mobile Number successfully verified.");
					jsonResponseArray.put(jsonSubResponseObj);
	
					jsonResponseObj.put("RESPONSE", jsonResponseArray);								
					jsonResponseMainArray.put(jsonResponseObj);
	
					return jsonResponseMainArray.toString();
				}else {
					//return the error message
					ls_langResCodeMsg=propConfiguration.getResponseCode("getBkashCustomerKYC."+ls_responseCOde);
					
					return common.ofGetErrDataJsonArray(ls_langResCodeMsg,
							propConfiguration.getMessageOfResCode("commen.title."+ls_langResCodeMsg, "Alert."),
							propConfiguration.getMessageOfResCode("getBkashCustomerKYC."+ls_responseCOde,"","(ENP140)"),
							ls_responseData, "Currently Service under maintenance so please try later(ENP140).", ls_responseCOde, "R");
					
				}
			}else{
				return ls_responseData;
			}
			
		}catch(Exception exception) {
			ls_actualErrMsg = common.ofGetTotalErrString(exception, "");            
            loggerImpl.error(logger,"Exception : " + ls_actualErrMsg,"IN:verifyBkashMobileNo");
            
            ls_responseData = common.ofGetErrDataJsonArray("999",
            		propConfiguration.getMessageOfResCode("commen.title.999", "Alert."),
            		propConfiguration.getMessageOfResCode("commen.exception.999","","(ENP109)"), 
            		exception.getMessage(), "Currently Service under maintenance so please try later (ENP109)", "0", "R");
                        
            return ls_responseData;
		}finally {
			loggerImpl.info(logger,"Bkash API response generated and sent to client.", "IN:verifyBkashMobileNo");
		}		
	}
}
