package com.easynet.controller.ApiController;

import static com.easynet.util.common.PrintErrLog;

import java.util.ArrayList;

import org.apache.ws.axis2.GetFDRAccountsSummary;
import org.apache.ws.axis2.GetFDRAccountsSummaryResponse;
import org.apache.ws.axis2.ObjectFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.soap.client.SoapFaultClientException;

import com.easynet.configuration.SOAPConnector;
import com.easynet.impl.JSONObjectImpl;
import com.easynet.util.common;
import com.easynet.util.readXML;

import city.xsd.FDRAccountsSummaryResponse;
import city.xsd.GetFDRAccountsSummaryRequest;

@Component
public class FDRAccountsSummary {

		@Autowired
		private SOAPConnector soapConnector;
		
		public String getFDRAccouuntsSummary(String requestData) {
			
			String ls_cbsCustomerID="";
			String ls_password="";
			String ls_userName="";
			String ls_responseCode="";
			String ls_responseMessage="";
			String ls_responseData="";
			String actualErrMsg="";
			ArrayList<FDRAccountsSummaryResponse> FDRAccountsSummaryList=null;
			
			try {
				JSONObject requestDataObj=new JSONObject(requestData);
				ls_cbsCustomerID=requestDataObj.getString("CBSCUSTOMERID");
				
				
				if((ls_cbsCustomerID==null || "".equals(ls_cbsCustomerID))) {
					ls_responseData = common.ofGetErrDataJsonArray("99", "Validation Failed.", "InValid request.", "Null values found in card_no key.", "","", "R");
					return ls_responseData; 
				}
				ls_userName = readXML.getXmlData("root>CUST_360>AUTHORIZATION>USER_NAME");
				ls_password = readXML.getXmlData("root>CUST_360>AUTHORIZATION>PASSWORD");
				
				//created object factory object.
				city.xsd.ObjectFactory xsdObjectFactory=new city.xsd.ObjectFactory();
				//created get fdr accounts summary object from object factory.
				GetFDRAccountsSummaryRequest fdrAccountsSummaryRequest=xsdObjectFactory.createGetFDRAccountsSummaryRequest();
				
				//setting all the required fields.
				fdrAccountsSummaryRequest.setCbsCustomerID(xsdObjectFactory.createGetFDRAccountsSummaryRequestCbsCustomerID(ls_cbsCustomerID));
				fdrAccountsSummaryRequest.setPassword(xsdObjectFactory.createGetFDRAccountsSummaryRequestPassword(ls_password));
				fdrAccountsSummaryRequest.setUsername(xsdObjectFactory.createGetFDRAccountsSummaryRequestUsername(ls_userName));
				
				ObjectFactory axis2ObjectFactory=new ObjectFactory();
				//created request wrapper object.
				GetFDRAccountsSummary fdrAccountSummary=axis2ObjectFactory.createGetFDRAccountsSummary();
				fdrAccountSummary.setRequest(axis2ObjectFactory.createGetFDRAccountsSummaryRequest(fdrAccountsSummaryRequest));
				
				//created get fdr account response object.
				GetFDRAccountsSummaryResponse getFDRAccountsSummaryresponse=null;
				
				try {
					//calling the API with request data and getting response object.
					getFDRAccountsSummaryresponse=(GetFDRAccountsSummaryResponse) soapConnector.callWebService(fdrAccountSummary);
				}catch(SoapFaultClientException soapException){				
					actualErrMsg=soapException.getFaultStringOrReason();			
					PrintErrLog("GetFDRAccountsSummary SoapFaultClientException : " + actualErrMsg);
					ls_responseData = common.ofGetErrDataJsonArray("999", "Alert", "Currently Service under maintenance so please try later (ENP069)", actualErrMsg, "", "0", "R");
					return ls_responseData;							
				}
				
				//get the response object from API.
				city.xsd.GetFDRAccountsSummaryResponse xsd_getFDRAccountsSummaryresponse=getFDRAccountsSummaryresponse.getReturn().getValue();
				
				ls_responseCode=xsd_getFDRAccountsSummaryresponse.getResponseCode().getValue();
				ls_responseMessage=xsd_getFDRAccountsSummaryresponse.getResponseMessage().getValue();
				
				/*if response is 100 then success.
				 *If response is 101 then no records found.
				 */
				if(ls_responseCode!=null && "100".equals(ls_responseCode)) {
					
					JSONObject responseJsonObject=new JSONObject();
					JSONArray FDRaccountssummaryJsonArray=new JSONArray();
					
					FDRAccountsSummaryList=(ArrayList<FDRAccountsSummaryResponse>) xsd_getFDRAccountsSummaryresponse.getResponseData();
					for (FDRAccountsSummaryResponse Accountsummarylist : FDRAccountsSummaryList) {
						JSONObjectImpl FDRaccountssummaryJsonObj=new JSONObjectImpl();
						FDRaccountssummaryJsonObj.put("ACCTNO", Accountsummarylist.getAccountNumber().getValue());
						FDRaccountssummaryJsonObj.put("ACCOUNTSTATUS", Accountsummarylist.getAccountStatus().getValue());
						FDRaccountssummaryJsonObj.put("ACCOUNTTITLE", Accountsummarylist.getAccountTitle().getValue());
						FDRaccountssummaryJsonObj.put("ACCOUNTTYPE", Accountsummarylist.getAccountType().getValue());
						FDRaccountssummaryJsonObj.put("BALANCE", Accountsummarylist.getBalance());
						FDRaccountssummaryJsonObj.put("BRANCHCODE", Accountsummarylist.getBranchCode().getValue());
						FDRaccountssummaryJsonObj.put("BRANCHNAME", Accountsummarylist.getBranchName().getValue());
						FDRaccountssummaryJsonObj.put("CURRENCYCODE", Accountsummarylist.getCurrencyCode().getValue());
						FDRaccountssummaryJsonObj.put("INTERESTRATE",(Accountsummarylist.getInterestRate()) );
						FDRaccountssummaryJsonObj.put("LEDGERBALANCE", Accountsummarylist.getLedgerBalance().getValue());
						FDRaccountssummaryJsonObj.put("PRODUCTNAME", Accountsummarylist.getProductName().getValue());
						FDRaccountssummaryJsonObj.put("TENOR", Accountsummarylist.getTenor().getValue());
						
						FDRaccountssummaryJsonArray.put(FDRaccountssummaryJsonObj);
					
						
					}
						/*
						 * If transaction happens and API is called successfully.
						 */
						responseJsonObject.put("STATUS", "0");
						responseJsonObject.put("COLOR", "G");
						responseJsonObject.put("RESPONSE", FDRaccountssummaryJsonArray);
						responseJsonObject.put("MESSAGE","");
						ls_responseData=responseJsonObject.toString();
				}else
					{ //Response other than 100.
						ls_responseData = common.ofGetErrDataJsonArray("999", "Alert", "Currently Service under maintenance so please try later (ENP075).", ls_responseMessage, "",ls_responseCode, "R");
					}
			}
				
				catch(Exception err) {
					actualErrMsg = common.ofGetTotalErrString(err, "");
					PrintErrLog("GetFDRAccountsSummary Exception : " + actualErrMsg);
					ls_responseData = common.ofGetErrDataJsonArray("999", "Alert", "Currently Service under maintenance so please try later (ENP072)", actualErrMsg, "", "0", "R");		
				}
				return ls_responseData;
					
				
			}
			
			
			
			
			
			
			
			
	
		}

