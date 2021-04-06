package com.easynet.controller.ApiController;

import city.qr.xsd.FxRateVisaRequest;
import city.qr.xsd.FxRateVisaResponse;
import city.qr.xsd.ObjectFactory;
import com.easynet.configuration.PropConfiguration;
import com.easynet.configuration.SOAPConnector;
import com.easynet.impl.JSONObjectImpl;
import com.easynet.impl.LoggerImpl;
import com.easynet.util.common;
import static com.easynet.util.common.PrintErrLog;
import com.easynet.util.readXML;
import org.apache.ws.axis2.GetQrFxRateVisaResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.soap.client.SoapFaultClientException;

@Component
public class GetQrFxRateVisa {
    
    static Logger logger=LoggerFactory.getLogger(GetQrFxRateVisa.class);
    @Autowired
    private SOAPConnector soapConnector;
    
    @Autowired
    PropConfiguration propConfig;

    public String GetQrFxRateVISA(String reqData) {

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
        
            loggerImpl.info(logger,"Preparing requset data and calling API.","IN:GetQrFxRateVISA");
            loggerImpl.generateProfiler("GetQrFxRateVISA");
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
            FxRateVisaRequest FxRateVisaReq = xsdObjectFactory.createFxRateVisaRequest();
            
            loggerImpl.debug(logger,"Json to xml conversion done.", "IN:GetQrFxRateVISA");
            
            //setting all the values in the request.
            FxRateVisaReq.setPassword(xsdObjectFactory.createFxRateVisaRequestPassword(ls_password));
            FxRateVisaReq.setSourceAmount(xsdObjectFactory.createFxRateVisaRequestSourceAmount(ls_sourceAmount));
            FxRateVisaReq.setSourceCurrCode(xsdObjectFactory.createFxRateVisaRequestSourceCurrCode(ls_sourceCurrCode));
            FxRateVisaReq.setTransId(xsdObjectFactory.createFxRateVisaRequestTransId(ls_transId));
            FxRateVisaReq.setUsername(xsdObjectFactory.createFxRateVisaRequestUsername(ls_userName));

            //get the object factory object of axis2.
            org.apache.ws.axis2.ObjectFactory axis2ObjectFactory = new org.apache.ws.axis2.ObjectFactory();
            //wrapping the request object.
            org.apache.ws.axis2.GetQrFxRateVisa getqrFxRateVisa = axis2ObjectFactory.createGetQrFxRateVisa();
            getqrFxRateVisa.setRequest(axis2ObjectFactory.createGetQrFxRateVisaRequest(FxRateVisaReq));
            
            loggerImpl.debug(logger,"GetQrFxRateVISA API calling", "IN:GetQrFxRateVISA");
            loggerImpl.startProfiler("GetQrFxRateVISA API calling.");

            //created response object.
            GetQrFxRateVisaResponse getQrFxRateVisaResponse = null;
            try {
                getQrFxRateVisaResponse = (GetQrFxRateVisaResponse) soapConnector.callWebService(getqrFxRateVisa);
            }catch (SoapFaultClientException soapException) {
				ls_actualErrMsg = soapException.getFaultStringOrReason();
				ls_responseData = common.ofGetErrDataJsonArray("999", 
						propConfig.getMessageOfResCode("commen.title.999", "Alert."),
						propConfig.getMessageOfResCode("commen.exception.999","","(ENP221)"),
						ls_actualErrMsg, "Currently Service under maintenance so please try later (ENP221)", "0", "R");

				ls_actualErrMsg = common.ofGetTotalErrString(soapException, "");
				loggerImpl.error(logger,"SoapFaultClientException : " + ls_actualErrMsg,"EX:GetQrFxRateVISA");
				return ls_responseData;
			}
            
            loggerImpl.debug(logger,"GetQrFxRateVISA API called successfully.", "IN:GetQrFxRateVISA",getQrFxRateVisaResponse);
            loggerImpl.startProfiler("preparing GetQrFxRateVISA API response data.");
            
            //created response object.
            FxRateVisaResponse FxRateVisaResponse = getQrFxRateVisaResponse.getReturn().getValue();

            //getting all the data from the response.
            ls_responseCode = FxRateVisaResponse.getResponseCode().getValue();
            ls_responseMessage = FxRateVisaResponse.getResponseMessage().getValue();
            ls_conversionRate = FxRateVisaResponse.getConversionRate().getValue();
            ls_destinationAmount = String.valueOf(FxRateVisaResponse.getDestinationAmount());
            ls_markUpDestinationAmount = String.valueOf(FxRateVisaResponse.getMarkUpDestinationAmount());
            ls_markUpPercentage = FxRateVisaResponse.getMarkUpPercentage().getValue();

            /* if response is 100 then success.
	     * If response is 101 then no records found.
             */
            if (ls_responseCode != null && "100".equals(ls_responseCode)) {

                JSONObjectImpl FxRateVisaJsonObj = new JSONObjectImpl();
                JSONObject responseJsonObject = new JSONObject();
                JSONArray FxRateVisaJsonArray = new JSONArray();

                FxRateVisaJsonObj.put("RESPONSECODE", ls_responseCode);
                FxRateVisaJsonObj.put("RESPONSEMESSAGE", ls_responseMessage);
                FxRateVisaJsonObj.put("CONVERSIONRATE", ls_conversionRate);
                FxRateVisaJsonObj.put("DESTAMT", ls_destinationAmount);
                FxRateVisaJsonObj.put("MARKUPDESTAMT", ls_markUpDestinationAmount);
                FxRateVisaJsonObj.put("MARKUPPERCENTAGE", ls_markUpPercentage);

                FxRateVisaJsonArray.put(FxRateVisaJsonObj);

                /* If response is 100 and API is called successfully. */
                responseJsonObject.put("STATUS", "0");
                responseJsonObject.put("COLOR", "G");
                responseJsonObject.put("RESPONSE", FxRateVisaJsonArray);
                responseJsonObject.put("MESSAGE", "");
                ls_responseData = responseJsonObject.toString();

            }else { 

				ls_langResCodeMsg=propConfig.getResponseCode("GetQrFxRateVISA."+ls_responseCode);			
				ls_responseData = common.ofGetErrDataJsonArray(ls_langResCodeMsg,
						propConfig.getMessageOfResCode("commen.title."+ls_langResCodeMsg, "Alert."), 
						propConfig.getMessageOfResCode("GetQrFxRateVISA."+ls_responseCode,"","(ENP222)"),
						ls_responseMessage, "Currently Service under maintenance so please try later (ENP222).", ls_responseCode, "R");            		            		            	
			}
        }catch (Exception exception) {
			ls_actualErrMsg = common.ofGetTotalErrString(exception, "");            
			loggerImpl.error(logger,"Exception : " + ls_actualErrMsg,"IN:GetQrFxRateVISA");

			ls_responseData = common.ofGetErrDataJsonArray("999",
					propConfig.getMessageOfResCode("commen.title.999", "Alert."),
					propConfig.getMessageOfResCode("commen.exception.999","","(ENP223)"),
					exception.getMessage(),"Currently Service under maintenance so please try later (ENP223)","0", "R");			

		}finally {
			loggerImpl.stopAndPrintOptLogs(logger,"All API called successfully.","IN:GetQrFxRateVISA");
			loggerImpl.info(logger,"Response generated and send to client.", "IN:GetQrFxRateVISA");        	
		}
        return ls_responseData;
    }
}
