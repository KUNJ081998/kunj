package com.easynet.controller.ApiController.AccountAPI;

import static com.easynet.util.common.PrintErrLog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.easynet.impl.LoggerImpl;

import org.apache.ws.axis2.GetAbabilAccountDetails;
import org.apache.ws.axis2.GetAbabilAccountDetailsResponse;
import org.apache.ws.axis2.GetAccountDetails;
import org.apache.ws.axis2.GetAccountDetailsResponse;
import org.codehaus.jackson.map.ObjectMapper;
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

import city.ababil.xsd.AbabilAccount;
import city.ababil.xsd.GetAbabilAccountDetailsRequest;
import city.xsd.Account;
import city.xsd.GetAccountDetailsRequest;

/**
 * This class in contain the mathod which return the account detail.
 * @author Sagar Umate.
 * 
 * */

@Component
public class GetSoapAccountDetail {

	@Autowired
	private SOAPConnector soapConnector;
	@Autowired
	GetRequestUniqueData getRequestUniqueData;
	
	@Autowired
	PropConfiguration propConfiguration;
	
	static Logger logger=LoggerFactory.getLogger(GetSoapAccountDetail.class);
	
	/**
	 * This method used to get account detail using SOAP API of finacle customer
	 * @param requestData json format string request data.
	 * @since -23/01/2021
	 * @return This Method will return the account detail of finalce and ababil customer.
	 * @apiNote This method used below API.<br>
	 * 	1.getAccountDetails for get a/c detail.
	 *   
	 * */	
	public String GetAccountDetails(String requestData)
	{	
		String ls_acctNo="";
		String ls_userName="";
		String ls_password="";
		String ls_responseCode="";
		String ls_responseMessage="";
		String ls_responseData="";
		String ls_resFlag="";
		String actualErrMsg="";
		String ls_defCurrencyCode="";
		LoggerImpl loggerImpl=null;
		String ls_langResCodeMsg="";

		try {
			loggerImpl=new LoggerImpl();
			loggerImpl.info(logger,"Preparing requset data and calling API.","IN:GetAccountDetails");
			
			JSONObject accountrequestJson=new JSONObject(requestData);
			ls_acctNo=accountrequestJson.getString("ACCT_NO");
			ls_resFlag=accountrequestJson.getString("RES_FLAG");

			if((ls_acctNo==null ||"".equals(ls_acctNo))||(ls_resFlag==null ||"".equals(ls_resFlag)))
			{				
				ls_responseData = common.ofGetErrDataJsonArray("99",
						propConfiguration.getMessageOfResCode("commen.title.99", "Validation Failed."),
						propConfiguration.getMessageOfResCode("commen.invalid_req_data",""),		
						"Null values found in request data key.", "Invalid Request.","", "R");
				
				return ls_responseData; 
			}
			ls_userName = readXML.getXmlData("root>CUST_360>AUTHORIZATION>USER_NAME");
			ls_password = readXML.getXmlData("root>CUST_360>AUTHORIZATION>PASSWORD");
						
			/*for get the times performance logs.*/
			loggerImpl.generateProfiler("GetAccountDetails");
			loggerImpl.startProfiler("Preparing request data");
			
			//get the object factory object for get object.
			city.xsd.ObjectFactory xsdObjecyfactory=new city.xsd.ObjectFactory(); 
			//get Account detail request object from object factory
			GetAccountDetailsRequest getAccountDetailRequest=xsdObjecyfactory.createGetAccountDetailsRequest();
			//set all required field

			getAccountDetailRequest.setUsername(xsdObjecyfactory.createGetAccountDetailsRequestUsername(ls_userName)); 
			getAccountDetailRequest.setPassword(xsdObjecyfactory.createGetAccountDetailsRequestPassword(ls_password)); 
			getAccountDetailRequest.setAccountNumber(xsdObjecyfactory.createGetAccountDetailsRequestAccountNumber(ls_acctNo));

			loggerImpl.debug(logger,"Json to xml conversion done.","IN:GetAccountDetails");
			
			org.apache.ws.axis2.ObjectFactory axis2ObjectFactory=new org.apache.ws.axis2.ObjectFactory();		
			//get account detail object for store account detail request object.
			GetAccountDetails getAccountDetails=axis2ObjectFactory.createGetAccountDetails();
			getAccountDetails.setRequest(axis2ObjectFactory.createGetAccountDetailsRequest(getAccountDetailRequest));

			/*call API with requset data and get response object
			 This method uesd default url set the configuration class.
			 */
			loggerImpl.debug(logger,"getAccountDetails API calling", "IN:GetAccountDetails");
			loggerImpl.startProfiler("GetAccountDetails API calling.");
			
			GetAccountDetailsResponse getAccountDetailResponse = null;
			
			try {
				getAccountDetailResponse=(GetAccountDetailsResponse) soapConnector.callWebService(getAccountDetails);
			}catch(SoapFaultClientException soapException){					
				actualErrMsg=soapException.getFaultStringOrReason();			
				ls_responseData = common.ofGetErrDataJsonArray("999", 
						propConfiguration.getMessageOfResCode("commen.title.999", "Alert."), 
						propConfiguration.getMessageOfResCode("commen.exception.999","","(ENP040)"), actualErrMsg, 
						"Currently Service under maintenance so please try later (ENP040)", "0", "R");
								
				actualErrMsg = common.ofGetTotalErrString(soapException, "");
				loggerImpl.error(logger,"Exception : " + actualErrMsg,"EX:getAccountDetails");				
				return ls_responseData;							
			}		

			loggerImpl.startProfiler("preparing GetAccountDetails API response data.");
			loggerImpl.debug(logger,"getAccountDetails API called successfully.", "IN:GetAccountDetails",getAccountDetailResponse);
			
			/*Get the response account object*/
			city.xsd.GetAccountDetailsResponse xsd_getAccountDetailresponse=getAccountDetailResponse.getReturn().getValue();
								
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

				Account account=xsd_getAccountDetailresponse.getResponseData().getValue();
				if ("B".equals(ls_resFlag))
				{					
					accountJsonObject.put("BALANCE", account.getBalance().getValue());
					accountJsonObject.put("AVAILBALANCE", account.getAvailBalance().getValue());
					accountJsonObject.put("CURRENCYCODE", account.getCurrencyCode().getValue());
					accountJsonObject.put("UNCLEARBALANCE", account.getUnClearBalance().getValue());
					accountJsonObject.put("BRANCH_CD", account.getSolId().getValue());
					accountJsonObject.put("SYSTEMRESERVEDAMOUNT", account.getSystemReservedAmount().getValue());
					accountJsonObject.put("ACCOUNT", account.getAccount().getValue());
				}else
				{					
					accountJsonObject.put("ACCOUNT", account.getAccount().getValue());
					accountJsonObject.put("BRANCH_CD", account.getSolId().getValue());
					accountJsonObject.put("BALANCE", account.getBalance().getValue());
					accountJsonObject.put("AVAILBALANCE", account.getAvailBalance().getValue());
					accountJsonObject.put("CURRENCYCODE", account.getCurrencyCode().getValue());
					accountJsonObject.put("UNCLEARBALANCE", account.getUnClearBalance().getValue());
					accountJsonObject.put("ACCOUNTNAME", account.getAccountName().getValue());
					accountJsonObject.put("ACCTCLSDATE", account.getAcctClsDate().getValue());
					accountJsonObject.put("ACCTOPENDATE", account.getAcctOpenDate().getValue());
					accountJsonObject.put("ACCTSTATUS", account.getAcctStatus().getValue());
					accountJsonObject.put("ACCTTYPE", account.getAcctType().getValue());
					accountJsonObject.put("ADDRESS", account.getAddress().getValue());
					accountJsonObject.put("BRANCHNAME", account.getBranchName().getValue());
					accountJsonObject.put("CHEQUEALLOWED", account.getChequeAllowed().getValue());
					accountJsonObject.put("CHARGEAPPLICABLE", account.getChargeApplicable().getValue());
					accountJsonObject.put("CONTACTNUMBER", account.getContactNumber().getValue());
					accountJsonObject.put("CUSTCONST", account.getCustconst().getValue());
					accountJsonObject.put("DOB", account.getDob().getValue());
					accountJsonObject.put("FATHERNAME", account.getFatherName().getValue());
					accountJsonObject.put("LIENAMOUNT", account.getLienAmount().getValue());
					accountJsonObject.put("MOTHERNAME", account.getMotherName().getValue());
					accountJsonObject.put("NATIONALID", account.getNationalId().getValue());
					accountJsonObject.put("PASSPORT", account.getPassport().getValue());
					accountJsonObject.put("PERMANENTADDRESS", account.getPermanentAddress().getValue());
					accountJsonObject.put("PRODCUTCODE", account.getProdcutCode().getValue());
					accountJsonObject.put("SCHEMECODE", account.getSchemeCode().getValue());
					accountJsonObject.put("SCHEMETYPE", account.getSchemeType().getValue());
					accountJsonObject.put("STATUS", account.getStatus().getValue());
					accountJsonObject.put("SYSTEMRESERVEDAMOUNT", account.getSystemReservedAmount().getValue());								
				}
				accountJsonArray.put(accountJsonObject);

				responseJsonObject.put("STATUS", "0");
				responseJsonObject.put("COLOR", "G");
				responseJsonObject.put("RESPONSE", accountJsonArray);
				responseJsonObject.put("MESSAGE","");

				ls_responseData=responseJsonObject.toString();
								
			}else{ //other than 100.	
				
				ls_langResCodeMsg=propConfiguration.getResponseCode("getAccountDetails."+ls_responseCode);							
				ls_responseData = common.ofGetErrDataJsonArray(ls_langResCodeMsg, 
						propConfiguration.getMessageOfResCode("commen.title."+ls_langResCodeMsg, "Alert."),
						propConfiguration.getMessageOfResCode("getAccountDetails."+ls_responseCode,"","(ENP036)"), ls_responseMessage, 
						"Currently Service under maintenance so please try later (ENP036).",ls_responseCode, "R");
			}
		}
		catch(Exception err){			
			actualErrMsg = common.ofGetTotalErrString(err, "");
			loggerImpl.error(logger,"Exception : " + actualErrMsg,"IN:GetAccountDetails");
			ls_responseData = common.ofGetErrDataJsonArray("999", 
					propConfiguration.getMessageOfResCode("commen.title.999", "Alert."),
					propConfiguration.getMessageOfResCode("commen.exception.999","","(ENP043)"),
					err.getMessage(),"Currently Service under maintenance so please try later (ENP043).", "0", "R");
			
			return ls_responseData;
		}finally {
			loggerImpl.info(logger,"Response generated and send to client.", "IN:GetAccountDetails");
			loggerImpl.stopAndPrintOptLogs(logger,"All API called successfully.","IN:GetAccountDetails");
		}
		return ls_responseData;
	}


	/**
	 *This API return the list of account detail and balance of ababil customer..
	 *@param  requestData the request must contain account number and res_flag -D-account detail,B-balance info only.
	 *@return return balance or account detail
	 *@date 06/03/2021
	 *@apiNote This method used below API.<br>
	 *	1. getAbabilAccountDetails for get the a/c detail of ababil customer.
	 * */
	public String getAbabilAccountDetails(String requestData)
	{	
		String ls_acctNo="";
		String ls_userName="";
		String ls_password="";
		String ls_responseCode="";
		String ls_responseMessage="";
		String ls_responseData="";
		String ls_resFlag="";
		String actualErrMsg="";
		String ls_defCurrencyCode="";
		LoggerImpl loggerImpl=null;
		String ls_langResCodeMsg="";
		
		try {
			loggerImpl=new LoggerImpl();
			
			JSONObject accountrequestJson=new JSONObject(requestData);
			ls_acctNo=accountrequestJson.getString("ACCT_NO");
			ls_resFlag=accountrequestJson.getString("RES_FLAG");

			if((ls_acctNo==null ||"".equals(ls_acctNo))||(ls_resFlag==null ||"".equals(ls_resFlag)))
			{			
				ls_responseData = common.ofGetErrDataJsonArray("99",
						propConfiguration.getMessageOfResCode("commen.title.99", "Validation Failed."),
						propConfiguration.getMessageOfResCode("commen.invalid_req_data",""),		
						"Null values found in request data key.", "Invalid Request.","", "R");
				
				return ls_responseData; 
			}
			ls_userName = readXML.getXmlData("root>CUST_360>AUTHORIZATION>USER_NAME");
			ls_password = readXML.getXmlData("root>CUST_360>AUTHORIZATION>PASSWORD");

			ls_defCurrencyCode = readXML.getXmlData("root>DEF_CUR_CD>ABABIL_CUR_CD");
			loggerImpl.info(logger,"Preparing requset data and calling API.","IN:getAbabilAccountDetails");
			
			/*for get the times performance logs.*/
			loggerImpl.generateProfiler("getAbabilAccountDetails");
			loggerImpl.startProfiler("Preparing request data");
			
			//get the object factory object for get object.
			city.ababil.xsd.ObjectFactory xsdObjecyfactory=new city.ababil.xsd.ObjectFactory();  
			//get Account detail request object from object factory
			GetAbabilAccountDetailsRequest getAbabilAccountDetailRequest=xsdObjecyfactory.createGetAbabilAccountDetailsRequest();		

			//set all required field				
			getAbabilAccountDetailRequest.setUsername(xsdObjecyfactory.createGetAbabilAccountDetailsRequestUsername(ls_userName)); 
			getAbabilAccountDetailRequest.setPassword(xsdObjecyfactory.createGetAbabilAccountDetailsRequestPassword(ls_password)); 
			getAbabilAccountDetailRequest.setAccountNumber(xsdObjecyfactory.createGetAbabilAccountDetailsRequestAccountNumber(ls_acctNo));

			loggerImpl.debug(logger,"Json to xml conversion done.", "IN:getAbabilAccountDetails");
			
			org.apache.ws.axis2.ObjectFactory axis2ObjectFactory=new org.apache.ws.axis2.ObjectFactory();		
			//get account detail object for store account detail request object.
			GetAbabilAccountDetails getAbabilAccountDetails=axis2ObjectFactory.createGetAbabilAccountDetails() ;
			getAbabilAccountDetails.setRequest(axis2ObjectFactory.createGetAbabilAccountDetailsRequest(getAbabilAccountDetailRequest));

			/*call API with requset data and get response object
			 This method uesd default url set the configuration class.	
			 */
			GetAbabilAccountDetailsResponse getAbabilAccountDetailsResponse=null;
			loggerImpl.debug(logger,"getAbabilAccountDetails API calling", "IN:getAbabilAccountDetails");
			loggerImpl.startProfiler("getAbabilAccountDetails API calling.");
			
			try {
				getAbabilAccountDetailsResponse=(GetAbabilAccountDetailsResponse) soapConnector.callWebService(getAbabilAccountDetails);
			}catch(SoapFaultClientException soapException){				
				actualErrMsg=soapException.getFaultStringOrReason();			
				
				ls_responseData = common.ofGetErrDataJsonArray("999",propConfiguration.getMessageOfResCode("commen.title.999", "Alert."), 
						propConfiguration.getMessageOfResCode("commen.exception.999","","(ENP041)"),
						actualErrMsg,"Currently Service under maintenance so please try later (ENP041)", "0", "R");
								
				actualErrMsg = common.ofGetTotalErrString(soapException, "");
				loggerImpl.error(logger,"Exception : " + actualErrMsg,"EX:getAbabilAccountDetails");
				return ls_responseData;							
			}
			loggerImpl.startProfiler("preparing getAbabilAccountDetails API response data.");
			loggerImpl.debug(logger,"getAbabilAccountDetails API called successfully.", "IN:getAbabilAccountDetails",getAbabilAccountDetailsResponse);
			
			/*Get the response account object*/
			city.ababil.xsd.GetAbabilAccountDetailsResponse  xsd_getAbabilAccountDetailsResponse=getAbabilAccountDetailsResponse.getReturn().getValue();		

			//get the response code status
			ls_responseCode=xsd_getAbabilAccountDetailsResponse.getResponseCode().getValue();
			ls_responseMessage=xsd_getAbabilAccountDetailsResponse.getResponseMessage().getValue();

			/*if response is 100 then success.
			 *If response is 101 then no records found.
			 */
			if(ls_responseCode!=null && "100".equals(ls_responseCode))
			{			
				JSONObjectImpl accountJsonObject=new JSONObjectImpl();
				JSONObject responseJsonObject=new JSONObject();
				JSONArray accountJsonArray=new JSONArray();

				AbabilAccount ababilAccount=xsd_getAbabilAccountDetailsResponse.getResponseData().getValue();
				if ("B".equals(ls_resFlag))
				{
					
					accountJsonObject.put("BALANCE", String.valueOf(ababilAccount.getCurrentBalance()));
					accountJsonObject.put("AVAILBALANCE", String.valueOf(ababilAccount.getAvailableBalance()));
					accountJsonObject.put("CURRENCYCODE",ls_defCurrencyCode);
					accountJsonObject.put("UNCLEARBALANCE", "0");
					accountJsonObject.put("BRANCH_CD", String.valueOf(ababilAccount.getSolId()));
					accountJsonObject.put("SYSTEMRESERVEDAMOUNT", "0");
					accountJsonObject.put("ACCOUNT", ababilAccount.getAccount().getValue());
				}else
				{
					accountJsonObject.put("ACCOUNT", ababilAccount.getAccount().getValue());
					accountJsonObject.put("BRANCH_CD", String.valueOf(ababilAccount.getSolId()));
					accountJsonObject.put("BALANCE", String.valueOf(ababilAccount.getCurrentBalance()));
					accountJsonObject.put("AVAILBALANCE", String.valueOf(ababilAccount.getAvailableBalance()));
					accountJsonObject.put("CURRENCYCODE",ls_defCurrencyCode);
					accountJsonObject.put("UNCLEARBALANCE", "0");
					accountJsonObject.put("ACCOUNTNAME", ababilAccount.getAccountName().getValue());
					accountJsonObject.put("ACCTCLSDATE", "");//
					accountJsonObject.put("ACCTOPENDATE", "");//
					accountJsonObject.put("ACCTSTATUS",ababilAccount.getAccstatus().getValue());//
					accountJsonObject.put("ACCTTYPE", ababilAccount.getAccountType().getValue());
					accountJsonObject.put("ADDRESS", ababilAccount.getAddress().getValue());
					accountJsonObject.put("BRANCHNAME", "");//
					accountJsonObject.put("CHEQUEALLOWED","");
					accountJsonObject.put("CHARGEAPPLICABLE", "");
					accountJsonObject.put("CONTACTNUMBER",ababilAccount.getContactNumber().getValue());
					accountJsonObject.put("CUSTCONST", "");
					accountJsonObject.put("DOB", ababilAccount.getDob().getValue());
					accountJsonObject.put("FATHERNAME", ababilAccount.getFatherName().getValue());
					accountJsonObject.put("LIENAMOUNT", "0");
					accountJsonObject.put("MOTHERNAME", ababilAccount.getMotherName().getValue());
					accountJsonObject.put("NATIONALID", ababilAccount.getNationalId().getValue());
					accountJsonObject.put("PASSPORT", ababilAccount.getPassport().getValue());//
					accountJsonObject.put("PERMANENTADDRESS", ababilAccount.getPermanentAddress().getValue());
					accountJsonObject.put("PRODCUTCODE", "");
					accountJsonObject.put("SCHEMECODE", "");//
					accountJsonObject.put("SCHEMETYPE", "");
					accountJsonObject.put("STATUS", "");
					accountJsonObject.put("SYSTEMRESERVEDAMOUNT", "0");								
				}
				accountJsonArray.put(accountJsonObject);

				responseJsonObject.put("STATUS", "0");
				responseJsonObject.put("COLOR", "G");
				responseJsonObject.put("RESPONSE", accountJsonArray);
				responseJsonObject.put("MESSAGE","");

				ls_responseData=responseJsonObject.toString();
				
				
			}else{ //other than 100.	
				
				ls_langResCodeMsg=propConfiguration.getResponseCode("getAbabilAccountDetails."+ls_responseCode);			
				ls_responseData = common.ofGetErrDataJsonArray(ls_langResCodeMsg,
						propConfiguration.getMessageOfResCode("commen.title."+ls_langResCodeMsg, "Alert."),
						propConfiguration.getMessageOfResCode("getAbabilAccountDetails."+ls_responseCode,"","(ENP036)"),
						ls_responseMessage, "Currently Service under maintenance so please try later (ENP036).",ls_responseCode, "R");								
			}
		}catch(Exception err){			
			actualErrMsg = common.ofGetTotalErrString(err, "");
			loggerImpl.error(logger,"Exception : " + actualErrMsg,"IN:getAbabilAccountDetails");
			
			ls_responseData = common.ofGetErrDataJsonArray("999", 
					propConfiguration.getMessageOfResCode("commen.title.999", "Alert."), 
					propConfiguration.getMessageOfResCode("commen.exception.999","","(ENP042)"),
					err.getMessage(),"Currently Service under maintenance so please try later (ENP042)", "0", "R");
					
			return ls_responseData;
		}finally {
			loggerImpl.stopAndPrintOptLogs(logger,"All API called successfully.","IN:getAbabilAccountDetails");				
			loggerImpl.info(logger,"Response generated and send to client.", "IN:getAbabilAccountDetails");
		}
		return ls_responseData;
	}	
}
