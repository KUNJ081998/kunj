package com.easynet.controller.ApiController;

import static com.easynet.util.common.PrintErrLog;

import java.math.BigDecimal;

import org.apache.ws.axis2.DoRTGSTransaction;
import org.apache.ws.axis2.DoRTGSTransactionResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.easynet.configuration.SOAPConnector;
import com.easynet.impl.JSONObjectImpl;
import com.easynet.util.common;
import com.easynet.util.readXML;

import city.xsd.DoOtherBankRtgsTransactionRequest;
import city.xsd.DoOtherBankRtgsTransactionResponse;
import city.xsd.ObjectFactory;

@Component
public class OtherBankRtgsTransaction {
	@Autowired
	private SOAPConnector soapConnector;
	
	/*
	 * This method is used to do other bank RTGS transaction.
	 */
	public String otherBankRtgsTransaction(String requestData)
	{
		double amount = 0;
		BigDecimal ls_amount=new BigDecimal(amount);
		String ls_benAccNo="";
		String ls_benName="";
		String ls_currency="";
		String ls_password="";
		String ls_payerName="";
		String ls_reason="";
		String ls_routingNo="";
		String ls_senderAccNo="";
		String ls_settlementdate;
		String ls_userName="";
		String ls_responseCode="";
		String ls_responseMesssage="";
		String ls_responseData="";
		String transactionRefNumber="";
		String actualErrMsg="";
		
		try {
		JSONObject requestJsonObj=new JSONObject(requestData);
		ls_amount=requestJsonObj.getBigDecimal("AMOUNT");
		ls_benAccNo=requestJsonObj.getString("BENACCTNO");
		ls_benName=requestJsonObj.getString("BENNAME");
		ls_currency=requestJsonObj.getString("CURRENCY");
		ls_payerName=requestJsonObj.getString("PAYERNAME");
		ls_routingNo=requestJsonObj.getString("ROUTINGNO");
		ls_senderAccNo=requestJsonObj.getString("SENDER_ACCTNO");
		ls_settlementdate=requestJsonObj.getString("SETTLEMENTDATE");
		
		if((ls_amount==null) || (ls_benAccNo==null || "".equals(ls_benAccNo)) || (ls_benName==null || "".equals(ls_benName)) || (ls_currency==null || "".equals(ls_currency))
				|| (ls_payerName==null || "".equals(ls_payerName)) || (ls_routingNo==null || "".equals(ls_routingNo)) || (ls_senderAccNo==null || "".equals(ls_senderAccNo))
				|| (ls_settlementdate==null || "".equals(ls_settlementdate)))
		{
			ls_responseData = common.ofGetErrDataJsonArray("99", "Validation Failed.", "InValid request.", "Null values found in acct_no key.", "","", "R");
			return ls_responseData; 
		}
		ls_userName = readXML.getXmlData("root>CUST_360>AUTHORIZATION>USER_NAME");
		ls_password = readXML.getXmlData("root>CUST_360>AUTHORIZATION>PASSWORD");
		
		//get the object factory object
		city.xsd.ObjectFactory xsdObjectFactory=new city.xsd.ObjectFactory();
		//get DoOtherBankRtgsTransaction request object using object factory
		DoOtherBankRtgsTransactionRequest doOtherBankRtgsTransactionRequest=xsdObjectFactory.createDoOtherBankRtgsTransactionRequest();
		
		doOtherBankRtgsTransactionRequest.setAmount(xsdObjectFactory.createDoOtherBankRtgsTransactionRequestAmount(ls_amount));
		doOtherBankRtgsTransactionRequest.setBenAccNo(xsdObjectFactory.createDoOtherBankRtgsTransactionRequestBenAccNo(ls_benAccNo));
		doOtherBankRtgsTransactionRequest.setBenName(xsdObjectFactory.createDoOtherBankRtgsTransactionRequestBenName(ls_benName));
		doOtherBankRtgsTransactionRequest.setCurrency(xsdObjectFactory.createDoOtherBankRtgsTransactionRequestCurrency(ls_currency));
		doOtherBankRtgsTransactionRequest.setPassword(xsdObjectFactory.createDoOtherBankRtgsTransactionRequestPassword(ls_password));
		doOtherBankRtgsTransactionRequest.setPayerName(xsdObjectFactory.createDoOtherBankRtgsTransactionRequestPayerName(ls_payerName));
		doOtherBankRtgsTransactionRequest.setReason(xsdObjectFactory.createDoOtherBankRtgsTransactionRequestReason(ls_reason));
		doOtherBankRtgsTransactionRequest.setRoutingNo(xsdObjectFactory.createDoOtherBankRtgsTransactionRequestRoutingNo(ls_routingNo));
		doOtherBankRtgsTransactionRequest.setSenderAccNo(xsdObjectFactory.createDoOtherBankRtgsTransactionRequestSenderAccNo(ls_senderAccNo));
		doOtherBankRtgsTransactionRequest.setSettlementdate(Long.parseLong(ls_settlementdate));
		doOtherBankRtgsTransactionRequest.setUsername(xsdObjectFactory.createDoOtherBankRtgsTransactionRequestUsername(ls_userName));
		
		org.apache.ws.axis2.ObjectFactory axis2ObjectFactory=new org.apache.ws.axis2.ObjectFactory();
		DoRTGSTransaction doRTGSTransaction=axis2ObjectFactory.createDoRTGSTransaction();
		doRTGSTransaction.setRequest(axis2ObjectFactory.createDoRTGSTransactionRequest(doOtherBankRtgsTransactionRequest));
		
		
		DoRTGSTransactionResponse RtgsTransactionresponse=null;
		try {
			RtgsTransactionresponse=(DoRTGSTransactionResponse) soapConnector.callWebService(doRTGSTransaction);
			}
		catch(SoapFaultClientException soapException){				
			actualErrMsg=soapException.getFaultStringOrReason();			
			PrintErrLog("DoOtherBankRtgsTransction SoapFaultClientException : " + actualErrMsg);
			ls_responseData = common.ofGetErrDataJsonArray("999", "Alert", "Currently Service under maintenance so please try later (ENP040)", actualErrMsg, "", "0", "R");
			return ls_responseData;							
		}		
		
		/*
		 * Creating response object from do other bank rtgs transaction response.
		 */
	   city.xsd.DoOtherBankRtgsTransactionResponse xsd_doRTGSTransactionresponse=RtgsTransactionresponse.getReturn().getValue();
	   ls_responseCode=xsd_doRTGSTransactionresponse.getResponseCode().getValue();
	   ls_responseMesssage=xsd_doRTGSTransactionresponse.getResponseMessage().getValue();
	   transactionRefNumber=xsd_doRTGSTransactionresponse.getTransactionRefNumber().getValue();
	   
	   /*
	    * If response is 100 then success.
	    * If response is 101 then transaction failed.
	    */
	   if(ls_responseCode!=null && "100".equals(ls_responseCode)) {
		   JSONObjectImpl otherBankRtgsTrasnsactionJsonObj=new JSONObjectImpl();
			JSONObject responseJsonObject=new JSONObject();
			JSONArray otherBankRtgsTrasnsactionJsonArray=new JSONArray();
			
			otherBankRtgsTrasnsactionJsonObj.put("RESPONSECODE", ls_responseCode);
			otherBankRtgsTrasnsactionJsonObj.put("RESPONSEMESSAGE", ls_responseMesssage);
			otherBankRtgsTrasnsactionJsonObj.put("TRANSACTIONREFNUMBER", transactionRefNumber);
			otherBankRtgsTrasnsactionJsonArray.put(otherBankRtgsTrasnsactionJsonObj);
			
			
			

			responseJsonObject.put("STATUS", "0");
			responseJsonObject.put("COLOR", "G");
			responseJsonObject.put("RESPONSE", otherBankRtgsTrasnsactionJsonArray);
			responseJsonObject.put("MESSAGE","");
			ls_responseData=responseJsonObject.toString();
	   }
	   else
		{ //Response other than 100.
			
			ls_responseData = common.ofGetErrDataJsonArray("999", "Alert", "Currently Service under maintenance so please try later (ENP075).", ls_responseMesssage, "",ls_responseCode, "R");
		}
		}
		catch(Exception err) {
			actualErrMsg = common.ofGetTotalErrString(err, "");
			PrintErrLog("doOtherBankTransaction Exception : " + actualErrMsg);
			ls_responseData = common.ofGetErrDataJsonArray("999", "Alert", "Currently Service under maintenance so please try later (ENP072)", actualErrMsg, "", "0", "R");		
		}
		return ls_responseData;
	
		
		
	}
	

}
