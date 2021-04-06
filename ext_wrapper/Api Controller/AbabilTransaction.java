package com.easynet.controller.ApiController;

import static com.easynet.util.common.PrintErrLog;

import org.apache.ws.axis2.DoAbabilTransaction;
import org.apache.ws.axis2.DoAbabilTransactionResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.easynet.configuration.SOAPConnector;
import com.easynet.impl.JSONObjectImpl;
import com.easynet.util.common;
import com.easynet.util.readXML;

import city.xsd.FinacleTransactionRequest;
import city.xsd.ObjectFactory;

@Component
public class AbabilTransaction {

	 @Autowired
	 private SOAPConnector soapConnector;
	 
	 /*
	  * this method is used to do ababil transaction.
	  */
	 public String doAbabilTransaction(String requestData)
	 {
		 String ls_creditAccount="";
		 String ls_currencyCode="";
		 String ls_debitAccount="";
		 String ls_password="";
		 String remarks="";
		 String ls_transactionAmount="";
		 String ls_userName="";
		 String ls_responseData="";
		 String ls_responseMessage="";
		 String ls_responseCode="";
		 String transactionDateTime="";
		 String transactionRefNumber="";
		 String actualErrMsg="";
		 
		try {
			JSONObject requestJsonObj=new JSONObject(requestData);
			ls_creditAccount=requestJsonObj.getString("CREDITACCT");
			ls_currencyCode=requestJsonObj.getString("CURRENCYCODE");
			ls_debitAccount=requestJsonObj.getString("DEBITACCT");
			remarks=requestJsonObj.getString("REMARKS");
			ls_transactionAmount=requestJsonObj.getString("TRANSAMOUNT");
			
			if((ls_creditAccount==null || "".equals(ls_creditAccount)) || (ls_currencyCode==null || "".equals(ls_currencyCode)) || (ls_debitAccount==null || "".equals(ls_debitAccount))
					|| (remarks==null || "".equals(remarks)) || (ls_transactionAmount==null || "".equals(ls_transactionAmount)))
			{
				ls_responseData = common.ofGetErrDataJsonArray("99", "Validation Failed.", "InValid request.", "Null values found in acct_no key.", "","", "R");
				return ls_responseData; 
			}
			ls_userName=readXML.getXmlData("root>CUST_360>AUTHORIZATION>USER_NAME");
			ls_password=readXML.getXmlData("root>CUST_360>AUTHORIZATION>PASSWORD");
			
			//created object factory object
			city.xsd.ObjectFactory xsdObjectFactory=new city.xsd.ObjectFactory();
			FinacleTransactionRequest doAbabilTrasactionrequest=xsdObjectFactory.createFinacleTransactionRequest();
			
			doAbabilTrasactionrequest.setCreditAccount(xsdObjectFactory.createFinacleTransactionRequestCreditAccount(ls_creditAccount));
			doAbabilTrasactionrequest.setCurrencyCode(xsdObjectFactory.createFinacleTransactionRequestCurrencyCode(ls_currencyCode));
			doAbabilTrasactionrequest.setDebitAccount(xsdObjectFactory.createFinacleTransactionRequestDebitAccount(ls_debitAccount));
			doAbabilTrasactionrequest.setPassword(xsdObjectFactory.createFinacleTransactionRequestPassword(ls_password));
			doAbabilTrasactionrequest.setRemarks(xsdObjectFactory.createFinacleTransactionRequestRemarks(remarks));
			doAbabilTrasactionrequest.setTransactionAmount(xsdObjectFactory.createFinacleTransactionRequestTransactionAmount(Double.valueOf(ls_transactionAmount)));
			doAbabilTrasactionrequest.setUsername(xsdObjectFactory.createFinacleTransactionRequestUsername(ls_userName));
			
			org.apache.ws.axis2.ObjectFactory axis2objectFactory=new org.apache.ws.axis2.ObjectFactory();
			DoAbabilTransaction ababilTransaction=axis2objectFactory.createDoAbabilTransaction();
			ababilTransaction.setRequest(axis2objectFactory.createDoAbabilTransactionRequest(doAbabilTrasactionrequest));
			
			DoAbabilTransactionResponse doAbabilTransactionResponse=null;
			try {
				doAbabilTransactionResponse=(DoAbabilTransactionResponse) soapConnector.callWebService(ababilTransaction);
			}
			catch(SoapFaultClientException soapException){				
				actualErrMsg=soapException.getFaultStringOrReason();			
				PrintErrLog("DoAbabilTransaction SoapFaultClientException : " + actualErrMsg);
				ls_responseData = common.ofGetErrDataJsonArray("999", "Alert", "Currently Service under maintenance so please try later (ENP040)", actualErrMsg, "", "0", "R");
				return ls_responseData;							
			}		
			/*
			 * Creating response object from DoAbabilTrasnactionResponse.
			 */
			city.ababil.xsd.AbabilTransactionResponse xsd_ababilTransactionresponse=doAbabilTransactionResponse.getReturn().getValue();
			ls_responseCode=xsd_ababilTransactionresponse.getResponseCode().getValue();
			ls_responseMessage=xsd_ababilTransactionresponse.getResponseMessage().getValue();
			transactionDateTime=xsd_ababilTransactionresponse.getTransactionDateTime().getValue();
			transactionRefNumber=xsd_ababilTransactionresponse.getTransactionRefNumber().getValue();
			
			/*
			 * If responseCode is 100 then success.
			 * If responseCode is 101 then transaction is failed.
			 */
			 if(ls_responseCode!=null && "100".equals(ls_responseCode)) {
				   JSONObjectImpl doAbabilTransactionJsonObj=new JSONObjectImpl();
					JSONObject responseJsonObject=new JSONObject();
					JSONArray doAbabilTransactionJsonArray=new JSONArray();
					
					doAbabilTransactionJsonObj.put("RESPONSECODE", ls_responseCode);
					doAbabilTransactionJsonObj.put("RESPONSEMESSAGE", ls_responseMessage);
					doAbabilTransactionJsonObj.put("TRANSACTIONREFNUMBER", transactionRefNumber);
					doAbabilTransactionJsonObj.put("TRANSACTIONDATETIME", transactionDateTime);
					doAbabilTransactionJsonArray.put(doAbabilTransactionJsonObj);
					
					
					responseJsonObject.put("STATUS", "0");
					responseJsonObject.put("COLOR", "G");
					responseJsonObject.put("RESPONSE", doAbabilTransactionJsonArray);
					responseJsonObject.put("MESSAGE","");
					ls_responseData=responseJsonObject.toString();
		}
			 else
			 {//Response other than 100.
				 ls_responseData = common.ofGetErrDataJsonArray("999", "Alert", "Currently Service under maintenance so please try later (ENP075).", ls_responseMessage, "",ls_responseCode, "R");
			 }
		
	 }
		catch(Exception err) {
			actualErrMsg = common.ofGetTotalErrString(err, "");
			PrintErrLog("DoAbabilTransactionResponse Exception : " + actualErrMsg);
			ls_responseData = common.ofGetErrDataJsonArray("999", "Alert", "Currently Service under maintenance so please try later (ENP072)", actualErrMsg, "", "0", "R");		
		}
		return ls_responseData;
}
}
