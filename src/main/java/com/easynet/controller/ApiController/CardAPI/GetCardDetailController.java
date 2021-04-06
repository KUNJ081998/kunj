package com.easynet.controller.ApiController.CardAPI;

import java.util.ArrayList;

import org.apache.ws.axis2.GetClientCardDetails;
import org.apache.ws.axis2.GetClientCardDetailsResponse;
import org.apache.ws.axis2.GetCreditCardDetails;
import org.apache.ws.axis2.GetCreditCardDetailsResponse;
import org.apache.ws.axis2.GetCustomerCreditCardsData;
import org.apache.ws.axis2.GetCustomerCreditCardsDataResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.easynet.bean.GetRequestUniqueData;
import com.easynet.configuration.PropConfiguration;
import com.easynet.configuration.SOAPConnector;
import com.easynet.impl.JSONObjectImpl;
import com.easynet.util.common;
import com.easynet.util.readXML;

import card.xsd.CreditCardListData;
import card.xsd.CustomerCard;
import card.xsd.GetCustomerCardDetailsRequest;
import city.xsd.CreditCardDetailsResponse;
import city.xsd.GetCreditCardDetailsRequest;
import city.xsd.GetCustomerCreditCardsRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.easynet.impl.LoggerImpl;

/**
 *@author Sagar Umate
 *This Class contain the card balance /detail API.  
 * 
 * */
@Component
public class GetCardDetailController {

	@Autowired
	private SOAPConnector soapConnector;

	@Autowired
	GetRequestUniqueData getRequestUniqueData;

	String 	ls_defCurrencyCode = readXML.getXmlData("root>DEF_CUR_CD>CARD_CUR_CD");

	static Logger logger=LoggerFactory.getLogger(GetCardDetailController.class);

	@Autowired
	PropConfiguration propConfiguration;
	
