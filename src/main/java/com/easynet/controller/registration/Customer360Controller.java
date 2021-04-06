package com.easynet.controller.registration;

import com.easynet.bean.GetRequestUniqueData;
import com.easynet.configuration.PropConfiguration;
import com.easynet.controller.ApiController.AllAccountsAndCardsApi;
import com.easynet.controller.cardAPI.CardDetailController;
import com.easynet.impl.JSONObjectImpl;
import com.easynet.util.GetData;
import com.easynet.util.apiCall1;
import com.easynet.util.common;
import static com.easynet.util.common.PrintErrLog;

import java.util.Set;

import com.easynet.util.readXML;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.easynet.impl.LoggerImpl;

/**
 * @author Sagar Umate 
 * This API will get all detail of customer with the help of
 * account number and card no.
 *
 */

@Component
public class Customer360Controller {

	@Autowired
	apiCall1 callApi;
	@Autowired
	GetData getData;

	@Autowired
	CardDetailController cardDetailController;

	@Autowired
	AllAccountsAndCardsApi allAccountsAndCardsAPi;

	@Autowired
	GetRequestUniqueData getRequestUniqueData;

	static Logger logger=LoggerFactory.getLogger(Customer360Controller.class);

	@Autowired
	PropConfiguration propConfiguration;

