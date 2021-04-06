package com.easynet.controller.ApiController;

import city.ivac.xsd.GetIVACCenterListRequest;
import city.ivac.xsd.IVACCenterListResponse;
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
import org.apache.ws.axis2.GetIVACCenterList;
import org.apache.ws.axis2.GetIVACCenterListResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.soap.client.SoapFaultClientException;

@Component
public class GetIVACCCenterList {

    static Logger logger = LoggerFactory.getLogger(GetIVACCCenterList.class);
    @Autowired
    private SOAPConnector soapConnector;
    
    @Autowired
    PropConfiguration propConfig;

    public String getIVACCCenterList(String reqData) {

        String ls_userName = "";
        String ls_password = "";
        String ls_responseCode = "";
        String ls_responseData = "";
        String ls_responseMessage = "";
        String ls_actualErrMsg = "";
        String ls_langResCodeMsg="";
        ArrayList<IVACCenterListResponse> IVACCenterList;
        LoggerImpl loggerImpl = null;

        try {

            loggerImpl = new LoggerImpl();

            loggerImpl.info(logger, "Preaparing request data and calling API", "IN:getIVACCCenterList");
            loggerImpl.generateProfiler("getIVACCCenterList");
            loggerImpl.startProfiler("Preparing request data.");

            ls_userName = readXML.getXmlData("root>CUST_360>AUTHORIZATION>USER_NAME");
            ls_password = readXML.getXmlData("root>CUST_360>AUTHORIZATION>PASSWORD");

            //created object of object factory of city.ivac.xsd.
            city.ivac.xsd.ObjectFactory xsdObjectFactory = new ObjectFactory();
            //created request object of GetIVACCenterListRequest.
            GetIVACCenterListRequest getIVACCCenterListRequest = xsdObjectFactory.createGetIVACCenterListRequest();

            loggerImpl.debug(logger, "Json to xml conversion done.", "IN:getIVACCCenterList");

            //setting all the values in the request.
            getIVACCCenterListRequest.setPassword(xsdObjectFactory.createGetIVACCenterListRequestPassword(ls_password));
            getIVACCCenterListRequest.setUsername(xsdObjectFactory.createGetIVACCenterListRequestUsername(ls_userName));

            //created object of object factory of ws.axis2.
            org.apache.ws.axis2.ObjectFactory axis2ObjectFactory = new org.apache.ws.axis2.ObjectFactory();
            //wrapping the requestin GetIVACCenterList.
            GetIVACCenterList getIVACCcenterlist = axis2ObjectFactory.createGetIVACCenterList();
            getIVACCcenterlist.setRequest(axis2ObjectFactory.createGetIVACCenterListRequest(getIVACCCenterListRequest));

            loggerImpl.debug(logger, "getIVACCCenterList API calling", "IN:getIVACCCenterList");
            loggerImpl.startProfiler("getIVACCCenterList API calling.");

            //created response object of GetIVACCenterListResponse.
            GetIVACCenterListResponse getIVACCenterListResponse = null;
            try {
                getIVACCenterListResponse = (GetIVACCenterListResponse) soapConnector.callWebService(getIVACCcenterlist);
            }catch (SoapFaultClientException soapException) {
				ls_actualErrMsg = soapException.getFaultStringOrReason();
				ls_responseData = common.ofGetErrDataJsonArray("999", 
						propConfig.getMessageOfResCode("commen.title.999", "Alert."),
						propConfig.getMessageOfResCode("commen.exception.999","","(ENP149)"),
						ls_actualErrMsg, "Currently Service under maintenance so please try later (ENP149)", "0", "R");

				ls_actualErrMsg = common.ofGetTotalErrString(soapException, "");
				loggerImpl.error(logger,"SoapFaultClientException : " + ls_actualErrMsg,"EX:getIVACCCenterList");
				return ls_responseData;
			}

            loggerImpl.debug(logger, "getIVACCCenterList API called successfully.", "IN:getIVACCCenterList",getIVACCenterListResponse);
            loggerImpl.startProfiler("preparing getIVACCCenterList API response data.");

            //created GetIVACCenterListResponse object of city.ivac.xsd.
            city.ivac.xsd.GetIVACCenterListResponse xsd_getIVACCenterListResponse = getIVACCenterListResponse.getReturn().getValue();

            //getting all the data from the response.
            ls_responseCode = xsd_getIVACCenterListResponse.getResponseCode().getValue();
            ls_responseMessage = xsd_getIVACCenterListResponse.getResponseMessage().getValue();

            /* If response is 100 then success.
         * If response is 101 then no records found.
             */
            if (ls_responseCode != null && "100".equals(ls_responseCode)) {

                JSONObject responseJsonObject = new JSONObject();
                JSONArray GetIVACCenterListJsonArray = new JSONArray();

                IVACCenterList = (ArrayList<IVACCenterListResponse>) xsd_getIVACCenterListResponse.getResponseData();
                for (IVACCenterListResponse iVACCenterListResponse : IVACCenterList) {
                    JSONObjectImpl GetIVACCenterListJsonObj = new JSONObjectImpl();
                    GetIVACCenterListJsonObj.put("CENTERID", iVACCenterListResponse.getCenterId());
                    GetIVACCenterListJsonObj.put("CENTERNAME", iVACCenterListResponse.getCenterName().getValue());

                    GetIVACCenterListJsonArray.put(GetIVACCenterListJsonObj);
                }

                /*
	     * If transaction happens and API is called successfully.
                 */
                responseJsonObject.put("STATUS", "0");
                responseJsonObject.put("COLOR", "G");
                responseJsonObject.put("MESSAGE", "");
                responseJsonObject.put("RESPONSE", GetIVACCenterListJsonArray);
                responseJsonObject.put("RESPONSEMESSAGE", ls_responseMessage);
                responseJsonObject.put("RESPONSECODE", ls_responseCode);
                ls_responseData = responseJsonObject.toString();
            } else { 

				ls_langResCodeMsg=propConfig.getResponseCode("getIVACCCenterList."+ls_responseCode);			
				ls_responseData = common.ofGetErrDataJsonArray(ls_langResCodeMsg,
						propConfig.getMessageOfResCode("commen.title."+ls_langResCodeMsg, "Alert."), 
						propConfig.getMessageOfResCode("getIVACCCenterList."+ls_responseCode,"","(ENP150)"),
						ls_responseMessage, "Currently Service under maintenance so please try later (ENP150).", ls_responseCode, "R");            		            		            	
			}
        } catch (Exception exception) {
			ls_actualErrMsg = common.ofGetTotalErrString(exception, "");            
			loggerImpl.error(logger,"Exception : " + ls_actualErrMsg,"IN:getIVACCCenterList");

			ls_responseData = common.ofGetErrDataJsonArray("999",
					propConfig.getMessageOfResCode("commen.title.999", "Alert."),
					propConfig.getMessageOfResCode("commen.exception.999","","(ENP151)"),
					exception.getMessage(),"Currently Service under maintenance so please try later (ENP151)","0", "R");			

		}finally {
            loggerImpl.stopAndPrintOptLogs(logger, "All API called successfully.", "IN:getIVACCCenterList");
            loggerImpl.info(logger, "Response generated and send to client.", "IN:getIVACCCenterList");
        }
        return ls_responseData;

    }
}
