package com.easynet.controller.ApiController;

import com.easynet.util.common;
import com.easynet.bean.GetRequestUniqueData;
import com.easynet.configuration.PropConfiguration;
import com.easynet.util.Connectiondb;
import com.easynet.util.apiCall1;
import com.easynet.util.readXML;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Base64;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.easynet.impl.LoggerImpl;

/**
 *This controller used for upload file using 2 API to Citybank server. 
*/
@Controller
public class DocUplodApiController {

	@Autowired
	apiCall1 callApi;
	
	@Autowired
	GetRequestUniqueData getRequestUniqueData;

	@Autowired
	PropConfiguration propConfiguration;
	
	static Logger logger=LoggerFactory.getLogger(DocUplodApiController.class);
	
	/**
	 *This method used to upload file suing API.
	 *@param  requestData json format string data.
	 *@return return the success and failed response.
	 *@apiNote This method used below API<br>
	 *	1.getBearerToken for get access token.<br>
	 *	2.saveAppData get the file upload no.<br>
	 *	3.fileUpload upload file on server.	 
	 * 
	 * */
	public String ofUploadFileData(String requestData) {

		String httpHeaders = "";
		String ls_responseData = "";
		String ls_authorizationJson = "";
		String ls_saveAppDataJson = "";
		String ls_fileUploadJson = "";
		String ls_fileUploadResponseJson = "";
		String ls_statusCd = "";
		String ls_appId = "";
		JSONObject jsonResponse = null;
		String uploadFileName="";
		String ls_FileData="";
		LoggerImpl loggerImpl=null;
		String ls_responseCode="";
		String ls_langResCodeMsg="";
		

		try {
			loggerImpl=new LoggerImpl();
			/* get data from json */
			JSONObject jsonRequest = new JSONObject(requestData);
			
			loggerImpl.info(logger,"Preparing requset data and calling API.","IN:ofUploadFileData");
			
			String ls_apiMethodType = readXML.getXmlData("root>CUST_360>AUTHORIZATION>METHOD_TYPE");
			String ls_apiURL =  readXML.getXmlData("root>CUST_360>AUTHORIZATION>OAUTH_URL");

			// Make request data of authorization.
			JSONObject ls_authorizationReqJson = new JSONObject();
			ls_authorizationReqJson.put("username", readXML.getXmlData("root>CUST_360>AUTHORIZATION>USER_NAME"));
			ls_authorizationReqJson.put("password", readXML.getXmlData("root>CUST_360>AUTHORIZATION>PASSWORD"));

			loggerImpl.debug(logger,"getBearerToken API calling.", "IN:ofUploadFileData");
			
			/*for get the times performance logs.*/
			loggerImpl.generateProfiler("ofUploadFileData");
			loggerImpl.startProfiler("getBearerToken API Calling.");
			
			/* Call API for get access token from City Bank*/
			ls_authorizationJson = callApi.PostApi(ls_apiMethodType, ls_apiURL, ls_authorizationReqJson.toString(),	null);
			
			loggerImpl.debug(logger,"getBearerToken API called successfully.", "IN:ofUploadFileData");
			
			loggerImpl.startProfiler("Reading res. and preparing req. data for saveAppData API.");
			
			if (ls_authorizationJson.trim().substring(0, 1).equals("[")) {
				jsonResponse=new JSONArray(ls_authorizationJson).getJSONObject(0);
			}else if (ls_authorizationJson.trim().substring(0, 1).equals("{")) {
				jsonResponse=new JSONObject(ls_authorizationJson);                  
			}

			/* Get status for check response data */
			ls_statusCd = jsonResponse.getString("STATUS");
			if (ls_statusCd != null && !"".equals(ls_statusCd) && "0".equals(ls_statusCd)) {
				/* get access token data from response key */
				ls_authorizationJson = jsonResponse.getString("RESPONSE");
				
				loggerImpl.debug(logger,"preparing request data for saveAppData API.", "IN:ofUploadFileData");
				
				/* Set the access token data */
				JSONObject httpHeadersJson = new JSONObject();
				httpHeadersJson.put("ACCESS_TOKEN", ls_authorizationJson);
				httpHeaders = httpHeadersJson.toString();

				JSONObject saveAppData = new JSONObject();
				saveAppData.put("customerName", "");
				saveAppData.put("cbNumber", jsonRequest.optString("cbNumber", ""));
				saveAppData.put("accountNumber", jsonRequest.optString("accountNumber", ""));
				saveAppData.put("solId", "");
				saveAppData.put("userId", "");

				//Save App Data Api Call
				ls_apiURL = readXML.getXmlData("root>FILE_UPLOAD_DTL>SAVE_FILE_DATA>API_URL");
				ls_apiMethodType = readXML.getXmlData("root>FILE_UPLOAD_DTL>SAVE_FILE_DATA>METHOD_TYPE");

				loggerImpl.debug(logger,"Calling saveAppData API.", "IN:ofUploadFileData");
				loggerImpl.startProfiler("saveAppData API calling.");
				
				//Call City Bank API for Save the File reference on City Bank Server
				ls_saveAppDataJson = callApi.PostApi(ls_apiMethodType, ls_apiURL, saveAppData.toString(),httpHeaders);
				
				loggerImpl.startProfiler("Reading res. and preparing req. data for fileUpload API.");
				loggerImpl.debug(logger,"saveAppData API called successfully.", "IN:ofUploadFileData",ls_saveAppDataJson);
				
				if (ls_saveAppDataJson.trim().substring(0, 1).equals("[")) {
					jsonResponse=new JSONArray(ls_saveAppDataJson).getJSONObject(0);
				}else if (ls_saveAppDataJson.trim().substring(0, 1).equals("{")) {
					jsonResponse=new JSONObject(ls_saveAppDataJson);                  
				}

				ls_statusCd = jsonResponse.getString("STATUS");
				if (ls_statusCd != null && !"".equals(ls_statusCd) && "0".equals(ls_statusCd)) {
					
					loggerImpl.debug(logger,"preparing request data for fileUpload API.", "IN:ofUploadFileData");
					
					/* get app id from response key */
					ls_appId = jsonResponse.getString("RESPONSE");

					JSONObject fileUploadRequest = new JSONObject();
					fileUploadRequest.put("appId", ls_appId);
					fileUploadRequest.put("remarks", jsonRequest.getString("DOC_NM"));
										
					ls_apiURL =readXML.getXmlData("root>FILE_UPLOAD_DTL>UPLOAD_FILE_DATA>API_URL");		

					/// Call City Bank API for Upload File on Server
					uploadFileName=jsonRequest.getString("FILE_NAME");

					ls_FileData=jsonRequest.getString("FILE_DATA");					
					
					InputStream fileInputData = new ByteArrayInputStream(Base64.getDecoder().decode(ls_FileData));					 

					loggerImpl.debug(logger,"Calling fileUpload API.", "IN:ofUploadFileData");
					loggerImpl.startProfiler("Calling fileUpload API.");
					
					ls_fileUploadJson = callApi.PostFileUploadApi(ls_apiURL, fileUploadRequest, httpHeaders,uploadFileName,fileInputData);					
					
					loggerImpl.startProfiler("Reading res. and preparing req. data for DB API.");					
					loggerImpl.debug(logger,"fileUpload API called successfully.", "IN:ofUploadFileData",ls_fileUploadJson);
					
					if (ls_fileUploadJson.trim().substring(0, 1).equals("[")) {
						jsonResponse=new JSONArray(ls_fileUploadJson).getJSONObject(0);
					}else if (ls_fileUploadJson.trim().substring(0, 1).equals("{")) {
						jsonResponse=new JSONObject(ls_fileUploadJson);                  
					}					

					ls_statusCd = jsonResponse.getString("STATUS");
					if (ls_statusCd != null && !"".equals(ls_statusCd) && "0".equals(ls_statusCd)) {
						/* get response from response key */
						ls_fileUploadResponseJson = jsonResponse.getString("FILE_NM");

						JSONObject saveAppDataCBS = new JSONObject();
						saveAppDataCBS.put("REQ_FLAG", "D");
						saveAppDataCBS.put("ACTIVITY_TYPE", "UPLOAD_DOC");
						saveAppDataCBS.put("KEY_VAL", "");
						saveAppDataCBS.put("USER_NAME", jsonRequest.getString("USER_NAME"));
						saveAppDataCBS.put("DOC_ID", jsonRequest.getString("DOC_ID"));
						saveAppDataCBS.put("DOC_NO", jsonRequest.getString("DOC_NO"));
						saveAppDataCBS.put("DOC_NM", jsonRequest.getString("DOC_NM"));
						saveAppDataCBS.put("DOC_FILE_NM", ls_fileUploadResponseJson);
						saveAppDataCBS.put("DOC_API_ID", ls_appId);
						saveAppDataCBS.put("CHANGE_REQ_TYPE", jsonRequest.getString("CHANGE_REQ_TYPE"));
						saveAppDataCBS.put("CHANNEL", jsonRequest.getString("CHANNEL"));
						saveAppDataCBS.put("DEVICE_IP", jsonRequest.getString("DEVICE_IP"));
						
						loggerImpl.debug(logger,"DB API calling.", "IN:ofUploadFileData");
						
						ls_responseData = funcProcessUpdate(saveAppDataCBS.toString());
						
					} else {
						ls_responseCode=jsonResponse.getString("RESPONSECODE");
						ls_langResCodeMsg=propConfiguration.getResponseCode("fileUpload."+ls_responseCode);
						
						ls_responseData = common.ofGetErrDataJsonArray(ls_langResCodeMsg,
								propConfiguration.getMessageOfResCode("commen.title."+ls_langResCodeMsg, "Alert."),
								propConfiguration.getMessageOfResCode("fileUpload."+ls_responseCode,"","(ENP137)"),
								ls_fileUploadJson,"Currently Service under maintenance so please try later (ENP137).", "0", "R");	
												
						return ls_responseData;
					}
				} else {
					
					ls_responseCode=jsonResponse.getString("RESPONSECODE");
					ls_langResCodeMsg=propConfiguration.getResponseCode("saveAppData."+ls_responseCode);
					
					ls_responseData = common.ofGetErrDataJsonArray(ls_langResCodeMsg,
							propConfiguration.getMessageOfResCode("commen.title."+ls_langResCodeMsg, "Alert."),
							propConfiguration.getMessageOfResCode("saveAppData."+ls_responseCode,"","(ENP138)"),
							ls_saveAppDataJson,"Currently Service under maintenance so please try later (ENP138).", "0", "R");

					return ls_responseData;
				}
			} else {											
				ls_responseCode=jsonResponse.getString("RESPONSECODE");
				ls_langResCodeMsg=propConfiguration.getResponseCode("saveAppData."+ls_responseCode);
				
				ls_responseData = common.ofGetErrDataJsonArray(ls_langResCodeMsg,
						propConfiguration.getMessageOfResCode("commen.title."+ls_langResCodeMsg, "Alert."),
						propConfiguration.getMessageOfResCode("saveAppData."+ls_responseCode,"","(ENP139)"),
						ls_authorizationJson,"Currently Service under maintenance so please try later (ENP139).", "0", "R");
								
				return ls_responseData;
			}
			return ls_responseData;

		} catch (Exception exception) {
			loggerImpl.error(logger,"Exception : " + common.ofGetTotalErrString(exception, ""),"IN:ofUploadFileData");
			ls_responseData = common.ofGetErrDataJsonArray("999",
					propConfiguration.getMessageOfResCode("commen.title.999", "Alert."),
					propConfiguration.getMessageOfResCode("commen.exception.999","","(ENP052)"),
					exception.getMessage(),"Currently Service under maintenance so please try later (ENP052).", "0", "R");
			
								
			return ls_responseData;
		} finally {
			loggerImpl.stopAndPrintOptLogs(logger,"All API called successfully.","IN:ofUploadFileData");
			loggerImpl.info(logger,"Response generated and send to client.", "IN:ofUploadFileData");
			System.gc();
		}
	}


