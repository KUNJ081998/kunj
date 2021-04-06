package com.easynet.controller.ApiController;

import static com.easynet.util.common.PrintErrLog;

import org.apache.ws.axis2.CreditCardPaymentResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.easynet.configuration.SOAPConnector;
import com.easynet.impl.JSONObjectImpl;
import com.easynet.util.common;
import com.easynet.util.readXML;

import card.xsd.CreditCardPaymentRequest;

@Component
public class CreditCardPayment {

		@Autowired
		private SOAPConnector soapConnector;
		
		/*
		 * This method is used to do credit card payment.
		 */
		public String DoCreditCardPayment(String requestData) {
			String ls_amount;
			String ls_cardNoActual="";
			String ls_currency="";
			String ls_password="";
			String remarks="";
			String ls_userName="";
			String approvalCode="";
			String cashierRequestId="";
			String ls_responseCode="";
			String ls_responseMessage="";
			String ls_responseData="";
			String actualErrMsg="";
			
			try
			{
				JSONObject requestJsonObj=new JSONObject(requestData);
				ls_amount=requestJsonObj.getString("AMOUNT");
				ls_cardNoActual=requestJsonObj.getString("CARDNOACTUAL");
				ls_currency=requestJsonObj.getString("CURRENCY");
				remarks=requestJsonObj.getString("REMARKS");
				
				if((ls_amount==null || "".equals(ls_amount)) || (ls_cardNoActual==null || "".equals(ls_cardNoActual)) || (ls_currency==null || "".equals(ls_currency))
						|| (remarks==null || "".equals(remarks))){
					
					ls_responseData = common.ofGetErrDataJsonArray("99", "Validation Failed.", "InValid request.", "Null values found in acct_no key.", "","", "R");
					return ls_responseData; 
				}
				ls_userName = readXML.getXmlData("root>CUST_360>AUTHORIZATION>USER_NAME");
				ls_password = readXML.getXmlData("root>CUST_360>AUTHORIZATION>PASSWORD");
				
				
				//getting the object factory object.
				card.xsd.ObjectFactory xsdObjectFactory=new card.xsd.ObjectFactory();
				//credit card payment objectb from object factory.
				CreditCardPaymentRequest doCreditCardPaymentRequest=xsdObjectFactory.createCreditCardPaymentRequest();
				
				//setting all the required fields.
				doCreditCardPaymentRequest.setAmount(xsdObjectFactory.createCreditCardPaymentRequestAmount(Double.parseDouble(ls_amount)));
				doCreditCardPaymentRequest.setCardNoActual(xsdObjectFactory.createCreditCardPaymentRequestCardNoActual(ls_cardNoActual));
				doCreditCardPaymentRequest.setCurrency(xsdObjectFactory.createCreditCardPaymentRequestCurrency(ls_currency));
				doCreditCardPaymentRequest.setPassword(xsdObjectFactory.createCreditCardPaymentRequestPassword(ls_password));
				doCreditCardPaymentRequest.setRemarks(xsdObjectFactory.createCreditCardPaymentRequestRemarks(remarks));
				doCreditCardPaymentRequest.setUsername(xsdObjectFactory.createCreditCardPaymentRequestUsername(ls_userName));
				
				
				org.apache.ws.axis2.ObjectFactory axis2ObjectFactory=new org.apache.ws.axis2.ObjectFactory();
				//credit card request to store do credit card payment request object.
				org.apache.ws.axis2.CreditCardPayment doCreditCardPayment=axis2ObjectFactory.createCreditCardPayment();
				doCreditCardPayment.setRequest(axis2ObjectFactory.createCreditCardPaymentRequest(doCreditCardPaymentRequest));
				
				/*
				 * created response object of credit card payment response.
				 */
				CreditCardPaymentResponse creditCardPaymentresponse=null;
				try {
					creditCardPaymentresponse=(CreditCardPaymentResponse) soapConnector.callWebService(doCreditCardPayment);
				}
				catch(SoapFaultClientException soapException){				
					actualErrMsg=soapException.getFaultStringOrReason();			
					PrintErrLog("CreditCardPayment SoapFaultClientException : " + actualErrMsg);
					ls_responseData = common.ofGetErrDataJsonArray("999", "Alert", "Currently Service under maintenance so please try later (ENP040)", actualErrMsg, "", "0", "R");
					return ls_responseData;							
				}
				
				/*get credit card payment response object*/
				card.xsd.CreditCardPaymentResponse xsd_creditCardPaymentresponse=creditCardPaymentresponse.getReturn().getValue();
				
				//get the response
				approvalCode=xsd_creditCardPaymentresponse.getApprovalCode().getValue();
				cashierRequestId=xsd_creditCardPaymentresponse.getApprovalCode().getValue();
				ls_responseCode=xsd_creditCardPaymentresponse.getResponseCode().getValue();
				ls_responseMessage=xsd_creditCardPaymentresponse.getResponseMessage().getValue();
				
				/*
				 * if response is 100 then success.
				 * if response is 101 then transaction failed.
				 */
				if(ls_responseCode!=null && "100".equals(ls_responseCode)) {
					
					JSONObjectImpl creditCardPaymentJsonObj=new JSONObjectImpl();
					JSONObject responseJsonObject=new JSONObject();
					JSONArray creditCardPaymentJsonArray=new JSONArray();
					
					
					
					creditCardPaymentJsonObj.put("APPROVALCODE", approvalCode);
					creditCardPaymentJsonObj.put("CASHIERREQUEST_ID", cashierRequestId);
					creditCardPaymentJsonObj.put("RESPONSECODE", ls_responseCode);
					creditCardPaymentJsonObj.put("RESPONSEMESSAGE", ls_responseMessage);
					creditCardPaymentJsonArray.put(creditCardPaymentJsonObj);
					
					/*
					 * If transaction happens and API is called successfully.
					 */
					responseJsonObject.put("STATUS", "0");
					responseJsonObject.put("COLOR", "G");
					responseJsonObject.put("RESPONSE", creditCardPaymentJsonArray);
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
				PrintErrLog("CreditCardPayment Exception : " + actualErrMsg);
				ls_responseData = common.ofGetErrDataJsonArray("999", "Alert", "Currently Service under maintenance so please try later (ENP072)", actualErrMsg, "", "0", "R");		
			}
			return ls_responseData;
			}
		}

