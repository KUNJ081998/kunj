package com.easynet.controller.ApiController;

import com.easynet.configuration.PropConfiguration;
import com.easynet.configuration.SOAPConnector;
// import static com.easynet.controller.ApiController.DoMobileTopup.logger;
import com.easynet.impl.JSONObjectImpl;
import com.easynet.impl.LoggerImpl;
import com.easynet.util.common;
import static com.easynet.util.common.PrintErrLog;
import com.easynet.util.readXML;
import fi.xsd.DoFinacleTransactionRequest;
import fi.xsd.ObjectFactory;
import fi.xsd.PartTrnRec;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.ws.axis2.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.soap.client.SoapFaultClientException;

    @Component
public class FinacleTransactionFI {

     static Logger logger=LoggerFactory.getLogger(FinacleTransactionFI.class);
    @Autowired
    private SOAPConnector soapConnector;
    
    @Autowired
    private PropConfiguration propConfig;

    public String dofinacleTransactionFI(String requestData) {

        JSONArray ls_partTrnRecList=new JSONArray();
        String ls_password = "";
        String ls_trnSubType = "";
        String ls_trnType = "";
        String ls_userName = "";
        String ls_acctId = "";
        String ls_creditDebitFlg = "";
        String ls_partTrnRmks = "";
        String ls_trnAmtAmountValue = "";
        String ls_trnAmtCurrencyCode = "";
        String ls_trnParticulars = "";
        String ls_trnParticulars2 = "";
        String ls_valueDt = "";
        String ls_responseData = "";
        String ls_responseCode = "";
        String ls_responseMessage = "";
        String transactionDate = "";
        String transactionId = "";
        String ls_actualErrMsg = "";
        String ls_langResCodeMsg= "";
        LoggerImpl loggerImpl=null;
      
        try {
            
            loggerImpl=new LoggerImpl();
            
            loggerImpl.info(logger, "Preaparing request data and calling API", "IN:dofinacleTransactionFI");
            loggerImpl.generateProfiler("dofinacleTransactionFI");
            loggerImpl.startProfiler("Preparing request data.");
            
            
            JSONObject requestJsonObj = new JSONObject(requestData);
            ls_partTrnRecList=requestJsonObj.getJSONArray("PARTTRNRECLIST");
            ls_trnSubType = requestJsonObj.getString("TRNSUBTYPE");
            ls_trnType = requestJsonObj.getString("TRNTYPE");
            //JSONArray jarray=new JSONArray(ls_partTrnRecList);
            
            if ((ls_partTrnRecList == null || "".equals(ls_partTrnRecList)) || (ls_trnSubType == null || "".equals(ls_trnSubType)) || (ls_trnType == null || "".equals(ls_trnType))) {
                ls_responseData = common.ofGetErrDataJsonArray("99",
						propConfig.getMessageOfResCode("commen.title.99", "Validation Failed."),
						propConfig.getMessageOfResCode("commen.invalid_req_data",""),		
						"Null values found in request data.", "Invalid Request.","", "R");
            }
            ls_userName = readXML.getXmlData("root>CUST_360>AUTHORIZATION>USER_NAME");
            ls_password = readXML.getXmlData("root>CUST_360>AUTHORIZATION>PASSWORD");
            
            

            //get the object factory object
            fi.xsd.ObjectFactory xsdObjectFactory = new fi.xsd.ObjectFactory();
            //created do fincale transaction request object from object factory.
            DoFinacleTransactionRequest doFinacleTransactionrequest = xsdObjectFactory.createDoFinacleTransactionRequest();
            PartTrnRec partTrnRec = xsdObjectFactory.createPartTrnRec();
            
            loggerImpl.debug(logger,"Json to xml conversion done.", "dofinacleTransactionFI");
            
            //setting all the values in do finacle transaction request
            doFinacleTransactionrequest.setPassword(xsdObjectFactory.createDoFinacleTransactionRequestPassword(ls_password));
            doFinacleTransactionrequest.setTrnSubType(xsdObjectFactory.createDoFinacleTransactionRequestTrnSubType(ls_trnSubType));
            doFinacleTransactionrequest.setTrnType(xsdObjectFactory.createDoFinacleTransactionRequestTrnType(ls_trnType));
            doFinacleTransactionrequest.setUsername(xsdObjectFactory.createDoFinacleTransactionRequestUsername(ls_userName));
            ArrayList<PartTrnRec> parttrn = (ArrayList<PartTrnRec>) doFinacleTransactionrequest.getPartTrnRecList();
            
            
            for(int i=0;i < ls_partTrnRecList.length();i++){
            
                JSONObject parttrnlistJsonObj=ls_partTrnRecList.getJSONObject(i);
                ls_acctId=parttrnlistJsonObj.getString("ACCTID");
                ls_creditDebitFlg=parttrnlistJsonObj.getString("CREDITDEBITFLG");
                ls_partTrnRmks=parttrnlistJsonObj.getString("PARTTRNRMKS");
                ls_trnAmtAmountValue=parttrnlistJsonObj.getString("TRNAMTAMOUNTVALUE");
                ls_trnAmtCurrencyCode=parttrnlistJsonObj.getString("TRNAMTCURRENCYCODE");
                ls_trnParticulars=parttrnlistJsonObj.getString("TRNPARTICULARS");
                ls_trnParticulars2=parttrnlistJsonObj.getString("TRNPARTICULARS2");
                ls_valueDt=parttrnlistJsonObj.getString("VALUEDT");
                
                
                  
            if ((ls_acctId == null || "".equals(ls_acctId)) || (ls_creditDebitFlg == null || "".equals(ls_creditDebitFlg)) || (ls_partTrnRmks == null || "".equals(ls_partTrnRmks))
                    || (ls_trnAmtAmountValue==null || "".equals(ls_trnAmtAmountValue)) || (ls_trnAmtCurrencyCode==null || "".equals(ls_trnAmtCurrencyCode))
                    || (ls_trnParticulars==null || "".equals(ls_trnParticulars)) || (ls_trnParticulars2==null || "".equals(ls_trnParticulars2)) || (ls_valueDt==null || "".equals(ls_valueDt))) {
                
                ls_responseData = common.ofGetErrDataJsonArray("99",
						propConfig.getMessageOfResCode("commen.title.99", "Validation Failed."),
						propConfig.getMessageOfResCode("commen.invalid_req_data",""),		
						"Null values found in request data.", "Invalid Request.","", "R");
            }
            

                
                 //setting all the values in the request part trn rec.
            partTrnRec.setAcctId(xsdObjectFactory.createPartTrnRecAcctId(ls_acctId));
            partTrnRec.setCreditDebitFlg(xsdObjectFactory.createPartTrnRecCreditDebitFlg(ls_creditDebitFlg));
            partTrnRec.setPartTrnRmks(xsdObjectFactory.createPartTrnRecPartTrnRmks(ls_partTrnRmks));
            partTrnRec.setTrnAmtAmountValue(xsdObjectFactory.createPartTrnRecTrnAmtAmountValue(ls_trnAmtAmountValue));
            partTrnRec.setTrnAmtCurrencyCode(xsdObjectFactory.createPartTrnRecTrnAmtCurrencyCode(ls_trnAmtCurrencyCode));
            partTrnRec.setTrnParticulars(xsdObjectFactory.createPartTrnRecTrnParticulars(ls_trnParticulars));
            partTrnRec.setTrnParticulars2(xsdObjectFactory.createPartTrnRecTrnParticulars2(ls_trnParticulars2));
            partTrnRec.setValueDt(xsdObjectFactory.createPartTrnRecValueDt(ls_valueDt));

            //setting the values in the request do fincale transaction request
           
            parttrn.add(partTrnRec);
            
            
            }
            
            org.apache.ws.axis2.ObjectFactory axis2ObjectFactory = new org.apache.ws.axis2.ObjectFactory();
            //created do finacle transaction fi object to store the request
            DoFinacleTransactionFI dofinacletransactionfi = axis2ObjectFactory.createDoFinacleTransactionFI();
            dofinacletransactionfi.setRequest(axis2ObjectFactory.createDoFinacleTransactionFIRequest(doFinacleTransactionrequest));
            
            loggerImpl.debug(logger,"dofinacleTransactionFI API calling", "dofinacleTransactionFI");
            loggerImpl.startProfiler("dofinacleTransactionFI API calling.");
            
            //called API with request data and get response object.
            DoFinacleTransactionFIResponse doFinacleTransactionFIresponse = null;
            try {
                doFinacleTransactionFIresponse = (DoFinacleTransactionFIResponse) soapConnector.callWebService(dofinacletransactionfi);
            } catch (SoapFaultClientException soapException) {
				ls_actualErrMsg = soapException.getFaultStringOrReason();
				ls_responseData = common.ofGetErrDataJsonArray("999", 
						propConfig.getMessageOfResCode("commen.title.999", "Alert."),
						propConfig.getMessageOfResCode("commen.exception.999","","(ENP164)"),
						ls_actualErrMsg, "Currently Service under maintenance so please try later (ENP164)", "0", "R");

				ls_actualErrMsg = common.ofGetTotalErrString(soapException, "");
				loggerImpl.error(logger,"SoapFaultClientException : " + ls_actualErrMsg,"EX:dofinacleTransactionFI");
				return ls_responseData;
			}
            
            loggerImpl.debug(logger,"dofinacleTransactionFI API called successfully.", "dofinacleTransactionFI",doFinacleTransactionFIresponse);
            loggerImpl.startProfiler("preparing dofinacleTransactionFI API response data.");

            //created do finacle transaction response object
            fi.xsd.DoFinacleTransactionResponse xsd_dofinacletransactionresponse = doFinacleTransactionFIresponse.getReturn().getValue();

            //getting all the values from the response object.
            ls_responseCode = xsd_dofinacletransactionresponse.getResponseCode().getValue();
            ls_responseMessage = xsd_dofinacletransactionresponse.getResponseMessage().getValue();
            transactionDate = xsd_dofinacletransactionresponse.getTransactionDate().getValue();
            transactionId = xsd_dofinacletransactionresponse.getTransactionId().getValue();

            /*
            *if response is 100 then success.
            *if response is other than 100 then transaction failed.
             */
            if (ls_responseCode != null && "100".equals(ls_responseCode)) {
                JSONObjectImpl dofinacletransactionresponseJsonObj = new JSONObjectImpl();
                JSONObject responseJsonObject = new JSONObject();
                JSONArray dofinacletransactionresponseJsonArray = new JSONArray();

                dofinacletransactionresponseJsonObj.put("RESPONSECODE", ls_responseCode);
                dofinacletransactionresponseJsonObj.put("RESPONSEMESSAGE", ls_responseMessage);
                dofinacletransactionresponseJsonObj.put("TRANSACTIONDATE", transactionDate);
                dofinacletransactionresponseJsonObj.put("TRANSACTIONID", transactionId);
                dofinacletransactionresponseJsonArray.put(dofinacletransactionresponseJsonObj);

                responseJsonObject.put("STATUS", "0");
                responseJsonObject.put("COLOR", "G");
                responseJsonObject.put("RESPONSE", dofinacletransactionresponseJsonArray);
                responseJsonObject.put("MESSAGE", "");
                ls_responseData = responseJsonObject.toString();

            } else { 

				ls_langResCodeMsg=propConfig.getResponseCode("dofinacleTransactionFI."+ls_responseCode);			
				ls_responseData = common.ofGetErrDataJsonArray(ls_langResCodeMsg,
						propConfig.getMessageOfResCode("commen.title."+ls_langResCodeMsg, "Alert."), 
						propConfig.getMessageOfResCode("dofinacleTransactionFI."+ls_responseCode,"","(ENP165)"),
						ls_responseMessage, "Currently Service under maintenance so please try later (ENP165).", ls_responseCode, "R");            		            		            	
			}
        } catch (Exception exception) {
			ls_actualErrMsg = common.ofGetTotalErrString(exception, "");            
			loggerImpl.error(logger,"Exception : " + ls_actualErrMsg,"IN:dofinacleTransactionFI");

			ls_responseData = common.ofGetErrDataJsonArray("999",
					propConfig.getMessageOfResCode("commen.title.999", "Alert."),
					propConfig.getMessageOfResCode("commen.exception.999","","(ENP166)"),
					exception.getMessage(),"Currently Service under maintenance so please try later (ENP166)","0", "R");			

		}finally {
        	loggerImpl.stopAndPrintOptLogs(logger,"All API called successfully.","dofinacleTransactionFI");
        	loggerImpl.info(logger,"Response generated and send to client.", "dofinacleTransactionFI");        	
        }
        return ls_responseData;

    }

}
