package com.easynet.controller.ApiController;

import city.nsu.xsd.StudentTransactionDataResponse;
import city.nsu.xsd.StudentTransactionRequest;
import city.nsu.xsd.StudentTransactionResponse;
import com.easynet.configuration.PropConfiguration;
import com.easynet.configuration.SOAPConnector;
import com.easynet.impl.JSONObjectImpl;
import com.easynet.impl.LoggerImpl;
import com.easynet.util.common;
import static com.easynet.util.common.PrintErrLog;
import com.easynet.util.readXML;
import java.util.ArrayList;
import org.apache.ws.axis2.StudentNSUTransactionResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.soap.client.SoapFaultClientException;

@Component
public class StudentNSUTransaction {

    static Logger logger=LoggerFactory.getLogger(StudentNSUTransaction.class);
    @Autowired
    private SOAPConnector soapConnector;
    
    @Autowired
    PropConfiguration propConfig;

    public String studentNSUTransaction(String reqData) {

        String ls_accountNumber = "";
        String ls_comments = "";
        String ls_name = "";
        String ls_paidAmount = "";
        String ls_password = "";
        String ls_paymentType = "";
        String ls_paymentVehicleId = "";
        String ls_semesterCode = "";
        String ls_studentId = "";
        String ls_userName = "";
        String ls_responseCode = "";
        String ls_responseData = "";
        String ls_responseMessage = "";
        String ls_transactionRefNumber = "";
        String ls_actualErrMsg = "";
        String ls_langResCodeMsg="";
        ArrayList<StudentTransactionDataResponse> StudentTransactionDataList = null;
        LoggerImpl loggerImpl=null;

        try {
            loggerImpl=new LoggerImpl();
        
            loggerImpl.info(logger,"Preparing requset data and calling API.","IN:studentNSUTransaction");
            loggerImpl.generateProfiler("studentNSUTransaction");
            loggerImpl.startProfiler("Preparing request data");
            
            JSONObject reqJsonObj = new JSONObject(reqData);
            ls_accountNumber = reqJsonObj.getString("ACCOUNTNUMBER");
            ls_comments = reqJsonObj.getString("COMMENTS");
            ls_name = reqJsonObj.getString("NAME");
            ls_paidAmount = reqJsonObj.getString("PAIDAMOUNT");
            ls_paymentType = reqJsonObj.getString("PAYMENTTYPE");
            ls_paymentVehicleId = reqJsonObj.getString("PAYMENTVEHICLEID");
            ls_semesterCode = reqJsonObj.getString("SEMESTERCODE");
            ls_studentId = reqJsonObj.getString("STUDENTID");

            if ((ls_accountNumber == null || "".equals(ls_accountNumber)) || (ls_comments == null || "".equals(ls_comments)) || (ls_name == null || "".equals(ls_name))
                    || (ls_paidAmount == null || "".equals(ls_paidAmount)) || (ls_paymentType == null || "".equals(ls_paymentType)) || (ls_paymentVehicleId == null || "".equals(ls_paymentVehicleId))
                    || (ls_semesterCode == null || "".equals(ls_semesterCode)) || (ls_studentId == null || "".equals(ls_studentId))) {

                ls_responseData = common.ofGetErrDataJsonArray("99",
						propConfig.getMessageOfResCode("commen.title.99", "Validation Failed."),
						propConfig.getMessageOfResCode("commen.invalid_req_data",""),		
						"Null values found in request data.", "Invalid Request.","", "R");

				return ls_responseData;
            }
            ls_userName = readXML.getXmlData("root>CUST_360>AUTHORIZATION>USER_NAME");
            ls_password = readXML.getXmlData("root>CUST_360>AUTHORIZATION>PASSWORD");

            //created object factory object of city.nsu.xsd
            city.nsu.xsd.ObjectFactory nsuxsdObjectFactory = new city.nsu.xsd.ObjectFactory();
            //created request object of StudentTransactionRequest from object factory.
            StudentTransactionRequest studentTransactionRequest = nsuxsdObjectFactory.createStudentTransactionRequest();
            
            loggerImpl.debug(logger,"Json to xml conversion done.", "IN:studentNSUTransaction");

            //setting all the values in the request.
            studentTransactionRequest.setAccountNumber(nsuxsdObjectFactory.createStudentTransactionRequestAccountNumber(ls_accountNumber));
            studentTransactionRequest.setComments(nsuxsdObjectFactory.createStudentTransactionRequestComments(ls_name));
            studentTransactionRequest.setName(nsuxsdObjectFactory.createStudentTransactionRequestName(ls_name));
            studentTransactionRequest.setPaidAmount(nsuxsdObjectFactory.createStudentTransactionRequestPaidAmount(ls_paidAmount));
            studentTransactionRequest.setPassword(nsuxsdObjectFactory.createStudentTransactionRequestPassword(ls_password));
            studentTransactionRequest.setPaymentType(nsuxsdObjectFactory.createStudentTransactionRequestPaymentType(ls_paymentType));
            studentTransactionRequest.setPaymentVehicleId(nsuxsdObjectFactory.createStudentTransactionRequestPaymentVehicleId(ls_paymentVehicleId));
            studentTransactionRequest.setSemesterCode(nsuxsdObjectFactory.createStudentTransactionRequestSemesterCode(ls_semesterCode));
            studentTransactionRequest.setStudentId(nsuxsdObjectFactory.createStudentTransactionRequestStudentId(ls_studentId));
            studentTransactionRequest.setUsername(nsuxsdObjectFactory.createStudentTransactionRequestUsername(ls_userName));

            //created object factory object of ws.axis2.
            org.apache.ws.axis2.ObjectFactory axis2ObjectFactory = new org.apache.ws.axis2.ObjectFactory();
            //wrapping the request in StudentNSUTransaction.
            org.apache.ws.axis2.StudentNSUTransaction studentNSUTransaction = axis2ObjectFactory.createStudentNSUTransaction();
            studentNSUTransaction.setRequest(axis2ObjectFactory.createStudentNSUTransactionRequest(studentTransactionRequest));
            
            loggerImpl.debug(logger,"studentNSUTransaction API calling", "IN:studentNSUTransaction");
            loggerImpl.startProfiler("studentNSUTransaction API calling.");

            //created response object of StudentNSUTransactionResponse.
            StudentNSUTransactionResponse studentNSUTransactionResponse = null;
            try {
                studentNSUTransactionResponse = (StudentNSUTransactionResponse) soapConnector.callWebService(studentNSUTransaction);
            }catch (SoapFaultClientException soapException) {
				ls_actualErrMsg = soapException.getFaultStringOrReason();
				ls_responseData = common.ofGetErrDataJsonArray("999", 
						propConfig.getMessageOfResCode("commen.title.999", "Alert."),
						propConfig.getMessageOfResCode("commen.exception.999","","(ENP236)"),
						ls_actualErrMsg, "Currently Service under maintenance so please try later (ENP236)", "0", "R");

				ls_actualErrMsg = common.ofGetTotalErrString(soapException, "");
				loggerImpl.error(logger,"SoapFaultClientException : " + ls_actualErrMsg,"EX:getCASADetailsStatement");
				return ls_responseData;
			}
            
            loggerImpl.debug(logger,"studentNSUTransaction API called successfully.", "IN:studentNSUTransaction",studentNSUTransactionResponse);
            loggerImpl.startProfiler("preparing studentNSUTransaction API response data.");

            //created StudentTransactionResponse object.
            StudentTransactionResponse studentTransactionResponse = studentNSUTransactionResponse.getReturn().getValue();

            //getting all the values from response.
            ls_responseCode = studentTransactionResponse.getResponseCode().getValue();
            ls_responseMessage = studentTransactionResponse.getResponseMessage().getValue();
            ls_transactionRefNumber = studentTransactionResponse.getTransactionRefNumber().getValue();

            /*if response is 100 then success.
             *If response is 101 then no records found.
             */
            if (ls_responseCode != null && "100".equals(ls_responseCode)) {

                JSONObject responseJsonObject = new JSONObject();
                JSONArray StudentTransactionResponseJsonArray = new JSONArray();

                StudentTransactionDataList = (ArrayList<StudentTransactionDataResponse>) studentTransactionResponse.getResponseData();
                for (StudentTransactionDataResponse StudentList : StudentTransactionDataList) {
                    JSONObjectImpl StudentTransactionResponseJsonObj = new JSONObjectImpl();
                    StudentTransactionResponseJsonObj.put("DEPARTMENTCODE", StudentList.getDepartmentCode().getValue());
                    StudentTransactionResponseJsonObj.put("STUDENTID", StudentList.getStudentId().getValue());
                    StudentTransactionResponseJsonObj.put("TRANSACTIONREFNUMBER", ls_transactionRefNumber);

                    StudentTransactionResponseJsonArray.put(StudentTransactionResponseJsonObj);
                }

                /*
	        * If transaction happens and API is called successfully.
                 */
                responseJsonObject.put("STATUS", "0");
                responseJsonObject.put("COLOR", "G");
                responseJsonObject.put("RESPONSE", StudentTransactionResponseJsonArray);
                responseJsonObject.put("MESSAGE", "");
                responseJsonObject.put("RESPONSECODE", ls_responseCode);
                responseJsonObject.put("RESPONSEMESSAGE", ls_responseMessage);
                ls_responseData = responseJsonObject.toString();
            }else { 

				ls_langResCodeMsg=propConfig.getResponseCode("studentNSUTransaction."+ls_responseCode);			
				ls_responseData = common.ofGetErrDataJsonArray(ls_langResCodeMsg,
						propConfig.getMessageOfResCode("commen.title."+ls_langResCodeMsg, "Alert."), 
						propConfig.getMessageOfResCode("studentNSUTransaction."+ls_responseCode,"","(ENP237)"),
						ls_responseMessage, "Currently Service under maintenance so please try later (ENP237).", ls_responseCode, "R");            		            		            	
			}

        }catch (Exception exception) {
			ls_actualErrMsg = common.ofGetTotalErrString(exception, "");            
			loggerImpl.error(logger,"Exception : " + ls_actualErrMsg,"IN:studentNSUTransaction");

			ls_responseData = common.ofGetErrDataJsonArray("999",
					propConfig.getMessageOfResCode("commen.title.999", "Alert."),
					propConfig.getMessageOfResCode("commen.exception.999","","(ENP238)"),
					exception.getMessage(),"Currently Service under maintenance so please try later (ENP238)","0", "R");			

		}finally {
			loggerImpl.stopAndPrintOptLogs(logger,"All API called successfully.","IN:studentNSUTransaction");
			loggerImpl.info(logger,"Response generated and send to client.", "IN:studentNSUTransaction");        	
		}
        return ls_responseData;

    }
}
