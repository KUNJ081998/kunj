package com.easynet.controller.ApiController.AccountAPI;

import city.xsd.AccountsSummaryResponse;
import city.xsd.GetAccountsSummaryRequest;
import city.xsd.ObjectFactory;
import com.easynet.configuration.PropConfiguration;
import com.easynet.configuration.SOAPConnector;
import com.easynet.impl.JSONObjectImpl;
import com.easynet.impl.LoggerImpl;
import com.easynet.util.common;
import static com.easynet.util.common.PrintErrLog;
import com.easynet.util.readXML;
import java.util.ArrayList;
import org.apache.ws.axis2.GetCASAAccountsSummaryResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.soap.client.SoapFaultClientException;

@Component
public class GetCASAAccountsSummary {

    static Logger logger=LoggerFactory.getLogger(GetCASAAccountsSummary.class);
    
    @Autowired
    PropConfiguration propConfig;
    
    
    @Autowired
    private SOAPConnector soapConnector;

    public String getCASAAccountsSummary(String reqData) {

        String ls_cbsCustomerID = "";
        String ls_userName = "";
        String ls_password = "";
        String ls_responseCode = "";
        String ls_responseData = "";
        String ls_responseMessage = "";
        String ls_actualErrMsg = "";
        String ls_langResCodeMsg="";
        LoggerImpl loggerImpl=null;
        ArrayList<AccountsSummaryResponse> AccountsSummaryList;

        try {
            
            loggerImpl=new LoggerImpl();
            
            loggerImpl.info(logger, "Preaparing request data and calling API", "IN:getCASAAccountsSummary");
            loggerImpl.generateProfiler("getCASAAccountsSummary");
            loggerImpl.startProfiler("Preparing request data.");
            
            
            JSONObject reqJsonObj = new JSONObject(reqData);
            ls_cbsCustomerID = reqJsonObj.getString("CBSCUSTOMERID");

            if ((ls_cbsCustomerID == null || "".equals(ls_cbsCustomerID))) {

                ls_responseData = common.ofGetErrDataJsonArray("99",
						propConfig.getMessageOfResCode("commen.title.99", "Validation Failed."),
						propConfig.getMessageOfResCode("commen.invalid_req_data",""),		
						"Null values found in request data.", "Invalid Request.","", "R");
            }
            ls_userName = readXML.getXmlData("root>CUST_360>AUTHORIZATION>USER_NAME");
            ls_password = readXML.getXmlData("root>CUST_360>AUTHORIZATION>PASSWORD");

            //created object factory object of city.xsd.
            city.xsd.ObjectFactory xsdObjectFactroy = new ObjectFactory();
            //created object of CASAAccountsSummaryRequest.
            GetAccountsSummaryRequest getAccountsSummaryRequest = xsdObjectFactroy.createGetAccountsSummaryRequest();
            
            loggerImpl.debug(logger,"Json to xml conversion done.", "getCASAAccountsSummary");

            //setting the values in the request.
            getAccountsSummaryRequest.setCbsCustomerID(xsdObjectFactroy.createGetAccountsSummaryRequestCbsCustomerID(ls_cbsCustomerID));
            getAccountsSummaryRequest.setPassword(xsdObjectFactroy.createGetAccountsSummaryRequestPassword(ls_password));
            getAccountsSummaryRequest.setUsername(xsdObjectFactroy.createGetAccountsSummaryRequestUsername(ls_userName));

            //created object of object factory of ws.axis2.
            org.apache.ws.axis2.ObjectFactory axis2ObjectFactory = new org.apache.ws.axis2.ObjectFactory();
            //wrapping the object in GetCASAACcountsSummary.
            org.apache.ws.axis2.GetCASAAccountsSummary getCASAAccountssummary = axis2ObjectFactory.createGetCASAAccountsSummary();
            getCASAAccountssummary.setRequest(axis2ObjectFactory.createGetCASAAccountsSummaryRequest(getAccountsSummaryRequest));
            
            loggerImpl.debug(logger,"getCASAAccountsSummary API calling", "getCASAAccountsSummary");
            loggerImpl.startProfiler("getCASAAccountsSummary API calling.");

            //created response object of GetCASAAccountsSummaryresponse.
            GetCASAAccountsSummaryResponse getCASAAccountsSummaryresponse = null;
            try {
                getCASAAccountsSummaryresponse = (GetCASAAccountsSummaryResponse) soapConnector.callWebService(getCASAAccountssummary);
            }catch (SoapFaultClientException soapException) {
				ls_actualErrMsg = soapException.getFaultStringOrReason();
				ls_responseData = common.ofGetErrDataJsonArray("999", 
						propConfig.getMessageOfResCode("commen.title.999", "Alert."),
						propConfig.getMessageOfResCode("commen.exception.999","","(ENP236)"),
						ls_actualErrMsg, "Currently Service under maintenance so please try later (ENP236)", "0", "R");

				ls_actualErrMsg = common.ofGetTotalErrString(soapException, "");
				loggerImpl.error(logger,"SoapFaultClientException : " + ls_actualErrMsg,"EX:getCASAAccountsSummary");
				return ls_responseData;
			}
            
            loggerImpl.debug(logger,"getCASAAccountsSummary API called successfully.", "getCASAAccountsSummary",getCASAAccountsSummaryresponse);
            loggerImpl.startProfiler("preparing getCASAAccountsSummary API response data.");

            //get the response object of city.xsd.
            city.xsd.GetCASAAccountsSummaryResponse xsd_getCASAAccountsSummaryResponse = getCASAAccountsSummaryresponse.getReturn().getValue();

            //getting all the data from response.
            ls_responseCode = xsd_getCASAAccountsSummaryResponse.getResponseCode().getValue();
            ls_responseMessage = xsd_getCASAAccountsSummaryResponse.getResponseMessage().getValue();
          

            /*if response is 100 then success.
	     * If response is 101 then no records found.
             */
            if (ls_responseCode != null && "100".equals(ls_responseCode)) {

                JSONObject responseJsonObject = new JSONObject();
                JSONArray CASAAccountsJsonArray = new JSONArray();

                AccountsSummaryList = (ArrayList<AccountsSummaryResponse>) xsd_getCASAAccountsSummaryResponse.getResponseData();
                for (AccountsSummaryResponse accountsSummaryResponse : AccountsSummaryList) {

                    JSONObjectImpl CASAAccountsJsonObj = new JSONObjectImpl();
                   
                    CASAAccountsJsonObj.put("ACCOUNTNUMBER", accountsSummaryResponse.getAccountNumber().getValue());
                    CASAAccountsJsonObj.put("ACCOUNTSTATUS", accountsSummaryResponse.getAccountStatus().getValue());
                    CASAAccountsJsonObj.put("ACCOUNTTYPE", accountsSummaryResponse.getAccountType().getValue());
                    CASAAccountsJsonObj.put("AVAILBALANCE", accountsSummaryResponse.getAvailableBalance());
                    CASAAccountsJsonObj.put("BRANCHCODE", accountsSummaryResponse.getBranchCode().getValue());
                    CASAAccountsJsonObj.put("CURRENCYCODE", accountsSummaryResponse.getCurrencyCode().getValue());
                    CASAAccountsJsonObj.put("LEDGERBALANCE", accountsSummaryResponse.getLedgerBalance().getValue());
                    CASAAccountsJsonObj.put("PRODUCTNAME", accountsSummaryResponse.getProductName().getValue());

                    CASAAccountsJsonArray.put(CASAAccountsJsonObj);
                    
                }
                responseJsonObject.put("STATUS", "0");
                responseJsonObject.put("COLOR", "G");
                responseJsonObject.put("RESPONSE", CASAAccountsJsonArray);
                responseJsonObject.put("MESSAGE", "");
                responseJsonObject.put("RESPONSECODE", ls_responseCode);
                responseJsonObject.put("RESPONSEMESSAGE", ls_responseMessage);
               
                ls_responseData=responseJsonObject.toString();
            } else { 

				ls_langResCodeMsg=propConfig.getResponseCode("getCASAAccountsSummary."+ls_responseCode);			
				ls_responseData = common.ofGetErrDataJsonArray(ls_langResCodeMsg,
						propConfig.getMessageOfResCode("commen.title."+ls_langResCodeMsg, "Alert."), 
						propConfig.getMessageOfResCode("getCASAAccountsSummary."+ls_responseCode,"","(ENP237)"),
						ls_responseMessage, "Currently Service under maintenance so please try later (ENP237).", ls_responseCode, "R");            		            		            	
			}
        } catch (Exception exception) {
			ls_actualErrMsg = common.ofGetTotalErrString(exception, "");            
			loggerImpl.error(logger,"Exception : " + ls_actualErrMsg,"IN:getCASAAccountsSummary");

			ls_responseData = common.ofGetErrDataJsonArray("999",
					propConfig.getMessageOfResCode("commen.title.999", "Alert."),
					propConfig.getMessageOfResCode("commen.exception.999","","(ENP238)"),
					exception.getMessage(),"Currently Service under maintenance so please try later (ENP238)","0", "R");			

		}finally {
        	loggerImpl.stopAndPrintOptLogs(logger,"All API called successfully.","getCASAAccountsSummary");
        	loggerImpl.info(logger,"Response generated and send to client.", "getCASAAccountsSummary");        	
        }
        return ls_responseData;

    }
}
