package com.easynet.util;

import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.easynet.impl.JSONObjectImpl;

public class common {
		
	public static boolean getAuthorizationValid(String argu) {			
		String split = ":";
		String ls_header = null;
		String ls_username = null;
		String ls_password = null;
		String ls_Authorization = null;

		boolean lb_ = false;
		ls_header = argu.split(" ")[0];
		//System.out.println(ls_header);
		if (ls_header.trim().equalsIgnoreCase("Bearer")) {
			ls_Authorization = argu.split(" ")[1];

			ls_Authorization = new String(Base64.decodeBase64(ls_Authorization));

			ls_username = ls_Authorization.split(split)[0];
			ls_password = ls_Authorization.split(split)[1];
			if (ls_username.trim().equals(common.getConfig("AuthUsername").trim())
					&& ls_password.trim().equals(common.getConfig("AuthPassword").trim())) {
				lb_ = true;
			} else {
				lb_ = false;
			}
		} else {
			lb_ = false;
		}
		return lb_;
	}

	public static String getConfig(String argu) {
		String value = "";

		Properties prop = new Properties();
		InputStream input = null;		
		
		try {
			
			String filename = "application.properties";
			input = common.class.getClassLoader().getResourceAsStream(filename);
			if (input == null) {
				System.out.println("Sorry, unable to find ");				
				return "";
			}
			//load a properties file from class path, inside static method
			prop.load(input);
			value = prop.getProperty(argu);
			if (value == null) {
				value = "";
			}

		} catch (Exception e) {
			value = "";
			e.printStackTrace();
		}
		return value;
	}

	public static void errorLogPrint(String argu) {
		System.out.println("===================Netbanking API ======================");
		System.out.println(argu);
		System.out.println("==================================================");
	}

	public static String xmlConvert(String argu) {

		if (argu.trim().substring(0, 1).equals("[")) {
			argu = (String) argu.substring(1, argu.length() - 1);
		} else {
			argu = (String) argu;
		}

		return XML.toString(new JSONObject(argu));
	}

	public static String xmlToString(String xml) throws JAXBException {
		//String xmlString = xml;
		//String jsonString = null;

		if (xml == null) {
			return "[{\"ResponseCode\":\"0\",\"ResponseMessage\":\"Something going wrong, please contact your branch, 1001\"}]";
		}
		try {
			return org.json.XML.toJSONObject(xml).toString();
		} catch (JSONException e) {
			//return "[{\"ResponseCode\":\"0\",\"ResponseMessage\":\"Something going wrong, xmlToString : \"" + e.getMessage() + "\"}]";
			return "[{\"ResponseCode\":\"0\",\"ResponseMessage\":\"Something going wrong, xmlToString \"}]";

		}

	}

	public static Document loadXML(String xml) throws Exception {
		DocumentBuilderFactory fctr = DocumentBuilderFactory.newInstance();
		DocumentBuilder bldr = fctr.newDocumentBuilder();
		InputSource insrc = new InputSource(new StringReader(xml));
		return bldr.parse(insrc);
	}

	public static JSONObject xmlToJson(String xml) throws JAXBException {
		//String xmlString = xml;
		//String jsonString = null;
		JSONObject ls_jsonobj = null;
		Document lobj_loadXML;
		try {
			if (xml == null) {
				return getErrorMsg("BBPS Response failed");
			}

			lobj_loadXML = loadXML(xml);
			ls_jsonobj = new JSONObject(lobj_loadXML.getElementsByTagName("string").item(0).getTextContent());
			return ls_jsonobj;

		} catch (Exception ex) {
			return getErrorMsg("BBPS Exception  Response failed " + ex.getMessage());
			//return "[{\"ResponseCode\":\"99\",\"ResponseMessage\":\"Something going wrong, JAXBException : \"" + ex.getMessage() + "\"}]";
		}

	}

