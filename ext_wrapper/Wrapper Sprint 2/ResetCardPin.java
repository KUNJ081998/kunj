package com.easynet.controller.ApiController.CardAPI;

import card.xsd.ObjectFactory;
import card.xsd.ResetCardPINRequest;
import card.xsd.ResetCardPINResponse;
import com.easynet.configuration.PropConfiguration;
import com.easynet.configuration.SOAPConnector;
import com.easynet.impl.JSONObjectImpl;
import com.easynet.impl.LoggerImpl;
import com.easynet.util.common;
import static com.easynet.util.common.PrintErrLog;
import com.easynet.util.readXML;
import org.apache.ws.axis2.ResetCardPIN;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.soap.client.SoapFaultClientException;

@Component
public class ResetCardPin {

    static Logger logger=LoggerFactory.getLogger(ResetCardPin.class);
    
    @Autowired
    PropConfiguration propConfig;
    
    
    @Autowired
    private SOAPConnector soapConnector;

    public String resetCardPin(String reqData) {
        String ls_cardNoActual = "";
        String ls_changeReason = "";
        String ls_clearPIN = "";
        String ls_password = "";
        String ls_userName = "";
        String ls_responseCode = "";
        String ls_responseMessage = "";
        String ls_responseData = "";
        String ls_actualErrMsg = "";
        String ls_langResCodeMsg="";
        LoggerImpl loggerImpl=null;

        try {
            loggerImpl=new LoggerImpl();
            
            loggerImpl.info(logger, "Preparing requset data and calling API.", "resetCardPin");
            loggerImpl.generateProfiler("resetCardPin");
            loggerImpl.startProfiler("preparing request data.");
            
            JSONObject reqJsonObj = new JSONObject(reqData);
            ls_cardNoActual = reqJsonObj.getString("CARDNOACTUAL");
            ls_changeReason = reqJsonObj.getString("CHANGEREASON");
            ls_clearPIN = reqJsonObj.getString("CLEARPIN");

            if ((ls_cardNoActual == null || "".equals(ls_cardNoActual)) || (ls_changeReason == null || "".equals(ls_changeReason)) || (ls_clearPIN == null || "".equals(ls_clearPIN))) {

                ls_responseData = common.ofGetErrDataJsonArray("99",
						propConfig.getMessageOfResCode("commen.title.99", "Validation Failed."),
						propConfig.getMessageOfResCode("commen.invalid_req_data",""),		
						"Null values found in request data.", "Invalid Request.","", "R");
            }

            ls_userName = readXML.getXmlData("root>CUST_360>AUTHORIZATION>USER_NAME");
            ls_password = readXML.getXmlData("root>CUST_360>AUTHORIZATION>PASSWORD");

            //creating object factory object of card.xsd.
            card.xsd.ObjectFactory xsdObjectFactory = new ObjectFactory();
            //creating request object of ResetCardPin.
            ResetCardPINRequest resetCardPinRequest = xsdObjectFactory.createResetCardPINRequest();
            
            loggerImpl.debug(logger,"Json to xml conversion done.", "resetCardPin");

            //setting all the values in the request
            resetCardPinRequest.setCardNoActual(xsdObjectFactory.createResetCardPINRequestCardNoActual(ls_cardNoActual));
            resetCardPinRequest.setChangeReason(xsdObjectFactory.createResetCardPINRequestChangeReason(ls_changeReason));
            resetCardPinRequest.setClearPIN(xsdObjectFactory.createResetCardPINRequestClearPIN(ls_clearPIN));
            resetCardPinRequest.setPassword(xsdObjectFactory.createResetCardPINRequestPassword(ls_password));
            resetCardPinRequest.setUsername(xsdObjectFactory.createResetCardPINRequestUsername(ls_userName));

            //creating object of object factory.
            org.apache.ws.axis2.ObjectFactory axis2ObjectFactory = new org.apache.ws.axis2.ObjectFactory();
            //wrapping the request in Reset card pin of ws.axis2.
            ResetCardPIN resetCardPIN = axis2ObjectFactory.createResetCardPIN();
            resetCardPIN.setRequest(axis2ObjectFactory.createResetCardPINRequest(resetCardPinRequest));
            
            loggerImpl.debug(logger,"resetCardPin API calling", "resetCardPin");
            loggerImpl.startProfiler("resetCardPin API calling.");

            //created response object of ResetCardPinResponse.
            org.apache.ws.axis2.ResetCardPINResponse resetCardPinResponse = null;
            try {
                resetCardPinResponse=(org.apache.ws.axis2.ResetCardPINResponse) soapConnector.callWebService(resetCardPIN);
            } catch (SoapFaultClientException soapException) {
				ls_actualErrMsg = soapException.getFaultStringOrReason();
				ls_responseData = common.ofGetErrDataJsonArray("999", 
						propConfig.getMessageOfResCode("commen.title.999", "Alert."),
						propConfig.getMessageOfResCode("commen.exception.999","","(ENP263)"),
						ls_actualErrMsg, "Currently Service under maintenance so please try later (ENP263)", "0", "R");

				ls_actualErrMsg = common.ofGetTotalErrString(soapException, "");
				loggerImpl.error(logger,"SoapFaultClientException : " + ls_actualErrMsg,"EX:resetCardPin");
				return ls_responseData;
			}
            
            loggerImpl.debug(logger,"resetCardPin API called successfully.", "resetCardPin",resetCardPinResponse);
            loggerImpl.startProfiler("preparing resetCardPin API response data.");
            
            //created resetCardPINResponse object of card.xsd.
            ResetCardPINResponse xsd_resetCardPinResponse=resetCardPinResponse.getReturn().getValue();
            
            //getting all the values from response data.
            ls_responseCode=xsd_resetCardPinResponse.getResponseCode().getValue();
            ls_responseMessage=xsd_resetCardPinResponse.getResponseMessage().getValue();
            
             /*
             * if response is 100 then success.
	     *If response is 101 then no records found.
             */
            if (ls_responseCode != null && "100".equals(ls_responseCode)) {

                JSONObjectImpl ResetCardPINJsonObj = new JSONObjectImpl();
                JSONObject responseJsonObject = new JSONObject();
                JSONArray ResetCardPINJsonArray = new JSONArray();
                
                ResetCardPINJsonObj.put("RESPONSECODE", ls_responseCode);
                ResetCardPINJsonObj.put("RESPONSEMESSAGE", ls_responseMessage);
                ResetCardPINJsonArray.put(ResetCardPINJsonObj);
                
                 /* If response is 100 and API is called successfully. */
                responseJsonObject.put("STATUS", "0");
                responseJsonObject.put("COLOR", "G");
                responseJsonObject.put("RESPONSE", ResetCardPINJsonArray);
                responseJsonObject.put("MESSAGE", "");
                ls_responseData = responseJsonObject.toString();


        }else { 

				ls_langResCodeMsg=propConfig.getResponseCode("resetCardPin."+ls_responseCode);			
				ls_responseData = common.ofGetErrDataJsonArray(ls_langResCodeMsg,
						propConfig.getMessageOfResCode("commen.title."+ls_langResCodeMsg, "Alert."), 
						propConfig.getMessageOfResCode("resetCardPin."+ls_responseCode,"","(ENP264)"),
						ls_responseMessage, "Currently Service under maintenance so please try later (ENP264).", ls_responseCode, "R");            		            		            	
			}
        } catch (Exception exception) {
			ls_actualErrMsg = common.ofGetTotalErrString(exception, "");            
			loggerImpl.error(logger,"Exception : " + ls_actualErrMsg,"IN:resetCardPin");

			ls_responseData = common.ofGetErrDataJsonArray("999",
					propConfig.getMessageOfResCode("commen.title.999", "Alert."),
					propConfig.getMessageOfResCode("commen.exception.999","","(ENP265)"),
					exception.getMessage(),"Currently Service under maintenance so please try later (ENP265)","0", "R");			

		}finally {
        	loggerImpl.stopAndPrintOptLogs(logger,"All API called successfully.","resetCardPin");
        	loggerImpl.info(logger,"Response generated and send to client.", "resetCardPin");        	
        }
        return ls_responseData;
}
}