	/**
	 *This method used for verify customer card or account already exists in system or not.
	 *@param requestData It contain json data of request.
	 *@return this method return the json data with 360 api response code.
	 *@apiNote This method used below API.<br>
	 *	1.allAccountsAndCards to get customer details.<br>
	 * 	2.call database api for verify customer already exists or not.<br>
	 *	
	 * */
	public String ofVerifyAcctCard(String requestData){

		String ls_360ResponeData = "";       
		String ls_responseData = "";
		String ls_actualResData = "";
		String ls_statusCd = "";
		String actualErrMsg = "";//to store error message
		String ls_regWith="";
		String ls_responseCode="";
		String ls_acctNo="";
		LoggerImpl loggerImpl=null;
		String 	ls_langResCodeMsg="";


		try {

			loggerImpl=new LoggerImpl();
			loggerImpl.info(logger,"Preparing requset data and calling API.","IN:ofVerifyAcctCard");
			/*for get the times performance logs.*/
			loggerImpl.generateProfiler("ofVerifyAcctCard");

			/*get data from  json*/
			JSONObject jsonRequest = new JSONObject(requestData);
			JSONObject jsonResponse = null;

			JSONObject cust360ReqJsonData=new JSONObject();
			ls_regWith=jsonRequest.getString("REG_WITH");
			ls_acctNo=jsonRequest.getString("ACCT_NO");

			if((ls_regWith==null || "".equals(ls_regWith))||
					(ls_acctNo==null || "".equals(ls_acctNo))){
				return common.ofGetErrDataJsonArray("99", 
						propConfiguration.getMessageOfResCode("commen.title.99", "Validation Failed."), 
						propConfiguration.getMessageOfResCode("commen.invalid_req_data",""),
						"Null values found in request data.", "Invalid request.", "0", "R");								
			}

			if("A".equals(ls_regWith)){
				cust360ReqJsonData.put("accountNumber",ls_acctNo);
			}else if("C".equals(ls_regWith) || "P".equals(ls_regWith)){                                
				cust360ReqJsonData.put("cardNumber",ls_acctNo);
			}else{
				ls_responseData = common.ofGetErrDataJsonArray("999", 
						propConfiguration.getMessageOfResCode("commen.title.999", "Alert."), 
						propConfiguration.getMessageOfResCode("commen.invalid_req_data",""),
						"Wrong value found in reg_with flag=>"+ls_regWith,"Invalid request.", "0", "R");

				return ls_responseData;
			}

			loggerImpl.debug(logger,"Get details from customer 360 API.", "IN:ofVerifyAcctCard");			
			loggerImpl.startProfiler("customer 360 API Calling.");

			/*Call API to get actual account data .*/
			ls_responseData = allAccountsAndCardsAPi.ofGetCustomer360(cust360ReqJsonData.toString());		

			loggerImpl.startProfiler("Reading response and Calling db API.");
			loggerImpl.debug(logger,"Customer 360 API called.", "IN:ofVerifyAcctCard",ls_responseData);

			/*load response data into json*/
			if (ls_responseData.trim().substring(0, 1).equals("[")) {
				jsonResponse=new JSONArray(ls_responseData).getJSONObject(0);
			}else if (ls_responseData.trim().substring(0, 1).equals("{")) {
				jsonResponse=new JSONObject(ls_responseData);                  
			}

			ls_statusCd = jsonResponse.getString("STATUS");

			if (ls_statusCd != null && !"".equals(ls_statusCd) && "0".equals(ls_statusCd)) {
				/*get API response Data*/
				ls_360ResponeData = jsonResponse.getString("RESPONSE");

				if (ls_360ResponeData.trim().substring(0, 1).equals("[")) {
					/*Set API response Data*/
					jsonRequest.put("ALL_ACCOUNT_DETAIL", new JSONArray(ls_360ResponeData));
				}else if (ls_360ResponeData.trim().substring(0, 1).equals("{")) {
					jsonRequest.put("ALL_ACCOUNT_DETAIL", new JSONObject(ls_360ResponeData));                  
				}else{
					jsonRequest.put("ALL_ACCOUNT_DETAIL", ls_360ResponeData);
				}

				ls_360ResponeData = jsonRequest.toString();

				loggerImpl.debug(logger,"DB API calling.", "IN:ofVerifyAcctCard");

				/*get the data from database procedure*/
				ls_actualResData = getData.ofGetResponseData(ls_360ResponeData);

				loggerImpl.debug(logger,"DB API called.", "IN:ofVerifyAcctCard");

				//return the response data generated from procedure.
				return ls_actualResData;
			} else {

				ls_responseCode=jsonResponse.getString("RESPONSECODE");
				ls_langResCodeMsg=propConfiguration.getResponseCode("allAccountsAndCards."+ls_responseCode);

				if("404".equals(ls_responseCode))
				{
					String ls_message=("A".equals(ls_regWith))? "ac":"card";						
					ls_responseData = common.ofGetErrDataJsonArray(ls_langResCodeMsg,
							propConfiguration.getMessageOfResCode("commen.title."+ls_langResCodeMsg, "Alert."), 
							propConfiguration.getMessageOfResCode("allAccountsAndCards.404."+ls_message,"",""),
							ls_responseData, "No data found.", ls_responseCode, "R");
				}else {
					ls_responseData = common.ofGetErrDataJsonArray(ls_langResCodeMsg,
							propConfiguration.getMessageOfResCode("commen.title."+ls_langResCodeMsg, "Alert."), 
							propConfiguration.getMessageOfResCode("allAccountsAndCards."+ls_responseCode,"","(ENP051)"), 
							ls_responseData,"Currently Service under maintenance so please try later (ENP051).", ls_responseCode, "R");
				}
				//return same response comes from API.
				return ls_responseData;
			}			
		} catch (Exception exception) {
			actualErrMsg = common.ofGetTotalErrString(exception, "");
			loggerImpl.error(logger,"Exception : " + actualErrMsg,"IN:ofVerifyAcctCard");

			ls_responseData = common.ofGetErrDataJsonArray("999", 
					propConfiguration.getMessageOfResCode("commen.title.999", "Alert."),
					propConfiguration.getMessageOfResCode("commen.exception.999","","(ENP015)"),
					exception.getMessage(),"Currently Service under maintenance so please try later (ENP015)", "0", "R");

			return ls_responseData;
		}finally {
			loggerImpl.stopAndPrintOptLogs(logger,"All API called successfully.","IN:ofVerifyAcctCard");
			loggerImpl.info(logger,"Response generated and send to client.", "IN:ofVerifyAcctCard");			
		}
	}