	public String funcProcessUpdate(String RequestData) {
		String ls_return = null;
		Connection con = null;
		CallableStatement cs = null;
		LoggerImpl loggerImpl=null;

		try {
			loggerImpl=new LoggerImpl();
			
			if (RequestData.trim().substring(0, 1).equals("[")) {
				RequestData = (String) RequestData.substring(1, RequestData.length() - 1);
			} else {
				RequestData = (String) RequestData;
			}

			con = Connectiondb.Getconnection();
			cs = con.prepareCall("{ CALL PACK_NOTIF_PROCESS.PROC_PROCESS_NOTIF(?,?) }");
			cs.setString(1, RequestData);
			cs.registerOutParameter(2, Types.CLOB);
			cs.execute(); // execute stored procedure

			Clob clob_data = cs.getClob(2);
			if(clob_data==null) {
				ls_return=common.ofGetErrDataJsonObject("999",
						propConfiguration.getMessageOfResCode("commen.title.999", "Alert."),
						propConfiguration.getMessageOfResCode("commen.exception.999","","(ENP059)"),
						"Null response get from procedure.",
						"Currently Service under maintenance so please try later (ENP059).","0","R");
								
			}else
			{
				ls_return = clob_data.getSubString(1, (int) clob_data.length());
			}

		} catch (SQLException ex) {
			loggerImpl.error(logger,common.ofGetTotalErrString(ex, "SQLException :"),"IN:funcProcessUpdate");
			ls_return=common.ofGetErrDataJsonObject("999",
					propConfiguration.getMessageOfResCode("commen.title.999", "Alert."),
					propConfiguration.getMessageOfResCode("commen.exception.999","","(ENP060)"),
					ex.getMessage(),
					"Currently Service under maintenance so please try later (ENP060)","0","R");	
			
		} catch (Exception ex) {
			loggerImpl.error(logger,common.ofGetTotalErrString(ex, "Exception :"),"IN:funcProcessUpdate");						
			ls_return=common.ofGetErrDataJsonObject("999",
					propConfiguration.getMessageOfResCode("commen.title.999", "Alert."),
					propConfiguration.getMessageOfResCode("commen.exception.999","","(ENP061)"),
					ex.getMessage(),
					"Currently Service under maintenance so please try later (ENP061).","0","R");
			
		} finally {
			// It's important to close the statement when you are done with
			if (cs != null) {
				try {
					cs.close();
				} catch (SQLException e) {
					/* ignored */
				}
			}

			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					/* ignored */
				}
			}
		}

		return ls_return;
	}
}
