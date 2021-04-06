package com.easynet.controller.ApiController;

import city.ivac.xsd.DoIVACPaymentRequest;
import city.ivac.xsd.DoIVACPaymentResponse;
import city.ivac.xsd.ObjectFactory;
import com.easynet.configuration.PropConfiguration;
import com.easynet.configuration.SOAPConnector;
import com.easynet.impl.JSONObjectImpl;
import com.easynet.impl.LoggerImpl;
import com.easynet.util.common;
import static com.easynet.util.common.PrintErrLog;
import com.easynet.util.readXML;
import org.apache.ws.axis2.DoIVACPayment;
import org.json.JSONArray;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.soap.client.SoapFaultClientException;

@Component
public class DoIVACCPayment {

    static Logger logger = LoggerFactory.getLogger(DoIVACCPayment.class);
    
    @Autowired
    private SOAPConnector soapConnector;
    
    @Autowired
    PropConfiguration propConfig;

    public String doIVACPayment(String reqData) {

        String ls_amount = "";
        String ls_expiryDate = "";
        String ls_password = "";
        String ls_transactionId = "";
        String ls_transactionSource = "";
        String ls_userName = "";
        String ls_responselid = "";
        String ls_responseCode = "";
        String ls_responseMessage = "";
        String ls_reverseParam = "";
        String ls_transactionRefNumber = "";
        String ls_transionRefId = "";
        String ls_responseData = "";
        String ls_actualErrMsg = "";
        String ls_langResCodeMsg="";
        LoggerImpl loggerImpl=null;

        try {
            
            loggerImpl=new LoggerImpl();
            
            loggerImpl.info(logger, "Preparing requset data and calling API.", "IN:doIVACPayment");
            loggerImpl.generateProfiler("doIVACPayment");
            loggerImpl.startProfiler("preparing request data.");
            
            
            JSONObject reqJsonObj = new JSONObject(reqData);
            ls_amount = reqJsonObj.getString("AMOUNT");
            ls_expiryDate = reqJsonObj.getString("EXPIRYDATE");
            ls_transactionId = reqJsonObj.getString("TRANSACTIONID");
            ls_transactionSource = reqJsonObj.getString("TRANSACTIONSOURCE");
            
            

            if ((ls_amount == null || "".equals(ls_amount)) || (ls_expiryDate == null || "".equals(ls_expiryDate)) || (ls_transactionId == null || "".equals(ls_transactionId))
                    || (ls_transactionSource == null || "".equals(ls_transactionSource))) {
    
                    ls_responseData = common.ofGetErrDataJsonArray("99",
						propConfig.getMessageOfResCode("commen.title.99", "Validation Failed."),
						propConfig.getMessageOfResCode("commen.invalid_req_data",""),		
						"Null values found in request data.", "Invalid Request.","", "R");
            }
            ls_userName = readXML.getXmlData("root>CUST_360>AUTHORIZATION>USER_NAME");
            ls_password = readXML.getXmlData("root>CUST_360>AUTHORIZATION>PASSWORD");

            //created object of object factory of city.ivac.xsd.
            city.ivac.xsd.ObjectFactory xsdObjectFactory = new ObjectFactory();
            //created object of DoIvacPayment.
            DoIVACPaymentRequest doIVACPaymentRequest = xsdObjectFactory.createDoIVACPaymentRequest();
            
            loggerImpl.debug(logger,"Json to xml conversion done.", "doIVACPayment");

            //setting all the values in the request.
            doIVACPaymentRequest.setAmount(xsdObjectFactory.createDoIVACPaymentRequestAmount(Double.valueOf(ls_amount)));
            doIVACPaymentRequest.setExpiryDate(xsdObjectFactory.createDoIVACPaymentRequestExpiryDate(ls_expiryDate));
            doIVACPaymentRequest.setPassword(xsdObjectFactory.createDoIVACPaymentRequestPassword(ls_amount));
            doIVACPaymentRequest.setTransactionId(xsdObjectFactory.createDoIVACPaymentRequestTransactionId(ls_transactionId));
            doIVACPaymentRequest.setTransactionSource(xsdObjectFactory.createDoIVACPaymentRequestTransactionSource(ls_transactionSource));
            doIVACPaymentRequest.setUsername(xsdObjectFactory.createDoIVACPaymentRequestUsername(ls_userName));

            //created object of object factory og ws.axis2.
            org.apache.ws.axis2.ObjectFactory axis2ObjectFactory = new org.apache.ws.axis2.ObjectFactory();
            //wrapping the object in DoIVACPayment.
            DoIVACPayment doIVACpayment = axis2ObjectFactory.createDoIVACPayment();
            doIVACpayment.setRequest(axis2ObjectFactory.createDoIVACPaymentRequest(doIVACPaymentRequest));
            
            
            loggerImpl.debug(logger,"doIVACPayment API calling", "doIVACPayment");
            loggerImpl.startProfiler("doIVACPayment API calling.");
            
            
            //created DoIVACPaymentResponse object.
            org.apache.ws.axis2.DoIVACPaymentResponse doIVACPaymentResponse = null;
            try {
                doIVACPaymentResponse = (org.apache.ws.axis2.DoIVACPaymentResponse) soapConnector.callWebService(doIVACpayment);
            } catch (SoapFaultClientException soapException) {
				ls_actualErrMsg = soapException.getFaultStringOrReason();
				ls_responseData = common.ofGetErrDataJsonArray("999", 
						propConfig.getMessageOfResCode("commen.title.999", "Alert."),
						propConfig.getMessageOfResCode("commen.exception.999","","(ENP182)"),
						ls_actualErrMsg, "Currently Service under maintenance so please try later (ENP182)", "0", "R");

				ls_actualErrMsg = common.ofGetTotalErrString(soapException, "");
				loggerImpl.error(logger,"SoapFaultClientException : " + ls_actualErrMsg,"EX:doIVACPayment");
				return ls_responseData;
			}
            
            loggerImpl.debug(logger,"doIVACPayment API called successfully.", "doIVACPayment",doIVACPaymentResponse);
            loggerImpl.startProfiler("preparing doIVACPayment API response data.");

            //created object of DoIVACPaymentResponse.
            city.ivac.xsd.DoIVACPaymentResponse xsd_doIVACPaymentResponse = doIVACPaymentResponse.getReturn().getValue();

            //getting all the data from the response.
            ls_responselid = xsd_doIVACPaymentResponse.getLid().getValue();
            ls_responseCode = xsd_doIVACPaymentResponse.getResponseCode().getValue();
            ls_responseMessage = xsd_doIVACPaymentResponse.getResponseMessage().getValue();
            ls_reverseParam = xsd_doIVACPaymentResponse.getReverseParam().getValue();
            ls_transactionRefNumber = xsd_doIVACPaymentResponse.getTransactionRefNumber().getValue();
            ls_transactionId = xsd_doIVACPaymentResponse.getTransionRefId().getValue();

            /* If response is 100 then success.
             * If response is 101 then no records found.
             */
            

                JSONObjectImpl DoIVACPaymentJsonObj = new JSONObjectImpl();
                JSONObject responseJsonObject = new JSONObject();
                JSONArray DoIVACPaymentJsonArray = new JSONArray();

                DoIVACPaymentJsonObj.put("LID", ls_responselid);
                DoIVACPaymentJsonObj.put("RESPONSECODE", ls_responseCode);
                DoIVACPaymentJsonObj.put("RESPONSEMESSAGE", ls_responseMessage);
                DoIVACPaymentJsonObj.put("REVERSEPARAM", ls_reverseParam);
                DoIVACPaymentJsonObj.put("TRANSACTIONREFNUMBER", ls_transactionRefNumber);
                DoIVACPaymentJsonObj.put("TRANSACTIONID", ls_transactionId);
                DoIVACPaymentJsonArray.put(DoIVACPaymentJsonObj);

                /*
                 * If transaction happens and API is called successfully.
                 */
                responseJsonObject.put("STATUS", "0");
                responseJsonObject.put("COLOR", "G");
                responseJsonObject.put("RESPONSE", DoIVACPaymentJsonArray);
                responseJsonObject.put("MESSAGE", "");
                ls_responseData = responseJsonObject.toString();
            
        } catch (Exception exception) {
			ls_actualErrMsg = common.ofGetTotalErrString(exception, "");            
			loggerImpl.error(logger,"Exception : " + ls_actualErrMsg,"IN:doIVACPayment");

			ls_responseData = common.ofGetErrDataJsonArray("999",
					propConfig.getMessageOfResCode("commen.title.999", "Alert."),
					propConfig.getMessageOfResCode("commen.exception.999","","(ENP184)"),
					exception.getMessage(),"Currently Service under maintenance so please try later (ENP184)","0", "R");			

		}finally {
        	loggerImpl.stopAndPrintOptLogs(logger,"All API called successfully.","doIVACPayment");
        	loggerImpl.info(logger,"Response generated and send to client.", "doIVACPayment");        	
        }
        return ls_responseData;

    }
}
