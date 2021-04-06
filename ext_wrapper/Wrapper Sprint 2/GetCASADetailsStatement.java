package com.easynet.controller.ApiController;

import city.xsd.CASADetailsStatementResponse;
import city.xsd.GetCASADetailsStatementRequest;
import city.xsd.MiniStatementTransationList;
import city.xsd.ObjectFactory;
import com.easynet.configuration.PropConfiguration;
import com.easynet.configuration.SOAPConnector;
import com.easynet.impl.JSONObjectImpl;
import com.easynet.impl.LoggerImpl;
import com.easynet.util.common;
import com.easynet.util.readXML;
import java.util.ArrayList;
import org.apache.ws.axis2.GetCASADetailsStatementResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.soap.client.SoapFaultClientException;


    /**
     * This is GetCASADetailsStatement wrapper class which calls soap API
     * @Date -25/3/21
     */
@Component
public class GetCASADetailsStatement {
    
    static Logger logger=LoggerFactory.getLogger(GetCASADetailsStatement.class);
    
    @Autowired
    private SOAPConnector soapConnector;
    
    @Autowired
    private PropConfiguration propConfig;
    
        /**
	 *Call the soap API with given parameter.
	 *@param reqData string format json request data.
	 *@return return the json format string data.
	 *@apiNote This method call below API.<br>
	 *	1.getCASADetailsStatement
	 * 
	 * */
    
