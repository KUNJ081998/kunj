package com.easynet.controller.ApiController;

import city.xsd.GetWASABillAmountRequest;
import city.xsd.WASABillInfo;
import com.easynet.configuration.PropConfiguration;
import com.easynet.configuration.SOAPConnector;
import com.easynet.impl.JSONObjectImpl;
import com.easynet.impl.LoggerImpl;
import com.easynet.util.common;
import static com.easynet.util.common.PrintErrLog;
import com.easynet.util.readXML;
import org.apache.ws.axis2.GetWASABillAmountResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.soap.client.SoapFaultClientException;

@Component
public class GetWASABillAmount {

    static Logger logger=LoggerFactory.getLogger(GetWASABillAmount.class);
    @Autowired
    private SOAPConnector soapConnector;

    @Autowired
    PropConfiguration propConfig;
    public String getWASABillAmount(String reqData) {

        String ls_billId = "";
        String ls_password = "";
        String ls_stockId = "";
        String ls_userName = "";
        String ls_utilityId = "";
        String ls_responseCode = "";
        String ls_responseData = "";
        String ls_responseMessage = "";
        String ls_actualErrMsg = "";
        String ls_langResCodeMsg="";
        LoggerImpl loggerImpl=null;

        try {
            loggerImpl=new LoggerImpl();
        
            loggerImpl.info(logger,"Preparing requset data and calling API.","IN:getWASABillAmount");
            loggerImpl.generateProfiler("getWASABillAmount");
            loggerImpl.startProfiler("Preparing request data");
            
            
            JSONObject reqJsonObj = new JSONObject(reqData);
            ls_billId = reqJsonObj.getString("BILLID");
            ls_stockId = reqJsonObj.getString("STOCKID");
            ls_utilityId = reqJsonObj.getString("UTILITYID");

            if ((ls_billId == null || "".equals(ls_billId)) || (ls_stockId == null || "".equals(ls_stockId)) || (ls_utilityId == null || "".equals(ls_utilityId))) {

               ls_responseData = common.ofGetErrDataJsonArray("99",
						propConfig.getMessageOfResCode("commen.title.99", "Validation Failed."),
						propConfig.getMessageOfResCode("commen.invalid_req_data",""),		
						"Null values found in request data.", "Invalid Request.","", "R");

				return ls_responseData;
            }
            ls_userName = readXML.getXmlData("root>CUST_360>AUTHORIZATION>USER_NAME");
            ls_password = readXML.getXmlData("root>CUST_360>AUTHORIZATION>PASSWORD");

            //created object factory object.
            city.xsd.ObjectFactory xsdObjectFactory = new city.xsd.ObjectFactory();
            //created object of GetWASABillAmountRequest.
            GetWASABillAmountRequest getWASABillAmountRequest = xsdObjectFactory.createGetWASABillAmountRequest();
            
            loggerImpl.debug(logger,"Json to xml conversion done.", "IN:getWASABillAmount");
            
            //setting all the values in the request.
            getWASABillAmountRequest.setBillId(xsdObjectFactory.createGetWASABillAmountRequestBillId(ls_billId));
            getWASABillAmountRequest.setPassword(xsdObjectFactory.createGetWASABillAmountRequestPassword(ls_password));
            getWASABillAmountRequest.setStockId(xsdObjectFactory.createGetWASABillAmountRequestStockId(ls_stockId));
            getWASABillAmountRequest.setUsername(xsdObjectFactory.createGetWASABillAmountRequestUsername(ls_userName));
            getWASABillAmountRequest.setUtilityId(xsdObjectFactory.createGetWASABillAmountRequestUtilityId(ls_utilityId));

            //created object factory object of ws.axis2.
            org.apache.ws.axis2.ObjectFactory axis2ObjectFactory = new org.apache.ws.axis2.ObjectFactory();
            //wrapping the request in GetWASABillAmount.
            org.apache.ws.axis2.GetWASABillAmount getWASABillAmount = axis2ObjectFactory.createGetWASABillAmount();
            getWASABillAmount.setRequest(axis2ObjectFactory.createGetWASABillAmountRequest(getWASABillAmountRequest));
            
            loggerImpl.debug(logger,"getWASABillAmount API calling", "IN:getWASABillAmount");
            loggerImpl.startProfiler("getWASABillAmount API calling.");

            //created response object of GetWASABillAmountResponse.
            GetWASABillAmountResponse getWASABillAmountResponse = null;
            try {
                getWASABillAmountResponse = (GetWASABillAmountResponse) soapConnector.callWebService(getWASABillAmount);
            }catch (SoapFaultClientException soapException) {
				ls_actualErrMsg = soapException.getFaultStringOrReason();
				ls_responseData = common.ofGetErrDataJsonArray("999", 
						propConfig.getMessageOfResCode("commen.title.999", "Alert."),
						propConfig.getMessageOfResCode("commen.exception.999","","(ENP224)"),
						ls_actualErrMsg, "Currently Service under maintenance so please try later (ENP224)", "0", "R");

				ls_actualErrMsg = common.ofGetTotalErrString(soapException, "");
				loggerImpl.error(logger,"SoapFaultClientException : " + ls_actualErrMsg,"EX:getWASABillAmount");
				return ls_responseData;
			}
            
            loggerImpl.debug(logger,"getWASABillAmount API called successfully.", "IN:getWASABillAmount",getWASABillAmountResponse);
            loggerImpl.startProfiler("preparing getWASABillAmount API response data.");

            //created GetWASABillAmountResponse object of city.xsd.
            city.xsd.GetWASABillAmountResponse xsd_getWASABillAmountResponse = getWASABillAmountResponse.getReturn().getValue();

            //getting all the values from the response data.
            ls_responseCode = xsd_getWASABillAmountResponse.getResponseCode().getValue();
            ls_responseMessage = xsd_getWASABillAmountResponse.getResponseMessage().getValue();
         

            /*if response is 100 then success.
	     *If response is 101 then no records found.
             */
            if (ls_responseCode != null && "100".equals(ls_responseCode)) {

                JSONObjectImpl GetWASABillAmountJsonObj = new JSONObjectImpl();
                JSONObject responseJsonObject = new JSONObject();
                JSONArray GetWASABillAmountJsonArray = new JSONArray();

                WASABillInfo WASAbillinfo = xsd_getWASABillAmountResponse.getResponseData().getValue();
               
                GetWASABillAmountJsonObj.put("LPC", "");
                GetWASABillAmountJsonObj.put("DUEDATE", "");
                GetWASABillAmountJsonObj.put("ORGANIZATIONCODE", "");
                GetWASABillAmountJsonObj.put("ACCOUNTNO", WASAbillinfo.getAccountNo().getValue());
                GetWASABillAmountJsonObj.put("MESSAGE", WASAbillinfo.getMessage().getValue());
                GetWASABillAmountJsonObj.put("OTHERONE", WASAbillinfo.getOtherOne().getValue());
                GetWASABillAmountJsonObj.put("OTHERTWO", WASAbillinfo.getOtherTwo().getValue());
                GetWASABillAmountJsonObj.put("REQUESTSTATUS", WASAbillinfo.getRequestStatus().getValue());
                GetWASABillAmountJsonObj.put("TOTALAMOUNT", WASAbillinfo.getTotalAmount().getValue());
                GetWASABillAmountJsonObj.put("VATAMOUNT", WASAbillinfo.getVatAmount().getValue());
               
                GetWASABillAmountJsonArray.put(GetWASABillAmountJsonObj);

                /* If response is 100 and API is called successfully. */
                responseJsonObject.put("STATUS", "0");
                responseJsonObject.put("COLOR", "G");
                responseJsonObject.put("RESPONSE", GetWASABillAmountJsonArray);
                responseJsonObject.put("MESSAGE", "");
                responseJsonObject.put("RESPONSECODE", ls_responseCode);
                responseJsonObject.put("RESPONSEMESSAGE", ls_responseMessage);
                ls_responseData = responseJsonObject.toString();
            } else { 

				ls_langResCodeMsg=propConfig.getResponseCode("getWASABillAmount."+ls_responseCode);			
				ls_responseData = common.ofGetErrDataJsonArray(ls_langResCodeMsg,
						propConfig.getMessageOfResCode("commen.title."+ls_langResCodeMsg, "Alert."), 
						propConfig.getMessageOfResCode("getWASABillAmount."+ls_responseCode,"","(ENP225)"),
						ls_responseMessage, "Currently Service under maintenance so please try later (ENP225).", ls_responseCode, "R");            		            		            	
			}
        }catch (Exception exception) {
			ls_actualErrMsg = common.ofGetTotalErrString(exception, "");            
			loggerImpl.error(logger,"Exception : " + ls_actualErrMsg,"IN:getWASABillAmount");

			ls_responseData = common.ofGetErrDataJsonArray("999",
					propConfig.getMessageOfResCode("commen.title.999", "Alert."),
					propConfig.getMessageOfResCode("commen.exception.999","","(ENP226)"),
					exception.getMessage(),"Currently Service under maintenance so please try later (ENP226)","0", "R");			

		}finally {
			loggerImpl.stopAndPrintOptLogs(logger,"All API called successfully.","IN:getWASABillAmount");
			loggerImpl.info(logger,"Response generated and send to client.", "IN:getWASABillAmount");        	
		}
        return ls_responseData;

    }
}