	/**
	 *This method used for generate user ID.
	 *@param ls_requestData it contain json data of request with authorization.
	 *@return this method return the json format string data which comes from database api in case of success.
	 *@apiNote this method used below API.<br>
	 *	1.verify card details.<br>
	 *	2.Database API for generate user id.
	 *
	 * */
	public String ofGenerateUserID(String ls_requestData)
	{		
		String ls_cardVerify="";
		String ls_cardVerifyResponse="";
		String ls_cardVerifyStatus="";
		String actualErrMsg="";
		String ls_authToken="";
		LoggerImpl loggerImpl=null;

		try {
			loggerImpl=new LoggerImpl();
			JSONObject requestData=new JSONObject(ls_requestData);			
			JSONObject josnCardDataResponse=null;
			loggerImpl.info(logger,"Preparing requset data and calling API.","IN:ofGenerateUserID");

			/*for get the times performance logs.*/
			loggerImpl.generateProfiler("ofGenerateUserID");			

			ls_cardVerify=requestData.getString("CARD_VERIFY");

			if(ls_cardVerify!=null &&"Y".equals(ls_cardVerify))
			{
				loggerImpl.debug(logger,"Calling API for verify card.", "IN:ofGenerateUserID");				
				loggerImpl.startProfiler("Calling card verify API.");

				ls_cardVerifyResponse=cardDetailController.ofVerifyCardDetail(ls_requestData);

				loggerImpl.startProfiler("Reading response and calling db API.");				
				loggerImpl.debug(logger,"Card Verify API called.", "IN:ofGenerateUserID");

				if (ls_cardVerifyResponse.trim().substring(0, 1).equals("[")) {
					josnCardDataResponse=new JSONArray(ls_cardVerifyResponse).getJSONObject(0);
				}else {
					josnCardDataResponse = new JSONObject(ls_cardVerifyResponse);                  
				}

				ls_cardVerifyStatus=josnCardDataResponse.getString("STATUS");
				if(ls_cardVerifyStatus!=null && "0".equals(ls_cardVerifyStatus))
				{				
					ls_authToken=josnCardDataResponse.getString("AUTH_TOKEN");
					requestData.put("AUTH_TOKEN", ls_authToken);
					requestData.put("BLOCK_AUTH_STATUS", josnCardDataResponse.getString("BLOCK_AUTH_STATUS"));

					loggerImpl.debug(logger,"Database API calling.", "IN:ofGenerateUserID");
					//send in backend for insert data.
					return getData.ofGetResponseData(requestData.toString());			
				}else
				{
					//return error response for card validation failed.
					return ls_cardVerifyResponse;
				}					
			}else{
				//send in backend for insert data.
				return getData.ofGetResponseData(ls_requestData);
			}
		} catch (Exception exception) {
			actualErrMsg = common.ofGetTotalErrString(exception, "");
			loggerImpl.error(logger,"Exception : " + actualErrMsg,"IN:ofGenerateUserID");

			ls_cardVerifyResponse = common.ofGetErrDataJsonArray("999",
					propConfiguration.getMessageOfResCode("commen.title.999", "Alert."), 
					propConfiguration.getMessageOfResCode("commen.exception.999","","(ENP025)"),
					exception.getMessage(),
					"Currently Service under maintenance so please try later (ENP025).", "0", "R");

			return ls_cardVerifyResponse;
		}finally {
			loggerImpl.stopAndPrintOptLogs(logger,"All API called successfully.","IN:ofGenerateUserID");
			loggerImpl.info(logger,"Response generated and send to client.", "IN:ofGenerateUserID");
			System.gc();
		}
	}

