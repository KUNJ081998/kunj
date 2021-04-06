package com.easynet.controller.account;

import static com.easynet.util.common.PrintErrLog;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.easynet.bean.GetRequestUniqueData;
import com.easynet.configuration.PropConfiguration;
import com.easynet.controller.ApiController.AllAccountsAndCardsApi;
import com.easynet.controller.ApiController.AccountAPI.GetLoanAccountDetail;
import com.easynet.controller.ApiController.AccountAPI.GetSoapAccountDetail;
import com.easynet.controller.ApiController.AccountAPI.termDeposit.GetTermDepositAccountDetail;
import com.easynet.controller.ApiController.CardAPI.GetCardDetailController;
import com.easynet.impl.JSONObjectImpl;
import com.easynet.impl.LoggerImpl;
import com.easynet.util.GetData;
import com.easynet.util.apiCall1;
import com.easynet.util.common;

/**
 * @author Mahendra Suthar 
 * This API will get all detail of Operative Account account number and card no.
 *
 */

@Component
public class AccountDetail {

	@Autowired
	apiCall1 callApi;
	@Autowired
	GetData getData;

	@Autowired
	AllAccountsAndCardsApi allAccountsAndCardsApi;

	@Autowired
	GetSoapAccountDetail getSoapAccountDetail;

	@Autowired
	GetCardDetailController getCardDetailController;

	@Autowired
	GetLoanAccountDetail getLoanAccountDetail;

	@Autowired
	GetTermDepositAccountDetail getTermDepositAccountDetail;

	@Autowired
	GetRequestUniqueData getRequestUniqueData;

	@Autowired
	PropConfiguration propConfiguration;

	static Logger logger=LoggerFactory.getLogger(AccountDetail.class);

