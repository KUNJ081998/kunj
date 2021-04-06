package com.easynet.controller.ApiController;

import city.qr.xsd.ObjectFactory;
import city.qr.xsd.QrCodeParseRequest;
import city.qr.xsd.QrCodeParseResponse;
import com.easynet.configuration.PropConfiguration;
import com.easynet.configuration.SOAPConnector;
import com.easynet.impl.JSONObjectImpl;
import com.easynet.impl.LoggerImpl;
import com.easynet.util.common;
import static com.easynet.util.common.PrintErrLog;
import com.easynet.util.readXML;
import org.apache.ws.axis2.GetQrData;
import org.apache.ws.axis2.GetQrDataResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.soap.client.SoapFaultClientException;

@Component
public class GetQRData {

    static Logger logger=LoggerFactory.getLogger(GetQRData.class);
    @Autowired
    private SOAPConnector soapConnector;
    
    @Autowired
    PropConfiguration propConfig;

    public String getQRData(String reqData) {

        //request parameters
        String ls_cbsCustomerID = "";
        String ls_clientId = "";
        String ls_password = "";
        String ls_qrString = "";
        String ls_remarks = "";
        String ls_requestID = "";
        String ls_userName = "";
        //response parameters.
        String ls_addiConDataReq6209 = "";
        String ls_billNumber6201 = "";
        String ls_conFeeFixed56 = "";
        String ls_conFeePercentage57 = "";
        String ls_conLabel6206 = "";
        String ls_isCblMerchant = "";
        String ls_loyaltyNumber6204 = "";
        String ls_mcc52 = "";
        String ls_merAccInfoAMEX = "";
        String ls_merAccInfoBangla = "";
        String ls_merAccInfoCUP = "";
        String ls_merAccInfoVISA = "";
        String ls_merAccInfoMC = "";
        String ls_merCityAltLang6402 = "";
        String ls_merCity60 = "";
        String ls_merCountryCode58 = "";
        String ls_merIdType = "";
        String ls_merLangPref6400 = "";
        String ls_merNameAltLang6401 = "";
        String ls_merName59 = "";
        String ls_merPostalCode61 = "";
        String ls_mobileNumber6202 = "";
        String ls_poiMethod01 = "";
        String ls_purOfTransaction6208 = "";
        String ls_qrStatusDetailsBan = "";
        String ls_qrStatusDetailsEng = "";
        String ls_refLabel6205 = "";
        String ls_responseCode = "";
        String ls_responseMessage = "";
        String ls_storeLabel6203 = "";
        String ls_terLabel6207 = "";
        String ls_tipIndicator55 = "";
        String ls_tranAmount54 = "";
        String ls_tranCurrencyAlphaCode = "";
        String ls_tranCurrency53 = "";
        String ls_tranProcessStatus = "";
        String ls_actualErrMsg = "";
        String ls_responseData = "";
        String ls_langResCodeMsg="";
        LoggerImpl loggerImpl=null;

        try {
            loggerImpl=new LoggerImpl();
        
            loggerImpl.info(logger,"Preparing requset data and calling API.","IN:getQRData");
            loggerImpl.generateProfiler("getQRData");
            loggerImpl.startProfiler("Preparing request data");
        
        
            JSONObject reqJsonObj = new JSONObject(reqData);
            ls_cbsCustomerID = reqJsonObj.getString("CSBCUSTOMERID");
            ls_clientId = reqJsonObj.getString("CLIENTID");
            ls_qrString = reqJsonObj.getString("QRSTRING");
            ls_remarks = reqJsonObj.getString("REMARKS");
            ls_requestID = reqJsonObj.getString("REQUESTID");

            if ((ls_cbsCustomerID == null || "".equals(ls_cbsCustomerID)) || (ls_clientId == null || "".equals(ls_clientId)) || (ls_qrString == null || "".equals(ls_qrString))
                    || (ls_remarks == null || "".equals(ls_remarks)) || (ls_requestID == null || "".equals(ls_requestID))) {

                ls_responseData = common.ofGetErrDataJsonArray("99",
						propConfig.getMessageOfResCode("commen.title.99", "Validation Failed."),
						propConfig.getMessageOfResCode("commen.invalid_req_data",""),		
						"Null values found in request data.", "Invalid Request.","", "R");

				return ls_responseData;
            }
            ls_userName = readXML.getXmlData("root>CUST_360>AUTHORIZATION>USER_NAME");
            ls_password = readXML.getXmlData("root>CUST_360>AUTHORIZATION>PASSWORD");

            //created object factory object.
            city.qr.xsd.ObjectFactory xsdObjectFactory = new ObjectFactory();
            //created request object of qrCodeParserRequest.
            QrCodeParseRequest qrCodeParseRequest = xsdObjectFactory.createQrCodeParseRequest();
            
            loggerImpl.debug(logger,"Json to xml conversion done.", "IN:getQRData");

            //setting all the values in the request.
            qrCodeParseRequest.setCbsCustomerID(xsdObjectFactory.createQrCodeParseRequestCbsCustomerID(ls_cbsCustomerID));
            qrCodeParseRequest.setClientId(xsdObjectFactory.createQrCodeParseRequestClientId(ls_clientId));
            qrCodeParseRequest.setPassword(xsdObjectFactory.createQrCodeParseRequestPassword(ls_password));
            qrCodeParseRequest.setQrString(xsdObjectFactory.createQrCodeParseRequestQrString(ls_qrString));
            qrCodeParseRequest.setRemarks(xsdObjectFactory.createQrCodeParseRequestRemarks(ls_remarks));
            qrCodeParseRequest.setRequestID(xsdObjectFactory.createQrCodeParseRequestRequestID(ls_requestID));
            qrCodeParseRequest.setUsername(xsdObjectFactory.createQrCodeParseRequestUsername(ls_userName));

            //created object factory object.
            org.apache.ws.axis2.ObjectFactory axis2ObjectFactory = new org.apache.ws.axis2.ObjectFactory();
            //wrapping the request in GetQRData.
            GetQrData getQrdata = axis2ObjectFactory.createGetQrData();
            getQrdata.setRequest(axis2ObjectFactory.createGetQrDataRequest(qrCodeParseRequest));
            
            loggerImpl.debug(logger,"getQRData API calling", "IN:getQRData");
            loggerImpl.startProfiler("getQRData API calling.");

            //created GetQrdataResponse object.
            GetQrDataResponse getQrDataResponse = null;
            try {
                getQrDataResponse = (GetQrDataResponse) soapConnector.callWebService(getQrdata);
            } catch (SoapFaultClientException soapException) {
				ls_actualErrMsg = soapException.getFaultStringOrReason();
				ls_responseData = common.ofGetErrDataJsonArray("999", 
						propConfig.getMessageOfResCode("commen.title.999", "Alert."),
						propConfig.getMessageOfResCode("commen.exception.999","","(ENP212)"),
						ls_actualErrMsg, "Currently Service under maintenance so please try later (ENP212)", "0", "R");

				ls_actualErrMsg = common.ofGetTotalErrString(soapException, "");
				loggerImpl.error(logger,"SoapFaultClientException : " + ls_actualErrMsg,"EX:getQRData");
				return ls_responseData;
			}
            
            loggerImpl.debug(logger,"getQRData API called successfully.", "IN:getQRData",getQrDataResponse);
            loggerImpl.startProfiler("preparing getQRData API response data.");
        
            //created response object of QrCodeParserResponse.
            QrCodeParseResponse xsd_QrDataResponse = getQrDataResponse.getReturn().getValue();

            //getting all the data from the response.
            ls_addiConDataReq6209 = xsd_QrDataResponse.getAddiConDataReq6209().getValue();
            ls_billNumber6201 = xsd_QrDataResponse.getBillNumber6201().getValue();
            ls_conFeeFixed56 = xsd_QrDataResponse.getConFeeFixed56().getValue();
            ls_conFeePercentage57 = xsd_QrDataResponse.getConFeePercentage57().getValue();
            ls_conLabel6206 = xsd_QrDataResponse.getConLabel6206().getValue();
            ls_isCblMerchant = xsd_QrDataResponse.getIsCblMerchant().getValue();
            ls_loyaltyNumber6204 = xsd_QrDataResponse.getLoyaltyNumber6204().getValue();
            ls_mcc52 = xsd_QrDataResponse.getMcc52().getValue();
            ls_merAccInfoAMEX = xsd_QrDataResponse.getMerAccInfoAMEX().getValue();
            ls_merAccInfoBangla = xsd_QrDataResponse.getMerAccInfoBangla().getValue();
            ls_merAccInfoCUP = xsd_QrDataResponse.getMerAccInfoCUP().getValue();
            ls_merAccInfoMC = xsd_QrDataResponse.getMerAccInfoMC().getValue();
            ls_merAccInfoVISA = xsd_QrDataResponse.getMerAccInfoVISA().getValue();
            ls_merCity60 = xsd_QrDataResponse.getMerCity60().getValue();
            ls_merCityAltLang6402 = xsd_QrDataResponse.getMerCityAltLang6402().getValue();
            ls_merCountryCode58 = xsd_QrDataResponse.getMerCountryCode58().getValue();
            ls_merIdType = xsd_QrDataResponse.getMerIdType().getValue();
            ls_merLangPref6400 = xsd_QrDataResponse.getMerLangPref6400().getValue();
            ls_merNameAltLang6401 = xsd_QrDataResponse.getMerNameAltLang6401().getValue();
            ls_merName59 = xsd_QrDataResponse.getMerName59().getValue();
            ls_merPostalCode61 = xsd_QrDataResponse.getMerPostalCode61().getValue();
            ls_mobileNumber6202 = xsd_QrDataResponse.getMobileNumber6202().getValue();
            ls_poiMethod01 = xsd_QrDataResponse.getPoiMethod01().getValue();
            ls_purOfTransaction6208 = xsd_QrDataResponse.getPurOfTransaction6208().getValue();
            ls_qrStatusDetailsBan = xsd_QrDataResponse.getQrStatusDetailsBan().getValue();
            ls_qrStatusDetailsEng = xsd_QrDataResponse.getQrStatusDetailsEng().getValue();
            ls_refLabel6205 = xsd_QrDataResponse.getRefLabel6205().getValue();
            ls_responseCode = xsd_QrDataResponse.getResponseCode().getValue();
            ls_responseMessage = xsd_QrDataResponse.getResponseMessage().getValue();
            ls_storeLabel6203 = xsd_QrDataResponse.getStoreLabel6203().getValue();
            ls_terLabel6207 = xsd_QrDataResponse.getTerLabel6207().getValue();
            ls_tipIndicator55 = xsd_QrDataResponse.getTipIndicator55().getValue();
            ls_tranAmount54 = xsd_QrDataResponse.getTranAmount54().getValue();
            ls_tranCurrency53 = xsd_QrDataResponse.getTranCurrency53().getValue();
            ls_tranCurrencyAlphaCode = xsd_QrDataResponse.getTranCurrencyAlphaCode().getValue();
            ls_tranProcessStatus = xsd_QrDataResponse.getTranProcessStatus().getValue();

            /* if response is 100 then success.
	     * If response is 101 then no records found.
             */
            if (ls_responseCode != null && "100".equals(ls_responseCode)) {

                JSONObjectImpl GetQrDataJsonObj = new JSONObjectImpl();
                JSONObject responseJsonObject = new JSONObject();
                JSONArray GetQrDataJsonArray = new JSONArray();

                GetQrDataJsonObj.put("ADDICONDATAREQ6209", ls_addiConDataReq6209);
                GetQrDataJsonObj.put("BILLNUMBER6201", ls_billNumber6201);
                GetQrDataJsonObj.put("CONFEEFIXED56", ls_conFeeFixed56);
                GetQrDataJsonObj.put("CONFEEPERCENTAGE57", ls_conFeePercentage57);
                GetQrDataJsonObj.put("CONLABEL6206", ls_conLabel6206);
                GetQrDataJsonObj.put("ISCBLMERCHANT", ls_isCblMerchant);
                GetQrDataJsonObj.put("LOYALTYNUMBER6204", ls_loyaltyNumber6204);
                GetQrDataJsonObj.put("MCC52", ls_mcc52);
                GetQrDataJsonObj.put("MERACCINFOAMEX", ls_merAccInfoAMEX);
                GetQrDataJsonObj.put("MERACCINFOBANGLA", ls_merAccInfoBangla);
                GetQrDataJsonObj.put("MERACCINFOCUP", ls_merAccInfoCUP);
                GetQrDataJsonObj.put("MERACCINFOMC", ls_merAccInfoMC);
                GetQrDataJsonObj.put("MERACCINFOVISA", ls_merAccInfoVISA);
                GetQrDataJsonObj.put("MERCITY60", ls_merCity60);
                GetQrDataJsonObj.put("MERCITYALTLANG6402", ls_merCityAltLang6402);
                GetQrDataJsonObj.put("MERCOUNTRYCODE58", ls_merCountryCode58);
                GetQrDataJsonObj.put("MERIDTYPE", ls_merIdType);
                GetQrDataJsonObj.put("MERLANGPREF6400", ls_merLangPref6400);
                GetQrDataJsonObj.put("MERNAMEALTLANG6401", ls_merNameAltLang6401);
                GetQrDataJsonObj.put("MERNAME59", ls_merName59);
                GetQrDataJsonObj.put("MERPOSTALCODE61", ls_merPostalCode61);
                GetQrDataJsonObj.put("MOBILENUMBER6202", ls_mobileNumber6202);
                GetQrDataJsonObj.put("POIMETHOD01", ls_poiMethod01);
                GetQrDataJsonObj.put("PUROFTRANSACTION6208", ls_purOfTransaction6208);
                GetQrDataJsonObj.put("QRSTATUSDETAILSBAN", ls_qrStatusDetailsBan);
                GetQrDataJsonObj.put("QRSTATUSDETAILSENG", ls_qrStatusDetailsEng);
                GetQrDataJsonObj.put("REFLABEL6205", ls_refLabel6205);
                GetQrDataJsonObj.put("RESPONSECODE", ls_responseCode);
                GetQrDataJsonObj.put("RESPONSEMESSAGE", ls_responseMessage);
                GetQrDataJsonObj.put("STORELABEL6203", ls_storeLabel6203);
                GetQrDataJsonObj.put("TERLABEL6207", ls_terLabel6207);
                GetQrDataJsonObj.put("TIPINDICATOR55", ls_tipIndicator55);
                GetQrDataJsonObj.put("TRANAMOUNT54", ls_tranAmount54);
                GetQrDataJsonObj.put("TRANCURRENCY53", ls_tranCurrency53);
                GetQrDataJsonObj.put("TRANCURRENCYALPHACODE", ls_tranCurrencyAlphaCode);
                GetQrDataJsonObj.put("TRANPROCESSSTATUS", ls_tranProcessStatus);

                GetQrDataJsonArray.put(GetQrDataJsonObj);

                /* If response is 100 and API is called successfully. */
                responseJsonObject.put("STATUS", "0");
                responseJsonObject.put("COLOR", "G");
                responseJsonObject.put("RESPONSE", GetQrDataJsonArray);
                responseJsonObject.put("MESSAGE", "");
                ls_responseData = responseJsonObject.toString();

            } else { 

				ls_langResCodeMsg=propConfig.getResponseCode("getQRData."+ls_responseCode);			
				ls_responseData = common.ofGetErrDataJsonArray(ls_langResCodeMsg,
						propConfig.getMessageOfResCode("commen.title."+ls_langResCodeMsg, "Alert."), 
						propConfig.getMessageOfResCode("getQRData."+ls_responseCode,"","(ENP213)"),
						ls_responseMessage, "Currently Service under maintenance so please try later (ENP213).", ls_responseCode, "R");            		            		            	
			}
        } catch (Exception exception) {
			ls_actualErrMsg = common.ofGetTotalErrString(exception, "");            
			loggerImpl.error(logger,"Exception : " + ls_actualErrMsg,"IN:getQRData");

			ls_responseData = common.ofGetErrDataJsonArray("999",
					propConfig.getMessageOfResCode("commen.title.999", "Alert."),
					propConfig.getMessageOfResCode("commen.exception.999","","(ENP214)"),
					exception.getMessage(),"Currently Service under maintenance so please try later (ENP214)","0", "R");			

		}finally {
			loggerImpl.stopAndPrintOptLogs(logger,"All API called successfully.","IN:getQRData");
			loggerImpl.info(logger,"Response generated and send to client.", "IN:getQRData");        	
		}
        return ls_responseData;
    }
}
