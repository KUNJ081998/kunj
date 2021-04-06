package com.easynet.controller.UpdateController;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.easynet.bean.GetRequestUniqueData;
import com.easynet.configuration.PropConfiguration;
import com.easynet.controller.ApiController.AccountAPI.updateDetail.DoUpdateAcctDetail;
import com.easynet.controller.ApiController.CardAPI.UpdateDetail.DoUpdateCardDetail;
import com.easynet.impl.JSONObjectImpl;
import com.easynet.util.GetData;
import com.easynet.util.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.easynet.impl.LoggerImpl;
/**
 * This is mapping class for update all account related details
 * 
 * @author- Sagar Umate
 * @since- 12/02/2021
 * 
 */
@Component
public class UpdateDetailController {

	@Autowired
	DoUpdateAcctDetail doUpdateAcctdetail;

	@Autowired
	DoUpdateCardDetail doUpdateCardDetail;

	@Autowired
	GetData getData;

	@Autowired
	GetRequestUniqueData getRequestUniqueData;
	
	@Autowired
	PropConfiguration propConfiguration;
	
	static Logger logger=LoggerFactory.getLogger(UpdateDetailController.class);

	/**
	 * This is the mapping method for update email id detail as per source of
	 * account.
	 * @param asRequestData string format json request data.
	 * @return return the api response.
	 * @since-12/02/2021
	 * @apiNote this method used below API.<br>
	 * 	1.ChangeFinacleEmailAdderss for update finacle email add.<br>
	 * 	2.ChangeCardEmailAdderss used to update the email id on card.<br>
	 */
	public String ofUpdateEmailId(String as_requestData) {
		String ls_sourceCode = "";
		String ls_responseData = "";
		String actualErrMsg = "";
		LoggerImpl loggerImpl=null;

		try {
			loggerImpl=new LoggerImpl();
			JSONObject jsonRequestData = new JSONObject(as_requestData);
			ls_sourceCode = jsonRequestData.getString("APP_INDICATOR");

			if (ls_sourceCode != null && "FINACLE".equalsIgnoreCase(ls_sourceCode)) {
				// Call method for update the email id.
				ls_responseData = doUpdateAcctdetail.ofUpdateFinacleEmailId(as_requestData);

			} else if (ls_sourceCode != null && "ABABIL".equalsIgnoreCase(ls_sourceCode)) {
				// since no api found.

			} else if (ls_sourceCode != null && "TRANZWARE".equalsIgnoreCase(ls_sourceCode)) {
				// call api for update card Email Id.
				ls_responseData = doUpdateCardDetail.ofUpdateCardEmailId(as_requestData);

			} else {
				ls_responseData = common.ofGetErrDataJsonArray("99", 
						propConfiguration.getMessageOfResCode("commen.title.99", "Alert."),
						propConfiguration.getMessageOfResCode("commen.invalid_req_data","",""),						
						"Wrong source mapping found.", "InValid request Data.", "", "R");
			}
		} catch (Exception exception) {
			actualErrMsg = common.ofGetTotalErrString(exception, "");
			loggerImpl.error(logger,"Exception : " + actualErrMsg,"IN:ofUpdateEmailId");
			
			ls_responseData = common.ofGetErrDataJsonArray("999",
					propConfiguration.getMessageOfResCode("commen.title.999", "Alert."),
					propConfiguration.getMessageOfResCode("commen.exception.999","","(ENP085)"),
					exception.getMessage(),"Currently Service under maintenance so please try later (ENP085).", "0", "R");
		}
		return ls_responseData;
	}

	/**
	 * This is the mapping method for update Mobile Number detail as per source of
	 * account.
	 * @param asRequestData string format json request data.
	 * @return return the same response of API.
	 * @since-12/02/2021
	 * @apiNote This method used below api.<br>
	 * 	1.ChangeFinacleMobileNumber used for update finacle mobile no.<br>
	 * 	2.ChangeCardMobileNumber update mobile no. on card.<br> 
	 * 
	 */