	/**
	 *Used this method for get card balance.
	 *@param requestData 	String format json request data
	 *@param ACCT_NO 	 	card no. in json object
	 *@param CURRENCYCODE   card currency code value in json object
	 *
	 *@return return the card balance detail in json format.
	 *@date 06/03/2021 
	 *@apiNote This method used below API.<br>
	 *	1.getCreditCardDetails for get the card balance.
	 *
	 * */	
	public String getCardBalance(String requestData)
	{		
		String ls_acctNo="";
		String ls_userName="";
		String ls_password="";
		String ls_responseCode="";
		String ls_responseMessage="";
		String ls_responseData="";
		String ls_resFlag="";
		String ls_currencyCode="";
		String actualErrMsg="";
		LoggerImpl loggerImpl=null;
		String ls_langResCodeMsg="";
		
		try {
			loggerImpl=new LoggerImpl();
			JSONObject accountrequestJson=new JSONObject(requestData);
			ls_acctNo=accountrequestJson.getString("ACCT_NO");
			ls_currencyCode=accountrequestJson.getString("CURRENCYCODE");//ask to bank

			if((ls_acctNo==null ||"".equals(ls_acctNo))||
					(ls_currencyCode==null ||"".equals(ls_currencyCode)))
			{
				ls_responseData = common.ofGetErrDataJsonArray("99",
						propConfiguration.getMessageOfResCode("commen.title.99", "Validation Failed."),
						propConfiguration.getMessageOfResCode("commen.invalid_req_data",""),		
						"Null values found in request data.", "Invalid Request.","", "R");			
				return ls_responseData; 
			}

			loggerImpl.info(logger,"Preparing requset data and calling API.","IN:getCardBalance");
			/*for get the times performance logs.*/
			loggerImpl.generateProfiler("getCardBalance");
			loggerImpl.startProfiler("Preparing request data");

			ls_userName = readXML.getXmlData("root>CUST_360>AUTHORIZATION>USER_NAME");
			ls_password = readXML.getXmlData("root>CUST_360>AUTHORIZATION>PASSWORD");

			//get the object factory object for get object.
			city.xsd.ObjectFactory xsdObjecyfactory=new city.xsd.ObjectFactory(); 
			//get Account detail request object from object factory
			GetCreditCardDetailsRequest getCreditCardDetailsRequest= xsdObjecyfactory.createGetCreditCardDetailsRequest();				
			//set all required field

			getCreditCardDetailsRequest.setCardNoActual(xsdObjecyfactory.createGetCreditCardDetailsRequestCardNoActual(ls_acctNo));
			getCreditCardDetailsRequest.setCurrencyCode(xsdObjecyfactory.createGetCreditCardDetailsRequestCurrencyCode(ls_currencyCode));
			getCreditCardDetailsRequest.setPassword(xsdObjecyfactory.createGetCreditCardDetailsRequestPassword(ls_password));
			getCreditCardDetailsRequest.setUsername(xsdObjecyfactory.createGetCreditCardDetailsRequestUsername(ls_userName));

			loggerImpl.debug(logger,"Json to xml conversion done.", "IN:getCardBalance");

			org.apache.ws.axis2.ObjectFactory axis2ObjectFactory=new org.apache.ws.axis2.ObjectFactory();		
			//get account detail object for store account detail request object.

			GetCreditCardDetails getCreditCardDetails=axis2ObjectFactory.createGetCreditCardDetails();
			getCreditCardDetails.setRequest(axis2ObjectFactory.createGetCreditCardDetailsRequest(getCreditCardDetailsRequest));			

			/*call API with requset data and get response object
			 This method uesd default url set the configuration class.
			 */
			GetCreditCardDetailsResponse getCreditCardDetailsResponse=null;

			loggerImpl.debug(logger,"getCreditCardDetails API calling", "IN:getCardBalance");
			loggerImpl.startProfiler("getCreditCardDetails API calling.");

			try {
				getCreditCardDetailsResponse=(GetCreditCardDetailsResponse) soapConnector.callWebService(getCreditCardDetails);
			}catch(SoapFaultClientException soapException){				
				actualErrMsg=soapException.getFaultStringOrReason();			
				ls_responseData = common.ofGetErrDataJsonArray("999",
						propConfiguration.getMessageOfResCode("commen.title.999", "Alert."),
						propConfiguration.getMessageOfResCode("commen.exception.999","","(ENP046)"),
						actualErrMsg,"Currently Service under maintenance so please try later (ENP046)", "0", "R");			

				actualErrMsg = common.ofGetTotalErrString(soapException, "");
				loggerImpl.error(logger,"Exception : " + actualErrMsg,"EX:getCreditCardDetails");
				return ls_responseData;							
			}

			loggerImpl.startProfiler("preparing getCreditCardDetails API response data.");
			loggerImpl.debug(logger,"getCreditCardDetails API called successfully.", "IN:getCardBalance",getCreditCardDetailsResponse);

			/*Get the response account object*/
			city.xsd.GetCreditCardDetailsResponse xsd_getAccountDetailresponse=getCreditCardDetailsResponse.getReturn().getValue();

			//get the response code status
			ls_responseCode=xsd_getAccountDetailresponse.getResponseCode().getValue();
			ls_responseMessage=xsd_getAccountDetailresponse.getResponseMessage().getValue();

			/*if response is 100 then success.
			 *If response is 101 then no records found.
			 */
			if(ls_responseCode!=null && "100".equals(ls_responseCode))
			{			
				JSONObjectImpl accountJsonObject=new JSONObjectImpl();
				JSONObject responseJsonObject=new JSONObject();
				JSONArray accountJsonArray=new JSONArray();

				CreditCardDetailsResponse creditCardDetailResponse=xsd_getAccountDetailresponse.getResponseData().getValue();

				accountJsonObject.put("BALANCE",creditCardDetailResponse.getCurrentBalance().getValue());				
				accountJsonObject.put("AVAILBALANCE",String.valueOf(creditCardDetailResponse.getAvailableCreditLimit().getValue()) );
				accountJsonObject.put("CURRENCYCODE",ls_defCurrencyCode);
				accountJsonObject.put("UNCLEARBALANCE", "0");
				accountJsonObject.put("BRANCH_CD", "");
				accountJsonObject.put("SYSTEMRESERVEDAMOUNT","0");	
				accountJsonObject.put("ACCOUNT","");
				accountJsonArray.put(accountJsonObject);							

				responseJsonObject.put("STATUS", "0");
				responseJsonObject.put("COLOR", "G");
				responseJsonObject.put("RESPONSE", accountJsonArray);
				responseJsonObject.put("MESSAGE","");
				responseJsonObject.put("RESPONSECODE",ls_responseCode);
				responseJsonObject.put("RESPONSEMESSAGE",ls_responseMessage);

				ls_responseData=responseJsonObject.toString();
			}else{ //other than 100.			
				
				ls_langResCodeMsg=propConfiguration.getResponseCode("getCreditCardDetails."+ls_responseCode);			
				ls_responseData = common.ofGetErrDataJsonArray(ls_langResCodeMsg, 
						propConfiguration.getMessageOfResCode("commen.title."+ls_langResCodeMsg, "Alert."), 
						propConfiguration.getMessageOfResCode("getCreditCardDetails."+ls_responseCode,"","(ENP050)"), ls_responseMessage, 
						"Currently Service under maintenance so please try later (ENP050).",ls_responseCode, "R");			
			}

		} catch (Exception exception) {
			actualErrMsg = common.ofGetTotalErrString(exception, "");
			loggerImpl.error(logger,"Exception : " + actualErrMsg,"IN:getCardBalance");
			ls_responseData = common.ofGetErrDataJsonArray("999",
					propConfiguration.getMessageOfResCode("commen.title.999", "Alert."),
					propConfiguration.getMessageOfResCode("commen.exception.999","","(ENP048)"),
					exception.getMessage(),"Currently Service under maintenance so please try later (ENP048)", "0", "R");
						
			return ls_responseData;
		}finally {
			loggerImpl.stopAndPrintOptLogs(logger,"All API called successfully.","IN:getCardBalance");
			loggerImpl.info(logger,"Response generated and send to client.", "IN:getCardBalance");
		}
		return ls_responseData;
	}

