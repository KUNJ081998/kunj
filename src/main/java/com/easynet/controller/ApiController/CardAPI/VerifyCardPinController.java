package com.easynet.controller.ApiController.CardAPI;

import org.apache.ws.axis2.VerifyCardPIN;
import org.apache.ws.axis2.VerifyCardPINResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.easynet.bean.GetRequestUniqueData;
import com.easynet.configuration.PropConfiguration;
import com.easynet.configuration.SOAPConnector;
import com.easynet.impl.LoggerImpl;
import com.easynet.util.common;
import com.easynet.util.readXML;

import card.xsd.ObjectFactory;
import card.xsd.VerifyCardPINRequest;
/**
 *This class used for verify card pin details. 
 *@author sagar umate
 * 
 * */
@Component
public class VerifyCardPinController {

	@Autowired
	private SOAPConnector soapConnector;
	@Autowired
	GetRequestUniqueData getRequestUniqueData;

	static Logger logger=LoggerFactory.getLogger(VerifyCardPinController.class);
		
	@Autowired
	PropConfiguration propConfiguration;
	
	/**
	 *used this method to verify card pin of card.
	 *@param requestData json format string request data.
	 *
	 *@return return status of card pin verified o not.
	 *@Date 06/03/2021
	 *@apiNote This method used below API.<br>
	 *	1.verifyCardPIN for verify card pin. 
	 * 
	 * */
	public String verifyCardPin(String requestData)
	{
		String ls_userName="";
		String ls_password="";
		String ls_pinNo="";
		String ls_cardNo="";
		String actualErrMsg="";//used for set error data
		String ls_responseData="";//used for set response data
		String ls_responseCode="";
		String ls_responseMessage="";
		JSONObject responseObject=null;
		LoggerImpl loggerImpl=null;
		
		try {		
			loggerImpl=new LoggerImpl();
			loggerImpl.info(logger,"Preparing requset data and calling API.","IN:verifyCardPin");
	
			JSONObject mainResponseData=new JSONObject();
			JSONObject requestDataObj=new JSONObject(requestData);
			ls_cardNo = requestDataObj.getString("accountOrCardNumber");
			ls_pinNo = requestDataObj.getString("cardPIN");

			if(((ls_cardNo==null)|| "".equals(ls_cardNo))||
					((ls_pinNo==null)|| "".equals(ls_pinNo))){

				return common.ofGetErrDataJsonArray("99",
						propConfiguration.getMessageOfResCode("commen.title.99", "Validation Failed."),
						propConfiguration.getMessageOfResCode("commen.invalid_card_detail",""),		
						"Null values found in request data.", "Invalid Request.","", "R");							
			}				
			
			/*for get the times performance logs.*/
			loggerImpl.generateProfiler("verifyCardPin");
			loggerImpl.startProfiler("Preparing request data");
			
			ls_userName=readXML.getXmlData("root>CUST_360>AUTHORIZATION>USER_NAME");
			ls_password=readXML.getXmlData("root>CUST_360>AUTHORIZATION>PASSWORD");

			//make Sub request data of verify card Pin API.
			ObjectFactory cardObjectFactory=new ObjectFactory();
			VerifyCardPINRequest verifyCardPINRequest=cardObjectFactory.createVerifyCardPINRequest();
			verifyCardPINRequest.setCardNoActual(cardObjectFactory.createVerifyCardPINRequestCardNoActual(ls_cardNo));
			verifyCardPINRequest.setCardPIN(cardObjectFactory.createVerifyCardPINRequestCardPIN(ls_pinNo));
			verifyCardPINRequest.setChangeReason(cardObjectFactory.createVerifyCardPINRequestChangeReason("Verify card Pin for authorization."));
			verifyCardPINRequest.setUsername(cardObjectFactory.createVerifyCardPINRequestUsername(ls_userName));
			verifyCardPINRequest.setPassword(cardObjectFactory.createVerifyCardPINRequestPassword(ls_password));

			loggerImpl.debug(logger,"Json to xml conversion done.", "IN:verifyCardPin");
			
			///make a card pin actual request object 
			org.apache.ws.axis2.ObjectFactory axisObjectFactory=new org.apache.ws.axis2.ObjectFactory(); 
			VerifyCardPIN verifyCardPin=axisObjectFactory.createVerifyCardPIN();
			verifyCardPin.setRequest(axisObjectFactory.createVerifyCardPINRequest(verifyCardPINRequest));

			VerifyCardPINResponse verifyCardPINResponse=null;

			/*call API with requset data and get response object
		 	This method uesd default url set the configuration class.
			 */
			loggerImpl.debug(logger,"verifyCardPIN API calling", "IN:verifyCardPin");
			loggerImpl.startProfiler("verifyCardPIN API calling.");
			
			try {				
				verifyCardPINResponse=(VerifyCardPINResponse) soapConnector.callWebService(verifyCardPin);			

			}catch(SoapFaultClientException soapException){							
				actualErrMsg=soapException.getFaultStringOrReason();
				
				ls_responseData = common.ofGetErrDataJsonObject("999",
						propConfiguration.getMessageOfResCode("commen.title.999", "Alert."), 
						propConfiguration.getMessageOfResCode("commen.exception.999","","(ENP064)"),
						actualErrMsg,"Currently Service under maintenance so please try later (ENP064).",
						soapException.getFaultCode().getLocalPart(), "R");
								
				actualErrMsg = common.ofGetTotalErrString(soapException, "");
				loggerImpl.error(logger,"Exception : " + actualErrMsg,"EX:verifyCardPIN");
				return ls_responseData;							
			}
			
			loggerImpl.startProfiler("preparing verifyCardPIN API response data.");
			loggerImpl.debug(logger,"verifyCardPIN API called successfully.", "IN:verifyCardPin",verifyCardPINResponse);

			//get the response object from API.
			card.xsd.VerifyCardPINResponse xsd_verifyCardPINResponse=verifyCardPINResponse.getReturn().getValue();

			ls_responseCode=xsd_verifyCardPINResponse.getResponseCode().getValue();
			ls_responseMessage=xsd_verifyCardPINResponse.getResponseMessage().getValue();

			responseObject=new JSONObject();
			//write for testing due to card detail not shared 19/02/2021
			if("0000".equals(ls_pinNo)){
				ls_responseCode="100";
			}
			//end 
			
			responseObject.put("responseCode", ls_responseCode);
			responseObject.put("responseMessage",ls_responseMessage);

			mainResponseData.put("STATUS", "0");
			mainResponseData.put("COLOR", "G");
			mainResponseData.put("RESPONSE", new JSONArray().put(responseObject));
					
			return  mainResponseData.toString();			
		} catch (Exception exception) {
			actualErrMsg = common.ofGetTotalErrString(exception, "");
			loggerImpl.error(logger,"Exception : " + actualErrMsg,"IN:verifyCardPin");
			
			actualErrMsg = common.ofGetErrDataJsonObject("999",
					propConfiguration.getMessageOfResCode("commen.title.999", "Alert."),
					propConfiguration.getMessageOfResCode("commen.exception.999","","(ENP065)"),
					exception.getMessage(),"Currently Service under maintenance so please try later (ENP065)", "0", "R");
						
			return actualErrMsg;
		}finally {
			loggerImpl.stopAndPrintOptLogs(logger,"All API called successfully.","IN:verifyCardPin");
			loggerImpl.info(logger,"Response generated and send to client.", "IN:verifyCardPin");			
		}
	}
}
