package com.easynet.controller.ApiController;

import city.xsd.DESCOBillInfo;
import city.xsd.GetDESCOBillAmountRequest;
import com.easynet.configuration.PropConfiguration;
import com.easynet.configuration.SOAPConnector;
import static com.easynet.controller.ApiController.DoMobileTopup.logger;
import com.easynet.impl.JSONObjectImpl;
import com.easynet.impl.LoggerImpl;
import com.easynet.util.common;
import static com.easynet.util.common.PrintErrLog;
import com.easynet.util.readXML;
import org.apache.ws.axis2.GetDESCOBillAmountResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.soap.client.SoapFaultClientException;

@Component
public class GetDESCOBillAmount {

    static Logger logger=LoggerFactory.getLogger(GetDESCOBillAmount.class);
    @Autowired
    private SOAPConnector soapConnector;
    
    @Autowired
    PropConfiguration propConfig;
    
    public String getDESCOBillAmount(String reqData) {

        String ls_billId = "";
        String ls_password = "";
        String ls_stockId = "";
        String ls_userName = "";
        String ls_utilityId = "";
        String ls_responseCode = "";
        String ls_responseData = "";
        String ls_responseMessage = "";
        String ls_actualErrMsg = "";
        String   ls_langResCodeMsg="";
        LoggerImpl loggerImpl=null;

        try {
            loggerImpl=new LoggerImpl();
            
            loggerImpl.info(logger, "Preaparing request data and calling API", "IN:getDESCOBillAmount");
            loggerImpl.generateProfiler("getDESCOBillAmount");
            loggerImpl.startProfiler("Preparing request data.");
            
            
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
            //created GetDESCOBillAmount request object from object factory.
            GetDESCOBillAmountRequest getDESCOBillAmountRequest = xsdObjectFactory.createGetDESCOBillAmountRequest();
            
            loggerImpl.debug(logger,"Json to xml conversion done.", "IN:getDESCOBillAmount");
            
            //setting all the values in required fields.
            getDESCOBillAmountRequest.setBillId(xsdObjectFactory.createGetDESCOBillAmountRequestBillId(ls_billId));
            getDESCOBillAmountRequest.setPassword(xsdObjectFactory.createGetDESCOBillAmountRequestPassword(ls_password));
            getDESCOBillAmountRequest.setStockId(xsdObjectFactory.createGetDESCOBillAmountRequestStockId(ls_stockId));
            getDESCOBillAmountRequest.setUsername(xsdObjectFactory.createGetDESCOBillAmountRequestUsername(ls_userName));
            getDESCOBillAmountRequest.setUtilityId(xsdObjectFactory.createGetDESCOBillAmountRequestUtilityId(ls_utilityId));

            //created object factory object of ws.axis2.
            org.apache.ws.axis2.ObjectFactory axis2ObjectFactory = new org.apache.ws.axis2.ObjectFactory();
            //wrapping the request in GetDESCOBillAmount.
            org.apache.ws.axis2.GetDESCOBillAmount getDESCOBillamount = axis2ObjectFactory.createGetDESCOBillAmount();
            getDESCOBillamount.setRequest(axis2ObjectFactory.createGetDESCOBillAmountRequest(getDESCOBillAmountRequest));
            
            loggerImpl.debug(logger,"getDESCOBillAmount API calling", "IN:getDESCOBillAmount");
            loggerImpl.startProfiler("getDESCOBillAmount API calling.");

            //created GetDESCOBillAmountResponse object.
            GetDESCOBillAmountResponse getDESCOBillAmountResponse = null;
            try {
                getDESCOBillAmountResponse = (GetDESCOBillAmountResponse) soapConnector.callWebService(getDESCOBillamount);
            } catch (SoapFaultClientException soapException) {
				ls_actualErrMsg = soapException.getFaultStringOrReason();
				ls_responseData = common.ofGetErrDataJsonArray("999", 
						propConfig.getMessageOfResCode("commen.title.999", "Alert."),
						propConfig.getMessageOfResCode("commen.exception.999","","(ENP209)"),
						ls_actualErrMsg, "Currently Service under maintenance so please try later (ENP209)", "0", "R");

				ls_actualErrMsg = common.ofGetTotalErrString(soapException, "");
				loggerImpl.error(logger,"SoapFaultClientException : " + ls_actualErrMsg,"EX:getDESCOBillAmount");
				return ls_responseData;
			}
            
            loggerImpl.debug(logger,"getDESCOBillAmount API called successfully.", "IN:getDESCOBillAmount",getDESCOBillAmountResponse);
            loggerImpl.startProfiler("preparing getDESCOBillAmount API response data.");

            //created GetDESCOBillAmountResponse object of city.xsd.
            city.xsd.GetDESCOBillAmountResponse xsd_getDESCOBillAmountResponse = getDESCOBillAmountResponse.getReturn().getValue();

            //getting all the data from the response.
            ls_responseCode = xsd_getDESCOBillAmountResponse.getResponseCode().getValue();
            ls_responseMessage = xsd_getDESCOBillAmountResponse.getResponseMessage().getValue();
           
            /*if response is 100 then success.
	     *If response is 101 then no records found.
             */
            if (ls_responseCode != null && "100".equals(ls_responseCode)) {

                JSONObjectImpl GetDESCOBillAmountJsonObj = new JSONObjectImpl();
                JSONObject responseJsonObject = new JSONObject();
                JSONArray GetDESCOBillAmountJsonArray = new JSONArray();

                DESCOBillInfo DESCObillinfo = xsd_getDESCOBillAmountResponse.getResponseData().getValue();
                GetDESCOBillAmountJsonObj.put("DUEDATE", DESCObillinfo.getDueDate().getValue());
                GetDESCOBillAmountJsonObj.put("LPC", DESCObillinfo.getLpc().getValue());
                GetDESCOBillAmountJsonObj.put("MESSAGE", DESCObillinfo.getMessage().getValue());
                GetDESCOBillAmountJsonObj.put("NETAMOUNT", DESCObillinfo.getNetAmount().getValue());
                GetDESCOBillAmountJsonObj.put("ORGANIZATIONCODE", DESCObillinfo.getOrganizationCode().getValue());
                GetDESCOBillAmountJsonObj.put("SETTLEMENTACCOUNT", DESCObillinfo.getSettlementAccount().getValue());
                GetDESCOBillAmountJsonObj.put("STATUS", DESCObillinfo.getStatus().getValue());
                GetDESCOBillAmountJsonObj.put("TOTALPAYABLEAMOUNT", DESCObillinfo.getTotalPayableAmount().getValue());
                GetDESCOBillAmountJsonObj.put("UTILITYACCOUNT", DESCObillinfo.getUtilityAccount().getValue());
                GetDESCOBillAmountJsonObj.put("VATAMOUNT", DESCObillinfo.getVatAmount().getValue());


                GetDESCOBillAmountJsonArray.put(GetDESCOBillAmountJsonObj);

                /* If response is 100 and API is called successfully. */
                responseJsonObject.put("STATUS", "0");
                responseJsonObject.put("COLOR", "G");
                responseJsonObject.put("MESSAGE", "");
                responseJsonObject.put("RESPONSE", GetDESCOBillAmountJsonArray);
                responseJsonObject.put("RESPONSEMESSAGE", ls_responseMessage);
                responseJsonObject.put("RESPONSECODE", ls_responseCode);
                ls_responseData = responseJsonObject.toString();
            } else { 

				ls_langResCodeMsg=propConfig.getResponseCode("getDESCOBillAmount."+ls_responseCode);			
				ls_responseData = common.ofGetErrDataJsonArray(ls_langResCodeMsg,
						propConfig.getMessageOfResCode("commen.title."+ls_langResCodeMsg, "Alert."), 
						propConfig.getMessageOfResCode("getDESCOBillAmount."+ls_responseCode,"","(ENP210)"),
						ls_responseMessage, "Currently Service under maintenance so please try later (ENP210).", ls_responseCode, "R");            		            		            	
			}
        }
        catch (Exception exception) {
			ls_actualErrMsg = common.ofGetTotalErrString(exception, "");            
			loggerImpl.error(logger,"Exception : " + ls_actualErrMsg,"IN:getDESCOBillAmount");

			ls_responseData = common.ofGetErrDataJsonArray("999",
					propConfig.getMessageOfResCode("commen.title.999", "Alert."),
					propConfig.getMessageOfResCode("commen.exception.999","","(ENP211)"),
					exception.getMessage(),"Currently Service under maintenance so please try later (ENP211)","0", "R");			

		}finally {
        	loggerImpl.stopAndPrintOptLogs(logger,"All API called successfully.","getDESCOBillAmount");
        	loggerImpl.info(logger,"Response generated and send to client.", "getDESCOBillAmount");        	
        }
        return ls_responseData;
    }
}
