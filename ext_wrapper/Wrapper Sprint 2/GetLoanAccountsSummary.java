package com.easynet.controller.ApiController;

import city.xsd.GetAccountsSummaryRequest;
import city.xsd.LoanAccountSummary;
import city.xsd.ObjectFactory;
import com.easynet.configuration.PropConfiguration;
import com.easynet.configuration.SOAPConnector;
import static com.easynet.controller.ApiController.DoBkashDepositMoney.logger;
import com.easynet.impl.JSONObjectImpl;
import com.easynet.impl.LoggerImpl;
import com.easynet.util.common;
import com.easynet.util.readXML;
import java.util.ArrayList;
import org.apache.ws.axis2.GetLoanAccountsSummaryResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.soap.client.SoapFaultClientException;


@Component
public class GetLoanAccountsSummary {
    
    static Logger logger=LoggerFactory.getLogger(GetLoanAccountsSummary.class);
    
    @Autowired
    private SOAPConnector soapConnector;
    
    @Autowired
    PropConfiguration propConfig;
    
    
    public String getLoanAccountsSummary(String reqData){
        
        String    ls_cbsCustomerId="";
        String    ls_password="";
        String    ls_userName="";
        String    ls_responseData="";
        String    ls_responseCode="";
        String    ls_responseMessage="";
        String    ls_actualErrMsg="";
        String    ls_langResCodeMsg="";
        ArrayList<LoanAccountSummary> LoanAccountSummaryList;
        LoggerImpl loggerImpl=null;
        
        try{
            
            loggerImpl=new LoggerImpl();
            
            loggerImpl.info(logger, "Preparing requset data and calling API.", "IN:GetLoanAccountsSummary");
            loggerImpl.generateProfiler("GetLoanAccountsSummary");
            loggerImpl.startProfiler("Preparing request data");
            
            JSONObject reqJsonObj=new JSONObject(reqData);
            ls_cbsCustomerId=reqJsonObj.getString("CBSCUSTID");
            
            if((ls_cbsCustomerId==null || "".equals(ls_cbsCustomerId))){
                
                ls_responseData = common.ofGetErrDataJsonArray("99",
                propConfig.getMessageOfResCode("commen.title.99", "Validation Failed."),
                propConfig.getMessageOfResCode("commen.invalid_req_data", ""),
                "Null values found in request data.", "Invalid Request.", "", "R");
                return ls_responseData;
            }
            
            ls_userName = readXML.getXmlData("root>CUST_360>AUTHORIZATION>USER_NAME");
            ls_password = readXML.getXmlData("root>CUST_360>AUTHORIZATION>PASSWORD");
            
            //get the object factory object.
            city.xsd.ObjectFactory xsdObjectFactory=new ObjectFactory();
            //get the request object.
            GetAccountsSummaryRequest getAccountsSummaryReq=xsdObjectFactory.createGetAccountsSummaryRequest();
            
            loggerImpl.debug(logger,"Json to xml conversion done.", "IN:GetLoanAccountsSummary");
            
            //setting values in request object.
            getAccountsSummaryReq.setCbsCustomerID(xsdObjectFactory.createGetAccountsSummaryRequestCbsCustomerID(ls_cbsCustomerId));
            getAccountsSummaryReq.setPassword(xsdObjectFactory.createGetAccountsSummaryRequestPassword(ls_password));
            getAccountsSummaryReq.setUsername(xsdObjectFactory.createGetAccountsSummaryRequestUsername(ls_userName));
            
            
            //get the object factory object.
            org.apache.ws.axis2.ObjectFactory axis2ObjectFactory=new org.apache.ws.axis2.ObjectFactory();
            
            //wrapping the request object in the request object.
            org.apache.ws.axis2.GetLoanAccountsSummary getloanAccountsSummary=axis2ObjectFactory.createGetLoanAccountsSummary();
            getloanAccountsSummary.setRequest(axis2ObjectFactory.createGetLoanAccountsSummaryRequest(getAccountsSummaryReq));
            
            
            loggerImpl.debug(logger,"GetLoanAccountsSummary API calling", "IN:GetLoanAccountsSummary");
            loggerImpl.startProfiler("GetLoanAccountsSummary API calling.");
            
            //get the response object.
            GetLoanAccountsSummaryResponse getLoanAccountsSummaryResp=null;
            try{
                getLoanAccountsSummaryResp=(GetLoanAccountsSummaryResponse) soapConnector.callWebService(getloanAccountsSummary);
            }catch (SoapFaultClientException soapException) {
                ls_actualErrMsg = soapException.getFaultStringOrReason();
                ls_actualErrMsg = common.ofGetTotalErrString(soapException, "");
                ls_responseData = common.ofGetErrDataJsonArray("999", 
						propConfig.getMessageOfResCode("commen.title.999", "Alert."),
						propConfig.getMessageOfResCode("commen.exception.999","","(ENP177)"),
						ls_actualErrMsg, "Currently Service under maintenance so please try later (ENP177)", "0", "R");
                loggerImpl.error(logger,"SoapFaultClientException : " + ls_actualErrMsg,"EX:GetLoanAccountsSummary");
                return ls_responseData;
            }
            
            loggerImpl.debug(logger,"GetLoanAccountsSummary API called successfully.", "IN:GetLoanAccountsSummary",getLoanAccountsSummaryResp);
            loggerImpl.startProfiler("preparing GetLoanAccountsSummary API response data.");
            
            //get the response object.
            city.xsd.GetLoanAccountsSummaryResponse xsd_getLoanAccountsSummaryResp=getLoanAccountsSummaryResp.getReturn().getValue();
            
            //getting all the data from the values.
            ls_responseCode=xsd_getLoanAccountsSummaryResp.getResponseCode().getValue();
            ls_responseMessage=xsd_getLoanAccountsSummaryResp.getResponseMessage().getValue();
            
                /* If response is 100 then success.*/
                JSONObjectImpl GetLoanAccountsSummaryJsonObj = new JSONObjectImpl();
                JSONObject responseJsonObject = new JSONObject();
                JSONArray GetLoanAccountsSummaryJsonArray = new JSONArray();
                
                LoanAccountSummaryList=(ArrayList<LoanAccountSummary>) xsd_getLoanAccountsSummaryResp.getResponseData();
                
                for (LoanAccountSummary loanAccountSummary : LoanAccountSummaryList) {
                    
                    GetLoanAccountsSummaryJsonObj.put("ACCTNUMBER", loanAccountSummary.getAccountNumber().getValue());
                    GetLoanAccountsSummaryJsonObj.put("ACCTSTATUS", loanAccountSummary.getAccountStatus().getValue());
                    GetLoanAccountsSummaryJsonObj.put("ACCTTYPE", loanAccountSummary.getAccountType().getValue());
                    GetLoanAccountsSummaryJsonObj.put("AVAILABLEBAL", loanAccountSummary.getAvailableBalance());
                    GetLoanAccountsSummaryJsonObj.put("BRANCHCODE", loanAccountSummary.getBranchCode().getValue());
                    GetLoanAccountsSummaryJsonObj.put("CURRENCYCODE", loanAccountSummary.getCurrencyCode().getValue());
                    GetLoanAccountsSummaryJsonObj.put("LEDGERBAL", loanAccountSummary.getLedgerBalance().getValue());
                    GetLoanAccountsSummaryJsonObj.put("NEXTINSTALLMENTDATE", loanAccountSummary.getNextInstallmentDate().getValue());
                    GetLoanAccountsSummaryJsonObj.put("OUTSTANDINGBAL", loanAccountSummary.getOutStandingBalance());
                    GetLoanAccountsSummaryJsonObj.put("PRODUCTNAME", loanAccountSummary.getProductName().getValue());
                    GetLoanAccountsSummaryJsonObj.put("TENOR", loanAccountSummary.getTenor().getValue());
                    
                
            }
                
                GetLoanAccountsSummaryJsonArray.put(GetLoanAccountsSummaryJsonObj);
                
                /*
                 * If transaction happens and API is called successfully.
                 */
                responseJsonObject.put("STATUS", "0");
                responseJsonObject.put("COLOR", "G");
                responseJsonObject.put("RESPONSE", GetLoanAccountsSummaryJsonArray);
                responseJsonObject.put("MESSAGE", "");
                responseJsonObject.put("RESPONSEMESSAGE", ls_responseMessage);
                responseJsonObject.put("RESPONSECODE", ls_responseCode);
                ls_responseData = responseJsonObject.toString();
                
                
            
        }catch (Exception exception) {
            ls_actualErrMsg = common.ofGetTotalErrString(exception, "");            
			loggerImpl.error(logger,"Exception : " + ls_actualErrMsg,"IN:GetLoanAccountsSummary");

			ls_responseData = common.ofGetErrDataJsonArray("999",
					propConfig.getMessageOfResCode("commen.title.999", "Alert."),
					propConfig.getMessageOfResCode("commen.exception.999","","(ENP178)"),
					exception.getMessage(),"Currently Service under maintenance so please try later (ENP178)","0", "R");
        }finally {
			loggerImpl.stopAndPrintOptLogs(logger,"All API called successfully.","IN:GetLoanAccountsSummary");
			loggerImpl.info(logger,"Response generated and send to client.", "IN:GetLoanAccountsSummary");        	
		}
        return ls_responseData;
    }
}
