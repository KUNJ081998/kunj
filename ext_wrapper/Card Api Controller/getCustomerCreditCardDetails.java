package com.easynet.controller.ApiController;

import static com.easynet.util.common.PrintErrLog;

import java.util.ArrayList;
import java.util.List;

import org.apache.ws.axis2.GetCustomerCreditCardsData;
import org.apache.ws.axis2.GetCustomerCreditCardsDataResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.easynet.configuration.SOAPConnector;
import com.easynet.impl.JSONObjectImpl;
import com.easynet.util.common;
import com.easynet.util.readXML;


import card.xsd.CreditCardListData;
import city.xsd.GetCustomerCreditCardsRequest;

@SuppressWarnings("hiding")
@Component
public class getCustomerCreditCardDetails<CreditCardListData> {

		@Autowired
		private SOAPConnector soapConnector;
		
		public String GetCustomerCreditCardDetails(String requestData) {
			
			String ls_clientId="";
			String ls_password="";
			String ls_userName="";
			String ls_responseCode="";
			String ls_responseMessage="";
			List<CreditCardListData> ls_responseData;
			String actualErrMsg="";
			
			try {
				JSONObject getcustomercarddetailobj=new JSONObject(requestData);
				getcustomercarddetailobj.get("CLIENTID");
				
				
				if((ls_clientId==null || "".equals(ls_clientId)))
				{
					ls_responseData = common.ofGetErrDataJsonArray("99", "Validation Failed.", "InValid request.", "Null values found in card_no key.", "","", "R");
					return ls_responseData; 
				}
				ls_userName = readXML.getXmlData("root>CUST_360>AUTHORIZATION>USER_NAME");
				ls_password = readXML.getXmlData("root>CUST_360>AUTHORIZATION>PASSWORD");
				
				
				//created object factory object.
				city.xsd.ObjectFactory xsdObjectFactory=new city.xsd.ObjectFactory();
				//created get customer credit cards request object from object factory.
				GetCustomerCreditCardsRequest getCustomerCreditCardsRequest=xsdObjectFactory.createGetCustomerCreditCardsRequest();
				
				//setting all the required fields.
				getCustomerCreditCardsRequest.setClientId(xsdObjectFactory.createGetCustomerCreditCardsRequestClientId(ls_clientId));
				getCustomerCreditCardsRequest.setPassword(xsdObjectFactory.createGetCustomerCreditCardsRequestPassword(ls_password));
				getCustomerCreditCardsRequest.setUsername(xsdObjectFactory.createGetCustomerCreditCardsRequestUsername(ls_userName));
				
				
				org.apache.ws.axis2.ObjectFactory axis2ObjectFactory=new org.apache.ws.axis2.ObjectFactory();
				//get customer credit cards request stored in get customer credit cards data.
				GetCustomerCreditCardsData getCustomerCreditCardsData=axis2ObjectFactory.createGetCustomerCreditCardsData();
				getCustomerCreditCardsData.setRequest(axis2ObjectFactory.createGetCustomerCreditCardsDataRequest(getCustomerCreditCardsRequest));
				
				/*
				 * created response object of get customer credit cards data response.
				 */
				GetCustomerCreditCardsDataResponse getCustomerCreditCardsDataReponse=null;
				
				try {
					
					getCustomerCreditCardsDataReponse=(GetCustomerCreditCardsDataResponse) soapConnector.callWebService(getCustomerCreditCardsData);
				}
				catch(SoapFaultClientException soapException){				
					actualErrMsg=soapException.getFaultStringOrReason();			
					PrintErrLog("GetCustomerCreditCardsDetails SoapFaultClientException : " + actualErrMsg);
					ls_responseData = common.ofGetErrDataJsonArray("999", "Alert", "Currently Service under maintenance so please try later (ENP040)", actualErrMsg, "", "0", "R");
					return ls_responseData;							
				}
				
				//created get customer credit cards data response object.
				city.xsd.GetCustomerCreditCardsDataResponse xsd_getcustomercreditcardsdataresponse=getCustomerCreditCardsDataReponse.getReturn().getValue();
				
				//getting all the values from the response object.
				ls_responseCode=xsd_getcustomercreditcardsdataresponse.getResponseCode().getValue();
				ls_responseMessage=xsd_getcustomercreditcardsdataresponse.getResponseMessage().getValue();
				
				/*if response is 100 then success.
				 *If response is 101 then no records found.
				 */
				if(ls_responseCode!=null && "100".equals(ls_responseCode)) {
					
					JSONObjectImpl customercreditcardsJsonObj=new JSONObjectImpl();
					JSONObject responseJsonObject=new JSONObject();
					JSONArray customercreditcardsJsonArray=new JSONArray();
					
					ArrayList<CreditCardListData>  creditcardlist=(ArrayList<CreditCardListData>) xsd_getcustomercreditcardsdataresponse.getResponseData();
					for(int i=0;i<=creditcardlist.size();i++) {
						
						
						
					}
					
					
				}
			}
			
		}
}
