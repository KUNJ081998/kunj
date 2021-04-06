package com.easynet.controller.qr;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.easynet.configuration.PropConfiguration;
import com.easynet.controller.ApiController.QRAPI.GetQRData;
import com.easynet.controller.ApiController.QRAPI.GetQRDataById;
import com.easynet.impl.LoggerImpl;
import com.easynet.util.GetData;
import com.easynet.util.common;

@Controller
public class QRScanController {

	private static Logger LOG = LoggerFactory.getLogger(QRScanController.class);

	private final LoggerImpl _log = new LoggerImpl();

	private static final String TYPE = "TYPE";
	private static final String DATA = "DATA";
	private static final String CUSTOMER_ID = "CUSTOMER_ID";
	private static final String USER_ID = "USER_ID";
	private static final String ACTIVITY_CD = "ACTIVITY_CD";
	private static final String REMARKS = "REMARKS";
	private static final String CHANNEL = "CHANNEL";
	private static final String KEY_ID = "KEY_ID";
	private static final String ISLOGGEDIN = "ISLOGGEDIN";
	private static final String DISPLAY_LANGUAGE = "DISPLAY_LANGUAGE";
	private static final String REQUEST_CD = "REQUEST_CD";
	private static final String ACTION = "ACTION";
	private static final String CARD_LIST = "CARD_LIST";
	private static final String ALLOW_AFTER_LOGIN = "ALLOW_AFTER_LOGIN";
	private static final String SCAN_REQUEST_CD = "SCAN_REQUEST_CD";

	@Autowired
	private GetData getData;

	@Autowired
	private GetQRData getQRData;

	@Autowired
	private GetQRDataById getQRDataById;

	@Autowired
	private PropConfiguration propConfiguration;

	public String getQRData(String input) {
		_log.info(LOG, "Getting QA data: " + input, "IN:getQRData");

		String responseData = StringUtils.EMPTY;

		JSONObject json = new JSONObject(input);
		String type = json.getString(TYPE);
		String qrCode = json.getString(DATA);
		String customerId = json.getString(CUSTOMER_ID);
		String userId = json.getString(USER_ID);
		String activityCd = json.getString(ACTIVITY_CD);
		String remarks = json.getString(REMARKS);
		String channel = json.getString(CHANNEL);
		String keyId = json.getString(KEY_ID);
		String loggedIn = json.getString(ISLOGGEDIN);
		String displayLanguage = json.getString(DISPLAY_LANGUAGE);

		boolean checkValidation = checkValidation(type, qrCode, customerId, activityCd, remarks, channel, loggedIn,
				displayLanguage);

		if (checkValidation) {
			String actionInput = createJsonObject(customerId, userId, "CARDSCAN", activityCd, channel, keyId,
					loggedIn, displayLanguage);

			_log.info(LOG, "Preparing data and getting response of Action", "IN:CARDINSERT");
			_log.generateProfiler("CARDINSERT");
			_log.startProfiler("CARDINSERT");
			String ofGetResponseData = getData.ofGetResponseData(actionInput);
			_log.stopAndPrintOptLogs(LOG, "Response generated for the request", "IN:CARDINSERT");
			boolean checkErrorExist = checkErrorExist(ofGetResponseData);
			if (checkErrorExist) {
				return ofGetResponseData;
			} else {
				JSONArray cardInsertRsp = new JSONArray(ofGetResponseData);

				// Assuming request is successful and data will be at 0 index.
				JSONObject jsonObject = cardInsertRsp.getJSONObject(0);
				String requestId = jsonObject.getString(REQUEST_CD);
				if ("ID".equals(type)) {
					responseData = callQRDataAPI(qrCode, customerId, activityCd, remarks, requestId, jsonObject);
				} else if ("SCAN".equals(type)) {
					responseData = callQRDataIdAPI(qrCode, jsonObject);
				} else {
					responseData = common.ofGetErrDataJsonArray("99",
							propConfiguration.getMessageOfResCode("commen.title.99", "Validation Failed."),
							propConfiguration.getMessageOfResCode("qrscan.invalidtype", "InValid TYPE"),
							"Invalid values found in TYPE keys.", "", "", "R");
				}
			}

		} else {
			responseData = common.ofGetErrDataJsonArray("99",
					propConfiguration.getMessageOfResCode("commen.title.99", "Validation Failed."),
					propConfiguration.getMessageOfResCode("commen.invalid_req_data", "InValid request."),
					"Null values found in request data keys.", "", "", "R");
		}
		return responseData;
	}

