package com.easynet.controller.cardAPI;

import com.easynet.bean.GetRequestUniqueData;
import com.easynet.configuration.PropConfiguration;
import com.easynet.controller.ApiController.CardAPI.GetCardDetailController;
import com.easynet.controller.ApiController.CardAPI.VerifyCardPinController;
import com.easynet.util.GetData;
import com.easynet.util.apiCall1;
import com.easynet.util.common;
import com.easynet.util.readXML;

import static com.easynet.util.common.PrintErrLog;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.easynet.impl.LoggerImpl;

/**
 * @author Sagar Umate
 * @Date 16/01/2020 The All card related operation done through this controller.
 */

@Component
public class CardDetailController {

	@Autowired
	apiCall1 apicall1;

	@Autowired
	VerifyCardPinController verifyCardPinController;

	@Autowired
	GetData getData;
	@Autowired
	GetRequestUniqueData getRequestUniqueData;

	@Autowired
	GetCardDetailController getCardDetailController;

	@Autowired
	PropConfiguration propConfiguration;
	
	static Logger logger=LoggerFactory.getLogger(CardDetailController.class);

	/**
	 * This method are used for verify card details
	 * @param a_input  It contain request data value with authorization data.
	 * @return  Return 0 as status code when all API successfully called.
	 *          if validation of data failed then return code values as 99.
	 * @apiNote This method used below API. <br>
	 * 	1.Call database APi for check card is already block or not.<br>
	 *  2.VerifyCardPIN call this api for verify card pin<br>
	 *  3.getClientCardDetails call this api for get expiry date.<br>
	 *  4.if verification failed then call database API for update the block status.
	 * 
	 */    
	public String ofVerifyCardDetail(String a_input) {
		String 		ls_cardNo = "", ls_cardPin = "", ls_expiryDate = "", ls_ResponseexpiryDate = "";
		String 		ls_apiRequestData = "", ls_apiResponseData = "", ls_statusCd = "";
		String  	ls_responseCd = "", ls_responseMessage = "";
		String 		ls_apiURL="",ls_apiMethodType="";
		String 		actualErrMsg="";

		JSONObject 	apiRequestData = new JSONObject();
		JSONObject 	apiResponseData = null;//used for read api response data
		JSONObject 	apiActualResponseData = null;//used for read actual response data
		JSONObject 	expiryApiresponseData = null; // used to read expiry date verify API.
		String 	   	ls_DBresponseData="";
		JSONObject 	dbResponseObject;
		String 		ls_updateStatus="";
		JSONObject	cardJsonrequestData;
		String		ls_msg_append_str="";
		LoggerImpl 	loggerImpl=null;
		String		ls_langResCodeMsg="";

		try {
			loggerImpl=new LoggerImpl();
			loggerImpl.info(logger,"Preparing requset data and calling API.","IN:ofVerifyCardDetail");

			JSONObject requestData = new JSONObject(a_input);
			cardJsonrequestData=requestData.getJSONObject("CARD_DETAIL");

			ls_cardNo = cardJsonrequestData.getString("ACCT_NO");
			ls_cardPin = cardJsonrequestData.getString("CARD_PIN");
			ls_expiryDate = cardJsonrequestData.getString("EXPIRY_DATE");
			//ls_authorization=cardJsonrequestData.getJSONObject("AUTHORIZATION").toString();//it contain json value 

			//if validation fail then return 
			if(((ls_cardNo==null)|| "".equals(ls_cardNo))||
					((ls_cardPin==null)|| "".equals(ls_cardPin))||
					((ls_expiryDate == null)|| "".equals(ls_expiryDate))){

				return  common.ofGetErrDataJsonArray("99",
						propConfiguration.getMessageOfResCode("commen.title.99", "Validation Failed."),
						propConfiguration.getMessageOfResCode("commen.invalid_req_data",""),
						"Null values found in request data.","Invalid Request.", "", "R");			
			}

			/*
			 *This below process check for card is blocked or not.if card is 
			 *blocked then API not called and return from here. 
			 * */
			loggerImpl.debug(logger,"User block process started.","IN:ofVerifyCardDetail");

			/* for get the times performance logs.*/
			loggerImpl.generateProfiler("ofVerifyCardDetail");
			loggerImpl.startProfiler("Preparing request data and checking user block status.");

			requestData.put("ACTUAL_ACTION", requestData.getString("ACTION"));
			requestData.put("BLOCK_ACTIVITY_DECRIPTION","Invalid card details.");
			requestData.put("ACTION", "BLOCK_CP_PROCESS");

			requestData.put("BLOCK_PROCESS_TYPE", "VERIFY_BLOCK_PROCESS");
			ls_DBresponseData= getData.ofGetResponseData(requestData.toString());

			if (ls_DBresponseData.trim().substring(0, 1).equals("[")) {
				dbResponseObject=new JSONArray(ls_DBresponseData).getJSONObject(0);
			}else {
				dbResponseObject = new JSONObject(ls_DBresponseData);                  
			}

			ls_statusCd=dbResponseObject.getString("STATUS");
			if(ls_statusCd!=null && !"0".equals(ls_statusCd)){
				return ls_DBresponseData;
			}
			loggerImpl.debug(logger,"User block process completed.","IN:ofVerifyCardDetail");
			//end block check logic

			//Make request data for verify card pin.
			apiRequestData.put("accountOrCardNumber", ls_cardNo);
			apiRequestData.put("cardPIN", ls_cardPin);

			ls_apiRequestData = apiRequestData.toString();
			ls_apiURL=readXML.getXmlData("root>VERIFY_CARD>API_URL");
			ls_apiMethodType=readXML.getXmlData("root>VERIFY_CARD>METHOD_TYPE");

			loggerImpl.debug(logger,"Calling API for verify card PIN.","IN:ofVerifyCardDetail");
			loggerImpl.startProfiler("Card Verify API Calling.");

			//Call SOAP API to verify card pin details.
			ls_apiResponseData=verifyCardPinController.verifyCardPin(ls_apiRequestData);			

			loggerImpl.debug(logger,"Reading response and preparing data for expiry date verify API.","IN:ofVerifyCardDetail");
			loggerImpl.startProfiler("Preparing date for get expiry date API.");

			if (ls_apiResponseData.trim().substring(0, 1).equals("[")) {
				apiResponseData=new JSONArray(ls_apiResponseData).getJSONObject(0);
			}else {
				apiResponseData = new JSONObject(ls_apiResponseData);                  
			}

			ls_statusCd = apiResponseData.getString("STATUS");

			//Check status code from API response.        
			if (ls_statusCd != null && !"".equals(ls_statusCd) && "0".equals(ls_statusCd)) {
				//Read the actual response message from response object.
				apiActualResponseData = apiResponseData.getJSONArray("RESPONSE").getJSONObject(0);			
				ls_responseCd = apiActualResponseData.getString("responseCode");
				ls_responseMessage = apiActualResponseData.getString("responseMessage");

				/*
				 *Check for card pin API response.
				 *if response code =100 then card pin is verified. 
				 * */
				if (ls_responseCd != null && !"".equals(ls_responseCd) && "100".equals(ls_responseCd)) {

					apiRequestData.remove("accountOrCardNumber");
					apiRequestData.remove("cardPIN");

					//Make request data for verify Expiry Data.
					apiRequestData.put("ACCT_NO", ls_cardNo);
					apiRequestData.put("CURRENCYCODE",readXML.getXmlData("root>DEF_CUR_CD>CARD_CUR_CD"));

					ls_apiRequestData = apiRequestData.toString();
					ls_apiURL=readXML.getXmlData("root>VERIFY_EXP_DATE>API_URL");
					ls_apiMethodType=readXML.getXmlData("root>VERIFY_EXP_DATE>METHOD_TYPE");

					loggerImpl.debug(logger,"Calling API to get expiry date.","IN:ofVerifyCardDetail");
					loggerImpl.startProfiler("Expiry date API Calling.");

					//Call API to verify expiry date details.
					ls_apiResponseData=getCardDetailController.ofGetCreditCardDetail(ls_apiRequestData);					
					loggerImpl.startProfiler("Verifing exp. date and generating response.");

					loggerImpl.debug(logger,"API called and expiry date verifing.","IN:ofVerifyCardDetail");

					if (ls_apiResponseData.trim().substring(0, 1).equals("[")) {
						apiResponseData=new JSONArray(ls_apiResponseData).getJSONObject(0);
					}else if(ls_apiResponseData.trim().substring(0, 1).equals("{")) {
						apiResponseData = new JSONObject(ls_apiResponseData);                  
					}

					ls_statusCd = apiResponseData.getString("STATUS");
										
					//Check status code from api response.        
					if (ls_statusCd != null && !"".equals(ls_statusCd) && "0".equals(ls_statusCd)) {
						
						ls_responseCd = apiResponseData.getString("RESPONSECODE");
						ls_responseMessage = apiResponseData.getString("RESPONSEMESSAGE");

						//if response code =100 then successful operation
						if (ls_responseCd != null && !"".equals(ls_responseCd) && "100".equals(ls_responseCd)) {

							//Read the actual response message from response object.
							expiryApiresponseData = apiResponseData.getJSONArray("RESPONSE").getJSONObject(0);							
							//get expire date													
							ls_ResponseexpiryDate = expiryApiresponseData.getString("ACCTCLSDATE");							

							if (ls_ResponseexpiryDate != null && ls_expiryDate!=null && ls_expiryDate.equals(ls_ResponseexpiryDate)) {

								/*check for need to update the block status in from here or not ,
								 * this is write due to directly call this API without request.
								 * For other case block count will be updated using respective procedure.*/
								ls_updateStatus=requestData.optString("UPDATE_BLOCK_STATUS","N");

								if("Y".equalsIgnoreCase(ls_updateStatus)) {

									loggerImpl.debug(logger,"User blocked process started.","IN:ofVerifyCardDetail");
									//update blocked status of card.							
									requestData.put("BLOCK_PROCESS_TYPE", "UPDATE_BLOCK_STATUS");
									requestData.put("BLOCK_AUTH_STATUS", "Y");
									ls_DBresponseData= getData.ofGetResponseData(requestData.toString());								

									if (ls_DBresponseData.trim().substring(0, 1).equals("[")) {
										dbResponseObject=new JSONArray(ls_DBresponseData).getJSONObject(0);
									}else {
										dbResponseObject = new JSONObject(ls_DBresponseData);                  
									}									
									ls_statusCd=dbResponseObject.getString("STATUS");

									loggerImpl.debug(logger,"User blocked process completed.","IN:ofVerifyCardDetail");

									if(ls_statusCd!=null && !"0".equals(ls_statusCd)) {																
										return ls_DBresponseData;									
									}																	
								}

								//return the success message
								JSONObject jsonResponseObj=new JSONObject();
								JSONObject jsonSubResponseObj=new JSONObject();
								JSONArray jsonResponseArray=new JSONArray(); 
								JSONArray jsonResponseMainArray=new JSONArray(); 

								jsonResponseObj.put("STATUS", "0");
								jsonResponseObj.put("MESSAGE", "Card Verified.");
								jsonResponseObj.put("COLOR", "G");
								jsonResponseObj.put("AUTH_TOKEN", "value");
								jsonResponseObj.put("BLOCK_AUTH_STATUS", "Y");//card verified successfully and update block status.

								jsonSubResponseObj.put("TITLE", "Alert.");
								jsonSubResponseObj.put("MESSAGE", "Card Verified.");
								jsonResponseArray.put(jsonSubResponseObj);

								jsonResponseObj.put("RESPONSE", jsonResponseArray);								
								jsonResponseMainArray.put(jsonResponseObj);

								return jsonResponseMainArray.toString();

							} else { //expiry date failed case 							

								//check for update blocked status of card.							
								requestData.put("BLOCK_PROCESS_TYPE", "UPDATE_BLOCK_STATUS");
								requestData.put("BLOCK_AUTH_STATUS", "N");
								ls_DBresponseData= getData.ofGetResponseData(requestData.toString());								

								if (ls_DBresponseData.trim().substring(0, 1).equals("[")) {
									dbResponseObject=new JSONArray(ls_DBresponseData).getJSONObject(0);
								}else {
									dbResponseObject = new JSONObject(ls_DBresponseData);                  
								}

								ls_statusCd=dbResponseObject.getString("STATUS");
								ls_msg_append_str=dbResponseObject.getString("MESSAGE");
								if(ls_msg_append_str.endsWith(".")){
									ls_msg_append_str=ls_msg_append_str.substring(0, ls_msg_append_str.length() -1 );
								}

								//error msg when expiry date not matched.
								return  common.ofGetErrDataJsonArray(ls_statusCd, 
										propConfiguration.getMessageOfResCode("commen.title."+ls_statusCd, "Alert."),
										ls_msg_append_str+"(ENP020).",
										"Invalid Expiry Date.", "Currently Service under maintenance so please try later (ENP020).", "", "R");								
							}

						} else { 
							//return the error message
							ls_langResCodeMsg=propConfiguration.getResponseCode("getClientCardDetails."+ls_responseCd);
							
							return common.ofGetErrDataJsonArray(ls_langResCodeMsg, 
									propConfiguration.getMessageOfResCode("commen.title."+ls_langResCodeMsg, "Alert."), 
									propConfiguration.getMessageOfResCode("getClientCardDetails."+ls_responseCd,"","(ENP090)"),
									ls_apiResponseData,"Invalid Card Details(ENP090).", "", "R");							
						}

					} else {
						//same value return from api response when status not equal to 0.	
						return ls_apiResponseData;						
					}

				} else {

					//check for update blocked status of card.
					if("000".equals(ls_responseCd)){

						requestData.put("BLOCK_PROCESS_TYPE", "UPDATE_BLOCK_STATUS");
						requestData.put("BLOCK_AUTH_STATUS", "N");
						ls_DBresponseData= getData.ofGetResponseData(requestData.toString());								

						if (ls_DBresponseData.trim().substring(0, 1).equals("[")){
							dbResponseObject=new JSONArray(ls_DBresponseData).getJSONObject(0);
						}else {
							dbResponseObject = new JSONObject(ls_DBresponseData);                  
						}

						ls_statusCd=dbResponseObject.getString("STATUS");
						ls_msg_append_str=dbResponseObject.getString("MESSAGE");
						if(ls_msg_append_str.endsWith("."))
						{
							ls_msg_append_str=ls_msg_append_str.substring(0, ls_msg_append_str.length() -1 );
						}
						return common.ofGetErrDataJsonArray(ls_statusCd, 
								propConfiguration.getMessageOfResCode("commen.title."+ls_statusCd, "Alert."), 
								ls_msg_append_str+"(ENP021).",
								apiActualResponseData.toString(), "Invalid Card Details(ENP021).", "", "R");											
					}
					//return the error message
					ls_langResCodeMsg=propConfiguration.getResponseCode("getClientCardDetails."+ls_responseCd);
					
					return common.ofGetErrDataJsonArray(ls_langResCodeMsg, 
							propConfiguration.getMessageOfResCode("commen.title."+ls_langResCodeMsg, "Alert."),
							propConfiguration.getMessageOfResCode("getClientCardDetails."+ls_responseCd,"","(ENP023)"),
							apiActualResponseData.toString(),"Invalid Card Details(ENP023).", "", "R");
				}			
			} else {
				//same value return from api response when status not equal to 0.	
				return ls_apiResponseData;
								
			}
		} catch (Exception exception) {
			actualErrMsg = common.ofGetTotalErrString(exception, "");
			loggerImpl.error(logger,"Exception : " + actualErrMsg,"IN:ofVerifyCardDetail");
			
			actualErrMsg = common.ofGetErrDataJsonArray("999", 
					propConfiguration.getMessageOfResCode("commen.title.999", "Alert."), 
					propConfiguration.getMessageOfResCode("commen.exception.999","","(ENP026)"),
					exception.getMessage(),"Currently Service under maintenance so please try later (ENP026)", "0", "R");
						
			return actualErrMsg;
		}finally {			
			loggerImpl.stopAndPrintOptLogs(logger,"All API called successfully.","IN:ofVerifyCardDetail");
			loggerImpl.info(logger,"Response generated and send to client.", "IN:ofVerifyCardDetail");
			System.gc();
		}
	}