	/**
	 * Used this method for get the list of account/card for show in dddw.
	 * @param  requestData  Call this method with request parameter and fetch the detail from database.
	 * @return This method return json object of account and card.
	 * @apiNote This method Used below API.<br>
	 * 		1.database API.
	 * date-22-01-2020
	 */
	public String ofGetOperativeAccount(String requestData) {

		String 			ls_responseData = "";
		String 			ls_statusCd = "";
		String 			actualErrMsg = "";// to store error message
		String 			ls_responseCode = "";
		JSONObject 		jsonResponse = null;
		JSONArray 		accountDtl = null;
		JSONArray 		cardDtl = null;
		JSONObject 		objectCard = null;
		JSONObject 		objectPrepairedCard = null;
		JSONObject 		objectCreditCard = null;
		JSONObject 		objectDebitCard = null;
		JSONObject 		objectAccount = null;
		JSONArray 		acctCardRespArray = null;
		JSONArray 		acctDebitCardRespArray =null;
		JSONArray 		acctPrepairedCardRespArray = null;
		JSONArray 		acctCreditCardRespArray =null;
		JSONObjectImpl  acctCardRespObject = null;
		String 			ls_langResCodeMsg="";
		//String			ls_langResCodeMsg="";

		LoggerImpl loggerImpl=new LoggerImpl();
		try {

			loggerImpl.info(logger,"Preparing requset data and calling API.","IN:ofGetOperativeAccount");				
			/*for get the times performance logs.*/
			loggerImpl.generateProfiler("ofGetOperativeAccount");
			loggerImpl.startProfiler("Get User Detail From DB.");

			loggerImpl.debug(logger, "DB API Calling with request Data.", "IN:ofGetOperativeAccount",requestData);
			/* Call DATABASE API to get actual account data instead of 360 API. */
			ls_responseData = getData.ofGetResponseData("PACK_MOB_USER.PROC_GET_USER_ACCT_DTL", requestData);

			loggerImpl.debug(logger, "DB API Called and preparing response data.", "IN:ofGetOperativeAccount",ls_responseData);

			loggerImpl.startProfiler("Preparing response data.");

			/* load response data into json */
			if (ls_responseData.trim().substring(0, 1).equals("[")) {
				jsonResponse = new JSONArray(ls_responseData).getJSONObject(0);
			} else if (ls_responseData.trim().substring(0, 1).equals("{")) {
				jsonResponse = new JSONObject(ls_responseData);
			}

			ls_statusCd = jsonResponse.getString("STATUS");

			if (ls_statusCd != null && !"".equals(ls_statusCd) && "0".equals(ls_statusCd)) {

				int jsonDataLength = jsonResponse.getJSONArray("RESPONSE").length();

				JSONObject ls_actualResData = new JSONObject();

				ls_actualResData.put("STATUS", jsonDataLength == 0 ? propConfiguration.getResponseCode("user_dtl.404") : "0");
				ls_actualResData.put("MESSAGE", jsonDataLength == 0 ? propConfiguration.getMessageOfResCode("user_dtl.404", "No Data Found.") : "");
				ls_actualResData.put("COLOR", jsonDataLength == 0 ? "R" : "G");

				accountDtl 	= new JSONArray();
				cardDtl 	= new JSONArray();				
				acctCardRespArray = new JSONArray();

				acctDebitCardRespArray 		= new JSONArray();
				acctPrepairedCardRespArray 	= new JSONArray();
				acctCreditCardRespArray 	= new JSONArray();

				acctCardRespObject = new JSONObjectImpl();

				for (int i = 0; i < jsonDataLength; i++) {
					JSONObjectImpl account = new JSONObjectImpl(jsonResponse.getJSONArray("RESPONSE").getJSONObject(i));

					if (account.getString("source").equalsIgnoreCase("TRANZWARE")) {
						//if (!"DEBIT CARD".equals(account.getString("productType"))) {
						objectCard = new JSONObjectImpl();
						objectCard.put("SOURCE", account.getString("source"));
						objectCard.put("ACCOUNT_NO", account.getString("accountOrCardNo"));
						objectCard.put("ACCT_NAME", account.getString("fullName"));
						objectCard.put("ACCT_TYPE", account.getString("productType"));
						objectCard.put("ACCT_TYPE_NM", account.getString("productName"));
						objectCard.put("FATHER_NAME", account.getString("fathersName"));
						objectCard.put("MOBILE_NO", account.getString("phone"));
						objectCard.put("BIRTH_DATE", account.getString("dob"));
						objectCard.put("MOTHER_NAME", account.getString("mothersName"));
						objectCard.put("APPCUSTOMER_ID", account.getString("customerId"));
						objectCard.put("EMAIL_ID", account.getString("email"));
						cardDtl.put(objectCard);
						//}
					} else {
						objectAccount = new JSONObjectImpl();
						objectAccount.put("SOURCE", account.getString("source"));
						objectAccount.put("ACCOUNT_NO", account.getString("accountOrCardNo"));
						objectAccount.put("ACCT_NAME", account.getString("fullName"));
						objectAccount.put("ACCT_TYPE", account.getString("productType"));
						objectAccount.put("ACCT_TYPE_NM", account.getString("productName"));
						objectAccount.put("FATHER_NAME", account.getString("fathersName"));
						objectAccount.put("MOBILE_NO", account.getString("phone"));
						objectAccount.put("BIRTH_DATE", account.getString("dob"));
						objectAccount.put("MOTHER_NAME", account.getString("mothersName"));
						objectAccount.put("APPCUSTOMER_ID", account.getString("customerId"));
						objectAccount.put("EMAIL_ID", account.getString("email"));
						accountDtl.put(objectAccount);
					}

					if ("TRANZWARE".equalsIgnoreCase(account.getString("source"))) {

						if ("DEBIT CARD".equalsIgnoreCase(account.getString("productType"))) {
							objectDebitCard = new JSONObjectImpl();
							objectDebitCard.put("SOURCE", account.getString("source"));
							objectDebitCard.put("ACCOUNT_NO", account.getString("accountOrCardNo"));
							objectDebitCard.put("ACCT_NAME", account.getString("fullName"));
							objectDebitCard.put("ACCT_TYPE", account.getString("productType"));
							objectDebitCard.put("ACCT_TYPE_NM", account.getString("productName"));
							objectDebitCard.put("FATHER_NAME", account.getString("fathersName"));
							objectDebitCard.put("MOBILE_NO", account.getString("phone"));
							objectDebitCard.put("BIRTH_DATE", account.getString("dob"));
							objectDebitCard.put("MOTHER_NAME", account.getString("mothersName"));
							objectDebitCard.put("APPCUSTOMER_ID", account.getString("customerId"));
							objectDebitCard.put("EMAIL_ID", account.getString("email"));
							acctDebitCardRespArray.put(objectDebitCard);

						} else if ("CREDIT CARD".equalsIgnoreCase(account.getString("productType"))) {
							objectCreditCard = new JSONObjectImpl();
							objectCreditCard.put("SOURCE", account.getString("source"));
							objectCreditCard.put("ACCOUNT_NO", account.getString("accountOrCardNo"));
							objectCreditCard.put("ACCT_NAME", account.getString("fullName"));
							objectCreditCard.put("ACCT_TYPE", account.getString("productType"));
							objectCreditCard.put("ACCT_TYPE_NM", account.getString("productName"));
							objectCreditCard.put("FATHER_NAME", account.getString("fathersName"));
							objectCreditCard.put("MOBILE_NO", account.getString("phone"));
							objectCreditCard.put("BIRTH_DATE", account.getString("dob"));
							objectCreditCard.put("MOTHER_NAME", account.getString("mothersName"));
							objectCreditCard.put("APPCUSTOMER_ID", account.getString("customerId"));
							objectCreditCard.put("EMAIL_ID", account.getString("email"));
							acctCreditCardRespArray.put(objectCreditCard);

						} else if ("PREPAID CARD".equalsIgnoreCase(account.getString("productType"))) {
							objectPrepairedCard = new JSONObjectImpl();
							objectPrepairedCard.put("SOURCE", account.getString("source"));
							objectPrepairedCard.put("ACCOUNT_NO", account.getString("accountOrCardNo"));
							objectPrepairedCard.put("ACCT_NAME", account.getString("fullName"));
							objectPrepairedCard.put("ACCT_TYPE", account.getString("productType"));
							objectPrepairedCard.put("ACCT_TYPE_NM", account.getString("productName"));
							objectPrepairedCard.put("FATHER_NAME", account.getString("fathersName"));
							objectPrepairedCard.put("MOBILE_NO", account.getString("phone"));
							objectPrepairedCard.put("BIRTH_DATE", account.getString("dob"));
							objectPrepairedCard.put("MOTHER_NAME", account.getString("mothersName"));
							objectPrepairedCard.put("APPCUSTOMER_ID", account.getString("customerId"));
							objectPrepairedCard.put("EMAIL_ID", account.getString("email"));
							acctPrepairedCardRespArray.put(objectPrepairedCard);
						}
					}
				}

				if (jsonDataLength > 0) {
					acctCardRespObject.put("ACCOUNT_DTL", accountDtl);
					acctCardRespObject.put("CARD_DTL", cardDtl);

					acctCardRespObject.put("DEBIT_CARD", acctDebitCardRespArray);
					acctCardRespObject.put("CREDIT_CARD", acctCreditCardRespArray);
					acctCardRespObject.put("PREPAID_CARD", acctPrepairedCardRespArray);
					acctCardRespArray.put(acctCardRespObject);
				}
				ls_actualResData.put("RESPONSE", acctCardRespArray);

				// return the interpreted response.
				return ls_actualResData.toString();
			} else {

				ls_responseCode = jsonResponse.getString("RESPONSECODE");
				ls_langResCodeMsg=propConfiguration.getResponseCode("user_dtl."+ls_responseCode);				

				ls_responseData = common.ofGetErrDataJsonArray(ls_langResCodeMsg,
						propConfiguration.getMessageOfResCode("commen.title."+ls_langResCodeMsg, "Validation Failed.")
						,propConfiguration.getMessageOfResCode("user_dtl."+ls_responseCode, "","(ENP031)"),
						ls_responseData, "Currently Service under maintenance so please try later (ENP031).", ls_responseCode, "R");

				loggerImpl.debug(logger, "API return response with error code :"+ls_responseCode,"IN:ofGetOperativeAccount");
				// return same response comes from API.
				return ls_responseData;
			}

		} catch (Exception exception) {
			actualErrMsg = common.ofGetTotalErrString(exception, "");
			loggerImpl.error(logger,"Exception : " + actualErrMsg,"IN:ofGetOperativeAccount");
			ls_responseData = common.ofGetErrDataJsonArray("999", 
					propConfiguration.getMessageOfResCode("commen.title.999", "Alert."),
					propConfiguration.getMessageOfResCode("commen.exception.999","","(ENP032)")
					, exception.getMessage(), "Currently Service under maintenance so please try later (ENP032).", "0", "R");
			return ls_responseData;
		} finally {
			/*stop the profiler and print logs.*/
			loggerImpl.stopAndPrintOptLogs(logger,"All API called successfully", "IN:ofGetOperativeAccount");				
			loggerImpl.info(logger,"Response generated and send to client.", "IN:ofGetOperativeAccount");
		}
	}

