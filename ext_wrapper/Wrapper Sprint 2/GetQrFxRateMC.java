package com.easynet.controller.ApiController;

import city.qr.xsd.FxRateMcRequest;
import city.qr.xsd.FxRateMcResponse;
import city.qr.xsd.ObjectFactory;
import com.easynet.configuration.PropConfiguration;
import com.easynet.configuration.SOAPConnector;
import com.easynet.impl.JSONObjectImpl;
import com.easynet.impl.LoggerImpl;
import com.easynet.util.common;
import static com.easynet.util.common.PrintErrLog;
import com.easynet.util.readXML;
import org.apache.ws.axis2.GetQrFxRateMc;
import org.apache.ws.axis2.GetQrFxRateMcResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.soap.client.SoapFaultClientException;

@Component
public class GetQrFxRateMC {

    static Logger logger=LoggerFactory.getLogger(GetQrFxRateMC.class);
    @Autowired
    private SOAPConnector soapConnector;
    
    @Autowired
    PropConfiguration propConfig;

    public String GetQrFxRateMc(String reqData) {
        String ls_password = "";
        String ls_userName = "";
        String ls_sourceAmount = "";
        String ls_sourceCurrCode = "";
        String ls_transId = "";
        String ls_responseCode = "";
        String ls_responseMessage = "";
        String ls_conversionRate = "";
        String ls_destinationAmount = "";
        String ls_markUpDestinationAmount = "";
        String ls_markUpPercentage = "";
        String ls_actualErrMsg = "";
        String ls_responseData = "";
        String ls_langResCodeMsg="";
        LoggerImpl loggerImpl=null; 

        try {
            loggerImpl=new LoggerImpl();
        
            loggerImpl.info(logger,"Preparing requset data and calling API.","IN:GetQrFxRateMc");
            loggerImpl.generateProfiler("GetQrFxRateMc");
            loggerImpl.startProfiler("Preparing request data");
            
            JSONObject reqJsonObj = new JSONObject(reqData);
            ls_sourceAmount = reqJsonObj.getString("SOURCEAMT");
            ls_sourceCurrCode = reqJsonObj.getString("SOURCECURRCODE");
            ls_transId = reqJsonObj.getString("TRANSID");

            if ((ls_sourceAmount == null || "".equals(ls_sourceAmount)) || (ls_sourceCurrCode == null || "".equals(ls_sourceCurrCode))
                    || (ls_transId == null || "".equals(ls_transId))) {

                ls_responseData = common.ofGetErrDataJsonArray("99",
						propConfig.getMessageOfResCode("commen.title.99", "Validation Failed."),
						propConfig.getMessageOfResCode("commen.invalid_req_data",""),		
						"Null values found in request data.", "Invalid Request.","", "R");

				return ls_responseData;
            }
            ls_userName = readXML.getXmlData("root>CUST_360>AUTHORIZATION>USER_NAME");
            ls_password = readXML.getXmlData("root>CUST_360>AUTHORIZATION>PASSWORD");
            
            
            
            //get object factory object.
            city.qr.xsd.ObjectFactory xsdObjectFactory = new ObjectFactory();
            //created request object.
            FxRateMcRequest FxRateMcReq = xsdObjectFactory.createFxRateMcRequest();
            
            loggerImpl.debug(logger,"Json to xml conversion done.", "IN:GetQrFxRateMc");
            
            //setting all the values in the request.
            FxRateMcReq.setPassword(xsdObjectFactory.createFxRateMcRequestPassword(ls_password));
            FxRateMcReq.setSourceAmount(xsdObjectFactory.createFxRateMcRequestSourceAmount(ls_sourceAmount));
            FxRateMcReq.setSourceCurrCode(xsdObjectFactory.createFxRateMcRequestSourceCurrCode(ls_sourceCurrCode));
            FxRateMcReq.setTransId(xsdObjectFactory.createFxRateMcRequestTransId(ls_transId));
            FxRateMcReq.setUsername(xsdObjectFactory.createFxRateMcRequestUsername(ls_userName));

            //get the object factory object of axis2.
            org.apache.ws.axis2.ObjectFactory axis2ObjectFactory = new org.apache.ws.axis2.ObjectFactory();
            //wrapping the request object in request.
            GetQrFxRateMc getQRFXRateMc = axis2ObjectFactory.createGetQrFxRateMc();
            getQRFXRateMc.setRequest(axis2ObjectFactory.createGetQrFxRateMcRequest(FxRateMcReq));
            
            loggerImpl.debug(logger,"GetQrFxRateMc API calling", "IN:GetQrFxRateMc");
            loggerImpl.startProfiler("GetQrFxRateMc API calling.");

            //created response object.
            GetQrFxRateMcResponse getQrFxRateMcResponse = null;
            try {
                getQrFxRateMcResponse = (GetQrFxRateMcResponse) soapConnector.callWebService(getQRFXRateMc);
            }catch (SoapFaultClientException soapException) {
				ls_actualErrMsg = soapException.getFaultStringOrReason();
				ls_responseData = common.ofGetErrDataJsonArray("999", 
						propConfig.getMessageOfResCode("commen.title.999", "Alert."),
						propConfig.getMessageOfResCode("commen.exception.999","","(ENP218)"),
						ls_actualErrMsg, "Currently Service under maintenance so please try later (ENP218)", "0", "R");

				ls_actualErrMsg = common.ofGetTotalErrString(soapException, "");
				loggerImpl.error(logger,"SoapFaultClientException : " + ls_actualErrMsg,"EX:GetQrFxRateMc");
				return ls_responseData;
			}
            
            loggerImpl.debug(logger,"GetQrFxRateMc API called successfully.", "IN:GetQrFxRateMc",getQrFxRateMcResponse);
            loggerImpl.startProfiler("preparing GetQrFxRateMc API response data.");

            //created response object.
            FxRateMcResponse FxRateMcResponse = getQrFxRateMcResponse.getReturn().getValue();

            //getting all the data from the response object.
            ls_responseCode = FxRateMcResponse.getResponseCode().getValue();
            ls_responseMessage = FxRateMcResponse.getResponseMessage().getValue();
            ls_conversionRate = FxRateMcResponse.getConversionRate().getValue();
            ls_destinationAmount = String.valueOf(FxRateMcResponse.getDestinationAmount());
            ls_markUpDestinationAmount = String.valueOf(FxRateMcResponse.getMarkUpDestinationAmount());
            ls_markUpPercentage = FxRateMcResponse.getMarkUpPercentage().getValue();

            /* if response is 100 then success.
	     * If response is 101 then no records found.
             */
            if (ls_responseCode != null && "100".equals(ls_responseCode)) {

                JSONObjectImpl FxRateMcJsonObj = new JSONObjectImpl();
                JSONObject responseJsonObject = new JSONObject();
                JSONArray FxRateMcJsonArray = new JSONArray();

                FxRateMcJsonObj.put("RESPONSECODE", ls_responseCode);
                FxRateMcJsonObj.put("RESPONSEMESSAGE", ls_responseMessage);
                FxRateMcJsonObj.put("CONVERSIONRATE", ls_conversionRate);
                FxRateMcJsonObj.put("DESTAMT", ls_destinationAmount);
                FxRateMcJsonObj.put("MARKUPDESTAMT", ls_markUpDestinationAmount);
                FxRateMcJsonObj.put("MARKUPPERCENTAGE", ls_markUpPercentage);

                FxRateMcJsonArray.put(FxRateMcJsonObj);

                /* If response is 100 and API is called successfully. */
                responseJsonObject.put("STATUS", "0");
                responseJsonObject.put("COLOR", "G");
                responseJsonObject.put("RESPONSE", FxRateMcJsonArray);
                responseJsonObject.put("MESSAGE", "");
                ls_responseData = responseJsonObject.toString();

            } else { 

				ls_langResCodeMsg=propConfig.getResponseCode("GetQrFxRateMc."+ls_responseCode);			
				ls_responseData = common.ofGetErrDataJsonArray(ls_langResCodeMsg,
						propConfig.getMessageOfResCode("commen.title."+ls_langResCodeMsg, "Alert."), 
						propConfig.getMessageOfResCode("GetQrFxRateMc."+ls_responseCode,"","(ENP219)"),
						ls_responseMessage, "Currently Service under maintenance so please try later (ENP219).", ls_responseCode, "R");            		            		            	
			}
        } catch (Exception exception) {
			ls_actualErrMsg = common.ofGetTotalErrString(exception, "");            
			loggerImpl.error(logger,"Exception : " + ls_actualErrMsg,"IN:GetQrFxRateMc");

			ls_responseData = common.ofGetErrDataJsonArray("999",
					propConfig.getMessageOfResCode("commen.title.999", "Alert."),
					propConfig.getMessageOfResCode("commen.exception.999","","(ENP220)"),
					exception.getMessage(),"Currently Service under maintenance so please try later (ENP220)","0", "R");			

		}finally {
			loggerImpl.stopAndPrintOptLogs(logger,"All API called successfully.","IN:GetQrFxRateMc");
			loggerImpl.info(logger,"Response generated and send to client.", "IN:GetQrFxRateMc");        	
		}
        return ls_responseData;
    }
}