	public String OfUpdateMobileNo(String asRequestData) {
		String ls_sourceCode = "";
		String ls_responseData = "";
		String actualErrMsg = "";
		LoggerImpl loggerImpl=null;
		
		try {
			loggerImpl=new LoggerImpl();
			JSONObject jsonRequestData = new JSONObject(asRequestData);
			ls_sourceCode = jsonRequestData.getString("APP_INDICATOR");

			if (ls_sourceCode != null && "FINACLE".equalsIgnoreCase(ls_sourceCode)) {
				// update mobileNO for finacle Account
				ls_responseData = doUpdateAcctdetail.ofUpdateFinacleMobileNo(asRequestData);

			} else if (ls_sourceCode != null && "ABABIL".equalsIgnoreCase(ls_sourceCode)) {
				// since no api found.

			} else if (ls_sourceCode != null && "TRANZWARE".equalsIgnoreCase(ls_sourceCode)) {
				// update card mobile Number
				ls_responseData = doUpdateCardDetail.ofUpdateCardMobileNo(asRequestData);

			} else {
				ls_responseData = common.ofGetErrDataJsonArray("99",
						propConfiguration.getMessageOfResCode("commen.title.99", "Alert."),
						propConfiguration.getMessageOfResCode("commen.invalid_req_data","",""),
						"Wrong source mapping found.", "InValid request Data.", "", "R");												
			}
		} catch (Exception exception) {
			actualErrMsg = common.ofGetTotalErrString(exception, "");
			loggerImpl.error(logger,"Exception : " + actualErrMsg,"IN:OfUpdateMobileNo");
			
			ls_responseData = common.ofGetErrDataJsonArray("999",
					propConfiguration.getMessageOfResCode("commen.title.999", "Alert."),
					propConfiguration.getMessageOfResCode("commen.exception.999","","(ENP086)"),
					exception.getMessage(), "Currently Service under maintenance so please try later (ENP086)", "0", "R");
		}
		return ls_responseData;
	}

	/**
	 * This API used to update the email address for all type of source in loop for given customer ID Details.
	 * @param requestData -request data in String json format.
	 * @return return the string format json data.
	 * @author sagar Umate
	 * @since 17/02/2021 05.15PM
	 * @apiNote this method call api in loop for every customer id and update the all api response code using database api.
	 * */