    public String getCASADetailsStatement(String reqData){
    
    String   ls_accountNumber="";
    String   ls_cbsCustomerId="";
    String   ls_fromDate="";
    String   ls_password="";
    String   ls_toDate="";
    String   ls_userName="";
    String   ls_responseCode="";
    String   ls_responseMessage="";
    String   ls_responseData="";
    String   ls_actualErrMsg="";
    String   ls_langResCodeMsg="";
    LoggerImpl       loggerImpl=null;
    ArrayList<MiniStatementTransationList> miniStatementTransactionList;
    
    
    try{
        loggerImpl=new LoggerImpl();
        
        loggerImpl.info(logger,"Preparing requset data and calling API.","IN:getCASADetailsStatement");
	loggerImpl.generateProfiler("getCASADetailsStatement");
        loggerImpl.startProfiler("Preparing request data");
                        
                        
        JSONObject reqJsonObj=new JSONObject(reqData);
        ls_accountNumber=reqJsonObj.getString("ACCTNUMBER");
        ls_cbsCustomerId=reqJsonObj.getString("CBSCUSTID");
        ls_fromDate=reqJsonObj.getString("FROMDATE");
        ls_toDate=reqJsonObj.getString("TODATE");
        
        if((ls_accountNumber==null || "".equals(ls_accountNumber)) || (ls_cbsCustomerId==null || "".equals(ls_cbsCustomerId)) || (ls_fromDate==null || "".equals(ls_fromDate))
                || (ls_toDate==null || "".equals(ls_toDate))){
            
            ls_responseData = common.ofGetErrDataJsonArray("99",
						propConfig.getMessageOfResCode("commen.title.99", "Validation Failed."),
						propConfig.getMessageOfResCode("commen.invalid_req_data",""),		
						"Null values found in request data.", "Invalid Request.","", "R");

				return ls_responseData;
        }
        ls_userName = readXML.getXmlData("root>CUST_360>AUTHORIZATION>USER_NAME");
	ls_password = readXML.getXmlData("root>CUST_360>AUTHORIZATION>PASSWORD");
        
        //get the object factory object.
        city.xsd.ObjectFactory xsdObjectFactory=new ObjectFactory();
        GetCASADetailsStatementRequest getCASADetailsStatementReq=xsdObjectFactory.createGetCASADetailsStatementRequest();
        
        loggerImpl.debug(logger,"Json to xml conversion done.", "IN:getCASADetailsStatement");
        
        //setting all the values in the request.
        getCASADetailsStatementReq.setAccountNumber(xsdObjectFactory.createGetCASADetailsStatementRequestAccountNumber(ls_accountNumber));
        getCASADetailsStatementReq.setCbsCustomerID(xsdObjectFactory.createGetCASADetailsStatementRequestCbsCustomerID(ls_cbsCustomerId));
        getCASADetailsStatementReq.setFromDate(xsdObjectFactory.createGetCASADetailsStatementRequestFromDate(ls_fromDate));
        getCASADetailsStatementReq.setPassword(xsdObjectFactory.createGetCASADetailsStatementRequestPassword(ls_password));
        getCASADetailsStatementReq.setToDate(xsdObjectFactory.createGetCASADetailsStatementRequestToDate(ls_toDate));
        getCASADetailsStatementReq.setUsername(xsdObjectFactory.createGetCASADetailsStatementRequestUsername(ls_userName));
        
        //get the object factory object.
        org.apache.ws.axis2.ObjectFactory axis2ObjectFactory=new org.apache.ws.axis2.ObjectFactory();
        
        //wrapping the request object in the request.
        org.apache.ws.axis2.GetCASADetailsStatement getcasadetailsstatement=axis2ObjectFactory.createGetCASADetailsStatement();
        getcasadetailsstatement.setRequest(axis2ObjectFactory.createGetCASADetailsStatementRequest(getCASADetailsStatementReq));
        
        
        loggerImpl.debug(logger,"getCASADetailsStatement API calling", "IN:getCASADetailsStatement");
	loggerImpl.startProfiler("getCASADetailsStatement API calling.");
        
        
        //get the response object.
        GetCASADetailsStatementResponse getCASADetailsStatementResponse=null;
        try{
            getCASADetailsStatementResponse=(GetCASADetailsStatementResponse) soapConnector.callWebService(getcasadetailsstatement);
        }catch (SoapFaultClientException soapException) {
				ls_actualErrMsg = soapException.getFaultStringOrReason();
				ls_responseData = common.ofGetErrDataJsonArray("999", 
						propConfig.getMessageOfResCode("commen.title.999", "Alert."),
						propConfig.getMessageOfResCode("commen.exception.999","","(ENP197)"),
						ls_actualErrMsg, "Currently Service under maintenance so please try later (ENP197)", "0", "R");

				ls_actualErrMsg = common.ofGetTotalErrString(soapException, "");
				loggerImpl.error(logger,"SoapFaultClientException : " + ls_actualErrMsg,"EX:getCASADetailsStatement");
				return ls_responseData;
			}
        
        loggerImpl.debug(logger,"getCASADetailsStatement API called successfully.", "IN:getCASADetailsStatement",getCASADetailsStatementResponse);
	loggerImpl.startProfiler("preparing getCASADetailsStatement API response data.");
        
        //get the response object.
        city.xsd.GetCASADetailsStatementResponse xsd_getCASADetailsStatementResp=getCASADetailsStatementResponse.getReturn().getValue();
        
        //getting all the values from the response data.
        ls_responseCode=xsd_getCASADetailsStatementResp.getResponseCode().getValue();
        ls_responseMessage=xsd_getCASADetailsStatementResp.getResponseMessage().getValue();
        
        /*if response is 100 then success.
         "000"-not successful
        */
        
        if (ls_responseCode != null && "100".equals(ls_responseCode)) {
            
            JSONObjectImpl getCASADetailsJsonObj = new JSONObjectImpl();
	    JSONObject responseJsonObject = new JSONObject();
            JSONArray getCASADetailsJsonArray = new JSONArray();
            
            CASADetailsStatementResponse CASADetailsStatementResp=xsd_getCASADetailsStatementResp.getResponseData().getValue();
            miniStatementTransactionList=(ArrayList<MiniStatementTransationList>) CASADetailsStatementResp.getTransactionList();
            
            getCASADetailsJsonObj.put("ACCTHOLDERNAME", CASADetailsStatementResp.getAccountHolderName().getValue());
            getCASADetailsJsonObj.put("ACCTNUMBER", CASADetailsStatementResp.getAccountNumber().getValue());
            getCASADetailsJsonObj.put("ACCTSTATUS", CASADetailsStatementResp.getAccountStatus().getValue());
            getCASADetailsJsonObj.put("ACCTTYPE", CASADetailsStatementResp.getAccountType().getValue());
            getCASADetailsJsonObj.put("CURRENCYCODE", CASADetailsStatementResp.getCurrencyCode().getValue());
            getCASADetailsJsonObj.put("OPENINGBAL", CASADetailsStatementResp.getOpeningBalance());
            getCASADetailsJsonObj.put("TOTALBAL", CASADetailsStatementResp.getTotalBalance());
            getCASADetailsJsonObj.put("TOTALCREDIT", CASADetailsStatementResp.getTotalCredit());
            getCASADetailsJsonObj.put("TOTALDEBIT", CASADetailsStatementResp.getTotalDebit());
            
            for (MiniStatementTransationList miniStatementTransationList : miniStatementTransactionList) {
                getCASADetailsJsonObj.put("AVAILBAL", miniStatementTransationList.getAvailableBalance());
                getCASADetailsJsonObj.put("DEPOSIT", miniStatementTransationList.getDeposit());
                getCASADetailsJsonObj.put("DESCRIPTION", miniStatementTransationList.getDescription().getValue());
                getCASADetailsJsonObj.put("OUTSTANDINGBAL", miniStatementTransationList.getOutstandingBalance());
                getCASADetailsJsonObj.put("REFCHEQUE", miniStatementTransationList.getRefCheque().getValue());
                getCASADetailsJsonObj.put("TRANSDATE", miniStatementTransationList.getTransactionDate().getValue());
                getCASADetailsJsonObj.put("WTIHDRAW", miniStatementTransationList.getWithdraw());
            }
            
            getCASADetailsJsonArray.put(getCASADetailsJsonObj);
            
            responseJsonObject.put("STATUS", "0");
            responseJsonObject.put("COLOR", "G");
            responseJsonObject.put("RESPONSE", getCASADetailsJsonArray);
            responseJsonObject.put("MESSAGE", "");
            responseJsonObject.put("RESPONSECODE",ls_responseCode);
            responseJsonObject.put("RESPONSEMESSAGE",ls_responseMessage);
            ls_responseData = responseJsonObject.toString();
        }else { 

				ls_langResCodeMsg=propConfig.getResponseCode("getCASADetailsStatement."+ls_responseCode);			
				ls_responseData = common.ofGetErrDataJsonArray(ls_langResCodeMsg,
						propConfig.getMessageOfResCode("commen.title."+ls_langResCodeMsg, "Alert."), 
						propConfig.getMessageOfResCode("getCASADetailsStatement."+ls_responseCode,"","(ENP198)"),
						ls_responseMessage, "Currently Service under maintenance so please try later (ENP198).", ls_responseCode, "R");            		            		            	
			}
    }catch (Exception exception) {
			ls_actualErrMsg = common.ofGetTotalErrString(exception, "");            
			loggerImpl.error(logger,"Exception : " + ls_actualErrMsg,"IN:getCASADetailsStatement");

			ls_responseData = common.ofGetErrDataJsonArray("999",
					propConfig.getMessageOfResCode("commen.title.999", "Alert."),
					propConfig.getMessageOfResCode("commen.exception.999","","(ENP199)"),
					exception.getMessage(),"Currently Service under maintenance so please try later (ENP199)","0", "R");			

		}finally {
			loggerImpl.stopAndPrintOptLogs(logger,"All API called successfully.","IN:getCASADetailsStatement");
			loggerImpl.info(logger,"Response generated and send to client.", "IN:getCASADetailsStatement");        	
		}
    return ls_responseData;
    }
    
}
