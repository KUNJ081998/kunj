package com.easynet.controller.ApiController;

import city.xsd.DoInternetRechargePaymentRequest;
import city.xsd.DoInternetRechargePaymentResponse;
import com.easynet.configuration.SOAPConnector;
import com.easynet.impl.JSONObjectImpl;
import com.easynet.util.common;
import static com.easynet.util.common.PrintErrLog;
import com.easynet.util.readXML;
import org.apache.ws.axis2.DoInternetRecharge;
import org.apache.ws.axis2.DoInternetRechargeResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.soap.client.SoapFaultClientException;
import com.easynet.impl.LoggerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.easynet.configuration.PropConfiguration;

@Component
public class doInternetRecharge {

	static Logger logger=LoggerFactory.getLogger(doInternetRecharge.class);

	@Autowired
	PropConfiguration propConfig;

    @Autowired
    private SOAPConnector soapConnector;

    public String doInternetrecharge(String reqData) {
        String ls_amount = "";
        String ls_connectionType = "";
        String ls_expiryDate = "";
        String ls_operatorId = "";
        String ls_password = "";
        String ls_rechargeAmount = "";
        String ls_senderId = "";
        String ls_transactionSource = "";
        String ls_userAccount = "";
        String ls_userName = "";
        String ls_responseCode = "";
        String ls_responseMessage = "";
        String ls_reverseParam = "";
        String ls_transactionRefNumber = "";
        String ls_responseData = "";
        String actualErrMsg = "";

        try {

        	loggerImpl=new LoggerImpl();
            
            loggerImpl.info(logger, "Preaparing request data and calling API", "IN:doInternetrecharge");
            loggerImpl.generateProfiler("doInternetrecharge");
            loggerImpl.startProfiler("Preparing request data.");

            JSONObject reqJsonObj = new JSONObject(reqData);
            ls_amount = reqJsonObj.getString("AMOUNT");
            ls_connectionType = reqJsonObj.getString("CONNECTIONTYPE");
            ls_expiryDate = reqJsonObj.getString("EXPIRYDATE");
            ls_operatorId = reqJsonObj.getString("OPERATORID");
            ls_rechargeAmount = reqJsonObj.getString("RECHARGEAMOUNT");
            ls_senderId = reqJsonObj.getString("SENDERID");
            ls_transactionSource = reqJsonObj.getString("TRANSACTIONSOURCE");
            ls_userAccount = reqJsonObj.getString("USERACCOUNT");

            if ((ls_amount == null || "".equals(ls_amount)) || (ls_connectionType == null || "".equals(ls_connectionType)) || (ls_expiryDate == null || "".equals(ls_expiryDate))
                    || (ls_operatorId == null || "".equals(ls_operatorId)) || (ls_rechargeAmount == null || "".equals(ls_rechargeAmount)) || (ls_senderId == null || "".equals(ls_senderId))
                    || (ls_transactionSource == null || "".equals(ls_transactionSource)) || (ls_transactionRefNumber == null || "".equals(ls_transactionRefNumber))) {

                ls_responseData = common.ofGetErrDataJsonArray("99",
						propConfig.getMessageOfResCode("commen.title.99", "Validation Failed."),
						propConfig.getMessageOfResCode("commen.invalid_req_data",""),		
						"Null values found in request data.", "Invalid Request.","", "R");
            }
            ls_userName = readXML.getXmlData("root>CUST_360>AUTHORIZATION>USER_NAME");
            ls_password = readXML.getXmlData("root>CUST_360>AUTHORIZATION>PASSWORD");

            //get city xsd object factory object.
            city.xsd.ObjectFactory xsdObjectFactory = new city.xsd.ObjectFactory();
            //created DoInternetRechargePayemntRequest request object from object factory.
            DoInternetRechargePaymentRequest doInternetRechargePaymentRequest = xsdObjectFactory.createDoInternetRechargePaymentRequest();

            loggerImpl.debug(logger,"Json to xml conversion done.", "doInternetrecharge");

            //setting all the calues in the request.
            doInternetRechargePaymentRequest.setAmount(xsdObjectFactory.createDoInternetBillPaymentRequestAmount(Double.valueOf(ls_amount)));
            doInternetRechargePaymentRequest.setConnectionType(xsdObjectFactory.createDoInternetRechargePaymentRequestConnectionType(ls_connectionType));
            doInternetRechargePaymentRequest.setExpiryDate(xsdObjectFactory.createDoInternetRechargePaymentRequestExpiryDate(ls_expiryDate));
            doInternetRechargePaymentRequest.setOperatorId(Integer.parseInt(ls_operatorId));
            doInternetRechargePaymentRequest.setPassword(xsdObjectFactory.createDoInternetRechargePaymentRequestPassword(ls_password));
            doInternetRechargePaymentRequest.setRechargeAmount(xsdObjectFactory.createDoInternetRechargePaymentRequestRechargeAmount(ls_rechargeAmount));
            doInternetRechargePaymentRequest.setSenderId(xsdObjectFactory.createDoInternetRechargePaymentRequestSenderId(ls_senderId));
            doInternetRechargePaymentRequest.setTransactionSource(xsdObjectFactory.createDoInternetRechargePaymentRequestTransactionSource(ls_transactionSource));
            doInternetRechargePaymentRequest.setUserAccount(xsdObjectFactory.createDoInternetRechargePaymentRequestUserAccount(ls_userAccount));
            doInternetRechargePaymentRequest.setUsername(xsdObjectFactory.createDoInternetRechargePaymentRequestUsername(ls_userName));

            //get object factory object from ws axis2.
            org.apache.ws.axis2.ObjectFactory axis2ObjectFactory = new org.apache.ws.axis2.ObjectFactory();
            //wrapping the content in DoInternetRecharge
            DoInternetRecharge DoInternetrechargeRequest = axis2ObjectFactory.createDoInternetRecharge();
            DoInternetrechargeRequest.setRequest(axis2ObjectFactory.createDoInternetRechargeRequest(doInternetRechargePaymentRequest));

            loggerImpl.debug(logger,"doInternetrecharge API calling", "doInternetrecharge");
            loggerImpl.startProfiler("doInternetrecharge API calling.");

            //created response object of DoInternetrechargeResponse response
            DoInternetRechargeResponse doInternetRechargeResponse = null;
            try {
                doInternetRechargeResponse = (DoInternetRechargeResponse) soapConnector.callWebService(DoInternetrechargeRequest);
            } catch (SoapFaultClientException soapException) {
				ls_actualErrMsg = soapException.getFaultStringOrReason();
				ls_responseData = common.ofGetErrDataJsonArray("999", 
						propConfig.getMessageOfResCode("commen.title.999", "Alert."),
						propConfig.getMessageOfResCode("commen.exception.999","","(ENP254)"),
						ls_actualErrMsg, "Currently Service under maintenance so please try later (ENP254)", "0", "R");

				ls_actualErrMsg = common.ofGetTotalErrString(soapException, "");
				loggerImpl.error(logger,"SoapFaultClientException : " + ls_actualErrMsg,"EX:doInternetrecharge");
				return ls_responseData;
			}
            
            loggerImpl.debug(logger,"doInternetrecharge API called successfully.", "doInternetrecharge",doInternetRechargeResponse);
            loggerImpl.startProfiler("preparing doInternetrecharge API response data.");

            //created DoInternetRechargePaymentResponse object from city xsd
            DoInternetRechargePaymentResponse xsd_doInternetRechargePaymentResponse = doInternetRechargeResponse.getReturn().getValue();

            //getting all the values from response.
            ls_responseCode = xsd_doInternetRechargePaymentResponse.getResponseCode().getValue();
            ls_responseMessage = xsd_doInternetRechargePaymentResponse.getResponseMessage().getValue();
            ls_reverseParam = xsd_doInternetRechargePaymentResponse.getReverseParam().getValue();
            ls_transactionRefNumber = xsd_doInternetRechargePaymentResponse.getTransactionRefNumber().getValue();

            /*
            * If response is 100 then success.
            * If response is 101 then transaction failed.
             */
            if (ls_responseCode != null && "100".equals(ls_responseCode)) {
                JSONObjectImpl doInternetRechargeJsonObj = new JSONObjectImpl();
                JSONObject responseJsonObject = new JSONObject();
                JSONArray doInternetRechargeJsonArray = new JSONArray();

                doInternetRechargeJsonObj.put("RESPONSECODE", ls_responseCode);
                doInternetRechargeJsonObj.put("RESPONSEMESSAGE", ls_responseMessage);
                doInternetRechargeJsonObj.put("REVERSEPARAM", ls_reverseParam);
                doInternetRechargeJsonObj.put("TRANSACTIONREFNUMBER", ls_transactionRefNumber);
                doInternetRechargeJsonArray.put(doInternetRechargeJsonObj);

                /*
                 * If transaction happens and API is called successfully.
                 */
                responseJsonObject.put("STATUS", "0");
                responseJsonObject.put("COLOR", "G");
                responseJsonObject.put("RESPONSE", doInternetRechargeJsonArray);
                responseJsonObject.put("MESSAGE", "");
                ls_responseData = responseJsonObject.toString();
            } else { 

				ls_langResCodeMsg=propConfig.getResponseCode("doInternetrecharge."+ls_responseCode);			
				ls_responseData = common.ofGetErrDataJsonArray(ls_langResCodeMsg,
						propConfig.getMessageOfResCode("commen.title."+ls_langResCodeMsg, "Alert."), 
						propConfig.getMessageOfResCode("doInternetrecharge."+ls_responseCode,"","(ENP255)"),
						ls_responseMessage, "Currently Service under maintenance so please try later (ENP255).", ls_responseCode, "R");            		            		            	
			}

        } catch (Exception exception) {
			ls_actualErrMsg = common.ofGetTotalErrString(exception, "");            
			loggerImpl.error(logger,"Exception : " + ls_actualErrMsg,"IN:doInternetrecharge");

			ls_responseData = common.ofGetErrDataJsonArray("999",
					propConfig.getMessageOfResCode("commen.title.999", "Alert."),
					propConfig.getMessageOfResCode("commen.exception.999","","(ENP256)"),
					exception.getMessage(),"Currently Service under maintenance so please try later (ENP256)","0", "R");			

		}finally {
        	loggerImpl.stopAndPrintOptLogs(logger,"All API called successfully.","doInternetrecharge");
        	loggerImpl.info(logger,"Response generated and send to client.", "doInternetrecharge");        	
        }
        return ls_responseData;
    }
}
