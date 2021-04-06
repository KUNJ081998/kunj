package com.easynet.CASA;

import city.xsd.CASAMinistatementResponse;
import city.xsd.GetCASAMiniStatementRequest;
import city.xsd.MiniStatementTransationList;
import city.xsd.ObjectFactory;
import com.easynet.configuration.PropConfiguration;
import com.easynet.configuration.SOAPConnector;
import com.easynet.impl.JSONObjectImpl;
import com.easynet.impl.LoggerImpl;
import com.easynet.util.common;
import com.easynet.util.readXML;
import java.util.ArrayList;
import org.apache.ws.axis2.GetCASAMiniStatementResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.soap.client.SoapFaultClientException;


@Component
public class GetCASAMiniStatement {
    
    static Logger logger=LoggerFactory.getLogger(GetCASAMiniStatement.class);
    
    @Autowired
    private SOAPConnector soapConnector;
    
    @Autowired
    PropConfiguration propConfig;
    
    public String getCASAMiniStatement(String reqData){
        
        String ls_accountNumber="";
        String ls_cbsCustomerId="";
        String ls_password="";
        String ls_userName="";
        String ls_responseCode="";
        String ls_responseData="";
        String ls_responseMessage="";
        String ls_actualErrMsg="";
        String ls_langResCodeMsg="";
        LoggerImpl loggerImpl=null;
        ArrayList<MiniStatementTransationList> miniStatementTransationList;
        
        try{
            
            loggerImpl=new LoggerImpl();
            
            loggerImpl.info(logger, "Preaparing request data and calling API", "IN:getCASAMiniStatement");
            loggerImpl.generateProfiler("getCASAMiniStatement");
            loggerImpl.startProfiler("Preparing request data.");
            
            JSONObject reqJsonObj=new JSONObject(reqData);
            ls_accountNumber=reqJsonObj.getString("ACCTNUMBER");
            ls_cbsCustomerId=reqJsonObj.getString("CBSCUSTID");
            
            if((ls_accountNumber==null || "".equals(ls_accountNumber)) || (ls_cbsCustomerId==null || "".equals(ls_cbsCustomerId))){
                
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
            GetCASAMiniStatementRequest getCASAMiniStatementReq=xsdObjectFactory.createGetCASAMiniStatementRequest();
            
            loggerImpl.debug(logger,"Json to xml conversion done.", "getCASAMiniStatement");
            
            //setting all the values in the request.
            getCASAMiniStatementReq.setAccountNumber(xsdObjectFactory.createGetCASAMiniStatementRequestAccountNumber(ls_accountNumber));
            getCASAMiniStatementReq.setCbsCustomerID(xsdObjectFactory.createGetCASAMiniStatementRequestCbsCustomerID(ls_cbsCustomerId));
            getCASAMiniStatementReq.setPassword(xsdObjectFactory.createGetCASAMiniStatementRequestPassword(ls_password));
            getCASAMiniStatementReq.setUsername(xsdObjectFactory.createGetCASAMiniStatementRequestUsername(ls_userName));
            
            //get the object factory object.
            org.apache.ws.axis2.ObjectFactory axis2ObjectFactory=new org.apache.ws.axis2.ObjectFactory();
            
            //wrapping the request object in  the request.
            org.apache.ws.axis2.GetCASAMiniStatement getcasaMiniStatement=axis2ObjectFactory.createGetCASAMiniStatement();
            getcasaMiniStatement.setRequest(axis2ObjectFactory.createGetCASAMiniStatementRequest(getCASAMiniStatementReq));
            
            loggerImpl.debug(logger,"getCASAMiniStatement API calling", "getCASAMiniStatement");
            loggerImpl.startProfiler("getCASAMiniStatement API calling.");
            
            //created response object.
            GetCASAMiniStatementResponse getCASAMiniStatementResp=null;
            try{
                getCASAMiniStatementResp=(GetCASAMiniStatementResponse) soapConnector.callWebService(getcasaMiniStatement);
            } catch (SoapFaultClientException soapException) {
				ls_actualErrMsg = soapException.getFaultStringOrReason();
				ls_responseData = common.ofGetErrDataJsonArray("999", 
						propConfig.getMessageOfResCode("commen.title.999", "Alert."),
						propConfig.getMessageOfResCode("commen.exception.999","","(ENP203)"),
						ls_actualErrMsg, "Currently Service under maintenance so please try later (ENP203)", "0", "R");

				ls_actualErrMsg = common.ofGetTotalErrString(soapException, "");
				loggerImpl.error(logger,"SoapFaultClientException : " + ls_actualErrMsg,"EX:getCASAMiniStatement");
				return ls_responseData;
			}
            
            loggerImpl.debug(logger,"getCASAMiniStatement API called successfully.", "getCASAMiniStatement",getCASAMiniStatementResp);
            loggerImpl.startProfiler("preparing getCASAMiniStatement API response data.");
            
            //get the response object.
            city.xsd.GetCASAMiniStatementResponse xsd_getCASAMiniStatementResp=getCASAMiniStatementResp.getReturn().getValue();
            
            //getting all the data from the response.
            ls_responseCode=xsd_getCASAMiniStatementResp.getResponseCode().getValue();
            ls_responseMessage=xsd_getCASAMiniStatementResp.getResponseMessage().getValue();
            
            
            /*if response is 100 then success.
	     *If response is 101 then no records found.
             */
            if (ls_responseCode != null && "100".equals(ls_responseCode)){
                
                JSONObjectImpl GetCASAMiniStatementJsonObj = new JSONObjectImpl();
                JSONObject responseJsonObject = new JSONObject();
                JSONArray GetCASAMiniStatementJsonArray = new JSONArray();
                JSONArray GetCASAMiniStmtArray = new JSONArray();
                
                CASAMinistatementResponse casaministatementresp=xsd_getCASAMiniStatementResp.getResponseData().getValue();
                
                GetCASAMiniStatementJsonObj.put("ACCTNUMBER", casaministatementresp.getAccountNumber().getValue());
                GetCASAMiniStatementJsonObj.put("ACCTSTATUS", casaministatementresp.getAccountStatus().getValue());
                GetCASAMiniStatementJsonObj.put("AVAILBAL", casaministatementresp.getAvailableBalance());
                GetCASAMiniStatementJsonObj.put("CLEARBAL", casaministatementresp.getClearBalance());
                GetCASAMiniStatementJsonObj.put("CURRENCYCODE", casaministatementresp.getCurrencyCode().getValue());
                GetCASAMiniStatementJsonObj.put("TOTALBAL", casaministatementresp.getTotalBalance());
                
                miniStatementTransationList=(ArrayList<MiniStatementTransationList>) casaministatementresp.getTransactionList();
//              GetCASAMiniStatementJsonObj.put("TRNLIST", casaministatementresp.getTransactionList());

                for (MiniStatementTransationList miniStatementTransationList1 : miniStatementTransationList) {
                    
                    JSONObjectImpl casaminiJsonObj=new JSONObjectImpl();
                    casaminiJsonObj.put("AVAILBALANCE", miniStatementTransationList1.getAvailableBalance());
                    casaminiJsonObj.put("DEPOSIT", miniStatementTransationList1.getDeposit());
                    casaminiJsonObj.put("DESCRIPTION", miniStatementTransationList1.getDescription().getValue());
                    casaminiJsonObj.put("OUTSTANDINGBAL", miniStatementTransationList1.getOutstandingBalance());
                    casaminiJsonObj.put("REFCHEQUE", miniStatementTransationList1.getRefCheque().getValue());
                    casaminiJsonObj.put("TRANSDATE", miniStatementTransationList1.getTransactionDate().getValue());
                    casaminiJsonObj.put("WITHDRAW", miniStatementTransationList1.getWithdraw());
                    GetCASAMiniStmtArray.put(casaminiJsonObj);
                     
                }
                GetCASAMiniStatementJsonObj.put("TRNLIST",GetCASAMiniStmtArray);
                GetCASAMiniStatementJsonArray.put(GetCASAMiniStatementJsonObj);
               
                
                
                /*
                 * If transaction happens and API is called successfully.
                 */
                responseJsonObject.put("STATUS", "0");
                responseJsonObject.put("COLOR", "G");
                responseJsonObject.put("RESPONSE", GetCASAMiniStatementJsonArray);
                responseJsonObject.put("RESPONSEMESSAGE", ls_responseMessage);
                responseJsonObject.put("RESPONSECODE", ls_responseCode);
                ls_responseData = responseJsonObject.toString();
            }else { 

				ls_langResCodeMsg=propConfig.getResponseCode("getCASAMiniStatement."+ls_responseCode);			
				ls_responseData = common.ofGetErrDataJsonArray(ls_langResCodeMsg,
						propConfig.getMessageOfResCode("commen.title."+ls_langResCodeMsg, "Alert."), 
						propConfig.getMessageOfResCode("getCASAMiniStatement."+ls_responseCode,"","(ENP204)"),
						ls_responseMessage, "Currently Service under maintenance so please try later (ENP204).", ls_responseCode, "R");            		            		            	
			}
            
        }catch (Exception exception) {
			ls_actualErrMsg = common.ofGetTotalErrString(exception, "");            
			loggerImpl.error(logger,"Exception : " + ls_actualErrMsg,"IN:getCASAMiniStatement");

			ls_responseData = common.ofGetErrDataJsonArray("999",
					propConfig.getMessageOfResCode("commen.title.999", "Alert."),
					propConfig.getMessageOfResCode("commen.exception.999","","(ENP205)"),
					exception.getMessage(),"Currently Service under maintenance so please try later (ENP205)","0", "R");			

		}finally {
        	loggerImpl.stopAndPrintOptLogs(logger,"All API called successfully.","getCASAMiniStatement");
        	loggerImpl.info(logger,"Response generated and send to client.", "getCASAMiniStatement");        	
        }
        return ls_responseData;
    }
}
