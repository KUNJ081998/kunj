package com.easynet.controller.ApiController.AccountAPI.termDeposit;

import java.util.ArrayList;

import org.apache.ws.axis2.GetFDRAccountsSummary;
import org.apache.ws.axis2.GetFDRAccountsSummaryResponse;
import org.apache.ws.axis2.GetTermDepositAccountDetails;
import org.apache.ws.axis2.GetTermDepositAccountDetailsResponse;
import org.apache.ws.axis2.ObjectFactory;
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
import com.easynet.impl.JSONObjectImpl;
import com.easynet.impl.LoggerImpl;
import com.easynet.util.common;
import com.easynet.util.readXML;

import city.xsd.FDRAccountsSummaryResponse;
import city.xsd.GetFDRAccountsSummaryRequest;
import city.xsd.GetTermDepositAccountDetailsRequest;
import city.xsd.TermDepositAccountDetailsResponse;

/**
 *This Class used to Get TermDeposit Type Account Details.
 *@author Sagar Umate
 *@since 12/02/2021
 */

@Component
public class GetTermDepositAccountDetail {

	@Autowired
	private SOAPConnector soapConnector;

	@Autowired
	GetRequestUniqueData getRequestUniqueData;
	
	@Autowired
	PropConfiguration propConfiguration;	
	
	static Logger logger=LoggerFactory.getLogger(GetTermDepositAccountDetail.class);
	
