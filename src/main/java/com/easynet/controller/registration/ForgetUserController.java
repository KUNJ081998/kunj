package com.easynet.controller.registration;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.easynet.bean.GetRequestUniqueData;
import com.easynet.configuration.PropConfiguration;
import com.easynet.controller.cardAPI.CardDetailController;
import com.easynet.util.common;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.easynet.impl.LoggerImpl;

/**
 *This class contain method related to forget operation.
 *@author Sagar umate
 * 
 * */
@Component
public class ForgetUserController {

	@Autowired
	CardDetailController cardDetailController;

	@Autowired
	Customer360Controller  customer360Controller;
	@Autowired
	GetRequestUniqueData getRequestUniqueData;
	
	static Logger logger=LoggerFactory.getLogger(ForgetUserController.class);

	@Autowired
	PropConfiguration propConfiguration;
	
	/**
	 * This method used for get user id from card and also verify card detail.
	 * 
	 * @param requestData json format string data.
	 * @return return the user name if already exists and also return user account details.
	 * @date 06/03/2021
	 * @apiNote This method used below API.<br>
	 * 	1.verify card detail.<br>
	 * 	2.call 360 api and also call data base api for verify user exists or not.
	 * 
	 * */
	public String GetUserIdByCard(String requestData){

		JSONObject 	jsonRequestData=new JSONObject(requestData);		
		JSONObject 	josnCardDataResponse=null;//used to store card response data.
		String 		ls_cardVerifyResponse="";
		String 		ls_cardVerifyStatus="";//used to store card status
		String 		ls_responseData="";//Store the response of card or account verify.
		String      ls_regFlag="";
		String 		ls_acctNo="";
		String 		actualErrMsg="";
		LoggerImpl loggerImpl=null;
		
		try {
			loggerImpl=new LoggerImpl();
			loggerImpl.info(logger,"Preparing requset data and calling API.","IN:GetUserIdByCard");
			
			ls_regFlag=jsonRequestData.getString("REG_WITH");
			ls_acctNo=jsonRequestData.getString("ACCT_NO");

			if((ls_regFlag==null ||"".equals(ls_regFlag))||
					(ls_acctNo==null ||"".equals(ls_acctNo)))
			{
				return common.ofGetErrDataJsonArray("99",
						propConfiguration.getMessageOfResCode("commen.title.99", "Validation Failed."), 
						propConfiguration.getMessageOfResCode("commen.invalid_req_data",""),
						"Null values found in request data.","Invalid Request.", "0", "R");				
			}

			loggerImpl.debug(logger,"Calling verify Card API", "IN:GetUserIdByCard");
			/*for get the times performance logs.*/
			loggerImpl.generateProfiler("GetUserIdByCard");
			loggerImpl.startProfiler("Calling Card verify API.");
			
			ls_cardVerifyResponse=cardDetailController.ofVerifyCardDetail(requestData);

			loggerImpl.startProfiler("Reading response data and call API for get ID.");
			loggerImpl.debug(logger,"Verify Card API called.", "IN:GetUserIdByCard");
			
			if (ls_cardVerifyResponse.trim().substring(0, 1).equals("[")) {
				josnCardDataResponse=new JSONArray(ls_cardVerifyResponse).getJSONObject(0);

			}else {
				josnCardDataResponse = new JSONObject(ls_cardVerifyResponse);                  
			}

			ls_cardVerifyStatus=josnCardDataResponse.getString("STATUS");
			if(ls_cardVerifyStatus!=null && "0".equals(ls_cardVerifyStatus)){	

				JSONObject json360RequestData=new JSONObject();
				json360RequestData.put("REG_WITH",ls_regFlag );
				json360RequestData.put("ACCT_NO",ls_acctNo );
				json360RequestData.put("ACTION", "GETUSERALLEXISTS");//set action for get already register user id.
				json360RequestData.put("AUTH_TOKEN",josnCardDataResponse.getString("AUTH_TOKEN"));				
				json360RequestData.put("BLOCK_AUTH_STATUS", josnCardDataResponse.getString("BLOCK_AUTH_STATUS"));

				loggerImpl.debug(logger,"Call Db and customer 360 API.", "IN:GetUserIdByCard");
				
				//call another API for get user ID from account or card no.			
				ls_responseData= customer360Controller.ofVerifyAcctCard(json360RequestData.toString());
				return ls_responseData;
			}else{
				//return error response for card validation failed.
				return ls_cardVerifyResponse;
			}
		} catch (Exception exception) {
			actualErrMsg = common.ofGetTotalErrString(exception, "");
			loggerImpl.error(logger,"Exception : " + actualErrMsg,"IN:GetUserIdByCard");
			
			ls_responseData = common.ofGetErrDataJsonArray("999",
					propConfiguration.getMessageOfResCode("commen.title.999", "Alert."),
					propConfiguration.getMessageOfResCode("commen.exception.999","","(ENP093)"), 
					exception.getMessage(),"Currently Service under maintenance so please try later (ENP093).", "0", "R");
						
			return ls_responseData;
		}finally {
			loggerImpl.stopAndPrintOptLogs(logger,"All API called successfully.","IN:GetUserIdByCard");
			loggerImpl.info(logger,"Response generated and send to client.", "IN:GetUserIdByCard");
		}
	}
}
