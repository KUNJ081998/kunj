package com.easynet.controller.ApiController.CardAPI.UpdateDetail;

import org.apache.ws.axis2.ChangeCardEmailAdderss;
import org.apache.ws.axis2.ChangeCardEmailAdderssResponse;
import org.apache.ws.axis2.ChangeCardMobileNumber;
import org.apache.ws.axis2.ChangeCardMobileNumberResponse;
import org.apache.ws.axis2.ObjectFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.easynet.configuration.PropConfiguration;
import com.easynet.configuration.SOAPConnector;
import com.easynet.impl.JSONObjectImpl;
import com.easynet.util.common;
import com.easynet.util.readXML;

import city.xsd.ChangeCardEmailAdderssRequest;
import city.xsd.ChangeCardMobileNumberRequest;
import city.xsd.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.easynet.impl.LoggerImpl;

/**
 *This class used to update the card detail using API and also in database.
 *
 *@author Sagar Umate
 *@since 12/02/2021
 * */
@Component
public class DoUpdateCardDetail {

	@Autowired
	private SOAPConnector soapConnector;
		
	@Autowired
	PropConfiguration propConfiguration;
	
	static Logger logger=LoggerFactory.getLogger(DoUpdateCardDetail.class);
	

	/**
	 *This Method are used for update the email address for Card using client id and also update the detail
	 *	in database.
	 *
	 *@param as_requestData String format json request data.
	 *@return  return Json data with success or failed detail.
	 *@apiNote This method used below API.<br>
	 *	1.ChangeCardEmailAdderss used to update the email id on card.
	 * 
	 *@since 12/02/2021
	 */
	public String ofUpdateCardEmailId(String as_requestData)
	{			
		String ls_newEmailID="";
		String ls_OLdnewEmailID="";
		String ls_customerId="";
		String ls_userName="";
		String ls_password="";
		String ls_responseCode="";
		String ls_responseMessage="";
		String ls_responseData="";
		String actualErrMsg="";
		LoggerImpl loggerImpl=null;
		

		try {			
			loggerImpl=new LoggerImpl();
			
			JSONObject accountrequestJson=new JSONObject(as_requestData);
			ls_newEmailID=accountrequestJson.getString("NEWEMAILID");
			ls_customerId=accountrequestJson.getString("APPCUSTOMER_ID");
			ls_OLdnewEmailID=accountrequestJson.getString("OLDEMAILID");

			if((ls_newEmailID==null ||"".equals(ls_newEmailID))||(ls_customerId==null ||"".equals(ls_customerId))
					||(ls_OLdnewEmailID==null ||"".equals(ls_OLdnewEmailID)))
			{
				ls_responseData = common.ofGetErrDataJsonArray("99",
						propConfiguration.getMessageOfResCode("commen.title.99", "Validation Failed."),
						propConfiguration.getMessageOfResCode("commen.invalid_req_data",""),		
						"Null values found in request data.", "Invalid Request.","", "R");
				
				return ls_responseData; 
			}					
			loggerImpl.info(logger,"Preparing requset data and calling API.","IN:ofUpdateCardEmailId");
			
			/*for get the times performance logs.*/
			loggerImpl.generateProfiler("ofUpdateCardEmailId");
			loggerImpl.startProfiler("Preparing request data");
			
			ls_userName = readXML.getXmlData("root>CUST_360>AUTHORIZATION>USER_NAME");
			ls_password = readXML.getXmlData("root>CUST_360>AUTHORIZATION>PASSWORD");

			//get the object factory object for get object.
			city.xsd.ObjectFactory xsdObjecyfactory=new city.xsd.ObjectFactory();

			//set the request data
			ChangeCardEmailAdderssRequest changeCardEmailAdderssRequest=xsdObjecyfactory.createChangeCardEmailAdderssRequest();
			changeCardEmailAdderssRequest.setClientNumber(xsdObjecyfactory.createChangeCardEmailAdderssRequestClientNumber(ls_customerId));
			changeCardEmailAdderssRequest.setNewEmailAdderss(xsdObjecyfactory.createChangeCardEmailAdderssRequestNewEmailAdderss(ls_newEmailID));
			changeCardEmailAdderssRequest.setOldEmailAdderss(xsdObjecyfactory.createChangeCardEmailAdderssRequestOldEmailAdderss(ls_OLdnewEmailID));
			changeCardEmailAdderssRequest.setPassword(xsdObjecyfactory.createChangeCardEmailAdderssRequestPassword(ls_password));
			changeCardEmailAdderssRequest.setUsername(xsdObjecyfactory.createChangeCardEmailAdderssRequestUsername(ls_userName));
			
			loggerImpl.debug(logger,"Json to xml conversion done.", "IN:ofUpdateCardEmailId");
			
			//get the request wrapper object
			ObjectFactory axisObjectFactory=new ObjectFactory();			
			ChangeCardEmailAdderss changeCardEmailAdderss=axisObjectFactory.createChangeCardEmailAdderss();
			changeCardEmailAdderss.setRequest(axisObjectFactory.createChangeCardEmailAdderssRequest(changeCardEmailAdderssRequest));

			ChangeCardEmailAdderssResponse changeCardEmailAdderssResponse=null;
			loggerImpl.debug(logger,"ChangeCardEmailAdderss API calling.", "IN:ofUpdateCardEmailId");
			loggerImpl.startProfiler("ChangeCardEmailAdderss API calling.");
			
			try {
				/*call API with requset data and get response object*/	
				changeCardEmailAdderssResponse=(ChangeCardEmailAdderssResponse) soapConnector.callWebService(changeCardEmailAdderss);
			}catch(SoapFaultClientException soapException){				
				actualErrMsg=soapException.getFaultStringOrReason();			
				ls_responseData = common.ofGetErrDataJsonArray("999", 
						propConfiguration.getMessageOfResCode("commen.title.999", "Alert."),
						propConfiguration.getMessageOfResCode("commen.exception.999","","(ENP079)"),
						actualErrMsg,"Currently Service under maintenance so please try later (ENP079)", "0", "R");
								
				actualErrMsg = common.ofGetTotalErrString(soapException, "");
				loggerImpl.error(logger,"Exception : " + actualErrMsg,"EX:ChangeCardEmailAdderss");
				return ls_responseData;							
			}
			
			loggerImpl.startProfiler("preparing ChangeCardEmailAdderss API response data.");			
			loggerImpl.debug(logger,"ChangeCardEmailAdderss API called successfully.", "IN:ofUpdateCardEmailId",changeCardEmailAdderssResponse);
			
			Response response= changeCardEmailAdderssResponse.getReturn().getValue();

			ls_responseCode=response.getResponseCode().getValue();
			ls_responseMessage=response.getResponseMessage().getValue();

			/*if response is 100 then success.*/
			//do check status because we need response detail in case of failed also.		
			JSONObjectImpl accountJsonObject=new JSONObjectImpl();
			JSONObject responseJsonObject=new JSONObject();
			JSONArray accountJsonArray=new JSONArray();

			accountJsonObject.put("RESPONSEMESSAGE",ls_responseMessage);
			accountJsonObject.put("RESPONSECODE",ls_responseCode);
			accountJsonArray.put(accountJsonObject);

			responseJsonObject.put("STATUS", "0");
			responseJsonObject.put("COLOR", "G");
			responseJsonObject.put("RESPONSE", accountJsonArray);
			responseJsonObject.put("MESSAGE",ls_responseMessage);
		
			ls_responseData=responseJsonObject.toString();
									
		}catch(Exception err){			
			actualErrMsg = common.ofGetTotalErrString(err, "");
			loggerImpl.error(logger,"Exception : " + actualErrMsg,"IN:ofUpdateCardEmailId");
			ls_responseData = common.ofGetErrDataJsonArray("999",
					propConfiguration.getMessageOfResCode("commen.title.999", "Alert."),
					propConfiguration.getMessageOfResCode("commen.exception.999","","(ENP081)"),
					err.getMessage(),"Currently Service under maintenance so please try later (ENP081)",
					 "0", "R");
						
		}finally {
			loggerImpl.stopAndPrintOptLogs(logger,"All API called successfully.","IN:ofUpdateCardEmailId");
			loggerImpl.info(logger,"Response generated and send to client.", "IN:ofUpdateCardEmailId");
		}
		return ls_responseData;
	}