	public static JSONObject SoapXmlToJson(String xml, String as_tag_nm) throws JAXBException {
		//String xmlString = xml;
		//String jsonString = null;
		JSONObject ls_jsonobj = null;
		Document lobj_loadXML;
		try {
			if (xml == null) {
				return getErrorMsg("BBPS Response failed");
			}

			lobj_loadXML = loadXML(xml);
			ls_jsonobj = new JSONObject(lobj_loadXML.getElementsByTagName(as_tag_nm + "Result").item(0).getTextContent());
			return ls_jsonobj;

		} catch (Exception ex) {
			return getErrorMsg("BBPS Exception  Response failed " + ex.getMessage());
			//return "[{\"ResponseCode\":\"99\",\"ResponseMessage\":\"Something going wrong, JAXBException : \"" + ex.getMessage() + "\"}]";
		}

	}

	public static JSONObject xmlToJsonObj(String xml) throws JAXBException, JSONException {
		String xmlString = xml;
		if (xml == null) {
			return getErrorMsg("BBPS Response failed");
		}
		try {
			JAXBContext jc = JAXBContext.newInstance(String.class);
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			StreamSource xmlSource = new StreamSource(new StringReader(xmlString));
			JAXBElement<String> je = (JAXBElement<String>) unmarshaller.unmarshal(xmlSource, String.class);
			return new JSONObject(je.getValue());
		} catch (JAXBException ex) {
			return getErrorMsg("BBPS JAXBException Response failed " + ex.getMessage());
			//return "[{\"ResponseCode\":\"99\",\"ResponseMessage\":\"Something going wrong, JAXBException : \"" + ex.getMessage() + "\"}]";
		} catch (Exception ex) {
			return getErrorMsg("BBPS Exception  Response failed " + ex.getMessage());
			//return "[{\"ResponseCode\":\"99\",\"ResponseMessage\":\"Something going wrong, JAXBException : \"" + ex.getMessage() + "\"}]";
		}
	}

	public static JSONObject getErrorMsg(String as_message) {
		String ls_error;
		ls_error = "[{\"ResponseCode\":\"0\",\"ResponseMessage\":\"Something going wrong, JAXBException : \"" + as_message + "\"}]";
		try {
			return new JSONObject(ls_error);
		} catch (JSONException e) {
			return null;
		}
	}

	public String convertMapToSring(Map<String, ?> map) {
		String mapAsString = map.keySet().stream()
				.map(key -> key + "=" + map.get(key))
				.collect(Collectors.joining(", ", "{", "}"));
		return mapAsString;
	}

	public Map<String, String> convertMapToString(String mapAsString) {
		Map<String, String> map = Arrays.stream(mapAsString.split(","))
				.map(entry -> entry.split("="))
				.collect(Collectors.toMap(entry -> entry[0].trim(), entry -> entry[1].trim()));
		return map;
	}

	public static void LogPrintError(String error) {
		System.out.println("We are sorry for the inconvenience caused to you,HRMS : " + error);
	}

