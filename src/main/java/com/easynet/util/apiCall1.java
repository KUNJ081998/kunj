/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easynet.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.TimeZone;

import javax.servlet.http.HttpSession;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.easynet.bean.GetRequestUniqueData;
import com.easynet.configuration.PropConfiguration;
import com.easynet.impl.LoggerImpl;

/**
 *
 * @author HP
 */

@Component
public class apiCall1 {

	@Autowired
	GetRequestUniqueData getRequestUniqueData;
	
	static Logger logger=LoggerFactory.getLogger(apiCall1.class);

	@Value("${client.api.connectionTimeout:30}")
	private int connectionTimeout;

	@Value("${client.api.readTimeout:120}")
	private int readTimeout;
	
	@Autowired
	PropConfiguration propConfiguration;
	
	/**
	 * @param apiUrl       -pass the url link.
	 * @param httpHeaderss - String format json object to set into request header.
	 * @return -Return response data in string format.
	 */
	public String GetApi(String apiUrl, String httpHeaderss) {

		String responseString = "";
		String outputString = "";
		String AccessToken = "";
		String actualErrMsg = "";
		JSONObject headerJson = null;
		LoggerImpl loggerImpl=null;

		try {
			loggerImpl=new LoggerImpl();
			if (httpHeaderss != null && !"".equals(httpHeaderss)) {
				headerJson = new JSONObject(httpHeaderss);
				AccessToken = headerJson.optString("ACCESS_TOKEN", "");
			}

			URL url = new URL(apiUrl);

			URLConnection connection = url.openConnection();
			HttpURLConnection httpConn = (HttpURLConnection) connection;

			// add reuqest header
			httpConn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
			httpConn.setRequestMethod("GET");
			httpConn.setRequestProperty("Authorization", "Bearer " + AccessToken);
			httpConn.setConnectTimeout(connectionTimeout*1000);
			httpConn.setReadTimeout(readTimeout*1000);
			httpConn.setDoOutput(true);

			loggerImpl.debug(logger,"Calling API request.", "IN:GetApi");
			
			if (httpConn.getResponseCode() != 200) {
				
				//get error message of API and return error in response.
				InputStream errorObjectStream = httpConn.getErrorStream();
				BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorObjectStream));

				StringBuilder errorRespoceData = new StringBuilder();
				try {
					String line = null;
					while ((line = errorReader.readLine()) != null) {
						errorRespoceData.append(line + "\n");
					}
				}finally {

					errorReader.close();
					httpConn.disconnect();
				}	
												
				outputString = common.ofGetErrDataJsonObject("9", 
						propConfiguration.getMessageOfResCode("commen.title.999", "Alert."),
						propConfiguration.getMessageOfResCode("commen.exception.999","",""),
						errorRespoceData.toString()/*httpConn.getResponseCode() + " : " + AccessToken*/, "",
						String.valueOf(httpConn.getResponseCode()), "R");
			} else {

				InputStreamReader isr = new InputStreamReader(httpConn.getInputStream());
				BufferedReader in = new BufferedReader(isr);

				// Write the message response to a String.
				while ((responseString = in.readLine()) != null) {
					outputString = outputString + responseString;
				}

				/* by sagar for set data into json format for identify response data. */
				JSONObject JsonResponsedata = new JSONObject();

				JsonResponsedata.put("STATUS", "0");
				JsonResponsedata.put("COLOR", "G");
				JsonResponsedata.put("RESPONSE", outputString);
				outputString = JsonResponsedata.toString();			
			}
			
