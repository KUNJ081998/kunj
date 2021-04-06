package com.easynet.controller.ApiController;

import com.easynet.configuration.PropConfiguration;
import com.easynet.configuration.SOAPConnector;

import com.easynet.impl.JSONObjectImpl;
import com.easynet.impl.LoggerImpl;
import com.easynet.util.common;
import static com.easynet.util.common.PrintErrLog;
import com.easynet.util.readXML;
import fi.xsd.DoFinacleTranReversalRequest;
import fi.xsd.DoFinacleTranReversalResponse;
import fi.xsd.ObjectFactory;
import fi.xsd.RevTrnIdRec;
import java.util.ArrayList;
import org.apache.ws.axis2.DoFinacleTransactionReversalFIResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.profiler.Profiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.soap.client.SoapFaultClientException;

@Component
public class DoFinacleTransactionReversalFI {

    @Autowired
    private SOAPConnector soapConnector;

    static Logger logger = LoggerFactory.getLogger(DoFinacleTransactionReversalFI.class);
    
    @Autowired
    PropConfiguration propConfig;

    public String doFinacleTransactionReversalFI(String reqData) {

        String ls_password = "";
        String ls_userName = "";
        String ls_tranDate = "";
        String ls_tranId = "";
        String ls_responseCode = "";
        String ls_responseMessage = "";
        String ls_responseData = "";
        String ls_actualErrMsg = "";
        String ls_langResCodeMsg="";
        ArrayList<RevTrnIdRec> RevTrnRecList;
        LoggerImpl loggerImpl = null;

        try {
            loggerImpl = new LoggerImpl();

            loggerImpl.info(logger, "Preaparing request data and calling API", "IN:doFinacleTransactionReversalFI");
            loggerImpl.generateProfiler("doFinacleTransactionReversalFI");
            loggerImpl.startProfiler("Preparing request data.");


            JSONObject reqJsonObj = new JSONObject(reqData);
            ls_tranDate = reqJsonObj.getString("TRANDATE");
            ls_tranId = reqJsonObj.getString("TRANID");

            

            if ((ls_tranDate == null || "".equals(ls_tranDate)) || (ls_tranId == null || "".equals(ls_tranId))) {

                 ls_responseData = common.ofGetErrDataJsonArray("99",
						propConfig.getMessageOfResCode("commen.title.99", "Validation Failed."),
						propConfig.getMessageOfResCode("commen.invalid_req_data",""),		
						"Null values found in request data.", "Invalid Request.","", "R");
            }
            ls_userName = readXML.getXmlData("root>CUST_360>AUTHORIZATION>USER_NAME");
            ls_password = readXML.getXmlData("root>CUST_360>AUTHORIZATION>PASSWORD");

            //get object factory object.
            fi.xsd.ObjectFactory xsdObjectFactory = new ObjectFactory();
            //created request object.
            DoFinacleTranReversalRequest doFinacleTranReversalRequest = xsdObjectFactory.createDoFinacleTranReversalRequest();

            loggerImpl.debug(logger, "Json to xml conversion done.", "doFinacleTransactionReversalFI");

            //setting the values in the request.
            doFinacleTranReversalRequest.setPassword(xsdObjectFactory.createDoFinacleTranReversalRequestPassword(ls_password));
            doFinacleTranReversalRequest.setTranDate(xsdObjectFactory.createDoFinacleTranReversalRequestTranDate(ls_tranDate));
            doFinacleTranReversalRequest.setTranId(xsdObjectFactory.createDoFinacleTranReversalRequestTranId(ls_tranId));
            doFinacleTranReversalRequest.setUsername(xsdObjectFactory.createDoFinacleTranReversalRequestUsername(ls_userName));

            //get the object factory object.
            org.apache.ws.axis2.ObjectFactory axis2ObjectFactory = new org.apache.ws.axis2.ObjectFactory();
            //wrapping the request object in the request.
            org.apache.ws.axis2.DoFinacleTransactionReversalFI dofinacletransactionreversalfi = axis2ObjectFactory.createDoFinacleTransactionReversalFI();
            dofinacletransactionreversalfi.setRequest(axis2ObjectFactory.createDoFinacleTransactionReversalFIRequest(doFinacleTranReversalRequest));

            loggerImpl.debug(logger, "doFinacleTransactionReversalFI API calling", "doFinacleTransactionReversalFI");
            loggerImpl.startProfiler("doFinacleTransactionReversalFI API calling.");

            //created response object.
            DoFinacleTransactionReversalFIResponse doFinacleTransactionReversalFIResponse = null;
            try {
                doFinacleTransactionReversalFIResponse = (DoFinacleTransactionReversalFIResponse) soapConnector.callWebService(dofinacletransactionreversalfi);
            } catch (SoapFaultClientException soapException) {
				ls_actualErrMsg = soapException.getFaultStringOrReason();
				ls_responseData = common.ofGetErrDataJsonArray("999", 
						propConfig.getMessageOfResCode("commen.title.999", "Alert."),
						propConfig.getMessageOfResCode("commen.exception.999","","(ENP179)"),
						ls_actualErrMsg, "Currently Service under maintenance so please try later (ENP179)", "0", "R");

				ls_actualErrMsg = common.ofGetTotalErrString(soapException, "");
				loggerImpl.error(logger,"SoapFaultClientException : " + ls_actualErrMsg,"EX:doFinacleTransactionReversalFI");
				return ls_responseData;
			}

            loggerImpl.debug(logger, "doFinacleTransactionReversalFI API called successfully.", "doFinacleTransactionReversalFI",doFinacleTransactionReversalFIResponse);
            loggerImpl.startProfiler("preparing doFinacleTransactionReversalFI API response data.");

            //created response object.
            DoFinacleTranReversalResponse xsd_dofinacleTranReversalResponse = doFinacleTransactionReversalFIResponse.getReturn().getValue();

            //getting all the data from the values.
            ls_responseCode = xsd_dofinacleTranReversalResponse.getResponseCode().getValue();
            ls_responseMessage = xsd_dofinacleTranReversalResponse.getResponseMessage().getValue();

            /*
            *if response is 100 then success.
            *if response is other than 100 then transaction failed.
             */
           

                JSONObject responseJsonObject = new JSONObject();
                JSONArray dofinacletransactionreversalresponseJsonArray = new JSONArray();

                RevTrnRecList = (ArrayList<RevTrnIdRec>) xsd_dofinacleTranReversalResponse.getRevTrnIdRecList();
                for (RevTrnIdRec revTrnIdRec : RevTrnRecList) {

                    JSONObjectImpl dofinacletransactionreversalresponseJsonObj = new JSONObjectImpl();
                    dofinacletransactionreversalresponseJsonObj.put("REVTRNID", revTrnIdRec.getRevTrnId().getValue());

                    dofinacletransactionreversalresponseJsonArray.put(dofinacletransactionreversalresponseJsonObj);

                }
                responseJsonObject.put("STATUS", "0");
                responseJsonObject.put("COLOR", "G");
                responseJsonObject.put("RESPONSE", dofinacletransactionreversalresponseJsonArray);
                responseJsonObject.put("RESPONSEMESSAGE", ls_responseMessage);
                responseJsonObject.put("RESPONSECODE", ls_responseCode);
                ls_responseData = responseJsonObject.toString();

            
        } catch (Exception exception) {
			ls_actualErrMsg = common.ofGetTotalErrString(exception, "");            
			loggerImpl.error(logger,"Exception : " + ls_actualErrMsg,"IN:doFinacleTransactionReversalFI");

			ls_responseData = common.ofGetErrDataJsonArray("999",
					propConfig.getMessageOfResCode("commen.title.999", "Alert."),
					propConfig.getMessageOfResCode("commen.exception.999","","(ENP181)"),
					exception.getMessage(),"Currently Service under maintenance so please try later (ENP181)","0", "R");			

		}finally {
        	loggerImpl.stopAndPrintOptLogs(logger,"All API called successfully.","doFinacleTransactionReversalFI");
        	loggerImpl.info(logger,"Response generated and send to client.", "doFinacleTransactionReversalFI");        	
        }
        return ls_responseData;

    }
}