	/**
	 *Used this method for get card details.
	 *@param requestData 	String format json request data
	 *@param ACCT_NO 	 	card no. in json object
	 *@param CURRENCYCODE   card currency code value in json object
	 *
	 *@return return the card detail in json format.
	 *@date 06/03/2021
	 *@apiNote This method used below API.<br>
	 *	1.getClientCardDetails for get the card details.
	 * 
	 * */		
	public String ofGetCreditCardDetail(String requestData)
	{		
		String ls_acctNo="";
		String ls_userName="";
		String ls_password="";
		String ls_responseCode="";
		String ls_responseMessage="";
		String ls_responseData="";
		String ls_currencyCode="";
		String actualErrMsg="";
		LoggerImpl loggerImpl=null;
		String ls_langResCodeMsg="";

		try {
			loggerImpl=new LoggerImpl();

			JSONObject accountrequestJson=new JSONObject(requestData);
			ls_acctNo=accountrequestJson.getString("ACCT_NO");
			ls_currencyCode=accountrequestJson.getString("CURRENCYCODE");//ask to bank

			if((ls_acctNo==null ||"".equals(ls_acctNo))||
					(ls_currencyCode==null ||"".equals(ls_currencyCode)))
			{				
				ls_responseData = common.ofGetErrDataJsonArray("99",
						propConfiguration.getMessageOfResCode("commen.title.99", "Validation Failed."),
						propConfiguration.getMessageOfResCode("commen.invalid_card_detail",""),		
						"Null values found in request data.", "Invalid Request.","", "R");
				
				return ls_responseData; 
			}

			loggerImpl.info(logger,"Preparing requset data and calling API.","IN:ofGetCreditCardDetail");
			/*for get the times performance logs.*/
			loggerImpl.generateProfiler("ofGetCreditCardDetail");
			loggerImpl.startProfiler("Preparing request data");

			ls_userName = readXML.getXmlData("root>CUST_360>AUTHORIZATION>USER_NAME");
			ls_password = readXML.getXmlData("root>CUST_360>AUTHORIZATION>PASSWORD");

			//get the object factory object for get object.
			card.xsd.ObjectFactory xsdObjecyfactory=new card.xsd.ObjectFactory(); 
			//get Account detail request object from object factory

			GetCustomerCardDetailsRequest getCustomerCardDetailsrequest =xsdObjecyfactory.createGetCustomerCardDetailsRequest();

			//set all required field
			getCustomerCardDetailsrequest.setCardNoActual(xsdObjecyfactory.createGetCustomerCardDetailsRequestCardNoActual(ls_acctNo));
			getCustomerCardDetailsrequest.setPassword(xsdObjecyfactory.createGetCustomerCardDetailsRequestPassword(ls_password));
			getCustomerCardDetailsrequest.setUsername(xsdObjecyfactory.createGetCustomerCardDetailsRequestUsername(ls_userName));

			loggerImpl.debug(logger,"Json to xml conversion done.", "IN:ofGetCreditCardDetail");

			org.apache.ws.axis2.ObjectFactory axis2ObjectFactory=new org.apache.ws.axis2.ObjectFactory();		
			//get account detail object for store account detail request object.

			GetClientCardDetails getClientCardDetails =axis2ObjectFactory.createGetClientCardDetails();

			getClientCardDetails.setRequest(axis2ObjectFactory.createGetClientCardDetailsRequest(getCustomerCardDetailsrequest));			
			/*call API with requset data and get response object
			 This method uesd default url set the configuration class.
			 */
			GetClientCardDetailsResponse getClientCardDetailsRespone=null;
			loggerImpl.debug(logger,"getClientCardDetails API calling", "IN:ofGetCreditCardDetail");
			loggerImpl.startProfiler("getClientCardDetails API calling.");

			try {
				getClientCardDetailsRespone=(GetClientCardDetailsResponse) soapConnector.callWebService(getClientCardDetails);
			}catch(SoapFaultClientException soapException){				
				actualErrMsg=soapException.getFaultStringOrReason();							
				ls_responseData = common.ofGetErrDataJsonArray("999", 
						propConfiguration.getMessageOfResCode("commen.title.999", "Alert."), 						
						propConfiguration.getMessageOfResCode("commen.exception.999","","(ENP045)"),
						actualErrMsg,"Currently Service under maintenance so please try later (ENP045)", "0", "R");
				
				actualErrMsg = common.ofGetTotalErrString(soapException, "");
				loggerImpl.error(logger,"Exception : " + actualErrMsg,"EX:getClientCardDetails");
				return ls_responseData;							
			}

			loggerImpl.startProfiler("preparing getClientCardDetails API response data.");
			loggerImpl.debug(logger,"getClientCardDetails API called successfully.", "IN:ofGetCreditCardDetail",getClientCardDetailsRespone);

			/*Get the response account object*/
			card.xsd.GetClientCardDetailsResponse xsd_getClientCardDetailResponse=getClientCardDetailsRespone.getReturn().getValue();

			//get the response code status
			ls_responseCode=xsd_getClientCardDetailResponse.getResponseCode().getValue();
			ls_responseMessage=xsd_getClientCardDetailResponse.getResponseMessage().getValue();

			/*if response is 100 then success.
			 *If response is 101 then no records found.
			 */
			if(ls_responseCode!=null && "100".equals(ls_responseCode))
			{			
				JSONObjectImpl accountJsonObject=new JSONObjectImpl();
				JSONObject responseJsonObject=new JSONObject();
				JSONArray accountJsonArray=new JSONArray();

				CustomerCard customerCard=xsd_getClientCardDetailResponse.getResponseData().getValue();			
				accountJsonObject.put("BRANCH_CD", "");
				accountJsonObject.put("BALANCE","");
				accountJsonObject.put("AVAILBALANCE","0");
				accountJsonObject.put("CURRENCYCODE",ls_defCurrencyCode);
				accountJsonObject.put("UNCLEARBALANCE","0");
				accountJsonObject.put("ACCOUNTNAME", customerCard.getCardHolderName().getValue());
				accountJsonObject.put("ACCTCLSDATE", customerCard.getExpiryDate().getValue());
				accountJsonObject.put("ACCTOPENDATE", "");
				accountJsonObject.put("ACCTSTATUS", customerCard.getCardStatus().getValue());
				accountJsonObject.put("ACCTTYPE", customerCard.getAccountType().getValue());
				accountJsonObject.put("ADDRESS","");
				accountJsonObject.put("BRANCHNAME", customerCard.getBranchCode().getValue());
				accountJsonObject.put("CHEQUEALLOWED","");
				accountJsonObject.put("CHARGEAPPLICABLE","");
				accountJsonObject.put("CONTACTNUMBER", "");
				accountJsonObject.put("CUSTCONST","");
				accountJsonObject.put("DOB","");
				accountJsonObject.put("FATHERNAME","");
				accountJsonObject.put("LIENAMOUNT","0");
				accountJsonObject.put("MOTHERNAME", "");
				accountJsonObject.put("NATIONALID", "");
				accountJsonObject.put("PASSPORT", "");
				accountJsonObject.put("PERMANENTADDRESS", "");
				accountJsonObject.put("PRODCUTCODE", "");
				accountJsonObject.put("SCHEMECODE", customerCard.getCASAScheme().getValue());
				accountJsonObject.put("SCHEMETYPE", "");
				accountJsonObject.put("STATUS", "");
				accountJsonObject.put("SYSTEMRESERVEDAMOUNT", "0");
				accountJsonObject.put("ACCOUNT",customerCard.getCardNoActual().getValue());

				accountJsonArray.put(accountJsonObject);

				responseJsonObject.put("STATUS", "0");
				responseJsonObject.put("COLOR", "G");
				responseJsonObject.put("RESPONSE", accountJsonArray);
				responseJsonObject.put("MESSAGE","");
				responseJsonObject.put("RESPONSECODE",ls_responseCode);
				responseJsonObject.put("RESPONSEMESSAGE",ls_responseMessage);


				ls_responseData=responseJsonObject.toString();				

			}else{ //other than 100.
				
				ls_langResCodeMsg=propConfiguration.getResponseCode("getClientCardDetails."+ls_responseCode);
				
				ls_responseData = common.ofGetErrDataJsonArray(ls_langResCodeMsg, 
						propConfiguration.getMessageOfResCode("commen.title."+ls_langResCodeMsg, "Alert."), 
						propConfiguration.getMessageOfResCode("getClientCardDetails."+ls_responseCode,"","(ENP049)"),
						ls_responseMessage, "Currently Service under maintenance so please try later (ENP049).",
						ls_responseCode, "R");				
			}	
		} catch (Exception exception) {
			actualErrMsg = common.ofGetTotalErrString(exception, "");
			loggerImpl.error(logger,"Exception : " + actualErrMsg,"IN:ofGetCreditCardDetail");
			ls_responseData = common.ofGetErrDataJsonArray("999", 
					propConfiguration.getMessageOfResCode("commen.title.999", "Alert."), 
					propConfiguration.getMessageOfResCode("commen.exception.999","","(ENP047)"), 
					exception.getMessage(), "Currently Service under maintenance so please try later (ENP047)", "0", "R");			
			
			return ls_responseData;
		}finally {
			loggerImpl.stopAndPrintOptLogs(logger,"All API called successfully.","IN:ofGetCreditCardDetail");
			loggerImpl.info(logger,"Response generated and send to client.", "IN:ofGetCreditCardDetail");
		}
		return ls_responseData;
	}