	public static void PrintErrLog(long uniqueNumber,String Msg) {		
		System.out.println("=========================Netbanking Error Print=>"+uniqueNumber+"============================");
		System.out.println(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime()) + "  Error :  " + Msg);
	}

	/**
	 *
	 * @param ls_date
	 * @return
	 */
	public static boolean IsExpireDate(String ls_date) {

		Date d1 = null;
		Date d2 = null;

		SimpleDateFormat format = new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy");
		try {
			Date date = new Date();
			String dateStart = format.format(date);
			String dateStop = ls_date;
			//System.out.println("dateStart : " + dateStart);
			//System.out.println("dateStop : " + dateStop);
			try {
				d1 = format.parse(dateStart);
				d2 = format.parse(dateStop);
			} catch (ParseException e) {
				//System.out.println("ParseException : " + e.getMessage());
				//e.printStackTrace();
				PrintErrLog(0,"IsExpireDate ParseException: " + e.getMessage());
			}
			// Get msec from each, and subtract.
			long diff = d2.getTime() - d1.getTime();
			long diffSeconds = diff / 1000 % 60;

			if (diffSeconds < 30) {
				return true;
			}
			return false;
		} catch (Exception e) {
			PrintErrLog(0,"IsExpireDate Exception : " + e.getMessage());
			return false;
		}
	}

	/**
     @param aErrorTitle -Error message title.
     @param aErrorMsg -Error message which is shown to user.
     @param aError -Actual error message.
     @return -Return String value of json format.
	 */
	public static JSONArray ofGetErrDataJson(String aErrorTitle, String aErrorMsg, String aError) {

		JSONObject errorObject = new JSONObject();   
		if(aError!=null && !"".equals(aError)){
			if (aError.trim().substring(0, 1).equals("[")) {
				errorObject.put("ERROR",new JSONArray(aError));
			}else if (aError.trim().substring(0, 1).equals("{")) {
				errorObject.put("ERROR",new JSONObject(aError));                
			}else{
				errorObject.put("ERROR", aError);			
			}
		}else {
			errorObject.put("ERROR","");				
		}

		errorObject.put("ERROR_TITLE", aErrorTitle);
		errorObject.put("ERROR_MSG", aErrorMsg);

		JSONArray errorListObject = new JSONArray();
		errorListObject.put(errorObject);
		return errorListObject;
	}
	
	/**
    @param aSucessTitle -Sucess message title.
    @param aSucessMsg -Sucess message which is shown to user. 
    @return -Return String value of json format.
	 */
	public static JSONArray ofGetSucessDataJson(String aSucessTitle, String aSucessMsg) {

		JSONObject errorObject = new JSONObject();   
		
		errorObject.put("TITLE", aSucessTitle);
		errorObject.put("MESSAGE", aSucessMsg);

		JSONArray errorListObject = new JSONArray();
		errorListObject.put(errorObject);
		return errorListObject;
	}

	/**
     @param aErrorStatus - Error Status for identification of error.
     @param aErrorTitle -Error message title.
     @param aErrorMsg -Error message which is shown to user.
     @param aError -Actual error message.
     @param asDefaultMsg - default English message..
     @param aresponse_cd - Response code of API.
     @param acolor -Color code of Message
     @return -Return String value of jsonlist format.
	 */
	public static String ofGetErrDataJsonArray(String aErrorStatus, String aErrorTitle, String aErrorMsg, String aError,String asDefaultMsg,String aresponse_cd,String acolor) {

		String 		callerClassName ="";
		String 		methodname="";
		String[] 	callerClassNameList;
		int 		callerClassNameCnt;
		String 		ls_defaultMsg="";
		
		//get the class name of caller class
		try {		
			callerClassName = new Exception().getStackTrace()[1].getClassName();
			methodname=new Exception().getStackTrace()[1].getMethodName();

			callerClassNameList=callerClassName.split("[.]");
			callerClassNameCnt=callerClassNameList.length;			

			if (callerClassNameCnt == 1) {
				callerClassName=callerClassNameList[0]+"/"+methodname;
			}else if (callerClassNameCnt >= 2 ){
				callerClassName=callerClassNameList[callerClassNameCnt - 2]+"/"+callerClassNameList[callerClassNameCnt- 1]+"/"+methodname;
			}

		}catch(Exception err){
			callerClassName="Error in get class name.";
		}

		JSONObject errorObject = new JSONObject();   
		JSONArray errorListJsonObject = new JSONArray();
		
		if(aErrorMsg==null || "".equals(aErrorMsg)) {
			ls_defaultMsg=asDefaultMsg;
		}else {
			ls_defaultMsg=aErrorMsg;
		}		
		
		errorListJsonObject = ofGetErrDataJson( aErrorTitle, ls_defaultMsg, aError);

		errorObject.put("ERR_PATH", callerClassName);
		errorObject.put("STATUS", aErrorStatus);
		errorObject.put("MESSAGE",ls_defaultMsg);        
		errorObject.put("RESPONSE", errorListJsonObject);
		errorObject.put("RESPONSECODE", aresponse_cd);
		errorObject.put("MESSAGE_EN", asDefaultMsg);
		errorObject.put("ACTIVITY_CD", "");
		errorObject.put("COLOR", acolor);
		JSONArray errorListObject = new JSONArray();
		errorListObject.put(errorObject);
		return errorListObject.toString();
	}

	
	/**
    @param aSucessStatus - Sucess Status for identification of error.
    @param aSucessTitle -Sucess message title.
    @param aSucessMsg -Sucess message which is shown to user.
    @param aactivity_cd - activity Code.
    @param aresponse_cd - Response code of API.
    @param acolor -Color code of Message
    @param aextra_keys -used to store extra keys
    @return -Return String value of jsonlist format.
	 */
	public static String ofGetSuccessDataJsonArray(String aSucessStatus, String aSucessTitle, String aSucessMsg,String aactivity_cd,String aresponse_cd,String acolor,Map<String,Object> aextra_keys) {

		return "";
		/*
		 * String callerClassName =""; String methodname=""; String[]
		 * callerClassNameList; int callerClassNameCnt;
		 * 
		 * JSONObject errorObject = new JSONObject(); JSONArray errorListJsonObject =
		 * new JSONArray(); errorListJsonObject = ofGetErrDataJson( aErrorTitle,
		 * aErrorMsg, aError);
		 * 
		 * errorObject.put("ERR_PATH", callerClassName); errorObject.put("STATUS",
		 * aErrorStatus); errorObject.put("MESSAGE",aErrorMsg);
		 * errorObject.put("RESPONSE", errorListJsonObject);
		 * errorObject.put("RESPONSECODE", aresponse_cd); errorObject.put("ACTIVITY_CD",
		 * aactivity_cd); errorObject.put("COLOR", acolor); JSONArray errorListObject =
		 * new JSONArray(); errorListObject.put(errorObject); return
		 * errorListObject.toString();
		 */
		
		
	}

	/**
    @param aErrorStatus - Error Status for identification of error.
    @param aErrorTitle -Error message title.
    @param aErrorMsg -Error message which is shown to user.
    @param aError -Actual error message.
    @param asDefaultMsg - default English Message.
    @param aresponse_cd - Response code of API.
    @param acolor -Color code of Message
    @return -Return String value of jsonlist format.
	 */
	public static String ofGetErrDataJsonObject(String aErrorStatus, String aErrorTitle, String aErrorMsg, String aError,String asDefaultMsg,String aresponse_cd,String acolor) {

		String 		callerClassName ="";
		String 		methodname="";
		String[] 	callerClassNameList;
		int 		callerClassNameCnt;
		String 		ls_errorTitle;
		String 		ls_defaultMsg="";
		
		//get the class name of caller class
		try {		
			callerClassName = new Exception().getStackTrace()[1].getClassName();
			methodname=new Exception().getStackTrace()[1].getMethodName();

			callerClassNameList=callerClassName.split("[.]");
			callerClassNameCnt=callerClassNameList.length;			

			if (callerClassNameCnt == 1) {
				callerClassName=callerClassNameList[0]+"/"+methodname;
			}else if (callerClassNameCnt >= 2 ){
				callerClassName=callerClassNameList[callerClassNameCnt - 2]+"/"+callerClassNameList[callerClassNameCnt- 1]+"/"+methodname;
			}

		}catch(Exception err){
			callerClassName="Error in get class name.";
		}

		JSONObject errorObject = new JSONObject();    
		JSONArray errorListJsonObject = new JSONArray();
		
		if(aErrorMsg==null || "".equals(aErrorMsg)) {
			ls_defaultMsg=asDefaultMsg;
		}else {
			ls_defaultMsg=aErrorMsg;
		}
		
		errorListJsonObject = ofGetErrDataJson( aErrorTitle, aErrorMsg, aError);
		
		errorObject.put("ERR_PATH", callerClassName);
		errorObject.put("STATUS", aErrorStatus);
		errorObject.put("MESSAGE",aErrorMsg);        
		errorObject.put("RESPONSE", errorListJsonObject);
		errorObject.put("RESPONSECODE", aresponse_cd);
		errorObject.put("MESSAGE_EN",asDefaultMsg);
		errorObject.put("ACTIVITY_CD", "");
		errorObject.put("COLOR", acolor);
		return errorObject.toString();
	}

	public static String ofGetTotalErrString(Exception err,String userMessage)
	{
		String ls_error_msg="";
		ls_error_msg=userMessage+"\r\n"+err.getMessage();
		StringWriter stringWriter=new StringWriter();
		PrintWriter writer=new PrintWriter(stringWriter);
		err.printStackTrace(writer);
		ls_error_msg=ls_error_msg+"\r\n"+stringWriter.toString();
		return  ls_error_msg;
	}

	public static String GetMaskingMobileNo(String ls_mobile) {
		String ls_result="";
		int as=0;
		for(int i=0;i<ls_mobile.length();i++){
			as =(int)ls_mobile.charAt(i);
			if(as < 48 || as > 57){
				ls_result = ls_result+ls_mobile.charAt(i);
			}else{
				if (i<=1 || i == ls_mobile.length() - 1 ) {
					ls_result = ls_result+ls_mobile.charAt(i);
				}else{
					ls_result = ls_result + "X";
				}  
			}

		}
		return ls_result;
	}

	public static String GetMaskingEmail(String ls_mail){
		String maskedEmail=ls_mail.replaceAll("(^[^@]{3}|(?!^)\\G)[^@]", "$1*");
		return maskedEmail;
	}
	
	public static String ofGetAcctCardFalg(String as_Data) {
		String ls_flag="";
		
		if("FINACLE".equalsIgnoreCase(as_Data) || "ABABIL".equalsIgnoreCase(as_Data)){
			ls_flag="A";
		}else if("TRANZWARE".equalsIgnoreCase(as_Data)){
			ls_flag="C";
		}else{
			ls_flag=as_Data;
		}		
		return ls_flag;
	}
	
	public static String ofReplaceData(String a_replace_char,String a_key_data) {
		String ls_repString="";
		
		ls_repString=StringUtils.rightPad("",a_key_data.length(),a_replace_char);
		
		return ls_repString;
	}
	
	public static JSONObject of_replaceKeysData(JSONObject as_inputJson,String as_replaceChar,String as_replaceKeyStr) {
		
		
		JSONObjectImpl 		jsonRequesData;
		Set<String>   	keysList;
		JSONObject 		jsonReplaceObj;
		JSONArray   	JsonArrayObject;
		long 		keysCnt ;  
		long 		subJsonArrayCnt;
		String 		V_KEY_NAME;
		String 		V_KEY_VALUE = "";
		JSONObject 	JsonSubObj;
		JSONObject  jsonSubObjRet;
		JSONArray	V_SUB_JSON_ARR_RET;
		Object		object;
		String 		ls_replaceValue;
		try {
			
			jsonRequesData= new JSONObjectImpl(as_inputJson.toString());			
			keysList= jsonRequesData.keySet();
			keysCnt=keysList.size();
			jsonReplaceObj=new JSONObject(as_replaceKeyStr);
			
			/*GET THE LIST OF KEYS AND COUNT .LOOP KEY WISE*/ 
			for (String keyName : keysList) {
				/*CHECK IN KEY NAME STRING.*/
				if (jsonReplaceObj.has(keyName)){
					
					/*GET THE KEY VALUE FROM INPUT JSON*/
					object=jsonRequesData.get(keyName);
					if (object instanceof JSONObject== true || object instanceof JSONArray ==true){
						ls_replaceValue="";
					}else{
						ls_replaceValue=String.valueOf(object);
					}
					/*PUT THE REPLACE VALUE IN REQUEST JSON OBJECT*/
					jsonRequesData.put(V_KEY_VALUE,ofReplaceData(ls_replaceValue,as_replaceChar));									
				}
				
				object=jsonRequesData.get(keyName);
				
				/*GET THE JSON OBJECT FROM JSON */
				if (object instanceof JSONObject){
					/*RE CALL SAME FUNCTION TO REPLACE THE VALUE IN OBJECT AND GET OBJECT FROM FUNCTION.*/
					jsonSubObjRet=of_replaceKeysData((JSONObject)object,as_replaceChar,as_replaceKeyStr);					
					
					/*PUT THE REPLACED JSON OBJECT INTO MAIN JSON.*/
					jsonRequesData.put(V_KEY_VALUE, jsonSubObjRet);
				}else if(object instanceof JSONArray){
					 /*GET THE JSON ARRAY FOR KEY*/
					JsonArrayObject=(JSONArray)object;
					subJsonArrayCnt=JsonArrayObject.length();
					
					for(int i=0;i < subJsonArrayCnt ;i++) {
						
						JsonSubObj=null;
						
						
					}
					
					
					
					
				}
				
				
				
				
			
				
				
				
				
				
			}
			
			
			
			
			
		}catch(Exception exception) {
			exception.printStackTrace();
		}
		
		

		
		return null;
	}
	
}
