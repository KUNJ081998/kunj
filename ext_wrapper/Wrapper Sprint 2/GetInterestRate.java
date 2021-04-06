package com.easynet.controller.ApiController;

import card.xsd.GetFincleRateResponse;
import city.xsd.GetInterestRateRequest;
import com.easynet.configuration.PropConfiguration;
import com.easynet.configuration.SOAPConnector;
import static com.easynet.controller.ApiController.DoMobileTopup.logger;
import com.easynet.impl.JSONObjectImpl;
import com.easynet.impl.LoggerImpl;
import com.easynet.util.common;
import static com.easynet.util.common.PrintErrLog;
import com.easynet.util.readXML;
import org.apache.ws.axis2.GetFinacleRateResponse;
import org.apache.ws.axis2.GetInterestRateResponse;
import org.apache.ws.axis2.ObjectFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.soap.client.SoapFaultClientException;

@Component
public class GetInterestRate {

    static Logger logger=LoggerFactory.getLogger(GetInterestRate.class);
    @Autowired
    private SOAPConnector soapConnector;
    
    @Autowired
    PropConfiguration propConfig;
    public String getInterestRate(String reqData) {

        String ls_amount = "";
        String ls_schemeCode = "";
        String ls_tenor = "";
        String ls_password = "";
        String ls_userName = "";
        String ls_responseCode = "";
        String ls_responseMessage = "";
        String ls_responseData = "";
        String ls_responseRate = "";
        String ls_actualErrMsg = "";
        String ls_langResCodeMsg="";
        LoggerImpl loggerImpl=null;

        try {
            loggerImpl=new LoggerImpl();
            
            
            loggerImpl.info(logger, "Preaparing request data and calling API", "IN:GetInterestRate");
            loggerImpl.generateProfiler("GetInterestRate");
            loggerImpl.startProfiler("Preparing request data.");
            
            JSONObject reqJsonObj = new JSONObject(reqData);
            ls_amount = reqJsonObj.getString("AMOUNT");
            ls_schemeCode = reqJsonObj.getString("SCHEMECODE");
            ls_tenor = reqJsonObj.getString("TENOR");
            
            

            if ((ls_amount == null || "".equals(ls_amount)) || (ls_schemeCode == null || "".equals(ls_schemeCode)) || (ls_tenor == null || "".equals(ls_tenor))) {

                ls_responseData = common.ofGetErrDataJsonArray("99",
						propConfig.getMessageOfResCode("commen.title.99", "Validation Failed."),
						propConfig.getMessageOfResCode("commen.invalid_req_data",""),		
						"Null values found in request data.", "Invalid Request.","", "R");
            }
            ls_userName = readXML.getXmlData("root>CUST_360>AUTHORIZATION>USER_NAME");
            ls_password = readXML.getXmlData("root>CUST_360>AUTHORIZATION>PASSWORD");

            //get card xsd object factory object.
            city.xsd.ObjectFactory xsdObjectFactory = new city.xsd.ObjectFactory();
            //created GetInterestRateRequest object from object factory.
            GetInterestRateRequest getInterestRateRequest = xsdObjectFactory.createGetInterestRateRequest();
            
            loggerImpl.debug(logger,"Json to xml conversion done.", "IN:GetInterestRate");
            
            //setting all the values in the request.
            getInterestRateRequest.setAmount(xsdObjectFactory.createGetInterestRateRequestAmount(ls_amount));
            getInterestRateRequest.setPassword(xsdObjectFactory.createGetInterestRateRequestPassword(ls_password));
            getInterestRateRequest.setSchemeCode(xsdObjectFactory.createGetInterestRateRequestSchemeCode(ls_schemeCode));
            getInterestRateRequest.setTenor(xsdObjectFactory.createGetInterestRateRequestTenor(ls_tenor));
            getInterestRateRequest.setUsername(xsdObjectFactory.createGetInterestRateRequestUsername(ls_userName));

            //created ws.axis2 object factory object.
            org.apache.ws.axis2.ObjectFactory axis2ObjectFactory = new ObjectFactory();
            //wrapping the request in GetInterestRate.
            org.apache.ws.axis2.GetInterestRate getInterestrate = axis2ObjectFactory.createGetInterestRate();
            getInterestrate.setRequest(axis2ObjectFactory.createGetInterestRateRequest(getInterestRateRequest));
            
            loggerImpl.debug(logger,"GetInterestRate API calling", "IN:GetInterestRate");
            loggerImpl.startProfiler("GetInterestRate API calling.");
            //created GetInterestRateResponse object.
            GetInterestRateResponse getInterestRateResponse = null;
            try {
                getInterestRateResponse = (GetInterestRateResponse) soapConnector.callWebService(getInterestrate);
            }catch (SoapFaultClientException soapException) {
				ls_actualErrMsg = soapException.getFaultStringOrReason();
				ls_responseData = common.ofGetErrDataJsonArray("999", 
						propConfig.getMessageOfResCode("commen.title.999", "Alert."),
						propConfig.getMessageOfResCode("commen.exception.999","","(ENP152)"),
						ls_actualErrMsg, "Currently Service under maintenance so please try later (ENP152)", "0", "R");

				ls_actualErrMsg = common.ofGetTotalErrString(soapException, "");
				loggerImpl.error(logger,"SoapFaultClientException : " + ls_actualErrMsg,"EX:GetInterestRate");
				return ls_responseData;
			}
            
            loggerImpl.debug(logger,"GetInterestRate API called successfully.", "IN:GetInterestRate",getInterestRateResponse);
            loggerImpl.startProfiler("preparing GetInterestRate API response data.");

            //created GetFinacleRate response object.
            GetFincleRateResponse xsd_GetInterestRate = getInterestRateResponse.getReturn().getValue();

            //getting all the data from the response.
            ls_responseRate = xsd_GetInterestRate.getRate().getValue();
            ls_responseMessage = xsd_GetInterestRate.getResponseMessage().getValue();
            ls_responseCode = xsd_GetInterestRate.getResponseCode().getValue();

            /*
             * if response is 100 then success.
             * if response is 101 then transaction failed.
             */
            if (ls_responseCode != null && "100".equals(ls_responseCode)) {

                JSONObjectImpl getInterestRateJResponsesonObj = new JSONObjectImpl();
                JSONObject responseJsonObject = new JSONObject();
                JSONArray getInterestRateJResponsesonArray = new JSONArray();

                getInterestRateJResponsesonObj.put("RATE", ls_responseRate);
                getInterestRateJResponsesonObj.put("RESPONSECODE", ls_responseCode);
                getInterestRateJResponsesonObj.put("RESPONSEMESSAGE", ls_responseMessage);
                getInterestRateJResponsesonArray.put(getInterestRateJResponsesonObj);

                /*
                 * If transaction happens and API is called successfully.
                 */
                responseJsonObject.put("STATUS", "0");
                responseJsonObject.put("COLOR", "G");
                responseJsonObject.put("RESPONSE", getInterestRateJResponsesonArray);
                responseJsonObject.put("MESSAGE", "");
                ls_responseData = responseJsonObject.toString();

            } else { 

				ls_langResCodeMsg=propConfig.getResponseCode("GetInterestRate."+ls_responseCode);			
				ls_responseData = common.ofGetErrDataJsonArray(ls_langResCodeMsg,
						propConfig.getMessageOfResCode("commen.title."+ls_langResCodeMsg, "Alert."), 
						propConfig.getMessageOfResCode("GetInterestRate."+ls_responseCode,"","(ENP153)"),
						ls_responseMessage, "Currently Service under maintenance so please try later (ENP153)", ls_responseCode, "R");            		            		            	
			}
        }catch (Exception exception) {
			ls_actualErrMsg = common.ofGetTotalErrString(exception, "");            
			loggerImpl.error(logger,"Exception : " + ls_actualErrMsg,"IN:GetInterestRate");

			ls_responseData = common.ofGetErrDataJsonArray("999",
					propConfig.getMessageOfResCode("commen.title.999", "Alert."),
					propConfig.getMessageOfResCode("commen.exception.999","","(ENP154)"),
					exception.getMessage(),"Currently Service under maintenance so please try later (ENP154)","0", "R");			

		}finally {
        	loggerImpl.stopAndPrintOptLogs(logger,"All API called successfully.","IN:GetInterestRate");
        	loggerImpl.info(logger,"Response generated and send to client.", "IN:GetInterestRate");        	
        }
        return ls_responseData;
    }
}
