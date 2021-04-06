package com.easynet.controller.ApiController;

import city.xsd.BeginChequeList;
import city.xsd.GetAllUnpaidChequeRequest;
import city.xsd.ObjectFactory;
import city.xsd.UnpaidChequeList;
import city.xsd.UnpaidChequeListResponse;
import com.easynet.configuration.PropConfiguration;
import com.easynet.configuration.SOAPConnector;

import com.easynet.impl.JSONObjectImpl;
import com.easynet.impl.LoggerImpl;
import com.easynet.util.common;
import static com.easynet.util.common.PrintErrLog;
import com.easynet.util.readXML;
import java.util.ArrayList;
import javax.xml.bind.JAXBElement;
import org.apache.ws.axis2.GetAllUnpaidChequeStatusResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.soap.client.SoapFaultClientException;

@Component
public class GetAllUnpaidChequeStatus {

    @Autowired
    private SOAPConnector soapConnector;

    static Logger logger=LoggerFactory.getLogger(GetAllUnpaidChequeStatus.class);
    
    @Autowired
    PropConfiguration propConfig;
    
    
    public String getAllUnpaidChequeStatus(String reqData) {

        String ls_password = "";
        String ls_userName = "";
        String ls_accountNumber = "";
        String ls_responseCode = "";
        String ls_responseMessage = "";
        String ls_responseData = "";
        String ls_actualErrMsg = "";
        String ls_langResCodeMsg="";
        ArrayList<BeginChequeList> beginChequeList;
        ArrayList<UnpaidChequeList> unpaidChequeList;
        LoggerImpl loggerImpl=null;
        
        try {
            
            loggerImpl=new LoggerImpl();
            
            loggerImpl.info(logger, "Preaparing request data and calling API", "IN:getAllUnpaidChequeStatus");
            loggerImpl.generateProfiler("getAllUnpaidChequeStatus");
            loggerImpl.startProfiler("Preparing request data.");
            
            
            JSONObject reqJsonObj = new JSONObject(reqData);
            ls_accountNumber = reqJsonObj.getString("ACCTNUMBER");

           
            
            if ((ls_accountNumber == null || "".equals(ls_accountNumber))) {

                ls_responseData = common.ofGetErrDataJsonArray("99",
						propConfig.getMessageOfResCode("commen.title.99", "Validation Failed."),
						propConfig.getMessageOfResCode("commen.invalid_req_data",""),		
						"Null values found in request data.", "Invalid Request.","", "R");
            }
            ls_userName = readXML.getXmlData("root>CUST_360>AUTHORIZATION>USER_NAME");
            ls_password = readXML.getXmlData("root>CUST_360>AUTHORIZATION>PASSWORD");

            //get the object factory object.
            city.xsd.ObjectFactory xsdObjectFactory = new ObjectFactory();
            //created request object.
            GetAllUnpaidChequeRequest getAllUnpaidChequeRequest = xsdObjectFactory.createGetAllUnpaidChequeRequest();

            loggerImpl.debug(logger,"Json to xml conversion done.", "getAllUnpaidChequeStatus");
            
            //setting all the values in the request.
            getAllUnpaidChequeRequest.setAccountNumber(xsdObjectFactory.createGetAllUnpaidChequeRequestAccountNumber(ls_accountNumber));
            getAllUnpaidChequeRequest.setPassword(xsdObjectFactory.createGetAllUnpaidChequeRequestPassword(ls_password));
            getAllUnpaidChequeRequest.setUsername(xsdObjectFactory.createGetAllUnpaidChequeRequestUsername(ls_userName));

            //get the object factory object of axis2.
            org.apache.ws.axis2.ObjectFactory axis2ObjectFactory = new org.apache.ws.axis2.ObjectFactory();
            //wrapping the request.
            org.apache.ws.axis2.GetAllUnpaidChequeStatus getallunpaidchequestatus = axis2ObjectFactory.createGetAllUnpaidChequeStatus();
            getallunpaidchequestatus.setRequest(axis2ObjectFactory.createGetAllUnpaidChequeStatusRequest(getAllUnpaidChequeRequest));
            
            loggerImpl.debug(logger,"getAllUnpaidChequeStatus API calling", "getAllUnpaidChequeStatus");
            loggerImpl.startProfiler("getAllUnpaidChequeStatus API calling.");
            
            //created the response object.
            GetAllUnpaidChequeStatusResponse getAllUnpaidChequeStatusResponse = null;
            try {
                getAllUnpaidChequeStatusResponse = (GetAllUnpaidChequeStatusResponse) soapConnector.callWebService(getallunpaidchequestatus);
            } catch (SoapFaultClientException soapException) {
				ls_actualErrMsg = soapException.getFaultStringOrReason();
				ls_responseData = common.ofGetErrDataJsonArray("999", 
						propConfig.getMessageOfResCode("commen.title.999", "Alert."),
						propConfig.getMessageOfResCode("commen.exception.999","","(ENP194)"),
						ls_actualErrMsg, "Currently Service under maintenance so please try later (ENP194)", "0", "R");

				ls_actualErrMsg = common.ofGetTotalErrString(soapException, "");
				loggerImpl.error(logger,"SoapFaultClientException : " + ls_actualErrMsg,"EX:getAllUnpaidChequeStatus");
				return ls_responseData;
			}
            
            loggerImpl.debug(logger,"getAllUnpaidChequeStatus API called successfully.", "getAllUnpaidChequeStatus",getAllUnpaidChequeStatusResponse);
            loggerImpl.startProfiler("preparing getAllUnpaidChequeStatus API response data.");

            //created the response object to get the data.
            city.xsd.GetAllUnpaidChequeStatusResponse xsd_getAllUnpaidChequeStatusResponse = getAllUnpaidChequeStatusResponse.getReturn().getValue();

            //getting all the data from the response object.
            ls_responseCode = xsd_getAllUnpaidChequeStatusResponse.getResponseCode().getValue();
            ls_responseMessage = xsd_getAllUnpaidChequeStatusResponse.getResponseMessage().getValue();

            /*
             * if response is 100 then success.
             * if response is 101 then transaction failed.
             */
            if (ls_responseCode != null && "100".equals(ls_responseCode)) {

                JSONArray AllUnpaidChequeStatusJsonArray = new JSONArray();
                JSONObject responseJsonObject = new JSONObject();

                UnpaidChequeListResponse unpaidChequeListResponse = xsd_getAllUnpaidChequeStatusResponse.getResponseData().getValue();
                beginChequeList = (ArrayList<BeginChequeList>) unpaidChequeListResponse.getBeginList();

                for (BeginChequeList beginChequeList1 : beginChequeList) {
                    unpaidChequeList = (ArrayList<UnpaidChequeList>) beginChequeList1.getChequeList();

                    for (UnpaidChequeList unpaidChequeList1 : unpaidChequeList) {
                        JSONObjectImpl AllUnpaidChequeStatusJsonObj = new JSONObjectImpl();
                        AllUnpaidChequeStatusJsonObj.put("LEAFNUMBER", unpaidChequeList1.getLeafNumber());
                        AllUnpaidChequeStatusJsonObj.put("LEAFSTATUS", unpaidChequeList1.getLeafStatus().getValue());
                        AllUnpaidChequeStatusJsonObj.put("BEGINNUMBER", beginChequeList1.getBeginNumber().getValue().trim());
                        AllUnpaidChequeStatusJsonObj.put("LEAFNO", beginChequeList1.getLeafnumber());

                        AllUnpaidChequeStatusJsonArray.put(AllUnpaidChequeStatusJsonObj);
                    }

                }


                /*
                 * If transaction happens and API is called successfully.
                 */
                responseJsonObject.put("STATUS", "0");
                responseJsonObject.put("COLOR", "G");
                responseJsonObject.put("RESPONSE", AllUnpaidChequeStatusJsonArray);
                responseJsonObject.put("RESPONSEMESSAGE", ls_responseMessage);
                responseJsonObject.put("RESPONSECODE", ls_responseCode);
                ls_responseData = responseJsonObject.toString();

            }else { 

				ls_langResCodeMsg=propConfig.getResponseCode("getAllUnpaidChequeStatus."+ls_responseCode);			
				ls_responseData = common.ofGetErrDataJsonArray(ls_langResCodeMsg,
						propConfig.getMessageOfResCode("commen.title."+ls_langResCodeMsg, "Alert."), 
						propConfig.getMessageOfResCode("getAllUnpaidChequeStatus."+ls_responseCode,"","(ENP195)"),
						ls_responseMessage, "Currently Service under maintenance so please try later (ENP195).", ls_responseCode, "R");            		            		            	
			}
        } catch (Exception exception) {
			ls_actualErrMsg = common.ofGetTotalErrString(exception, "");            
			loggerImpl.error(logger,"Exception : " + ls_actualErrMsg,"IN:getAllUnpaidChequeStatus");

			ls_responseData = common.ofGetErrDataJsonArray("999",
					propConfig.getMessageOfResCode("commen.title.999", "Alert."),
					propConfig.getMessageOfResCode("commen.exception.999","","(ENP196)"),
					exception.getMessage(),"Currently Service under maintenance so please try later (ENP196)","0", "R");			

		}finally {
        	loggerImpl.stopAndPrintOptLogs(logger,"All API called successfully.","getAllUnpaidChequeStatus");
        	loggerImpl.info(logger,"Response generated and send to client.", "getAllUnpaidChequeStatus");        	
        }
        return ls_responseData;
    }
}
