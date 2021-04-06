package com.easynet.controller.ApiController;

import city.bkash.xsd.DepositMoneyFromBankAPIRequest;
import city.bkash.xsd.DepositMoneyFromBankAPIResponse;
import city.bkash.xsd.DepositMoneySummary;
import city.bkash.xsd.ObjectFactory;
import com.easynet.configuration.PropConfiguration;
import com.easynet.configuration.SOAPConnector;
import com.easynet.impl.JSONObjectImpl;
import com.easynet.impl.LoggerImpl;
import com.easynet.util.common;
import static com.easynet.util.common.PrintErrLog;
import com.easynet.util.readXML;
import org.apache.ws.axis2.DoBkashDepostitMoney;
import org.apache.ws.axis2.DoBkashDepostitMoneyResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.soap.client.SoapFaultClientException;

@Component
public class DoBkashDepositMoney {

    static Logger logger = LoggerFactory.getLogger(DoBkashDepositMoney.class);
    @Autowired
    private SOAPConnector soapConnector;

    @Autowired
    PropConfiguration propConfiguration;

    public String DoBkashDepositmoney(String reqData) {

        String ls_userName = "";
        String ls_password = "";
        String ls_accountName = "";
        String ls_accountNumber = "";
        String ls_amount = "";
        String ls_bankRefNumber = "";
        String ls_currency = "";
        String ls_mobileNumber = "";
        String ls_reason = "";
        String ls_remarks = "";
        String ls_responseData = "";
        String ls_responseMessage = "";
        String ls_responseCode = "";
        String ls_actualErrMsg = "";
        String ls_langResCodeMsg="";
        LoggerImpl loggerImpl = null;

        try {

            loggerImpl = new LoggerImpl();

            loggerImpl.info(logger, "Preparing requset data and calling API.", "IN:DoBkashDepositmoney");
            loggerImpl.generateProfiler("DoBkashDepositmoney");
            loggerImpl.startProfiler("Preparing request data");

            JSONObject reqJSONObject = new JSONObject(reqData);
            ls_accountName = reqJSONObject.getString("ACCTNAME");
            ls_accountNumber = reqJSONObject.getString("ACCTNUMBER");
            ls_amount = reqJSONObject.getString("AMOUNT");
            ls_bankRefNumber = reqJSONObject.getString("BANKREFNUMBER");
            ls_currency = reqJSONObject.getString("CURRENCY");
            ls_mobileNumber = reqJSONObject.getString("MOBILENUMBER");
            ls_reason = reqJSONObject.getString("REASON");
            ls_remarks = reqJSONObject.getString("REMARKS");

            if ((ls_accountName == null || "".equals(ls_accountName)) || (ls_accountNumber == null || "".equals(ls_accountNumber)) || (ls_amount == null || "".equals(ls_amount))
                    || (ls_bankRefNumber == null || "".equals(ls_bankRefNumber)) || (ls_currency == null || "".equals(ls_currency)) || (ls_mobileNumber == null || "".equals(ls_mobileNumber))
                    || (ls_reason == null || "".equals(ls_reason)) || (ls_remarks == null || "".equals(ls_remarks))) {

                ls_responseData = common.ofGetErrDataJsonArray("99",
                propConfiguration.getMessageOfResCode("commen.title.99", "Validation Failed."),
                propConfiguration.getMessageOfResCode("commen.invalid_req_data", ""),
                "Null values found in request data.", "Invalid Request.", "", "R");
                return ls_responseData;
            }

            ls_userName = readXML.getXmlData("root>CUST_360>AUTHORIZATION>USER_NAME");
            ls_password = readXML.getXmlData("root>CUST_360>AUTHORIZATION>PASSWORD");

            //get the object factory object.
            city.bkash.xsd.ObjectFactory xsdObjectFactory = new ObjectFactory();
            //get the request object.
            DepositMoneyFromBankAPIRequest BkashDepositMoneyRequest = xsdObjectFactory.createDepositMoneyFromBankAPIRequest();
            
            loggerImpl.debug(logger,"Json to xml conversion done.", "IN:DoBkashDepositmoney");

            //setting the values in the request.
            BkashDepositMoneyRequest.setAccountName(xsdObjectFactory.createDepositMoneyFromBankAPIRequestAccountName(ls_accountName));
            BkashDepositMoneyRequest.setAccountNumber(xsdObjectFactory.createDepositMoneyFromBankAPIRequestAccountNumber(ls_accountNumber));
            BkashDepositMoneyRequest.setAmount(xsdObjectFactory.createDepositMoneyFromBankAPIRequestAmount(ls_amount));
            BkashDepositMoneyRequest.setBankRefNumber(xsdObjectFactory.createDepositMoneyFromBankAPIRequestBankRefNumber(ls_bankRefNumber));
            BkashDepositMoneyRequest.setCurrency(xsdObjectFactory.createDepositMoneyFromBankAPIRequestCurrency(ls_currency));
            BkashDepositMoneyRequest.setMobileNumber(xsdObjectFactory.createDepositMoneyFromBankAPIRequestPassword(ls_mobileNumber));
            BkashDepositMoneyRequest.setPassword(xsdObjectFactory.createDepositMoneyFromBankAPIRequestPassword(ls_password));
            BkashDepositMoneyRequest.setReason(xsdObjectFactory.createDepositMoneyFromBankAPIRequestReason(ls_reason));
            BkashDepositMoneyRequest.setRemarks(xsdObjectFactory.createDepositMoneyFromBankAPIRequestRemarks(ls_remarks));
            BkashDepositMoneyRequest.setUsername(xsdObjectFactory.createDepositMoneyFromBankAPIRequestUsername(ls_userName));

            //get the objexct factory object.
            org.apache.ws.axis2.ObjectFactory axis2ObjectFactory = new org.apache.ws.axis2.ObjectFactory();
            //wrapping the request object in the request.
            DoBkashDepostitMoney doBkashDepositMoney = axis2ObjectFactory.createDoBkashDepostitMoney();
            doBkashDepositMoney.setRequest(axis2ObjectFactory.createDoBkashDepostitMoneyRequest(BkashDepositMoneyRequest));
            
            loggerImpl.debug(logger,"DoBkashDepositmoney API calling", "IN:DoBkashDepositmoney");
            loggerImpl.startProfiler("DoBkashDepositmoney API calling.");

            //get the response object.
            DoBkashDepostitMoneyResponse doBkashDepositMoneyResponse = null;
            try {
                doBkashDepositMoneyResponse = (DoBkashDepostitMoneyResponse) soapConnector.callWebService(doBkashDepositMoney);
            } catch (SoapFaultClientException soapException) {
                ls_actualErrMsg = soapException.getFaultStringOrReason();
                ls_actualErrMsg = common.ofGetTotalErrString(soapException, "");
                ls_responseData = common.ofGetErrDataJsonArray("999", 
						propConfiguration.getMessageOfResCode("commen.title.999", "Alert."),
						propConfiguration.getMessageOfResCode("commen.exception.999","","(ENP157)"),
						ls_actualErrMsg, "Currently Service under maintenance so please try later (ENP157)", "0", "R");
                loggerImpl.error(logger,"SoapFaultClientException : " + ls_actualErrMsg,"EX:DoBkashDepositmoney");
                return ls_responseData;
            }
            
            loggerImpl.debug(logger,"DoBkashDepositmoney API called successfully.", "IN:DoBkashDepositmoney",doBkashDepositMoneyResponse);
            loggerImpl.startProfiler("preparing DoBkashDepositmoney API response data.");
                        
            //get the response object.
            DepositMoneyFromBankAPIResponse xsd_doBkashDepositMoneyResponse = doBkashDepositMoneyResponse.getReturn().getValue();

            //getting all the data from the response.
            ls_responseCode = xsd_doBkashDepositMoneyResponse.getResponseCode().getValue();
            ls_responseMessage = xsd_doBkashDepositMoneyResponse.getResponseMessage().getValue();

            
            
                /* If response is 100 then success.*/
                JSONObjectImpl BkashDepositMoneyJsonObj = new JSONObjectImpl();
                JSONObject responseJsonObject = new JSONObject();
                JSONArray BkashDepositMoneyJsonArray = new JSONArray();

                DepositMoneySummary depositMoneySummary = xsd_doBkashDepositMoneyResponse.getResponseData().getValue();

                
                BkashDepositMoneyJsonObj.put("BKASHRESULTCODE", depositMoneySummary.getBKashResultCode().getValue());
                BkashDepositMoneyJsonObj.put("BKASHRESULTDESC", depositMoneySummary.getBKashResultDescription().getValue());
                BkashDepositMoneyJsonObj.put("CONVERSIONID", depositMoneySummary.getConversionId().getValue());
                BkashDepositMoneyJsonObj.put("TRANSID", depositMoneySummary.getTransactionId().getValue());

                BkashDepositMoneyJsonArray.put(BkashDepositMoneyJsonObj);

                /*
                 * If transaction happens and API is called successfully.
                 */
                responseJsonObject.put("STATUS", "0");
                responseJsonObject.put("COLOR", "G");
                responseJsonObject.put("RESPONSE", BkashDepositMoneyJsonArray);
                responseJsonObject.put("MESSAGE", "");
                responseJsonObject.put("RESPONSEMESSAGE", ls_responseMessage);
                responseJsonObject.put("RESPONSECODE", ls_responseCode);
                ls_responseData = responseJsonObject.toString();
           
        } catch (Exception exception) {
            ls_actualErrMsg = common.ofGetTotalErrString(exception, "");            
			loggerImpl.error(logger,"Exception : " + ls_actualErrMsg,"IN:DoBkashDepositmoney");

			ls_responseData = common.ofGetErrDataJsonArray("999",
					propConfiguration.getMessageOfResCode("commen.title.999", "Alert."),
					propConfiguration.getMessageOfResCode("commen.exception.999","","(ENP159)"),
					exception.getMessage(),"Currently Service under maintenance so please try later (ENP159)","0", "R");
        }finally {
			loggerImpl.stopAndPrintOptLogs(logger,"All API called successfully.","IN:DoBkashDepositmoney");
			loggerImpl.info(logger,"Response generated and send to client.", "IN:DoBkashDepositmoney");        	
		}
        return ls_responseData;
    }
}
