package com.easynet.controller.ApiController;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.easynet.bean.GetRequestUniqueData;
import com.easynet.configuration.PropConfiguration;
import com.easynet.util.GetData;
import com.easynet.util.apiCall1;
import com.easynet.util.common;
import com.easynet.util.readXML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.easynet.impl.LoggerImpl;

@Controller
public class AllAccountsAndCardsApi {

	@Autowired
	apiCall1 callApi;
	@Autowired
	GetData getData;
	@Autowired
	GetRequestUniqueData getRequestUniqueData;

	@Autowired
	PropConfiguration propConfiguration;

	static Logger logger=LoggerFactory.getLogger(AllAccountsAndCardsApi.class);

	/**
	 *This method used for get customer 360 api data using request argument.
	 *This method generate the authorization header every time.
	 *@Date-22/01/2020
	 *@return -return string Json data of api response.
	 *@apiNote This method used below API.<br>
	 *	1.getBearerToken for get access token
	 *	2.allAccountsAndCards for get customer details.
	 * */

	public String ofGetCustomer360(String requestData) {

		String httpHeaders = "";		     
		String ls_responseData = "";
		String ls_authorizationJson = "";       
		String ls_statusCd = "";
		String ls_authUname="";//to store authorization username
		String ls_authPass="";//to store authorization password
		String ls_apiURL = "";//to store API calling URL.
		String ls_apiMethodType = "";//to store API Method Type
		String actualErrMsg = "";//to store error message		
		String ls_responseCode="";
		JSONObject	jsonRequestData=null;
		LoggerImpl loggerImpl=null;
		String ls_langResCodeMsg="";

		try {

			loggerImpl=new LoggerImpl();					
			/*get data from  json*/
			//JSONObject jsonRequest = new JSONObject(requestData);
			JSONObject jsonResponse = null;

			//jsonRequest.put("REQ_FROM", "C");
			//ls_requestData = jsonRequest.toString();
			//GetData getData = new GetData();

			/*get the data from database procedure*/
			//ls_actualResData = getData.ofGetResponseData(ls_requestData);

			/*read the response data*/
			//jsonResponse = new JSONObject(ls_actualResData);
			//ls_outputTo = jsonResponse.getString(requestData);
			//ls_responseData = ls_actualResData;

			/*if flag is "M" means send data to API*/
			//if ("M".equals(ls_outputTo)) {
			//apiCall1 callApi = new apiCall1();

			//get authorization configuration
			loggerImpl.info(logger,"Preparing requset data and calling API.","IN:ofGetCustomer360");

			ls_apiURL = readXML.getXmlData("root>CUST_360>AUTHORIZATION>OAUTH_URL");
			ls_apiMethodType = readXML.getXmlData("root>CUST_360>AUTHORIZATION>METHOD_TYPE");
			ls_authUname=readXML.getXmlData("root>CUST_360>AUTHORIZATION>USER_NAME");
			ls_authPass=readXML.getXmlData("root>CUST_360>AUTHORIZATION>PASSWORD");

			//Make request data of authorization.
			JSONObject ls_authorizationReqJson=new JSONObject();
			ls_authorizationReqJson.put("username",ls_authUname);
			ls_authorizationReqJson.put("password",ls_authPass);

			/*for get the times performance logs.*/
			loggerImpl.generateProfiler("ofGetCustomer360");
			loggerImpl.startProfiler("getBearerToken API Calling.");

			loggerImpl.debug(logger,"getBearerToken API calling.", "IN:ofGetCustomer360");

			/*Call API for get acess token*/
			ls_authorizationJson = callApi.PostApi(ls_apiMethodType, ls_apiURL, ls_authorizationReqJson.toString(), null);			
			loggerImpl.startProfiler("Preparing request data.");

			loggerImpl.debug(logger,"getBearerToken API called successfully.", "IN:ofGetCustomer360");

			if (ls_authorizationJson.trim().substring(0, 1).equals("[")) {
				jsonResponse=new JSONArray(ls_authorizationJson).getJSONObject(0);
			}else if (ls_authorizationJson.trim().substring(0, 1).equals("{")) {
				jsonResponse=new JSONObject(ls_authorizationJson);                  
			}

			/*Get status for check response data*/
			ls_statusCd = jsonResponse.getString("STATUS");
			if (ls_statusCd != null && !"".equals(ls_statusCd) && "0".equals(ls_statusCd)) {
				/*get access token data from response key*/
				ls_authorizationJson = jsonResponse.getString("RESPONSE");

				/*Set the access token data */
				JSONObject httpHeadersJson = new JSONObject();
				httpHeadersJson.put("ACCESS_TOKEN", ls_authorizationJson);
				httpHeaders = httpHeadersJson.toString();

				ls_apiURL = readXML.getXmlData("root>CUST_360>API_URL");
				ls_apiMethodType = readXML.getXmlData("root>CUST_360>METHOD_TYPE");

				jsonRequestData=new JSONObject(requestData);
				jsonRequestData.put("allProducts","Y");

				loggerImpl.debug(logger,"allAccountsAndCards API calling.", "IN:ofGetCustomer360");
				loggerImpl.startProfiler("allAccountsAndCards API Calling.");

				/*Call API to get actual account data .*/
				ls_responseData = callApi.PostApi(ls_apiMethodType, ls_apiURL, jsonRequestData.toString(), httpHeaders);				
				/*load response data into json*/

				loggerImpl.debug(logger,"allAccountsAndCards API called successfully.", "IN:ofGetCustomer360",ls_responseData);

				if (ls_responseData.trim().substring(0, 1).equals("[")) {
					jsonResponse=new JSONArray(ls_responseData).getJSONObject(0);
				}else if (ls_responseData.trim().substring(0, 1).equals("{")) {
					jsonResponse=new JSONObject(ls_responseData);                  
				}

				ls_statusCd = jsonResponse.getString("STATUS");

				if (ls_statusCd != null && !"".equals(ls_statusCd) && "0".equals(ls_statusCd)) {

					//return the response data generated return from API.
					return ls_responseData;
				} else {										
					ls_responseCode=jsonResponse.getString("RESPONSECODE");										
					ls_langResCodeMsg=propConfiguration.getResponseCode("allAccountsAndCards."+ls_responseCode);

					ls_responseData = common.ofGetErrDataJsonArray(ls_langResCodeMsg,
							propConfiguration.getMessageOfResCode("commen.title."+ls_langResCodeMsg, "Alert."), 
							propConfiguration.getMessageOfResCode("allAccountsAndCards."+ls_responseCode,"","(ENP016)"),
							ls_responseData,"Currently Service under maintenance so please try later (ENP016).",
							ls_responseCode, "R");

					//return same response comes from API.
					return ls_responseData;
				}
			} else {

				ls_responseCode=jsonResponse.getString("RESPONSECODE");
				ls_langResCodeMsg=propConfiguration.getResponseCode("allAccountsAndCards."+ls_responseCode);

				ls_responseData = common.ofGetErrDataJsonArray(ls_langResCodeMsg,
						propConfiguration.getMessageOfResCode("commen.title."+ls_langResCodeMsg, "Alert."),
						propConfiguration.getMessageOfResCode("allAccountsAndCards."+ls_responseCode,"","(ENP136)"),
						ls_authorizationJson,"Currently Service under maintenance so please try later (ENP136).", "0", "R");

				return ls_responseData;                
			} 

		} catch (Exception exception) {
			actualErrMsg = common.ofGetTotalErrString(exception, "");
			loggerImpl.error(logger,"Exception : " + actualErrMsg,"IN:ofGetCustomer360");

			ls_responseData = common.ofGetErrDataJsonArray("999",
					propConfiguration.getMessageOfResCode("commen.title.999", "Alert."), 
					propConfiguration.getMessageOfResCode("commen.exception.999","","(ENP015)"), 
					exception.getMessage(), "Currently Service under maintenance so please try later (ENP015)", "0", "R");

			return ls_responseData;
		}finally {
			loggerImpl.stopAndPrintOptLogs(logger,"All API called successfully.","IN:ofGetCustomer360");
			loggerImpl.info(logger,"Response generated and send to client.", "IN:ofGetCustomer360");
			System.gc();
		}
	}
}
