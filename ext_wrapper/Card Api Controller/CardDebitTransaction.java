package com.easynet.controller.ApiController;

import static com.easynet.util.common.PrintErrLog;

import org.apache.ws.axis2.DoCardDebitTransaction;
import org.apache.ws.axis2.DoCardDebitTransactionResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.easynet.configuration.SOAPConnector;
import com.easynet.impl.JSONObjectImpl;
import com.easynet.util.common;
import com.easynet.util.readXML;

import card.xsd.CardDebitTransactionRequest;

@Component
public class CardDebitTransaction {
	
	@Autowired
	private SOAPConnector soapConnector;
	
	/*
	 * this method is used to do card debit transaction.
	 */
		public String cardDebitTransaction(String requestData) {
			String ls_cardNoActual="";
			String ls_currencyCode="";
			String ls_expiryDate="";
			String ls_merchantId="";
			String ls_password="";
			String remarks="";
			String ls_terminalId="";
			String ls_transactionAmount="";
			String ls_userName="";
			String approvalCode="";
			int cashierRequestId;
			String ls_responseCode="";
			String ls_responseMessage="";
			String transactionDateTime="";
			String ls_responseData="";
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
				
				if((ls_cardNoActual==null || "".equals(ls_cardNoActual)) || (ls_currencyCode==null || "".equals(ls_currencyCode)) || (ls_expiryDate==null || "".equals(ls_expiryDate))
						|| (ls_merchantId==null || "".equals(ls_merchantId)) || (remarks==null || "".equals(remarks)) || (ls_terminalId==null || "".equals(ls_terminalId))
						|| (ls_transactionAmount==null || "".equals(ls_transactionAmount))) {
					
					ls_responseData = common.ofGetErrDataJsonArray("99", "Validation Failed.", "InValid request.", "Null values found in card_no key.", "","", "R");
					return ls_responseData; 
				}
				ls_userName = readXML.getXmlData("root>CUST_360>AUTHORIZATION>USER_NAME");
				ls_password = readXML.getXmlData("root>CUST_360>AUTHORIZATION>PASSWORD");
				
				//created object factory object.
				card.xsd.ObjectFactory xsdObjectFactory=new card.xsd.ObjectFactory();
				//created card debit transaction request object from object factory.
				CardDebitTransactionRequest doCardDebitTransactionrequest=xsdObjectFactory.createCardDebitTransactionRequest();
				
				//setting all the required fields.
				doCardDebitTransactionrequest.setCardNoActual(xsdObjectFactory.createCardDebitTransactionRequestCardNoActual(ls_cardNoActual));
				doCardDebitTransactionrequest.setCurrencyCode(xsdObjectFactory.createCardDebitTransactionRequestCurrencyCode(ls_currencyCode));
				doCardDebitTransactionrequest.setExpiryDate(xsdObjectFactory.createCardDebitTransactionRequestExpiryDate(ls_expiryDate));
				doCardDebitTransactionrequest.setMerchantId(xsdObjectFactory.createCardDebitTransactionRequestMerchantId(ls_merchantId));
				doCardDebitTransactionrequest.setPassword(xsdObjectFactory.createCardDebitTransactionRequestPassword(ls_password));
				doCardDebitTransactionrequest.setRemarks(xsdObjectFactory.createCardDebitTransactionRequestRemarks(remarks));
				doCardDebitTransactionrequest.setTerminalId(xsdObjectFactory.createCardDebitTransactionRequestTerminalId(ls_terminalId));
				doCardDebitTransactionrequest.setTransactionAmount(xsdObjectFactory.createCardDebitTransactionRequestTransactionAmount(Double.parseDouble(ls_transactionAmount)));
				doCardDebitTransactionrequest.setUsername(xsdObjectFactory.createCardDebitTransactionRequestUsername(ls_userName));
				
				
				org.apache.ws.axis2.ObjectFactory axis2ObjectFactory=new org.apache.ws.axis2.ObjectFactory();
				//card debit transaction request stored to card debit transaction request object.
				DoCardDebitTransaction docarddebittransaction=axis2ObjectFactory.createDoCardDebitTransaction();
				docarddebittransaction.setRequest(axis2ObjectFactory.createDoCardDebitTransactionRequest(doCardDebitTransactionrequest));
				
				/*
				 * created response object of card debit transaction response.
				 */
				DoCardDebitTransactionResponse doCardDebitTransactionResponse=null;
				try {
					doCardDebitTransactionResponse=(DoCardDebitTransactionResponse) soapConnector.callWebService(docarddebittransaction);
				}catch(SoapFaultClientException soapException){				
					actualErrMsg=soapException.getFaultStringOrReason();			
					PrintErrLog("DoCardDebitTransaction SoapFaultClientException : " + actualErrMsg);
					ls_responseData = common.ofGetErrDataJsonArray("999", "Alert", "Currently Service under maintenance so please try later (ENP040)", actualErrMsg, "", "0", "R");
					return ls_responseData;							
				}
				
				/*get card debit transaction response object*/
				card.xsd.CardDebitTransactionResponse xsd_cardDebitTransactionresponse=doCardDebitTransactionResponse.getReturn().getValue();
				
				//get the response
				approvalCode=xsd_cardDebitTransactionresponse.getApprovalCode().getValue();
				cashierRequestId=xsd_cardDebitTransactionresponse.getCashierRequestId();
				ls_responseCode=xsd_cardDebitTransactionresponse.getResponseCode().getValue();
				ls_responseMessage=xsd_cardDebitTransactionresponse.getResponseMessage().getValue();
				transactionDateTime=xsd_cardDebitTransactionresponse.getTransactionDateTime().getValue();
				
				/*
				 * if response code is 100 then success.
				 * if response code is 101 then transaction failed.
				 */
				
				if(ls_responseCode!=null && "100".equals(ls_responseCode)) {
					JSONObjectImpl cardDebitPaymentJsonObj=new JSONObjectImpl();
					JSONObject responseJsonObject=new JSONObject();
					JSONArray cardDebitPaymentJsonArray=new JSONArray();
					
					cardDebitPaymentJsonObj.put("APPROVALCODE", approvalCode);
					cardDebitPaymentJsonObj.put("CASHIERREQUEST_ID", cashierRequestId);
					cardDebitPaymentJsonObj.put("RESPONSECODE", ls_responseCode);
					cardDebitPaymentJsonObj.put("RESPONSEMESSAGE", ls_responseMessage);
					cardDebitPaymentJsonObj.put("TRANSACTIONDATETIME", transactionDateTime);
					cardDebitPaymentJsonArray.put(cardDebitPaymentJsonObj);
					
					/*
					 * If transaction happens and API is called successfully.
					 */
					responseJsonObject.put("STATUS", "0");
					responseJsonObject.put("COLOR", "G");
					responseJsonObject.put("RESPONSE", cardDebitPaymentJsonArray);
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
				PrintErrLog("DoCardDebitTransaction Exception : " + actualErrMsg);
				ls_responseData = common.ofGetErrDataJsonArray("999", "Alert", "Currently Service under maintenance so please try later (ENP072)", actualErrMsg, "", "0", "R");		
			}
			return ls_responseData;
			}
				
		}

		