	public String ofVerifyAndUpdateEmailId(String requestData) {
		String ls_dbResponseData = "";
		String ls_responseData = "";
		String ls_responseStatus = "";
		String ls_responseCode = "";
		String ls_responseMessage = "";
		String actualErrMsg="";
		JSONArray updateEmailCustList;// used for store json object which is going to update.
		int updateEmailCustListCount;
		JSONArray updateEmailCustListWithRes;//used for store json object list of with response code.
		String ls_emailID="";
		LoggerImpl loggerImpl=null;

		try {
			loggerImpl=new LoggerImpl();
			loggerImpl.info(logger,"Preparing requset data and calling API.","ofVerifyAndUpdateEmailId");
			/*for get the times performance logs.*/
			loggerImpl.generateProfiler("ofVerifyAndUpdateEmailId");			
			
			JSONObjectImpl jsonRequestData = new JSONObjectImpl(requestData);
			JSONObject jsonAPIResponse = null;
			JSONObject jsonAPIActualResponse = null;
			
			ls_emailID=jsonRequestData.getString("NEWEMAILID");
			updateEmailCustList=jsonRequestData.getJSONArray("REQ_DATA");
			updateEmailCustListCount=updateEmailCustList.length();
			updateEmailCustListWithRes=new JSONArray();
			
			if(updateEmailCustListCount==0) {
				ls_responseData = common.ofGetErrDataJsonArray("99", 
						propConfiguration.getMessageOfResCode("commen.title.99", "Alert."),
						propConfiguration.getMessageOfResCode("commen.invalid_req_data","",""),						
						"no data found in REQ_DATA key for update Email ID detail.", "Invalid request.", "0", "R");
				return ls_responseData;
			}
			
			for(int i=0; i< updateEmailCustListCount; i++) {

				JSONObjectImpl updateEmailCustIdJson  =new JSONObjectImpl(updateEmailCustList.getJSONObject(i));
				updateEmailCustIdJson.put("NEWEMAILID", ls_emailID);
				/* Call API to update the email id*/
				loggerImpl.debug(logger,"API calling for update Email-Id.", "IN:ofVerifyAndUpdateEmailId");
				loggerImpl.startProfiler("Calling Email-ID Update API."+i);
				
				ls_responseData = ofUpdateEmailId(updateEmailCustIdJson.toString());
								
				loggerImpl.startProfiler("Reading response data.");				
				loggerImpl.debug(logger,"Update Email-Id API called.", "IN:ofVerifyAndUpdateEmailId");
				
				if (ls_responseData.trim().substring(0, 1).equals("[")) {
					jsonAPIResponse = new JSONArray(ls_responseData).getJSONObject(0);
				} else if (ls_responseData.trim().substring(0, 1).equals("{")) {
					jsonAPIResponse = new JSONObject(ls_responseData);
				}
				
				/* Get response status for check API called or not. */
				ls_responseStatus = jsonAPIResponse.getString("STATUS");
				
				/* Check response code of API */
				if (ls_responseStatus != null && "0".equals(ls_responseStatus)) {
					/* Get the actual API response. */
					jsonAPIActualResponse = jsonAPIResponse.getJSONArray("RESPONSE").getJSONObject(0);														
					ls_responseCode = jsonAPIActualResponse.getString("RESPONSECODE");
					ls_responseMessage = jsonAPIActualResponse.getString("RESPONSEMESSAGE");					
					
				} else {
					/* do not return in case of error generate send the error message to db API.*/
					ls_responseCode = jsonAPIResponse.getString("STATUS");
					ls_responseMessage = jsonAPIResponse.getString("MESSAGE");			
				}
								
				//set the response code and response data in object for pass to db API.
				updateEmailCustIdJson.put("RESPONSE_CD", ls_responseCode);
				updateEmailCustIdJson.put("RESPONSE_MSG", ls_responseMessage);				
				updateEmailCustListWithRes.put(updateEmailCustIdJson);				
			}
			
			//put updated request data with response code in req_data key.
			jsonRequestData.put("REQ_DATA", updateEmailCustListWithRes);
			
			loggerImpl.debug(logger,"DB calling for update API response.", "IN:ofVerifyAndUpdateEmailId");
			loggerImpl.startProfiler("DB API calling.");
			
			//call DB API for update the response code details.
			ls_dbResponseData = getData.ofGetResponseData(jsonRequestData.toString());
			return ls_dbResponseData;
			
		}catch(Exception exception) {
			actualErrMsg = common.ofGetTotalErrString(exception, "");
			loggerImpl.error(logger,"Exception : " + actualErrMsg,"IN:ofVerifyAndUpdateEmailId");
			
			ls_responseData = common.ofGetErrDataJsonArray("999",
					propConfiguration.getMessageOfResCode("commen.title.999", "Alert."),
					propConfiguration.getMessageOfResCode("commen.exception.999","","(ENP088)"), 
					exception.getMessage(),"Currently Service under maintenance so please try later (ENP088)", "0", "R");
									
			return ls_responseData;
		}finally {
			loggerImpl.stopAndPrintOptLogs(logger,"All API called successfully.","IN:ofVerifyAndUpdateEmailId");
			loggerImpl.info(logger,"Response generated and send to client.", "IN:ofVerifyAndUpdateEmailId");
		}
	}

	
	/**
	 * This API used to update the mobile Number for all type of source in loop for given customer id details.
	 * @param requestData -request data in String json format.
	 * @return return the string format json data. 
	 * @author Sagar Umate
	 * @since 17/02/2021 05.25PM
	 * @apiNote this method call api in loop for every customer id and update the all api response code using database api.
	 * */

