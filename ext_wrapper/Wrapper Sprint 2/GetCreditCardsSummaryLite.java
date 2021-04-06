package com.easynet.controller.ApiController.CardAPI;

import card.xsd.GetCardSummaryResponse;
import card.xsd.GetCardsSummaryRequest;
import card.xsd.ObjectFactory;
import card.xsd.SummaryCardList;
import com.easynet.configuration.PropConfiguration;
import com.easynet.configuration.SOAPConnector;
import com.easynet.impl.JSONObjectImpl;
import com.easynet.impl.LoggerImpl;
import com.easynet.util.common;
import static com.easynet.util.common.PrintErrLog;
import com.easynet.util.readXML;
import java.util.ArrayList;
import org.apache.ws.axis2.GetCreditCardsSummaryLight;
import org.apache.ws.axis2.GetCreditCardsSummaryLightResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.soap.client.SoapFaultClientException;

@Component
public class GetCreditCardsSummaryLite {
    
    static Logger logger=LoggerFactory.getLogger(GetCreditCardsSummaryLite.class);
    
    @Autowired
    PropConfiguration propConfig;
    
    
    @Autowired
    private SOAPConnector soapConnector;

    public String getCreditCardsSummaryLite(String reqData) {

        String ls_clientId = "";
        String ls_password = "";
        String ls_userName = "";
        String ls_responseCode = "";
        String ls_responseMessage = "";
        String ls_responseData = "";
        String ls_actualErrMsg = "";
        String ls_langResCodeMsg="";
        LoggerImpl loggerImpl=null;
        ArrayList<SummaryCardList> summaryCardList;

        try {
            loggerImpl=new LoggerImpl();
            
            loggerImpl.info(logger, "Preaparing request data and calling API", "IN:getCreditCardsSummaryLite");
            loggerImpl.generateProfiler("getCreditCardsSummaryLite");
            loggerImpl.startProfiler("Preparing request data.");
            
            JSONObject reqJsonObj = new JSONObject(reqData);
            ls_clientId = reqJsonObj.getString("CLIENTID");

            if ((ls_clientId == null || "".equals(ls_clientId))) {

                ls_responseData = common.ofGetErrDataJsonArray("99",
						propConfig.getMessageOfResCode("commen.title.99", "Validation Failed."),
						propConfig.getMessageOfResCode("commen.invalid_req_data",""),		
						"Null values found in request data.", "Invalid Request.","", "R");
            }
            ls_userName = readXML.getXmlData("root>CUST_360>AUTHORIZATION>USER_NAME");
            ls_password = readXML.getXmlData("root>CUST_360>AUTHORIZATION>PASSWORD");

            //created object factory object.
            card.xsd.ObjectFactory xsdObjectFactory = new ObjectFactory();
            //created request object of GetCardsSummaryRequest.
            GetCardsSummaryRequest getCardsSummaryRequest = xsdObjectFactory.createGetCardsSummaryRequest();
            
            loggerImpl.debug(logger,"Json to xml conversion done.", "getCreditCardsSummaryLite");

            //setting the values in the request.
            getCardsSummaryRequest.setClientId(xsdObjectFactory.createGetCardsSummaryRequestClientId(Integer.parseInt(ls_clientId)));
            getCardsSummaryRequest.setPassword(xsdObjectFactory.createGetCardsSummaryRequestPassword(ls_password));
            getCardsSummaryRequest.setUsername(xsdObjectFactory.createGetCardsSummaryRequestUsername(ls_userName));

            //created object factory object.
            org.apache.ws.axis2.ObjectFactory axis2ObjectFactory = new org.apache.ws.axis2.ObjectFactory();
            //wrappint the request in GetCreditCardsSummaryLight.
            GetCreditCardsSummaryLight getCreditCardsSummaryLight = axis2ObjectFactory.createGetCreditCardsSummaryLight();
            getCreditCardsSummaryLight.setRequest(axis2ObjectFactory.createGetCreditCardsSummaryRequest(getCardsSummaryRequest));
            
            loggerImpl.debug(logger,"getCreditCardsSummaryLite API calling", "getCreditCardsSummaryLite");
            loggerImpl.startProfiler("getCreditCardsSummaryLite API calling.");

            //created response object of GetCreditCardsSummaryLightresponse.
            GetCreditCardsSummaryLightResponse getCreditCardsSummaryLightResponse = null;
            try {
                getCreditCardsSummaryLightResponse = (GetCreditCardsSummaryLightResponse) soapConnector.callWebService(getCreditCardsSummaryLight);
            }catch (SoapFaultClientException soapException) {
				ls_actualErrMsg = soapException.getFaultStringOrReason();
				ls_responseData = common.ofGetErrDataJsonArray("999", 
						propConfig.getMessageOfResCode("commen.title.999", "Alert."),
						propConfig.getMessageOfResCode("commen.exception.999","","(ENP251)"),
						ls_actualErrMsg, "Currently Service under maintenance so please try later (ENP251)", "0", "R");

				ls_actualErrMsg = common.ofGetTotalErrString(soapException, "");
				loggerImpl.error(logger,"SoapFaultClientException : " + ls_actualErrMsg,"EX:getCreditCardsSummaryLite");
				return ls_responseData;
			}
            
            loggerImpl.debug(logger,"getCreditCardsSummaryLite API called successfully.", "getCreditCardsSummaryLite",getCreditCardsSummaryLightResponse);
            loggerImpl.startProfiler("preparing getCreditCardsSummaryLite API response data.");

            //created GetCardSummaryResponse object.
            GetCardSummaryResponse xsd_GetCardSummaryResponse = getCreditCardsSummaryLightResponse.getReturn().getValue();

            //getting all the data from the response.
            ls_responseCode = xsd_GetCardSummaryResponse.getResponseCode().getValue();
            ls_responseMessage = xsd_GetCardSummaryResponse.getResponseMessage().getValue();

            /*if response is 100 then success.
	     * If response is 101 then no records found.
             */
            if (ls_responseCode != null && "100".equals(ls_responseCode)) {

                JSONObject responseJsonObject = new JSONObject();
                JSONArray CreditCardsSummaryLightJsonArray = new JSONArray();

                summaryCardList = (ArrayList<SummaryCardList>) xsd_GetCardSummaryResponse.getResponseData();
                for (SummaryCardList summaryCardList1 : summaryCardList) {

                    JSONObjectImpl CreditCardsSummaryLightJsonObj = new JSONObjectImpl();
                    
                    CreditCardsSummaryLightJsonObj.put("AVAILBALANCEBDT", summaryCardList1.getAvailableBalanceBdt().getValue());
                    CreditCardsSummaryLightJsonObj.put("AVAILBALANCEUSD", summaryCardList1.getAvailableBalanceUsd().getValue());
                    CreditCardsSummaryLightJsonObj.put("CARDHOLDERNAME", summaryCardList1.getCardHolderName().getValue());
                    CreditCardsSummaryLightJsonObj.put("CARDNUMBER", summaryCardList1.getCardNumber().getValue());
                    CreditCardsSummaryLightJsonObj.put("CARDSTATUS", summaryCardList1.getCardStatus().getValue());
                    CreditCardsSummaryLightJsonObj.put("CARDTYPE", summaryCardList1.getCardType().getValue());
                    CreditCardsSummaryLightJsonObj.put("EXPIRYDATE", summaryCardList1.getExpiryDate().getValue());
                    CreditCardsSummaryLightJsonObj.put("PAYMENTDUEAMTBDT", summaryCardList1.getPaymentDueAmountBdt().getValue());
                    CreditCardsSummaryLightJsonObj.put("PAYMENTDUEAMTUSD", summaryCardList1.getPaymentDueAmountUsd().getValue());
                    CreditCardsSummaryLightJsonObj.put("PAYMENTDUEDATE", summaryCardList1.getPaymentDueDate().getValue());
                    CreditCardsSummaryLightJsonObj.put("STATEMENTDATE", summaryCardList1.getStatementDate().getValue());
                    CreditCardsSummaryLightJsonObj.put("TOTALOUTSTANDINGBDT", summaryCardList1.getTotalOutstandingBdt().getValue());
                    CreditCardsSummaryLightJsonObj.put("TOTALOUTSTANDINGUSD", summaryCardList1.getTotalOutstandingUsd().getValue());
                    CreditCardsSummaryLightJsonObj.put("TYPE", summaryCardList1.getType().getValue());
                    CreditCardsSummaryLightJsonObj.put("USDFLAG", summaryCardList1.getUsdFlag().getValue());

                    CreditCardsSummaryLightJsonArray.put(CreditCardsSummaryLightJsonObj);

                }
                responseJsonObject.put("STATUS", "0");
                responseJsonObject.put("COLOR", "G");
                responseJsonObject.put("RESPONSE", CreditCardsSummaryLightJsonArray);
                responseJsonObject.put("MESSAGE", "");
                responseJsonObject.put("RESPONSECODE", ls_responseCode);
                responseJsonObject.put("RESPONSEMESSAGE", ls_responseMessage);
                ls_responseData = responseJsonObject.toString();

            }else { 

				ls_langResCodeMsg=propConfig.getResponseCode("getCreditCardsSummaryLite."+ls_responseCode);			
				ls_responseData = common.ofGetErrDataJsonArray(ls_langResCodeMsg,
						propConfig.getMessageOfResCode("commen.title."+ls_langResCodeMsg, "Alert."), 
						propConfig.getMessageOfResCode("getCreditCardsSummaryLite."+ls_responseCode,"","(ENP252)"),
						ls_responseMessage, "Currently Service under maintenance so please try later (ENP252).", ls_responseCode, "R");            		            		            	
			}
            
        }catch (Exception exception) {
			ls_actualErrMsg = common.ofGetTotalErrString(exception, "");            
			loggerImpl.error(logger,"Exception : " + ls_actualErrMsg,"IN:getCreditCardsSummaryLite");

			ls_responseData = common.ofGetErrDataJsonArray("999",
					propConfig.getMessageOfResCode("commen.title.999", "Alert."),
					propConfig.getMessageOfResCode("commen.exception.999","","(ENP253)"),
					exception.getMessage(),"Currently Service under maintenance so please try later (ENP253)","0", "R");			

		}finally {
        	loggerImpl.stopAndPrintOptLogs(logger,"All API called successfully.","getCreditCardsSummaryLite");
        	loggerImpl.info(logger,"Response generated and send to client.", "getCreditCardsSummaryLite");        	
        }
        return ls_responseData;

    }
}
