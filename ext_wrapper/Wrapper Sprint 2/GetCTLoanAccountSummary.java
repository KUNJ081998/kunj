package com.easynet.controller.ApiController;

import city.xsd.GetAccountsSummaryRequest;
import city.xsd.GetLoanAccountsSummaryResponse;
import city.xsd.LoanAccountSummary;
import city.xsd.ObjectFactory;
import com.easynet.configuration.PropConfiguration;
import com.easynet.configuration.SOAPConnector;
//import static com.easynet.controller.ApiController.DoMobileTopup.logger;
import com.easynet.impl.JSONObjectImpl;
import com.easynet.impl.LoggerImpl;
import com.easynet.util.common;
import static com.easynet.util.common.PrintErrLog;
import com.easynet.util.readXML;
import java.util.ArrayList;
import org.apache.ws.axis2.GetCTLoanAccountsSummary;
import org.apache.ws.axis2.GetCTLoanAccountsSummaryResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.soap.client.SoapFaultClientException;

@Component
public class GetCTLoanAccountSummary {

    static Logger logger=LoggerFactory.getLogger(GetCTLoanAccountSummary.class);
    @Autowired
    private SOAPConnector soapConnector;
    
    @Autowired
    PropConfiguration propConfig;

    public String getCTLoanAccountSummary(String reqData) {
        String ls_cbsCustomerID = "";
        String ls_responseCode = "";
        String ls_responseData = "";
        String ls_responseMessage = "";
        String ls_userName = "";
        String ls_password = "";
        String ls_actualErrMsg = "";
        String ls_langResCodeMsg="";
        ArrayList<LoanAccountSummary> LoanAccountSummaryList;
        LoggerImpl loggerImpl=null;

        try {
            
            loggerImpl=new LoggerImpl();
            
            loggerImpl.info(logger, "Preaparing request data and calling API", "IN:getCTLoanAccountSummary");
            loggerImpl.generateProfiler("getCTLoanAccountSummary");
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

            //get object factory object.
            city.xsd.ObjectFactory xsdObjectFactory = new ObjectFactory();
            //created GetAccountsSummaryRequest object.
            GetAccountsSummaryRequest getAccountsSummaryRequest = xsdObjectFactory.createGetAccountsSummaryRequest();
            
             loggerImpl.debug(logger,"Json to xml conversion done.", "getCTLoanAccountSummary");

            //setting all the values in the request.
            getAccountsSummaryRequest.setCbsCustomerID(xsdObjectFactory.createGetAccountsSummaryRequestCbsCustomerID(ls_cbsCustomerID));
            getAccountsSummaryRequest.setPassword(xsdObjectFactory.createGetAccountsSummaryRequestPassword(ls_password));
            getAccountsSummaryRequest.setUsername(xsdObjectFactory.createGetAccountsSummaryRequestUsername(ls_userName));

            //get the object factory object.
            org.apache.ws.axis2.ObjectFactory axis2ObjectFactory = new org.apache.ws.axis2.ObjectFactory();
            //wrapping the request in GetCTLoanAccountsSummary.
            GetCTLoanAccountsSummary getctLoanAccountsSummary = axis2ObjectFactory.createGetCTLoanAccountsSummary();
            getctLoanAccountsSummary.setRequest(axis2ObjectFactory.createGetCTLoanAccountsSummaryRequest(getAccountsSummaryRequest));
            
            loggerImpl.debug(logger,"getCTLoanAccountSummary API calling", "getCTLoanAccountSummary");
            loggerImpl.startProfiler("getCTLoanAccountSummary API calling.");

            //created GetCTLoanAccountsSummaryResponse object.
            GetCTLoanAccountsSummaryResponse getCTLoanAccountsSummaryResponse = null;
            try {
                getCTLoanAccountsSummaryResponse = (GetCTLoanAccountsSummaryResponse) soapConnector.callWebService(getctLoanAccountsSummary);
            } catch (SoapFaultClientException soapException) {
				ls_actualErrMsg = soapException.getFaultStringOrReason();
				ls_responseData = common.ofGetErrDataJsonArray("999", 
						propConfig.getMessageOfResCode("commen.title.999", "Alert."),
						propConfig.getMessageOfResCode("commen.exception.999","","(ENP200)"),
						ls_actualErrMsg, "Currently Service under maintenance so please try later (ENP200)", "0", "R");

				ls_actualErrMsg = common.ofGetTotalErrString(soapException, "");
				loggerImpl.error(logger,"SoapFaultClientException : " + ls_actualErrMsg,"EX:getCTLoanAccountSummary");
				return ls_responseData;
			}
            
            loggerImpl.debug(logger,"getCTLoanAccountSummary API called successfully.", "getCTLoanAccountSummary",getCTLoanAccountsSummaryResponse);
            loggerImpl.startProfiler("preparing getCTLoanAccountSummary API response data.");

            //created response object of GetLoanAccountsSummaryResponse.
            GetLoanAccountsSummaryResponse xsd_getLoanAccountsSummaryResponse = getCTLoanAccountsSummaryResponse.getReturn().getValue();

            //getting all the data from the response.
            ls_responseCode = xsd_getLoanAccountsSummaryResponse.getResponseCode().getValue();
            ls_responseMessage = xsd_getLoanAccountsSummaryResponse.getResponseMessage().getValue();
            
            
            
            /*
             * if response is 100 then success.
             * if response is 101 then transaction failed.
             */
            if (ls_responseCode != null && "100".equals(ls_responseCode)) {

                JSONArray CTLoanAccountsSummaryJsonArray = new JSONArray();
                JSONObject responseJsonObject = new JSONObject();

                LoanAccountSummaryList = (ArrayList<LoanAccountSummary>) xsd_getLoanAccountsSummaryResponse.getResponseData();
                for (LoanAccountSummary loanAccountSummary : LoanAccountSummaryList) {

                    JSONObjectImpl CTLoanAccountsSummaryJsonObj = new JSONObjectImpl();
                    
                    CTLoanAccountsSummaryJsonObj.put("ACCTNUMBER", loanAccountSummary.getAccountNumber().getValue());
                    CTLoanAccountsSummaryJsonObj.put("ACCTSTATUS", loanAccountSummary.getAccountStatus().getValue());
                    CTLoanAccountsSummaryJsonObj.put("ACCTTYPE", loanAccountSummary.getAccountType().getValue());
                    CTLoanAccountsSummaryJsonObj.put("AVAILBALANCE", loanAccountSummary.getAvailableBalance());
                    CTLoanAccountsSummaryJsonObj.put("BRANCHCODE", loanAccountSummary.getBranchCode().getValue());
                    CTLoanAccountsSummaryJsonObj.put("CURRENCYCODE", loanAccountSummary.getCurrencyCode().getValue());
                    CTLoanAccountsSummaryJsonObj.put("LEDGERBAL", loanAccountSummary.getLedgerBalance().getValue());
                    CTLoanAccountsSummaryJsonObj.put("NEXTINSTALLMENTDATE", loanAccountSummary.getNextInstallmentDate().getValue());
                    CTLoanAccountsSummaryJsonObj.put("OUTSTANDINGBAL", loanAccountSummary.getOutStandingBalance());
                    CTLoanAccountsSummaryJsonObj.put("PRODUCTNAME", loanAccountSummary.getProductName().getValue());
                    CTLoanAccountsSummaryJsonObj.put("TENOR", loanAccountSummary.getTenor().getValue());
                   

                    CTLoanAccountsSummaryJsonArray.put(CTLoanAccountsSummaryJsonObj);

                }
                /*
                 * If transaction happens and API is called successfully.
                 */
                responseJsonObject.put("STATUS", "0");
                responseJsonObject.put("COLOR", "G");
                responseJsonObject.put("RESPONSE", CTLoanAccountsSummaryJsonArray);
                responseJsonObject.put("RESPONSEMESSGAE", ls_responseMessage);
                responseJsonObject.put("RESPONSECODE", ls_responseCode);
                ls_responseData = responseJsonObject.toString();
            } 
            else { 

				ls_langResCodeMsg=propConfig.getResponseCode("GetCTLoanAcccountsSummary."+ls_responseCode);			
				ls_responseData = common.ofGetErrDataJsonArray(ls_langResCodeMsg,
						propConfig.getMessageOfResCode("commen.title."+ls_langResCodeMsg, "Alert."), 
						propConfig.getMessageOfResCode("GetCTLoanAcccountsSummary."+ls_responseCode,"","(ENP201)"),
						ls_responseMessage, "Currently Service under maintenance so please try later (ENP201).", ls_responseCode, "R");            		            		            	
			}

        } catch (Exception exception) {
			ls_actualErrMsg = common.ofGetTotalErrString(exception, "");            
			loggerImpl.error(logger,"Exception : " + ls_actualErrMsg,"IN:GetCTLoanAcccountsSummary");

			ls_responseData = common.ofGetErrDataJsonArray("999",
					propConfig.getMessageOfResCode("commen.title.999", "Alert."),
					propConfig.getMessageOfResCode("commen.exception.999","","(ENP202)"),
					exception.getMessage(),"Currently Service under maintenance so please try later (ENP202)","0", "R");			

		}finally {
        	loggerImpl.stopAndPrintOptLogs(logger,"All API called successfully.","GetCTLoanAcccountsSummary");
        	loggerImpl.info(logger,"Response generated and send to client.", "GetCTLoanAcccountsSummary");        	
        }
        return ls_responseData;
    }
}
