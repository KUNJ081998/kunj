package com.easynet.controller;

import com.easynet.configuration.SOAPConnector;
/*import com.google.gson.Gson;
import com.google.gson.GsonBuilder;*/

import java.io.IOException;

import org.apache.ws.axis2.GetAccountDetails;
import org.apache.ws.axis2.ObjectFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import city.xsd.Account;
import city.xsd.GetAccountDetailsRequest;
import org.apache.ws.axis2.GetAccountDetailsResponse;


@RestController
public class SoapClient {

	@Autowired
	private SOAPConnector soapConnector;
	
	@RequestMapping("testJsondata")
	public String getAccountInfo()
	{		
		city.xsd.ObjectFactory xsdObjecyfactory=new city.xsd.ObjectFactory(); 
		GetAccountDetailsRequest request=new GetAccountDetailsRequest();
		request.setAccountNumber(xsdObjecyfactory.createGetAccountDetailsRequestAccountNumber("2251358101001"));
		request.setPassword(xsdObjecyfactory.createGetAbabilAccountImageRequestPassword("Prod@5731"));
		request.setUsername(xsdObjecyfactory.createGetAbabilAccountImageRequestUsername("dob"));
		
		ObjectFactory factory=new ObjectFactory();
		GetAccountDetails detail=factory.createGetAccountDetails();
		detail.setRequest(factory.createGetAccountDetailsRequest(request));
		GetAccountDetailsResponse responseWapper=(GetAccountDetailsResponse) soapConnector.callWebService(detail);
		
		city.xsd.GetAccountDetailsResponse response=responseWapper.getReturn().getValue();
		Account accountDetail=response.getResponseData().getValue();
		
		
		/*
		 * ////gson/////// Gson gson = new GsonBuilder()
		 * //.excludeFieldsWithoutExposeAnnotation()
		 * //.excludeFieldsWithModifiers(java.lang.reflect.Modifier.STATIC) //
		 * STATIC|TRANSIENT in the default configuration .create(); String jsonString =
		 * gson.toJson(accountDetail); System.err.println("jsonString=>"+jsonString);
		 * ////gson end ///////
		 */
	   	
		
		// Creating Object of ObjectMapper define in Jakson Api
        ObjectMapper Obj = new ObjectMapper();
        String jsonString="";
        try {
 
            // get Oraganisation object as a json string
            //jsonString = Obj.writeValueAsString(responseWapper);   
            jsonString = Obj.writeValueAsString(responseWapper);   
            // Displaying JSON String
            System.out.println(jsonString);
        }
 
        catch (IOException e) {
            e.printStackTrace();
        }
        
		
		/*
		 * GsonBuilder builder = new GsonBuilder(); builder.setPrettyPrinting();
		 * 
		 * Gson gson = builder.create();
		 * 
		 * String jsonString = gson.toJson(accountDetail);
		 */
	     return jsonString;
	     	     
	      	    	
		/*
		 * //Check for response code status
		 * JSONObject accountJsonObject=new JSONObject();
		 * accountJsonObject.put("account_name",
		 * accountDetail.getAccountName().getValue());
		 * accountJsonObject.put("account_no", accountDetail.getAccount().getValue());
		 * return accountJsonObject.toString();
		 */			
		
	}
}