	/**
	 * @deprecated Do not call this API for get details because detail will be return from database.
	 * @date-22-01-2020
	 * 
	 * @param requestData - Call this method with request parameter of customer 360 API.
	 *                    
	 * @return -This method return json object of account and card with segregated
	 *         format as per configuration found.
	 * 
	 */
	@Deprecated
	public String ofGetTypeWiseListAccount(String requestData) {

		String ls_allAccountsAndCards = "";
		String ls_responseData = "";
		String ls_statusCd = "";
		String actualErrMsg = "";// to store error message
		String ls_cbNumber = "";
		JSONObject errorjson = null;// for store error json
		String ls_responseCode = "";
		int customerJsonList = 0;// count of array
		JSONArray customerJsonArray = null;// customerID Json list array
		JSONObject customerJsonObject = null;// customerID object.
		int i = 0;
		String returnValue = "";

		try {
			/* get data from json */
			JSONObject jsonRequest = new JSONObject(requestData);
			JSONObject jsonResponse = null;

			JSONObject operativeAccount = new JSONObject();

			customerJsonArray = jsonRequest.getJSONArray("CUSTOMER_DTL");
			customerJsonList = customerJsonArray.length();

			for (i = 0; i < customerJsonList; i++) {
				customerJsonObject = customerJsonArray.getJSONObject(i);

				ls_cbNumber = customerJsonObject.getString("CBNUMBER");

				operativeAccount.put("cbNumber", ls_cbNumber);
				operativeAccount.put("allProducts", "Y");

				/* Call API to get actual account data . */
				ls_responseData = allAccountsAndCardsApi.ofGetCustomer360(operativeAccount.toString());

				/* load response data into json */
				if (ls_responseData.trim().substring(0, 1).equals("[")) {
					jsonResponse = new JSONArray(ls_responseData).getJSONObject(0);
				} else if (ls_responseData.trim().substring(0, 1).equals("{")) {
					jsonResponse = new JSONObject(ls_responseData);
				} else {
					jsonResponse = new JSONObject(ls_responseData);
				}

				ls_statusCd = jsonResponse.getString("STATUS");

				if (ls_statusCd != null && !"".equals(ls_statusCd) && "0".equals(ls_statusCd)) {
					/* get API response Data */
					ls_allAccountsAndCards = jsonResponse.getString("RESPONSE");

					if (ls_allAccountsAndCards.trim().substring(0, 1).equals("[")) {
						/* Set API response Data */
						jsonRequest.put("ALL_ACCOUNT_DETAIL", new JSONArray(ls_allAccountsAndCards));
					} else if (ls_allAccountsAndCards.trim().substring(0, 1).equals("{")) {
						jsonRequest.put("ALL_ACCOUNT_DETAIL", new JSONObject(ls_allAccountsAndCards));
					} else {
						jsonRequest.put("ALL_ACCOUNT_DETAIL", ls_allAccountsAndCards);
					}

					// return the interpreted response in procedure
					returnValue = getData.ofGetResponseData(jsonRequest.toString());

					break;
				} else {

					if (ls_responseData.trim().substring(0, 1).equals("[")) {
						errorjson = new JSONArray(ls_responseData).getJSONObject(0);
					} else if (ls_responseData.trim().substring(0, 1).equals("{")) {
						errorjson = new JSONObject(ls_responseData);
					}
					ls_responseCode = errorjson.getString("RESPONSECODE");
					if ("404".equals(ls_responseCode)) {
						String ls_message = "No Data Found.";
						returnValue = common.ofGetErrDataJsonArray("99", "Validation Failed.", ls_message,
								ls_responseData, "", ls_responseCode, "R");
					} else {
						returnValue = common.ofGetErrDataJsonArray("999", "Alert",
								"Currently Service under maintenance so please try later (ENP031).", ls_responseData,
								"", "0", "R");
						break;
					}
				}
			}
			return returnValue;

		} catch (Exception exception) {
			actualErrMsg = common.ofGetTotalErrString(exception, "");
			PrintErrLog(getRequestUniqueData.getUniqueNumber(), "ofGetTypeWiseListAccount Exception : " + actualErrMsg);
			ls_responseData = common.ofGetErrDataJsonArray("999", "Alert",
					"Currently Service under maintenance so please try later (ENP032)", exception.getMessage(), "", "0", "R");
			return ls_responseData;
		} finally {
			System.gc();
		}
	}

