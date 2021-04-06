package com.easynet.controller.ApiController.AccountAPI.updateDetail;

import org.apache.ws.axis2.ChangeFinacleEmailAdderss;
import org.apache.ws.axis2.ChangeFinacleEmailAdderssResponse;
import org.apache.ws.axis2.ChangeFinacleMobileNumber;
import org.apache.ws.axis2.ChangeFinacleMobileNumberResponse;
import org.apache.ws.axis2.ObjectFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.soap.client.SoapFaultClientException;
import org.springframework.ws.transport.http.HttpUrlConnectionMessageSender;

import com.easynet.bean.GetRequestUniqueData;
import com.easynet.configuration.PropConfiguration;
import com.easynet.configuration.SOAPConnector;
import com.easynet.impl.JSONObjectImpl;
import com.easynet.impl.LoggerImpl;
import com.easynet.util.common;
import com.easynet.util.readXML;

import city.xsd.ChangeFinacleEmailAdderssRequest;
import city.xsd.ChangeFinacleMobileNumberRequest;
import city.xsd.Response;

/**
 *This class are used to update the finacle account details.
 *@author sagar Umate
 *@since 12/02/2021
 *
 * */
@Component
public class DoUpdateAcctDetail {

	@Autowired
	private SOAPConnector soapConnector;
	
	@Autowired
	GetRequestUniqueData getRequestUniqueData;
	
	static Logger logger=LoggerFactory.getLogger(DoUpdateAcctDetail.class);
	
	@Autowired
	PropConfiguration propConfiguration;
	
	/**
	 *This Method are used for update the email address in finacle account using customer id and also update the detail
	 *	in database.
	 *
	 *@param as_requestData String format json request data.
	 *@return return Json data with sucess or failed detail. 
	 *@apiNote This method used below API.<br>
	 *	1.ChangeFinacleEmailAdderss for update finacle email add.
	 *@date 12/02/2021
	 */
	public String ofUpdateFinacleEmailId(String as_requestData)
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
			
			loggerImpl.info(logger,"Preparing requset data and calling API.","IN:ofUpdateFinacleEmailId");
			
