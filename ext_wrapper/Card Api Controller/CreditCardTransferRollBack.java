package com.easynet.controller.ApiController.CardAPI;

import card.xsd.CreditCardTransferResponse;
import card.xsd.CreditCardTransferReverseRequest;
import card.xsd.ObjectFactory;
import com.easynet.configuration.SOAPConnector;
import com.easynet.impl.JSONObjectImpl;
import com.easynet.util.common;
import static com.easynet.util.common.PrintErrLog;
import com.easynet.util.readXML;
import org.apache.ws.axis2.CreditCardTransferRollback;
import org.apache.ws.axis2.CreditCardTransferRollbackResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.soap.client.SoapFaultClientException;

@Component
public class CreditCardTransferRollBack {

    @Autowired
    private SOAPConnector soapConnector;

   
    public String creditCardTransferRollBack(String reqData) {

        String ls_amount = "";
        String ls_approvalCode = "";
        String ls_cardNoActual = "";
        String ls_cashierRequestId = "";
        String ls_currencyCode = "";
        String ls_password = "";
        String ls_remarks = "";
        String ls_userName = "";
        String ls_responseApprovalCode = "";
        String ls_responseCashierRequestId = "";
        String ls_responseCode = "";
        String ls_responseMessage = "";
        String ls_transactionDateTime = "";
        String actualErrMsg = "";
        String ls_responseData = "";

        try {
            JSONObject reqJsonObj = new JSONObject(reqData);
            ls_amount = reqJsonObj.getString("AMOUNT");
            ls_approvalCode = reqJsonObj.getString("APPROVALCODE");
            ls_cardNoActual = reqJsonObj.getString("CARDNOACTUAL");
            ls_cashierRequestId = reqJsonObj.getString("CASHIERREQID");
            ls_currencyCode = reqJsonObj.getString("CURRENCYCODE");
            ls_remarks = reqJsonObj.getString("REMARKS");

            if ((ls_amount == null || "".equals(ls_amount)) || (ls_approvalCode == null || "".equals(ls_approvalCode)) || (ls_cardNoActual == null || "".equals(ls_cardNoActual))
                    || (ls_cashierRequestId == null || "".equals(ls_cashierRequestId)) || (ls_currencyCode == null || "".equals(ls_currencyCode)) || (ls_remarks == null || "".equals(ls_remarks))) {

                ls_responseData = common.ofGetErrDataJsonArray("99", "Validation Failed.", "InValid request.", "Null values found in acct_no key.", "", "", "R");
                return ls_responseData;
            }
            ls_userName = readXML.getXmlData("root>CUST_360>AUTHORIZATION>USER_NAME");
            ls_password = readXML.getXmlData("root>CUST_360>AUTHORIZATION>PASSWORD");

            //get the object factory object.
            card.xsd.ObjectFactory xsdObjectFactory = new ObjectFactory();
            //get the request object.
            CreditCardTransferReverseRequest creditCardTransferReverseRequest = xsdObjectFactory.createCreditCardTransferReverseRequest();

            //setting all the values in the request.
            creditCardTransferReverseRequest.setAmount(xsdObjectFactory.createCreditCardTransferReverseRequestAmount(Double.valueOf(ls_amount)));
            creditCardTransferReverseRequest.setApprovalCode(xsdObjectFactory.createCreditCardTransferReverseRequestApprovalCode(ls_approvalCode));
            creditCardTransferReverseRequest.setCardNoActual(xsdObjectFactory.createCreditCardTransferReverseRequestCardNoActual(ls_cardNoActual));
            creditCardTransferReverseRequest.setCashierRequestId(xsdObjectFactory.createCreditCardTransferReverseRequestCashierRequestId(ls_cashierRequestId));
            creditCardTransferReverseRequest.setCurrencyCode(xsdObjectFactory.createCreditCardTransferReverseRequestCurrencyCode(ls_currencyCode));
            creditCardTransferReverseRequest.setPassword(xsdObjectFactory.createCreditCardTransferReverseRequestPassword(ls_password));
            creditCardTransferReverseRequest.setRemarks(xsdObjectFactory.createCreditCardTransferReverseRequestRemarks(ls_remarks));
            creditCardTransferReverseRequest.setUsername(xsdObjectFactory.createCreditCardTransferReverseRequestUsername(ls_userName));

            //get the object factory object.
            org.apache.ws.axis2.ObjectFactory axis2ObjectFactory = new org.apache.ws.axis2.ObjectFactory();
            //wrapping the request in the object.
            CreditCardTransferRollback creditCardTransferRollback = axis2ObjectFactory.createCreditCardTransferRollback();
            creditCardTransferRollback.setRequest(axis2ObjectFactory.createCreditCardTransferRollbackRequest(creditCardTransferReverseRequest));

            //get the response object.
            CreditCardTransferRollbackResponse creditCardTransferRollBackResponse = null;
            try {
                creditCardTransferRollBackResponse = (CreditCardTransferRollbackResponse) soapConnector.callWebService(creditCardTransferRollback);
            } catch (SoapFaultClientException soapException) {
                actualErrMsg = soapException.getFaultStringOrReason();
                PrintErrLog("CreditCardTransferRollBack SoapFaultClientException : " + actualErrMsg);
                ls_responseData = common.ofGetErrDataJsonArray("999", "Alert", "Currently Service under maintenance so please try later (ENP040)", actualErrMsg, "", "0", "R");
                return ls_responseData;
            }

            //get the response object.
            CreditCardTransferResponse xsd_creditCardTransferRollBackResponse = creditCardTransferRollBackResponse.getReturn().getValue();

            //getting all the data from the response.
            ls_responseApprovalCode = xsd_creditCardTransferRollBackResponse.getApprovalCode().getValue();
            ls_responseCashierRequestId = String.valueOf(xsd_creditCardTransferRollBackResponse.getCashierRequestId());
            ls_responseCode = xsd_creditCardTransferRollBackResponse.getResponseCode().getValue();
            ls_responseMessage = xsd_creditCardTransferRollBackResponse.getResponseMessage().getValue();
            ls_transactionDateTime = xsd_creditCardTransferRollBackResponse.getTransactionDateTime().getValue();

            /*
             * if response is 100 then success.
             * if response is 101 then transaction failed.
             */
            if (ls_responseCode != null && "100".equals(ls_responseCode)) {

                JSONObjectImpl CreditCardTransferRollBackJsonObj = new JSONObjectImpl();
                JSONArray CreditCardTransferRollBackJsonArray = new JSONArray();
                JSONObject responseJsonObject = new JSONObject();

                CreditCardTransferRollBackJsonObj.put("RESPAPPROVALCODE", ls_responseApprovalCode);
                CreditCardTransferRollBackJsonObj.put("RESPCASHIERREQID", ls_cashierRequestId);
                CreditCardTransferRollBackJsonObj.put("RESPONSECODE", ls_responseCode);
                CreditCardTransferRollBackJsonObj.put("RESPONSEMESSAGE", ls_responseMessage);
                CreditCardTransferRollBackJsonObj.put("TRANSDATETIME", ls_transactionDateTime);

                CreditCardTransferRollBackJsonArray.put(CreditCardTransferRollBackJsonObj);

                responseJsonObject.put("STATUS", "0");
                responseJsonObject.put("COLOR", "G");
                responseJsonObject.put("RESPONSE", CreditCardTransferRollBackJsonArray);
                responseJsonObject.put("MESSAGE", "");

                ls_responseData = responseJsonObject.toString();
            } else { //other than 100,101.			
                ls_responseData = common.ofGetErrDataJsonArray("999", "Alert", "Currently Service under maintenance so please try later (ENP036).", ls_responseMessage, "", ls_responseCode, "R");
            }
        } catch (Exception err) {
            actualErrMsg = common.ofGetTotalErrString(err, "");
            PrintErrLog("CreditCardTransferRollBack Exception : " + actualErrMsg);
            ls_responseData = common.ofGetErrDataJsonArray("999", "Alert", "Currently Service under maintenance so please try later (ENP043)", actualErrMsg, "", "0", "R");
            return ls_responseData;
        }
        return ls_responseData;
    }
}
