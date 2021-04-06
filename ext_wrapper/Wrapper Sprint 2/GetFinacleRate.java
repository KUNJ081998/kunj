package com.easynet.controller.ApiController;

import card.xsd.GetFincleRateRequest;
import card.xsd.GetFincleRateResponse;
import com.easynet.configuration.PropConfiguration;
import com.easynet.configuration.SOAPConnector;
import static com.easynet.controller.ApiController.DoMobileTopup.logger;
import com.easynet.impl.JSONObjectImpl;
import com.easynet.impl.LoggerImpl;
import com.easynet.util.common;
import static com.easynet.util.common.PrintErrLog;
import com.easynet.util.readXML;
import org.apache.ws.axis2.GetFinacleRateResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.soap.client.SoapFaultClientException;

@Component
public class GetFinacleRate {

    static Logger logger=LoggerFactory.getLogger(GetFinacleRate.class);
    @Autowired
    private SOAPConnector soapConnector;
    
    @Autowired
    PropConfiguration propConfig;

    public String getFinacleRate(String reqData) {
        String ls_fixedCurrency = "";
        String ls_password = "";
        String ls_rateCode = "";
        String ls_userName = "";
        String ls_varCurrency = "";
        String ls_responseRate = "";
        String ls_responseCode = "";
        String ls_responseMessage = "";
        String ls_actualErrMsg = "";
        String ls_responseData = "";
        String ls_langResCodeMsg="";
        LoggerImpl loggerImpl=null;

        try {
            loggerImpl=new LoggerImpl();
            
            loggerImpl.info(logger, "Preaparing request data and calling API", "IN:getFinacleRate");
            loggerImpl.generateProfiler("getFinacleRate");
            loggerImpl.startProfiler("Preparing request data.");
            
            JSONObject reqJsonObj = new JSONObject(reqData);
            ls_fixedCurrency = reqJsonObj.getString("FIXEDCURRENCY");
            ls_rateCode = reqJsonObj.getString("RATECODE");
            ls_varCurrency = reqJsonObj.getString("VARCURRENCY");
            
            

            if ((ls_fixedCurrency == null || "".equals(ls_fixedCurrency)) || (ls_rateCode == null || "".equals(ls_rateCode)) || (ls_varCurrency == null || "".equals(ls_varCurrency))) {

                ls_responseData = common.ofGetErrDataJsonArray("99",
						propConfig.getMessageOfResCode("commen.title.99", "Validation Failed."),
						propConfig.getMessageOfResCode("commen.invalid_req_data",""),		
						"Null values found in request data.", "Invalid Request.","", "R");

				return ls_responseData;
            }
            ls_userName = readXML.getXmlData("root>CUST_360>AUTHORIZATION>USER_NAME");
            ls_password = readXML.getXmlData("root>CUST_360>AUTHORIZATION>PASSWORD");

            //get card xsd object factory object.
            card.xsd.ObjectFactory xsdObjectFactory = new card.xsd.ObjectFactory();
            //created GetFincleRateRequest object from object factory.
            GetFincleRateRequest getFincleRateRequest = xsdObjectFactory.createGetFincleRateRequest();
            
            loggerImpl.debug(logger,"Json to xml conversion done.", "IN:getFinacleRate");
            
            //setting all the values in the request.
            getFincleRateRequest.setFixedCurrency(xsdObjectFactory.createGetFincleRateRequestFixedCurrency(ls_fixedCurrency));
            getFincleRateRequest.setPassword(xsdObjectFactory.createGetFincleRateRequestPassword(ls_password));
            getFincleRateRequest.setRateCode(xsdObjectFactory.createGetFincleRateRequestRateCode(ls_rateCode));
            getFincleRateRequest.setUsername(xsdObjectFactory.createGetFincleRateRequestUsername(ls_userName));
            getFincleRateRequest.setVarCurrency(xsdObjectFactory.createGetFincleRateRequestVarCurrency(ls_varCurrency));

            //get object factory object from ws axis2.
            org.apache.ws.axis2.ObjectFactory axis2ObjectFactory = new org.apache.ws.axis2.ObjectFactory();
            //wrapping the request in GetFincleRateRequest
            org.apache.ws.axis2.GetFinacleRate getFincleRaterequest = axis2ObjectFactory.createGetFinacleRate();
            getFincleRaterequest.setRequest(axis2ObjectFactory.createGetFinacleRateRequest(getFincleRateRequest));
            
            loggerImpl.debug(logger,"getFinacleRate API calling", "IN:getFinacleRate");
            loggerImpl.startProfiler("getFinacleRate API calling.");
            
            //created GetFinacleRateResponse object
            GetFinacleRateResponse getFinacleRateResponse = null;
            try {
                getFinacleRateResponse = (GetFinacleRateResponse) soapConnector.callWebService(getFincleRaterequest);
            } catch (SoapFaultClientException soapException) {
				ls_actualErrMsg = soapException.getFaultStringOrReason();
				ls_responseData = common.ofGetErrDataJsonArray("999", 
						propConfig.getMessageOfResCode("commen.title.999", "Alert."),
						propConfig.getMessageOfResCode("commen.exception.999","","(ENP143)"),
						ls_actualErrMsg, "Currently Service under maintenance so please try later (ENP143)", "0", "R");

				ls_actualErrMsg = common.ofGetTotalErrString(soapException, "");
				loggerImpl.error(logger,"SoapFaultClientException : " + ls_actualErrMsg,"EX:getFinacleRate");
				return ls_responseData;
			}
            
            loggerImpl.debug(logger,"getFinacleRate API called successfully.", "IN:getFinacleRate",getFinacleRateResponse);
            loggerImpl.startProfiler("preparing getFinacleRate API response data.");

            //created GetFincleRateresponse object.
            GetFincleRateResponse getfinclerateResponse = getFinacleRateResponse.getReturn().getValue();

            //getting all the values from response.
            ls_responseRate = getfinclerateResponse.getRate().getValue();
            ls_responseCode = getfinclerateResponse.getResponseCode().getValue();
            ls_responseMessage = getfinclerateResponse.getResponseMessage().getValue();

            /*
             * if response is 100 then success.
             * if response is 101 then transaction failed.
             */
            if (ls_responseCode != null && "100".equals(ls_responseCode)) {

                JSONObjectImpl getFinacleRateJResponsesonObj = new JSONObjectImpl();
                JSONObject responseJsonObject = new JSONObject();
                JSONArray getFinacleRateResponseJsonArray = new JSONArray();

                //setting all the values in response json object
                getFinacleRateJResponsesonObj.put("RESPONSERATE", ls_responseRate);
                getFinacleRateJResponsesonObj.put("RESPONSECODE", ls_responseCode);
                getFinacleRateJResponsesonObj.put("RESPONSEMESSAGE", ls_responseMessage);
                getFinacleRateResponseJsonArray.put(getFinacleRateJResponsesonObj);

                /*
                 * If transaction happens and API is called successfully.
                 */
                responseJsonObject.put("STATUS", "0");
                responseJsonObject.put("COLOR", "G");
                responseJsonObject.put("RESPONSE", getFinacleRateResponseJsonArray);
                responseJsonObject.put("MESSAGE", "");
                ls_responseData = responseJsonObject.toString();
            }else { 

				ls_langResCodeMsg=propConfig.getResponseCode("getFinacleRate."+ls_responseCode);			
				ls_responseData = common.ofGetErrDataJsonArray(ls_langResCodeMsg,
						propConfig.getMessageOfResCode("commen.title."+ls_langResCodeMsg, "Alert."), 
						propConfig.getMessageOfResCode("getFinacleRate."+ls_responseCode,"","(ENP144)"),
						ls_responseMessage, "Currently Service under maintenance so please try later (ENP144).", ls_responseCode, "R");            		            		            	
			}
        } catch (Exception exception) {
			ls_actualErrMsg = common.ofGetTotalErrString(exception, "");            
			loggerImpl.error(logger,"Exception : " + ls_actualErrMsg,"IN:getFinacleRate");

			ls_responseData = common.ofGetErrDataJsonArray("999",
					propConfig.getMessageOfResCode("commen.title.999", "Alert."),
					propConfig.getMessageOfResCode("commen.exception.999","","(ENP145)"),
					exception.getMessage(),"Currently Service under maintenance so please try later (ENP145)","0", "R");			

		}finally {
			loggerImpl.stopAndPrintOptLogs(logger,"All API called successfully.","IN:getFinacleRate");
			loggerImpl.info(logger,"Response generated and send to client.", "IN:getFinacleRate");        	
		}
        return ls_responseData;

    }
}
