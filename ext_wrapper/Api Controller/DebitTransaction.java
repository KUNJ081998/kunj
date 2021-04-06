package com.easynet.controller.ApiController;

import static com.easynet.util.common.PrintErrLog;

import org.apache.ws.axis2.DoDebitTransaction;
import org.apache.ws.axis2.DoDebitTransactionResponse;
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
public class DebitTransaction {
	@Autowired
	private SOAPConnector soapConnector;
	
	public String doDebitTransaction(String requestData) {
		String ls_creditAccount="";
		String ls_currencyCode="";
		String ls_debitAccount="";
		String ls_password="";
		String remarks="";
		String ls_transactionAmount="";
		String ls_userName="";
		String ls_responseCode="";
		String ls_responseData="";
		String ls_responseMessage="";
		String transactionDateTime="";
		String transactionRefNumber="";
		String actualErrMsg="";
		
		try {
			JSONObject requestJsonObj=new JSONObject(requestData);
			ls_creditAccount=requestJsonObj.getString("CREDITACCOUNT");
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
			ls_userName = readXML.getXmlData("root>CUST_360>AUTHORIZATION>USER_NAME");
			ls_password = readXML.getXmlData("root>CUST_360>AUTHORIZATION>PASSWORD");
			
			//created object factoryb object
			city.xsd.ObjectFactory xsdObjectFactory=new city.xsd.ObjectFactory();
			FinacleTransactionRequest debitTransaction=xsdObjectFactory.createFinacleTransactionRequest();
			
			debitTransaction.setCreditAccount(xsdObjectFactory.createFinacleTransactionRequestCreditAccount(ls_creditAccount));
			debitTransaction.setCurrencyCode(xsdObjectFactory.createFinacleTransactionRequestCurrencyCode(ls_currencyCode));
			debitTransaction.setDebitAccount(xsdObjectFactory.createFinacleTransactionRequestDebitAccount(ls_debitAccount));
			debitTransaction.setPassword(xsdObjectFactory.createFinacleTransactionRequestPassword(ls_password));
			debitTransaction.setRemarks(xsdObjectFactory.createFinacleTransactionRequestRemarks(remarks));
			debitTransaction.setTransactionAmount(xsdObjectFactory.createFinacleTransactionRequestTransactionAmount(Double.valueOf(ls_transactionAmount)));
			debitTransaction.setUsername(xsdObjectFactory.createFinacleTransactionRequestUsername(ls_userName));
			
			
			org.apache.ws.axis2.ObjectFactory axis2ObjectFactory=new org.apache.ws.axis2.ObjectFactory();
			DoDebitTransaction doDebitTransaction=axis2ObjectFactory.createDoDebitTransaction();
			doDebitTransaction.setRequest(axis2ObjectFactory.createDoDebitTransactionRequest(debitTransaction));
			
			DoDebitTransactionResponse doDebitTrasnactionresponse=null;
			try {
				doDebitTrasnactionresponse=(DoDebitTransactionResponse) soapConnector.callWebService(doDebitTransaction);
			}
			catch(SoapFaultClientException soapException){				
				actualErrMsg=soapException.getFaultStringOrReason();			
				PrintErrLog("DoDebitTransaction SoapFaultClientException : " + actualErrMsg);
				ls_responseData = common.ofGetErrDataJsonArray("999", "Alert", "Currently Service under maintenance so please try later (ENP040)", actualErrMsg, "", "0", "R");
			return ls_responseData;							
			}	
			
			/*
			 * Creating response object from doDebitTransaction response.
			 */
			city.xsd.TransactionResponse xsd_doDebitTrasnactionresponse=doDebitTrasnactionresponse.getReturn().getValue();
			ls_responseCode=xsd_doDebitTrasnactionresponse.getResponseCode().getValue();
			ls_responseMessage=xsd_doDebitTrasnactionresponse.getResponseMessage().getValue();
			transactionDateTime=xsd_doDebitTrasnactionresponse.getTransactionDateTime().getValue();
			transactionRefNumber=xsd_doDebitTrasnactionresponse.getTransactionRefNumber().getValue();
			
			/*
			 * If response is 100 then success.
			 * If response is 101 then transaction failed.
			 */
			if(ls_responseCode!=null && "100".equals(ls_responseCode)) {
				 	JSONObjectImpl doDebitTransactionJsonObj=new JSONObjectImpl();
					JSONObject responseJsonObject=new JSONObject();
					JSONArray doDebitTransactionJsonArray=new JSONArray();
					
					doDebitTransactionJsonObj.put("RESPONSECODE", ls_responseCode);
					doDebitTransactionJsonObj.put("RESPONSEMESSAGE", ls_responseMessage);
					doDebitTransactionJsonObj.put("TRANSACTIONDATETIME", transactionDateTime);
					doDebitTransactionJsonObj.put("TRANSACTIONREFNUMBER", transactionRefNumber);
					doDebitTransactionJsonArray.put(doDebitTransactionJsonObj);
					
					responseJsonObject.put("STATUS", "0");
					responseJsonObject.put("COLOR", "G");
					responseJsonObject.put("RESPONSE", doDebitTransactionJsonArray);
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
					PrintErrLog("DoDebitTransaction Exception : " + actualErrMsg);
					ls_responseData = common.ofGetErrDataJsonArray("999", "Alert", "Currently Service under maintenance so please try later (ENP072)", actualErrMsg, "", "0", "R");		
				}
				return ls_responseData;
			

}
}