			/*for get the times performance logs.*/
			loggerImpl.generateProfiler("ofUpdateFinacleEmailId");
			loggerImpl.startProfiler("Preparing request data");
			
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
						"Null values found in request data key.", "Invalid Request.","", "R");
				
				return ls_responseData; 
			}					

			ls_userName = readXML.getXmlData("root>CUST_360>AUTHORIZATION>USER_NAME");
			ls_password = readXML.getXmlData("root>CUST_360>AUTHORIZATION>PASSWORD");

			//get the object factory object for get object.
			city.xsd.ObjectFactory xsdObjecyfactory=new city.xsd.ObjectFactory();

			//set the request data
			ChangeFinacleEmailAdderssRequest changeFinacleEmailAddressRequest=xsdObjecyfactory.createChangeFinacleEmailAdderssRequest();
			changeFinacleEmailAddressRequest.setCBNumber(xsdObjecyfactory.createChangeFinacleEmailAdderssRequestCBNumber(ls_customerId));
			changeFinacleEmailAddressRequest.setNewEmailAdderss(xsdObjecyfactory.createChangeFinacleEmailAdderssRequestNewEmailAdderss(ls_newEmailID));
			changeFinacleEmailAddressRequest.setOldEmailAdderss(xsdObjecyfactory.createChangeFinacleEmailAdderssRequestOldEmailAdderss(ls_OLdnewEmailID));
			changeFinacleEmailAddressRequest.setPassword(xsdObjecyfactory.createChangeFinacleEmailAdderssRequestPassword(ls_password));
			changeFinacleEmailAddressRequest.setUsername(xsdObjecyfactory.createChangeFinacleEmailAdderssRequestUsername(ls_userName));

			//get the request wrapper object
			ObjectFactory axisObjectFactory=new ObjectFactory();			
			ChangeFinacleEmailAdderss  changeFinalceEmailAddress =axisObjectFactory.createChangeFinacleEmailAdderss();
			changeFinalceEmailAddress.setRequest(axisObjectFactory.createChangeFinacleEmailAdderssRequest(changeFinacleEmailAddressRequest));
			
			loggerImpl.debug(logger,"Json to xml conversion done.","IN:ofUpdateFinacleEmailId");
			
			ChangeFinacleEmailAdderssResponse changeFinacleEmailAddressResponse=null;
			
			loggerImpl.debug(logger,"ChangeFinacleEmailAdderss API calling","IN:ofUpdateFinacleEmailId");
			
			loggerImpl.startProfiler("ChangeFinacleEmailAdderss API calling.");
			
			try {
				/*call API with requset data and get response object*/	
				changeFinacleEmailAddressResponse=(ChangeFinacleEmailAdderssResponse) soapConnector.callWebService(changeFinalceEmailAddress);
			}catch(SoapFaultClientException soapException){				
				actualErrMsg=soapException.getFaultStringOrReason();							
				ls_responseData = common.ofGetErrDataJsonArray("999", propConfiguration.getMessageOfResCode("commen.title.999", "Alert."),
						propConfiguration.getMessageOfResCode("commen.exception.999","","(ENP074)"), actualErrMsg,
						"Currently Service under maintenance so please try later (ENP074)", "0", "R");
				
				actualErrMsg = common.ofGetTotalErrString(soapException, "");
				loggerImpl.error(logger,"Exception : " + actualErrMsg,"EX:ChangeFinacleEmailAdderss");		
				return ls_responseData;							
			}
			
			loggerImpl.debug(logger,"ChangeFinacleEmailAdderss API called successfully.", "IN:ofUpdateFinacleEmailId",changeFinacleEmailAddressResponse);
			loggerImpl.startProfiler("preparing ChangeFinacleEmailAdderss API response data.");
			
			Response response= changeFinacleEmailAddressResponse.getReturn().getValue();

			ls_responseCode=response.getResponseCode().getValue();
			ls_responseMessage=response.getResponseMessage().getValue();

			/*if response is 100 then success.*/
			//do not check status here due to we need response code in both case failed or successes.
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
			loggerImpl.error(logger,"Exception : " + actualErrMsg,"IN:ofUpdateFinacleEmailId");
			
			ls_responseData = common.ofGetErrDataJsonArray("999",
					propConfiguration.getMessageOfResCode("commen.title.999", "Alert."), 
					propConfiguration.getMessageOfResCode("commen.exception.999","","(ENP072)"), 
					err.getMessage(),"Currently Service under maintenance so please try later (ENP072)", "0", "R");
					
		}finally {
			loggerImpl.stopAndPrintOptLogs(logger,"All API called successfully.","IN:ofUpdateFinacleEmailId");
			loggerImpl.info(logger,"Response generated and send to client.", "IN:ofUpdateFinacleEmailId");
		}
		return ls_responseData;
	}

	/**
	 *This Method are used for update the Mobile Number in finacle account using customer id and also update the detail
	 *	in database.
	 *
	 *@param as_requestData String format json request data.
	 *@return return Json data with sucess or failed detail. 
	 *@apiNote This method used below API.<br>
	 *	1.ChangeFinacleMobileNumber used for update finacle mobile no.
	 *@since 12/02/2021
	 */
	public String ofUpdateFinacleMobileNo(String as_requestData)
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
			loggerImpl.info(logger,"Preparing requset data and calling API.","IN:ofUpdateFinacleMobileNo");
			
			/*for get the times performance logs.*/
			loggerImpl.generateProfiler("ofUpdateFinacleMobileNo");
			loggerImpl.startProfiler("Preparing request data");
			
			JSONObject accountrequestJson=new JSONObject(as_requestData);
			ls_newMobileNO=accountrequestJson.getString("NEWMOBILENO");
			ls_customerId=accountrequestJson.getString("APPCUSTOMER_ID");
			ls_oldMobileNO=accountrequestJson.getString("OLDMOBILENO");

			if((ls_newMobileNO==null ||"".equals(ls_newMobileNO))||(ls_customerId==null ||"".equals(ls_customerId))
					||(ls_oldMobileNO==null ||"".equals(ls_oldMobileNO))){
				
				ls_responseData = common.ofGetErrDataJsonArray("99",
						propConfiguration.getMessageOfResCode("commen.title.99", "Validation Failed."),
						propConfiguration.getMessageOfResCode("commen.invalid_req_data",""),		
						"Null values found in request data key.", "Invalid Request.","", "R");
							
				return ls_responseData; 
			}					

			ls_userName = readXML.getXmlData("root>CUST_360>AUTHORIZATION>USER_NAME");
			ls_password = readXML.getXmlData("root>CUST_360>AUTHORIZATION>PASSWORD");

			//get the object factory object for get object.
			city.xsd.ObjectFactory xsdObjecyfactory=new city.xsd.ObjectFactory();

			//set the request data
			ChangeFinacleMobileNumberRequest changeFinacleMobileNumberRequest=xsdObjecyfactory.createChangeFinacleMobileNumberRequest();
			changeFinacleMobileNumberRequest.setCBNumber(xsdObjecyfactory.createChangeFinacleMobileNumberRequestCBNumber(ls_customerId));
			changeFinacleMobileNumberRequest.setNewNumber(xsdObjecyfactory.createChangeFinacleMobileNumberRequestNewNumber(ls_newMobileNO));
			changeFinacleMobileNumberRequest.setOldNumber(xsdObjecyfactory.createChangeFinacleMobileNumberRequestOldNumber(ls_oldMobileNO));
			changeFinacleMobileNumberRequest.setPassword(xsdObjecyfactory.createChangeFinacleMobileNumberRequestPassword(ls_password));
			changeFinacleMobileNumberRequest.setUsername(xsdObjecyfactory.createChangeFinacleMobileNumberRequestUsername(ls_userName));						
			
			loggerImpl.debug(logger,"Json to xml conversion done.", "IN:ofUpdateFinacleMobileNo");
			//get the request wrapper object
			ObjectFactory axisObjectFactory=new ObjectFactory();						
			ChangeFinacleMobileNumber changeFinalceMobileNumber=axisObjectFactory.createChangeFinacleMobileNumber();
			changeFinalceMobileNumber.setRequest(axisObjectFactory.createChangeFinacleMobileNumberRequest(changeFinacleMobileNumberRequest));
						
			loggerImpl.debug(logger,"ChangeFinacleMobileNumber API calling", "IN:ofUpdateFinacleMobileNo");
			loggerImpl.startProfiler("ChangeFinacleMobileNumber API calling.");
			
			ChangeFinacleMobileNumberResponse changeFinacleMobileNumberResponse=null;

			try {
								
				/*call API with requset data and get response object*/	
				changeFinacleMobileNumberResponse=(ChangeFinacleMobileNumberResponse) soapConnector.callWebService(changeFinalceMobileNumber);
			}catch(SoapFaultClientException soapException){				
				actualErrMsg=soapException.getFaultStringOrReason();			
				ls_responseData = common.ofGetErrDataJsonArray("999", 
						propConfiguration.getMessageOfResCode("commen.title.999", "Alert."),
						propConfiguration.getMessageOfResCode("commen.exception.999","","(ENP076)"), actualErrMsg, 
						"Currently Service under maintenance so please try later (ENP076)", "0", "R");
				
				actualErrMsg = common.ofGetTotalErrString(soapException, "");
				loggerImpl.error(logger,"Exception : " + actualErrMsg,"EX:ChangeFinacleMobileNumber");
				return ls_responseData;							
			}
			
			loggerImpl.startProfiler("preparing ChangeFinacleMobileNumber API response data.");			
			loggerImpl.debug(logger,"ChangeFinacleMobileNumber API called successfully.", "IN:ofUpdateFinacleMobileNo",changeFinacleMobileNumberResponse);
			
			Response response= changeFinacleMobileNumberResponse.getReturn().getValue();

			ls_responseCode=response.getResponseCode().getValue();
			ls_responseMessage=response.getResponseMessage().getValue();
			
			//do not check status here due to we need response code in both case failed or successes.		
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
			loggerImpl.error(logger,"Exception : " + actualErrMsg,"IN:ofUpdateFinacleMobileNo");
			
			ls_responseData = common.ofGetErrDataJsonArray("999", 
					propConfiguration.getMessageOfResCode("commen.title.999", "Alert."),
					propConfiguration.getMessageOfResCode("commen.exception.999","","(ENP078)"),
					err.getMessage(),"Currently Service under maintenance so please try later (ENP078)", "0", "R");	
				
		}finally {
			loggerImpl.info(logger,"Response generated and send to client.", "IN:ofUpdateFinacleMobileNo");
			loggerImpl.stopAndPrintOptLogs(logger,"All API called successfully.","IN:ofUpdateFinacleMobileNo");
		}
		return ls_responseData;
	}
}
