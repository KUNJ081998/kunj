package com.easynet.controller.ApiController;

import city.xsd.GetTermDepositAccountDetailsRequest;
import city.xsd.ObjectFactory;
import city.xsd.TermDepositAccountDetailsResponse;
import com.easynet.configuration.PropConfiguration;
import com.easynet.configuration.SOAPConnector;
import static com.easynet.controller.ApiController.GetCASADetailsStatement.logger;
import com.easynet.impl.JSONObjectImpl;
import com.easynet.impl.LoggerImpl;
import com.easynet.util.common;
import com.easynet.util.readXML;
import org.apache.ws.axis2.GetTermDepositAccountDetailsResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.soap.client.SoapFaultClientException;


@Component
public class GetTermDepositAccountDetails {
    
    static Logger logger=LoggerFactory.getLogger(GetTermDepositAccountDetails.class);
    
    @Autowired
    PropConfiguration propConfig;
    
    @Autowired
    SOAPConnector soapConnector;
    
    public String getTermDepositAccountDetails(String reqData){
        
        String   ls_cbsCustomerId="";
        String   ls_accountNumber="";
        String   ls_password="";
        String   ls_userName="";
        String   ls_responseCode="";
        String   ls_responseData="";
        String   ls_responseMessage="";
        String   ls_actualErrMsg="";
        String   ls_langResCodeMsg="";
        LoggerImpl    loggerImpl=null;
        
        try{
            
            loggerImpl=new LoggerImpl();
            
            
            loggerImpl.info(logger,"Preparing requset data and calling API.","IN:getTermDepositAccountDetails");
            loggerImpl.generateProfiler("getTermDepositAccountDetails");
            loggerImpl.startProfiler("Preparing request data");
        
        
            JSONObject reqJsonObj=new JSONObject(reqData);
            ls_cbsCustomerId=reqJsonObj.getString("CBSCUSTID");
            ls_accountNumber=reqJsonObj.getString("ACCTNUMBER");
            
            
            if((ls_cbsCustomerId==null || "".equals(ls_cbsCustomerId)) || (ls_accountNumber==null || "".equals(ls_accountNumber))){
                
                ls_responseData = common.ofGetErrDataJsonArray("99",
						propConfig.getMessageOfResCode("commen.title.99", "Validation Failed."),
						propConfig.getMessageOfResCode("commen.invalid_req_data",""),		
						"Null values found in request data.", "Invalid Request.","", "R");
            }
            ls_userName = readXML.getXmlData("root>CUST_360>AUTHORIZATION>USER_NAME");
            ls_password = readXML.getXmlData("root>CUST_360>AUTHORIZATION>PASSWORD");
            
            //get the object factory object.
            city.xsd.ObjectFactory xsdObjectFactory=new ObjectFactory();
            GetTermDepositAccountDetailsRequest getTermAccountDetailsRequest=xsdObjectFactory.createGetTermDepositAccountDetailsRequest();
            
            loggerImpl.debug(logger,"Json to xml conversion done.", "IN:getTermDepositAccountDetails");
            
            //setting the values in the request.
            getTermAccountDetailsRequest.setAccountNumber(xsdObjectFactory.createGetTermDepositAccountDetailsRequestAccountNumber(ls_accountNumber));
            getTermAccountDetailsRequest.setCbsCustomerID(xsdObjectFactory.createGetTermDepositAccountDetailsRequestCbsCustomerID(ls_cbsCustomerId));
            getTermAccountDetailsRequest.setPassword(xsdObjectFactory.createGetTermDepositAccountDetailsRequestPassword(ls_password));
            getTermAccountDetailsRequest.setUsername(xsdObjectFactory.createGetTermDepositAccountDetailsRequestUsername(ls_userName));
            
            
            //get the object factory object.
            org.apache.ws.axis2.ObjectFactory axis2ObjectFactory=new org.apache.ws.axis2.ObjectFactory();
            
            //wrpping the request object in the request.
            org.apache.ws.axis2.GetTermDepositAccountDetails gettermdepositaccountdetails=axis2ObjectFactory.createGetTermDepositAccountDetails();
            gettermdepositaccountdetails.setRequest(axis2ObjectFactory.createGetTermDepositAccountDetailsRequest(getTermAccountDetailsRequest));
            
            loggerImpl.debug(logger,"getTermDepositAccountDetails API calling", "IN:getTermDepositAccountDetails");
            loggerImpl.startProfiler("getTermDepositAccountDetails API calling.");
            
            
            //get the response object
            GetTermDepositAccountDetailsResponse getTermDepositAccountDetailsResponse=null;
            try{
                getTermDepositAccountDetailsResponse=(GetTermDepositAccountDetailsResponse) soapConnector.callWebService(gettermdepositaccountdetails);
            }catch (SoapFaultClientException soapException) {
				ls_actualErrMsg = soapException.getFaultStringOrReason();
				ls_responseData = common.ofGetErrDataJsonArray("999", 
						propConfig.getMessageOfResCode("commen.title.999", "Alert."),
						propConfig.getMessageOfResCode("commen.exception.999","","(ENP164)"),
						ls_actualErrMsg, "Currently Service under maintenance so please try later (ENP164)", "0", "R");

				ls_actualErrMsg = common.ofGetTotalErrString(soapException, "");
				loggerImpl.error(logger,"SoapFaultClientException : " + ls_actualErrMsg,"EX:getTermDepositAccountDetails");
				return ls_responseData;
			}
            
            loggerImpl.debug(logger,"getTermDepositAccountDetails API called successfully.", "IN:getTermDepositAccountDetails",getTermDepositAccountDetailsResponse);
            loggerImpl.startProfiler("preparing getTermDepositAccountDetails API response data.");
            
            //get the response object.
            city.xsd.GetTermDepositAccountDetailsResponse xsd_getTermDepositAccountDetailsResponse=getTermDepositAccountDetailsResponse.getReturn().getValue();
            
            //getting all the values from the response.
            ls_responseCode=xsd_getTermDepositAccountDetailsResponse.getResponseCode().getValue();
            ls_responseMessage=xsd_getTermDepositAccountDetailsResponse.getResponseMessage().getValue();
            
            /*if response is 100 then success.
            "000"-not successful
            */
            
            if (ls_responseCode != null && "100".equals(ls_responseCode)){
                
                JSONObjectImpl getTermDepositAccountsJsonObj = new JSONObjectImpl();
                JSONObject responseJsonObject = new JSONObject();
                JSONArray getTermDepositAccountsJsonArray = new JSONArray();
                
                TermDepositAccountDetailsResponse termDepositAcoountDetailsResp=xsd_getTermDepositAccountDetailsResponse.getResponseData().getValue();
                
                getTermDepositAccountsJsonObj.put("BRANCHNAME", termDepositAcoountDetailsResp.getBranchName().getValue());
                getTermDepositAccountsJsonObj.put("CURRENCYCODE", termDepositAcoountDetailsResp.getCurrencyCode().getValue());
                getTermDepositAccountsJsonObj.put("DEPOSITAMT", termDepositAcoountDetailsResp.getDepositAmount().getValue());
                getTermDepositAccountsJsonObj.put("DEPOSITDATE", termDepositAcoountDetailsResp.getDepositDate().getValue());
                getTermDepositAccountsJsonObj.put("INTERESTRATE", termDepositAcoountDetailsResp.getInterestRate().getValue());
                getTermDepositAccountsJsonObj.put("MATURITYAMT", termDepositAcoountDetailsResp.getMaturityAmount().getValue());
                getTermDepositAccountsJsonObj.put("MATURITYDATE", termDepositAcoountDetailsResp.getMaturityDate().getValue());
                
                getTermDepositAccountsJsonArray.put(getTermDepositAccountsJsonObj);
                
                responseJsonObject.put("STATUS", "0");
                responseJsonObject.put("COLOR", "G");
                responseJsonObject.put("RESPONSE", getTermDepositAccountsJsonArray);
                responseJsonObject.put("MESSAGE", "");
                responseJsonObject.put("RESPONSECODE",ls_responseCode);
                responseJsonObject.put("RESPONSEMESSAGE",ls_responseMessage);
                ls_responseData = responseJsonObject.toString();
            }else { 

				ls_langResCodeMsg=propConfig.getResponseCode("getTermDepositAccountDetails."+ls_responseCode);			
				ls_responseData = common.ofGetErrDataJsonArray(ls_langResCodeMsg,
						propConfig.getMessageOfResCode("commen.title."+ls_langResCodeMsg, "Alert."), 
						propConfig.getMessageOfResCode("getTermDepositAccountDetails."+ls_responseCode,"","(ENP165)"),
						ls_responseMessage, "Currently Service under maintenance so please try later (ENP165).", ls_responseCode, "R");            		            		            	
			}
    }catch (Exception exception) {
			ls_actualErrMsg = common.ofGetTotalErrString(exception, "");            
			loggerImpl.error(logger,"Exception : " + ls_actualErrMsg,"IN:getTermDepositAccountDetails");

			ls_responseData = common.ofGetErrDataJsonArray("999",
					propConfig.getMessageOfResCode("commen.title.999", "Alert."),
					propConfig.getMessageOfResCode("commen.exception.999","","(ENP166)"),
					exception.getMessage(),"Currently Service under maintenance so please try later (ENP166)","0", "R");			

		}finally {
			loggerImpl.stopAndPrintOptLogs(logger,"All API called successfully.","IN:getTermDepositAccountDetails");
			loggerImpl.info(logger,"Response generated and send to client.", "IN:getTermDepositAccountDetails");        	
		}
            
            
        
    return ls_responseData;
        
    }
}