	private boolean checkErrorExist(String response) {
		_log.debug(LOG, "Checking Error in the response: " + response, "IN:checkErrorExist");
		boolean error = false;
		try {
			JSONArray json = new JSONArray(response);
			JSONObject jsonObject = json.getJSONObject(0);
			String status = jsonObject.getString("STATUS");
			if (StringUtils.isNotBlank(status) && !status.equals("0")) {
				error = true;
			}
		} catch (JSONException e) {

		}
		return error;
	}

	private String createJsonObject(String customerId, String userId, String action, String activityCd, String channel,
			String keyId, String loggedIn, String displayLanguage) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(CUSTOMER_ID, customerId);
		jsonObject.put(USER_ID, userId);
		jsonObject.put(ACTION, action);
		jsonObject.put(ACTIVITY_CD, activityCd);
		jsonObject.put(CHANNEL, channel);
		jsonObject.put(KEY_ID, keyId);
		jsonObject.put(ISLOGGEDIN, loggedIn);
		jsonObject.put(DISPLAY_LANGUAGE, displayLanguage);
		return jsonObject.toString();
	}

	private boolean checkValidation(String type, String qrCode, String customerId, String activityCd, String remarks,
			String channel, String loggedIn, String displayLanguage) {
		_log.debug(LOG, "Checking Validation for request data", "IN:checkValidation");
		boolean valid = true;
		if (StringUtils.isBlank(type) || StringUtils.isBlank(qrCode) || StringUtils.isBlank(customerId)
				|| StringUtils.isBlank(activityCd) || StringUtils.isBlank(remarks) || StringUtils.isBlank(channel)
				|| StringUtils.isBlank(loggedIn) || StringUtils.isBlank(displayLanguage)) {
			valid = false;
		}
		return valid;
	}

	private String callQRDataIdAPI(String qrCode, JSONObject insertCardJson) {
		String responseData = StringUtils.EMPTY;

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("QRCODEID", qrCode);
		_log.generateProfiler("GETQRDATABYID");
		_log.startProfiler("GETQRDATABYID");
		String qrDataResponse = getQRDataById.getQRDataById(jsonObject.toString());
		_log.stopAndPrintOptLogs(LOG, "Response generated for the request", "IN:GETQRDATABYID");
		boolean checkErrorExist = checkErrorExist(qrDataResponse);
		if (checkErrorExist) {
			responseData = qrDataResponse;
		} else {
			responseData = parseQRResponse(qrDataResponse, insertCardJson);
		}

		return responseData;
	}

	private String callQRDataAPI(String qrCode, String customerId, String activityCd, String remarks, String requestId,
			JSONObject insertCardJson) {

		String responseData = StringUtils.EMPTY;
		String qrDataJson = createQRDataJson(customerId, qrCode, remarks, requestId);
		_log.generateProfiler("GETQRDATA");
		_log.startProfiler("GETQRDATA");
		String qrDataResponse = getQRData.getQRData(qrDataJson);
		_log.stopAndPrintOptLogs(LOG, "Response generated for the request", "IN:GETQRDATA");
		boolean checkErrorExist = checkErrorExist(qrDataResponse);
		if (checkErrorExist) {
			responseData = qrDataResponse;
		} else {
			responseData = parseQRResponse(qrDataResponse, insertCardJson);
		}
		return responseData;

	}

	private String createQRDataJson(String customerId, String qrCode, String remarks, String requestId) {
		JSONObject json = new JSONObject();
		json.put("CSBCUSTOMERID", customerId);
		json.put("CLIENTID", customerId);
		json.put("QRSTRING", qrCode);
		json.put("REMARKS", remarks);
		json.put("REQUESTID", requestId);
		return json.toString();
	}

	private String parseQRResponse(String json, JSONObject insertCardJson) {

		String responseData = StringUtils.EMPTY;
		JSONArray responseJsonObjectArray = new JSONArray();

		JSONObject jsonObject = new JSONObject(json);
		JSONArray jsonArray = jsonObject.getJSONArray("RESPONSE");
		JSONObject resObject = jsonArray.getJSONObject(0);
		String amex = resObject.getString("MERACCINFOAMEX");
		String masterCard = resObject.getString("MERACCINFOMC");
		String visa = resObject.getString("MERACCINFOVISA");
		String transCurrency = resObject.getString("TRANCURRENCY53");
		resObject.put(REQUEST_CD, insertCardJson.getString(REQUEST_CD));
		resObject.put(ALLOW_AFTER_LOGIN, insertCardJson.getString(ALLOW_AFTER_LOGIN));
		resObject.put(SCAN_REQUEST_CD, insertCardJson.getString(SCAN_REQUEST_CD));

		JSONArray cardList = getCardList(insertCardJson, transCurrency, amex, masterCard, visa);

		if (cardList.length() != 0) {
			resObject.put(CARD_LIST, cardList);
		} else {
			resObject.put(CARD_LIST, common.ofGetErrDataJson(
					propConfiguration.getMessageOfResCode("qrscan.cardError.title", "Card Error."),
					propConfiguration.getMessageOfResCode("qrscan.cardError.msg", "Card not accepted by Merchant."),
					""));
		}

		responseJsonObjectArray.put(jsonObject);
		responseData = responseJsonObjectArray.toString();

		return responseData;

	}

	private JSONArray getCardList(JSONObject cardJson, String transCurrency, String amex, String master, String visa) {
		JSONArray jsonArray = new JSONArray();
		boolean amexCard = displayCard(amex);
		boolean masterCard = displayCard(master);
		boolean visaCard = displayCard(visa);
		JSONArray cardList = cardJson.getJSONArray(CARD_LIST);

		// Display BDT card only else display USD card
		if (StringUtils.isNotBlank(transCurrency) && transCurrency.equals("050")) {
			jsonArray = filterCardList(cardList, true, amexCard, masterCard, visaCard);
		} else {
			jsonArray = filterCardList(cardList, false, amexCard, masterCard, visaCard);
		}
		return jsonArray;
	}

	private JSONArray filterCardList(JSONArray cardList, boolean bdtCurrency, boolean amexCard, boolean masterCard,
			boolean visaCard) {
		JSONArray jsonArray = new JSONArray();
		if (cardList != null && cardList.length() > 0) {
			for (int i = 0; i < cardList.length(); i++) {
				JSONObject jsonObject = cardList.getJSONObject(i);
				String currencyCode = jsonObject.getString("CURRENCY_CODE");
				String cardSource = jsonObject.getString("UNMASK_CARD_NO");
				boolean add = checkCard(cardSource, visaCard, amexCard, masterCard);
				if (bdtCurrency && currencyCode.equals("BDT") && add) {
					jsonArray.put(jsonObject);
				} else if (currencyCode.equals("USD") && add) {
					jsonArray.put(jsonObject);
				}
			}
		}
		return jsonArray;
	}

	private boolean checkCard(String cardSource, boolean visaCard, boolean amexCard, boolean masterCard) {
		boolean add = false;
		char firstChar = cardSource.charAt(0);
		if ((firstChar == '4' && visaCard) || (firstChar == '5' && masterCard) || (firstChar == '3' && amexCard)) {
			add = true;
		}
		return add;
	}

	private boolean displayCard(String card) {
		boolean display = false;
		if (StringUtils.isNotBlank(card)) {
			display = true;
		}
		return display;
	}

}