			loggerImpl.debug(logger,"Response generated.", "IN:GetApi");
			httpConn.disconnect();
			
		} catch (SocketTimeoutException ex) {
			actualErrMsg = common.ofGetTotalErrString(ex, "");
			outputString = common.ofGetErrDataJsonObject("2",
					propConfiguration.getMessageOfResCode("commen.title.999", "Alert."),
					propConfiguration.getMessageOfResCode("commen.exception.999","","(ENP003)"),
					ex.getMessage(), "Currently Service under maintenance so please try later (ENP003).", "0", "R");
			loggerImpl.error(logger,"Exception : " + actualErrMsg,"IN:GetApi");
		
		} catch (IOException e) {
			actualErrMsg = common.ofGetTotalErrString(e, "");
			loggerImpl.error(logger,"Exception : " + actualErrMsg,"IN:GetApi");
		
			outputString = common.ofGetErrDataJsonObject("2", 
					propConfiguration.getMessageOfResCode("commen.title.999", "Alert."),
					propConfiguration.getMessageOfResCode("commen.exception.999","","(ENP004)"),
					e.getMessage(),"Currently Service under maintenance so please try later (ENP004).", "0", "R");
		
		} catch (Exception exception) {
			actualErrMsg = common.ofGetTotalErrString(exception, "");
			loggerImpl.error(logger,"Exception : " + actualErrMsg,"IN:GetApi");
		
			outputString = common.ofGetErrDataJsonObject("2",
					propConfiguration.getMessageOfResCode("commen.title.999", "Alert."),
					propConfiguration.getMessageOfResCode("commen.exception.999","","(ENP005)"), 
					exception.getMessage(),"Currently Service under maintenance so please try later (ENP005).", "0", "R");
		}
		return outputString;
	}

	/**
	 * @param methodType       -Set the Method name,if this parameter is null then
	 *                         default value is "POST"
	 * @param apiUrl           -API URL
	 * @param apiParameter     -String format json request data.
	 * @param httpHeaders 	   -pass the string format json header value.
	 * @return -Return the string response value.
	 */
	public String PostApi(String methodType, String apiUrl, String apiParameter, String httpHeaders) {

		String jsonInput = "";
		String responseString = "";
		String outputString = "";
		String AccessToken = "";
		String requestMethodType = "";
		String actualErrMsg = "";
		LoggerImpl loggerImpl=null;


		try {
			loggerImpl=new LoggerImpl();
			
			if (methodType != null && !"".equals(methodType)) {
				requestMethodType = methodType;
			} else { // default method type
				requestMethodType = "POST";
			}

			/* added by sagar for set access token value */
			if (httpHeaders != null && !"".equals(httpHeaders)) {
				JSONObject headerJson = new JSONObject(httpHeaders);
				AccessToken = headerJson.optString("ACCESS_TOKEN", "");
			}

			loggerImpl.debug(logger, "Connection established and preparing data.", "PostApi");
			
			URL url = new URL(apiUrl);

			URLConnection connection = url.openConnection();
			HttpURLConnection httpConn = (HttpURLConnection) connection;

			ByteArrayOutputStream bout = new ByteArrayOutputStream();

			jsonInput = apiParameter;

			byte[] buffer = new byte[jsonInput.length()];
			buffer = jsonInput.getBytes();
			bout.write(buffer);
			byte[] b = bout.toByteArray();

			// add reuqest header
			httpConn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
			httpConn.setConnectTimeout(connectionTimeout*1000); // set timeout 1 min
			httpConn.setReadTimeout(readTimeout*1000);
			
			/* Set only when value is not null */
			if (AccessToken != null && !"".equals(AccessToken)) {
				httpConn.setRequestProperty("Authorization", "Bearer " + AccessToken);
			}

			httpConn.setRequestMethod(requestMethodType);// set from variable because of we need to send request body in
			// get type also.
			httpConn.setDoOutput(true);
			httpConn.setDoInput(true);

			try (OutputStream out_strm = httpConn.getOutputStream()) {
				out_strm.write(b);
			}
						
			if (httpConn.getResponseCode() != 200) {
								
				//get error message of API and return error in response.
				InputStream errorObjectStream = httpConn.getErrorStream();
				BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorObjectStream));

				StringBuilder errorRespoceData = new StringBuilder();
				try {
					String line = null;
					while ((line = errorReader.readLine()) != null) {
						errorRespoceData.append(line + "\n");
					}
				}finally {
					errorReader.close();
					httpConn.disconnect();
				}	
															
				outputString = common.ofGetErrDataJsonObject("9",
						propConfiguration.getMessageOfResCode("commen.title.999", "Alert."),
						propConfiguration.getMessageOfResCode("commen.URL_not_respond","",""),
						errorRespoceData.toString(),
						"URL not respond.",String.valueOf(httpConn.getResponseCode()), "R");				
			} else {

				InputStreamReader isr = new InputStreamReader(httpConn.getInputStream());
				BufferedReader in = new BufferedReader(isr);

				// Write the message response to a String.
				while ((responseString = in.readLine()) != null) {
					outputString = outputString + responseString;
				}

				/* by sagar for set data into json format for identify response data. */
				JSONObject JsonResponsedata = new JSONObject();

				JsonResponsedata.put("STATUS", "0");
				JsonResponsedata.put("COLOR", "G");
				JsonResponsedata.put("RESPONSE", outputString);
				outputString = JsonResponsedata.toString();
			}
			
			loggerImpl.debug(logger, "Response generated.", "IN:PostApi");
			
			httpConn.disconnect();		

		} catch (SocketTimeoutException ex) {			
			actualErrMsg = common.ofGetTotalErrString(ex, "");
			outputString = common.ofGetErrDataJsonObject("2",
					propConfiguration.getMessageOfResCode("commen.title.999", "Alert."),
					propConfiguration.getMessageOfResCode("commen.exception.999","","(ENP007)"),
					ex.getMessage(),"Currently Service under maintenance so please try later (ENP007).", "0", "R");
			loggerImpl.error(logger,"SocketTimeoutException: " + actualErrMsg,"IN:PostApi");
			
		} catch (IOException ex) {
			actualErrMsg = common.ofGetTotalErrString(ex, "");
			loggerImpl.error(logger,"IOException: " + actualErrMsg,"IN:PostApi");
			
			outputString = common.ofGetErrDataJsonObject("2", 
					propConfiguration.getMessageOfResCode("commen.title.999", "Alert."),
					propConfiguration.getMessageOfResCode("commen.exception.999","","(ENP008)"), 
					ex.getMessage(),"Currently Service under maintenance so please try later (ENP008).", "0", "R");
		} catch (Exception ex) {
			actualErrMsg = common.ofGetTotalErrString(ex, "");
			loggerImpl.error(logger,"Exception: " + actualErrMsg,"IN:PostApi");
			
			outputString = common.ofGetErrDataJsonObject("2",
					propConfiguration.getMessageOfResCode("commen.title.999", "Alert."),
					propConfiguration.getMessageOfResCode("commen.exception.999","","(ENP009)"), 
					ex.getMessage(),"Currently Service under maintenance so please try later (ENP009).", "0", "R");
		}
		
		return outputString;
	}

	/**
	 * @param accessTokenUrl to url get access token
	 * @param httpHeraders   extra header to get access token ,if not pass "";
	 * @return this method return the string format json object of token values.
	 */
	public String GenarateAccessToken(String accessTokenUrl, String httpHeraders) {

		JSONObject jsonObj = null;
		String outputString = "";
		String result = "401";
		boolean flag = false;
		String actualErrMsg = "";
		String apiUrl = accessTokenUrl;// URLGenerator.URL_TOKEN;
		LoggerImpl loggerImpl=null;

		try {

			loggerImpl=new LoggerImpl();
			// String jsonInput = com.base.common.ImportJson(jsonObj.toString(),
			// session.getAttribute("GETCLIENTDTL").toString());
			String jsonInput = "";
			String ls_AuthUsername = "";
			String ls_AuthPassword = "";
			String ls_AccessToken = "";
			String ls_userName = "";
			String ls_password = "";
			String ls_grantType = "";

			ls_AuthUsername = readXML.getXmlData("root>AUTHORIZATION>CLIENT_KEY");
			ls_AuthPassword = readXML.getXmlData("root>AUTHORIZATION>CLIENT_PASSWORD");
			ls_userName = readXML.getXmlData("root>AUTHORIZATION>USER_NAME");
			ls_password = readXML.getXmlData("root>AUTHORIZATION>PASSWORD");
			ls_grantType = readXML.getXmlData("root>AUTHORIZATION>GRANT_TYPE");

			ls_AccessToken = ls_AuthUsername + ":" + ls_AuthPassword;
			ls_AccessToken = Base64.getEncoder().encodeToString(ls_AccessToken.getBytes());

			URL url = new URL(apiUrl);
			HttpURLConnection urlc = (HttpURLConnection) url.openConnection();

			/* If header value found then set into header. */
			if (httpHeraders != null && !"".equals(httpHeraders)) {
				JSONObject headerJson = new JSONObject(httpHeraders);

				for (String keyName : headerJson.keySet()) {
					urlc.setRequestProperty(keyName, headerJson.optString(keyName, ""));
				}
			}

			// request data for authorization
			jsonInput = "username=" + ls_userName + "&" + "password=" + ls_password + "&" + "grant_type="
					+ ls_grantType;

			urlc.setRequestMethod("POST");
			urlc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			urlc.setRequestProperty("Content-Language", "en-US");
			urlc.setRequestProperty("Authorization", "Basic " + ls_AccessToken);
			urlc.setRequestProperty("Accept", "*/*");
			urlc.setConnectTimeout(connectionTimeout*1000); // set timeout 1 min
			urlc.setReadTimeout(readTimeout*1000);
			urlc.setUseCaches(false);
			urlc.setDoOutput(true);

			ByteArrayOutputStream bout = new ByteArrayOutputStream();

			byte[] buffer = new byte[jsonInput.length()];
			buffer = jsonInput.getBytes();
			bout.write(buffer);
			byte[] b = bout.toByteArray();

			// OutputStream out_strm = httpConn.getOutputStream();
			// out_strm.write(b);
			// out_strm.close();
			// Send request
			DataOutputStream wr = new DataOutputStream(urlc.getOutputStream());
			wr.write(b);
			wr.close();

			// Get Response
			InputStream in_stream = urlc.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(in_stream));
			StringBuilder response = new StringBuilder(); // or StringBuffer if not Java 5+
			String line;
			while ((line = rd.readLine()) != null) {
				response.append(line);
				response.append('\r');
			}
			rd.close();

			// Thread.sleep(INTERVAL * 1000);
			outputString = response.toString().trim();
			// System.out.println(outputString);
			urlc.disconnect();
			flag = true;
		} catch (SocketTimeoutException ex) {
			actualErrMsg = common.ofGetTotalErrString(ex, "");
			outputString = common.ofGetErrDataJsonObject("999",
					propConfiguration.getMessageOfResCode("commen.title.999", "Alert."),
					propConfiguration.getMessageOfResCode("commen.exception.999","","(ENP010)"),
					ex.getMessage(),"Currently Service under maintenance so please try later (ENP010).", "0", "R");
			
			flag = false;
			loggerImpl.error(logger,actualErrMsg,"IN:GenarateAccessToken");
			
		} catch (IOException ex) {
			actualErrMsg = common.ofGetTotalErrString(ex, "");
			outputString = common.ofGetErrDataJsonObject("999", 
					propConfiguration.getMessageOfResCode("commen.title.999", "Alert."),
					propConfiguration.getMessageOfResCode("commen.exception.999","","(ENP011)"), 
					ex.getMessage(),"Currently Service under maintenance so please try later (ENP011)", "0", "R");
			flag = false;
			loggerImpl.error(logger, actualErrMsg, "IN:GenarateAccessToken");
			
		} catch (Exception exception) {
			actualErrMsg = common.ofGetTotalErrString(exception, "");
			loggerImpl.error(logger, actualErrMsg, "IN:GenarateAccessToken");
			
			outputString = common.ofGetErrDataJsonObject("999",
					propConfiguration.getMessageOfResCode("commen.title.999", "Alert."),
					propConfiguration.getMessageOfResCode("commen.exception.999","","(ENP012)"),
					exception.getMessage(),"Currently Service under maintenance so please try later (ENP012)", "0", "R");
			flag = false;
		}

		if (flag) {
			try {
				String token = outputString;
				if (token.trim().substring(0, 1).equals("[")) {
					token = (String) token.substring(1, token.length() - 1);
				}
				JSONObject jobj = new JSONObject(token);
				String access_token = jobj.getString("access_token");
				String refresh_token = jobj.getString("refresh_token");
				String expire = String.valueOf(jobj.getInt("expires_in"));
				String scope = jobj.getString("scope");

				JSONObject responsetokenData = new JSONObject();

				responsetokenData.put("ACCESS_TOKEN", access_token);
				responsetokenData.put("REFRESH_TOKEN", refresh_token);
				responsetokenData.put("EXPIRE_TOKEN", expire);
				responsetokenData.put("SCOPE", scope);

				result = responsetokenData.toString();
			} catch (JSONException ex) {
				actualErrMsg = common.ofGetTotalErrString(ex, "");
				
				result = common.ofGetErrDataJsonObject("999",
						propConfiguration.getMessageOfResCode("commen.title.999", "Alert."),
						propConfiguration.getMessageOfResCode("commen.exception.999","","(ENP013)"),
						ex.getMessage(),"Currently Service under maintenance so please try later (ENP013)", "0", "R");
				loggerImpl.error(logger, actualErrMsg, "IN:GenarateAccessToken");
			}
		} else {
			result = outputString;
		}

		return result;
	}

	/**
	 * @param httpHeaders header json object for set into url request.
	 * @return this method return the json object of token.
	 *
	 */
	@SuppressWarnings("null")
	public String GenarateRefreshAccessToken(String accessTokenUrl, String httpHeaders) {
		// System.out.println(refresh_token);

		JSONObject jsonObj = null;
		String responseString = "";
		String outputString = "";
		String encoding = "";
		String result = "401";
		boolean flag = false;
		JSONObject headerJson = null;
		String EXPIRE_TOKEN = "", refresh_token = "";
		String actualErrMsg = "";

		try {
			/* If header value found then set into header. */
			if (httpHeaders != null && !"".equals(httpHeaders)) {
				headerJson = new JSONObject(httpHeaders);
				EXPIRE_TOKEN = headerJson.optString("EXPIRE_TOKEN", "");
				refresh_token = headerJson.optString("REFRESH_TOKEN", "");
			} else {
				outputString = common.ofGetErrDataJsonObject("406", "Alert",
						"Currently Service under maintenance so please try later (ENP0014).", "Null Header Found", "",
						"0", "R");
				return outputString;
			}
		} catch (Exception e) {
			e.printStackTrace();
			actualErrMsg = common.ofGetTotalErrString(e, "");
			outputString = common.ofGetErrDataJsonObject("2", "Alert",
					"Currently Service under maintenance so please try later (ENP0015).", actualErrMsg, "", "0", "R");
			flag = false;
		}

		if (EXPIRE_TOKEN != null) {
			if (common.IsExpireDate(EXPIRE_TOKEN)) {
				try {

					String apiUrl = accessTokenUrl;// URLGenerator.URL_REFRESH_TOKEN;
					URL url = new URL(apiUrl);
					HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
					urlc.setRequestMethod("POST");
					urlc.setRequestProperty("Content-Type", "application/json");
					urlc.setRequestProperty("Content-Language", "en-US");
					urlc.setRequestProperty("Authorization", "Bearer " + refresh_token);
					urlc.setRequestProperty("Accept", "application/json");
					urlc.setConnectTimeout(connectionTimeout*1000); // set timeout 1 min
					urlc.setReadTimeout(readTimeout*1000);
					urlc.setUseCaches(false);
					urlc.setDoOutput(true);

					try (DataOutputStream wr = new DataOutputStream(urlc.getOutputStream())) {
						wr.writeBytes("");
					}

					// Get Response
					InputStream in_stream = urlc.getInputStream();
					BufferedReader rd = new BufferedReader(new InputStreamReader(in_stream));
					StringBuilder response = new StringBuilder(); // or StringBuffer if not Java 5+
					String line;
					while ((line = rd.readLine()) != null) {
						response.append(line);
						response.append('\r');
					}
					rd.close();

					outputString = response.toString().trim();
					urlc.disconnect();
					flag = true;
				} catch (SocketTimeoutException ex) {
					actualErrMsg = common.ofGetTotalErrString(ex, "");
					outputString = common.ofGetErrDataJsonObject("2", "Alert",
							"Currently Service under maintenance so please try later (ENP0016).", actualErrMsg, "", "0",
							"R");
					flag = false;
				} catch (IOException ex) {
					ex.printStackTrace();
					actualErrMsg = common.ofGetTotalErrString(ex, "");
					outputString = common.ofGetErrDataJsonObject("2", "Alert",
							"Currently Service under maintenance so please try later (ENP0017).", actualErrMsg, "", "0",
							"R");
					flag = false;
				} catch (Exception exception) {
					exception.printStackTrace();
					actualErrMsg = common.ofGetTotalErrString(exception, "");
					outputString = common.ofGetErrDataJsonObject("2", "Alert",
							"Currently Service under maintenance so please try later (ENP0017).", actualErrMsg, "", "0",
							"R");
					flag = false;
				}
				if (flag) {
					try {
						String token = outputString;
						if (token.trim().substring(0, 1).equals("[")) {
							token = (String) token.substring(1, token.length() - 1);
						}
						JSONObject jobj = new JSONObject(token);
						String access_token = jobj.getString("accessToken");
						String refreshToken = jobj.getString("refreshToken");
						String expire = jobj.getString("accessTokenExpire");
						String RefreshTokenExpire = jobj.getString("refreshTokenExpire");

						JSONObject responseAccessToken = new JSONObject();

						responseAccessToken.put("ACCESS_TOKEN", access_token);
						responseAccessToken.put("REFRESH_TOKEN", refreshToken);
						responseAccessToken.put("EXPIRE_TOKEN", expire);
						responseAccessToken.put("REFRESH_EXPIRE_TOKEN", RefreshTokenExpire);
						result = responseAccessToken.toString();

					} catch (JSONException ex) {
						System.out.println("=====================JSONException===========");
						System.out.println(ex.getMessage());
						System.out.println("=============================================");
						actualErrMsg = common.ofGetTotalErrString(ex, "");
						result = common.ofGetErrDataJsonObject("401", "Alert",
								"Currently Service under maintenance so please try later (ENP0018).", actualErrMsg, "",
								"0", "R");
					}
				} else {
					result = outputString;
				}
			} else {
				// return same header object json string data.
				result = headerJson.toString();
			}
		} else {
			result = common.ofGetErrDataJsonObject("406", "Alert",
					"Currently Service under maintenance so please try later (ENP0019).", "Null Header Found.", "", "0",
					"R");
		}
		return result;
	}

	/*
	 * Write Text Log
	 */
	public void WriteLog(String as_text) {
		readXML xmlread = new readXML();
		String as_path = xmlread.getPath() + File.separatorChar + "log" + File.separatorChar + "";
		System.out.println(as_path);
		try {

			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
			simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
			SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
			simpleDateFormat2.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));

			String date = simpleDateFormat.format(new Date());
			String date2 = simpleDateFormat2.format(new Date());
			FileWriter writer = new FileWriter(as_path + "LOG_" + date + ".txt", true);
			writer.write(date2 + " " + as_text);
			writer.write("\r\n");
			writer.close();

			File file = new File(as_path + "LOG_" + date + ".txt");
			if (file.exists()) {
				file.setExecutable(true);
				file.setReadable(true);
				file.setWritable(true);
			}

		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	/*
	 * Google Captcha Verify API
	 */
	public static boolean PostApiRecaptcha(HttpSession session, String apiParameter) throws JSONException {

		try {
			if (readXML.getXmlData("captcha_cd").equalsIgnoreCase("CC")) {
				if (AESEncryption.decryptText(session.getAttribute("captchakey").toString()).equals(apiParameter)) {
					return true;
				}
			} else if (readXML.getXmlData("captcha_cd").equalsIgnoreCase("GC")) {
				String responseString = "";
				String outputString = "";
				try {
					String apiURL = "https://www.google.com/recaptcha/api/siteverify?";
					String secret = readXML.getXmlData("site_secret_key");
					URL url = new URL(apiURL);
					URLConnection connection = url.openConnection();
					HttpURLConnection httpConn = (HttpURLConnection) connection;
					ByteArrayOutputStream bout = new ByteArrayOutputStream();
					String jsonInput = "secret=" + secret + "&response=" + apiParameter;
					byte[] buffer = new byte[jsonInput.length()];
					buffer = jsonInput.getBytes();
					bout.write(buffer);
					byte[] b = bout.toByteArray();

					// add reuqest header
					httpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
					httpConn.setConnectTimeout(60000); // set timeout 1 min
					httpConn.setReadTimeout(60000);
					httpConn.setRequestMethod("GET");
					httpConn.setDoOutput(true);
					httpConn.setDoInput(true);

					OutputStream out_strm = httpConn.getOutputStream();
					out_strm.write(b);
					out_strm.close();

					InputStreamReader isr = new InputStreamReader(httpConn.getInputStream());
					BufferedReader in = new BufferedReader(isr);
					// Write the SOAP message response to a String.
					while ((responseString = in.readLine()) != null) {
						outputString = outputString + responseString;
					}

					httpConn.disconnect();

					if (outputString.trim().substring(0, 1).equals("[")) {
						outputString = (String) outputString.substring(1, outputString.length() - 1);
					} else {
						outputString = (String) outputString;
					}
					JSONObject jobj = new JSONObject(outputString);
					boolean success = jobj.getBoolean("success");
					if (success) {
						return success;
					}

				} catch (SocketTimeoutException ex) {
					return false;
				} catch (IOException e) {
					return false;
				}
			}
		} catch (Exception ex) {
			return false;
		}
		/* Temparary By Pass Captcha */
		// return true;
		return false;
	}

	public String getAPIWithBody(String apiUrl, String apiParameter, String httpHeaders){

		String responseString = "";		
		String outputString = "";
		String AccessToken = "";
		String actualErrMsg = "";		
		LoggerImpl loggerImpl=null;
		
		try {
			
			loggerImpl=new LoggerImpl();
			loggerImpl.debug(logger,"apiUrl : " + apiUrl,"IN:getAPIWithBody");
			
			/* added by sagar for set access token value */
			if (httpHeaders != null && !"".equals(httpHeaders)) {
				JSONObject headerJson = new JSONObject(httpHeaders);
				AccessToken = headerJson.optString("ACCESS_TOKEN","");
			}

			//set timeout parameter
			RequestConfig config = RequestConfig.custom()
					.setConnectTimeout(connectionTimeout*1000)
					.setConnectionRequestTimeout(readTimeout*1000)				
					.setSocketTimeout(connectionTimeout*1000).build();

			//create http connection with parameter
			CloseableHttpClient  httpClient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();


			// client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);

			HttpGetWithBody httpgetWithBody = new HttpGetWithBody();

			//Set url to http client
			httpgetWithBody.setURI(new URI(apiUrl));

			httpgetWithBody.setHeader("Content-Type", "application/json; charset=utf-8");

			/* Set only when value is not null */
			if (AccessToken != null && !"".equals(AccessToken)) {
				httpgetWithBody.setHeader("Authorization", "Bearer " + AccessToken);
			}

			//set the request body
			httpgetWithBody.setEntity(new ByteArrayEntity(apiParameter.getBytes("UTF8")));

			//call api to fectch data
			HttpResponse httpResponse = httpClient.execute(httpgetWithBody);
			//get the response body.
			responseString = EntityUtils.toString(httpResponse.getEntity());

			//Get the status of request.
			StatusLine statusLine=httpResponse.getStatusLine();

			if (statusLine.getStatusCode() != 200) {
				outputString = common.ofGetErrDataJsonObject("9", 
						propConfiguration.getMessageOfResCode("commen.title.999", "Alert."), 
						propConfiguration.getMessageOfResCode("commen.URL_not_respond","",""),
						responseString, "URL not respond.",String.valueOf(statusLine.getStatusCode()), "R");				
			} else {

				outputString=responseString;

				/* by sagar for set data into json format for identify response data. */
				JSONObject JsonResponsedata = new JSONObject();

				JsonResponsedata.put("STATUS", "0");
				JsonResponsedata.put("COLOR", "G");
				JsonResponsedata.put("RESPONSE", outputString);
				outputString = JsonResponsedata.toString();

			}
			//close connection
			httpClient.close();

		} catch (SocketTimeoutException ex) {
			actualErrMsg = common.ofGetTotalErrString(ex, "");
			outputString = common.ofGetErrDataJsonObject("2", 
					propConfiguration.getMessageOfResCode("commen.title.999", "Alert."),
					propConfiguration.getMessageOfResCode("commen.exception.999","","(ENP028)"),
					ex.getMessage(),"Currently Service under maintenance so please try later (ENP028).", "0", "R");
			
			loggerImpl.error(logger,"SocketTimeoutException: " + actualErrMsg,"IN:getAPIWithBody");
		} catch (IOException ex) {
			actualErrMsg = common.ofGetTotalErrString(ex, "");
			loggerImpl.error(logger,"IOException: " + actualErrMsg,"IN:getAPIWithBody");
			outputString = common.ofGetErrDataJsonObject("2", 
					propConfiguration.getMessageOfResCode("commen.title.999", "Alert."),
					propConfiguration.getMessageOfResCode("commen.exception.999","","(ENP029)"), 
					ex.getMessage(),"Currently Service under maintenance so please try later (ENP029).", "0", "R");
			
		} catch (Exception ex) {
			actualErrMsg = common.ofGetTotalErrString(ex, "");
			loggerImpl.error(logger,"Exception: " + actualErrMsg,"IN:getAPIWithBody");
			outputString = common.ofGetErrDataJsonObject("2",
					propConfiguration.getMessageOfResCode("commen.title.999", "Alert."),
					propConfiguration.getMessageOfResCode("commen.exception.999","","(ENP030)"),
					ex.getMessage(),"Currently Service under maintenance so please try later (ENP030).", "0", "R");
		}
		
		System.gc();
		return outputString;
	}

	public String PostFileUploadApi(String apiUrl, JSONObject apiParameter, String httpHeaders, String apiFileName,InputStream apiFile) {

		
		String twoHyphens = "--";
		String boundary = "**" + Long.toString(System.currentTimeMillis()) + "**";
		String lineEnd = "\r\n";
		String outputString = "";
		String ls_Authorization = "";
		int maxBufferSize = 1 * 1024 * 1024;

		int bytesRead;
		int bytesAvailable;
		int bufferSize;
		LoggerImpl loggerImpl=null;
		String 	actualErrMsg="";
		
		try {
			loggerImpl=new LoggerImpl();

			loggerImpl.debug(logger,"apiUrl : " + apiUrl,"PostFileUploadApi");
			
			/* for set access token value */
			if (httpHeaders != null && !"".equals(httpHeaders)) {
				JSONObject headerJson = new JSONObject(httpHeaders);
				ls_Authorization = headerJson.optString("ACCESS_TOKEN", "");
			}
			//System.out.println(ls_Authorization);


			int TIMEOUTCONNECTION = 120000;
			URL url = new URL(apiUrl);
			URLConnection connection = url.openConnection();
			HttpURLConnection httpConn = (HttpURLConnection) connection;

			//add reuqest header
			httpConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
			//httpConn.setRequestProperty("Accept-Type", "application/json");
			//httpConn.setRequestProperty("Content-Type", "text/plain");
			httpConn.setRequestProperty("Connection", "Keep-Alive");
			httpConn.setRequestProperty("Authorization", "Bearer " + ls_Authorization);
			httpConn.setRequestMethod("POST");
			httpConn.setUseCaches(false);
			httpConn.setDoInput(true);
			httpConn.setDoOutput(true);
			httpConn.setConnectTimeout(connectionTimeout*1000);
			httpConn.setReadTimeout(readTimeout*1000);

			//set header token
			DataOutputStream outputStream = new DataOutputStream(httpConn.getOutputStream());

			//Document File
			outputStream.writeBytes(twoHyphens + boundary + lineEnd);
			outputStream.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"" + apiFileName + "\"" + lineEnd);
			outputStream.writeBytes("Content-Type: application/octet-stream" + lineEnd);
			outputStream.writeBytes("Content-Transfer-Encoding: binary" + lineEnd);

			outputStream.writeBytes(lineEnd);
			bytesAvailable = apiFile.available();
			bufferSize = Math.min(bytesAvailable, maxBufferSize);
			byte[] buffer = new byte[bufferSize];

			bytesRead = apiFile.read(buffer, 0, bufferSize);
			while (bytesRead > 0) {
				outputStream.write(buffer, 0, bufferSize);
				bytesAvailable = apiFile.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				bytesRead = apiFile.read(buffer, 0, bufferSize);
			}
			outputStream.writeBytes(lineEnd);

			for (String keyStr : apiParameter.keySet()) {
				String keyvalue = apiParameter.getString(keyStr);

				//System.out.println(keyStr  +" :" + keyvalue );

				outputStream.writeBytes(twoHyphens + boundary + lineEnd);
				outputStream.writeBytes("Content-Disposition: form-data; name=\""+keyStr+"\"" + lineEnd);
				outputStream.writeBytes("Content-Type: text/plain" + lineEnd);
				outputStream.writeBytes(lineEnd);
				outputStream.writeBytes(keyvalue);
				outputStream.writeBytes(lineEnd);

			}

			outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

			int code = httpConn.getResponseCode();
			
			outputStream.flush();
			outputStream.close();

			if (code == HttpURLConnection.HTTP_OK) {
				InputStream objInputstream = httpConn.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(
						objInputstream));

				StringBuilder respoceData = new StringBuilder();
				try {
					String line = null;
					while ((line = reader.readLine()) != null) {
						respoceData.append(line + "\n");
					}

					JSONObject JsonResponsedata = new JSONObject();

					JsonResponsedata.put("STATUS", "0");
					JsonResponsedata.put("FILE_NM", respoceData.toString());
					outputString = JsonResponsedata.toString();
					//System.out.println("Upload File Response :"  + outputString);
				} //tempcode
				finally {

					objInputstream.close();
					httpConn.disconnect();
				}

			} else {

				//get error message
				InputStream errorObjectStream = httpConn.getErrorStream();
				BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorObjectStream));

				StringBuilder errorRespoceData = new StringBuilder();
				try {
					String line = null;
					while ((line = errorReader.readLine()) != null) {
						errorRespoceData.append(line + "\n");
					}
				}finally {

					errorObjectStream.close();
					httpConn.disconnect();
				}	

				outputString = common.ofGetErrDataJsonObject("99", 
						propConfiguration.getMessageOfResCode("commen.title.999", "Alert."),
						propConfiguration.getMessageOfResCode("commen.exception.999","","(ENP058)"),
						"HTTP URL ERROR-"+errorRespoceData, "Currently Service under maintenance so please try later (ENP058).", "0", "R");				
			}	

			System.gc();

		} catch (OutOfMemoryError ex) {			
			actualErrMsg = "Out of memory exception.";
			outputString = common.ofGetErrDataJsonObject("99",
					propConfiguration.getMessageOfResCode("commen.title.999", "Alert."),
					propConfiguration.getMessageOfResCode("commen.exception.999","","(ENP054)"),
					ex.getMessage(),"Currently Service under maintenance so please try later (ENP054).", "0", "R");
			
			loggerImpl.error(logger,"SocketTimeoutException: " + actualErrMsg,"IN:PostFileUploadApi");
			
		} catch (SocketTimeoutException ex) {
			actualErrMsg = common.ofGetTotalErrString(ex, "");
			outputString = common.ofGetErrDataJsonObject("99", 
					propConfiguration.getMessageOfResCode("commen.title.999", "Alert."),
					propConfiguration.getMessageOfResCode("commen.exception.999","","(ENP055)"),
					ex.getMessage(),"Currently Service under maintenance so please try later (ENP055).", "0", "R");
			
			loggerImpl.error(logger,"SocketTimeoutException: " + actualErrMsg,"IN:PostFileUploadApi");			
		} catch (IOException ex) {			
			actualErrMsg = common.ofGetTotalErrString(ex, "");
			loggerImpl.error(logger,"IOException: " + actualErrMsg,"IN:PostFileUploadApi");
			
			outputString = common.ofGetErrDataJsonObject("99", 
					propConfiguration.getMessageOfResCode("commen.title.999", "Alert."),
					propConfiguration.getMessageOfResCode("commen.exception.999","","(ENP056)"),
					ex.getMessage(),"Currently Service under maintenance so please try later (ENP056).", "0", "R");
			
		} catch (Exception ex) {
			actualErrMsg = common.ofGetTotalErrString(ex, "");
			loggerImpl.error(logger,"Exception: " + actualErrMsg,"IN:PostFileUploadApi");
			
			outputString = common.ofGetErrDataJsonObject("99", 
					propConfiguration.getMessageOfResCode("commen.title.999", "Alert."),
					propConfiguration.getMessageOfResCode("commen.exception.999","","(ENP057)"), 
					ex.getMessage(),"Currently Service under maintenance so please try later (ENP057)", "0", "R");
		}
		return outputString;

	}
}
