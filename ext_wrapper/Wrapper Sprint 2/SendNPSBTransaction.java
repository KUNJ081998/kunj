package com.easynet.controller.ApiController;

import city.xsd.ObjectFactory;
import city.xsd.SendNPSBTransactionRequest;
import com.easynet.configuration.PropConfiguration;
import com.easynet.configuration.SOAPConnector;
import com.easynet.impl.JSONObjectImpl;
import com.easynet.impl.LoggerImpl;
import com.easynet.util.common;
import static com.easynet.util.common.PrintErrLog;
import com.easynet.util.readXML;
import org.apache.ws.axis2.SendNPSBTransactionResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.soap.client.SoapFaultClientException;

@Component
public class SendNPSBTransaction {
    
    static Logger logger=LoggerFactory.getLogger(SendNPSBTransaction.class);
    @Autowired
    private SOAPConnector soapConnector;
    
    @Autowired
    PropConfiguration propConfig;

    public String sendNPSBTransaction(String reqData) {

        String ls_acquiringBankCode = "";
        String ls_currencyCode = "";
        String ls_expiryDate = "";
        String ls_password = "";
        String ls_paymentDestination = "";
        String ls_paymentSource = "";
        String ls_receiverName = "";
        String ls_remarks = "";
        String ls_transactionAmount = "";
        String ls_transactionType = "";
        String ls_userName = "";
        String ls_responseCode = "";
        String ls_responseMessage = "";
        String ls_responseData = "";
        String ls_transactionRefNumber = "";
        String ls_actualErrMsg = "";
        String ls_langResCodeMsg="";
        LoggerImpl loggerImpl=null;

        try {
            loggerImpl=new LoggerImpl();
        
            loggerImpl.info(logger,"Preparing requset data and calling API.","IN:sendNPSBTransaction");
            loggerImpl.generateProfiler("sendNPSBTransaction");
            loggerImpl.startProfiler("Preparing request data");
            
            
            JSONObject reqJsonObj = new JSONObject(reqData);
            ls_acquiringBankCode = reqJsonObj.getString("ACQUIRINGBANKCODE");
            ls_currencyCode = reqJsonObj.getString("CURRENCYCODE");
            ls_expiryDate = reqJsonObj.getString("EXPDATE");
            ls_paymentDestination = reqJsonObj.getString("PAYMENTDEST");
            ls_paymentSource = reqJsonObj.getString("PAYMENTSOURCE");
            ls_receiverName = reqJsonObj.getString("RECEIVERNAME");
            ls_remarks = reqJsonObj.getString("REMARKS");
            ls_transactionAmount = reqJsonObj.getString("TRANSAMOUNT");
            ls_transactionType = reqJsonObj.getString("TRANSTYPE");

            if ((ls_acquiringBankCode == null || "".equals(ls_acquiringBankCode)) || (ls_currencyCode == null || "".equals(ls_currencyCode))
                    || (ls_expiryDate == null || "".equals(ls_expiryDate)) || (ls_paymentDestination == null || "".equals(ls_paymentDestination))
                    || (ls_paymentSource == null || "".equals(ls_paymentSource)) || (ls_receiverName == null || "".equals(ls_receiverName))
                    || (ls_remarks == null || "".equals(ls_remarks)) || (ls_transactionAmount == null || "".equals(ls_transactionAmount)) || (ls_transactionType == null || "".equals(ls_transactionType))) {

               ls_responseData = common.ofGetErrDataJsonArray("99",
						propConfig.getMessageOfResCode("commen.title.99", "Validation Failed."),
						propConfig.getMessageOfResCode("commen.invalid_req_data",""),		
						"Null values found in request data.", "Invalid Request.","", "R");

				return ls_responseData;
            }
            ls_userName = readXML.getXmlData("root>CUST_360>AUTHORIZATION>USER_NAME");
            ls_password = readXML.getXmlData("root>CUST_360>AUTHORIZATION>PASSWORD");

            //get object factory object.
            city.xsd.ObjectFactory xsdObjectFactory = new ObjectFactory();
            //get the request object.
            SendNPSBTransactionRequest sendNPSBTransactionRequest = xsdObjectFactory.createSendNPSBTransactionRequest();
            
            loggerImpl.debug(logger,"Json to xml conversion done.", "IN:sendNPSBTransaction");
            
            //setting all the values in the request.
            sendNPSBTransactionRequest.setAcquiringBankCode(xsdObjectFactory.createSendNPSBTransactionRequestAcquiringBankCode(ls_acquiringBankCode));
            sendNPSBTransactionRequest.setCurrencyCode(xsdObjectFactory.createSendNPSBTransactionRequestCurrencyCode(ls_currencyCode));
            sendNPSBTransactionRequest.setExpiryDate(xsdObjectFactory.createSendNPSBTransactionRequestExpiryDate(ls_expiryDate));
            sendNPSBTransactionRequest.setPassword(xsdObjectFactory.createSendNPSBTransactionRequestPassword(ls_password));
            sendNPSBTransactionRequest.setPaymentDestination(xsdObjectFactory.createSendNPSBTransactionRequestPaymentDestination(ls_paymentDestination));
            sendNPSBTransactionRequest.setPaymentSource(xsdObjectFactory.createSendNPSBTransactionRequestPaymentSource(ls_paymentSource));
            sendNPSBTransactionRequest.setReceiverName(xsdObjectFactory.createSendNPSBTransactionRequestReceiverName(ls_receiverName));
            sendNPSBTransactionRequest.setRemarks(xsdObjectFactory.createSendNPSBTransactionRequestRemarks(ls_remarks));
            sendNPSBTransactionRequest.setTransactionAmount(xsdObjectFactory.createSendNPSBTransactionRequestTransactionAmount(Double.valueOf(ls_transactionAmount)));
            sendNPSBTransactionRequest.setTransactionType(xsdObjectFactory.createSendNPSBTransactionRequestTransactionType(ls_transactionType));
            sendNPSBTransactionRequest.setUsername(xsdObjectFactory.createSendNPSBTransactionRequestUsername(ls_userName));

            //get the object factory object.
            org.apache.ws.axis2.ObjectFactory axis2ObjectFactory = new org.apache.ws.axis2.ObjectFactory();
            //wrapping the request object in request.
            org.apache.ws.axis2.SendNPSBTransaction sendnpsbtransaction = axis2ObjectFactory.createSendNPSBTransaction();
            sendnpsbtransaction.setRequest(axis2ObjectFactory.createSendNPSBTransactionRequest(sendNPSBTransactionRequest));
            
            loggerImpl.debug(logger,"sendNPSBTransaction API calling", "IN:sendNPSBTransaction");
            loggerImpl.startProfiler("sendNPSBTransaction API calling.");

            //get the response object.
            SendNPSBTransactionResponse sendNPSBTransactionResponse = null;
            try {
                sendNPSBTransactionResponse = (SendNPSBTransactionResponse) soapConnector.callWebService(sendnpsbtransaction);
            }catch (SoapFaultClientException soapException) {
				ls_actualErrMsg = soapException.getFaultStringOrReason();
				ls_responseData = common.ofGetErrDataJsonArray("999", 
						propConfig.getMessageOfResCode("commen.title.999", "Alert."),
						propConfig.getMessageOfResCode("commen.exception.999","","(ENP227)"),
						ls_actualErrMsg, "Currently Service under maintenance so please try later (ENP227)", "0", "R");

				ls_actualErrMsg = common.ofGetTotalErrString(soapException, "");
				loggerImpl.error(logger,"SoapFaultClientException : " + ls_actualErrMsg,"EX:sendNPSBTransaction");
				return ls_responseData;
			}
            
            loggerImpl.debug(logger,"sendNPSBTransaction API called successfully.", "IN:sendNPSBTransaction",sendNPSBTransactionResponse);
            loggerImpl.startProfiler("preparing sendNPSBTransaction API response data.");
        
            //get the response object.
            city.xsd.SendNPSBTransactionResponse xsd_sendNPSBTransactionResponse = sendNPSBTransactionResponse.getReturn().getValue();

            //getting all the data from the response.
            ls_responseCode = xsd_sendNPSBTransactionResponse.getResponseCode().getValue();
            ls_responseMessage = xsd_sendNPSBTransactionResponse.getResponseMessage().getValue();
            ls_transactionRefNumber = xsd_sendNPSBTransactionResponse.getTransactionRefNumber().getValue();

            /*if response is 100 then success.
	     *If response is 101 then no records found.
             */
            if (ls_responseCode != null && "100".equals(ls_responseCode)) {

                JSONObjectImpl SendNPSBTransactionJsonObj = new JSONObjectImpl();
                JSONObject responseJsonObject = new JSONObject();
                JSONArray SendNPSBTransactionJsonArray = new JSONArray();

                SendNPSBTransactionJsonObj.put("RESPONSECODE", ls_responseCode);
                SendNPSBTransactionJsonObj.put("RESPONSEMESSAGE", ls_responseMessage);
                SendNPSBTransactionJsonObj.put("TRANSACTIONREFNUMBER", ls_transactionRefNumber);

                SendNPSBTransactionJsonArray.put(SendNPSBTransactionJsonObj);

                /* If response is 100 and API is called successfully. */
                responseJsonObject.put("STATUS", "0");
                responseJsonObject.put("COLOR", "G");
                responseJsonObject.put("RESPONSE", SendNPSBTransactionJsonArray);
                responseJsonObject.put("MESSAGE", "");
                ls_responseData = responseJsonObject.toString();
            }else { 

				ls_langResCodeMsg=propConfig.getResponseCode("sendNPSBTransaction."+ls_responseCode);			
				ls_responseData = common.ofGetErrDataJsonArray(ls_langResCodeMsg,
						propConfig.getMessageOfResCode("commen.title."+ls_langResCodeMsg, "Alert."), 
						propConfig.getMessageOfResCode("sendNPSBTransaction."+ls_responseCode,"","(ENP228)"),
						ls_responseMessage, "Currently Service under maintenance so please try later (ENP228).", ls_responseCode, "R");            		            		            	
			}
        }catch (Exception exception) {
			ls_actualErrMsg = common.ofGetTotalErrString(exception, "");            
			loggerImpl.error(logger,"Exception : " + ls_actualErrMsg,"IN:sendNPSBTransaction");

			ls_responseData = common.ofGetErrDataJsonArray("999",
					propConfig.getMessageOfResCode("commen.title.999", "Alert."),
					propConfig.getMessageOfResCode("commen.exception.999","","(ENP229)"),
					exception.getMessage(),"Currently Service under maintenance so please try later (ENP229)","0", "R");			

		} finally {
			loggerImpl.stopAndPrintOptLogs(logger,"All API called successfully.","IN:sendNPSBTransaction");
			loggerImpl.info(logger,"Response generated and send to client.", "IN:sendNPSBTransaction");        	
		}
        
        return ls_responseData;
    }

}
