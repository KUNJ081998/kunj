package com.easynet.controller.ApiController;

import static com.easynet.util.common.PrintErrLog;

import org.apache.ws.axis2.ReverseCardPurchaseTransaction;
import org.apache.ws.axis2.ReverseCardPurchaseTransactionResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.easynet.configuration.SOAPConnector;
import com.easynet.impl.JSONObjectImpl;
import com.easynet.util.common;
import com.easynet.util.readXML;

import city.xsd.ReverseCardPurchaseTransactionRequest;

@Component
public class ReverseCardPurchaseTrans {

		@Autowired
		private SOAPConnector soapConnector;
		
		/*
		 * this method is used to do reverse card purchase transaction.
		 */
		public String doReverseCardPurchaseTransaction(String requestData) {
			
			String ls_cardNoActual="";
			String ls_currencyCode="";
			String ls_expiryDate="";
			String ls_merchantId="";
			String ls_password="";
			String remarks="";
			String ls_terminalId="";
			String ls_transactionAmount="";
			String ls_transactionRefNumber="";
			String ls_userName="";
			String ls_responseCode="";
			String ls_responseMessage="";
			String ls_responseData="";
			String transactionDateTime="";
			String transactionRefNumber="";
			String actualErrMsg="";
			
			try {
				JSONObject requestJsonObj=new JSONObject(requestData);
				ls_cardNoActual=requestJsonObj.getString("CARDNOACTUAL");
				ls_currencyCode=requestJsonObj.getString("CURRENCYCODE");
				ls_expiryDate=requestJsonObj.getString("EXPIRYDATE");
				ls_merchantId=requestJsonObj.getString("MERCHANTID");
				remarks=requestJsonObj.getString("REMARKS");
				ls_terminalId=requestJsonObj.getString("TERMINALID");
				ls_transactionAmount=requestJsonObj.getString("TRANSAMOUNT");
				ls_transactionRefNumber=requestJsonObj.getString("TRANSACTIONREFNUMBER");
				
				if((ls_cardNoActual==null || "".equals(ls_cardNoActual)) || (ls_currencyCode==null || "".equals(ls_currencyCode)) || (ls_expiryDate==null || "".equals(ls_expiryDate))
						|| (ls_merchantId==null || "".equals(ls_merchantId)) || (remarks==null || "".equals(remarks)) || (ls_terminalId==null || "".equals(ls_terminalId))
						|| (ls_transactionAmount==null || "".equals(ls_transactionAmount)) || (ls_transactionRefNumber==null || "".equals(ls_transactionRefNumber)))
				{
					ls_responseData = common.ofGetErrDataJsonArray("99", "Validation Failed.", "InValid request.", "Null values found in card_no key.", "","", "R");
					return ls_responseData; 
				}
				ls_userName = readXML.getXmlData("root>CUST_360>AUTHORIZATION>USER_NAME");
				ls_password = readXML.getXmlData("root>CUST_360>AUTHORIZATION>PASSWORD");
				
				//created object factory object.
				city.xsd.ObjectFactory xsdObjectFactory=new city.xsd.ObjectFactory();
				//created reverse card transaction request object from object factory.
				ReverseCardPurchaseTransactionRequest reverseCardPurchaseTransactionrequest=xsdObjectFactory.createReverseCardPurchaseTransactionRequest();
				
				//setting all the required fields.
				reverseCardPurchaseTransactionrequest.setCardNoActual(xsdObjectFactory.createReverseCardPurchaseTransactionRequestCardNoActual(ls_cardNoActual));
				reverseCardPurchaseTransactionrequest.setCurrencyCode(xsdObjectFactory.createReverseCardPurchaseTransactionRequestCurrencyCode(ls_currencyCode));
				reverseCardPurchaseTransactionrequest.setExpiryDate(xsdObjectFactory.createReverseCardPurchaseTransactionRequestExpiryDate(ls_expiryDate));
				reverseCardPurchaseTransactionrequest.setMerchantId(xsdObjectFactory.createReverseCardPurchaseTransactionRequestMerchantId(ls_merchantId));
				reverseCardPurchaseTransactionrequest.setPassword(xsdObjectFactory.createReverseCardPurchaseTransactionRequestPassword(ls_password));
				reverseCardPurchaseTransactionrequest.setRemarks(xsdObjectFactory.createReverseCardPurchaseTransactionRequestRemarks(remarks));
				reverseCardPurchaseTransactionrequest.setTerminalId(xsdObjectFactory.createReverseCardPurchaseTransactionRequestTerminalId(ls_terminalId));
				reverseCardPurchaseTransactionrequest.setTransactionAmount(xsdObjectFactory.createReverseCardPurchaseTransactionRequestTransactionAmount(Double.parseDouble(ls_transactionAmount)));
				reverseCardPurchaseTransactionrequest.setTransactionRefNumber(xsdObjectFactory.createReverseCardPurchaseTransactionRequestTransactionRefNumber(ls_transactionRefNumber));
				reverseCardPurchaseTransactionrequest.setUsername(xsdObjectFactory.createReverseCardPurchaseTransactionRequestUsername(ls_userName));
				
				
				org.apache.ws.axis2.ObjectFactory axis2ObjectFactory=new org.apache.ws.axis2.ObjectFactory();
				//reverse card transaction request object stored in reverse card transaction.
				ReverseCardPurchaseTransaction reverseCardPurchaseTransaction=axis2ObjectFactory.createReverseCardPurchaseTransaction();
				reverseCardPurchaseTransaction.setRequest(axis2ObjectFactory.createReverseCardPurchaseTransactionRequest(reverseCardPurchaseTransactionrequest));
				
				/*
				 * created response object of reverse card purchase transaction.
				 */
				ReverseCardPurchaseTransactionResponse reverseCardPurchaseTransactionresponse=null;
				try {
					reverseCardPurchaseTransactionresponse=(ReverseCardPurchaseTransactionResponse) soapConnector.callWebService(reverseCardPurchaseTransaction);
				}
				catch(SoapFaultClientException soapException){				
					actualErrMsg=soapException.getFaultStringOrReason();			
					PrintErrLog("ReverseCardPurchaseTransaction SoapFaultClientException : " + actualErrMsg);
					ls_responseData = common.ofGetErrDataJsonArray("999", "Alert", "Currently Service under maintenance so please try later (ENP040)", actualErrMsg, "", "0", "R");
					return ls_responseData;							
				}
				
				/*get card transaction response object*/
				city.xsd.CardTransactionResponse xsd_cardtransactionresponse=reverseCardPurchaseTransactionresponse.getReturn().getValue();
				
				//getting all the required values from the reponse.
				ls_responseCode=xsd_cardtransactionresponse.getResponseCode().getValue();
				ls_responseMessage=xsd_cardtransactionresponse.getResponseMessage().getValue();
				transactionDateTime=xsd_cardtransactionresponse.getTransactionDateTime().getValue();
				transactionRefNumber=xsd_cardtransactionresponse.getTransactionRefNumber().getValue();
				
				/*
				 * if response code is 100 then success.
				 * if response code is 101 then transaction failed.
				 */
				
				if(ls_responseCode!=null && "100".equals(ls_responseCode)) {
					JSONObjectImpl CardTransactionresponseJsonObj=new JSONObjectImpl();
					JSONObject responseJsonObject=new JSONObject();
					JSONArray CardTransactionresponseJsonArray=new JSONArray();
					
					CardTransactionresponseJsonObj.put("RESPONSECODE", ls_responseCode);
					CardTransactionresponseJsonObj.put("RESPONSEMESSAGE", ls_responseMessage);
					CardTransactionresponseJsonObj.put("TRANSACTIONDATETIME", transactionDateTime);
					CardTransactionresponseJsonObj.put("TRANSACTIONREFNUMBER", transactionRefNumber);
					CardTransactionresponseJsonArray.put(CardTransactionresponseJsonObj);
					
					
					/*
					 * If transaction happens and API is called successfully.
					 */
					responseJsonObject.put("STATUS", "0");
					responseJsonObject.put("COLOR", "G");
					responseJsonObject.put("RESPONSE", CardTransactionresponseJsonArray);
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
				PrintErrLog("ReverseCardPurchaseTransaction Exception : " + actualErrMsg);
				ls_responseData = common.ofGetErrDataJsonArray("999", "Alert", "Currently Service under maintenance so please try later (ENP072)", actualErrMsg, "", "0", "R");		
			}
			return ls_responseData;
			}

			
		}

