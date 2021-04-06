package com.easynet.controller.ApiController.CardAPI;

import card.xsd.DebitCardList;
import city.xsd.GetCustomerDebitCardsRequest;
import city.xsd.ObjectFactory;
import com.easynet.configuration.PropConfiguration;
import com.easynet.configuration.SOAPConnector;
import com.easynet.impl.JSONObjectImpl;
import com.easynet.impl.LoggerImpl;
import com.easynet.util.common;
import static com.easynet.util.common.PrintErrLog;
import com.easynet.util.readXML;
import java.util.ArrayList;
import org.apache.ws.axis2.GetCustomerDebitCards;
import org.apache.ws.axis2.GetCustomerDebitCardsResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.soap.client.SoapFaultClientException;

@Component
public class GetCustomerDebitCard {
    
    static Logger logger=LoggerFactory.getLogger(GetCustomerDebitCard.class);
    
    @Autowired
    PropConfiguration propConfig;
    
    
    @Autowired
    private SOAPConnector soapConnector;

    public String getCustomerDebitCard(String reqData) {
        String ls_cbsCustomerID = "";
        String ls_userName = "";
        String ls_password = "";
        String ls_responseCode = "";
        String ls_responseData = "";
        String ls_responseMessage = "";
        String ls_actualErrMsg = "";
        String ls_langResCodeMsg="";
        LoggerImpl loggerImpl=null;
        ArrayList<DebitCardList> debitCardList;

        try {
            JSONObject reqJsonObj = new JSONObject(reqData);
            ls_cbsCustomerID = reqJsonObj.getString("CBSCUSTOMERID");

            if ((ls_cbsCustomerID == null || "".equals(ls_cbsCustomerID))) {

                ls_responseData = common.ofGetErrDataJsonArray("99",
						propConfig.getMessageOfResCode("commen.title.99", "Validation Failed."),
						propConfig.getMessageOfResCode("commen.invalid_req_data",""),		
						"Null values found in request data.", "Invalid Request.","", "R");
            }
            ls_userName = readXML.getXmlData("root>CUST_360>AUTHORIZATION>USER_NAME");
            ls_password = readXML.getXmlData("root>CUST_360>AUTHORIZATION>PASSWORD");

            //created object of object factory.
            city.xsd.ObjectFactory xsdObjectFactory = new ObjectFactory();
            //created GetCustomerDebitCardsRequest object.
            GetCustomerDebitCardsRequest getCustomerDebitCardsRequest = xsdObjectFactory.createGetCustomerDebitCardsRequest();
            
            loggerImpl.debug(logger,"Json to xml conversion done.", "getCustomerDebitCard");

            //setting the values in request.
            getCustomerDebitCardsRequest.setCbsCustomerID(xsdObjectFactory.createGetCustomerDebitCardsRequestCbsCustomerID(ls_cbsCustomerID));
            getCustomerDebitCardsRequest.setPassword(xsdObjectFactory.createGetCustomerDebitCardsRequestPassword(ls_password));
            getCustomerDebitCardsRequest.setUsername(xsdObjectFactory.createGetCustomerDebitCardsRequestUsername(ls_userName));

            //created object of object factory of ws.axis2.
            org.apache.ws.axis2.ObjectFactory axis2ObjectFactory = new org.apache.ws.axis2.ObjectFactory();
            //wrapping the request in GetcustomerDebitCards.
            GetCustomerDebitCards getCustomerDebitCards = axis2ObjectFactory.createGetCustomerDebitCards();
            getCustomerDebitCards.setRequest(axis2ObjectFactory.createGetCustomerDebitCardsRequest(getCustomerDebitCardsRequest));
            
            loggerImpl.debug(logger,"getCustomerDebitCard API calling", "getCustomerDebitCard");
            loggerImpl.startProfiler("getCustomerDebitCard API calling.");

            //created response object of GetcustomerDebitCardsResponse.
            GetCustomerDebitCardsResponse getCustomerDebitCardsResponse = null;
            try {
                getCustomerDebitCardsResponse = (GetCustomerDebitCardsResponse) soapConnector.callWebService(getCustomerDebitCards);
            } catch (SoapFaultClientException soapException) {
				ls_actualErrMsg = soapException.getFaultStringOrReason();
				ls_responseData = common.ofGetErrDataJsonArray("999", 
						propConfig.getMessageOfResCode("commen.title.999", "Alert."),
						propConfig.getMessageOfResCode("commen.exception.999","","(ENP260)"),
						ls_actualErrMsg, "Currently Service under maintenance so please try later (ENP260)", "0", "R");

				ls_actualErrMsg = common.ofGetTotalErrString(soapException, "");
				loggerImpl.error(logger,"SoapFaultClientException : " + ls_actualErrMsg,"EX:getCustomerDebitCard");
				return ls_responseData;
			}
            
            loggerImpl.debug(logger,"getCustomerDebitCard API called successfully.", "getCustomerDebitCard",getCustomerDebitCardsResponse);
            loggerImpl.startProfiler("preparing getCustomerDebitCard API response data.");

            //created GetCustomerDebitCardsResponse.
            city.xsd.GetCustomerDebitCardsResponse xsd_getCustomerDebitCardsResponse = getCustomerDebitCardsResponse.getReturn().getValue();

            //getting all the data from the response.
            ls_responseCode = xsd_getCustomerDebitCardsResponse.getResponseCode().getValue();
            ls_responseMessage = xsd_getCustomerDebitCardsResponse.getResponseMessage().getValue();

            /*
             * if response is 100 then success.
	     * If response is 101 then no records found.
             */
            if (ls_responseCode != null && "100".equals(ls_responseCode)) {
                JSONObject responseJsonObject = new JSONObject();
                JSONArray GetCustomerDebitCardsJsonArray = new JSONArray();

                debitCardList = (ArrayList<DebitCardList>) xsd_getCustomerDebitCardsResponse.getResponseData();
                for (DebitCardList debitCardList1 : debitCardList) {
                    JSONObjectImpl GetCustomerDebitCardsJsonObj = new JSONObjectImpl();
                    GetCustomerDebitCardsJsonObj.put("CARDCVV2", debitCardList1.getCardCVV2().getValue());
                    GetCustomerDebitCardsJsonObj.put("CARDNOACTUAL", debitCardList1.getCardNoActual().getValue());
                    GetCustomerDebitCardsJsonObj.put("CARDPIN", debitCardList1.getCardPIN().getValue());
                    GetCustomerDebitCardsJsonObj.put("CARDSTATE", debitCardList1.getCardState().getValue());
                    GetCustomerDebitCardsJsonObj.put("CARDSTATUS", debitCardList1.getCardStatus().getValue());
                    GetCustomerDebitCardsJsonObj.put("CLIENTID", debitCardList1.getClientId());
                    GetCustomerDebitCardsJsonObj.put("EXPIRYDATE", debitCardList1.getExpiryDate().getValue());
                    
                    GetCustomerDebitCardsJsonArray.put(GetCustomerDebitCardsJsonObj);
                }
                /*
	         * If transaction happens and API is called successfully.
                 */
                responseJsonObject.put("STATUS", "0");
                responseJsonObject.put("COLOR", "G");
                responseJsonObject.put("RESPONSE", GetCustomerDebitCardsJsonArray);
                responseJsonObject.put("MESSAGE", "");
                responseJsonObject.put("RESPONSECODE", ls_responseCode);
                responseJsonObject.put("RESPONSEMESSAGE", ls_responseMessage);
                ls_responseData = responseJsonObject.toString();
            }else { 

				ls_langResCodeMsg=propConfig.getResponseCode("getCustomerDebitCard."+ls_responseCode);			
				ls_responseData = common.ofGetErrDataJsonArray(ls_langResCodeMsg,
						propConfig.getMessageOfResCode("commen.title."+ls_langResCodeMsg, "Alert."), 
						propConfig.getMessageOfResCode("getCustomerDebitCard."+ls_responseCode,"","(ENP261)"),
						ls_responseMessage, "Currently Service under maintenance so please try later (ENP261).", ls_responseCode, "R");            		            		            	
			}
        } catch (Exception exception) {
			ls_actualErrMsg = common.ofGetTotalErrString(exception, "");            
			loggerImpl.error(logger,"Exception : " + ls_actualErrMsg,"IN:getCustomerDebitCard");

			ls_responseData = common.ofGetErrDataJsonArray("999",
					propConfig.getMessageOfResCode("commen.title.999", "Alert."),
					propConfig.getMessageOfResCode("commen.exception.999","","(ENP262)"),
					exception.getMessage(),"Currently Service under maintenance so please try later (ENP262)","0", "R");			

		}finally {
        	loggerImpl.stopAndPrintOptLogs(logger,"All API called successfully.","getCustomerDebitCard");
        	loggerImpl.info(logger,"Response generated and send to client.", "getCustomerDebitCard");        	
        }
        return ls_responseData;
    }
}
