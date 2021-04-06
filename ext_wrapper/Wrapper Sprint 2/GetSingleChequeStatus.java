package com.easynet.controller.ApiController;

import city.xsd.GetSingleChequeStatusRequest;
import city.xsd.ObjectFactory;
import city.xsd.SingleChequeStatusResponse;
import com.easynet.configuration.PropConfiguration;
import com.easynet.configuration.SOAPConnector;
import static com.easynet.controller.ApiController.GetCASADetailsStatement.logger;
import com.easynet.impl.JSONObjectImpl;
import com.easynet.impl.LoggerImpl;
import com.easynet.util.common;
import com.easynet.util.readXML;
import org.apache.ws.axis2.GetSingleChequeStatusResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.soap.client.SoapFaultClientException;

@Component
public class GetSingleChequeStatus {
    
    static Logger logger=LoggerFactory.getLogger(GetSingleChequeStatus.class);
    
    @Autowired
    private SOAPConnector soapConnector;
    
    @Autowired
    PropConfiguration propConfig;
    
    public String getSingleChequeStatus(String reqData){
        
        String ls_accountNumber="";
        String ls_chequeNumber="";
        String ls_userName="";
        String ls_password="";
        String ls_responseCode="";
        String ls_responseData="";
        String ls_responseMessage="";
        String ls_actualErrMsg="";
        String ls_langResCodeMsg="";
        LoggerImpl loggerImpl=null;
        
        
        try{
            loggerImpl=new LoggerImpl();
        
            loggerImpl.info(logger,"Preparing requset data and calling API.","IN:getSingleChequeStatus");
            loggerImpl.generateProfiler("getSingleChequeStatus");
            loggerImpl.startProfiler("Preparing request data");
            
            JSONObjectImpl reqJsonObj=new JSONObjectImpl(reqData);
            ls_accountNumber=reqJsonObj.getString("ACCTNUMBER");
            ls_chequeNumber=reqJsonObj.getString("CHEQUENUMBER");
            
            
            if((ls_accountNumber==null || "".equals(ls_accountNumber)) || (ls_chequeNumber==null || "".equals(ls_chequeNumber))){
                
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
            GetSingleChequeStatusRequest getSingleChequeStatusReq=xsdObjectFactory.createGetSingleChequeStatusRequest();
            
            loggerImpl.debug(logger,"Json to xml conversion done.", "IN:getSingleChequeStatus");
            
            //setting the values in the request.
            getSingleChequeStatusReq.setAccountNumber(xsdObjectFactory.createGetSingleChequeStatusRequestAccountNumber(ls_accountNumber));
            getSingleChequeStatusReq.setChequeNumber(xsdObjectFactory.createGetSingleChequeStatusRequestChequeNumber(ls_chequeNumber));
            getSingleChequeStatusReq.setPassword(xsdObjectFactory.createGetSingleChequeStatusRequestPassword(ls_password));
            getSingleChequeStatusReq.setUsername(xsdObjectFactory.createGetSingleChequeStatusRequestUsername(ls_userName));
            
            //get the object factory object.
            org.apache.ws.axis2.ObjectFactory axis2ObjectFactory=new org.apache.ws.axis2.ObjectFactory();
            
            //wrapping the request object in the request.
            org.apache.ws.axis2.GetSingleChequeStatus getsingleChequeStatus=axis2ObjectFactory.createGetSingleChequeStatus();
            getsingleChequeStatus.setRequest(axis2ObjectFactory.createGetSingleChequeStatusRequest(getSingleChequeStatusReq));
            
            loggerImpl.debug(logger,"getSingleChequeStatus API calling", "IN:getSingleChequeStatus");
            loggerImpl.startProfiler("getSingleChequeStatus API calling.");
            
            //get the response object.
            GetSingleChequeStatusResponse getSingleChequeStatusResponse=null;
            try{
                getSingleChequeStatusResponse=(GetSingleChequeStatusResponse) soapConnector.callWebService(getsingleChequeStatus);
            }catch (SoapFaultClientException soapException) {
				ls_actualErrMsg = soapException.getFaultStringOrReason();
				ls_responseData = common.ofGetErrDataJsonArray("999", 
						propConfig.getMessageOfResCode("commen.title.999", "Alert."),
						propConfig.getMessageOfResCode("commen.exception.999","","(ENP206)"),
						ls_actualErrMsg, "Currently Service under maintenance so please try later (ENP206)", "0", "R");

				ls_actualErrMsg = common.ofGetTotalErrString(soapException, "");
				loggerImpl.error(logger,"SoapFaultClientException : " + ls_actualErrMsg,"EX:getSingleChequeStatus");
				return ls_responseData;
			}
            loggerImpl.debug(logger,"getSingleChequeStatus API called successfully.", "IN:getSingleChequeStatus",getSingleChequeStatusResponse);
            loggerImpl.startProfiler("preparing getSingleChequeStatus API response data.");
            
            //get the response object.
            city.xsd.GetSingleChequeStatusResponse xsd_getSingleChequeStatusResponse=getSingleChequeStatusResponse.getReturn().getValue();
            
            //getting all the data from the values.
            ls_responseCode=xsd_getSingleChequeStatusResponse.getResponseCode().getValue();
            ls_responseMessage=xsd_getSingleChequeStatusResponse.getResponseMessage().getValue();
            
            /*if response is 100 then success.
            "000"-not successful
            */
        
        if (ls_responseCode != null && "100".equals(ls_responseCode)){
            
            JSONObjectImpl getSingleChequeStatusJsonObj = new JSONObjectImpl();
	    JSONObject responseJsonObject = new JSONObject();
            JSONArray getSingleChequeStatusJsonArray = new JSONArray();
            
            SingleChequeStatusResponse singleChequeStatusResp=xsd_getSingleChequeStatusResponse.getResponseData().getValue();
            
            getSingleChequeStatusJsonObj.put("ACCTNUMBER", singleChequeStatusResp.getAccountNumber().getValue());
            getSingleChequeStatusJsonObj.put("AMOUNT", singleChequeStatusResp.getAmount().getValue());
            getSingleChequeStatusJsonObj.put("CHEQUENUMBER", singleChequeStatusResp.getChequeNumber().getValue());
            getSingleChequeStatusJsonObj.put("CHEQUESTATUS", singleChequeStatusResp.getChequeStatus().getValue());
            getSingleChequeStatusJsonObj.put("DEBIT", singleChequeStatusResp.getDebit().getValue());
            getSingleChequeStatusJsonObj.put("PARTICULARS", singleChequeStatusResp.getParticulars().getValue());
            getSingleChequeStatusJsonObj.put("POSTDATE", singleChequeStatusResp.getPostDate().getValue());
            
            getSingleChequeStatusJsonArray.put(getSingleChequeStatusJsonObj);
            
            responseJsonObject.put("STATUS", "0");
            responseJsonObject.put("COLOR", "G");
            responseJsonObject.put("RESPONSE", getSingleChequeStatusJsonArray);
            responseJsonObject.put("MESSAGE", "");
            responseJsonObject.put("RESPONSECODE",ls_responseCode);
            responseJsonObject.put("RESPONSEMESSAGE",ls_responseMessage);
            ls_responseData = responseJsonObject.toString();
            
        }else { 

				ls_langResCodeMsg=propConfig.getResponseCode("getSingleChequeStatus."+ls_responseCode);			
				ls_responseData = common.ofGetErrDataJsonArray(ls_langResCodeMsg,
						propConfig.getMessageOfResCode("commen.title."+ls_langResCodeMsg, "Alert."), 
						propConfig.getMessageOfResCode("getSingleChequeStatus."+ls_responseCode,"","(ENP207)"),
						ls_responseMessage, "Currently Service under maintenance so please try later (ENP207).", ls_responseCode, "R");            		            		            	
			}
        }catch (Exception exception) {
			ls_actualErrMsg = common.ofGetTotalErrString(exception, "");            
			loggerImpl.error(logger,"Exception : " + ls_actualErrMsg,"IN:getSingleChequeStatus");

			ls_responseData = common.ofGetErrDataJsonArray("999",
					propConfig.getMessageOfResCode("commen.title.999", "Alert."),
					propConfig.getMessageOfResCode("commen.exception.999","","(ENP208)"),
					exception.getMessage(),"Currently Service under maintenance so please try later (ENP208)","0", "R");			

		}finally {
			loggerImpl.stopAndPrintOptLogs(logger,"All API called successfully.","IN:getSingleChequeStatus");
			loggerImpl.info(logger,"Response generated and send to client.", "IN:getSingleChequeStatus");        	
		}
        return ls_responseData;
    }
}
