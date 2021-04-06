package com.easynet.controller.ApiController.CardAPI;

import card.xsd.CreditCardTransferResponse;
import city.xsd.CardPurchaseTransactionWithFeeRequest;
import city.xsd.ObjectFactory;
import com.easynet.configuration.PropConfiguration;
import com.easynet.configuration.SOAPConnector;
import com.easynet.impl.JSONObjectImpl;
import com.easynet.impl.LoggerImpl;
import com.easynet.util.common;
import static com.easynet.util.common.PrintErrLog;
import com.easynet.util.readXML;
import org.apache.ws.axis2.DoCardPurchaseTransactionWithFeeResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.soap.client.SoapFaultClientException;

@Component
public class DoCardPurchaseTransactionWithFee {
    
    static Logger logger=LoggerFactory.getLogger(DoCardPurchaseTransactionWithFee.class);
    
    @Autowired
    PropConfiguration propConfig;
    
    
    @Autowired
    private SOAPConnector soapConnector;

   
    public String doCardPurchaseTransactionWithFee(String reqData) {

        String ls_cardNoActual = "";
        String ls_currencyCode = "";
        String ls_expiryDate = "";
        String ls_feeAmount = "";
        String ls_merchantId = "";
        String ls_password = "";
        String ls_remarks = "";
        String ls_terminalId = "";
        String ls_transactionAmount = "";
        String ls_userName = "";
        String ls_responseCode = "";
        String ls_responseMessage = "";
        String ls_transactionDateTime = "";
        String ls_approvalCode = "";
        String ls_actualErrMsg = "";
        String ls_cashierRequestId = "";
        String ls_responseData = "";
        String ls_langResCodeMsg="";
        LoggerImpl loggerImpl=null;

        try {
            
            loggerImpl=new LoggerImpl();
            
            loggerImpl.info(logger, "Preaparing request data and calling API", "IN:doCardPurchaseTransactionWithFee");
            loggerImpl.generateProfiler("doCardPurchaseTransactionWithFee");
            loggerImpl.startProfiler("Preparing request data.");
            
            
            JSONObject reqJsonObj = new JSONObject(reqData);
            ls_cardNoActual = reqJsonObj.getString("CARDNO");
            ls_currencyCode = reqJsonObj.getString("CURRENCYCODE");
            ls_expiryDate = reqJsonObj.getString("EXPIRYDATE");
            ls_feeAmount = reqJsonObj.getString("FEEAMT");
            ls_merchantId = reqJsonObj.getString("MERCHANTID");
            ls_remarks = reqJsonObj.getString("REMARKS");
            ls_terminalId = reqJsonObj.getString("TERMINALID");
            ls_transactionAmount = reqJsonObj.getString("TRANSAMT");

            if ((ls_cardNoActual == null || "".equals(ls_cardNoActual)) || (ls_currencyCode == null || "".equals(ls_currencyCode)) || (ls_expiryDate == null || "".equals(ls_currencyCode))
                    || (ls_feeAmount == null || "".equals(ls_feeAmount)) || (ls_merchantId == null || "".equals(ls_merchantId)) || (ls_remarks == null || "".equals(ls_remarks))
                    || (ls_terminalId == null || "".equals(ls_terminalId)) || (ls_transactionAmount == null || "".equals(ls_transactionAmount))) {

                ls_responseData = common.ofGetErrDataJsonArray("99",
						propConfig.getMessageOfResCode("commen.title.99", "Validation Failed."),
						propConfig.getMessageOfResCode("commen.invalid_req_data",""),		
						"Null values found in request data.", "Invalid Request.","", "R");
            }
            ls_userName = readXML.getXmlData("root>CUST_360>AUTHORIZATION>USER_NAME");
            ls_password = readXML.getXmlData("root>CUST_360>AUTHORIZATION>PASSWORD");

            //get the object factory object.
            city.xsd.ObjectFactory xsdObjectFactory = new ObjectFactory();
            //get the request object.
            CardPurchaseTransactionWithFeeRequest cardPurchaseTransactionWithFee = xsdObjectFactory.createCardPurchaseTransactionWithFeeRequest();
            
            loggerImpl.debug(logger,"Json to xml conversion done.", "doCardPurchaseTransactionWithFee");

            //setting the values in the request.
            cardPurchaseTransactionWithFee.setCardNoActual(xsdObjectFactory.createCardPurchaseTransactionWithFeeRequestCardNoActual(ls_cardNoActual));
            cardPurchaseTransactionWithFee.setCurrencyCode(xsdObjectFactory.createCardPurchaseTransactionWithFeeRequestCurrencyCode(ls_currencyCode));
            cardPurchaseTransactionWithFee.setExpiryDate(xsdObjectFactory.createCardPurchaseTransactionWithFeeRequestExpiryDate(ls_expiryDate));
            cardPurchaseTransactionWithFee.setFeeAmount(xsdObjectFactory.createCardPurchaseTransactionWithFeeRequestFeeAmount(Double.valueOf(ls_feeAmount)));
            cardPurchaseTransactionWithFee.setMerchantId(xsdObjectFactory.createCardPurchaseTransactionRequestMerchantId(ls_merchantId));
            cardPurchaseTransactionWithFee.setPassword(xsdObjectFactory.createCardPurchaseTransactionWithFeeRequestPassword(ls_password));
            cardPurchaseTransactionWithFee.setRemarks(xsdObjectFactory.createCardPurchaseTransactionWithFeeRequestRemarks(ls_remarks));
            cardPurchaseTransactionWithFee.setTerminalId(xsdObjectFactory.createCardPurchaseTransactionWithFeeRequestTerminalId(ls_terminalId));
            cardPurchaseTransactionWithFee.setTransactionAmount(xsdObjectFactory.createCardPurchaseTransactionWithFeeRequestTransactionAmount(Double.valueOf(ls_transactionAmount)));
            cardPurchaseTransactionWithFee.setUsername(xsdObjectFactory.createCardPurchaseTransactionWithFeeRequestUsername(ls_userName));

            //get the object factory object.
            org.apache.ws.axis2.ObjectFactory axis2ObjectFactory = new org.apache.ws.axis2.ObjectFactory();
            //wrapping the request object in the request.
            org.apache.ws.axis2.DoCardPurchaseTransactionWithFee doCardPurchaseTransactionwithfee = axis2ObjectFactory.createDoCardPurchaseTransactionWithFee();
            doCardPurchaseTransactionwithfee.setRequest(axis2ObjectFactory.createDoCardPurchaseTransactionWithFeeRequest(cardPurchaseTransactionWithFee));
            
            loggerImpl.debug(logger,"doCardPurchaseTransactionWithFee API calling", "doCardPurchaseTransactionWithFee");
            loggerImpl.startProfiler("doCardPurchaseTransactionWithFee API calling.");

            //get the response object.
            DoCardPurchaseTransactionWithFeeResponse doCardPurchaseTransactionWithFee = null;
            try {
                doCardPurchaseTransactionWithFee = (DoCardPurchaseTransactionWithFeeResponse) soapConnector.callWebService(doCardPurchaseTransactionwithfee);
            }catch (SoapFaultClientException soapException) {
				ls_actualErrMsg = soapException.getFaultStringOrReason();
				ls_responseData = common.ofGetErrDataJsonArray("999", 
						propConfig.getMessageOfResCode("commen.title.999", "Alert."),
						propConfig.getMessageOfResCode("commen.exception.999","","(ENP248)"),
						ls_actualErrMsg, "Currently Service under maintenance so please try later (ENP248)", "0", "R");

				ls_actualErrMsg = common.ofGetTotalErrString(soapException, "");
				loggerImpl.error(logger,"SoapFaultClientException : " + ls_actualErrMsg,"EX:doCardPurchaseTransactionWithFee");
				return ls_responseData;
			}
            
            loggerImpl.debug(logger,"doCardPurchaseTransactionWithFee API called successfully.", "doCardPurchaseTransactionWithFee",doCardPurchaseTransactionWithFee);
            loggerImpl.startProfiler("preparing doCardPurchaseTransactionWithFee API response data.");

            //get the response object.
            CreditCardTransferResponse xsd_doCardPurchaseTransactionWithFee = doCardPurchaseTransactionWithFee.getReturn().getValue();

            //getting all tha data from the values.
            ls_approvalCode = xsd_doCardPurchaseTransactionWithFee.getApprovalCode().getValue();
            ls_cashierRequestId = String.valueOf(xsd_doCardPurchaseTransactionWithFee.getCashierRequestId());
            ls_responseCode = xsd_doCardPurchaseTransactionWithFee.getResponseCode().getValue();
            ls_transactionDateTime = xsd_doCardPurchaseTransactionWithFee.getTransactionDateTime().getValue();
            ls_responseMessage = xsd_doCardPurchaseTransactionWithFee.getResponseMessage().getValue();

            /*if response is 100 then success.
	     *If response is 101 then no records found.
             */
            if (ls_responseCode != null && "100".equals(ls_responseCode)) {

                JSONObjectImpl CardPurchaseTransactionJsonObj = new JSONObjectImpl();
                JSONObject responseJsonObject = new JSONObject();
                JSONArray CardPurchaseTransactionJsonArray = new JSONArray();

                CardPurchaseTransactionJsonObj.put("APPROVALCODE", ls_approvalCode);
                CardPurchaseTransactionJsonObj.put("CASHIERREQID", ls_cashierRequestId);
                CardPurchaseTransactionJsonObj.put("RESPONSECODE", ls_responseCode);
                CardPurchaseTransactionJsonObj.put("RESPONSEMESSAGE", ls_responseMessage);
                CardPurchaseTransactionJsonObj.put("TRANSDATETIME", ls_transactionDateTime);
                CardPurchaseTransactionJsonArray.put(CardPurchaseTransactionJsonObj);

                /* If response is 100 and API is called successfully. */
                responseJsonObject.put("STATUS", "0");
                responseJsonObject.put("COLOR", "G");
                responseJsonObject.put("RESPONSE", CardPurchaseTransactionJsonArray);
                responseJsonObject.put("MESSAGE", "");
                ls_responseData = responseJsonObject.toString();
            } else { 

				ls_langResCodeMsg=propConfig.getResponseCode("doCardPurchaseTransactionWithFee."+ls_responseCode);			
				ls_responseData = common.ofGetErrDataJsonArray(ls_langResCodeMsg,
						propConfig.getMessageOfResCode("commen.title."+ls_langResCodeMsg, "Alert."), 
						propConfig.getMessageOfResCode("doCardPurchaseTransactionWithFee."+ls_responseCode,"","(ENP249)"),
						ls_responseMessage, "Currently Service under maintenance so please try later (ENP249).", ls_responseCode, "R");            		            		            	
			}
            
        }catch (Exception exception) {
			ls_actualErrMsg = common.ofGetTotalErrString(exception, "");            
			loggerImpl.error(logger,"Exception : " + ls_actualErrMsg,"IN:doCardPurchaseTransactionWithFee");

			ls_responseData = common.ofGetErrDataJsonArray("999",
					propConfig.getMessageOfResCode("commen.title.999", "Alert."),
					propConfig.getMessageOfResCode("commen.exception.999","","(ENP250)"),
					exception.getMessage(),"Currently Service under maintenance so please try later (ENP250)","0", "R");			

		}finally {
        	loggerImpl.stopAndPrintOptLogs(logger,"All API called successfully.","doCardPurchaseTransactionWithFee");
        	loggerImpl.info(logger,"Response generated and send to client.", "doCardPurchaseTransactionWithFee");        	
        }
        return ls_responseData;

    }
}