	/**
	 * Used this method for get the user detail from 360 api as per given parameter.
	 * @param as_requestData string format json data.
	 * @return return user detail from 360 API.
	 * @apiNote this method used 360 api.
	 * */
	public String ofGetUserDtl360(String as_requestData) {

		JSONObject 	jsonRequestData=null;
		String 		actualErrMsg="";
		String 		ls_response="";
		JSONArray	jsonCustDtlList=null;
		int 		jsonCustDtlListCnt=0;
		String 		ls_apiResponseData="";
		JSONObject 	jsonAPIResponsetData=null;
		String 		ls_statusCd;
		String		ls_actualResData;
		String	 	ls_responseCode;
		JSONArray	userDtlAPIJsonList;
		JSONArray	userDtlAcctJsonList;
		int 		userDtlAPIJsonListCnt;	
		JSONArray	userDtlCardJsonList;
		JSONArray	userDtldebitCardJsonList;
		JSONArray	userDtlCreditCardJsonList;
		JSONArray	userDtlPrepaidCardJsonList;
		JSONObjectImpl 	objectDebitCard;
		JSONObjectImpl 	objectPrepaidCard;
		JSONObjectImpl 	objectCreditCard;
		JSONObjectImpl  userDtlSubJsonData;
		JSONArray		acctCardSubArray;
		LoggerImpl 		loggerImpl=null;
		String 			ls_langResCodeMsg="";

		try {
			loggerImpl=new LoggerImpl();
			loggerImpl.info(logger,"Preparing requset data and calling API.","IN:ofGetUserDtl360");
			/*for get the times performance logs.*/
			loggerImpl.generateProfiler("ofGetUserDtl360");

			jsonRequestData=new JSONObject(as_requestData);

			jsonCustDtlList=jsonRequestData.getJSONArray("CUSTOMER_DTL_LIST");
			jsonCustDtlListCnt=jsonCustDtlList.length();
			JSONObject	apiRequestData=new JSONObject();

			//check for json array list
			for(int i=0; i < jsonCustDtlListCnt ; i++){

				//get the json object
				JSONObjectImpl 	jsonCustDtl =new JSONObjectImpl(jsonCustDtlList.getJSONObject(i));				

				//check key name and set value as per  key name				
				if(jsonCustDtl.has("CBNUMBER")) {//cbNumber
					apiRequestData.put("cbNumber", jsonCustDtl.getString("CBNUMBER"));

				}else if (jsonCustDtl.has("ACCT_NO")) {//accountNumber
					apiRequestData.put("accountNumber", jsonCustDtl.getString("ACCT_NO"));

				}else if(jsonCustDtl.has("CARD_NO")) {//CARD
					apiRequestData.put("cardNumber", jsonCustDtl.getString("CARD_NO"));

				}else if(jsonCustDtl.has("PASSPORT_NO")) {//passport
					apiRequestData.put("passport", jsonCustDtl.getString("PASSPORT_NO"));

				}else if(jsonCustDtl.has("MOBILE_NO")) {//phone
					apiRequestData.put("phone", jsonCustDtl.getString("MOBILE_NO"));

				}else if(jsonCustDtl.has("TIN_NO")) {//tin
					apiRequestData.put("tin", jsonCustDtl.getString("TIN_NO"));

				}else if(jsonCustDtl.has("EMAIL_ID")) {//email
					apiRequestData.put("email", jsonCustDtl.getString("EMAIL_ID"));

				}else if(jsonCustDtl.has("NID")) {//nid
					apiRequestData.put("nid", jsonCustDtl.getString("NID"));

				}else {
					ls_response = common.ofGetErrDataJsonArray("999",
							propConfiguration.getMessageOfResCode("commen.title.999", "Alert."),
							propConfiguration.getMessageOfResCode("commen.exception.999","","(ENP100)")
							,"Wrong request key send in request data.\n"+as_requestData, 
							"Currently Service under maintenance so please try later (ENP100).", "0", "R");

					return ls_response;
				}

				loggerImpl.debug(logger,"Calling customer 360 API.", "IN:ofGetUserDtl360");
				loggerImpl.startProfiler("Calling customer 360 API "+i);

				/*Call API to get actual account data .*/
				ls_apiResponseData = allAccountsAndCardsAPi.ofGetCustomer360(apiRequestData.toString());		

				loggerImpl.startProfiler("preparing response data "+i);

				loggerImpl.debug(logger,"Customer 360 API called.", "IN:ofGetUserDtl360",ls_apiResponseData);

				/*load response data into json*/
				if (ls_apiResponseData.trim().substring(0, 1).equals("[")) {
					jsonAPIResponsetData=new JSONArray(ls_apiResponseData).getJSONObject(0);
				}else if (ls_apiResponseData.trim().substring(0, 1).equals("{")) {
					jsonAPIResponsetData=new JSONObject(ls_apiResponseData);                  
				}

				ls_statusCd = jsonAPIResponsetData.getString("STATUS");

				if (ls_statusCd != null && !"".equals(ls_statusCd) && "0".equals(ls_statusCd)) {
					/*get API response Data*/
					ls_actualResData = jsonAPIResponsetData.getString("RESPONSE");

					userDtlSubJsonData	=new JSONObjectImpl();

					userDtlAPIJsonList=new JSONArray(ls_actualResData);
					userDtlAPIJsonListCnt=userDtlAPIJsonList.length();

					userDtlAcctJsonList=new JSONArray();
					userDtlCardJsonList=new JSONArray();
					acctCardSubArray=new JSONArray();

					userDtldebitCardJsonList=new JSONArray();
					userDtlCreditCardJsonList=new JSONArray();
					userDtlPrepaidCardJsonList=new JSONArray();

					JSONObject userDtlMainData = new JSONObject();
					userDtlMainData.put("STATUS", userDtlAPIJsonListCnt == 0 ? propConfiguration.getResponseCode("allAccountsAndCards.404") : "0");
					userDtlMainData.put("MESSAGE", userDtlAPIJsonListCnt == 0 ? propConfiguration.getMessageOfResCode("allAccountsAndCards.404", "No Data Found.") : "");
					userDtlMainData.put("COLOR", userDtlAPIJsonListCnt == 0 ? "R" : "G");

					loggerImpl.debug(logger,"Preparing requested json data.", "IN:ofGetUserDtl360");

					//generate the data in our format.
					for(int j=0;j < userDtlAPIJsonListCnt ;j++) {

						JSONObjectImpl	userAPIDtlJson=new JSONObjectImpl(userDtlAPIJsonList.getJSONObject(j));
						JSONObjectImpl userDtlJson = new JSONObjectImpl();

						userDtlJson.put("ACCT_CARD_FLAG", common.ofGetAcctCardFalg(userAPIDtlJson.getString("source")));

						if (userAPIDtlJson.getString("source").equalsIgnoreCase("TRANZWARE")) {
							userDtlJson.put("SOURCE", userAPIDtlJson.getString("source"));
							userDtlJson.put("ACCOUNT_NO", userAPIDtlJson.getString("accountOrCardNo"));
							userDtlJson.put("ACCT_NAME", userAPIDtlJson.getString("fullName"));
							userDtlJson.put("ACCT_TYPE", userAPIDtlJson.getString("productType"));
							userDtlJson.put("ACCT_TYPE_NM", userAPIDtlJson.getString("productName"));
							userDtlJson.put("FATHER_NAME", userAPIDtlJson.getString("fathersName"));
							userDtlJson.put("MOBILE_NO", userAPIDtlJson.getString("phone"));
							userDtlJson.put("BIRTH_DATE", userAPIDtlJson.getString("dob"));
							userDtlJson.put("MOTHER_NAME", userAPIDtlJson.getString("mothersName"));
							userDtlJson.put("APPCUSTOMER_ID", userAPIDtlJson.getString("customerId"));
							userDtlJson.put("EMAIL_ID", userAPIDtlJson.getString("email"));
							userDtlCardJsonList.put(userDtlJson);

						}else {						
							userDtlJson.put("SOURCE", userAPIDtlJson.getString("source"));
							userDtlJson.put("ACCOUNT_NO", userAPIDtlJson.getString("accountOrCardNo"));
							userDtlJson.put("ACCT_NAME", userAPIDtlJson.getString("fullName"));
							userDtlJson.put("ACCT_TYPE", userAPIDtlJson.getString("productType"));
							userDtlJson.put("ACCT_TYPE_NM", userAPIDtlJson.getString("productName"));
							userDtlJson.put("FATHER_NAME", userAPIDtlJson.getString("fathersName"));
							userDtlJson.put("MOBILE_NO", userAPIDtlJson.getString("phone"));
							userDtlJson.put("BIRTH_DATE", userAPIDtlJson.getString("dob"));
							userDtlJson.put("MOTHER_NAME", userAPIDtlJson.getString("mothersName"));
							userDtlJson.put("APPCUSTOMER_ID", userAPIDtlJson.getString("customerId"));
							userDtlJson.put("EMAIL_ID", userAPIDtlJson.getString("email"));

							userDtlAcctJsonList.put(userDtlJson);
						}

						if ("TRANZWARE".equalsIgnoreCase(userAPIDtlJson.getString("source"))) {

							if ("DEBIT CARD".equalsIgnoreCase(userAPIDtlJson.getString("productType"))) {
								objectDebitCard = new JSONObjectImpl();								
								objectDebitCard.put("ACCT_CARD_FLAG", common.ofGetAcctCardFalg(userAPIDtlJson.getString("source")));
								objectDebitCard.put("SOURCE", userAPIDtlJson.getString("source"));
								objectDebitCard.put("ACCOUNT_NO", userAPIDtlJson.getString("accountOrCardNo"));
								objectDebitCard.put("ACCT_NAME", userAPIDtlJson.getString("fullName"));
								objectDebitCard.put("ACCT_TYPE", userAPIDtlJson.getString("productType"));
								objectDebitCard.put("ACCT_TYPE_NM", userAPIDtlJson.getString("productName"));
								objectDebitCard.put("FATHER_NAME", userAPIDtlJson.getString("fathersName"));
								objectDebitCard.put("MOBILE_NO", userAPIDtlJson.getString("phone"));
								objectDebitCard.put("BIRTH_DATE", userAPIDtlJson.getString("dob"));
								objectDebitCard.put("MOTHER_NAME", userAPIDtlJson.getString("mothersName"));
								objectDebitCard.put("APPCUSTOMER_ID", userAPIDtlJson.getString("customerId"));
								objectDebitCard.put("EMAIL_ID", userAPIDtlJson.getString("email"));
								userDtldebitCardJsonList.put(objectDebitCard);

							} else if ("CREDIT CARD".equalsIgnoreCase(userAPIDtlJson.getString("productType"))) {
								objectCreditCard = new JSONObjectImpl();
								objectCreditCard.put("ACCT_CARD_FLAG", common.ofGetAcctCardFalg(userAPIDtlJson.getString("source")));
								objectCreditCard.put("SOURCE", userAPIDtlJson.getString("source"));
								objectCreditCard.put("ACCOUNT_NO", userAPIDtlJson.getString("accountOrCardNo"));
								objectCreditCard.put("ACCT_NAME", userAPIDtlJson.getString("fullName"));
								objectCreditCard.put("ACCT_TYPE", userAPIDtlJson.getString("productType"));
								objectCreditCard.put("ACCT_TYPE_NM", userAPIDtlJson.getString("productName"));
								objectCreditCard.put("FATHER_NAME", userAPIDtlJson.getString("fathersName"));
								objectCreditCard.put("MOBILE_NO", userAPIDtlJson.getString("phone"));
								objectCreditCard.put("BIRTH_DATE", userAPIDtlJson.getString("dob"));
								objectCreditCard.put("MOTHER_NAME", userAPIDtlJson.getString("mothersName"));
								objectCreditCard.put("APPCUSTOMER_ID", userAPIDtlJson.getString("customerId"));
								objectCreditCard.put("EMAIL_ID", userAPIDtlJson.getString("email"));
								userDtlCreditCardJsonList.put(objectCreditCard);

							} else if ("PREPAID CARD".equalsIgnoreCase(userAPIDtlJson.getString("productType"))) {
								objectPrepaidCard = new JSONObjectImpl();
								objectPrepaidCard.put("ACCT_CARD_FLAG", common.ofGetAcctCardFalg(userAPIDtlJson.getString("source")));
								objectPrepaidCard.put("SOURCE", userAPIDtlJson.getString("source"));
								objectPrepaidCard.put("ACCOUNT_NO", userAPIDtlJson.getString("accountOrCardNo"));
								objectPrepaidCard.put("ACCT_NAME", userAPIDtlJson.getString("fullName"));
								objectPrepaidCard.put("ACCT_TYPE", userAPIDtlJson.getString("productType"));
								objectPrepaidCard.put("ACCT_TYPE_NM", userAPIDtlJson.getString("productName"));
								objectPrepaidCard.put("FATHER_NAME", userAPIDtlJson.getString("fathersName"));
								objectPrepaidCard.put("MOBILE_NO", userAPIDtlJson.getString("phone"));
								objectPrepaidCard.put("BIRTH_DATE", userAPIDtlJson.getString("dob"));
								objectPrepaidCard.put("MOTHER_NAME", userAPIDtlJson.getString("mothersName"));
								objectPrepaidCard.put("APPCUSTOMER_ID", userAPIDtlJson.getString("customerId"));
								objectPrepaidCard.put("EMAIL_ID", userAPIDtlJson.getString("email"));
								userDtlPrepaidCardJsonList.put(objectPrepaidCard);
							}
						}
					}

					if (userDtlAPIJsonListCnt > 0) {
						userDtlSubJsonData.put("ACCOUNT_DTL", userDtlAcctJsonList);
						userDtlSubJsonData.put("CARD_DTL", userDtlCardJsonList);

						userDtlSubJsonData.put("DEBIT_CARD", userDtldebitCardJsonList);
						userDtlSubJsonData.put("CREDIT_CARD", userDtlCreditCardJsonList);
						userDtlSubJsonData.put("PREPAID_CARD", userDtlPrepaidCardJsonList);
						acctCardSubArray.put(userDtlSubJsonData);
					}

					userDtlMainData.put("RESPONSE",acctCardSubArray);

					return new JSONArray().put(userDtlMainData).toString();					
				} else {

					ls_responseCode=jsonAPIResponsetData.getString("RESPONSECODE");
					ls_langResCodeMsg=propConfiguration.getResponseCode("allAccountsAndCards."+ls_responseCode);
					if("404".equals(ls_responseCode)){

						return common.ofGetErrDataJsonArray(ls_langResCodeMsg,
								propConfiguration.getMessageOfResCode("commen.title."+ls_langResCodeMsg, "Alert."),
								propConfiguration.getMessageOfResCode("login.data_not_found","",""), 
								ls_apiResponseData,"Your primary authenticator is not found ,Kindly contact customer care.", ls_responseCode, "R");
					}else {
						//return any error in 360 API other than 404.
						return ls_apiResponseData;				
					}
				}
			}			
		}catch(Exception exception){
			actualErrMsg = common.ofGetTotalErrString(exception, "");
			loggerImpl.error(logger,"Exception : " + actualErrMsg,"IN:ofGetUserDtl360");

			ls_response = common.ofGetErrDataJsonArray("999", 
					propConfiguration.getMessageOfResCode("commen.title.999", "Alert."), 
					propConfiguration.getMessageOfResCode("commen.exception.999","","(ENP099)"),
					exception.getMessage(),"Currently Service under maintenance so please try later (ENP099).", "0", "R");

		}finally {
			loggerImpl.stopAndPrintOptLogs(logger,"All API called successfully.","IN:ofGetUserDtl360");
			loggerImpl.info(logger,"Response generated and send to client.", "IN:ofGetUserDtl360");
		}
		return ls_response;
	}
}
