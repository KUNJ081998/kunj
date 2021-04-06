package com.easynet.controller.ApiController;

import city.xsd.BillsPayResponse;
import city.xsd.DoMobileTopupRequest;
import com.easynet.configuration.PropConfiguration;
import com.easynet.configuration.SOAPConnector;
// import static com.easynet.controller.ApiController.DoFinacleTransactionReversalFI.logger;
import com.easynet.impl.JSONObjectImpl;
import com.easynet.impl.LoggerImpl;
import com.easynet.util.common;
import static com.easynet.util.common.PrintErrLog;
import com.easynet.util.readXML;
import org.apache.ws.axis2.DoMobileTopupResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.soap.client.SoapFaultClientException;

@Component
public class DoMobileTopup {

    static Logger logger=LoggerFactory.getLogger(DoMobileTopup.class);
            
    @Autowired
    private SOAPConnector soapConnector;
    
    @Autowired
    PropConfiguration propConfig;

    public String doMobileTopup(String reqData) {
        String ls_amount = "";
        String ls_connectiontype = "";
        String ls_expiryDate = "";
        String ls_mobileNumber = "";
        String ls_password = "";
        String ls_sourceAccount = "";
        String ls_userName = "";
        String ls_responsemerchantRef = "";
        String ls_responseoperatorType = "";
        String ls_responseCode = "";
        String ls_responseMessage = "";
        String ls_reverseParam = "";
        String ls_transactionRef = "";
        String ls_transactionRefNumber = "";
        String ls_actualErrMsg = "";
        String ls_responseData = "";
        String ls_langResCodeMsg="";
        LoggerImpl loggerImpl=null;

        try {
            
            loggerImpl=new LoggerImpl();
            
            loggerImpl.info(logger, "Preaparing request data and calling API", "IN:doMobileTopup");
            loggerImpl.generateProfiler("doMobileTopup");
            loggerImpl.startProfiler("Preparing request data.");
            
            
            JSONObject reqJsonObj = new JSONObject(reqData);
            ls_amount = reqJsonObj.getString("AMOUNT");
            ls_connectiontype = reqJsonObj.getString("CONNECTIONTYPE");
            ls_expiryDate = reqJsonObj.getString("EXPIRYDATE");
            ls_mobileNumber = reqJsonObj.getString("MOBILENUMBER");
            ls_sourceAccount = reqJsonObj.getString("SOURCEACCOUNT");
            
            

            if ((ls_amount == null || "".equals(ls_amount)) || (ls_connectiontype == null || "".equals(ls_connectiontype)) || (ls_expiryDate == null || "".equals(ls_expiryDate))
                    || (ls_mobileNumber == null || "".equals(ls_mobileNumber)) || (ls_sourceAccount == null || "".equals(ls_sourceAccount))) {

                 ls_responseData = common.ofGetErrDataJsonArray("99",
						propConfig.getMessageOfResCode("commen.title.99", "Validation Failed."),
						propConfig.getMessageOfResCode("commen.invalid_req_data",""),		
						"Null values found in request data.", "Invalid Request.","", "R");
            }
            ls_userName = readXML.getXmlData("root>CUST_360>AUTHORIZATION>USER_NAME");
            ls_password = readXML.getXmlData("root>CUST_360>AUTHORIZATION>PASSWORD");

            //created object factory object from city.xsd.
            city.xsd.ObjectFactory xsdObjectFactory = new city.xsd.ObjectFactory();
            //created DoMobileTopupRequest object from object factory.
            DoMobileTopupRequest doMobileTopupRequest = xsdObjectFactory.createDoMobileTopupRequest();
            
            loggerImpl.debug(logger,"Json to xml conversion done.", "doMobileTopup");
            
            //setting all the values in the request.
            doMobileTopupRequest.setAmount(xsdObjectFactory.createDoMobileTopupRequestAmount(Double.valueOf(ls_amount)));
            doMobileTopupRequest.setConnectiontype(xsdObjectFactory.createDoMobileTopupRequestConnectiontype(ls_connectiontype));
            doMobileTopupRequest.setExpiryDate(xsdObjectFactory.createDoMobileTopupRequestExpiryDate(ls_expiryDate));
            doMobileTopupRequest.setMobileNumber(xsdObjectFactory.createDoMobileTopupRequestMobileNumber(ls_mobileNumber));
            doMobileTopupRequest.setPassword(xsdObjectFactory.createDoMobileTopupRequestPassword(ls_password));
            doMobileTopupRequest.setSourceAccount(xsdObjectFactory.createDoMobileTopupRequestSourceAccount(ls_sourceAccount));
            doMobileTopupRequest.setUsername(xsdObjectFactory.createDoMobileTopupRequestUsername(ls_userName));

            //created object factory object of ws.axis2.
            org.apache.ws.axis2.ObjectFactory axis2ObjectFactory = new org.apache.ws.axis2.ObjectFactory();
            //wrapping the request in DoMobileTopUp 
            org.apache.ws.axis2.DoMobileTopup domobiletopup = axis2ObjectFactory.createDoMobileTopup();
            domobiletopup.setRequest(axis2ObjectFactory.createDoMobileTopupRequest(doMobileTopupRequest));
            
            loggerImpl.debug(logger,"doMobileTopup API calling", "doMobileTopup");
            loggerImpl.startProfiler("doMobileTopup API calling.");
            
            
            
            //created DoMobileTopUp response object.
            DoMobileTopupResponse doMobileTopUpResponse = null;
            try {
                doMobileTopUpResponse = (DoMobileTopupResponse) soapConnector.callWebService(domobiletopup);
            } catch (SoapFaultClientException soapException) {
				ls_actualErrMsg = soapException.getFaultStringOrReason();
				ls_responseData = common.ofGetErrDataJsonArray("999", 
						propConfig.getMessageOfResCode("commen.title.999", "Alert."),
						propConfig.getMessageOfResCode("commen.exception.999","","(ENP185)"),
						ls_actualErrMsg, "Currently Service under maintenance so please try later (ENP185)", "0", "R");

				ls_actualErrMsg = common.ofGetTotalErrString(soapException, "");
				loggerImpl.error(logger,"SoapFaultClientException : " + ls_actualErrMsg,"EX:doMobileTopup");
				return ls_responseData;
			}
            
            loggerImpl.debug(logger,"doMobileTopup API called successfully.", "doMobileTopup",doMobileTopUpResponse);
            loggerImpl.startProfiler("preparing doMobileTopup API response data.");

            //created DoMobileTopUpResponse object.
            city.xsd.DoMobileTopupResponse xsd_doMobileTopUpResponse = doMobileTopUpResponse.getReturn().getValue();

            //getting all the values from the response.
            ls_responsemerchantRef = xsd_doMobileTopUpResponse.getMerchantRef().getValue();
            ls_responseoperatorType = xsd_doMobileTopUpResponse.getOperatorType().getValue();
            ls_responseCode = xsd_doMobileTopUpResponse.getResponseCode().getValue();
            ls_responseMessage = xsd_doMobileTopUpResponse.getResponseMessage().getValue();
            ls_reverseParam = xsd_doMobileTopUpResponse.getReverseParam().getValue();
            ls_transactionRef = xsd_doMobileTopUpResponse.getTransactionRef().getValue();
            ls_transactionRefNumber = xsd_doMobileTopUpResponse.getTransactionRefNumber().getValue();

            /*if response is 100 then success.
	     *If response is 101 then no records found.
             */
            if (ls_responseCode != null && "100".equals(ls_responseCode)) {
                JSONObjectImpl DoMobileTopUpJsonObj = new JSONObjectImpl();
                JSONObject responseJsonObject = new JSONObject();
                JSONArray DoMobileTopUpJsonArray = new JSONArray();

                BillsPayResponse billsPayResponse = xsd_doMobileTopUpResponse.getResponseData().getValue();
                DoMobileTopUpJsonObj.put("MERCHANTREF", ls_responsemerchantRef);
                DoMobileTopUpJsonObj.put("OPERATORTYPE", ls_responseoperatorType);
                DoMobileTopUpJsonObj.put("MESSAGE", billsPayResponse.getMessage().getValue());
                DoMobileTopUpJsonObj.put("RECHARGESTATUS", billsPayResponse.getRechargeStatus().getValue());
                DoMobileTopUpJsonObj.put("REVERSEPARAM", ls_reverseParam);
                DoMobileTopUpJsonObj.put("TRANSACTIONREF", ls_transactionRef);
                DoMobileTopUpJsonObj.put("TRANSACTIONREFNUMBER", ls_transactionRefNumber);
                DoMobileTopUpJsonArray.put(DoMobileTopUpJsonObj);

                /*
                 * If transaction happens and API is called successfully.
                 */
                responseJsonObject.put("STATUS", "0");
                responseJsonObject.put("COLOR", "G");
                responseJsonObject.put("RESPONSE", DoMobileTopUpJsonArray);
                responseJsonObject.put("RESPONSEMESSAGE", ls_responseMessage);
                responseJsonObject.put("RESPONSECODE", ls_responseCode);
                ls_responseData = responseJsonObject.toString();

            } else { 

				ls_langResCodeMsg=propConfig.getResponseCode("doMobileTopup."+ls_responseCode);			
				ls_responseData = common.ofGetErrDataJsonArray(ls_langResCodeMsg,
						propConfig.getMessageOfResCode("commen.title."+ls_langResCodeMsg, "Alert."), 
						propConfig.getMessageOfResCode("doMobileTopup."+ls_responseCode,"","(ENP186)"),
						ls_responseMessage, "Currently Service under maintenance so please try later (ENP186).", ls_responseCode, "R");            		            		            	
			}
        } catch (Exception exception) {
			ls_actualErrMsg = common.ofGetTotalErrString(exception, "");            
			loggerImpl.error(logger,"Exception : " + ls_actualErrMsg,"IN:doMobileTopup");

			ls_responseData = common.ofGetErrDataJsonArray("999",
					propConfig.getMessageOfResCode("commen.title.999", "Alert."),
					propConfig.getMessageOfResCode("commen.exception.999","","(ENP187)"),
					exception.getMessage(),"Currently Service under maintenance so please try later (ENP187)","0", "R");			

		}finally {
        	loggerImpl.stopAndPrintOptLogs(logger,"All API called successfully.","doMobileTopUp");
        	loggerImpl.info(logger,"Response generated and send to client.", "doMobileTopUp");        	
        }
        return ls_responseData;
    }
}
