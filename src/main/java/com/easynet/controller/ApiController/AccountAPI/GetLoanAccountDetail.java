package com.easynet.controller.ApiController.AccountAPI;

import org.apache.ws.axis2.GetLoanAccountDetails;
import org.apache.ws.axis2.GetLoanAccountDetailsResponse;
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

import city.xsd.GetLoanAccountDetailsRequest;
import city.xsd.LoanAccountDetails;

/**
 *This Class used to loan product type detail.
 *
 *@author Sagar Umate
 *@since 12/02/2021 
 * */
@Component
public class GetLoanAccountDetail {

	@Autowired
	private SOAPConnector soapConnector;
	
	@Autowired
	GetRequestUniqueData getRequestUniqueData;

	@Autowired
	PropConfiguration propConfiguration;
			
	static Logger logger=LoggerFactory.getLogger(GetLoanAccountDetail.class);	
	
	/**
	 *This method get the loan account detail for finacle source type.
	 *
	 *@param as_requestData Required String JSon Format Data.
	 *@return This method return all available account for customer id in string json format.
	 *@apiNote This method used below API.<br>
	 *	1.getLoanAccountDetails for finacle customer.
	 *@since-12/02/2021  
	 * */
	public String getAccountDetail(String as_requestData)
	{
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
			
			loggerImpl.info(logger,"Preparing requset data and calling API.","IN:getAccountDetail");
			/*for get the times performance logs.*/
			loggerImpl.generateProfiler("getAccountDetail");
			loggerImpl.startProfiler("Preparing request data");
			
			ls_userName = readXML.getXmlData("root>CUST_360>AUTHORIZATION>USER_NAME");
			ls_password = readXML.getXmlData("root>CUST_360>AUTHORIZATION>PASSWORD");

			//get the object factory object for get object.
			city.xsd.ObjectFactory xsdObjecyfactory=new city.xsd.ObjectFactory();

			//generate the request object
			GetLoanAccountDetailsRequest getLoanAccountDetailsRequest=xsdObjecyfactory.createGetLoanAccountDetailsRequest();
			//set all request object data
			getLoanAccountDetailsRequest.setAccountNumber(xsdObjecyfactory.createGetLoanAccountDetailsRequestAccountNumber(ls_acctNo));
			getLoanAccountDetailsRequest.setCbsCustomerID(xsdObjecyfactory.createGetLoanAccountDetailsRequestCbsCustomerID(ls_customerId));
			getLoanAccountDetailsRequest.setPassword(xsdObjecyfactory.createGetLoanAccountDetailsRequestPassword(ls_password));
			getLoanAccountDetailsRequest.setUsername(xsdObjecyfactory.createGetLoanAccountDetailsRequestUsername(ls_userName));

			loggerImpl.debug(logger,"Json to xml conversion done.", "IN:getAccountDetail");
			
			ObjectFactory axis2ObjectFactory=new ObjectFactory();
			//get request wrapper object
			GetLoanAccountDetails getLoanAccountDetails =axis2ObjectFactory.createGetLoanAccountDetails();
			getLoanAccountDetails.setRequest(axis2ObjectFactory.createGetLoanAccountDetailsRequest(getLoanAccountDetailsRequest));
						
			loggerImpl.debug(logger,"getLoanAccountDetails API calling", "IN:getAccountDetail");
			loggerImpl.startProfiler("getLoanAccountDetails API calling.");
			
			GetLoanAccountDetailsResponse getLoanAccountDetailsResponse=null;

			try {
				/*call API with requset data and get response object*/	
				getLoanAccountDetailsResponse=(GetLoanAccountDetailsResponse) soapConnector.callWebService(getLoanAccountDetails);
			}catch(SoapFaultClientException soapException){				
				
				actualErrMsg=soapException.getFaultStringOrReason();							
				ls_responseData = common.ofGetErrDataJsonArray("999", propConfiguration.getMessageOfResCode("commen.title.999", "Alert."), 
						propConfiguration.getMessageOfResCode("commen.exception.999","","(ENP069)"), actualErrMsg, 
						"Currently Service under maintenance so please try later (ENP069).", "0", "R");
													
				actualErrMsg = common.ofGetTotalErrString(soapException, "");
				loggerImpl.error(logger,"Exception : " + actualErrMsg,"EX:getLoanAccountDetails");
				return ls_responseData;							
			}
			
			loggerImpl.startProfiler("preparing getLoanAccountDetails API response data.");
			loggerImpl.debug(logger,"getLoanAccountDetails API called successfully.", "IN:getAccountDetail",getLoanAccountDetailsResponse);

			//get the response object from API Call
			city.xsd.GetLoanAccountDetailsResponse xsdGetLoanAccountDetailsResponse=getLoanAccountDetailsResponse.getReturn().getValue();

			//get the status code and message.
			ls_responseCode=xsdGetLoanAccountDetailsResponse.getResponseCode().getValue();
			ls_responseMessage=xsdGetLoanAccountDetailsResponse.getResponseMessage().getValue();			

			/*if response is 100 then success.
			 *If response is 101 then return with errors.
			 */
			if(ls_responseCode!=null && "100".equals(ls_responseCode))
			{			
				JSONObjectImpl accountJsonObject=new JSONObjectImpl();
				JSONObject responseJsonObject=new JSONObject();
				JSONArray accountJsonArray=new JSONArray();

				LoanAccountDetails loanAccountDetails=xsdGetLoanAccountDetailsResponse.getResponseData().getValue();			
				
				accountJsonObject.put("ACCOUNTNUMBER",loanAccountDetails.getAccountNumber().getValue());
				accountJsonObject.put("BALANCETENURE",String.valueOf(loanAccountDetails.getBalanceTenure()));
				accountJsonObject.put("CURRENCYCODE", loanAccountDetails.getCurrencyCode().getValue());
				accountJsonObject.put("CUSTOMERNAME", loanAccountDetails.getCustomerName().getValue());
				accountJsonObject.put("DISBURSALDATE", loanAccountDetails.getDisbursalDate().getValue());
				accountJsonObject.put("DISBURSALSTATUS",loanAccountDetails.getDisbursalStatus().getValue());
				accountJsonObject.put("DISBURSEDAMOUNT",String.valueOf(loanAccountDetails.getDisbursedAmount().getValue()));
				accountJsonObject.put("INSTALLMENTAMOUNT", String.valueOf(loanAccountDetails.getInstallmentAmount().getValue()));
				accountJsonObject.put("INSTALLMENTSTARTDATE",loanAccountDetails.getInstallmentStartDate().getValue());
				accountJsonObject.put("INTERESTRATE", String.valueOf(loanAccountDetails.getInterestRate().getValue()));
				accountJsonObject.put("LOANAMOUNT",String.valueOf(loanAccountDetails.getLoanAmount().getValue()));
				accountJsonObject.put("LOANENDDATE",loanAccountDetails.getLoanEndDate().getValue());
				accountJsonObject.put("LOANSTATUS", loanAccountDetails.getLoanStatus().getValue());
				accountJsonObject.put("NEXTINSTALLMENTDATE",loanAccountDetails.getNextInstallmentDate().getValue());
				accountJsonObject.put("ORIGINALTENUREMONTHS",String.valueOf(loanAccountDetails.getOriginalTenureMonths().getValue()));
				accountJsonObject.put("OUTSTANDINGINTEREST", String.valueOf(loanAccountDetails.getOutstandingInterest().getValue()));
				accountJsonObject.put("OUTSTANDINGPRINCIPAL", String.valueOf(loanAccountDetails.getOutstandingPrincipal().getValue()));
				accountJsonObject.put("OVERDUEINTEREST",String.valueOf(loanAccountDetails.getOverdueInterest().getValue()));
				accountJsonObject.put("OVERDUEOTHERCHARGES", String.valueOf(loanAccountDetails.getOverdueOtherCharges().getValue()));
				accountJsonObject.put("OVERDUEPRINCIPAL", String.valueOf(loanAccountDetails.getOverduePrincipal().getValue()));
				accountJsonObject.put("PRODUCTNAME",loanAccountDetails.getProductName().getValue());
				accountJsonObject.put("SANCTIONAMOUNT",String.valueOf(loanAccountDetails.getSanctionAmount().getValue()));
				accountJsonObject.put("SANCTIONDATE",String.valueOf(loanAccountDetails.getSanctionDate().getValue()));
				accountJsonObject.put("STATUSASON",loanAccountDetails.getStatusAsOn().getValue());
				accountJsonObject.put("TENUREBALANCEMONTHS",String.valueOf(loanAccountDetails.getTenureBalanceMonths().getValue()));
				accountJsonObject.put("TENUREBALANCEYEARS",String.valueOf(loanAccountDetails.getTenureBalanceYears().getValue()));
				accountJsonObject.put("TOTALOUTSTANDING",String.valueOf(loanAccountDetails.getTotalOutstanding().getValue()));
				accountJsonObject.put("TOTALREPAYMENTAMOUNT",String.valueOf(loanAccountDetails.getTotalRepaymentAmount().getValue()));							
				
				accountJsonArray.put(accountJsonObject);

				responseJsonObject.put("STATUS", "0");
				responseJsonObject.put("COLOR", "G");
				responseJsonObject.put("RESPONSE", accountJsonArray);
				responseJsonObject.put("MESSAGE","");

				ls_responseData=responseJsonObject.toString();
				
				
			}else{
				ls_langResCodeMsg=propConfiguration.getResponseCode("getLoanAccountDetails."+ls_responseCode);			
				
				ls_responseData = common.ofGetErrDataJsonArray(ls_langResCodeMsg, 
						propConfiguration.getMessageOfResCode("commen.title."+ls_langResCodeMsg, "Alert."),
						propConfiguration.getMessageOfResCode("getLoanAccountDetails."+ls_responseCode,"","(ENP070)")
						,ls_responseMessage,"Currently Service under maintenance so please try later (ENP070).",ls_responseCode, "R");								
			}

		}catch(Exception err){			
			actualErrMsg = common.ofGetTotalErrString(err, "");
			loggerImpl.error(logger,"Exception : " + actualErrMsg,"IN:getAccountDetail");
			
			ls_responseData = common.ofGetErrDataJsonArray("999", 
					propConfiguration.getMessageOfResCode("commen.title.999", "Alert."),
					propConfiguration.getMessageOfResCode("commen.exception.999","","(ENP071)"),
					err.getMessage(),"Currently Service under maintenance so please try later (ENP071)", "0", "R");
			
			return ls_responseData;
		}finally {			
			loggerImpl.info(logger,"Response generated and send to client.", "IN:getAccountDetail");
			loggerImpl.stopAndPrintOptLogs(logger,"All API called successfully.","IN:getAccountDetail");
		}
		return ls_responseData;		

	}
}