	/**
	 *Used this method to get detail of term deposit account.
	 *@param as_requestData String format JSON Data.
	 *@return String format json data with account detail.
	 *@apiNote This method used below API. <br>
	 *		1.getTermDepositAccountDetails soap API for get team deposit ac details.
	 *@since 12/02/2021 
	 * */
	public String getTermDepAcctDetail(String as_requestData){		
		String ls_acctNo="";
		String ls_customerId="";
		String ls_userName="";
		String ls_password="";
		String ls_responseCode="";
		String ls_responseMessage="";
		String ls_responseData="";
		String actualErrMsg="";
		LoggerImpl loggerImpl=null;
		String ls_langResCodeMsg="";
		
		try {		
			
			loggerImpl=new LoggerImpl();			
			loggerImpl.info(logger,"Preparing requset data and calling API.","IN:getTermDepAcctDetail");
			
			/*for get the times performance logs.*/
			loggerImpl.generateProfiler("getTermDepAcctDetail");
			loggerImpl.startProfiler("Preparing request data.");
			
			JSONObject accountrequestJson=new JSONObject(as_requestData);
			ls_acctNo=accountrequestJson.getString("ACCT_NO");
			ls_customerId=accountrequestJson.getString("APPCUSTOMER_ID");

			if((ls_acctNo==null ||"".equals(ls_acctNo))||(ls_customerId==null ||"".equals(ls_customerId)))
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

			//create the request data object.
			GetTermDepositAccountDetailsRequest getTermDepositAccountDetailsRequest=xsdObjecyfactory.createGetTermDepositAccountDetailsRequest();			
			getTermDepositAccountDetailsRequest.setAccountNumber(xsdObjecyfactory.createGetTermDepositAccountDetailsRequestAccountNumber(ls_acctNo));
			getTermDepositAccountDetailsRequest.setCbsCustomerID(xsdObjecyfactory.createGetTermDepositAccountDetailsRequestCbsCustomerID(ls_customerId));
			getTermDepositAccountDetailsRequest.setPassword(xsdObjecyfactory.createGetTermDepositAccountDetailsRequestPassword(ls_password));
			getTermDepositAccountDetailsRequest.setUsername(xsdObjecyfactory.createGetTermDepositAccountDetailsRequestUsername(ls_userName));

			//get the request data wrapper object			
			ObjectFactory axisObjectFactory=new ObjectFactory();			
			GetTermDepositAccountDetails getTermDepositAccountDetails= axisObjectFactory.createGetTermDepositAccountDetails();			
			getTermDepositAccountDetails.setRequest(axisObjectFactory.createGetTermDepositAccountDetailsRequest(getTermDepositAccountDetailsRequest));
			loggerImpl.debug(logger,"Json to xml conversion done.", "IN:getTermDepAcctDetail");
			
			GetTermDepositAccountDetailsResponse getTermDepositAccountDetailsResponse=null;
			
			loggerImpl.debug(logger,"getTermDepositAccountDetails API calling", "IN:getTermDepAcctDetail");
			loggerImpl.startProfiler("getTermDepositAccountDetails API calling.");
			
			try {
				/*call API with request data and get response object*/	
				getTermDepositAccountDetailsResponse=(GetTermDepositAccountDetailsResponse) soapConnector.callWebService(getTermDepositAccountDetails);
			}catch(SoapFaultClientException soapException){				
				
				actualErrMsg=soapException.getFaultStringOrReason();			
				ls_responseData = common.ofGetErrDataJsonArray("999", propConfiguration.getMessageOfResCode("commen.title.999", "Alert."), 
						propConfiguration.getMessageOfResCode("commen.exception.999","","(ENP073)")
						,actualErrMsg,"Currently Service under maintenance so please try later (ENP073)", "0", "R");
										
				actualErrMsg = common.ofGetTotalErrString(soapException, "");
				loggerImpl.error(logger,"Exception : " + actualErrMsg,"EX:getTermDepositAccountDetails");
				return ls_responseData;							
			}
			
			loggerImpl.debug(logger,"getTermDepAcctDetail API called successfully.", "IN:getTermDepAcctDetail",getTermDepositAccountDetailsResponse);
			loggerImpl.startProfiler("preparing getTermDepositAccountDetails API response data.");
			
			//read the response data
			city.xsd.GetTermDepositAccountDetailsResponse xsdGetTermDepositAccountDetailsResponse=getTermDepositAccountDetailsResponse.getReturn().getValue();
			ls_responseCode=xsdGetTermDepositAccountDetailsResponse.getResponseCode().getValue();
			ls_responseMessage=xsdGetTermDepositAccountDetailsResponse.getResponseMessage().getValue();

			/*if response is 100 then success.
			 *If response is 101 then return with errors.
			 */
			if(ls_responseCode!=null && "100".equals(ls_responseCode))
			{			
				JSONObjectImpl accountJsonObject=new JSONObjectImpl();
				JSONObject responseJsonObject=new JSONObject();
				JSONArray accountJsonArray=new JSONArray();

				TermDepositAccountDetailsResponse termDepositAccountDetailsResponse=xsdGetTermDepositAccountDetailsResponse.getResponseData().getValue();			

				accountJsonObject.put("BRANCHNAME",termDepositAccountDetailsResponse.getBranchName().getValue());
				accountJsonObject.put("CURRENCYCODE",termDepositAccountDetailsResponse.getCurrencyCode().getValue());
				accountJsonObject.put("DEPOSITAMOUNT",String.valueOf(termDepositAccountDetailsResponse.getDepositAmount().getValue()));
				accountJsonObject.put("DEPOSITDATE",termDepositAccountDetailsResponse.getDepositDate().getValue());
				accountJsonObject.put("INTERESTRATE",String.valueOf(termDepositAccountDetailsResponse.getInterestRate().getValue()));
				accountJsonObject.put("MATURITYAMOUNT",String.valueOf(termDepositAccountDetailsResponse.getMaturityAmount().getValue()));
				accountJsonObject.put("MATURITYDATE",termDepositAccountDetailsResponse.getMaturityDate().getValue());

				accountJsonArray.put(accountJsonObject);

				responseJsonObject.put("STATUS", "0");
				responseJsonObject.put("COLOR", "G");
				responseJsonObject.put("RESPONSE", accountJsonArray);
				responseJsonObject.put("MESSAGE","");

				ls_responseData=responseJsonObject.toString();
																
			}else 
				{
				ls_langResCodeMsg=propConfiguration.getResponseCode("getTermDepositAccountDetails."+ls_responseCode);
				ls_responseData = common.ofGetErrDataJsonArray(ls_langResCodeMsg, 
						propConfiguration.getMessageOfResCode("commen.title."+ls_langResCodeMsg, "Alert."),
						propConfiguration.getMessageOfResCode("getTermDepositAccountDetails."+ls_responseCode,"","(ENP075)"),						
						ls_responseMessage, "Currently Service under maintenance so please try later (ENP075).",ls_responseCode, "R");
							
			}
		}catch(Exception err){			
			actualErrMsg = common.ofGetTotalErrString(err, "");
			loggerImpl.error(logger,"Exception : " + actualErrMsg,"IN:getTermDepAcctDetail");
			
			ls_responseData = common.ofGetErrDataJsonArray("999", 
					propConfiguration.getMessageOfResCode("commen.title.999", "Alert."), 
					propConfiguration.getMessageOfResCode("commen.exception.999","","(ENP077)"),				
					err.getMessage(),"Currently Service under maintenance so please try later (ENP077)", "0", "R");
						
			return ls_responseData;
		}finally {
			/*stop the profiler and print logs.*/
			loggerImpl.stopAndPrintOptLogs(logger,"All API called successfully.","IN:getTermDepAcctDetail");					
			loggerImpl.info(logger,"Response generated and send to client.", "IN:getTermDepAcctDetail");
		}
		return ls_responseData;					
	}


