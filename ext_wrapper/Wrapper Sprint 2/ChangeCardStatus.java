package com.easynet.controller.ApiController.CardAPI;

import card.xsd.BlockCreditCardResponse;
import card.xsd.ChangeCardStatusRequest;
import card.xsd.ObjectFactory;
import com.easynet.configuration.PropConfiguration;
import com.easynet.configuration.SOAPConnector;
import com.easynet.impl.JSONObjectImpl;
import com.easynet.impl.LoggerImpl;
import com.easynet.util.common;
import static com.easynet.util.common.PrintErrLog;
import com.easynet.util.readXML;
import org.apache.ws.axis2.ChangeCardStatusResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.soap.client.SoapFaultClientException;

@Component
public class ChangeCardStatus {

    static Logger logger=LoggerFactory.getLogger(ChangeCardStatus.class);
    
    @Autowired
    PropConfiguration propConfig;
    
    
    @Autowired
    private SOAPConnector soapConnector;

    public String changeCardStatus(String reqData) {
        String ls_cardNoActual = "";
        String ls_cardStatus = "";
        String ls_changeReason = "";
        String ls_clientId = "";
        String ls_password = "";
        String ls_userName = "";
        String ls_responseCode = "";
        String ls_responseData = "";
        String ls_responseMessage = "";
        String ls_actualErrMsg = "";
        String ls_langResCodeMsg="";
        LoggerImpl loggerImpl=null;

        try {
            
            loggerImpl=new LoggerImpl();
            
            loggerImpl.info(logger, "Preaparing request data and calling API", "IN:changeCardStatus");
            loggerImpl.generateProfiler("changeCardStatus");
            loggerImpl.startProfiler("Preparing request data.");
            
            
            JSONObject reqJsonObj = new JSONObject(reqData);
            ls_cardNoActual = reqJsonObj.getString("CARDNOACTUAL");
            ls_cardStatus = reqJsonObj.getString("CARDSTATUS");
            ls_changeReason = reqJsonObj.getString("CHANGEREASON");
            ls_clientId = reqJsonObj.getString("CLIENTID");

            if ((ls_cardNoActual == null || "".equals(ls_cardNoActual)) || (ls_cardStatus == null || "".equals(ls_cardStatus)) || (ls_changeReason == null || "".equals(ls_changeReason))
                    || (ls_clientId == null || "".equals(ls_clientId))) {

                ls_responseData = common.ofGetErrDataJsonArray("99",
						propConfig.getMessageOfResCode("commen.title.99", "Validation Failed."),
						propConfig.getMessageOfResCode("commen.invalid_req_data",""),		
						"Null values found in request data.", "Invalid Request.","", "R");
            }
            ls_userName = readXML.getXmlData("root>CUST_360>AUTHORIZATION>USER_NAME");
            ls_password = readXML.getXmlData("root>CUST_360>AUTHORIZATION>PASSWORD");

            //get object factory object.
            card.xsd.ObjectFactory xsdObjectFactory = new ObjectFactory();
            //get object of ChangeCardStatusRequest.
            ChangeCardStatusRequest changeCardStatusRequest = xsdObjectFactory.createChangeCardStatusRequest();
            
            loggerImpl.debug(logger,"Json to xml conversion done.", "changeCardStatus");

            //setting all the values in the request.
            changeCardStatusRequest.setCardNoActual(xsdObjectFactory.createChangeCardStatusRequestCardNoActual(ls_cardNoActual));
            changeCardStatusRequest.setCardStatus(xsdObjectFactory.createChangeCardStatusRequestCardStatus(Integer.parseInt(ls_cardStatus)));
            changeCardStatusRequest.setChangeReason(xsdObjectFactory.createChangeCardStatusRequestChangeReason(ls_changeReason));
            changeCardStatusRequest.setClientId(xsdObjectFactory.createChangeCardStatusRequestClientId(ls_clientId));
            changeCardStatusRequest.setPassword(xsdObjectFactory.createChangeCardStatusRequestPassword(ls_password));
            changeCardStatusRequest.setUsername(xsdObjectFactory.createChangeCardStatusRequestUsername(ls_userName));

            //get object of object factory .
            org.apache.ws.axis2.ObjectFactory axis2ObjectFactory = new org.apache.ws.axis2.ObjectFactory();
            //wrapping the request in ChangeCardStatus.
            org.apache.ws.axis2.ChangeCardStatus changeCardStatusReq = axis2ObjectFactory.createChangeCardStatus();
            changeCardStatusReq.setRequest(axis2ObjectFactory.createChangeCardStatusRequest(changeCardStatusRequest));
            
            loggerImpl.debug(logger,"changeCardStatus API calling", "changeCardStatus");
            loggerImpl.startProfiler("changeCardStatus API calling.");

            //get the response object.
            ChangeCardStatusResponse changeCardStatusResponse = null;
            try {
                changeCardStatusResponse = (ChangeCardStatusResponse) soapConnector.callWebService(changeCardStatusReq);
            } catch (SoapFaultClientException soapException) {
				ls_actualErrMsg = soapException.getFaultStringOrReason();
				ls_responseData = common.ofGetErrDataJsonArray("999", 
						propConfig.getMessageOfResCode("commen.title.999", "Alert."),
						propConfig.getMessageOfResCode("commen.exception.999","","(ENP242)"),
						ls_actualErrMsg, "Currently Service under maintenance so please try later (ENP242)", "0", "R");

				ls_actualErrMsg = common.ofGetTotalErrString(soapException, "");
				loggerImpl.error(logger,"SoapFaultClientException : " + ls_actualErrMsg,"EX:changeCardStatus");
				return ls_responseData;
			}
            
            loggerImpl.debug(logger,"changeCardStatus API called successfully.", "changeCardStatus",changeCardStatusResponse);
            loggerImpl.startProfiler("preparing changeCardStatus API response data.");

            //get the response object
            BlockCreditCardResponse xsd_blockCreditCardResponse = changeCardStatusResponse.getReturn().getValue();

            //getting all the data from the reponse.
            ls_responseCode = xsd_blockCreditCardResponse.getResponseCode().getValue();
            ls_responseData = xsd_blockCreditCardResponse.getResponseData().getValue();
            ls_responseMessage = xsd_blockCreditCardResponse.getResponseMessage().getValue();

            /*
             * if response is 100 then success.
	     * If response is 101 then no records found.
             */
            if (ls_responseCode != null && "100".equals(ls_responseCode)) {
                JSONObjectImpl ChangeCardStatusJsonObj = new JSONObjectImpl();
                JSONObject responseJsonObject = new JSONObject();
                JSONArray ChangeCardStatusJsonArray = new JSONArray();

                ChangeCardStatusJsonObj.put("RESPONSECODE", ls_responseCode);
                ChangeCardStatusJsonObj.put("RESPONSEDATA", ls_responseData);
                ChangeCardStatusJsonObj.put("RESPONSEMESSAGE", ls_responseMessage);
                ChangeCardStatusJsonArray.put(ChangeCardStatusJsonObj);

                /*
	         * If transaction happens and API is called successfully.
                 */
                responseJsonObject.put("STATUS", "0");
                responseJsonObject.put("COLOR", "G");
                responseJsonObject.put("RESPONSE", ChangeCardStatusJsonArray);
                responseJsonObject.put("MESSAGE", "");
                ls_responseData = responseJsonObject.toString();
            }else { 

				ls_langResCodeMsg=propConfig.getResponseCode("changeCardStatus."+ls_responseCode);			
				ls_responseData = common.ofGetErrDataJsonArray(ls_langResCodeMsg,
						propConfig.getMessageOfResCode("commen.title."+ls_langResCodeMsg, "Alert."), 
						propConfig.getMessageOfResCode("changeCardStatus."+ls_responseCode,"","(ENP243)"),
						ls_responseMessage, "Currently Service under maintenance so please try later (ENP243).", ls_responseCode, "R");            		            		            	
			}
        }catch (Exception exception) {
			ls_actualErrMsg = common.ofGetTotalErrString(exception, "");            
			loggerImpl.error(logger,"Exception : " + ls_actualErrMsg,"IN:changeCardStatus");

			ls_responseData = common.ofGetErrDataJsonArray("999",
					propConfig.getMessageOfResCode("commen.title.999", "Alert."),
					propConfig.getMessageOfResCode("commen.exception.999","","(ENP244)"),
					exception.getMessage(),"Currently Service under maintenance so please try later (ENP244)","0", "R");			

		}finally {
        	loggerImpl.stopAndPrintOptLogs(logger,"All API called successfully.","changeCardStatus");
        	loggerImpl.info(logger,"Response generated and send to client.", "changeCardStatus");        	
        }
        return ls_responseData;
    }
}
