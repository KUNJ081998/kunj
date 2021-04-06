package com.easynet.controller.ApiController;

import static com.easynet.util.common.PrintErrLog;

import java.math.BigDecimal;

import org.apache.ws.axis2.DoOtherBankTransaction;
import org.apache.ws.axis2.DoOtherBankTransactionResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.easynet.configuration.SOAPConnector;
import com.easynet.impl.JSONObjectImpl;
import com.easynet.util.common;
import com.easynet.util.readXML;


import city.xsd.DoOtherBankTransactionRequest;

@Component
public class OtherBankTransaction {
	@Autowired
private SOAPConnector soapConnector;
	/*
	 * This method is used to do CBS transaction.
	 */
	
	public String doOtherBankTransaction(String requestData)
	{
		int ls_accType;
		double amount = 0;
		BigDecimal ls_amount=new BigDecimal(amount);
		String ls_cbsCustomerID="";
		String ls_dfiAccNo="";
		String ls_idNumber="";
		String ls_password="";
		String ls_paymentInfo="";
		String ls_reason="";
		String ls_receiverName="";
		String ls_routingNo="";
		String ls_senderAccNo="";
		String ls_userName="";
		String ls_responseCode="";
		String ls_responseMessage="";
	    int ls_transactionRefNumber;
	    String actualErrMsg="";
	    String ls_responseData="";
		try {
			JSONObject requestJsonObj=new JSONObject(requestData);
			ls_accType=requestJsonObj.getInt("ACCTTYPE");
			ls_amount=requestJsonObj.getBigDecimal("AMOUNT");
			ls_cbsCustomerID=requestJsonObj.getString("CBS_CUSTOMERID");
			ls_dfiAccNo=requestJsonObj.getString("DFI_ACCTNO");
			ls_idNumber=requestJsonObj.getString("IDNUMBER");
			ls_paymentInfo=requestJsonObj.getString("PAYMENTINFO");
			ls_reason=requestJsonObj.getString("REASON");
			ls_receiverName=requestJsonObj.getString("RECEIVERNAME");
			ls_routingNo=requestJsonObj.getString("ROUTINGNO");
			ls_senderAccNo=requestJsonObj.getString("SENDER_ACCTNO");
			
			if((String.valueOf(ls_accType)==null) || (ls_amount==null) || (ls_cbsCustomerID==null || "".equals(ls_cbsCustomerID)) || (ls_dfiAccNo==null || "".equals(ls_dfiAccNo)) || (ls_idNumber==null || "".equals(ls_idNumber))
					|| (ls_paymentInfo==null || "".equals(ls_paymentInfo)) || (ls_reason==null || "".equals(ls_reason)) || (ls_receiverName==null || "".equals(ls_receiverName))
					|| (ls_routingNo==null || "".equals(ls_routingNo)) || (ls_senderAccNo==null || "".equals(ls_senderAccNo)))
			{
				ls_responseMessage = common.ofGetErrDataJsonArray("99", "Validation Failed.", "InValid request.", "Null values found in acct_no key.", "","", "R");
				return ls_responseMessage; 
			}
			ls_userName=readXML.getXmlData("root>CUST_360>AUTHORIZATION>USER_NAME");
			ls_password=readXML.getXmlData("root>CUST_360>AUTHORIZATION>PASSWORD");
			
			//created object factory object
			city.xsd.ObjectFactory xsdObjectFactory=new city.xsd.ObjectFactory();
			//created object of DoOtherBankTransaction using object factory
			DoOtherBankTransactionRequest doOtherBankTransactionRequest=xsdObjectFactory.createDoOtherBankTransactionRequest();
			
			doOtherBankTransactionRequest.setAccType(xsdObjectFactory.createDoOtherBankTransactionRequestAccType(ls_accType));
			doOtherBankTransactionRequest.setAmount(xsdObjectFactory.createDoOtherBankTransactionRequestAmount(ls_amount));
			doOtherBankTransactionRequest.setCbsCustomerID(xsdObjectFactory.createDoOtherBankTransactionRequestCbsCustomerID(ls_cbsCustomerID));
			doOtherBankTransactionRequest.setDfiAccNo(xsdObjectFactory.createDoOtherBankTransactionRequestDfiAccNo(ls_dfiAccNo));
			doOtherBankTransactionRequest.setIdNumber(xsdObjectFactory.createDoOtherBankTransactionRequestIdNumber(ls_idNumber));
			doOtherBankTransactionRequest.setPassword(xsdObjectFactory.createDoOtherBankTransactionRequestPassword(ls_password));
			doOtherBankTransactionRequest.setPaymentInfo(xsdObjectFactory.createDoOtherBankTransactionRequestPaymentInfo(ls_paymentInfo));
			doOtherBankTransactionRequest.setReason(xsdObjectFactory.createDoOtherBankTransactionRequestReason(ls_reason));
			doOtherBankTransactionRequest.setReceiverName(xsdObjectFactory.createDoOtherBankTransactionRequestReason(ls_reason));
			doOtherBankTransactionRequest.setReceiverName(xsdObjectFactory.createDoOtherBankTransactionRequestReceiverName(ls_receiverName));
			doOtherBankTransactionRequest.setRoutingNo(xsdObjectFactory.createDoOtherBankTransactionRequestRoutingNo(ls_routingNo));
			doOtherBankTransactionRequest.setSenderAccNo(xsdObjectFactory.createDoOtherBankTransactionRequestSenderAccNo(ls_senderAccNo));
			doOtherBankTransactionRequest.setUsername(xsdObjectFactory.createDoOtherBankTransactionRequestUsername(ls_userName));
			
			org.apache.ws.axis2.ObjectFactory axis2ObjectFactory=new org.apache.ws.axis2.ObjectFactory();
			DoOtherBankTransaction doOtherBankTransaction=axis2ObjectFactory.createDoOtherBankTransaction();
			doOtherBankTransaction.setRequest(axis2ObjectFactory.createDoOtherBankTransactionRequest(doOtherBankTransactionRequest));
			
			DoOtherBankTransactionResponse doOtherBankTransactionResponse = null;
			try {
				doOtherBankTransactionResponse=(DoOtherBankTransactionResponse) soapConnector.callWebService(doOtherBankTransaction);
			}
			catch(SoapFaultClientException soapException) {
				actualErrMsg=soapException.getFaultStringOrReason();			
				PrintErrLog("DoOtherBankTransaction SoapFaultClientException : " + actualErrMsg);
				ls_responseData = common.ofGetErrDataJsonArray("999", "Alert", "Currently Service under maintenance so please try later (ENP040)", actualErrMsg, "", "0", "R");
				return ls_responseData;							
			}
			
			city.xsd.DoOtherBankTransactionResponse xsd_doOtherBankTransactionResponse=doOtherBankTransactionResponse.getReturn().getValue();
			
			ls_responseCode=xsd_doOtherBankTransactionResponse.getResponseCode().getValue();
			ls_responseMessage=xsd_doOtherBankTransactionResponse.getResponseMessage().getValue();
			ls_transactionRefNumber=xsd_doOtherBankTransactionResponse.getTransactionRefNumber();
			
			/*
			 * If response is 100 then success.
			 * If response is 101 the transaction failed.
			 */
			if(ls_responseCode!=null && "100".equals(ls_responseCode))
			{
				JSONObjectImpl otherBankTransactionJsonObj=new JSONObjectImpl();
				JSONObject responseJsonObject=new JSONObject();
				JSONArray otherBankTransactionJsonArray=new JSONArray();

				otherBankTransactionJsonObj.put("RESPONSECODE",ls_responseCode);
				otherBankTransactionJsonObj.put("RESPONSEMESSAGE", ls_responseMessage);
				otherBankTransactionJsonObj.put("TRANSACTIONREFNUMBER", ls_transactionRefNumber);
				otherBankTransactionJsonArray.put(otherBankTransactionJsonObj);
				
				/*
				 * If transaction happens and API is called successfully.
				 */
				responseJsonObject.put("STATUS", "0");
				responseJsonObject.put("COLOR", "G");
				responseJsonObject.put("RESPONSE", otherBankTransactionJsonArray);
				responseJsonObject.put("MESSAGE","");
				ls_responseData=responseJsonObject.toString();
				
			}
			else
			{ //Response other than 100.
				ls_responseData = common.ofGetErrDataJsonArray("999", "Alert", "Currently Service under maintenance so please try later (ENP075).", ls_responseMessage, "",ls_responseCode, "R");
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
