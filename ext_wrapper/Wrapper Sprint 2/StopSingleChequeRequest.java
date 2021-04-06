package com.easynet.controller.ApiController;

import city.xsd.CardTransactionResponse;
import city.xsd.StopChequeRq;
import com.easynet.configuration.PropConfiguration;
import com.easynet.configuration.SOAPConnector;
import com.easynet.impl.JSONObjectImpl;
import com.easynet.impl.LoggerImpl;
import com.easynet.util.common;
import static com.easynet.util.common.PrintErrLog;
import com.easynet.util.readXML;
import org.apache.ws.axis2.ObjectFactory;
import org.apache.ws.axis2.StopSingleChequeRequestResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.soap.client.SoapFaultClientException;

@Component
public class StopSingleChequeRequest {
    
    static Logger logger=LoggerFactory.getLogger(StopSingleChequeRequest.class);
    @Autowired
    PropConfiguration propConfig;
    @Autowired
    private SOAPConnector soapConnector;

    public String stopSingleChequeRequest(String reqData) {
        String ls_accountNumber = "";
        String ls_chequeNumber = "";
        String ls_password = "";
        String ls_userName = "";
        String ls_remarks = "";
        String ls_transactionDateTime = "";
        String ls_transactionRefNumber = "";
        String ls_responseCode = "";
        String ls_responseMessage = "";
        String ls_responseData = "";
        String ls_actualErrMsg = "";
        String ls_langResCodeMsg="";
        LoggerImpl loggerImpl=null;
        
        

        try {
            loggerImpl=new LoggerImpl();
        
            loggerImpl.info(logger,"Preparing requset data and calling API.","IN:stopSingleChequeRequest");
            loggerImpl.generateProfiler("stopSingleChequeRequest");
            loggerImpl.startProfiler("Preparing request data");
            
            JSONObject reqJsonObj = new JSONObject(reqData);
            ls_accountNumber = reqJsonObj.getString("ACCTNUMBER");
            ls_chequeNumber = reqJsonObj.getString("CHEQUENUMBER");
            ls_remarks = reqJsonObj.getString("REMARKS");

            if ((ls_accountNumber == null || "".equals(ls_accountNumber)) || (ls_chequeNumber == null || "".equals(ls_chequeNumber)) || (ls_remarks == null || "".equals(ls_remarks))) {

                ls_responseData = common.ofGetErrDataJsonArray("99",
						propConfig.getMessageOfResCode("commen.title.99", "Validation Failed."),
						propConfig.getMessageOfResCode("commen.invalid_req_data",""),		
						"Null values found in request data.", "Invalid Request.","", "R");

				return ls_responseData;
            }
            ls_userName = readXML.getXmlData("root>CUST_360>AUTHORIZATION>USER_NAME");
            ls_password = readXML.getXmlData("root>CUST_360>AUTHORIZATION>PASSWORD");

            //get city xsd object factory object.
            city.xsd.ObjectFactory xsdObjectFactory = new city.xsd.ObjectFactory();
            //created object of Stop Cheque Req.
            StopChequeRq stopChequeReq = xsdObjectFactory.createStopChequeRq();
            
            loggerImpl.debug(logger,"Json to xml conversion done.", "IN:stopSingleChequeRequest");

            //setting all the values in the request.
            stopChequeReq.setAccountNumber(xsdObjectFactory.createStopChequeRqAccountNumber(ls_accountNumber));
            stopChequeReq.setChequeNumber(xsdObjectFactory.createStopChequeRqChequeNumber(ls_chequeNumber));
            stopChequeReq.setPassword(xsdObjectFactory.createStopChequeRqPassword(ls_password));
            stopChequeReq.setRemarks(xsdObjectFactory.createStopChequeRqRemarks(ls_remarks));
            stopChequeReq.setUsername(xsdObjectFactory.createStopChequeRqUsername(ls_userName));

            //get axis2 object factory object.
            org.apache.ws.axis2.ObjectFactory axis2ObjectFactory = new ObjectFactory();
            //wrapping the request in StopSingleChequeRequest.
            org.apache.ws.axis2.StopSingleChequeRequest stopsinglechequeRequest = axis2ObjectFactory.createStopSingleChequeRequest();
            stopsinglechequeRequest.setRequest(axis2ObjectFactory.createStopSingleChequeRequestRequest(stopChequeReq));
            
            loggerImpl.debug(logger,"stopSingleChequeRequest API calling", "IN:stopSingleChequeRequest");
            loggerImpl.startProfiler("stopSingleChequeRequest API calling.");

            //created response object StopSingleChequeRequestResponse.
            StopSingleChequeRequestResponse stopSingleChequeRequestResponse = null;
            try {
                stopSingleChequeRequestResponse = (StopSingleChequeRequestResponse) soapConnector.callWebService(stopsinglechequeRequest);
            }catch (SoapFaultClientException soapException) {
				ls_actualErrMsg = soapException.getFaultStringOrReason();
				ls_responseData = common.ofGetErrDataJsonArray("999", 
						propConfig.getMessageOfResCode("commen.title.999", "Alert."),
						propConfig.getMessageOfResCode("commen.exception.999","","(ENP233)"),
						ls_actualErrMsg, "Currently Service under maintenance so please try later (ENP233)", "0", "R");

				ls_actualErrMsg = common.ofGetTotalErrString(soapException, "");
				loggerImpl.error(logger,"SoapFaultClientException : " + ls_actualErrMsg,"EX:stopSingleChequeRequest");
				return ls_responseData;
			}
            
            loggerImpl.debug(logger,"stopSingleChequeRequest API called successfully.", "IN:stopSingleChequeRequest",stopSingleChequeRequestResponse);
            loggerImpl.startProfiler("preparing stopSingleChequeRequest API response data.");

            //get response object of CradTransactionResponse.
            CardTransactionResponse xsd_cardTransacionResponse = stopSingleChequeRequestResponse.getReturn().getValue();

            //getting all the data from the response object.
            ls_responseCode = xsd_cardTransacionResponse.getResponseCode().getValue();
            ls_responseMessage = xsd_cardTransacionResponse.getResponseMessage().getValue();
            ls_transactionDateTime = xsd_cardTransacionResponse.getTransactionDateTime().getValue();
            ls_transactionRefNumber = xsd_cardTransacionResponse.getTransactionRefNumber().getValue();

            /*
             * if response is 100 then success.
             * if response is 101 then transaction failed.
             */
            if (ls_responseCode != null && "100".equals(ls_responseCode)) {

                JSONObjectImpl StopSingleChequeJsonObj = new JSONObjectImpl();
                JSONObject responseJsonObject = new JSONObject();
                JSONArray StopSingleChequeJsonArray = new JSONArray();

                StopSingleChequeJsonObj.put("RESPONSECODE", ls_responseCode);
                StopSingleChequeJsonObj.put("RESPONSEMESSAGE", ls_responseMessage);
                StopSingleChequeJsonObj.put("TRANSACTIONDATETIME", ls_transactionDateTime);
                StopSingleChequeJsonObj.put("TRANSACTIONREFNUMBER", ls_transactionRefNumber);

                StopSingleChequeJsonArray.put(StopSingleChequeJsonObj);

                /*
                 * If transaction happens and API is called successfully.
                 */
                responseJsonObject.put("STATUS", "0");
                responseJsonObject.put("COLOR", "G");
                responseJsonObject.put("RESPONSE", StopSingleChequeJsonArray);
                responseJsonObject.put("MESSAGE", "");
                ls_responseData = responseJsonObject.toString();
            }else { 

				ls_langResCodeMsg=propConfig.getResponseCode("stopSingleChequeRequest."+ls_responseCode);			
				ls_responseData = common.ofGetErrDataJsonArray(ls_langResCodeMsg,
						propConfig.getMessageOfResCode("commen.title."+ls_langResCodeMsg, "Alert."), 
						propConfig.getMessageOfResCode("stopSingleChequeRequest."+ls_responseCode,"","(ENP234)"),
						ls_responseMessage, "Currently Service under maintenance so please try later (ENP234).", ls_responseCode, "R");            		            		            	
			}
        }catch (Exception exception) {
			ls_actualErrMsg = common.ofGetTotalErrString(exception, "");            
			loggerImpl.error(logger,"Exception : " + ls_actualErrMsg,"IN:stopSingleChequeRequest");

			ls_responseData = common.ofGetErrDataJsonArray("999",
					propConfig.getMessageOfResCode("commen.title.999", "Alert."),
					propConfig.getMessageOfResCode("commen.exception.999","","(ENP235)"),
					exception.getMessage(),"Currently Service under maintenance so please try later (ENP235)","0", "R");			

		}finally {
			loggerImpl.stopAndPrintOptLogs(logger,"All API called successfully.","IN:stopSingleChequeRequest");
			loggerImpl.info(logger,"Response generated and send to client.", "IN:stopSingleChequeRequest");        	
		}
        return ls_responseData;

    }
}