	/**
	 *This method will be used to get fdr account list detail as per customer id.
	 *@param requestData-String format APPcustomerID JsonData.
	 *@return List of term deposit account in string json format.
	 *@apiNote This method used below API.<br>
	 *	1.getFDRAccountsSummary
	 *@since - 22/02/2021  
	 * */
	public String getFDRAccountsSummary(String requestData){
		String ls_appCustomerID="";
		String ls_password="";
		String ls_userName="";
		String ls_responseCode="";
		String ls_responseMessage="";
		String ls_responseData="";
		String actualErrMsg="";
		ArrayList<FDRAccountsSummaryResponse> fdrAccountsSummaryList=null;
		LoggerImpl loggerImpl = null;
		String ls_langResCodeMsg="";

		try {
			loggerImpl=new LoggerImpl();
			
			loggerImpl.info(logger,"Preparing requset data and calling API.","IN:getFDRAccountsSummary");
			loggerImpl.generateProfiler("getFDRAccountsSummary");
			loggerImpl.startProfiler("Prepare request data");
			
			JSONObject requestDataObj=new JSONObject(requestData);
			ls_appCustomerID=requestDataObj.getString("APPCUSTOMER_ID");

			if(ls_appCustomerID==null || "".equals(ls_appCustomerID)) {				
				ls_responseData = common.ofGetErrDataJsonArray("99",
						propConfiguration.getMessageOfResCode("commen.title.99", "Validation Failed."),
						propConfiguration.getMessageOfResCode("commen.invalid_req_data",""),		
						"Null values found in request data key.", "Invalid Request.","", "R");
				
				return ls_responseData; 
			}
			
			ls_userName = readXML.getXmlData("root>CUST_360>AUTHORIZATION>USER_NAME");
			ls_password = readXML.getXmlData("root>CUST_360>AUTHORIZATION>PASSWORD");

			//created object factory object.
			city.xsd.ObjectFactory xsdObjectFactory=new city.xsd.ObjectFactory();
			//created get fdr accounts summary object from object factory.
			GetFDRAccountsSummaryRequest fdrAccountsSummaryRequest=xsdObjectFactory.createGetFDRAccountsSummaryRequest();

			//setting all the required fields.
			fdrAccountsSummaryRequest.setCbsCustomerID(xsdObjectFactory.createGetFDRAccountsSummaryRequestCbsCustomerID(ls_appCustomerID));
			fdrAccountsSummaryRequest.setPassword(xsdObjectFactory.createGetFDRAccountsSummaryRequestPassword(ls_password));
			fdrAccountsSummaryRequest.setUsername(xsdObjectFactory.createGetFDRAccountsSummaryRequestUsername(ls_userName));

			ObjectFactory axis2ObjectFactory=new ObjectFactory();
			//created request wrapper object.
			GetFDRAccountsSummary fdrAccountSummary=axis2ObjectFactory.createGetFDRAccountsSummary();
			fdrAccountSummary.setRequest(axis2ObjectFactory.createGetFDRAccountsSummaryRequest(fdrAccountsSummaryRequest));
			
			loggerImpl.debug(logger,"Json to xml conversion done.", "IN:getFDRAccountsSummary");
			
			//created get fdr account response object.
			GetFDRAccountsSummaryResponse getFDRAccountsSummaryresponse=null;
			
			loggerImpl.debug(logger,"getFDRAccountsSummary API calling", "IN:getFDRAccountsSummary");
			loggerImpl.startProfiler("getFDRAccountsSummary API calling");

			try {
				//calling the API with request data and getting response object.
				getFDRAccountsSummaryresponse=(GetFDRAccountsSummaryResponse) soapConnector.callWebService(fdrAccountSummary);
			}catch(SoapFaultClientException soapException){				
				actualErrMsg=soapException.getFaultStringOrReason();							
				
				ls_responseData = common.ofGetErrDataJsonArray("999", propConfiguration.getMessageOfResCode("commen.title.999", "Alert."),
						propConfiguration.getMessageOfResCode("commen.exception.999","","(ENP087)"), 
						actualErrMsg,"Currently Service under maintenance so please try later (ENP087).", "0", "R");
								
				actualErrMsg=common.ofGetTotalErrString(soapException, "");
				loggerImpl.error(logger,"Exception : " + actualErrMsg,"EX:getFDRAccountsSummary");
				return ls_responseData;							
			}
			
			loggerImpl.startProfiler("generating response data");			
			loggerImpl.debug(logger,"getFDRAccountsSummary API called successfully.", "IN:getFDRAccountsSummary",getFDRAccountsSummaryresponse);
			
			//get the response object from API.
			city.xsd.GetFDRAccountsSummaryResponse xsdgetFDRAccountsSummaryresponse=getFDRAccountsSummaryresponse.getReturn().getValue();
			
			loggerImpl.debug(logger, "preparing response data", "IN:getFDRAccountsSummary");
			
			ls_responseCode=xsdgetFDRAccountsSummaryresponse.getResponseCode().getValue();
			ls_responseMessage=xsdgetFDRAccountsSummaryresponse.getResponseMessage().getValue();

			/*if response is 100 then success.*/
			if(ls_responseCode!=null && "100".equals(ls_responseCode)) {

				JSONObject responseJsonObject=new JSONObject();
				JSONArray fdrAccountSummayList=new JSONArray();

				fdrAccountsSummaryList=(ArrayList<FDRAccountsSummaryResponse>) xsdgetFDRAccountsSummaryresponse.getResponseData();

				for (FDRAccountsSummaryResponse accountsummarylist : fdrAccountsSummaryList) {

					JSONObjectImpl fdrAccountSummayObj=new JSONObjectImpl();
					fdrAccountSummayObj.put("ACCOUNTNUMBER", accountsummarylist.getAccountNumber().getValue());
					fdrAccountSummayObj.put("ACCOUNTSTATUS", accountsummarylist.getAccountStatus().getValue());
					fdrAccountSummayObj.put("ACCOUNTTITLE", accountsummarylist.getAccountTitle().getValue());
					fdrAccountSummayObj.put("ACCOUNTTYPE", accountsummarylist.getAccountType().getValue());
					fdrAccountSummayObj.put("BALANCE", accountsummarylist.getBalance());
					fdrAccountSummayObj.put("BRANCHCODE", accountsummarylist.getBranchCode().getValue());
					fdrAccountSummayObj.put("BRANCHNAME", accountsummarylist.getBranchName().getValue());
					fdrAccountSummayObj.put("CURRENCYCODE", accountsummarylist.getCurrencyCode().getValue());
					fdrAccountSummayObj.put("INTERESTRATE",accountsummarylist.getInterestRate());
					fdrAccountSummayObj.put("LEDGERBALANCE", accountsummarylist.getLedgerBalance().getValue());
					fdrAccountSummayObj.put("PRODUCTNAME", accountsummarylist.getProductName().getValue());
					fdrAccountSummayObj.put("TENOR", accountsummarylist.getTenor().getValue());

					fdrAccountSummayList.put(fdrAccountSummayObj);
				}

				responseJsonObject.put("STATUS","0");
				responseJsonObject.put("COLOR","G");
				responseJsonObject.put("RESPONSE",fdrAccountSummayList);
				responseJsonObject.put("MESSAGE","");
				ls_responseData=responseJsonObject.toString();

				
			}else{ //Response other than 100.
				ls_langResCodeMsg=propConfiguration.getResponseCode("getFDRAccountsSummary."+ls_responseCode);
				ls_responseData = common.ofGetErrDataJsonArray(ls_langResCodeMsg,
						propConfiguration.getMessageOfResCode("commen.title."+ls_langResCodeMsg, "Alert."),						 
						propConfiguration.getMessageOfResCode("getFDRAccountsSummary."+ls_responseCode,"","(ENP088)"),
						ls_responseMessage, "Currently Service under maintenance so please try later (ENP088)."
						,ls_responseCode, "R");					
			}
			
		}catch(Exception exception) {
			actualErrMsg = common.ofGetTotalErrString(exception, "");
			loggerImpl.error(logger,"Exception : " + actualErrMsg,"IN:getFDRAccountsSummary");
			ls_responseData = common.ofGetErrDataJsonArray("999", 
					propConfiguration.getMessageOfResCode("commen.title.999", "Alert."),
					propConfiguration.getMessageOfResCode("commen.exception.999","","(ENP089)"), exception.getMessage(), 
					"Currently Service under maintenance so please try later (ENP089).", "0", "R");	
								
		}finally {
			loggerImpl.info(logger,"Response generated and send to client.", "IN:getFDRAccountsSummary");
			loggerImpl.stopAndPrintOptLogs(logger, "Response generated.", "IN:getFDRAccountsSummary");
		}
		return ls_responseData;
	}
}