	/**
	 * This method return the balance and account detail of account/Card as per
	 * source of account.
	 * 
	 * @param requestData json Format string data. 	
	 *
	 * @return Unique response data if somes keys are missing in respective api the
	 *                empty string will be set in API. 
	 * @Date-12/02/2021
	 * @apiNote This Method used below API. <br>
	 * 			1.getAccountDetails for finacle and ababil.<br>
	 * 			2.getClientCardDetails  for get card details<br>
	 * 			3.getCreditCardDetails	for get balance.<br>
	 * 
	 */
	public String ofGetBalAcctInfo(String requestData) {
		String ls_responseData = "";
		String ls_source = "";
		String ls_resFlag = "";
		String actualErrMsg = "";
		LoggerImpl loggerImpl=new LoggerImpl();
		try {

			JSONObject jsonRequestObject = new JSONObject(requestData);
			ls_source = jsonRequestObject.getString("SOURCE");
			ls_resFlag = jsonRequestObject.getString("RES_FLAG");
			jsonRequestObject.put("CURRENCYCODE", "BDT");

			loggerImpl.debug(logger,"identifying source and calling respective API.", "IN:ofGetBalAcctInfo");
			
			if (ls_source != null && "FINACLE".equalsIgnoreCase(ls_source)) {
				ls_responseData = getSoapAccountDetail.GetAccountDetails(jsonRequestObject.toString());

			} else if (ls_source != null && "ABABIL".equalsIgnoreCase(ls_source)) {
				// call getaccountDetail API for ababil as per sajid sir.
				ls_responseData = getSoapAccountDetail.GetAccountDetails(jsonRequestObject.toString());				

			} else if (ls_source != null && "TRANZWARE".equalsIgnoreCase(ls_source)) {

				if (ls_resFlag != null && "D".equals(ls_resFlag)) {
					ls_responseData = getCardDetailController.ofGetCreditCardDetail(jsonRequestObject.toString());

				} else if (ls_resFlag != null && "B".equals(ls_resFlag)) {
					ls_responseData = getCardDetailController.getCardBalance(jsonRequestObject.toString());

				} else {
					ls_responseData = common.ofGetErrDataJsonArray("999", 
							propConfiguration.getMessageOfResCode("commen.title.999", "Alert."),
							propConfiguration.getMessageOfResCode("commen.invalid_req_data","","(ENP038)"),
							"Wrong response flag found.", "Currently Service under maintenance so please try later (ENP038)", "", "R");
				}
			} else {
				ls_responseData = common.ofGetErrDataJsonArray("999",
						propConfiguration.getMessageOfResCode("commen.title.999", "Alert."),
						propConfiguration.getMessageOfResCode("commen.invalid_source","","(ENP037)"),
						"Wrong source type found.","Currently Service under maintenance so please try later (ENP037)", "", "R");				
			}

			loggerImpl.debug(logger,"Source wise API called successfully.", "IN:ofGetBalAcctInfo");

		} catch (Exception exception) {
			actualErrMsg = common.ofGetTotalErrString(exception, "");
			loggerImpl.error(logger, "Exception : " + actualErrMsg,"IN:ofGetBalAcctInfo");
			
			ls_responseData = common.ofGetErrDataJsonArray("999", 
					propConfiguration.getMessageOfResCode("commen.title.999", "Alert."),
					propConfiguration.getMessageOfResCode("commen.exception.999","","(ENP044)")
					,exception.getMessage(), "Currently Service under maintenance so please try later (ENP044).", "0", "R");
			
			return ls_responseData;
		}
		return ls_responseData;
	}

