package com.easynet.controller.ApiController;

import city.nsu.xsd.StudentBalanceDataResponse;
import city.nsu.xsd.StudentBalanceRequest;
import city.nsu.xsd.StudentBalanceResponse;
import com.easynet.configuration.PropConfiguration;
import com.easynet.configuration.SOAPConnector;
import com.easynet.impl.JSONObjectImpl;
import com.easynet.impl.LoggerImpl;
import com.easynet.util.common;
import static com.easynet.util.common.PrintErrLog;
import com.easynet.util.readXML;
import java.util.ArrayList;
import org.apache.ws.axis2.StudentNSUBalanceResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.soap.client.SoapFaultClientException;

@Component
public class StudentNSUBalance {

    static Logger logger = LoggerFactory.getLogger(StudentNSUBalance.class);
    @Autowired
    private SOAPConnector soapConnector;

    @Autowired
    PropConfiguration propConfig;

    public String studentNSUBalance(String reqData) {

        String ls_password = "";
        String ls_paymentType = "";
        String ls_semesterCode = "";
        String ls_studentId = "";
        String ls_userName = "";
        String ls_responseData = "";
        String ls_responseCode = "";
        String ls_responseMessage = "";
        String ls_actualErrMsg = "";
        String ls_langResCodeMsg = "";
        LoggerImpl loggerImpl = null;
        ArrayList<StudentBalanceDataResponse> StudentBalanceDataList = null;

        try {
            loggerImpl = new LoggerImpl();

            loggerImpl.info(logger, "Preparing requset data and calling API.", "IN:studentNSUBalance");
            loggerImpl.generateProfiler("studentNSUBalance");
            loggerImpl.startProfiler("Preparing request data");

            JSONObject reqJsonObj = new JSONObject(reqData);
            ls_paymentType = reqJsonObj.getString("PAYMENTTYPE");
            ls_semesterCode = reqJsonObj.getString("SEMESTERCODE");
            ls_studentId = reqJsonObj.getString("STUDENTID");

            if ((ls_paymentType == null || "".equals(ls_paymentType)) || (ls_semesterCode == null || "".equals(ls_semesterCode)) || (ls_studentId == null || "".equals(ls_studentId))) {

                ls_responseData = common.ofGetErrDataJsonArray("99",
                        propConfig.getMessageOfResCode("commen.title.99", "Validation Failed."),
                        propConfig.getMessageOfResCode("commen.invalid_req_data", ""),
                        "Null values found in request data.", "Invalid Request.", "", "R");

                return ls_responseData;
            }
            ls_userName = readXML.getXmlData("root>CUST_360>AUTHORIZATION>USER_NAME");
            ls_password = readXML.getXmlData("root>CUST_360>AUTHORIZATION>PASSWORD");

            //created object factory object of city.nsu.xsd
            city.nsu.xsd.ObjectFactory nsuxsdObjectFactory = new city.nsu.xsd.ObjectFactory();
            //created object of StudentNSUBalance.
            StudentBalanceRequest studentBalanceRequest = nsuxsdObjectFactory.createStudentBalanceRequest();

            loggerImpl.debug(logger, "Json to xml conversion done.", "IN:studentNSUBalance");

            //setting all the values in the request.
            studentBalanceRequest.setPassword(nsuxsdObjectFactory.createStudentBalanceRequestPassword(ls_password));
            studentBalanceRequest.setPaymentType(nsuxsdObjectFactory.createStudentBalanceRequestPaymentType(ls_paymentType));
            studentBalanceRequest.setSemesterCode(nsuxsdObjectFactory.createStudentBalanceRequestSemesterCode(ls_semesterCode));
            studentBalanceRequest.setStudentId(nsuxsdObjectFactory.createStudentBalanceRequestStudentId(ls_studentId));
            studentBalanceRequest.setUsername(nsuxsdObjectFactory.createStudentBalanceRequestUsername(ls_userName));

            //created object factory object of ws.axis2.
            org.apache.ws.axis2.ObjectFactory axis2ObjectFactory = new org.apache.ws.axis2.ObjectFactory();
            //wrapping the request in StudentNSUBalance.
            org.apache.ws.axis2.StudentNSUBalance studentNSUbalance = axis2ObjectFactory.createStudentNSUBalance();
            studentNSUbalance.setRequest(axis2ObjectFactory.createStudentNSUBalanceRequest(studentBalanceRequest));

            loggerImpl.debug(logger, "studentNSUBalance API calling", "IN:studentNSUBalance");
            loggerImpl.startProfiler("studentNSUBalance API calling.");

            //created response object of StudentNSUBalanceResponse.
            StudentNSUBalanceResponse studentNSUBalanceResponse = null;
            try {
                studentNSUBalanceResponse = (StudentNSUBalanceResponse) soapConnector.callWebService(studentNSUbalance);
            } catch (SoapFaultClientException soapException) {
                ls_actualErrMsg = soapException.getFaultStringOrReason();
                ls_responseData = common.ofGetErrDataJsonArray("999",
                        propConfig.getMessageOfResCode("commen.title.999", "Alert."),
                        propConfig.getMessageOfResCode("commen.exception.999", "", "(ENP239)"),
                        ls_actualErrMsg, "Currently Service under maintenance so please try later (ENP239)", "0", "R");

                ls_actualErrMsg = common.ofGetTotalErrString(soapException, "");
                loggerImpl.error(logger, "SoapFaultClientException : " + ls_actualErrMsg, "EX:studentNSUBalance");
                return ls_responseData;
            }

            loggerImpl.debug(logger, "studentNSUBalance API called successfully.", "IN:studentNSUBalance", studentNSUBalanceResponse);
            loggerImpl.startProfiler("preparing studentNSUBalance API response data.");

            //created StudentBalanceResponse object 
            StudentBalanceResponse xsd_studentBalanceResponse = studentNSUBalanceResponse.getReturn().getValue();

            //getting all the data from the response.
            ls_responseCode = xsd_studentBalanceResponse.getResponseCode().getValue();
            ls_responseMessage = xsd_studentBalanceResponse.getResponseMessage().getValue();

            /*if response is 100 then success.
             *If response is 101 then no records found.
             */
            if (ls_responseCode != null && "100".equals(ls_responseCode)) {

                JSONObject responseJsonObject = new JSONObject();
                JSONArray StudentBalanceResponseJsonArray = new JSONArray();

                StudentBalanceDataList = (ArrayList<StudentBalanceDataResponse>) xsd_studentBalanceResponse.getResponseData();
                for (StudentBalanceDataResponse studentBalanceDataResponse : StudentBalanceDataList) {
                    JSONObjectImpl StudentBalanceResponseJsonObj = new JSONObjectImpl();
                   
                    StudentBalanceResponseJsonObj.put("BALANCE", studentBalanceDataResponse.getBalance().getValue());
                    StudentBalanceResponseJsonObj.put("DEPARTMENTCODE", studentBalanceDataResponse.getDepartmentCode().getValue());
                    StudentBalanceResponseJsonObj.put("NAME", studentBalanceDataResponse.getName().getValue());
                    StudentBalanceResponseJsonObj.put("PAYMENTTYPE", studentBalanceDataResponse.getPaymentType().getValue());
                    StudentBalanceResponseJsonObj.put("STUDENTID", studentBalanceDataResponse.getStudentId().getValue());
                    

                    StudentBalanceResponseJsonArray.put(StudentBalanceResponseJsonObj);
                }

                /*
	          * If transaction happens and API is called successfully.
                 */
                responseJsonObject.put("STATUS", "0");
                responseJsonObject.put("COLOR", "G");
                responseJsonObject.put("RESPONSE", StudentBalanceResponseJsonArray);
                responseJsonObject.put("MESSAGE", "");
                responseJsonObject.put("RESPONSECODE", ls_responseCode);
                responseJsonObject.put("RESPONSEMESSAGE", ls_responseMessage);
                ls_responseData = responseJsonObject.toString();
            }else { 

				ls_langResCodeMsg=propConfig.getResponseCode("studentNSUBalance."+ls_responseCode);			
				ls_responseData = common.ofGetErrDataJsonArray(ls_langResCodeMsg,
						propConfig.getMessageOfResCode("commen.title."+ls_langResCodeMsg, "Alert."), 
						propConfig.getMessageOfResCode("studentNSUBalance."+ls_responseCode,"","(ENP240)"),
						ls_responseMessage, "Currently Service under maintenance so please try later (ENP240).", ls_responseCode, "R");            		            		            	
			}
    

        } catch (Exception exception) {
			ls_actualErrMsg = common.ofGetTotalErrString(exception, "");            
			loggerImpl.error(logger,"Exception : " + ls_actualErrMsg,"IN:studentNSUBalance");

			ls_responseData = common.ofGetErrDataJsonArray("999",
					propConfig.getMessageOfResCode("commen.title.999", "Alert."),
					propConfig.getMessageOfResCode("commen.exception.999","","(ENP241)"),
					exception.getMessage(),"Currently Service under maintenance so please try later (ENP241)","0", "R");			

		}finally {
			loggerImpl.stopAndPrintOptLogs(logger,"All API called successfully.","IN:studentNSUBalance");
			loggerImpl.info(logger,"Response generated and send to client.", "IN:studentNSUBalance");        	
		}
        return ls_responseData;
    }
}