	public String ofVerifyAndUpdateMobileNo(String requestData) {
		String ls_dbResponseData = "";
		String ls_responseData = "";
		String ls_responseStatus = "";
		String ls_responseCode = "";
		String ls_responseMessage = "";
		String actualErrMsg="";
		JSONArray updateMobCustList;// used for store json object which is going to update.
		int updateMobCustListCount;
		JSONArray updateMobCustListWithRes;//used for store json object list of with response code.
		String ls_newMobilNo="";
		JSONObject jsonAPIActualResponse = null;
		LoggerImpl loggerImpl=null;
		
		try {
			loggerImpl=new LoggerImpl();
			loggerImpl.info(logger,"Preparing requset data and calling API.","IN:ofVerifyAndUpdateMobileNo");
			/*for get the times performance logs.*/
			loggerImpl.generateProfiler("ofVerifyAndUpdateMobileNo");
			
			JSONObjectImpl jsonRequestData = new JSONObjectImpl(requestData);
			JSONObject jsonAPIResponse = null;

			ls_newMobilNo=jsonRequestData.getString("NEWMOBILENO");
			updateMobCustList=jsonRequestData.getJSONArray("REQ_DATA");
			updateMobCustListCount=updateMobCustList.length();
			updateMobCustListWithRes=new JSONArray();
			
			if(updateMobCustListCount==0) {
				ls_responseData = common.ofGetErrDataJsonArray("99",
						propConfiguration.getMessageOfResCode("commen.title.99", "Validation Failed."),
						propConfiguration.getMessageOfResCode("commen.invalid_req_data",""),		
						"no data found in REQ_DATA key for update Mobile number detail.", "Invalid request.", "0", "R");
				return ls_responseData;
			}
			
			for(int i=0;i< updateMobCustListCount;i++) {

				JSONObjectImpl updateMobCustIdJson  =new JSONObjectImpl(updateMobCustList.getJSONObject(i));				
				updateMobCustIdJson.put("NEWMOBILENO",ls_newMobilNo);
				
				loggerImpl.debug(logger,"Calling API to update Mobile No.", "IN:ofVerifyAndUpdateMobileNo");
				loggerImpl.startProfiler("Calling Mobile Update API."+i);
				/* Call API to update the email id*/
				ls_responseData = OfUpdateMobileNo(updateMobCustIdJson.toString());
				
				loggerImpl.startProfiler("Reading response data.");		
				loggerImpl.debug(logger,"Update Mobile No. API called.", "IN:ofVerifyAndUpdateMobileNo");
				
				if (ls_responseData.trim().substring(0, 1).equals("[")) {
					jsonAPIResponse = new JSONArray(ls_responseData).getJSONObject(0);
				} else if (ls_responseData.trim().substring(0, 1).equals("{")) {
					jsonAPIResponse = new JSONObject(ls_responseData);
				}

				/* Get response status for check API called or not. */
				ls_responseStatus = jsonAPIResponse.getString("STATUS");

				/* Check response code of API */
				if (ls_responseStatus != null && "0".equals(ls_responseStatus)) {
					/* Get the actual API response. */
					jsonAPIActualResponse = jsonAPIResponse.getJSONArray("RESPONSE").getJSONObject(0);
					ls_responseCode = jsonAPIActualResponse.getString("RESPONSECODE");
					ls_responseMessage = jsonAPIActualResponse.getString("RESPONSEMESSAGE");

				} else {
					//set the response in object to pass into db api in case of error.
					ls_responseCode = ls_responseStatus;
					ls_responseMessage = jsonAPIResponse.getString("MESSAGE");									
				}

				updateMobCustIdJson.put("RESPONSE_CD", ls_responseCode);
				updateMobCustIdJson.put("RESPONSE_MSG", ls_responseMessage);

				updateMobCustListWithRes.put(updateMobCustIdJson);				
			}
			
			loggerImpl.debug(logger,"DB API Calling for update the status.", "IN:ofVerifyAndUpdateMobileNo");
			loggerImpl.startProfiler("DB API calling.");
			// call DataBase API to update the response of API and email address.
			jsonRequestData.put("REQ_DATA", updateMobCustListWithRes);
			ls_dbResponseData = getData.ofGetResponseData(jsonRequestData.toString());

			//return same response comes from procedure.
			return ls_dbResponseData;

		}catch(Exception exception) {
			actualErrMsg = common.ofGetTotalErrString(exception, "");
			loggerImpl.error(logger,"Exception : " + actualErrMsg,"IN:ofVerifyAndUpdateMobileNo");
			
			ls_responseData = common.ofGetErrDataJsonArray("999",
					propConfiguration.getMessageOfResCode("commen.title.999", "Alert."),
					propConfiguration.getMessageOfResCode("commen.exception.999","","(ENP089)"), 
					exception.getMessage(),"Currently Service under maintenance so please try later (ENP089).", "0", "R");
			
			return ls_responseData;
		}finally {
			
			loggerImpl.stopAndPrintOptLogs(logger,"All API called successfully.","IN:ofVerifyAndUpdateMobileNo");
			loggerImpl.info(logger,"Response generated and send to client.", "IN:ofVerifyAndUpdateMobileNo");
		}
	}
}