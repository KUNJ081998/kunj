package com.easynet.controller.registration;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;

import com.easynet.bean.GetRequestUniqueData;
import com.easynet.configuration.PropConfiguration;
import com.easynet.controller.ApiController.AllAccountsAndCardsApi;
import com.easynet.impl.JSONObjectImpl;
import com.easynet.util.GetData;
import com.easynet.util.common;

import static com.easynet.util.common.PrintErrLog;
import java.util.Set;

import org.json.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.easynet.impl.LoggerImpl;


@Component
public class LoginController {

	@Autowired
	GetData getData;

	@Autowired
	GetRequestUniqueData getRequestUniqueData;

	@Autowired
	AllAccountsAndCardsApi allAccountsAndCardsApi;

	@Autowired
	PropConfiguration propConfiguration;
	
	static Logger logger=LoggerFactory.getLogger(LoginController.class);
	
	/**
	 *This method used for check user login credentials and 
	 *	update the user detail as per 360 api response. 
	 *
	 * @param requestData - Json data in string format.
	 * @author sagar Umate
	 * @since 03/03/2021
	 * @return return the detail of user and also updated details string data in json format.
	 * @apiNote This method used below API.<br>
	 * 	1.call database api for verify user details.<br>
	 * 	2.call 360 api for get user updated details.<br>
	 * 	3.call database api for refresh the user details.
	 *  
	 * */
	public String ofverifyAndUpdateDtl(String requestData)
	{
		String ls_responseData="";
		String ls_apiResponseData="";
		String ls_dbStatusCode="";
		String actualErrMsg="";
		String ls_cbNumber="";
		String ls_statusCd="";
		String ls_userName="";
		String ls_customerID="";
		String ls_DBResponseData="";
		String ls_responseCode="";
		String ls_allAccountsAndCards="";

		JSONObject jsonDbResponse = null;
		JSONObject jsonResponseData = null;
		JSONObject jsonUpdatedResponseData = null;
		JSONObject jsonUpdateAPIResData = null;
		JSONObject jsonRequestData=null;
		JSONObject jsonAPIResponsetData=null;
		JSONObject jsonCustomerDtl=null;
		LoggerImpl loggerImpl=null;		
		String 	   ls_langResCodeMsg="";

		try {
			loggerImpl=new LoggerImpl();
			loggerImpl.info(logger,"Preparing requset data and calling API.","IN:ofverifyAndUpdateDtl");
			/*for get the times performance logs.*/
			loggerImpl.generateProfiler("ofverifyAndUpdateDtl");						
			loggerImpl.debug(logger,"Calling db API for verify UserID.", "IN:ofverifyAndUpdateDtl");
			
			loggerImpl.startProfiler("Calling db API.");
			
			//call back end procedure for get login details.
			ls_responseData= getData.ofGetResponseData(requestData);
			
			loggerImpl.startProfiler("DB API called and reading data.");
			
			loggerImpl.debug(logger,"DB API called and reading response data.", "IN:ofverifyAndUpdateDtl");
			
			//check for success response data
			if(ls_responseData.trim().substring(0, 1).equals("{")){
				jsonDbResponse=new JSONObjectImpl(ls_responseData);

			}else if(ls_responseData.trim().substring(0, 1).equals("[")) {
				jsonDbResponse=new JSONObjectImpl(new JSONArray(ls_responseData).getJSONObject(0));
			}

			ls_dbStatusCode=jsonDbResponse.getString("STATUS");
			//check the status of database API if 0 or 61(first time login then continue else return.)
			if(ls_dbStatusCode!=null &&("0".equals(ls_dbStatusCode) || "61".equals(ls_dbStatusCode))) {
						
				//get the response data of login API.
				jsonResponseData=jsonDbResponse.getJSONArray("RESPONSE").getJSONObject(0);
				ls_userName=jsonResponseData.getString("USER_ID");
				ls_customerID=jsonResponseData.getString("CUSTOMER_ID");

				jsonCustomerDtl=jsonResponseData.getJSONArray("CUSTOMER_DTL_LIST").getJSONObject(0);							
				ls_cbNumber=jsonCustomerDtl.getString("CBNUMBER");

				jsonRequestData=new JSONObject();
				jsonRequestData.put("cbNumber",ls_cbNumber);

				loggerImpl.debug(logger,"Calling customer 360 API.", "IN:ofverifyAndUpdateDtl");
				loggerImpl.startProfiler("Call customer 360 API.");
				
				/*Call API to get actual account data .*/
				ls_apiResponseData = allAccountsAndCardsApi.ofGetCustomer360(jsonRequestData.toString());		
				
				loggerImpl.startProfiler("Customer 360 API called and reading response data.");
				
				loggerImpl.debug(logger,"Customer 360 API called and reading response data.", "IN:ofverifyAndUpdateDtl",ls_apiResponseData);
				
				/*load response data into json*/
				if (ls_apiResponseData.trim().substring(0, 1).equals("[")) {
					jsonAPIResponsetData=new JSONArray(ls_apiResponseData).getJSONObject(0);
				}else if (ls_apiResponseData.trim().substring(0, 1).equals("{")) {
					jsonAPIResponsetData=new JSONObject(ls_apiResponseData);                  
				}

				ls_statusCd = jsonAPIResponsetData.getString("STATUS");

				if (ls_statusCd != null && !"".equals(ls_statusCd) && "0".equals(ls_statusCd)) {
					/*get API response Data*/
					ls_allAccountsAndCards = jsonAPIResponsetData.getString("RESPONSE");

					jsonRequestData.remove("cbNumber");
					jsonRequestData.put("USER_ID", ls_userName);
					jsonRequestData.put("CUSTOMER_ID", ls_customerID);

					if (ls_allAccountsAndCards.trim().substring(0, 1).equals("[")) {
						/*Set API response Data*/
						jsonRequestData.put("ALL_ACCOUNT_DETAIL", new JSONArray(ls_allAccountsAndCards));
					}else if (ls_allAccountsAndCards.trim().substring(0, 1).equals("{")) {
						jsonRequestData.put("ALL_ACCOUNT_DETAIL", new JSONObject(ls_allAccountsAndCards));                  
					}else{
						jsonRequestData.put("ALL_ACCOUNT_DETAIL", ls_allAccountsAndCards);
					}
					
					loggerImpl.debug(logger,"Calling DB API for update details.", "IN:ofverifyAndUpdateDtl");
					loggerImpl.startProfiler("Call DB API for UPD DTL.");
					
					//call procedure for update the user details.
					ls_DBResponseData=getData.ofGetResponseData("PACK_MOB_USER.PROC_VERIFY_AND_UPDATE_DTL",jsonRequestData.toString());

					loggerImpl.startProfiler("Generating response.");
					
					loggerImpl.debug(logger,"DB API called and generating response data.", "IN:ofverifyAndUpdateDtl");
					
					if(ls_DBResponseData.trim().substring(0, 1).equals("{")){
						jsonUpdateAPIResData=new JSONObject(ls_DBResponseData);

					}else if(ls_DBResponseData.trim().substring(0, 1).equals("[")) {
						jsonUpdateAPIResData=new JSONArray(ls_DBResponseData).getJSONObject(0);
					}

					ls_dbStatusCode=jsonUpdateAPIResData.getString("STATUS");

					if(ls_dbStatusCode!=null && "0".equals(ls_dbStatusCode)){
						
						jsonUpdatedResponseData=jsonUpdateAPIResData.getJSONArray("RESPONSE").getJSONObject(0);
								
						//get the keys data for update into login request data.
						Set<String> jsonKeyList=jsonUpdatedResponseData.keySet();
						//put all values from response data.
						for (String keyName : jsonKeyList) {
							jsonResponseData.put(keyName, jsonUpdatedResponseData.get(keyName));					
						}
						//set new response data into actual response data object.
						jsonDbResponse.put("RESPONSE", new JSONArray().put(jsonResponseData));
						
						//return the updated data of user in API response.						
						return new JSONArray().put(jsonDbResponse).toString();
					}else{
						//return the same response of update API in case of error.
						return ls_DBResponseData;
					}
				} else {

					ls_responseCode=jsonAPIResponsetData.getString("RESPONSECODE");
					ls_langResCodeMsg=propConfiguration.getResponseCode("allAccountsAndCards."+ls_responseCode);
					if("404".equals(ls_responseCode)){								
						
						return common.ofGetErrDataJsonArray(ls_langResCodeMsg,
								propConfiguration.getMessageOfResCode("commen.title."+ls_langResCodeMsg, "Alert."),
								propConfiguration.getMessageOfResCode("login.data_not_found","",""),
								ls_apiResponseData,"Your primary authenticator is not found ,Kindly contact customer care.", ls_responseCode, "R");
					}else {
						//return the 1st login database api response data in case of any error in 360 API other than 404.
						return ls_responseData;				
					}
				}
			}else{
				//return same response of database API. 
				return ls_responseData;				
			}
		}catch(Exception exception){

			actualErrMsg = common.ofGetTotalErrString(exception, "");
			loggerImpl.error(logger,"Exception : " + actualErrMsg,"IN:ofverifyAndUpdateDtl");
			
			actualErrMsg = common.ofGetErrDataJsonArray("999", 
					propConfiguration.getMessageOfResCode("commen.title.999", "Alert."),
					propConfiguration.getMessageOfResCode("commen.exception.999","","(ENP096)"),
					exception.getMessage(),"Currently Service under maintenance so please try later (ENP096).", "0", "R");
						
			return actualErrMsg;
		}finally {			
			loggerImpl.stopAndPrintOptLogs(logger,"All API called successfully.","IN:ofverifyAndUpdateDtl");
			loggerImpl.info(logger,"Response generated and send to client.", "IN:ofverifyAndUpdateDtl");
		}
	}
}