	/**
	 * This Method return the loan account detail of account as per source.
	 * @apiNote This method used below API.
	 * 			1.getLoanAccountDetails
	 * @param requestData json format String request Data.
	 * @param SOURCE the sounce of account/card.This key in json object.
	 * @return return the loan detail in json format. 
	 * 
	 * Date-12/02/2020 12.55PM
	 */
	public String ofGetLoanAcctDetail(String requestData) {

		String ls_sourceCode = "";
		String ls_responseData = "";
		String actualErrMsg = "";
		LoggerImpl loggerImpl=new LoggerImpl();
		try {
			JSONObject jsonRequestData = new JSONObject(requestData);
			ls_sourceCode = jsonRequestData.getString("SOURCE");

			loggerImpl.debug(logger,"Identifying source and calling respective API.", "IN:ofGetLoanAcctDetail");

			if (ls_sourceCode != null && "FINACLE".equalsIgnoreCase(ls_sourceCode)) {
				// Call method for get loan detail
				ls_responseData = getLoanAccountDetail.getAccountDetail(requestData);

			} else if (ls_sourceCode != null && "ABABIL".equalsIgnoreCase(ls_sourceCode)) {
				// since no api found.
				ls_responseData = "[{\"error\":\"since no api found\"}]";

			} else if (ls_sourceCode != null && "TRANZWARE".equalsIgnoreCase(ls_sourceCode)) {
				// since no api found.
				ls_responseData = "[{\"error\":\"since no api found\"}]";
			} else {				
				ls_responseData = common.ofGetErrDataJsonArray("999",
						propConfiguration.getMessageOfResCode("commen.title.999", "Alert."),
						propConfiguration.getMessageOfResCode("commen.invalid_source","","(ENP110)"),
						"Wrong source type found.","Currently Service under maintenance so please try later (ENP110).", "", "R");
			}
			loggerImpl.debug(logger,"Source wise API called successfully.", "IN:ofGetLoanAcctDetail");

		} catch (Exception exception) {
			actualErrMsg = common.ofGetTotalErrString(exception, "");
			loggerImpl.error(logger, "Exception : " + actualErrMsg,"IN:ofGetLoanAcctDetail");

			ls_responseData = common.ofGetErrDataJsonArray("999", propConfiguration.getMessageOfResCode("commen.title.999", "Alert."),
					propConfiguration.getMessageOfResCode("commen.exception.999","","(ENP091)"), exception.getMessage(),
					"Currently Service under maintenance so please try later (ENP091).", "0", "R");
			
			return ls_responseData;
		}
		return ls_responseData;
	}

