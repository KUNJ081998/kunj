package com.easynet;

import com.easynet.bean.GetRequestUniqueData;
import com.easynet.configuration.PropConfiguration;
import com.easynet.controller.ApiController.DocUplodApiController;
import com.easynet.controller.ApiController.CardAPI.GetCardDetailController;
import com.easynet.controller.UpdateController.UpdateDetailController;
import com.easynet.controller.account.AccountDetail;
import com.easynet.controller.beneficiary.ManageBeneficiary;
import com.easynet.controller.bkash.VerifyBkashDetailController;
import com.easynet.controller.cardAPI.CardDetailController;
import com.easynet.controller.qr.QRScanController;
import com.easynet.controller.registration.Customer360Controller;
import com.easynet.controller.registration.ForgetUserController;
import com.easynet.controller.registration.LoginController;
import com.easynet.impl.LoggerImpl;

import static com.easynet.util.common.PrintErrLog;

import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.easynet.util.Connectiondb;
import com.easynet.util.GetData;
import com.easynet.util.apiCall1;
import com.easynet.util.common;
import com.easynet.util.readXML;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;


@RestController
@RequestMapping("/easynetpro-serv")
public class easy_api {

	static Logger logger=LoggerFactory.getLogger(easy_api.class);

	//Spring generate the object and load into below varibale.
	@Autowired
	Customer360Controller  customer360Controller;

	@Autowired
	CardDetailController cardDetailController;

	@Autowired
	GetData getData;

	@Autowired
	apiCall1 callApi;

	@Autowired
	AccountDetail accountDetail; 

	@Autowired
	ForgetUserController forgetUserController;

	@Autowired
	DocUplodApiController docUploadApiController;

	@Autowired
	ManageBeneficiary manageBeneficiary; 

	@Autowired
	UpdateDetailController updateDetailController;

	@Autowired
	GetCardDetailController getCardDetailController;

	@Autowired
	GetRequestUniqueData getRequestUniqueData;
	
	@Autowired
	LoginController	loginController;
	
	@Autowired
	VerifyBkashDetailController verifyBkashDetailController;
	
	@Autowired
	private QRScanController qrController;

  @Autowired
	PropConfiguration propConfiguration;
	