	/**
	 *This API used for get the card list for show on dashbaord
	 *@param requestData Json format request data
	 *@return return the response data of API in string json format.
	 *@since 19/02/2021 06:09 PM  
	 *@apiNote This method used below API.<br>
	 *	1.getCustomerCreditCardsData to get the card detail whcih is used on dashboard screen.
	 * */
	public String ofGetDashboardCardList(String requestData) {
		String ls_clientId="";
		String ls_password="";
		String ls_userName="";
		String ls_responseCode="";
		String ls_responseMessage="";
		ArrayList<CreditCardListData>  creditcardlist;
		String ls_responseData="";
		String actualErrMsg="";
		LoggerImpl loggerImpl=null;
		String ls_langResCodeMsg="";

		try {
			loggerImpl=new LoggerImpl();
			JSONObject requestDataJson=new JSONObject(requestData);
			ls_clientId=requestDataJson.getString("APPCUSTOMER_ID");

			if((ls_clientId==null || "".equals(ls_clientId))){
				ls_responseData = common.ofGetErrDataJsonArray("99",
						propConfiguration.getMessageOfResCode("commen.title.99", "Validation Failed."),
						propConfiguration.getMessageOfResCode("commen.invalid_req_data",""),		
						"Null values found in request data.", "Invalid Request.","", "R");

				return ls_responseData; 
			}

			loggerImpl.info(logger,"Preparing requset data and calling API.","IN:ofGetDashboardCardList");
			/*for get the times performance logs.*/
			loggerImpl.generateProfiler("ofGetDashboardCardList");
			loggerImpl.startProfiler("Preparing request data");

			ls_userName = readXML.getXmlData("root>CUST_360>AUTHORIZATION>USER_NAME");
			ls_password = readXML.getXmlData("root>CUST_360>AUTHORIZATION>PASSWORD");

			//created object factory object.
			city.xsd.ObjectFactory xsdObjectFactory=new city.xsd.ObjectFactory();
			//created get customer credit cards request object from object factory.
			GetCustomerCreditCardsRequest getCustomerCreditCardsRequest=xsdObjectFactory.createGetCustomerCreditCardsRequest();

			//setting all the required fields.
			getCustomerCreditCardsRequest.setClientId(xsdObjectFactory.createGetCustomerCreditCardsRequestClientId(ls_clientId));
			getCustomerCreditCardsRequest.setPassword(xsdObjectFactory.createGetCustomerCreditCardsRequestPassword(ls_password));
			getCustomerCreditCardsRequest.setUsername(xsdObjectFactory.createGetCustomerCreditCardsRequestUsername(ls_userName));

			loggerImpl.debug(logger,"Json to xml conversion done.", "IN:ofGetDashboardCardList");

			org.apache.ws.axis2.ObjectFactory axis2ObjectFactory=new org.apache.ws.axis2.ObjectFactory();
			//get customer credit cards request stored in get customer credit cards data.
			GetCustomerCreditCardsData getCustomerCreditCardsData=axis2ObjectFactory.createGetCustomerCreditCardsData();
			getCustomerCreditCardsData.setRequest(axis2ObjectFactory.createGetCustomerCreditCardsDataRequest(getCustomerCreditCardsRequest));

			/*created response object of get customer credit cards data response.*/
			GetCustomerCreditCardsDataResponse getCustomerCreditCardsDataReponse=null;

			loggerImpl.debug(logger,"getCustomerCreditCardsData API calling", "IN:ofGetDashboardCardList");
			loggerImpl.startProfiler("getCustomerCreditCardsData API calling.");

			try {
				getCustomerCreditCardsDataReponse=(GetCustomerCreditCardsDataResponse) soapConnector.callWebService(getCustomerCreditCardsData);
			}
			catch(SoapFaultClientException soapException){				
				actualErrMsg=soapException.getFaultStringOrReason();			
				ls_responseData = common.ofGetErrDataJsonArray("999", 
						propConfiguration.getMessageOfResCode("commen.title.999", "Alert."),
						propConfiguration.getMessageOfResCode("commen.exception.999","","(ENP080)"),
						actualErrMsg,"Currently Service under maintenance so please try later (ENP080)", "0", "R");				

				actualErrMsg = common.ofGetTotalErrString(soapException, "");
				loggerImpl.error(logger,"Exception : " + actualErrMsg,"EX:getCustomerCreditCardsData");
				return ls_responseData;							
			}
			
			loggerImpl.startProfiler("preparing getCustomerCreditCardsData API response data.");
			loggerImpl.debug(logger,"getCustomerCreditCardsData API called successfully.", "IN:ofGetDashboardCardList",getCustomerCreditCardsDataReponse);

			//created get customer credit cards data response object.
			city.xsd.GetCustomerCreditCardsDataResponse xsd_getcustomercreditcardsdataresponse=getCustomerCreditCardsDataReponse.getReturn().getValue();

			//getting all the values from the response object.
			ls_responseCode=xsd_getcustomercreditcardsdataresponse.getResponseCode().getValue();
			ls_responseMessage=xsd_getcustomercreditcardsdataresponse.getResponseMessage().getValue();

			/*if response is 100 then success.*/
			if(ls_responseCode!=null && "100".equals(ls_responseCode)){

				JSONObject responseJsonObject=new JSONObject();
				JSONArray responseJsonObjectArray=new JSONArray();

				creditcardlist=(ArrayList<CreditCardListData>) xsd_getcustomercreditcardsdataresponse.getResponseData();

				JSONArray creditCardListDataArray=new JSONArray();

				for (CreditCardListData creditCardListData : creditcardlist) {
					//set the data into json 					
					if(!"Closed".equalsIgnoreCase(creditCardListData.getCardStatus().getValue())){

						JSONObjectImpl creditCardData=new JSONObjectImpl();
						creditCardData.put("CARDHOLDERNAME",creditCardListData.getCardHolderName().getValue() );
						creditCardData.put("CARDNOACTUAL",creditCardListData.getCardNoActual().getValue() );
						creditCardData.put("CARDPIN",creditCardListData.getCardPIN().getValue() );
						creditCardData.put("CARDSTATE",creditCardListData.getCardState().getValue() );
						creditCardData.put("CARDSTATUS",creditCardListData.getCardStatus().getValue() );
						creditCardData.put("CARDTYPE",creditCardListData.getCardType().getValue() );
						creditCardData.put("EXPIRYDATE",creditCardListData.getExpiryDate().getValue() );
						creditCardData.put("TYPE",creditCardListData.getType().getValue() );

						creditCardListDataArray.put(creditCardData);
					}
				}

				responseJsonObject.put("STATUS", "0");
				responseJsonObject.put("COLOR", "G");
				responseJsonObject.put("RESPONSE", creditCardListDataArray);
				responseJsonObject.put("MESSAGE","");
				responseJsonObject.put("RESPONSECODE",ls_responseCode);
				responseJsonObject.put("RESPONSEMESSAGE",ls_responseMessage);

				responseJsonObjectArray.put(responseJsonObject);
				ls_responseData=responseJsonObjectArray.toString();	
							
			}else{ //other than 100		
				ls_langResCodeMsg=propConfiguration.getResponseCode("getCustomerCreditCardsData."+ls_responseCode);
				
				ls_responseData = common.ofGetErrDataJsonArray(ls_langResCodeMsg,
						propConfiguration.getMessageOfResCode("commen.title."+ls_langResCodeMsg, "Alert."),
						propConfiguration.getMessageOfResCode("getCustomerCreditCardsData."+ls_responseCode,"","(ENP082)"), 
						ls_responseMessage,"Currently Service under maintenance so please try later (ENP082)."
						,ls_responseCode, "R");				
			}

		}catch(Exception exception){
			actualErrMsg = common.ofGetTotalErrString(exception, "");
			loggerImpl.error(logger,"Exception : " + actualErrMsg,"IN:ofGetDashboardCardList");
			
			ls_responseData = common.ofGetErrDataJsonArray("999", 
					propConfiguration.getMessageOfResCode("commen.title.999", "Alert."), 
					propConfiguration.getMessageOfResCode("commen.exception.999","","(ENP081)"), 
					exception.getMessage(), "Currently Service under maintenance so please try later (ENP081).", "0", "R");
						
		}finally {
			loggerImpl.stopAndPrintOptLogs(logger,"All API called successfully.","IN:ofGetDashboardCardList");
			loggerImpl.info(logger,"Response generated and send to client.", "IN:ofGetDashboardCardList");
		}
		return ls_responseData;
	}
}
