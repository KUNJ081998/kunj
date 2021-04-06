package com.easynet.controller.ApiController.CardAPI;

import card.xsd.CreditCardTransferResponse;
import card.xsd.CreditCardTransferReverseRequest;
import card.xsd.ObjectFactory;
import com.easynet.configuration.PropConfiguration;
import com.easynet.configuration.SOAPConnector;
import com.easynet.impl.JSONObjectImpl;
import com.easynet.impl.LoggerImpl;
import com.easynet.util.common;
import static com.easynet.util.common.PrintErrLog;
import com.easynet.util.readXML;
import org.apache.ws.axis2.CreditCardTransferRollback;
import org.apache.ws.axis2.CreditCardTransferRollbackResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.soap.client.SoapFaultClientException;

@Component
public class CreditCardTransferRollBack {

    static Logger logger=LoggerFactory.getLogger(CreditCardTransferRollBack.class);
    
    @Autowired
    PropConfiguration propConfig;
    
    
    @Autowired
    private SOAPConnector soapConnector;

   
    public String creditCardTransferRollBack(String reqData) {

        String ls_amount = "";
        String ls_approvalCode = "";
        String ls_cardNoActual = "";
        String ls_cashierRequestId = "";
        String ls_currencyCode = "";
        String ls_password = "";
        String ls_remarks = "";
        String ls_userName = "";
        String ls_responseApprovalCode = "";
        String ls_responseCashierRequestId = "";
        String ls_responseCode = "";
        String ls_responseMessage = "";
        String ls_transactionDateTime = "";
        String ls_actualErrMsg = "";
        String ls_responseData = "";
        String ls_langResCodeMsg="";
        LoggerImpl loggerImpl=null;

        try {
            
            loggerImpl=new LoggerImpl();
            
            loggerImpl.info(logger, "Preaparing request data and calling API", "IN:creditCardTransferRollBack");
            loggerImpl.generateProfiler("creditCardTransferRollBack");
            loggerImpl.startProfiler("Preparing request data.");
            
            
            JSONObject reqJsonObj = new JSONObject(reqData);
            ls_amount = reqJsonObj.getString("AMOUNT");
            ls_approvalCode = reqJsonObj.getString("APPROVALCODE");
            ls_cardNoActual = reqJsonObj.getString("CARDNOACTUAL");
            ls_cashierRequestId = reqJsonObj.getString("CASHIERREQID");
            ls_currencyCode = reqJsonObj.getString("CURRENCYCODE");
            ls_remarks = reqJsonObj.getString("REMARKS");

            if ((ls_amount == null || "".equals(ls_amount)) || (ls_approvalCode == null || "".equals(ls_approvalCode)) || (ls_cardNoActual == null || "".equals(ls_cardNoActual))
                    || (ls_cashierRequestId == null || "".equals(ls_cashierRequestId)) || (ls_currencyCode == null || "".equals(ls_currencyCode)) || (ls_remarks == null || "".equals(ls_remarks))) {

               ls_responseData = common.ofGetErrDataJsonArray("99",
						propConfig.getMessageOfResCode("commen.title.99", "Validation Failed."),
						propConfig.getMessageOfResCode("commen.invalid_req_data",""),		
						"Null values found in request data.", "Invalid Request.","", "R");
            }
            ls_userName = readXML.getXmlData("root>CUST_360>AUTHORIZATION>USER_NAME");
            ls_password = readXML.getXmlData("root>CUST_360>AUTHORIZATION>PASSWORD");

            //get the object factory object.
            card.xsd.ObjectFactory xsdObjectFactory = new ObjectFactory();
            //get the request object.
            CreditCardTransferReverseRequest creditCardTransferReverseRequest = xsdObjectFactory.createCreditCardTransferReverseRequest();
            
            loggerImpl.debug(logger,"Json to xml conversion done.", "creditCardTransferRollBack");

            //setting all the values in the request.
            creditCardTransferReverseRequest.setAmount(xsdObjectFactory.createCreditCardTransferReverseRequestAmount(Double.valueOf(ls_amount)));
            creditCardTransferReverseRequest.setApprovalCode(xsdObjectFactory.createCreditCardTransferReverseRequestApprovalCode(ls_approvalCode));
            creditCardTransferReverseRequest.setCardNoActual(xsdObjectFactory.createCreditCardTransferReverseRequestCardNoActual(ls_cardNoActual));
            creditCardTransferReverseRequest.setCashierRequestId(xsdObjectFactory.createCreditCardTransferReverseRequestCashierRequestId(ls_cashierRequestId));
            creditCardTransferReverseRequest.setCurrencyCode(xsdObjectFactory.createCreditCardTransferReverseRequestCurrencyCode(ls_currencyCode));
            creditCardTransferReverseRequest.setPassword(xsdObjectFactory.createCreditCardTransferReverseRequestPassword(ls_password));
            creditCardTransferReverseRequest.setRemarks(xsdObjectFactory.createCreditCardTransferReverseRequestRemarks(ls_remarks));
            creditCardTransferReverseRequest.setUsername(xsdObjectFactory.createCreditCardTransferReverseRequestUsername(ls_userName));

            //get the object factory object.
            org.apache.ws.axis2.ObjectFactory axis2ObjectFactory = new org.apache.ws.axis2.ObjectFactory();
            //wrapping the request in the object.
            CreditCardTransferRollback creditCardTransferRollback = axis2ObjectFactory.createCreditCardTransferRollback();
            creditCardTransferRollback.setRequest(axis2ObjectFactory.createCreditCardTransferRollbackRequest(creditCardTransferReverseRequest));
            
            loggerImpl.debug(logger,"creditCardTransferRollBack API calling", "creditCardTransferRollBack");
            loggerImpl.startProfiler("creditCardTransferRollBack API calling.");

            //get the response object.
            CreditCardTransferRollbackResponse creditCardTransferRollBackResponse = null;
            try {
                creditCardTransferRollBackResponse = (CreditCardTransferRollbackResponse) soapConnector.callWebService(creditCardTransferRollback);
            }catch (SoapFaultClientException soapException) {
				ls_actualErrMsg = soapException.getFaultStringOrReason();
				ls_responseData = common.ofGetErrDataJsonArray("999", 
						propConfig.getMessageOfResCode("commen.title.999", "Alert."),
						propConfig.getMessageOfResCode("commen.exception.999","","(ENP245)"),
						ls_actualErrMsg, "Currently Service under maintenance so please try later (ENP245)", "0", "R");

				ls_actualErrMsg = common.ofGetTotalErrString(soapException, "");
				loggerImpl.error(logger,"SoapFaultClientException : " + ls_actualErrMsg,"EX:creditCardTransferRollBack");
				return ls_responseData;
			}
            
            loggerImpl.debug(logger,"creditCardTransferRollBack API called successfully.", "creditCardTransferRollBack",creditCardTransferRollBackResponse);
            loggerImpl.startProfiler("preparing creditCardTransferRollBack API response data.");

            //get the response object.
            CreditCardTransferResponse xsd_creditCardTransferRollBackResponse = creditCardTransferRollBackResponse.getReturn().getValue();

            //getting all the data from the response.
            ls_responseApprovalCode = xsd_creditCardTransferRollBackResponse.getApprovalCode().getValue();
            ls_responseCashierRequestId = String.valueOf(xsd_creditCardTransferRollBackResponse.getCashierRequestId());
            ls_responseCode = xsd_creditCardTransferRollBackResponse.getResponseCode().getValue();
            ls_responseMessage = xsd_creditCardTransferRollBackResponse.getResponseMessage().getValue();
            ls_transactionDateTime = xsd_creditCardTransferRollBackResponse.getTransactionDateTime().getValue();

            /*
             * if response is 100 then success.
             * if response is 101 then transaction failed.
             */
            if (ls_responseCode != null && "100".equals(ls_responseCode)) {

                JSONObjectImpl CreditCardTransferRollBackJsonObj = new JSONObjectImpl();
                JSONArray CreditCardTransferRollBackJsonArray = new JSONArray();
                JSONObject responseJsonObject = new JSONObject();

                CreditCardTransferRollBackJsonObj.put("RESPAPPROVALCODE", ls_responseApprovalCode);
                CreditCardTransferRollBackJsonObj.put("RESPCASHIERREQID", ls_cashierRequestId);
                CreditCardTransferRollBackJsonObj.put("RESPONSECODE", ls_responseCode);
                CreditCardTransferRollBackJsonObj.put("RESPONSEMESSAGE", ls_responseMessage);
                CreditCardTransferRollBackJsonObj.put("TRANSDATETIME", ls_transactionDateTime);

                CreditCardTransferRollBackJsonArray.put(CreditCardTransferRollBackJsonObj);

                responseJsonObject.put("STATUS", "0");
                responseJsonObject.put("COLOR", "G");
                responseJsonObject.put("RESPONSE", CreditCardTransferRollBackJsonArray);
                responseJsonObject.put("MESSAGE", "");

                ls_responseData = responseJsonObject.toString();
            }else { 

				ls_langResCodeMsg=propConfig.getResponseCode("creditCardTransferRollBack."+ls_responseCode);			
				ls_responseData = common.ofGetErrDataJsonArray(ls_langResCodeMsg,
						propConfig.getMessageOfResCode("commen.title."+ls_langResCodeMsg, "Alert."), 
						propConfig.getMessageOfResCode("creditCardTransferRollBack."+ls_responseCode,"","(ENP246)"),
						ls_responseMessage, "Currently Service under maintenance so please try later (ENP246).", ls_responseCode, "R");            		            		            	
			}
            
        }catch (Exception exception) {
			ls_actualErrMsg = common.ofGetTotalErrString(exception, "");            
			loggerImpl.error(logger,"Exception : " + ls_actualErrMsg,"IN:creditCardTransferRollBack");

			ls_responseData = common.ofGetErrDataJsonArray("999",
					propConfig.getMessageOfResCode("commen.title.999", "Alert."),
					propConfig.getMessageOfResCode("commen.exception.999","","(ENP247)"),
					exception.getMessage(),"Currently Service under maintenance so please try later (ENP247)","0", "R");			

		}finally {
        	loggerImpl.stopAndPrintOptLogs(logger,"All API called successfully.","creditCardTransferRollBack");
        	loggerImpl.info(logger,"Response generated and send to client.", "creditCardTransferRollBack");        	
        }
        return ls_responseData;
    }
}
