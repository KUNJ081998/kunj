package com.easynet.controller.ApiController;

import city.xsd.DoOtherBankRtgsTransactionRequest;
import city.xsd.DoOtherBankRtgsTransactionResponse;
import city.xsd.ObjectFactory;
import com.easynet.configuration.PropConfiguration;
// import com.easynet.configuration.SOAPConnector;
// import static com.easynet.controller.ApiController.DoMobileTopup.logger;
import com.easynet.impl.JSONObjectImpl;
import com.easynet.impl.LoggerImpl;
import com.easynet.util.common;
import static com.easynet.util.common.PrintErrLog;
import com.easynet.util.readXML;
import java.math.BigDecimal;
import org.apache.ws.axis2.DoRTGSTransactionResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.soap.client.SoapFaultClientException;

@Component
public class DoRTGSTransaction {

    static Logger logger=LoggerFactory.getLogger(DoRTGSTransaction.class);
    
    @Autowired
    private SOAPConnector soapConnector;
    
    @Autowired
    PropConfiguration propConfig;

    public String doRTGsTransaction(String reqData) {

        String ls_amount="";
        
        String ls_benAccNo = "";
        String ls_benName = "";
        String ls_currency = "";
        String ls_password = "";
        String ls_payerName = "";
        String ls_reason = "";
        String ls_routingNo = "";
        String ls_senderAccNo = "";
        String ls_settlementdate = "";
        String ls_userName = "";
        String ls_responseData = "";
        String ls_responseCode = "";
        String ls_responseMessage = "";
        String ls_transactionRefNumber = "";
        String ls_actualErrMsg = "";
         String   ls_langResCodeMsg="";
        LoggerImpl loggerImpl=null;

        try {
            
            loggerImpl=new LoggerImpl();
            
            loggerImpl.info(logger,"Preparing requset data and calling API.","IN:doRTGsTransaction");
            loggerImpl.generateProfiler("doRTGsTransaction");
            loggerImpl.startProfiler("Preparing request data");
            
            
            JSONObject reqJsonObj = new JSONObject(reqData);
            ls_amount = reqJsonObj.getString("AMOUNT");
            BigDecimal amount=new BigDecimal(ls_amount);
            ls_benAccNo = reqJsonObj.getString("BENACCNO");
            ls_benName = reqJsonObj.getString("BENNAME");
            ls_currency = reqJsonObj.getString("CURRENCY");
            ls_payerName = reqJsonObj.getString("PAYERNAME");
            ls_reason = reqJsonObj.getString("REASON");
            ls_routingNo = reqJsonObj.getString("ROUTINGNO");
            ls_senderAccNo = reqJsonObj.getString("SENDERACCNO");
            ls_settlementdate = reqJsonObj.getString("SETTLEMENTDATE");
            
            

            if ((ls_amount == null || "".equals(ls_amount)) || (ls_benAccNo == null || "".equals(ls_benAccNo)) || (ls_benName == null || "".equals(ls_benName))
                    || (ls_currency == null || "".equals(ls_currency)) || (ls_payerName == null || "".equals(ls_payerName)) || (ls_reason == null || "".equals(ls_reason))
                    || (ls_routingNo == null || "".equals(ls_routingNo)) || (ls_senderAccNo == null || "".equals(ls_senderAccNo)) || (ls_settlementdate == null || "".equals(ls_settlementdate))) {

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
            DoOtherBankRtgsTransactionRequest doRTGSTRansRequest = xsdObjectFactory.createDoOtherBankRtgsTransactionRequest();
            
            loggerImpl.debug(logger,"Json to xml conversion done.", "doRTGsTransaction");
            
            
            //setting all the values in the request.
            doRTGSTRansRequest.setAmount(xsdObjectFactory.createDoOtherBankRtgsTransactionRequestAmount(amount));
            doRTGSTRansRequest.setBenAccNo(xsdObjectFactory.createDoOtherBankRtgsTransactionRequestBenAccNo(ls_benAccNo));
            doRTGSTRansRequest.setBenName(xsdObjectFactory.createDoOtherBankRtgsTransactionRequestBenName(ls_benName));
            doRTGSTRansRequest.setCurrency(xsdObjectFactory.createDoOtherBankRtgsTransactionRequestCurrency(ls_currency));
            doRTGSTRansRequest.setPassword(xsdObjectFactory.createDoOtherBankRtgsTransactionRequestPassword(ls_password));
            doRTGSTRansRequest.setPayerName(xsdObjectFactory.createDoOtherBankRtgsTransactionRequestPayerName(ls_payerName));
            doRTGSTRansRequest.setReason(xsdObjectFactory.createDoOtherBankRtgsTransactionRequestRoutingNo(ls_routingNo));
            doRTGSTRansRequest.setSenderAccNo(xsdObjectFactory.createDoOtherBankRtgsTransactionRequestSenderAccNo(ls_senderAccNo));
            doRTGSTRansRequest.setSettlementdate(Long.valueOf(ls_settlementdate));
            doRTGSTRansRequest.setUsername(xsdObjectFactory.createDoOtherBankRtgsTransactionRequestUsername(ls_userName));

            //get the object factory object.
            org.apache.ws.axis2.ObjectFactory axis2ObjectFactory = new org.apache.ws.axis2.ObjectFactory();
            //wrapping the request object in the request.
            org.apache.ws.axis2.DoRTGSTransaction dortgstransaction = axis2ObjectFactory.createDoRTGSTransaction();
            dortgstransaction.setRequest(axis2ObjectFactory.createDoRTGSTransactionRequest(doRTGSTRansRequest));
            
            loggerImpl.debug(logger,"doRTGsTransaction API calling", "doRTGsTransaction");
            loggerImpl.startProfiler("doRTGsTransaction API calling.");

            //get the response object.
            DoRTGSTransactionResponse doRTGSTransactionResponse = null;
            try {
                doRTGSTransactionResponse = (DoRTGSTransactionResponse) soapConnector.callWebService(dortgstransaction);
            } catch (SoapFaultClientException soapException) {
				ls_actualErrMsg = soapException.getFaultStringOrReason();
				ls_responseData = common.ofGetErrDataJsonArray("999", 
						propConfig.getMessageOfResCode("commen.title.999", "Alert."),
						propConfig.getMessageOfResCode("commen.exception.999","","(ENP188)"),
						ls_actualErrMsg, "Currently Service under maintenance so please try later (ENP188)", "0", "R");

				ls_actualErrMsg = common.ofGetTotalErrString(soapException, "");
				loggerImpl.error(logger,"SoapFaultClientException : " + ls_actualErrMsg,"EX:doRTGsTransaction");
				return ls_responseData;
			}
            
            loggerImpl.debug(logger,"doRTGsTransaction API called successfully.","doRTGSTransactionResponse", doRTGSTransactionResponse);
            loggerImpl.startProfiler("preparing doRTGsTransaction API response data.");

            //get the response object.
            DoOtherBankRtgsTransactionResponse xsd_doRTGSTransactionResponse = doRTGSTransactionResponse.getReturn().getValue();

            //getting all the data from the values.
            ls_responseCode = xsd_doRTGSTransactionResponse.getResponseCode().getValue();
            ls_responseMessage = xsd_doRTGSTransactionResponse.getResponseMessage().getValue();
            ls_transactionRefNumber = xsd_doRTGSTransactionResponse.getTransactionRefNumber().getValue();

            /*if response is 100 then success.
	     *If response is 101 then no records found.
             */
            if (ls_responseCode != null && "100".equals(ls_responseCode)) {

                JSONObjectImpl RTGSTransactionJsonObj = new JSONObjectImpl();
                JSONObject responseJsonObject = new JSONObject();
                JSONArray RTGSTransactionJsonArray = new JSONArray();

                RTGSTransactionJsonObj.put("RESPONSECODE", ls_responseCode);
                RTGSTransactionJsonObj.put("RESPONSEMESSAGE", ls_responseMessage);
                RTGSTransactionJsonObj.put("TRANSREFNUMBER", ls_transactionRefNumber);
                RTGSTransactionJsonArray.put(RTGSTransactionJsonObj);

                /* If response is 100 and API is called successfully. */
                responseJsonObject.put("STATUS", "0");
                responseJsonObject.put("COLOR", "G");
                responseJsonObject.put("RESPONSE", RTGSTransactionJsonArray);
                responseJsonObject.put("MESSAGE", "");
                ls_responseData = responseJsonObject.toString();
            } else { 

				ls_langResCodeMsg=propConfig.getResponseCode("doRTGsTransaction."+ls_responseCode);			
				ls_responseData = common.ofGetErrDataJsonArray(ls_langResCodeMsg,
						propConfig.getMessageOfResCode("commen.title."+ls_langResCodeMsg, "Alert."), 
						propConfig.getMessageOfResCode("doRTGsTransaction."+ls_responseCode,"","(ENP189)"),
						ls_responseMessage, "Currently Service under maintenance so please try later (ENP189).", ls_responseCode, "R");            		            		            	
			
    }
        } catch (Exception exception) {
			ls_actualErrMsg = common.ofGetTotalErrString(exception, "");            
			loggerImpl.error(logger,"Exception : " + ls_actualErrMsg,"IN:doRTGsTransaction");

			ls_responseData = common.ofGetErrDataJsonArray("999",
					propConfig.getMessageOfResCode("commen.title.999", "Alert."),
					propConfig.getMessageOfResCode("commen.exception.999","","(ENP190)"),
					exception.getMessage(),"Currently Service under maintenance so please try later (ENP190)","0", "R");			

		}finally {
			loggerImpl.stopAndPrintOptLogs(logger,"All API called successfully.","IN:doRTGsTransaction");
			loggerImpl.info(logger,"Response generated and send to client.", "IN:doRTGsTransaction");        	
		}
        return ls_responseData;

    }
}
