package com.easynet.controller.ApiController;

import city.bkash.xsd.BkashCustomerKYC;
import city.bkash.xsd.GetBkashCustomerKYCRequest;
import city.bkash.xsd.ObjectFactory;
import com.easynet.configuration.SOAPConnector;
import com.easynet.impl.JSONObjectImpl;
import com.easynet.util.common;
import static com.easynet.util.common.PrintErrLog;
import com.easynet.util.readXML;
import org.apache.ws.axis2.GetBkashCustomerKYC;
import org.apache.ws.axis2.GetBkashCustomerKYCResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.soap.client.SoapFaultClientException;

@Component
public class getBkashCustomerKYC {

    @Autowired
    private SOAPConnector soapConnector;

    public String getBkashCustomerKyc(String reqData) {

        String ls_mobileNumber = "";
        String ls_password = "";
        String ls_userName = "";
        String ls_responseMessage = "";
        String ls_responseData = "";
        String ls_responseCode = "";
        String actualErrMsg = "";

        try {
            JSONObject reqJsonObj = new JSONObject(reqData);
            ls_mobileNumber = reqJsonObj.getString("MOBILENUMBER");

            if ((ls_mobileNumber == null || "".equals(ls_mobileNumber))) {

                ls_responseData = common.ofGetErrDataJsonArray("99", "Validation Failed.", "InValid request.", "Null values found in acct_no key.", "", "", "R");
                return ls_responseData;
            }
            ls_userName = readXML.getXmlData("root>CUST_360>AUTHORIZATION>USER_NAME");
            ls_password = readXML.getXmlData("root>CUST_360>AUTHORIZATION>PASSWORD");

            //get object factory object.
            city.bkash.xsd.ObjectFactory xsdObjectFactory = new ObjectFactory();
            //get the request object.
            GetBkashCustomerKYCRequest getbkashCustomerKYCRequest = xsdObjectFactory.createGetBkashCustomerKYCRequest();

            //setting the values in the request.
            getbkashCustomerKYCRequest.setMobileNumber(xsdObjectFactory.createGetBkashCustomerKYCRequestMobileNumber(ls_mobileNumber));
            getbkashCustomerKYCRequest.setPassword(xsdObjectFactory.createGetBkashCustomerKYCRequestPassword(ls_password));
            getbkashCustomerKYCRequest.setUsername(xsdObjectFactory.createGetBkashCustomerKYCRequestUsername(ls_userName));

            //get the object factory object.
            org.apache.ws.axis2.ObjectFactory axis2ObjectFactory = new org.apache.ws.axis2.ObjectFactory();
            //wrapping the request object in the request.
            GetBkashCustomerKYC getbkashcustomerKYC = axis2ObjectFactory.createGetBkashCustomerKYC();
            getbkashcustomerKYC.setRequest(axis2ObjectFactory.createGetBkashCustomerKYCRequest(getbkashCustomerKYCRequest));

            //get the response object.
            GetBkashCustomerKYCResponse getBkashCustomerKYCResponse = null;
            try {
                getBkashCustomerKYCResponse = (GetBkashCustomerKYCResponse) soapConnector.callWebService(getbkashcustomerKYC);
            } catch (SoapFaultClientException soapException) {
                actualErrMsg = soapException.getFaultStringOrReason();
                PrintErrLog("GetBkashCustomerKYC SoapFaultClientException : " + actualErrMsg);
                ls_responseData = common.ofGetErrDataJsonArray("999", "Alert", "Currently Service under maintenance so please try later (ENP040)", actualErrMsg, "", "0", "R");
                return ls_responseData;
            }

            //get the response object.
            city.bkash.xsd.GetBkashCustomerKYCResponse xsd_getBkashCustomerKYCResponse = getBkashCustomerKYCResponse.getReturn().getValue();

            //getting all the data from the response.
            ls_responseCode = xsd_getBkashCustomerKYCResponse.getResponseCode().getValue();
            ls_responseMessage = xsd_getBkashCustomerKYCResponse.getResponseMessage().getValue();

            /*if response is 100 then success.
	     *If response is 101 then no records found.
             */
            if (ls_responseCode != null && "100".equals(ls_responseCode)) {

                JSONObjectImpl BkashCustomerKYCJsonObj = new JSONObjectImpl();
                JSONObject responseJsonObject = new JSONObject();
                JSONArray BkashCustomerKYCJsonArray = new JSONArray();

                BkashCustomerKYC bkashCustomerKYC = xsd_getBkashCustomerKYCResponse.getResponseData().getValue();
                BkashCustomerKYCJsonObj.put("RESPONSECODE", ls_responseCode);
                BkashCustomerKYCJsonObj.put("RESPONSEMESSAGE", ls_responseMessage);
                BkashCustomerKYCJsonObj.put("ALTCONTACT", bkashCustomerKYC.getAlternativeContact().getValue());
                BkashCustomerKYCJsonObj.put("BKASHRESPONSECODE", bkashCustomerKYC.getBKashResponseCode().getValue());
                BkashCustomerKYCJsonObj.put("BKASHRESPONSEDESC", bkashCustomerKYC.getBKashResponseDescription().getValue());
                BkashCustomerKYCJsonObj.put("BKASHRESULTCODE", bkashCustomerKYC.getBKashResultCode().getValue());
                BkashCustomerKYCJsonObj.put("BKASHRESULTDESC", bkashCustomerKYC.getBKashResultDescription().getValue());
                BkashCustomerKYCJsonObj.put("CONVERSIONID", bkashCustomerKYC.getConversionId().getValue());
                BkashCustomerKYCJsonObj.put("DOB", bkashCustomerKYC.getDateOfBirth().getValue());
                BkashCustomerKYCJsonObj.put("EMAIL", bkashCustomerKYC.getEmail().getValue());
                BkashCustomerKYCJsonObj.put("FATHERNAME", bkashCustomerKYC.getFatherOrHusbandName().getValue());
                BkashCustomerKYCJsonObj.put("FIRSTNAME", bkashCustomerKYC.getFirstName().getValue());
                BkashCustomerKYCJsonObj.put("FULLACTIVATION", bkashCustomerKYC.getFullActivation().getValue());
                BkashCustomerKYCJsonObj.put("FULLNAME", bkashCustomerKYC.getFullName().getValue());
                BkashCustomerKYCJsonObj.put("GENDER ", bkashCustomerKYC.getGender().getValue());
                BkashCustomerKYCJsonObj.put("KYCFORMNUMBER", bkashCustomerKYC.getKycFormNumber().getValue());
                BkashCustomerKYCJsonObj.put("KYCTRACKINGNUMBER", bkashCustomerKYC.getKycTackingNumber().getValue());
                BkashCustomerKYCJsonObj.put("LASTNAME", bkashCustomerKYC.getLastName().getValue());
                BkashCustomerKYCJsonObj.put("MIDDLENAME", bkashCustomerKYC.getMiddleName().getValue());
                BkashCustomerKYCJsonObj.put("MOTHERNAME", bkashCustomerKYC.getMotherName().getValue());
                BkashCustomerKYCJsonObj.put("OCCUPATION", bkashCustomerKYC.getOccupation().getValue());
                BkashCustomerKYCJsonObj.put("PLACEOFBIRTH", bkashCustomerKYC.getPlaceOfBirth().getValue());
                BkashCustomerKYCJsonObj.put("TIN", bkashCustomerKYC.getTin().getValue());

                BkashCustomerKYCJsonArray.put(BkashCustomerKYCJsonObj);

                /*
                 * If transaction happens and API is called successfully.
                 */
                responseJsonObject.put("STATUS", "0");
                responseJsonObject.put("COLOR", "G");
                responseJsonObject.put("RESPONSE", BkashCustomerKYCJsonArray);
                responseJsonObject.put("MESSAGE", "");
                ls_responseData = responseJsonObject.toString();
            } else { //Response other than 100.
                ls_responseData = common.ofGetErrDataJsonArray("999", "Alert", "Currently Service under maintenance so please try later (ENP075).", ls_responseMessage, "", ls_responseCode, "R");
            }
        } catch (Exception err) {
            actualErrMsg = common.ofGetTotalErrString(err, "");
            PrintErrLog("GetBkashCustomerKYC Exception : " + actualErrMsg);
            ls_responseData = common.ofGetErrDataJsonArray("999", "Alert", "Currently Service under maintenance so please try later (ENP072)", actualErrMsg, "", "0", "R");
        }
        return ls_responseData;
    }
}