	@RequestMapping(value = "request", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {"application/json"})
	public @ResponseBody
	String request(@RequestBody String input, HttpServletRequest request) {

		String 		ls_action = "";
		String 		ls_responseData="";
		JSONObject 	jsonAction = null;
		String 		ls_apiURL="";
		String 		actualErrMsg="";
		String 		ls_cardVerify="";
		JSONObject 	JsonRequestData = null;//store main json request	
		String 		ls_cardVerifyResponse = null;//get the card verify API response.
		JSONObject 	josnCardDataResponse;//store the card verify API response json object.
		String 		ls_cardVerifyStatus="";
		String 		ls_screenName="";
		JSONObject	requestData=null;
		String		ls_langCode="";
		
		LoggerImpl loggerImpl =new LoggerImpl();
		Logger reqResLogger=null;
		
		try {	
			//for write the req and res logs.
			reqResLogger=LoggerFactory.getLogger("REQ_RES_LOGGER");
						
			if (input.trim().substring(0, 1).equals("[")) {
				input = (String) input.substring(1, input.length() - 1);				
			} else {
				input = (String) input;
			}
			
			requestData=new JSONObject(input);			
			ls_langCode=requestData.getString("DISPLAY_LANGUAGE");
			getRequestUniqueData.setLangCode(ls_langCode);
			
			try {		
				
				ls_action = request.getHeader("ACTION");
				ls_cardVerify=request.getHeader("CARD_VERIFY");			
				if(ls_action==null || "".equals(ls_action)){					
				//	jsonAction=new JSONObject(input);                  
					ls_action=requestData.getString("ACTION");	
				}
			}catch(Exception e){
				actualErrMsg = common.ofGetTotalErrString(e,"Error in read Header Data");
				loggerImpl.error(logger,actualErrMsg,"request API");			
			}
									
			//write this statement for print unique no. and screen name in log file
			MDC.put("uniqueReqID",String.valueOf(getRequestUniqueData.getUniqueNumber()));
			ls_screenName=request.getHeader("SCREEN_NAME");
			
			if(ls_screenName==null ||"".equalsIgnoreCase(ls_screenName)) {
				ls_screenName=ls_action;
			}
						
			MDC.put("screenName",ls_screenName);
			
			loggerImpl.info(reqResLogger,input, "Request Data");
			
			/*If card verify flag is y then 1st verify card detail with expiry data and
			 *If card verify flag is P then 1st verify card pin only  
			 * then send response to other action*/
			try {
				if(ls_cardVerify!=null && ( "Y".equals(ls_cardVerify) || "P".equals(ls_cardVerify) )) {
					//verify card pin and expiry date
					if("Y".equals(ls_cardVerify)) {
						ls_cardVerifyResponse=cardDetailController.ofVerifyCardDetail(input);
					//verify card pin only.
					}else if("P".equals(ls_cardVerify)){
						ls_cardVerifyResponse=cardDetailController.ofVerifyCardPinDetail(input);
					}

					/*Read the API response data.*/
					if (ls_cardVerifyResponse.trim().substring(0, 1).equals("[")) {
						josnCardDataResponse=new JSONArray(ls_cardVerifyResponse).getJSONObject(0);
					}else {
						josnCardDataResponse = new JSONObject(ls_cardVerifyResponse); 
					}

					ls_cardVerifyStatus=josnCardDataResponse.getString("STATUS");
					if(ls_cardVerifyStatus!=null && "0".equals(ls_cardVerifyStatus)){ 
						/*send data to forward action code and set the auth token 
						 * to request data for check in function action.
						 */						
						JsonRequestData=new JSONObject(input);                  						
						JsonRequestData.put("AUTH_TOKEN", josnCardDataResponse.getString("AUTH_TOKEN"));
						JsonRequestData.put("BLOCK_AUTH_STATUS", josnCardDataResponse.getString("BLOCK_AUTH_STATUS"));
						input=JsonRequestData.toString();
					}else { 
						//return error response for card validation failed.
						ls_responseData=ls_cardVerifyResponse; //for print into logs.
						return	ls_cardVerifyResponse; 
					} 
				}
			}catch(Exception e){
				actualErrMsg = common.ofGetTotalErrString(e,"Error in validating authorization details.");
				loggerImpl.error(logger,actualErrMsg,"Request API");		
				
				ls_responseData= common.ofGetErrDataJsonArray("999", 
						propConfiguration.getMessageOfResCode("commen.title.999", "Alert."),
						propConfiguration.getMessageOfResCode("commen.exception.999","","(ENP097)"),
						e.getMessage(),"Currently Service under maintenance so please try later (ENP097)", "0", "R");
				return ls_responseData;
			}
			
			if ("VERIFYUSERACCT".equals(ls_action) ||"GETUSERALLEXISTS".equals(ls_action)) {
				String ls_resFlag="";
				String ls_appURL="";
				String ls_webURL="";

				//below code in only for send link to user with msg or email.
				if (input.trim().substring(0, 1).equals("[")) {
					JsonRequestData=new JSONArray(input).getJSONObject(0);
				}else if (input.trim().substring(0, 1).equals("{")) {
					JsonRequestData=new JSONObject(input);                  
				}

				ls_resFlag=JsonRequestData.optString("RES_TYPE","D");
				if(ls_resFlag!=null && "L".equals(ls_resFlag)){

					ls_appURL=readXML.getXmlData("root>URL>APP_URL");
					ls_webURL=readXML.getXmlData("root>URL>WEB_URL");
					if(ls_appURL == null ||"".equals(ls_appURL)) {
						
						ls_responseData= common.ofGetErrDataJsonArray("99", 
								propConfiguration.getMessageOfResCode("commen.title.99", "Validation Failed."),
								propConfiguration.getMessageOfResCode("commen.APP_URL.isnull",""),
								"Null value found in APP_URL.","APP_URL URL not found.", "", "R");
						return ls_responseData;
					}

					if(ls_webURL == null ||"".equals(ls_webURL)) {
						ls_responseData= common.ofGetErrDataJsonArray("99",
								propConfiguration.getMessageOfResCode("commen.title.99", "Validation Failed."),
								propConfiguration.getMessageOfResCode("commen.WEB_URL.isnull",""), 
								"Null value found in WEB_URL.","WEB_URL URL not found.", "", "R");
						return ls_responseData;
					}

					JsonRequestData.put("APP_URL", ls_appURL);
					JsonRequestData.put("WEB_URL", ls_webURL);		
					input=JsonRequestData.toString();					
				}

				ls_responseData= customer360Controller.ofVerifyAcctCard(input);

			} else if ("VERIFYCARDDETAIL".equals(ls_action)) {
				/*
				 *below logic write due update the card block status in case of card verified successfully. 
				 * */							
				if (input.trim().substring(0, 1).equals("[")) {
					JsonRequestData=new JSONArray(input).getJSONObject(0);
				}else if (input.trim().substring(0, 1).equals("{")) {
					JsonRequestData=new JSONObject(input);                  
				}
				JsonRequestData.put("UPDATE_BLOCK_STATUS", "Y");

				ls_responseData= cardDetailController.ofVerifyCardDetail(JsonRequestData.toString());

			} else if ("GET_AUTH_CRED".equals(ls_action)) {
				ls_apiURL=readXML.getXmlData("root>AUTHORIZATION>OAUTH_URL");
				ls_responseData=callApi.GenarateAccessToken(ls_apiURL,"");    

			}else if ("REGUSER".equals(ls_action)){
				ls_responseData= customer360Controller.ofGenerateUserID(input);
			}	
			else if("GETOPERATIVEACCT".equals(ls_action)){        	
				ls_responseData=accountDetail.ofGetOperativeAccount(input);
			}
			else if("VERIFYCARDGETUID".equals(ls_action)){        	
				ls_responseData=forgetUserController.GetUserIdByCard(input);
			}
			else if("GETACCTBALDETAIL".equals(ls_action)){        	
				ls_responseData=accountDetail.ofGetBalAcctInfo(input);
			}
			else if("UPLOADSUPDOCFILE".equals(ls_action)){     				
				ls_responseData=docUploadApiController.ofUploadFileData(input);
			}
			else if("UPDATEMOBILENO".equals(ls_action)){
				//update mobile number controller.
				ls_responseData=updateDetailController.ofVerifyAndUpdateMobileNo(input);
			}
			else if("UPDATEEMAILID".equals(ls_action)){     				
				//update emailID Controller
				ls_responseData=updateDetailController.ofVerifyAndUpdateEmailId(input);							
			}
			else if("GETTERMDEPACCTDTL".equals(ls_action)){     	
				//call api to get term deposit account detail.				
				ls_responseData=accountDetail.ofGetTermDepositAccount(input);				
			}
			else if("GETLOANACCTDTL".equals(ls_action)){     				
				//get loan account detail as per source.
				ls_responseData=accountDetail.ofGetLoanAcctDetail(input);
			}
			else if("GETCARDTYPEDETAIL".equals(ls_action)){     				
				//get card detail for show in dashboard
				ls_responseData=getCardDetailController.ofGetDashboardCardList(input);
			}
			else if("LOGINREQ".equals(ls_action)){     				
				//login request and update the user detail.				
				ls_responseData=loginController.ofverifyAndUpdateDtl(input);			
			}else if("GETUSERDTLALLKEYS".equals(ls_action)){     				
				//for get the data for all keys from customer 360 api.		
				ls_responseData=customer360Controller.ofGetUserDtl360(input);		
			}else if("VERIFYBKASHAC".equals(ls_action)){     				
				//for verify mobile number using bkash API.		
				ls_responseData=verifyBkashDetailController.verifyBkashMobileNo(input);	
			}else if("QRSCANCODE".equals(ls_action)) {
				//for getting QR data using getQRData API
				ls_responseData = qrController.getQRData(input);
			}
			else {				
				loggerImpl.debug(logger, "Request Data send to Database API.", "request");
				ls_responseData= getData.ofGetResponseData(input);
			}
		}catch(Exception e){
			actualErrMsg = common.ofGetTotalErrString(e,"Error in Calling API..");
			loggerImpl.error(logger,actualErrMsg,"Request API");
			
			//this below line is for write the log statement.
			
			ls_responseData= common.ofGetErrDataJsonArray("999", 
					propConfiguration.getMessageOfResCode("commen.title.999", "Alert."),
					propConfiguration.getMessageOfResCode("commen.exception.999","","(ENP098)"),
					e.getMessage(), "Currently Service under maintenance so please try later (ENP098)", "0", "R");
			return ls_responseData;
			
		}finally {				
			loggerImpl.info(reqResLogger, ls_responseData, "Response Data");
			
			/*Must clear the set data in MDC else same data will be used 
			again for same thread when start in thread pool.*/
			MDC.clear();					
		}		
		return ls_responseData;
	}
}