	/**
	 *This Method are used for update the Mobile Number for Card using client id and also update the detail
	 *	in database.
	 *
	 *@param as_requestData String format json request data.
	 *@return return Json data with success or failed detail. 
	 *@since 12/02/2021
	 *@apiNote This method used below API.<br>
	 *	1.ChangeCardMobileNumber update mobile no. on card.
	 *
	 */
	public String ofUpdateCardMobileNo(String as_requestData)
	{			
		String ls_newMobileNO="";
		String ls_oldMobileNO="";
		String ls_customerId="";
		String ls_userName="";
		String ls_password="";
		String ls_responseCode="";
		String ls_responseMessage="";
		String ls_responseData="";
		String actualErrMsg="";
		LoggerImpl loggerImpl=null;

		try {			
			loggerImpl=new LoggerImpl();
			
			JSONObject accountrequestJson=new JSONObject(as_requestData);
			ls_newMobileNO=accountrequestJson.getString("NEWMOBILENO");
			ls_customerId=accountrequestJson.getString("APPCUSTOMER_ID");
			ls_oldMobileNO=accountrequestJson.getString("OLDMOBILENO");

			if((ls_newMobileNO==null ||"".equals(ls_newMobileNO))||(ls_customerId==null ||"".equals(ls_customerId))
					||(ls_oldMobileNO==null ||"".equals(ls_oldMobileNO)))
			{
				ls_responseData = common.ofGetErrDataJsonArray("99",
						propConfiguration.getMessageOfResCode("commen.title.99", "Validation Failed."),
						propConfiguration.getMessageOfResCode("commen.invalid_req_data",""),		
						"Null values found in request data.", "Invalid Request.","", "R");
							
				return ls_responseData; 
			}					

			loggerImpl.info(logger,"Preparing requset data and calling API.","IN:ofUpdateCardMobileNo");
			/*for get the times performance logs.*/
			loggerImpl.generateProfiler("ofUpdateCardMobileNo");
			loggerImpl.startProfiler("Preparing request data");
			
			ls_userName = readXML.getXmlData("root>CUST_360>AUTHORIZATION>USER_NAME");
			ls_password = readXML.getXmlData("root>CUST_360>AUTHORIZATION>PASSWORD");

			//get the object factory object for get object.
			city.xsd.ObjectFactory xsdObjecyfactory=new city.xsd.ObjectFactory();

			//set the request data
			ChangeCardMobileNumberRequest changeCardMobileNumberRequest=xsdObjecyfactory.createChangeCardMobileNumberRequest();
			changeCardMobileNumberRequest.setClientNumber(xsdObjecyfactory.createChangeCardMobileNumberRequestClientNumber(ls_customerId));
			changeCardMobileNumberRequest.setNewNumber(xsdObjecyfactory.createChangeCardMobileNumberRequestNewNumber(ls_newMobileNO));
			changeCardMobileNumberRequest.setOldNumber(xsdObjecyfactory.createChangeCardMobileNumberRequestOldNumber(ls_oldMobileNO));
			changeCardMobileNumberRequest.setPassword(xsdObjecyfactory.createChangeCardMobileNumberRequestPassword(ls_password));
			changeCardMobileNumberRequest.setUsername(xsdObjecyfactory.createChangeCardMobileNumberRequestUsername(ls_userName));
			
			loggerImpl.debug(logger,"Json to xml conversion done.", "IN:ofUpdateCardMobileNo");
			
			//get the request wrapper object
			ObjectFactory axisObjectFactory=new ObjectFactory();						
			
			ChangeCardMobileNumber changeCardMobileNumber=axisObjectFactory.createChangeCardMobileNumber();
			changeCardMobileNumber.setRequest(axisObjectFactory.createChangeCardMobileNumberRequest(changeCardMobileNumberRequest));

			ChangeCardMobileNumberResponse changeCardMobileNumberResponse=null;
			
			loggerImpl.debug(logger,"ChangeCardMobileNumber API calling", "IN:ofUpdateCardMobileNo");
			loggerImpl.startProfiler("ChangeCardMobileNumber API calling.");
			
			try {
				/*call API with request data and get response object*/	
				changeCardMobileNumberResponse=(ChangeCardMobileNumberResponse) soapConnector.callWebService(changeCardMobileNumber);
			}catch(SoapFaultClientException soapException){				
				actualErrMsg=soapException.getFaultStringOrReason();			
				ls_responseData = common.ofGetErrDataJsonArray("999",
						propConfiguration.getMessageOfResCode("commen.title.999", "Alert."), 
						propConfiguration.getMessageOfResCode("commen.exception.999","","(ENP084)"),
						actualErrMsg,"Currently Service under maintenance so please try later (ENP084)", "0", "R");
								
				actualErrMsg = common.ofGetTotalErrString(soapException, "");
				loggerImpl.error(logger,"Exception : " + actualErrMsg,"EX:ChangeCardMobileNumber");
				return ls_responseData;							
			}
			
			loggerImpl.startProfiler("preparing ChangeCardMobileNumber API response data.");
			loggerImpl.debug(logger,"ChangeCardMobileNumber API called successfully.", "IN:ofUpdateCardMobileNo",changeCardMobileNumberResponse);
			
			Response response= changeCardMobileNumberResponse.getReturn().getValue();

			ls_responseCode=response.getResponseCode().getValue();
			ls_responseMessage=response.getResponseMessage().getValue();

			/*if response is 100 then success.*/
			//do not check status of API because we need response detail. 		
			JSONObjectImpl accountJsonObject=new JSONObjectImpl();
			JSONObject responseJsonObject=new JSONObject();
			JSONArray accountJsonArray=new JSONArray();

			accountJsonObject.put("RESPONSECODE",ls_responseCode);
			accountJsonObject.put("RESPONSEMESSAGE",ls_responseMessage);
			accountJsonArray.put(accountJsonObject);

			responseJsonObject.put("STATUS", "0");
			responseJsonObject.put("COLOR", "G");
			responseJsonObject.put("RESPONSE", accountJsonArray);
			responseJsonObject.put("MESSAGE",ls_responseMessage);
			
			ls_responseData=responseJsonObject.toString();			
			
		}catch(Exception err){			
			actualErrMsg = common.ofGetTotalErrString(err, "");
			loggerImpl.error(logger,"Exception : " + actualErrMsg,"IN:ofUpdateCardMobileNo");
			ls_responseData = common.ofGetErrDataJsonArray("999",
					propConfiguration.getMessageOfResCode("commen.title.999", "Alert."),
					propConfiguration.getMessageOfResCode("commen.exception.999","","(ENP082)"),
					err.getMessage(),"Currently Service under maintenance so please try later (ENP082)", "0", "R");
						
		}finally {
			loggerImpl.info(logger,"Response generated and send to client.", "IN:ofUpdateCardMobileNo");
			loggerImpl.stopAndPrintOptLogs(logger,"All API called successfully.","IN:ofUpdateCardMobileNo");
		}
		return ls_responseData;
	}
}
