package com.easynet.controller.ApiController;

import city.xsd.SendWASABillRequest;
import city.xsd.WasaBillResponse;
import com.easynet.configuration.PropConfiguration;
import com.easynet.configuration.SOAPConnector;
import com.easynet.impl.JSONObjectImpl;
import com.easynet.impl.LoggerImpl;
import com.easynet.util.common;
import static com.easynet.util.common.PrintErrLog;
import com.easynet.util.readXML;
import org.apache.ws.axis2.SendWASABillResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.soap.client.SoapFaultClientException;

@Component
public class SendWASABill {
    
    static Logger logger=LoggerFactory.getLogger(SendWASABill.class);
    @Autowired
    private SOAPConnector soapConnector;
    
    @Autowired
    PropConfiguration propConfig;

    public String sendWASABill(String reqData) {

        String ls_accountNo = "";
        String ls_billAmount = "";
        String ls_billNo = "";
        String ls_expiryDate = "";
        String ls_mobileNo = "";
        String ls_password = "";
        String ls_paymentSource = "";
        String ls_remarks = "";
        String ls_userName = "";
        String ls_responsemerchantRef = "";
        String ls_responseCode = "";
        String ls_responseData = "";
        String ls_responseMessage = "";
        String ls_reverseParam = "";
        String transactionRef = "";
        String ls_actualErrMsg = "";
        String ls_langResCodeMsg="";
        LoggerImpl loggerImpl=null;

        try {
            loggerImpl=new LoggerImpl();
        
            loggerImpl.info(logger,"Preparing requset data and calling API.","IN:sendWASABill");
            loggerImpl.generateProfiler("sendWASABill");
            loggerImpl.startProfiler("Preparing request data");
            
            
            JSONObject reqJsonObj = new JSONObject(reqData);
            ls_accountNo = reqJsonObj.getString("ACCOUNTNO");
            ls_billAmount = reqJsonObj.getString("BILLAMOUNT");
            ls_billNo = reqJsonObj.getString("BILLNO");
            ls_expiryDate = reqJsonObj.getString("EXPIRYDATE");
            ls_mobileNo = reqJsonObj.getString("MOBILENO");
            ls_paymentSource = reqJsonObj.getString("PAYMENTSOURCE");
            ls_remarks = reqJsonObj.getString("REMARKS");

            if ((ls_accountNo == null || "".equals(ls_accountNo)) || (ls_billAmount == null || "".equals(ls_billAmount)) || (ls_billNo == null || "".equals(ls_billNo))
                    || (ls_expiryDate == null || "".equals(ls_expiryDate)) || (ls_mobileNo == null || "".equals(ls_mobileNo)) || (ls_paymentSource == null || "".equals(ls_paymentSource))
                    || (ls_remarks == null || "".equals(ls_remarks))) {

                ls_responseData = common.ofGetErrDataJsonArray("99",
						propConfig.getMessageOfResCode("commen.title.99", "Validation Failed."),
						propConfig.getMessageOfResCode("commen.invalid_req_data",""),		
						"Null values found in request data.", "Invalid Request.","", "R");

				return ls_responseData;
            }
            ls_userName = readXML.getXmlData("root>CUST_360>AUTHORIZATION>USER_NAME");
            ls_password = readXML.getXmlData("root>CUST_360>AUTHORIZATION>PASSWORD");

            //created object factory object.
            city.xsd.ObjectFactory xsdObjectFactory = new city.xsd.ObjectFactory();
            //created SendWASABillRequest object from object factory.
            SendWASABillRequest sendWASABillRequest = xsdObjectFactory.createSendWASABillRequest();
            
            loggerImpl.debug(logger,"Json to xml conversion done.", "IN:sendWASABill");

            //setting all the values in the request.
            sendWASABillRequest.setAccountNo(xsdObjectFactory.createSendWASABillRequestAccountNo(ls_accountNo));
            sendWASABillRequest.setBillAmount(xsdObjectFactory.createSendWASABillRequestBillAmount(Double.valueOf(ls_billAmount)));
            sendWASABillRequest.setBillNo(xsdObjectFactory.createSendWASABillRequestBillNo(ls_billNo));
            sendWASABillRequest.setExpiryDate(xsdObjectFactory.createSendWASABillRequestExpiryDate(ls_expiryDate));
            sendWASABillRequest.setMobileNo(xsdObjectFactory.createSendWASABillRequestMobileNo(ls_mobileNo));
            sendWASABillRequest.setPassword(xsdObjectFactory.createSendWASABillRequestPassword(ls_password));
            sendWASABillRequest.setPaymentSource(xsdObjectFactory.createSendWASABillRequestPaymentSource(ls_paymentSource));
            sendWASABillRequest.setRemarks(xsdObjectFactory.createSendWASABillRequestRemarks(ls_remarks));
            sendWASABillRequest.setUsername(xsdObjectFactory.createSendWASABillRequestUsername(ls_userName));

            //created object factory object of ws.axis2.
            org.apache.ws.axis2.ObjectFactory axis2ObjectFactory = new org.apache.ws.axis2.ObjectFactory();
            //wrapping the request in SendWASABill.
            org.apache.ws.axis2.SendWASABill sendWASAbill = axis2ObjectFactory.createSendWASABill();
            sendWASAbill.setRequest(axis2ObjectFactory.createSendWASABillRequest(sendWASABillRequest));
            
            loggerImpl.debug(logger,"sendWASABill API calling", "IN:sendWASABill");
            loggerImpl.startProfiler("sendWASABill API calling.");

            //created response object of SendWASABillResponse.
            SendWASABillResponse sendWASABillResponse = null;
            try {
                sendWASABillResponse = (SendWASABillResponse) soapConnector.callWebService(sendWASAbill);
            }catch (SoapFaultClientException soapException) {
				ls_actualErrMsg = soapException.getFaultStringOrReason();
				ls_responseData = common.ofGetErrDataJsonArray("999", 
						propConfig.getMessageOfResCode("commen.title.999", "Alert."),
						propConfig.getMessageOfResCode("commen.exception.999","","(ENP230)"),
						ls_actualErrMsg, "Currently Service under maintenance so please try later (ENP230)", "0", "R");

				ls_actualErrMsg = common.ofGetTotalErrString(soapException, "");
				loggerImpl.error(logger,"SoapFaultClientException : " + ls_actualErrMsg,"EX:sendWASABill");
				return ls_responseData;
			}
            
            loggerImpl.debug(logger,"sendWASABill API called successfully.", "IN:sendWASABill",sendWASABillResponse);
            loggerImpl.startProfiler("preparing sendWASABill API response data.");

            //created SendWASABillResponse object of city.xsd.
            city.xsd.SendWASABillResponse xsd_sendWASABillResponse = sendWASABillResponse.getReturn().getValue();

            ls_responsemerchantRef = xsd_sendWASABillResponse.getMerchantRef().getValue();
            ls_responseCode = xsd_sendWASABillResponse.getResponseCode().getValue();
            ls_responseMessage = xsd_sendWASABillResponse.getResponseMessage().getValue();
            ls_reverseParam = xsd_sendWASABillResponse.getReverseParam().getValue();
            transactionRef = xsd_sendWASABillResponse.getTransactionRef().getValue();

            /*if response is 100 then success.
	     *If response is 101 then no records found.
             */
            if (ls_responseCode != null && "100".equals(ls_responseCode)) {

                JSONObjectImpl SendWASABillJsonObj = new JSONObjectImpl();
                JSONObject responseJsonObject = new JSONObject();
                JSONArray SendWASABillJsonArray = new JSONArray();

                WasaBillResponse WASABillResponse = xsd_sendWASABillResponse.getResponseData().getValue();

                SendWASABillJsonObj.put("MERCHANTREF", ls_responsemerchantRef);
                SendWASABillJsonObj.put("RESPONSECODE", ls_responseCode);
                SendWASABillJsonObj.put("RESPONSEMESSAGE", ls_responseMessage);
                SendWASABillJsonObj.put("REVERSEPARAM", ls_reverseParam);
                SendWASABillJsonObj.put("TRANSACTIONREF", transactionRef);
                SendWASABillJsonObj.put("MESSAGE", WASABillResponse.getMessage().getValue());
                SendWASABillJsonObj.put("STATUS", WASABillResponse.getStatus().getValue());
                SendWASABillJsonArray.put(SendWASABillJsonObj);

                /* If response is 100 and API is called successfully. */
                responseJsonObject.put("STATUS", "0");
                responseJsonObject.put("COLOR", "G");
                responseJsonObject.put("RESPONSE", SendWASABillJsonArray);
                responseJsonObject.put("MESSAGE", "");
                ls_responseData = responseJsonObject.toString();

            }else { 

				ls_langResCodeMsg=propConfig.getResponseCode("sendWASABill."+ls_responseCode);			
				ls_responseData = common.ofGetErrDataJsonArray(ls_langResCodeMsg,
						propConfig.getMessageOfResCode("commen.title."+ls_langResCodeMsg, "Alert."), 
						propConfig.getMessageOfResCode("sendWASABill."+ls_responseCode,"","(ENP231)"),
						ls_responseMessage, "Currently Service under maintenance so please try later (ENP231).", ls_responseCode, "R");            		            		            	
			}
        }catch (Exception exception) {
			ls_actualErrMsg = common.ofGetTotalErrString(exception, "");            
			loggerImpl.error(logger,"Exception : " + ls_actualErrMsg,"IN:sendWASABill");

			ls_responseData = common.ofGetErrDataJsonArray("999",
					propConfig.getMessageOfResCode("commen.title.999", "Alert."),
					propConfig.getMessageOfResCode("commen.exception.999","","(ENP232)"),
					exception.getMessage(),"Currently Service under maintenance so please try later (ENP232)","0", "R");			

		}finally {
			loggerImpl.stopAndPrintOptLogs(logger,"All API called successfully.","IN:sendWASABill");
			loggerImpl.info(logger,"Response generated and send to client.", "IN:sendWASABill");        	
		}
        return ls_responseData;
    }
}
