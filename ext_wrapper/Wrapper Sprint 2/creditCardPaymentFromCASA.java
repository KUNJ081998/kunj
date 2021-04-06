package com.easynet.controller.ApiController.CardAPI;

import card.xsd.CreditCardPaymentFromCASARequest;
import card.xsd.ObjectFactory;
import com.easynet.configuration.PropConfiguration;
import com.easynet.configuration.SOAPConnector;
import com.easynet.impl.JSONObjectImpl;
import com.easynet.impl.LoggerImpl;
import com.easynet.util.common;
import static com.easynet.util.common.PrintErrLog;
import com.easynet.util.readXML;
import org.apache.ws.axis2.CreditCardPaymentFromCASA;
import org.apache.ws.axis2.CreditCardPaymentFromCASAResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.soap.client.SoapFaultClientException;

@Component
public class creditCardPaymentFromCASA {

    static Logger logger=LoggerFactory.getLogger(creditCardPaymentFromCASA.class);
    
    @Autowired
    PropConfiguration propConfig;
    
    
    @Autowired
    private SOAPConnector soapConnector;

    public String creditcardPaymentFromCASA(String reqdata) {
        String ls_accountCurrency = "";
        String ls_amount = "";
        String ls_cardNoActual = "";
        String ls_customerAccount = "";
        String ls_originalAmount = "";
        String ls_originalCurrency = "";
        String ls_password = "";
        String remarks = "";
        String ls_userName = "";
        String ls_responseData = "";
        String ls_responseapprovalData = "";
        String ls_responseCode = "";
        String ls_responseMessage = "";
        String ls_actualErrMsg = "";
        String ls_langResCodeMsg="";
        LoggerImpl loggerImpl=null;

        try {
            loggerImpl=new LoggerImpl();
        
            loggerImpl.info(logger,"Preparing requset data and calling API.","IN:creditcardPaymentFromCASA");
            loggerImpl.generateProfiler("creditcardPaymentFromCASA");
            loggerImpl.startProfiler("Preparing request data");
            
            JSONObject reqJsonObj = new JSONObject(reqdata);
            ls_accountCurrency = reqJsonObj.getString("ACCOUNTCURRENCY");
            ls_amount = reqJsonObj.getString("AMOUNT");
            ls_cardNoActual = reqJsonObj.getString("CARDNOACTUAL");
            ls_customerAccount = reqJsonObj.getString("CUSTOMERACCOUNT");
            ls_originalAmount = reqJsonObj.getString("ORIGINALAMOUNT");
            ls_originalCurrency = reqJsonObj.getString("ORIGINALCURRENCY");
            remarks = reqJsonObj.getString("REMARKS");

            if ((ls_accountCurrency == null || "".equals(ls_accountCurrency)) || (ls_amount == null || "".equals(ls_amount)) || (ls_cardNoActual == null || "".equals(ls_cardNoActual))
                    || (ls_customerAccount == null || "".equals(ls_customerAccount)) || (ls_originalAmount == null || "".equals(ls_originalAmount))
                    || (ls_originalCurrency == null || "".equals(ls_originalCurrency)) || (remarks == null || "".equals(remarks))) {

                ls_responseData = common.ofGetErrDataJsonArray("99", "Validation Failed.", "InValid request.", "Null values found in acct_no key.", "", "", "R");
                return ls_responseData;
            }
            ls_userName = readXML.getXmlData("root>CUST_360>AUTHORIZATION>USER_NAME");
            ls_password = readXML.getXmlData("root>CUST_360>AUTHORIZATION>PASSWORD");

            //get card xsd object factory object.
            card.xsd.ObjectFactory xsdObjectFactory = new card.xsd.ObjectFactory();
            //created CreditCardRequestFromCASA request object from object factory.
            CreditCardPaymentFromCASARequest creditCardPaymentFromCASArequest = xsdObjectFactory.createCreditCardPaymentFromCASARequest();
            
            loggerImpl.debug(logger,"Json to xml conversion done.", "IN:creditcardPaymentFromCASA");

            //setting all the values in request.
            creditCardPaymentFromCASArequest.setAccountCurrency(xsdObjectFactory.createCreditCardPaymentFromCASARequestAccountCurrency(ls_accountCurrency));
            creditCardPaymentFromCASArequest.setAmount(xsdObjectFactory.createCreditCardPaymentFromCASARequestAmount(Double.valueOf(ls_amount)));
            creditCardPaymentFromCASArequest.setCardNoActual(xsdObjectFactory.createCreditCardPaymentFromCASARequestCardNoActual(ls_cardNoActual));
            creditCardPaymentFromCASArequest.setCustomerAccount(xsdObjectFactory.createCreditCardPaymentFromCASARequestCustomerAccount(ls_customerAccount));
            creditCardPaymentFromCASArequest.setOriginalAmount(xsdObjectFactory.createCreditCardPaymentFromCASARequestOriginalAmount(Double.valueOf(ls_originalAmount)));
            creditCardPaymentFromCASArequest.setOriginalCurrency(xsdObjectFactory.createCreditCardPaymentFromCASARequestOriginalCurrency(ls_originalCurrency));
            creditCardPaymentFromCASArequest.setPassword(xsdObjectFactory.createCreditCardPaymentFromCASARequestPassword(ls_password));
            creditCardPaymentFromCASArequest.setRemarks(xsdObjectFactory.createCreditCardPaymentFromCASARequestRemarks(remarks));
            creditCardPaymentFromCASArequest.setUsername(xsdObjectFactory.createCreditCardPaymentFromCASARequestUsername(ls_userName));

            //get object factory object from ws axis2.
            org.apache.ws.axis2.ObjectFactory axis2ObjectFactory = new org.apache.ws.axis2.ObjectFactory();
            //wrapping the request in CreditCardPaymentFromCASA
            CreditCardPaymentFromCASA creditCardPaymentFromCasa = axis2ObjectFactory.createCreditCardPaymentFromCASA();
            creditCardPaymentFromCasa.setRequest(axis2ObjectFactory.createCreditCardPaymentFromCASARequest(creditCardPaymentFromCASArequest));
            
            loggerImpl.debug(logger,"creditcardPaymentFromCASA API calling", "IN:creditcardPaymentFromCASA");
            loggerImpl.startProfiler("creditcardPaymentFromCASA API calling.");

            //created response object of CreditCardPaymentFromCASA response
            CreditCardPaymentFromCASAResponse creditCardPaymentFromCASAresponse = null;
            try {
                creditCardPaymentFromCASAresponse = (CreditCardPaymentFromCASAResponse) soapConnector.callWebService(creditCardPaymentFromCasa);
            } catch (SoapFaultClientException soapException) {
				ls_actualErrMsg = soapException.getFaultStringOrReason();
				ls_responseData = common.ofGetErrDataJsonArray("999", 
						propConfig.getMessageOfResCode("commen.title.999", "Alert."),
						propConfig.getMessageOfResCode("commen.exception.999","","(ENP271)"),
						ls_actualErrMsg, "Currently Service under maintenance so please try later (ENP271)", "0", "R");

				ls_actualErrMsg = common.ofGetTotalErrString(soapException, "");
				loggerImpl.error(logger,"SoapFaultClientException : " + ls_actualErrMsg,"EX:creditcardPaymentFromCASA");
				return ls_responseData;
			}
            
            loggerImpl.debug(logger,"creditcardPaymentFromCASA API called successfully.", "IN:creditcardPaymentFromCASA",creditCardPaymentFromCASAresponse);
            loggerImpl.startProfiler("preparing creditcardPaymentFromCASA API response data.");

            //created CreditCardPaymentFromCASAResponse object.
            card.xsd.CreditCardPaymentFromCASAResponse xsd_creditCardPaymentFromCASAresponse = creditCardPaymentFromCASAresponse.getReturn().getValue();

            //getting all the values from the response.
            ls_responseCode = xsd_creditCardPaymentFromCASAresponse.getResponseCode().getValue();
            ls_responseapprovalData = xsd_creditCardPaymentFromCASAresponse.getApprovalCode().getValue();
            ls_responseMessage = xsd_creditCardPaymentFromCASAresponse.getResponseMessage().getValue();

            /*
	* if response is 100 then success.
	* if response is 101 then transaction failed.
             */
            if (ls_responseCode != null && "100".equals(ls_responseCode)) {

                JSONObjectImpl creditCardPaymentFromCASAJsonObj = new JSONObjectImpl();
                JSONObject responseJsonObject = new JSONObject();
                JSONArray creditCardPaymentFromCASAJsonArray = new JSONArray();

                //setting the values in json response object.
                creditCardPaymentFromCASAJsonObj.put("APPROVALCODE", ls_responseapprovalData);
                creditCardPaymentFromCASAJsonObj.put("RESPONSECODE", ls_responseCode);
                creditCardPaymentFromCASAJsonObj.put("RESPONSEMESSAGE", ls_responseMessage);
                creditCardPaymentFromCASAJsonArray.put(creditCardPaymentFromCASAJsonObj);

                /*
                 * If transaction happens and API is called successfully.
                 */
                responseJsonObject.put("STATUS", "0");
                responseJsonObject.put("COLOR", "G");
                responseJsonObject.put("RESPONSE", creditCardPaymentFromCASAJsonArray);
                responseJsonObject.put("MESSAGE", "");
                ls_responseData = responseJsonObject.toString();
            } else { 

				ls_langResCodeMsg=propConfig.getResponseCode("creditcardPaymentFromCASA."+ls_responseCode);			
				ls_responseData = common.ofGetErrDataJsonArray(ls_langResCodeMsg,
						propConfig.getMessageOfResCode("commen.title."+ls_langResCodeMsg, "Alert."), 
						propConfig.getMessageOfResCode("creditcardPaymentFromCASA."+ls_responseCode,"","(ENP272)"),
						ls_responseMessage, "Currently Service under maintenance so please try later (ENP272).", ls_responseCode, "R");            		            		            	
			}
        }catch (Exception exception) {
			ls_actualErrMsg = common.ofGetTotalErrString(exception, "");            
			loggerImpl.error(logger,"Exception : " + ls_actualErrMsg,"IN:creditcardPaymentFromCASA");

			ls_responseData = common.ofGetErrDataJsonArray("999",
					propConfig.getMessageOfResCode("commen.title.999", "Alert."),
					propConfig.getMessageOfResCode("commen.exception.999","","(ENP273)"),
					exception.getMessage(),"Currently Service under maintenance so please try later (ENP273)","0", "R");			

		}finally {
			loggerImpl.stopAndPrintOptLogs(logger,"All API called successfully.","IN:creditcardPaymentFromCASA");
			loggerImpl.info(logger,"Response generated and send to client.", "IN:creditcardPaymentFromCASA");        	
		}
        return ls_responseData;
    }
}
