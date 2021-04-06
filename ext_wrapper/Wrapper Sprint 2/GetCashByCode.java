package com.easynet.controller.ApiController;

import card.xsd.GetCashByCodeRequest;
import card.xsd.ObjectFactory;
import com.easynet.configuration.PropConfiguration;
import com.easynet.configuration.SOAPConnector;
import static com.easynet.controller.ApiController.DoMobileTopup.logger;
import com.easynet.impl.JSONObjectImpl;
import com.easynet.impl.LoggerImpl;
import com.easynet.util.common;
import static com.easynet.util.common.PrintErrLog;
import com.easynet.util.readXML;
import org.apache.ws.axis2.GetCashByCodeResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.soap.client.SoapFaultClientException;

@Component
public class GetCashByCode {

    static Logger logger=LoggerFactory.getLogger(GetCashByCode.class);
    @Autowired
    private SOAPConnector soapConnector;
    
    @Autowired
    PropConfiguration propConfig;

    public String getCashByCode(String reqData) {

        String ls_cardNoActual = "";
        String ls_currencyCode = "";
        String ls_expiryDate = "";
        String ls_password = "";
        String ls_receiverNo = "";
        String ls_remarks = "";
        String ls_tranCode = "";
        String ls_transactionAmount = "";
        String ls_userName = "";
        String ls_responseData = "";
        String ls_responseCode = "";
        String ls_responseMessage = "";
        String ls_approvalCode = "";
        String ls_actualErrMsg = "";
        String ls_langResCodeMsg="";
        LoggerImpl loggerImpl=null;

        try {
            
            loggerImpl=new LoggerImpl();
            
            loggerImpl.info(logger, "Preaparing request data and calling API", "IN:getCashByCode");
            loggerImpl.generateProfiler("getCashByCode");
            loggerImpl.startProfiler("Preparing request data.");
            
            
            JSONObject reqJsonObj = new JSONObject(reqData);
            ls_cardNoActual = reqJsonObj.getString("CARDNOACTUAL");
            ls_currencyCode = reqJsonObj.getString("CURRENCYCODE");
            ls_expiryDate = reqJsonObj.getString("EXPDATE");
            ls_receiverNo = reqJsonObj.getString("RECEIVERNO");
            ls_remarks = reqJsonObj.getString("REMARKS");
            ls_tranCode = reqJsonObj.getString("TRANCODE");
            ls_transactionAmount = reqJsonObj.getString("TRANAMOUNT");
            
            
            
            if ((ls_cardNoActual == null || "".equals(ls_cardNoActual)) || (ls_currencyCode == null || "".equals(ls_currencyCode)) || (ls_expiryDate == null || "".equals(ls_expiryDate))
                    || (ls_receiverNo == null || "".equals(ls_receiverNo)) || (ls_remarks == null || "".equals(ls_remarks)) || (ls_tranCode == null || "".equals(ls_tranCode))
                    || (ls_transactionAmount == null || "".equals(ls_transactionAmount))) {

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
            GetCashByCodeRequest getCashByCodeRequest = xsdObjectFactory.createGetCashByCodeRequest();
                
            loggerImpl.debug(logger,"Json to xml conversion done.", "getCashByCode");
            
            //setting all the values in the request.
            getCashByCodeRequest.setCardNoActual(xsdObjectFactory.createGetCashByCodeRequestCardNoActual(ls_cardNoActual));
            getCashByCodeRequest.setCurrencyCode(xsdObjectFactory.createGetCashByCodeRequestCurrencyCode(ls_currencyCode));
            getCashByCodeRequest.setExpiryDate(xsdObjectFactory.createGetCashByCodeRequestExpiryDate(ls_expiryDate));
            getCashByCodeRequest.setPassword(xsdObjectFactory.createGetCashByCodeRequestPassword(ls_password));
            getCashByCodeRequest.setReceiverNo(xsdObjectFactory.createGetCashByCodeRequestReceiverNo(ls_receiverNo));
            getCashByCodeRequest.setRemarks(xsdObjectFactory.createGetCashByCodeRequestRemarks(ls_remarks));
            getCashByCodeRequest.setTranCode(Integer.parseInt(ls_tranCode));
            getCashByCodeRequest.setTransactionAmount(xsdObjectFactory.createGetCashByCodeRequestTransactionAmount(Double.valueOf(ls_transactionAmount)));
            getCashByCodeRequest.setUsername(xsdObjectFactory.createGetCashByCodeRequestUsername(ls_userName));

            //get the object factory object.
            org.apache.ws.axis2.ObjectFactory axis2ObjectFactory = new org.apache.ws.axis2.ObjectFactory();
            //wrapping the request object in the request.
            org.apache.ws.axis2.GetCashByCode getcashByCode = axis2ObjectFactory.createGetCashByCode();
            getcashByCode.setRequest(axis2ObjectFactory.createGetCashByCodeRequest(getCashByCodeRequest));
            
            loggerImpl.debug(logger,"getCashByCode API calling", "getCashByCode");
            loggerImpl.startProfiler("getCashByCode API calling.");

            //get the response object.
            GetCashByCodeResponse getCashByCodeResponse = null;
            try {
                getCashByCodeResponse = (GetCashByCodeResponse) soapConnector.callWebService(getcashByCode);
            } catch (SoapFaultClientException soapException) {
				ls_actualErrMsg = soapException.getFaultStringOrReason();
				ls_responseData = common.ofGetErrDataJsonArray("999", 
						propConfig.getMessageOfResCode("commen.title.999", "Alert."),
						propConfig.getMessageOfResCode("commen.exception.999","","(ENP203)"),
						ls_actualErrMsg, "Currently Service under maintenance so please try later (ENP203)", "0", "R");

				ls_actualErrMsg = common.ofGetTotalErrString(soapException, "");
				loggerImpl.error(logger,"SoapFaultClientException : " + ls_actualErrMsg,"EX:getCashByCode");
				return ls_responseData;
			}
            
            loggerImpl.debug(logger,"getCashByCode API called successfully.", "getCashByCode",getCashByCodeResponse);
            loggerImpl.startProfiler("preparing getCashByCode API response data.");

            //get the response object.
            card.xsd.GetCashByCodeResponse xsd_getCashByCodeResponse = getCashByCodeResponse.getReturn().getValue();

            //getting all the data from the response.
            ls_responseCode = xsd_getCashByCodeResponse.getResponseCode().getValue();
            ls_responseMessage = xsd_getCashByCodeResponse.getResponseMessage().getValue();
            ls_approvalCode = xsd_getCashByCodeResponse.getApprovalCode().getValue();

            /*if response is 100 then success.
	     *If response is 101 then no records found.
             */
            if (ls_responseCode != null && "100".equals(ls_responseCode)) {

                JSONObjectImpl CashByCodeJsonObj = new JSONObjectImpl();
                JSONObject responseJsonObject = new JSONObject();
                JSONArray CashByCodeJsonArray = new JSONArray();

                CashByCodeJsonObj.put("RESPONSECODE", ls_responseCode);
                CashByCodeJsonObj.put("RESPONSEMESSAGE", ls_responseMessage);
                CashByCodeJsonObj.put("APPROVALCODE", ls_approvalCode);

                CashByCodeJsonArray.put(CashByCodeJsonObj);

                /* If response is 100 and API is called successfully. */
                responseJsonObject.put("STATUS", "0");
                responseJsonObject.put("COLOR", "G");
                responseJsonObject.put("RESPONSE", CashByCodeJsonArray);
                responseJsonObject.put("MESSAGE", "");
                ls_responseData = responseJsonObject.toString();
            } else { 

				ls_langResCodeMsg=propConfig.getResponseCode("getCashByCode."+ls_responseCode);			
				ls_responseData = common.ofGetErrDataJsonArray(ls_langResCodeMsg,
						propConfig.getMessageOfResCode("commen.title."+ls_langResCodeMsg, "Alert."), 
						propConfig.getMessageOfResCode("getCashByCode."+ls_responseCode,"","(ENP204)"),
						ls_responseMessage, "Currently Service under maintenance so please try later (ENP204).", ls_responseCode, "R");            		            		            	
			}
        } catch (Exception exception) {
			ls_actualErrMsg = common.ofGetTotalErrString(exception, "");            
			loggerImpl.error(logger,"Exception : " + ls_actualErrMsg,"IN:getCashByCode");

			ls_responseData = common.ofGetErrDataJsonArray("999",
					propConfig.getMessageOfResCode("commen.title.999", "Alert."),
					propConfig.getMessageOfResCode("commen.exception.999","","(ENP205)"),
					exception.getMessage(),"Currently Service under maintenance so please try later (ENP205)","0", "R");			

		}finally {
        	loggerImpl.stopAndPrintOptLogs(logger,"All API called successfully.","getCashByCode");
        	loggerImpl.info(logger,"Response generated and send to client.", "getCashByCode");        	
        }
        return ls_responseData;
    }

}