	/**
	 * This method return the term deposit account detail as per account source.
	 * 
	 * @param requestData String format json request data
	 * @param SOURCE source of account/card.This key in json data objet.
	 * @return return the list of term deposit account.
	 * @apiNote This method used below API.
	 * 			1.getFDRAccountsSummary for finacle source.
	 * @Date -12/02/2021 01.13PM
	 */
	public String ofGetTermDepositAccount(String requestData) {

		String ls_sourceCode = "";
		String ls_responseData = "";
		String actualErrMsg = "";
		LoggerImpl loggerImpl=new LoggerImpl();

		try {
			JSONObject jsonRequestData = new JSONObject(requestData);
			ls_sourceCode = jsonRequestData.getString("SOURCE");

			loggerImpl.debug(logger,"Identifying source and calling respective API.", "IN:ofGetTermDepositAccount");

			if (ls_sourceCode != null && "FINACLE".equalsIgnoreCase(ls_sourceCode)) {
				// Call method for get term deposit account detail
				ls_responseData = getTermDepositAccountDetail.getFDRAccountsSummary(requestData);

			} else if (ls_sourceCode != null && "ABABIL".equalsIgnoreCase(ls_sourceCode)) {
				// since no api found.
				ls_responseData = "[{\"error\":\"since no api found\"}]";

			} else if (ls_sourceCode != null && "TRANZWARE".equalsIgnoreCase(ls_sourceCode)) {
				// since no api found.
				ls_responseData = "[{\"error\":\"since no api found\"}]";

			} else {				
				ls_responseData = common.ofGetErrDataJsonArray("999",
						propConfiguration.getMessageOfResCode("commen.title.999", "Alert."),
						propConfiguration.getMessageOfResCode("commen.invalid_source","","(ENP111)"),
						"Wrong source type found.","Currently Service under maintenance so please try later (ENP111).", "", "R");

			}
			loggerImpl.debug(logger,"Source wise API called successfully.", "IN:ofGetTermDepositAccount");

		} catch (Exception exception) {
			actualErrMsg = common.ofGetTotalErrString(exception, "");
			loggerImpl.error(logger, "Exception : " + actualErrMsg,"IN:ofGetTermDepositAccount");				
			
			ls_responseData = common.ofGetErrDataJsonArray("999",
					propConfiguration.getMessageOfResCode("commen.title.999", "Alert."),
					propConfiguration.getMessageOfResCode("commen.exception.999","","(ENP092)"), exception.getMessage(),
					"Currently Service under maintenance so please try later (ENP092).", "0", "R");			
			return ls_responseData;
		}
		return ls_responseData;
	}
}
