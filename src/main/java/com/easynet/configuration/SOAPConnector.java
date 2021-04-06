package com.easynet.configuration;

import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

public class SOAPConnector extends WebServiceGatewaySupport{

	/**
     This Method return response from soap URL.
	 * @param URL-User Specified URL of SOAP API.
	 * @param request- Request data which send to SOAP API.         
	 */
	public Object callWebService(String url, Object request){
		return getWebServiceTemplate().marshalSendAndReceive(url, request);
	}        


	/**
      This Method return response from soap URL and Default URL will be 
      used to call SOAP API.
	 * @param request- Request data which send to SOAP API.         
	 */
	public Object callWebService(Object request){
		return getWebServiceTemplate().marshalSendAndReceive(request);
	}
}