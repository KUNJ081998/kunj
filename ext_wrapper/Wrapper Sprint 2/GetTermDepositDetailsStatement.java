package com.easynet.controller.ApiController;

import city.xsd.GetTermDepositDetailsStatementRequest;
import city.xsd.MiniStatementTransationList;
import city.xsd.ObjectFactory;
import city.xsd.TermDepositDetailsStatementResponse;
import com.easynet.configuration.PropConfiguration;
import com.easynet.configuration.SOAPConnector;
import static com.easynet.controller.ApiController.GetTermDepositAccountDetails.logger;
import com.easynet.impl.JSONObjectImpl;
import com.easynet.impl.LoggerImpl;
import com.easynet.util.common;
import com.easynet.util.readXML;
import java.util.ArrayList;
import org.apache.ws.axis2.GetTermDepositDetailsStatementResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.soap.client.SoapFaultClientException;


@Component
public class GetTermDepositDetailsStatement {
    
    
    static Logger logger=LoggerFactory.getLogger(GetTermDepositDetailsStatement.class);
    
    @Autowired
    PropConfiguration propConfig;
    
    @Autowired
    SOAPConnector soapConnector;
    
    public String getTermDepositDetailsStatement(String reqData){
        
        String    ls_cbsCustomerId="";
        String    ls_accountNumber="";
        String    ls_fromDate="";
        String    ls_toDate="";
        String    ls_password="";
        String    ls_userName="";
        String    ls_responseCode="";
        String    ls_responseData="";
        String    ls_responseMessage="";
        String    ls_actualErrMsg="";
        String    ls_langResCodeMsg="";
        LoggerImpl     loggerImpl=null;
        ArrayList<MiniStatementTransationList> miniStatementTransactionList;
        
        
        try{
            
            loggerImpl=new LoggerImpl();
            
            loggerImpl.info(logger,"Preparing requset data and calling API.","IN:getTermDepositDetailsStatement");
            loggerImpl.generateProfiler("getTermDepositDetailsStatement");
            loggerImpl.startProfiler("Preparing request data");
            
            
            JSONObject reqJsonObj=new JSONObject(reqData);
            ls_cbsCustomerId=reqJsonObj.getString("CBSCUSTID");
            ls_accountNumber=reqJsonObj.getString("ACCTNUMBER");
            ls_fromDate=reqJsonObj.getString("FROMDATE");
            ls_toDate=reqJsonObj.getString("TODATE");
            
            if((ls_cbsCustomerId==null || "".equals(ls_cbsCustomerId)) || (ls_accountNumber==null || "".equals(ls_accountNumber)) || (ls_fromDate==null || "".equals(ls_fromDate))
                    || (ls_toDate==null || "".equals(ls_toDate))){
                
                ls_responseData = common.ofGetErrDataJsonArray("99",
						propConfig.getMessageOfResCode("commen.title.99", "Validation Failed."),
						propConfig.getMessageOfResCode("commen.invalid_req_data",""),		
						"Null values found in request data.", "Invalid Request.","", "R");
            }
            ls_userName = readXML.getXmlData("root>CUST_360>AUTHORIZATION>USER_NAME");
            ls_password = readXML.getXmlData("root>CUST_360>AUTHORIZATION>PASSWORD");
            
            //get the object factory object.
            city.xsd.ObjectFactory xsdObjectFactory=new ObjectFactory();
            //get the request object.
            GetTermDepositDetailsStatementRequest getTermDepositDetailsStatementReq=xsdObjectFactory.createGetTermDepositDetailsStatementRequest();
            
            //setting the values in the fields.
            getTermDepositDetailsStatementReq.setAccountNumber(xsdObjectFactory.createGetTermDepositDetailsStatementRequestAccountNumber(ls_accountNumber));
            getTermDepositDetailsStatementReq.setCbsCustomerID(xsdObjectFactory.createGetTermDepositDetailsStatementRequestCbsCustomerID(ls_cbsCustomerId));
            getTermDepositDetailsStatementReq.setFromDate(xsdObjectFactory.createGetTermDepositDetailsStatementRequestFromDate(ls_fromDate));
            getTermDepositDetailsStatementReq.setPassword(xsdObjectFactory.createGetTermDepositDetailsStatementRequestPassword(ls_password));
            getTermDepositDetailsStatementReq.setToDate(xsdObjectFactory.createGetTermDepositDetailsStatementRequestToDate(ls_toDate));
            getTermDepositDetailsStatementReq.setUsername(xsdObjectFactory.createGetTermDepositDetailsStatementRequestUsername(ls_userName));
            
            loggerImpl.debug(logger,"Json to xml conversion done.", "IN:getTermDepositDetailsStatement");
            
            //get the object factory object.
            org.apache.ws.axis2.ObjectFactory axis2ObjectFactory=new org.apache.ws.axis2.ObjectFactory();
            
            //wrapping the request object in the request.
            org.apache.ws.axis2.GetTermDepositDetailsStatement gettermdepositdetailsstatement=axis2ObjectFactory.createGetTermDepositDetailsStatement();
            gettermdepositdetailsstatement.setRequest(axis2ObjectFactory.createGetTermDepositDetailsStatementRequest(getTermDepositDetailsStatementReq));
            
            loggerImpl.debug(logger,"getTermDepositDetailsStatement API calling", "IN:getTermDepositDetailsStatement");
            loggerImpl.startProfiler("getTermDepositDetailsStatement API calling.");
            
            //get the response object.
            GetTermDepositDetailsStatementResponse getTermDepositDetailsStatementResponse=null;
            try{
                getTermDepositDetailsStatementResponse=(GetTermDepositDetailsStatementResponse) soapConnector.callWebService(gettermdepositdetailsstatement);
            }catch (SoapFaultClientException soapException) {
				ls_actualErrMsg = soapException.getFaultStringOrReason();
				ls_responseData = common.ofGetErrDataJsonArray("999", 
						propConfig.getMessageOfResCode("commen.title.999", "Alert."),
						propConfig.getMessageOfResCode("commen.exception.999","","(ENP170)"),
						ls_actualErrMsg, "Currently Service under maintenance so please try later (ENP170)", "0", "R");

				ls_actualErrMsg = common.ofGetTotalErrString(soapException, "");
				loggerImpl.error(logger,"SoapFaultClientException : " + ls_actualErrMsg,"EX:getTermDepositDetailsStatement");
				return ls_responseData;
			}
            
            loggerImpl.debug(logger,"getTermDepositDetailsStatement API called successfully.", "IN:getTermDepositDetailsStatement",getTermDepositDetailsStatementResponse);
            loggerImpl.startProfiler("preparing getTermDepositDetailsStatement API response data.");
            
            //get the response object.
            city.xsd.GetTermDepositDetailsStatementResponse xsd_getTermDepositDetailsStatementResponse=getTermDepositDetailsStatementResponse.getReturn().getValue();
            
            //getting all the data from the response.
            ls_responseCode=xsd_getTermDepositDetailsStatementResponse.getResponseCode().getValue();
            ls_responseMessage=xsd_getTermDepositDetailsStatementResponse.getResponseMessage().getValue();
            
            /*if response is 100 then success.
            "000"-not successful
            */
            
            if (ls_responseCode != null && "100".equals(ls_responseCode)){
                
                JSONObjectImpl getTermDepositDetailsJsonObj = new JSONObjectImpl();
                JSONObject responseJsonObject = new JSONObject();
                JSONArray getTermDepositDetailsJsonArray = new JSONArray();
                
                TermDepositDetailsStatementResponse termdepositdetails=xsd_getTermDepositDetailsStatementResponse.getResponseData().getValue();
                miniStatementTransactionList=(ArrayList<MiniStatementTransationList>) termdepositdetails.getTransactionList();
                
                getTermDepositDetailsJsonObj.put("ACCTHOLDERNAME", termdepositdetails.getAccountHolderName().getValue());
                getTermDepositDetailsJsonObj.put("ACCTNUMBER", termdepositdetails.getAccountNumber().getValue());
                getTermDepositDetailsJsonObj.put("ACCTSTATUS", termdepositdetails.getAccountStatus().getValue());
                getTermDepositDetailsJsonObj.put("ACCTTYPE", termdepositdetails.getAccountType().getValue());
                getTermDepositDetailsJsonObj.put("CURRENCYCODE", termdepositdetails.getCurrencyCode().getValue());
                getTermDepositDetailsJsonObj.put("INTERESTRATE", termdepositdetails.getInterestRate());
                getTermDepositDetailsJsonObj.put("OPENINGBAL", termdepositdetails.getOpeningBalance());
                getTermDepositDetailsJsonObj.put("TENOR", termdepositdetails.getTenor().getValue());
                getTermDepositDetailsJsonObj.put("TOTALBALANCE", termdepositdetails.getTotalBalance());
                getTermDepositDetailsJsonObj.put("TOTALCREDIT", termdepositdetails.getTotalCredit());
                getTermDepositDetailsJsonObj.put("TOTALDEBIT", termdepositdetails.getTotalDebit());
                
                
                for (MiniStatementTransationList miniStatementTransationList : miniStatementTransactionList) {
                    
                    getTermDepositDetailsJsonObj.put("AVAILBAL", miniStatementTransationList.getAvailableBalance());
                    getTermDepositDetailsJsonObj.put("DEPOSIT", miniStatementTransationList.getDeposit());
                    getTermDepositDetailsJsonObj.put("DESC", miniStatementTransationList.getDescription().getValue());
                    getTermDepositDetailsJsonObj.put("OUTSTANDINGBAL", miniStatementTransationList.getOutstandingBalance());
                    getTermDepositDetailsJsonObj.put("REFCHEQUE", miniStatementTransationList.getRefCheque().getValue());
                    getTermDepositDetailsJsonObj.put("TRANSDATE", miniStatementTransationList.getTransactionDate().getValue());
                    getTermDepositDetailsJsonObj.put("WITHDRAW", miniStatementTransationList.getWithdraw());
                }
                
                getTermDepositDetailsJsonArray.put(getTermDepositDetailsJsonObj);
                
                responseJsonObject.put("STATUS", "0");
                responseJsonObject.put("COLOR", "G");
                responseJsonObject.put("RESPONSE", getTermDepositDetailsJsonArray);
                responseJsonObject.put("MESSAGE", "");
                responseJsonObject.put("RESPONSECODE",ls_responseCode);
                responseJsonObject.put("RESPONSEMESSAGE",ls_responseMessage);
                ls_responseData = responseJsonObject.toString();
                
            }else { 

				ls_langResCodeMsg=propConfig.getResponseCode("getTermDepositDetailsStatement."+ls_responseCode);			
				ls_responseData = common.ofGetErrDataJsonArray(ls_langResCodeMsg,
						propConfig.getMessageOfResCode("commen.title."+ls_langResCodeMsg, "Alert."), 
						propConfig.getMessageOfResCode("getTermDepositDetailsStatement."+ls_responseCode,"","(ENP171)"),
						ls_responseMessage, "Currently Service under maintenance so please try later (ENP171).", ls_responseCode, "R");            		            		            	
			}
    }catch (Exception exception) {
			ls_actualErrMsg = common.ofGetTotalErrString(exception, "");            
			loggerImpl.error(logger,"Exception : " + ls_actualErrMsg,"IN:getTermDepositDetailsStatement");

			ls_responseData = common.ofGetErrDataJsonArray("999",
					propConfig.getMessageOfResCode("commen.title.999", "Alert."),
					propConfig.getMessageOfResCode("commen.exception.999","","(ENP172)"),
					exception.getMessage(),"Currently Service under maintenance so please try later (ENP172)","0", "R");			

		}finally {
			loggerImpl.stopAndPrintOptLogs(logger,"All API called successfully.","IN:getTermDepositDetailsStatement");
			loggerImpl.info(logger,"Response generated and send to client.", "IN:getTermDepositDetailsStatement");        	
		}
            
            
        
    return ls_responseData;
            
            
        
    }
}
