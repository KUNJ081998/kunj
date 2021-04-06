package com.easynet.controller.ApiController.CardAPI;

import card.xsd.CustomerCard;
import card.xsd.GetCustomerCardDetailsRequest;
import card.xsd.ObjectFactory;
import com.easynet.configuration.PropConfiguration;
import com.easynet.configuration.SOAPConnector;
import com.easynet.impl.JSONObjectImpl;
import com.easynet.impl.LoggerImpl;
import com.easynet.util.common;
import static com.easynet.util.common.PrintErrLog;
import com.easynet.util.readXML;
import org.apache.ws.axis2.GetClientCardDetails;
import org.apache.ws.axis2.GetClientCardDetailsResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.soap.client.SoapFaultClientException;

@Component
public class GetClientCardDetail {

    static Logger logger=LoggerFactory.getLogger(GetClientCardDetail.class);
    
    @Autowired
    PropConfiguration propConfig;
    
    
    @Autowired
    private SOAPConnector soapConnector;

    public String getClientCardDetails(String reqData) {

        String ls_cardNoActual = "";
        String ls_responseData = "";
        String ls_responseMessage = "";
        String ls_responseCode = "";
        String ls_userName = "";
        String ls_password = "";
        String ls_actualErrMsg = "";
        String ls_langResCodeMsg="";
        LoggerImpl loggerImpl=null;
        
        
        try {
            loggerImpl=new LoggerImpl();
            
            loggerImpl.info(logger, "Preaparing request data and calling API", "IN:getClientCardDetails");
            loggerImpl.generateProfiler("getClientCardDetails");
            loggerImpl.startProfiler("Preparing request data.");
            
            JSONObject reqJsonObj = new JSONObject(reqData);
            ls_cardNoActual = reqJsonObj.getString("CARDNOACTUAL");

            if ((ls_cardNoActual == null || "".equals(ls_cardNoActual))) {

                ls_responseData = common.ofGetErrDataJsonArray("99",
						propConfig.getMessageOfResCode("commen.title.99", "Validation Failed."),
						propConfig.getMessageOfResCode("commen.invalid_req_data",""),		
						"Null values found in request data.", "Invalid Request.","", "R");
            }
            ls_userName = readXML.getXmlData("root>CUST_360>AUTHORIZATION>USER_NAME");
            ls_password = readXML.getXmlData("root>CUST_360>AUTHORIZATION>PASSWORD");

            //get the object factory object.
            card.xsd.ObjectFactory xsdObjectFactory = new ObjectFactory();
            //created Request object.
            GetCustomerCardDetailsRequest getCustomerCardDetaislRequest = xsdObjectFactory.createGetCustomerCardDetailsRequest();
            
            loggerImpl.debug(logger,"Json to xml conversion done.", "getCASAMiniStatement");

            //setting all the values in the request.
            getCustomerCardDetaislRequest.setCardNoActual(xsdObjectFactory.createGetCustomerCardDetailsRequestCardNoActual(ls_cardNoActual));
            getCustomerCardDetaislRequest.setPassword(xsdObjectFactory.createGetCustomerCardDetailsRequestPassword(ls_password));
            getCustomerCardDetaislRequest.setUsername(xsdObjectFactory.createGetCustomerCardDetailsRequestUsername(ls_userName));

            //get the object factory object of axis2.
            org.apache.ws.axis2.ObjectFactory axis2ObjectFactory = new org.apache.ws.axis2.ObjectFactory();
            //wrapping the request in the request object.
            GetClientCardDetails getClientcardDetails = axis2ObjectFactory.createGetClientCardDetails();
            getClientcardDetails.setRequest(axis2ObjectFactory.createGetClientCardDetailsRequest(getCustomerCardDetaislRequest));
            
            loggerImpl.debug(logger,"getClientCardDetails API calling", "getClientCardDetails");
            loggerImpl.startProfiler("getClientCardDetails API calling.");

            //created the response object.
            GetClientCardDetailsResponse getClientCardDetailsResponse = null;
            try {
                getClientCardDetailsResponse = (GetClientCardDetailsResponse) soapConnector.callWebService(getClientcardDetails);
            }  catch (SoapFaultClientException soapException) {
				ls_actualErrMsg = soapException.getFaultStringOrReason();
				ls_responseData = common.ofGetErrDataJsonArray("999", 
						propConfig.getMessageOfResCode("commen.title.999", "Alert."),
						propConfig.getMessageOfResCode("commen.exception.999","","(ENP257)"),
						ls_actualErrMsg, "Currently Service under maintenance so please try later (ENP257)", "0", "R");

				ls_actualErrMsg = common.ofGetTotalErrString(soapException, "");
				loggerImpl.error(logger,"SoapFaultClientException : " + ls_actualErrMsg,"EX:getClientCardDetails");
				return ls_responseData;
			}
            
            loggerImpl.debug(logger,"getClientCardDetails API called successfully.", "IN:getClientCardDetails",getClientCardDetailsResponse);
            loggerImpl.startProfiler("preparing getClientCardDetails API response data.");

            //created response object to fetch the data.
            card.xsd.GetClientCardDetailsResponse xsd_getClientCardDetailsResponse = getClientCardDetailsResponse.getReturn().getValue();

            //getting all the data from the response.
            ls_responseCode = xsd_getClientCardDetailsResponse.getResponseCode().getValue();
            ls_responseMessage = xsd_getClientCardDetailsResponse.getResponseMessage().getValue();

            /*
             * if response is 100 then success.
             * if response is 101 then transaction failed.
             */
            if (ls_responseCode != null && "100".equals(ls_responseCode)) {

                JSONObjectImpl GetClientCardDetailsJsonObj = new JSONObjectImpl();
                JSONArray GetClientCardDetailsJsonArray = new JSONArray();
                JSONObject responseJsonObject = new JSONObject();

                CustomerCard customercard = xsd_getClientCardDetailsResponse.getResponseData().getValue();
                
                GetClientCardDetailsJsonObj.put("CASASCHEME", customercard.getCASAScheme().getValue());
                GetClientCardDetailsJsonObj.put("ACCTTYPE", customercard.getAccountType().getValue());
                GetClientCardDetailsJsonObj.put("ACCTTYPEBDT", customercard.getAccountTypeBdt().getValue());
                GetClientCardDetailsJsonObj.put("ACCTTYPEUSD", customercard.getAccountTypeUsd().getValue());
                GetClientCardDetailsJsonObj.put("BDTACCT", customercard.getBdtAccount().getValue());
                GetClientCardDetailsJsonObj.put("BRANCHCODE", customercard.getBranchCode().getValue());
                GetClientCardDetailsJsonObj.put("CARDCONTRACT", customercard.getCardContract().getValue());
                GetClientCardDetailsJsonObj.put("CARDHOLDERNAME", customercard.getCardHolderName().getValue());
                GetClientCardDetailsJsonObj.put("CARDNOACTUAL", customercard.getCardNoActual().getValue());
                GetClientCardDetailsJsonObj.put("CARDSTATUS", customercard.getCardStatus().getValue());
                GetClientCardDetailsJsonObj.put("CARDTYPE", customercard.getCardType().getValue());
                GetClientCardDetailsJsonObj.put("CARDUID", customercard.getCardUID().getValue());
                GetClientCardDetailsJsonObj.put("CLIENTID", customercard.getClientId().getValue());
                GetClientCardDetailsJsonObj.put("EXPDATE", customercard.getExpiryDate().getValue());
                GetClientCardDetailsJsonObj.put("USDACCT", customercard.getUsdAccount().getValue());

                GetClientCardDetailsJsonArray.put(GetClientCardDetailsJsonObj);

                responseJsonObject.put("STATUS", "0");
                responseJsonObject.put("COLOR", "G");
                responseJsonObject.put("RESPONSE", GetClientCardDetailsJsonArray);
                responseJsonObject.put("MESSAGE", "");
                responseJsonObject.put("RESPONSECODE", ls_responseCode);
                responseJsonObject.put("RESPONSEMESSAGE", ls_responseMessage);
                ls_responseData = responseJsonObject.toString();

            } else { 

				ls_langResCodeMsg=propConfig.getResponseCode("getClientCardDetails."+ls_responseCode);			
				ls_responseData = common.ofGetErrDataJsonArray(ls_langResCodeMsg,
						propConfig.getMessageOfResCode("commen.title."+ls_langResCodeMsg, "Alert."), 
						propConfig.getMessageOfResCode("getClientCardDetails."+ls_responseCode,"","(ENP258)"),
						ls_responseMessage, "Currently Service under maintenance so please try later (ENP258).", ls_responseCode, "R");            		            		            	
			}
            
        }catch (Exception exception) {
			ls_actualErrMsg = common.ofGetTotalErrString(exception, "");            
			loggerImpl.error(logger,"Exception : " + ls_actualErrMsg,"IN:getClientCardDetails");

			ls_responseData = common.ofGetErrDataJsonArray("999",
					propConfig.getMessageOfResCode("commen.title.999", "Alert."),
					propConfig.getMessageOfResCode("commen.exception.999","","(ENP259)"),
					exception.getMessage(),"Currently Service under maintenance so please try later (ENP259)","0", "R");			

		}finally {
        	loggerImpl.stopAndPrintOptLogs(logger,"All API called successfully.","getClientCardDetails");
        	loggerImpl.info(logger,"Response generated and send to client.", "getClientCardDetails");        	
        }
        return ls_responseData;

    }
}
