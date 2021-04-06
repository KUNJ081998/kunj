package com.easynet.controller.ApiController.CardAPI;

import card.xsd.GetCreditLastStatementRequest;
import card.xsd.GetCreditLastStatementResponse;
import city.xsd.CreditCardDetailsResponse;
import com.easynet.configuration.PropConfiguration;
import com.easynet.configuration.SOAPConnector;
import com.easynet.impl.JSONObjectImpl;
import com.easynet.impl.LoggerImpl;
import com.easynet.util.common;
import static com.easynet.util.common.PrintErrLog;
import com.easynet.util.readXML;
import org.apache.ws.axis2.GetCreditLastStatementDetails;
import org.apache.ws.axis2.GetCreditLastStatementDetailsResponse;
import org.json.JSONArray;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.soap.client.SoapFaultClientException;

@Component
public class getCreditLastStatementDetails {

    static Logger logger=LoggerFactory.getLogger(getCreditLastStatementDetails.class);
    
    @Autowired
    PropConfiguration propConfig;
    
    
    @Autowired
    private SOAPConnector soapConnector;

    public String GetCreditLastStatementDetails(String reqData) {
        String ls_cardNoActual="";
        String ls_currencyCode="";
        String ls_password="";
        String ls_userName="";
        String ls_responseData="";
        String ls_responseMessage="";
        String ls_responseCode="";
        String ls_actualErrMsg="";
        String ls_langResCodeMsg="";
        LoggerImpl loggerImpl=null;
        

        try {
            
            loggerImpl=new LoggerImpl();
            
            loggerImpl.info(logger, "Preparing requset data and calling API.", "IN:GetCreditLastStatementDetails");
            loggerImpl.generateProfiler("GetCreditLastStatementDetails");
            loggerImpl.startProfiler("preparing request data.");
            
            JSONObject reqJsonObj = new JSONObject(reqData);
            ls_cardNoActual = reqJsonObj.getString("CARDNOACTUAL");
            ls_currencyCode = reqJsonObj.getString("CURRENCYCODE");

            if ((ls_cardNoActual == null || "".equals(ls_cardNoActual)) || (ls_currencyCode == null || "".equals(ls_currencyCode))) {

                ls_responseData = common.ofGetErrDataJsonArray("99",
						propConfig.getMessageOfResCode("commen.title.99", "Validation Failed."),
						propConfig.getMessageOfResCode("commen.invalid_req_data",""),		
						"Null values found in request data.", "Invalid Request.","", "R");
            }
            ls_userName = readXML.getXmlData("root>CUST_360>AUTHORIZATION>USER_NAME");
            ls_password = readXML.getXmlData("root>CUST_360>AUTHORIZATION>PASSWORD");

            //get card xsd object factory object.
            card.xsd.ObjectFactory xsdObjectFactory = new card.xsd.ObjectFactory();
            //created GetCreditLastStatementDetails request object from object factory.
            GetCreditLastStatementRequest getCreditLastStatementRequest = xsdObjectFactory.createGetCreditLastStatementRequest();
            
            loggerImpl.debug(logger,"Json to xml conversion done.", "GetCreditLastStatementDetails");

            //setting all the values in request.
            getCreditLastStatementRequest.setCardNoActual(xsdObjectFactory.createGetCreditLastStatementRequestCardNoActual(ls_cardNoActual));
            getCreditLastStatementRequest.setCurrencyCode(xsdObjectFactory.createGetCreditLastStatementRequestCurrencyCode(ls_currencyCode));
            getCreditLastStatementRequest.setPassword(xsdObjectFactory.createGetCreditLastStatementRequestPassword(ls_password));
            getCreditLastStatementRequest.setUsername(xsdObjectFactory.createGetCreditLastStatementRequestUsername(ls_userName));

            //get object factory object from ws axis2.
            org.apache.ws.axis2.ObjectFactory axis2ObjectFactory = new org.apache.ws.axis2.ObjectFactory();
            //wrapping the request in GetCreditLastStatementDetailsRequest
            GetCreditLastStatementDetails getcreditlastStatementDetails = axis2ObjectFactory.createGetCreditLastStatementDetails();
            getcreditlastStatementDetails.setRequest(axis2ObjectFactory.createGetCreditLastStatementDetailsRequest(getCreditLastStatementRequest));
            
            loggerImpl.debug(logger,"GetCreditLastStatementDetails API calling", "GetCreditLastStatementDetails");
            loggerImpl.startProfiler("GetCreditLastStatementDetails API calling.");

            //created response object of GetCreditLastStatementDetailsResponse
            GetCreditLastStatementDetailsResponse getCreditLastStatementDetailsResponse = null;
            try {
                getCreditLastStatementDetailsResponse = (GetCreditLastStatementDetailsResponse) soapConnector.callWebService(getcreditlastStatementDetails);
            } catch (SoapFaultClientException soapException) {
				ls_actualErrMsg = soapException.getFaultStringOrReason();
				ls_responseData = common.ofGetErrDataJsonArray("999", 
						propConfig.getMessageOfResCode("commen.title.999", "Alert."),
						propConfig.getMessageOfResCode("commen.exception.999","","(ENP268)"),
						ls_actualErrMsg, "Currently Service under maintenance so please try later (ENP268)", "0", "R");

				ls_actualErrMsg = common.ofGetTotalErrString(soapException, "");
				loggerImpl.error(logger,"SoapFaultClientException : " + ls_actualErrMsg,"EX:GetCreditLastStatementDetails");
				return ls_responseData;
			}
            
            loggerImpl.debug(logger,"GetCreditLastStatementDetails API called successfully.", "GetCreditLastStatementDetails",getCreditLastStatementDetailsResponse);
            loggerImpl.startProfiler("preparing GetCreditLastStatementDetails API response data.");

            //created GetCreditLastStatementDetailsresponse object
            card.xsd.GetCreditLastStatementDetailsResponse xsd_getCreditLastStatementDetailsResponse = getCreditLastStatementDetailsResponse.getReturn().getValue();

            //getting all the values from the response.
            ls_responseCode = xsd_getCreditLastStatementDetailsResponse.getResponseCode().getValue();
            ls_responseMessage = xsd_getCreditLastStatementDetailsResponse.getResponseMessage().getValue();

            /*if response is 100 then success.
	     *If response is 101 then no records found.
             */
            if (ls_responseCode != null && "100".equals(ls_responseCode)) {
                JSONObjectImpl getCreditCardDetailsJsonObj = new JSONObjectImpl();
                JSONObject responseJsonObject = new JSONObject();
                JSONArray getCreditCardDetailsJsonArray = new JSONArray();

                GetCreditLastStatementResponse getcreditLastStatementResponse = xsd_getCreditLastStatementDetailsResponse.getResponseData().getValue();

                getCreditCardDetailsJsonObj.put("NEWBALANCE", getcreditLastStatementResponse.getNewBalance().getValue());
                getCreditCardDetailsJsonObj.put("PAYMENTDUEAMOUNT", getcreditLastStatementResponse.getPaymentDueAmount().getValue());
                getCreditCardDetailsJsonObj.put("PAYMENTDUEDATE", getcreditLastStatementResponse.getPaymentDueDate().getValue());
                getCreditCardDetailsJsonObj.put("PREVIOUSBALANCE", getcreditLastStatementResponse.getPreviousBalance().getValue());
                getCreditCardDetailsJsonObj.put("STATEMENTDATE", getcreditLastStatementResponse.getStatementDate().getValue());
                getCreditCardDetailsJsonArray.put(getCreditCardDetailsJsonObj);

                responseJsonObject.put("STATUS", "0");
                responseJsonObject.put("COLOR", "G");
                responseJsonObject.put("RESPONSE", getCreditCardDetailsJsonArray);
                responseJsonObject.put("MESSAGE", "");
                responseJsonObject.put("RESPONSECODE", ls_responseCode);
                responseJsonObject.put("RESPONSEMESSAGE", ls_responseMessage);
                ls_responseData = responseJsonObject.toString();
            } else { 

				ls_langResCodeMsg=propConfig.getResponseCode("GetCreditLastStatementDetails."+ls_responseCode);			
				ls_responseData = common.ofGetErrDataJsonArray(ls_langResCodeMsg,
						propConfig.getMessageOfResCode("commen.title."+ls_langResCodeMsg, "Alert."), 
						propConfig.getMessageOfResCode("GetCreditLastStatementDetails."+ls_responseCode,"","(ENP269)"),
						ls_responseMessage, "Currently Service under maintenance so please try later (ENP269).", ls_responseCode, "R");            		            		            	
			}
        } catch (Exception exception) {
			ls_actualErrMsg = common.ofGetTotalErrString(exception, "");            
			loggerImpl.error(logger,"Exception : " + ls_actualErrMsg,"IN:GetCreditLastStatementDetails");

			ls_responseData = common.ofGetErrDataJsonArray("999",
					propConfig.getMessageOfResCode("commen.title.999", "Alert."),
					propConfig.getMessageOfResCode("commen.exception.999","","(ENP270)"),
					exception.getMessage(),"Currently Service under maintenance so please try later (ENP270)","0", "R");			

		}finally {
        	loggerImpl.stopAndPrintOptLogs(logger,"All API called successfully.","GetCreditLastStatementDetails");
        	loggerImpl.info(logger,"Response generated and send to client.", "GetCreditLastStatementDetails");        	
        }
		return ls_responseData;
    }
}