	/**
	 * This method are used for verify card pin details only
	 * @param a_input  It contain request data value with authorization data.
	 * @return  Return 0 as status code when all API successfully called.
	 *          if validation of data failed then return code values as 99.
	 * @apiNote This method used below API. <br>
	 * 	1.Call database APi for check card is already block or not.<br>
	 *  2.VerifyCardPIN call this api for verify card pin<br>
	 *  3.if verification failed then call database API for update the block status.
	 * 
	 */    
	public String ofVerifyCardPinDetail(String a_input) {
		String ls_cardNo = "", ls_cardPin = "";
		String ls_apiRequestData = "", ls_apiResponseData = "", ls_statusCd = "";
		String  ls_responseCd = "", ls_responseMessage = "";
		String actualErrMsg="";

		JSONObject 	apiRequestData = new JSONObject();
		JSONObject 	apiResponseData = null;//used for read api response data
		JSONObject 	apiActualResponseData = null;//used for read actual response data
		String 	   	ls_DBresponseData="";
		JSONObject 	dbResponseObject;
		String 		ls_updateStatus="";
		JSONObject	cardJsonrequestData;
		String		ls_msg_append_str="";
		LoggerImpl loggerImpl=null;
		String 		ls_langResCodeMsg="";

		try {
			loggerImpl=new LoggerImpl();
			loggerImpl.info(logger,"Preparing requset data and calling API.","IN:ofVerifyCardPinDetail");

			JSONObject requestData = new JSONObject(a_input);
			cardJsonrequestData=requestData.getJSONObject("CARD_DETAIL");

			ls_cardNo = cardJsonrequestData.getString("ACCT_NO");
			ls_cardPin = cardJsonrequestData.getString("CARD_PIN");

			//if validation fail then return 
			if(((ls_cardNo==null)|| "".equals(ls_cardNo))||
					((ls_cardPin==null)|| "".equals(ls_cardPin))){

				return  common.ofGetErrDataJsonArray("99",
						propConfiguration.getMessageOfResCode("commen.title.99", "Validation Failed."), 					
						propConfiguration.getMessageOfResCode("commen.invalid_card_detail",""),
						"Null values found in request data.","Invalid Card Details(ENP101).", "", "R");								
			}

			/*
			 *This below process check for card is blocked or not.if card is 
			 *blocked then API not called and return from here. 
			 * */
			loggerImpl.debug(logger,"User block process started.","IN:ofVerifyCardPinDetail");

			/* for get the times performance logs.*/
			loggerImpl.generateProfiler("ofVerifyCardPinDetail");
			loggerImpl.startProfiler("Preparing request data and checking user block status.");

			requestData.put("ACTUAL_ACTION", requestData.getString("ACTION"));
			requestData.put("BLOCK_ACTIVITY_DECRIPTION","Invalid card details.");
			requestData.put("ACTION", "BLOCK_CP_PROCESS");

			requestData.put("BLOCK_PROCESS_TYPE", "VERIFY_BLOCK_PROCESS");
			ls_DBresponseData= getData.ofGetResponseData(requestData.toString());

			if (ls_DBresponseData.trim().substring(0, 1).equals("[")) {
				dbResponseObject=new JSONArray(ls_DBresponseData).getJSONObject(0);
			}else {
				dbResponseObject = new JSONObject(ls_DBresponseData);                  
			}

			ls_statusCd=dbResponseObject.getString("STATUS");
			if(ls_statusCd!=null && !"0".equals(ls_statusCd)){
				return ls_DBresponseData;
			}
			loggerImpl.debug(logger,"User block process completed.","IN:ofVerifyCardPinDetail");
			//end block check logic

			//Make request data for verify card pin.
			apiRequestData.put("accountOrCardNumber", ls_cardNo);
			apiRequestData.put("cardPIN", ls_cardPin);

			ls_apiRequestData = apiRequestData.toString();		

			loggerImpl.debug(logger,"Calling API for verify card PIN.","IN:ofVerifyCardPinDetail");
			loggerImpl.startProfiler("Card Verify API Calling.");

			//Call SOAP API to verify card pin details.
			ls_apiResponseData=verifyCardPinController.verifyCardPin(ls_apiRequestData);			

			loggerImpl.debug(logger,"Reading response data.","IN:ofVerifyCardPinDetail");
			loggerImpl.startProfiler("Preparing response data.");

			if (ls_apiResponseData.trim().substring(0, 1).equals("[")) {
				apiResponseData=new JSONArray(ls_apiResponseData).getJSONObject(0);
			}else {
				apiResponseData = new JSONObject(ls_apiResponseData);                  
			}

			ls_statusCd = apiResponseData.getString("STATUS");

			//Check status code from API response.        
			if (ls_statusCd != null && !"".equals(ls_statusCd) && "0".equals(ls_statusCd)) {
				//Read the actual response message from response object.
				apiActualResponseData = apiResponseData.getJSONArray("RESPONSE").getJSONObject(0);			
				ls_responseCd = apiActualResponseData.getString("responseCode");
				ls_responseMessage = apiActualResponseData.getString("responseMessage");

				/*
				 *Check for card pin API response.
				 *if response code =100 then card pin is verified. 
				 * */
				if (ls_responseCd != null && !"".equals(ls_responseCd) && "100".equals(ls_responseCd)) {

					/*check for need to update the block status in from here or not ,
					 * this is write due to directly call this API without request.
					 * For other case block count will be updated using respective procedure.*/
					ls_updateStatus=requestData.optString("UPDATE_BLOCK_STATUS","N");

					if("Y".equalsIgnoreCase(ls_updateStatus)) {

						loggerImpl.debug(logger,"User blocked process started.","IN:ofVerifyCardPinDetail");
						//update blocked status of card.							
						requestData.put("BLOCK_PROCESS_TYPE", "UPDATE_BLOCK_STATUS");
						requestData.put("BLOCK_AUTH_STATUS", "Y");
						ls_DBresponseData= getData.ofGetResponseData(requestData.toString());								

						if (ls_DBresponseData.trim().substring(0, 1).equals("[")) {
							dbResponseObject=new JSONArray(ls_DBresponseData).getJSONObject(0);
						}else {
							dbResponseObject = new JSONObject(ls_DBresponseData);                  
						}									
						ls_statusCd=dbResponseObject.getString("STATUS");

						loggerImpl.debug(logger,"User blocked process completed.","IN:ofVerifyCardPinDetail");

						if(ls_statusCd!=null && !"0".equals(ls_statusCd)) {																
							return ls_DBresponseData;									
						}																	
					}

					//return the success message
					JSONObject jsonResponseObj=new JSONObject();
					JSONObject jsonSubResponseObj=new JSONObject();
					JSONArray jsonResponseArray=new JSONArray(); 
					JSONArray jsonResponseMainArray=new JSONArray(); 

					jsonResponseObj.put("STATUS", "0");
					jsonResponseObj.put("MESSAGE", "Card Verified.");
					jsonResponseObj.put("COLOR", "G");
					jsonResponseObj.put("AUTH_TOKEN", "value");
					jsonResponseObj.put("BLOCK_AUTH_STATUS", "Y");//card verified successfully and update block status.

					jsonSubResponseObj.put("TITLE", "Alert.");
					jsonSubResponseObj.put("MESSAGE", "Card Verified.");
					jsonResponseArray.put(jsonSubResponseObj);

					jsonResponseObj.put("RESPONSE", jsonResponseArray);								
					jsonResponseMainArray.put(jsonResponseObj);

					return jsonResponseMainArray.toString();

				} else {

					//check for update blocked status of card.
					if("000".equals(ls_responseCd)){

						requestData.put("BLOCK_PROCESS_TYPE", "UPDATE_BLOCK_STATUS");
						requestData.put("BLOCK_AUTH_STATUS", "N");
						ls_DBresponseData= getData.ofGetResponseData(requestData.toString());								

						if (ls_DBresponseData.trim().substring(0, 1).equals("[")){
							dbResponseObject=new JSONArray(ls_DBresponseData).getJSONObject(0);
						}else {
							dbResponseObject = new JSONObject(ls_DBresponseData);                  
						}

						ls_statusCd=dbResponseObject.getString("STATUS");
						ls_msg_append_str=dbResponseObject.getString("MESSAGE");
						if(ls_msg_append_str.endsWith("."))
						{
							ls_msg_append_str=ls_msg_append_str.substring(0, ls_msg_append_str.length() -1 );
						}
												
						return common.ofGetErrDataJsonArray(ls_statusCd,
								propConfiguration.getMessageOfResCode("commen.title."+ls_statusCd, "Alert."),
								ls_msg_append_str+"(ENP102).",
								apiActualResponseData.toString(), "Invalid Card Details(ENP102).", "", "R");
					}
					//return the error message
					
					ls_langResCodeMsg=propConfiguration.getResponseCode("getClientCardDetails."+ls_responseCd);
					return common.ofGetErrDataJsonArray(ls_langResCodeMsg, 
							propConfiguration.getMessageOfResCode("commen.title."+ls_langResCodeMsg, "Alert."),
							propConfiguration.getMessageOfResCode("commen.invalid_card_detail","","(ENP103)"),
							apiActualResponseData.toString(),"Invalid Card Details(ENP103).", "", "R");
				}			
			} else {
				//same value return from api response when status not equal to 0.
				return ls_apiResponseData;
						
			}
		} catch (Exception exception) {
			actualErrMsg = common.ofGetTotalErrString(exception, "");
			loggerImpl.error(logger,"Exception : " + actualErrMsg,"IN:ofVerifyCardPinDetail");
			
			actualErrMsg = common.ofGetErrDataJsonArray("999", 
					propConfiguration.getMessageOfResCode("commen.title.999", "Alert."),
					propConfiguration.getMessageOfResCode("commen.exception.999","","(ENP105)"),
					exception.getMessage(),"Currently Service under maintenance so please try later (ENP105).", "0", "R");
								
			return actualErrMsg;
		}finally {			
			loggerImpl.stopAndPrintOptLogs(logger,"All API called successfully.","IN:ofVerifyCardPinDetail");
			loggerImpl.info(logger,"Response generated and send to client.", "IN:ofVerifyCardPinDetail");
			System.gc();
		}
	}
}
