package com.easynet.controller.ApiController;

import city.ivac.xsd.GetIVACBillInfoRequest;
import city.ivac.xsd.IVACBillResponse;
import city.ivac.xsd.ObjectFactory;
import com.easynet.configuration.PropConfiguration;
import com.easynet.configuration.SOAPConnector;
import static com.easynet.controller.ApiController.DoMobileTopup.logger;
import com.easynet.impl.JSONObjectImpl;
import com.easynet.impl.LoggerImpl;
import com.easynet.util.common;
import static com.easynet.util.common.PrintErrLog;
import com.easynet.util.readXML;
import java.util.ArrayList;
import org.apache.ws.axis2.GetIVACBillInfo;
import org.apache.ws.axis2.GetIVACBillInfoResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.soap.client.SoapFaultClientException;

@Component
public class GetIVACCBillInfo {

    static Logger logger=LoggerFactory.getLogger(GetIVACCBillInfo.class);
    @Autowired
    private SOAPConnector soapConnector;
    
    @Autowired PropConfiguration propConfig;

    public String getIVACBillInfo(String reqData) {

        String ls_appointDate = "";
        String ls_appointType = "";
        String ls_emailAddress = "";
        String ls_idIVAC = "";
        String ls_mobileNo = "";
        String ls_passportNo = "";
        String ls_password = "";
        String ls_transactionId = "";
        String ls_userName = "";
        String ls_webFileId = "";
        String ls_responseCode = "";
        String ls_responseData = "";
        String ls_responseMessage = "";
        String ls_actualErrMsg = "";
        String ls_langResCodeMsg="";
        ArrayList<IVACBillResponse> IVACBillResponseList;
        LoggerImpl loggerImpl=null;

        try {
            loggerImpl=new LoggerImpl();
            
            loggerImpl.info(logger, "Preaparing request data and calling API", "IN:getIVACBillInfo");
            loggerImpl.generateProfiler("getIVACBillInfo");
            loggerImpl.startProfiler("Preparing request data.");
            
            
            JSONObject reqJsonObj = new JSONObject(reqData);
            ls_appointDate = reqJsonObj.getString("APPOINTDATE");
            ls_appointType = reqJsonObj.getString("APPOINTTYPE");
            ls_emailAddress = reqJsonObj.getString("EMAILADDRESS");
            ls_idIVAC = reqJsonObj.getString("IDIVAC");
            ls_mobileNo = reqJsonObj.getString("MOBILENO");
            ls_passportNo = reqJsonObj.getString("PASSPORTNO");
            ls_transactionId = reqJsonObj.getString("TRANSACTIONID");
            ls_webFileId = reqJsonObj.getString("WEBFILEID");
            
            

            if ((ls_appointDate == null || "".equals(ls_appointDate)) || (ls_appointType == null || "".equals(ls_appointType)) || (ls_emailAddress == null || "".equals(ls_emailAddress))
                    || (ls_idIVAC == null || "".equals(ls_idIVAC)) || (ls_mobileNo == null || "".equals(ls_mobileNo)) || (ls_passportNo == null || "".equals(ls_passportNo))
                    || (ls_transactionId == null || "".equals(ls_transactionId)) || (ls_webFileId == null || "".equals(ls_webFileId))) {

                ls_responseData = common.ofGetErrDataJsonArray("99",
						propConfig.getMessageOfResCode("commen.title.99", "Validation Failed."),
						propConfig.getMessageOfResCode("commen.invalid_req_data",""),		
						"Null values found in request data.", "Invalid Request.","", "R");

				return ls_responseData;
            }
            ls_userName = readXML.getXmlData("root>CUST_360>AUTHORIZATION>USER_NAME");
            ls_password = readXML.getXmlData("root>CUST_360>AUTHORIZATION>PASSWORD");

            //created object of object factory of city.ivac.xsd.
            city.ivac.xsd.ObjectFactory xsdObjectFactory = new ObjectFactory();
            //created object of GetIVACBillInfoRequest.
            GetIVACBillInfoRequest getIVACBillInfoRequest = xsdObjectFactory.createGetIVACBillInfoRequest();
            
            loggerImpl.debug(logger,"Json to xml conversion done.", "IN:getIVACBillInfo");
            
            //setting all the values in the request.
            getIVACBillInfoRequest.setAppointDate(xsdObjectFactory.createGetIVACBillInfoRequestAppointDate(ls_appointDate));
            getIVACBillInfoRequest.setAppointType(xsdObjectFactory.createGetIVACBillInfoRequestAppointType(ls_appointType));
            getIVACBillInfoRequest.setEmailAddress(xsdObjectFactory.createGetIVACBillInfoRequestEmailAddress(ls_emailAddress));
            getIVACBillInfoRequest.setIdIVAC(xsdObjectFactory.createGetIVACBillInfoRequestIdIVAC(ls_idIVAC));
            getIVACBillInfoRequest.setMobileNo(xsdObjectFactory.createGetIVACBillInfoRequestMobileNo(ls_mobileNo));
            getIVACBillInfoRequest.setPassportNo(xsdObjectFactory.createGetIVACBillInfoRequestPassportNo(ls_passportNo));
            getIVACBillInfoRequest.setPassword(xsdObjectFactory.createGetIVACBillInfoRequestPassword(ls_password));
            getIVACBillInfoRequest.setTransactionId(xsdObjectFactory.createGetIVACBillInfoRequestTransactionId(ls_transactionId));
            getIVACBillInfoRequest.setUsername(xsdObjectFactory.createGetIVACBillInfoRequestUsername(ls_userName));
            getIVACBillInfoRequest.setWebFileId(xsdObjectFactory.createGetIVACBillInfoRequestWebFileId(ls_webFileId));

            //created object of object factory og ws.axis2.
            org.apache.ws.axis2.ObjectFactory axis2ObjectFactory = new org.apache.ws.axis2.ObjectFactory();
            //wrapping the request in GetIVACBillInfo.
            GetIVACBillInfo getIVACBillinfo = axis2ObjectFactory.createGetIVACBillInfo();
            getIVACBillinfo.setRequest(axis2ObjectFactory.createGetIVACBillInfoRequest(getIVACBillInfoRequest));
            
            loggerImpl.debug(logger,"getIVACBillInfo API calling", "IN:getIVACBillInfo");
            loggerImpl.startProfiler("getIVACBillInfo API calling.");

            //created response object of GetIVACBillInfoResponse.
            GetIVACBillInfoResponse getIVACBillInfoResponse = null;
            try {
                getIVACBillInfoResponse = (GetIVACBillInfoResponse) soapConnector.callWebService(getIVACBillinfo);
            }catch (SoapFaultClientException soapException) {
				ls_actualErrMsg = soapException.getFaultStringOrReason();
				ls_responseData = common.ofGetErrDataJsonArray("999", 
						propConfig.getMessageOfResCode("commen.title.999", "Alert."),
						propConfig.getMessageOfResCode("commen.exception.999","","(ENP146)"),
						ls_actualErrMsg, "Currently Service under maintenance so please try later (ENP146)", "0", "R");

				ls_actualErrMsg = common.ofGetTotalErrString(soapException, "");
				loggerImpl.error(logger,"SoapFaultClientException : " + ls_actualErrMsg,"EX:getIVACBillInfo");
				return ls_responseData;
			}
            
            loggerImpl.debug(logger,"getIVACBillInfo API called successfully.", "IN:getIVACBillInfo",getIVACBillInfoResponse);
            loggerImpl.startProfiler("preparing getIVACBillInfo API response data.");

            //created object of GetIVACBillInfoResponse.
            city.ivac.xsd.GetIVACBillInfoResponse xsd_getIVACBillInfoResponse = getIVACBillInfoResponse.getReturn().getValue();

            //getting all the data from the response.
            ls_responseCode = xsd_getIVACBillInfoResponse.getResponseCode().getValue();
            ls_responseMessage = xsd_getIVACBillInfoResponse.getResponseMessage().getValue();

            /* If response is 100 then success.
             * If response is 101 then no records found.
             */
            if (ls_responseCode != null && "100".equals(ls_responseCode)) {

                JSONObject responseJsonObject = new JSONObject();
                JSONArray GetIVACBillInfoJsonArray = new JSONArray();

                IVACBillResponseList = (ArrayList<IVACBillResponse>) xsd_getIVACBillInfoResponse.getResponseData();
                for (IVACBillResponse iVACBillResponse : IVACBillResponseList) {

                    JSONObjectImpl GetIVACBillInfoJsonObj = new JSONObjectImpl();
                    
                    GetIVACBillInfoJsonObj.put("BILLAMOUNT", iVACBillResponse.getBillAmount().getValue());
                    GetIVACBillInfoJsonObj.put("CENTERNAME", iVACBillResponse.getCenterName().getValue());
                    GetIVACBillInfoJsonObj.put("LID", iVACBillResponse.getLid().getValue());
                    GetIVACBillInfoJsonObj.put("TOTALAMOUNT", iVACBillResponse.getTotalAmount().getValue());
                    GetIVACBillInfoJsonObj.put("TRANSACTIONID", iVACBillResponse.getTransactionId().getValue());

                    GetIVACBillInfoJsonArray.put(GetIVACBillInfoJsonObj);

                }
                /*
                 * If transaction happens and API is called successfully.
                 */
                responseJsonObject.put("STATUS", "0");
                responseJsonObject.put("COLOR", "G");
                responseJsonObject.put("MESSAGE", "");
                responseJsonObject.put("RESPONSE", GetIVACBillInfoJsonArray);
                responseJsonObject.put("RESPONSEMESSAGE", ls_responseMessage);
                responseJsonObject.put("RESPONSECODE", ls_responseCode);
                ls_responseData = responseJsonObject.toString();
            }else { 

				ls_langResCodeMsg=propConfig.getResponseCode("getIVACBillInfo."+ls_responseCode);			
				ls_responseData = common.ofGetErrDataJsonArray(ls_langResCodeMsg,
						propConfig.getMessageOfResCode("commen.title."+ls_langResCodeMsg, "Alert."), 
						propConfig.getMessageOfResCode("getIVACBillInfo."+ls_responseCode,"","(ENP147)"),
						ls_responseMessage, "Currently Service under maintenance so please try later (ENP147).", ls_responseCode, "R");            		            		            	
			}
        }catch (Exception exception) {
			ls_actualErrMsg = common.ofGetTotalErrString(exception, "");            
			loggerImpl.error(logger,"Exception : " + ls_actualErrMsg,"IN:getIVACBillInfo");

			ls_responseData = common.ofGetErrDataJsonArray("999",
					propConfig.getMessageOfResCode("commen.title.999", "Alert."),
					propConfig.getMessageOfResCode("commen.exception.999","","(ENP148)"),
					exception.getMessage(),"Currently Service under maintenance so please try later (ENP148)","0", "R");			

		}finally {
        	loggerImpl.stopAndPrintOptLogs(logger,"All API called successfully.","IN:getIVACBillInfo");
        	loggerImpl.info(logger,"Response generated and send to client.", "IN:getIVACBillInfo");        	
        }
        return ls_responseData;
    }
}
