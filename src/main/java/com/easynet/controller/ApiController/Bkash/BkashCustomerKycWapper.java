package com.easynet.controller.ApiController.Bkash;

import city.bkash.xsd.BkashCustomerKYC;
import city.bkash.xsd.GetBkashCustomerKYCRequest;
import city.bkash.xsd.ObjectFactory;

import com.easynet.configuration.PropConfiguration;
import com.easynet.configuration.SOAPConnector;
import com.easynet.impl.JSONObjectImpl;
import com.easynet.impl.LoggerImpl;
import com.easynet.util.common;
import com.easynet.util.readXML;
import org.apache.ws.axis2.GetBkashCustomerKYC;
import org.apache.ws.axis2.GetBkashCustomerKYCResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.soap.client.SoapFaultClientException;

/**
 *This is the bkash customer wrapper class which called soap API.
 *@Date -18/03/2021.
 * */
@Component
public class BkashCustomerKycWapper{

	@Autowired
	private SOAPConnector soapConnector;

	@Autowired
	PropConfiguration propConfiguration;

	static Logger logger=LoggerFactory.getLogger(BkashCustomerKycWapper.class);

	/**
	 *Call the soap API with given parameter.
	 *@param requestData string format json request data.
	 *@return return the json format string data.
	 *@apiNote This method call below API.<br>
	 *	1.getBkashCustomerKYC
	 * 
	 * */
	public String getBkashCustomerKyc(String requestData) {

		String ls_mobileNumber = "";
		String ls_password = "";
		String ls_userName = "";
		String ls_responseMessage = "";
		String ls_responseData = "";
		String ls_responseCode = "";
		String ls_actualErrMsg = "";
		LoggerImpl	loggerImpl=null;
		String ls_langResCodeMsg="";

		try {
			loggerImpl=new LoggerImpl();

			loggerImpl.info(logger,"Preparing requset data and calling API.","IN:getBkashCustomerKyc");
			loggerImpl.generateProfiler("getBkashCustomerKyc");
			loggerImpl.startProfiler("Preparing request data");

			JSONObject reqJsonObj = new JSONObject(requestData);
			ls_mobileNumber = reqJsonObj.getString("MOBILE_NUMBER");

			if ((ls_mobileNumber == null || "".equals(ls_mobileNumber))) {                

				ls_responseData = common.ofGetErrDataJsonArray("99",
						propConfiguration.getMessageOfResCode("commen.title.99", "Validation Failed."),
						propConfiguration.getMessageOfResCode("commen.invalid_req_data",""),		
						"Null values found in request data.", "Invalid Request.","", "R");

				return ls_responseData;
			}
			ls_userName = readXML.getXmlData("root>CUST_360>AUTHORIZATION>USER_NAME");
			ls_password = readXML.getXmlData("root>CUST_360>AUTHORIZATION>PASSWORD");

			//get object factory object.
			city.bkash.xsd.ObjectFactory xsdObjectFactory = new ObjectFactory();
			//get the request object.
			GetBkashCustomerKYCRequest getbkashCustomerKYCRequest = xsdObjectFactory.createGetBkashCustomerKYCRequest();

			loggerImpl.debug(logger,"Json to xml conversion done.", "IN:getBkashCustomerKyc");

			//setting the values in the request.
			getbkashCustomerKYCRequest.setMobileNumber(xsdObjectFactory.createGetBkashCustomerKYCRequestMobileNumber(ls_mobileNumber));
			getbkashCustomerKYCRequest.setPassword(xsdObjectFactory.createGetBkashCustomerKYCRequestPassword(ls_password));
			getbkashCustomerKYCRequest.setUsername(xsdObjectFactory.createGetBkashCustomerKYCRequestUsername(ls_userName));

			//get the object factory object.
			org.apache.ws.axis2.ObjectFactory axis2ObjectFactory = new org.apache.ws.axis2.ObjectFactory();

			//wrapping the request object in the request.
			GetBkashCustomerKYC getbkashcustomerKYC = axis2ObjectFactory.createGetBkashCustomerKYC();
			getbkashcustomerKYC.setRequest(axis2ObjectFactory.createGetBkashCustomerKYCRequest(getbkashCustomerKYCRequest));

			loggerImpl.debug(logger,"getBkashCustomerKYC API calling", "IN:getBkashCustomerKyc");
			loggerImpl.startProfiler("getBkashCustomerKYC API calling.");

			//get the response object.
			GetBkashCustomerKYCResponse getBkashCustomerKYCResponse = null;
			try {
				getBkashCustomerKYCResponse = (GetBkashCustomerKYCResponse) soapConnector.callWebService(getbkashcustomerKYC);
			} catch (SoapFaultClientException soapException) {
				ls_actualErrMsg = soapException.getFaultStringOrReason();
				ls_responseData = common.ofGetErrDataJsonArray("999", 
						propConfiguration.getMessageOfResCode("commen.title.999", "Alert."),
						propConfiguration.getMessageOfResCode("commen.exception.999","","(ENP106)"),
						ls_actualErrMsg, "Currently Service under maintenance so please try later (ENP106)", "0", "R");

				ls_actualErrMsg = common.ofGetTotalErrString(soapException, "");
				loggerImpl.error(logger,"SoapFaultClientException : " + ls_actualErrMsg,"EX:getBkashCustomerKYC");
				return ls_responseData;
			}

			loggerImpl.debug(logger,"getBkashCustomerKYC API called successfully.", "IN:getBkashCustomerKyc",getBkashCustomerKYCResponse);
			loggerImpl.startProfiler("preparing getBkashCustomerKYC API response data.");

			//get the response object.
			city.bkash.xsd.GetBkashCustomerKYCResponse xsd_getBkashCustomerKYCResponse = getBkashCustomerKYCResponse.getReturn().getValue();

			//getting all the data from the response.
			ls_responseCode = xsd_getBkashCustomerKYCResponse.getResponseCode().getValue();
			ls_responseMessage = xsd_getBkashCustomerKYCResponse.getResponseMessage().getValue();

			/*if response is 100 then success.
          		"000"-not successful
			 */
			if (ls_responseCode != null && "100".equals(ls_responseCode)) {

				JSONObjectImpl bkashCustomerKYCJsonObj = new JSONObjectImpl();
				JSONObject responseJsonObject = new JSONObject();
				JSONArray bkashCustomerKYCJsonArray = new JSONArray();

				BkashCustomerKYC bkashCustomerKYC = xsd_getBkashCustomerKYCResponse.getResponseData().getValue();

				bkashCustomerKYCJsonObj.put("ALTCONTACT", bkashCustomerKYC.getAlternativeContact().getValue());
				bkashCustomerKYCJsonObj.put("BKASHRESPONSECODE", bkashCustomerKYC.getBKashResponseCode().getValue());
				bkashCustomerKYCJsonObj.put("BKASHRESPONSEDESC", bkashCustomerKYC.getBKashResponseDescription().getValue());
				bkashCustomerKYCJsonObj.put("BKASHRESULTCODE", bkashCustomerKYC.getBKashResultCode().getValue());
				bkashCustomerKYCJsonObj.put("BKASHRESULTDESC", bkashCustomerKYC.getBKashResultDescription().getValue());
				bkashCustomerKYCJsonObj.put("CONVERSIONID", bkashCustomerKYC.getConversionId().getValue());
				bkashCustomerKYCJsonObj.put("DOB", bkashCustomerKYC.getDateOfBirth().getValue());
				bkashCustomerKYCJsonObj.put("EMAIL", bkashCustomerKYC.getEmail().getValue());
				bkashCustomerKYCJsonObj.put("FATHERORHUSBANDNM", bkashCustomerKYC.getFatherOrHusbandName().getValue());
				bkashCustomerKYCJsonObj.put("FIRSTNAME", bkashCustomerKYC.getFirstName().getValue());
				bkashCustomerKYCJsonObj.put("FULLACTIVATION", bkashCustomerKYC.getFullActivation().getValue());
				bkashCustomerKYCJsonObj.put("FULLNAME", bkashCustomerKYC.getFullName().getValue());
				bkashCustomerKYCJsonObj.put("GENDER ", bkashCustomerKYC.getGender().getValue());
				bkashCustomerKYCJsonObj.put("KYCFORMNUMBER", bkashCustomerKYC.getKycFormNumber().getValue());
				bkashCustomerKYCJsonObj.put("KYCTRACKINGNUMBER", bkashCustomerKYC.getKycTackingNumber().getValue());
				bkashCustomerKYCJsonObj.put("LASTNAME", bkashCustomerKYC.getLastName().getValue());
				bkashCustomerKYCJsonObj.put("MIDDLENAME", bkashCustomerKYC.getMiddleName().getValue());
				bkashCustomerKYCJsonObj.put("MOTHERNAME", bkashCustomerKYC.getMotherName().getValue());
				bkashCustomerKYCJsonObj.put("OCCUPATION", bkashCustomerKYC.getOccupation().getValue());
				bkashCustomerKYCJsonObj.put("PLACEOFBIRTH", bkashCustomerKYC.getPlaceOfBirth().getValue());
				bkashCustomerKYCJsonObj.put("TIN", bkashCustomerKYC.getTin().getValue());

				bkashCustomerKYCJsonArray.put(bkashCustomerKYCJsonObj);

				responseJsonObject.put("STATUS", "0");
				responseJsonObject.put("COLOR", "G");
				responseJsonObject.put("RESPONSE", bkashCustomerKYCJsonArray);
				responseJsonObject.put("MESSAGE", "");
				responseJsonObject.put("RESPONSECODE",ls_responseCode);
				responseJsonObject.put("RESPONSEMESSAGE",ls_responseMessage);
				ls_responseData = responseJsonObject.toString();
			} else { 

				ls_langResCodeMsg=propConfiguration.getResponseCode("getBkashCustomerKYC."+ls_responseCode);			
				ls_responseData = common.ofGetErrDataJsonArray(ls_langResCodeMsg,
						propConfiguration.getMessageOfResCode("commen.title."+ls_langResCodeMsg, "Alert."), 
						propConfiguration.getMessageOfResCode("getBkashCustomerKYC."+ls_responseCode,"","(ENP107)"),
						ls_responseMessage, "Currently Service under maintenance so please try later (ENP107).", ls_responseCode, "R");            		            		            	
			}
		} catch (Exception exception) {
			ls_actualErrMsg = common.ofGetTotalErrString(exception, "");            
			loggerImpl.error(logger,"Exception : " + ls_actualErrMsg,"IN:getBkashCustomerKyc");

			ls_responseData = common.ofGetErrDataJsonArray("999",
					propConfiguration.getMessageOfResCode("commen.title.999", "Alert."),
					propConfiguration.getMessageOfResCode("commen.exception.999","","(ENP108)"),
					exception.getMessage(),"Currently Service under maintenance so please try later (ENP108)","0", "R");			

		}finally {
			loggerImpl.stopAndPrintOptLogs(logger,"All API called successfully.","IN:getBkashCustomerKyc");
			loggerImpl.info(logger,"Response generated and send to client.", "IN:getBkashCustomerKyc");        	
		}
		return ls_responseData;
	}
}
