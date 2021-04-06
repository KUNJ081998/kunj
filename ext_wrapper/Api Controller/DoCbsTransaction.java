package com.easynet.controller.ApiController;
import static com.easynet.util.common.PrintErrLog;

import org.apache.ws.axis2.DoCBSTransaction;
import org.apache.ws.axis2.DoCBSTransactionResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.easynet.configuration.SOAPConnector;
import com.easynet.impl.JSONObjectImpl;
import com.easynet.util.common;
import com.easynet.util.readXML;


import city.xsd.CBSTransRequest;
import city.xsd.TransactionResponse;


@Component
public class DoCbsTransaction {
	@Autowired
	private SOAPConnector soapConnector;
	
	/**
	 * This method is used to do cbs transaction using SOAP api of finacle customer.
	 * @return success message
	 */
	public String doCbsTransaction(String requestData) {
		String ls_clientCode="";
		String ls_creditAccount="";
		String ls_currencyCode="";
		String ls_debitAccount="";
		String ls_password="";
		String remarks="";
		Double transactionAmount=0.0;
		String transactionRefNumber="";
		String ls_userName="";
		String ls_responseCode="";
		String ls_responseMessage="";
		String ls_responseData="";
		String actualErrMsg="";
		String transactionDateTime="";
		
		try {
			JSONObject requestJsonObj=new JSONObject(requestData);
			ls_clientCode=requestJsonObj.getString("CLIENTCODE");
			ls_creditAccount=requestJsonObj.getString("CREDITACCT");
			ls_currencyCode=requestJsonObj.getString("CURRENCYCODE");
			ls_debitAccount=requestJsonObj.getString("DEBITACCT");
			remarks=requestJsonObj.getString("REMARKS");
			transactionAmount=Double.parseDouble(requestJsonObj.getString("TRANSAMOUNT"));
			transactionRefNumber=requestJsonObj.getString("TRANSACTION_REFNUMBER");
			
			if((ls_clientCode==null ||"".equals(ls_clientCode)) || (ls_creditAccount==null ||"".equals(ls_creditAccount)) || (ls_currencyCode==null ||"".equals(ls_currencyCode))
					|| (ls_debitAccount==null ||"".equals(ls_debitAccount)) || (remarks==null ||"".equals(remarks)) || (transactionAmount==null ||"".equals(transactionAmount))
					||(transactionRefNumber==null ||"".equals(transactionRefNumber)))
			{
				ls_responseData=common.ofGetErrDataJsonArray("99", "Validation Failed.", "InValid request.", "Null values found in acct_no key.", "","", "R");
				return ls_responseData;
			}
			ls_userName = readXML.getXmlData("root>CUST_360>AUTHORIZATION>USER_NAME");
			ls_password = readXML.getXmlData("root>CUST_360>AUTHORIZATION>PASSWORD");
			
			
			
			city.xsd.ObjectFactory xsdObjectFactory=new city.xsd.ObjectFactory();
			CBSTransRequest cbsTransRequest=xsdObjectFactory.createCBSTransRequest();
			
			
			cbsTransRequest.setClientCode(xsdObjectFactory.createCBSTransRequestClientCode(ls_clientCode));
			cbsTransRequest.setCreditAccount(xsdObjectFactory.createCBSTransRequestCreditAccount(ls_creditAccount));
			cbsTransRequest.setCurrencyCode(xsdObjectFactory.createCBSTransRequestCurrencyCode(ls_currencyCode));
			cbsTransRequest.setDebitAccount(xsdObjectFactory.createCBSTransRequestDebitAccount(ls_debitAccount));
			cbsTransRequest.setPassword(xsdObjectFactory.createCBSTransRequestPassword(ls_password));
			cbsTransRequest.setRemarks(xsdObjectFactory.createCBSTransRequestRemarks(remarks));
			cbsTransRequest.setTransactionAmount(xsdObjectFactory.createCBSTransRequestTransactionAmount(transactionAmount));
			cbsTransRequest.setTransactionRefNumber(xsdObjectFactory.createCBSTransRequestTransactionRefNumber(transactionRefNumber));
			cbsTransRequest.setUsername(xsdObjectFactory.createCBSTransRequestUsername(ls_userName));
			
			
			org.apache.ws.axis2.ObjectFactory axis2ObjectFactory=new org.apache.ws.axis2.ObjectFactory();
			DoCBSTransaction docbstrans=axis2ObjectFactory.createDoCBSTransaction();
			docbstrans.setRequest(axis2ObjectFactory.createDoCBSTransactionRequest(cbsTransRequest));
			
			DoCBSTransactionResponse CbstransactionResponse=null;
			try {
				CbstransactionResponse=(DoCBSTransactionResponse) soapConnector.callWebService(docbstrans);
			}
			catch(SoapFaultClientException soapException){				
				actualErrMsg=soapException.getFaultStringOrReason();			
				PrintErrLog("DoCbsTransaction SoapFaultClientException : " + actualErrMsg);
				ls_responseData = common.ofGetErrDataJsonArray("999", "Alert", "Currently Service under maintenance so please try later (ENP040)", actualErrMsg, "", "0", "R");
			return ls_responseData;							
			}	
			
          /*
           * Creating response object from do cbs transaction response.
           */
			city.xsd.TransactionResponse xsd_doCbsTransactionresponse=CbstransactionResponse.getReturn().getValue();
			ls_responseCode=xsd_doCbsTransactionresponse.getResponseCode().getValue();
			ls_responseMessage=xsd_doCbsTransactionresponse.getResponseMessage().getValue();
			transactionRefNumber=xsd_doCbsTransactionresponse.getTransactionRefNumber().getValue();
			transactionDateTime=xsd_doCbsTransactionresponse.getTransactionDateTime().getValue();
			
		/*
		 * If response is 100 then success.
		 * If response is 101 then transaction failed.
		 */
			if(ls_responseCode!=null && "100".equals(ls_responseCode)) {
				 	JSONObjectImpl doCbsTransactionJsonObj=new JSONObjectImpl();
					JSONObject responseJsonObject=new JSONObject();
					JSONArray doCbsTransactionJsonArray=new JSONArray();
					
					doCbsTransactionJsonObj.put("RESPONSECODE", ls_responseCode);
					doCbsTransactionJsonObj.put("RESPONSEMESSAGE", ls_responseMessage);
					doCbsTransactionJsonObj.put("TRANSACTIONREFNUMBER", transactionRefNumber);
					doCbsTransactionJsonObj.put("TRANSACTIONDATETIME", transactionDateTime);
					doCbsTransactionJsonArray.put(doCbsTransactionJsonObj);
					
					
					responseJsonObject.put("STATUS", "0");
					responseJsonObject.put("COLOR", "G");
					responseJsonObject.put("RESPONSE", doCbsTransactionJsonArray);
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
					PrintErrLog("DoCbsTransaction Exception : " + actualErrMsg);
					ls_responseData = common.ofGetErrDataJsonArray("999", "Alert", "Currently Service under maintenance so please try later (ENP072)", actualErrMsg, "", "0", "R");		
				}
				return ls_responseData;
			
			
		}
		
		
	}

