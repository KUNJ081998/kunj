package com.easynet.controller.ApiController;

import city.qr.xsd.DoQrMerchantPaymentRequest;
import city.qr.xsd.DoQrMerchantPaymentResponse;
import city.qr.xsd.ObjectFactory;
import com.easynet.configuration.SOAPConnector;
import com.easynet.impl.JSONObjectImpl;
import com.easynet.util.common;
import static com.easynet.util.common.PrintErrLog;
import com.easynet.util.readXML;
import org.apache.ws.axis2.DoQrMerchantPaymentMCResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.soap.client.SoapFaultClientException;

@Component
public class DoQrMerchantPaymentMC {

    @Autowired
    private SOAPConnector soapConnector;

    public String DoQrMerchantPaymentMC(String reqData) {

        //reqeust parameters
        String ls_mccString = "";
        String ls_billNumber = "";
        String ls_conAmount = "";
        String ls_conLabel = "";
        String ls_conversionRate = "";
        String ls_destinationAmount = "";
        String ls_emailAddress = "";
        String ls_fundingSource = "";
        String ls_loyaltyNumber = "";
        String ls_markUpDestinationAmount = "";
        String ls_markUpPercentage = "";
        String ls_merAccInfo = "";
        String ls_merCity = "";
        String ls_merCityAltLang = "";
        String ls_merCountryCode = "";
        String ls_merIdType = "";
        String ls_merName = "";
        String ls_merNameAltLang = "";
        String ls_merPostalCode = "";
        String ls_mobileNumber = "";
        String ls_password = "";
        String ls_purOfTransaction = "";
        String ls_qrString = "";
        String ls_refLabel = "";
        String ls_remarks = "";
        String ls_sourceCard = "";
        String ls_sourceCardExpiry = "";
        String ls_sourceName = "";
        String ls_storeLabel = "";
        String ls_terLabel = "";
        String ls_tipAmount = "";
        String ls_tranAmount = "";
        String ls_tranCurrency = "";
        String ls_transId = "";
        String ls_userName = "";

        //response parameters.
        String ls_responseData = "";
        String ls_responseCode = "";
        String ls_responseMessage = "";
        String ls_transactionDateTime = "";
        String ls_transactionRefNumber = "";
        String actualErrMsg = "";

        try {
            JSONObject reqJsonObj = new JSONObject(reqData);
            ls_billNumber = reqJsonObj.getString("BILLNUMBER");
            ls_conAmount = reqJsonObj.getString("CONAMOUNT");
            ls_conLabel = reqJsonObj.getString("CONLABEL");
            ls_conversionRate = reqJsonObj.getString("CONVERSIONRATE");
            ls_destinationAmount = reqJsonObj.getString("DESTAMT");
            ls_emailAddress = reqJsonObj.getString("EMAILADDR");
            ls_fundingSource = reqJsonObj.getString("FUNDINGSRC");
            ls_loyaltyNumber = reqJsonObj.getString("LOYALTYNUMBER");
            ls_markUpDestinationAmount = reqJsonObj.getString("MARKUPDESTAMT");
            ls_markUpPercentage = reqJsonObj.getString("MARKUPPERC");
            ls_mccString = reqJsonObj.getString("MCCSTRING");
            ls_merAccInfo = reqJsonObj.getString("MERACCINFO");
            ls_merCity = reqJsonObj.getString("MERCITY");
            ls_merCityAltLang = reqJsonObj.getString("MERCITYALTLANG");
            ls_merCountryCode = reqJsonObj.getString("MERCOUNTRYCODE");
            ls_merIdType = reqJsonObj.getString("MERIDTYPE");
            ls_merName = reqJsonObj.getString("MERNAME");
            ls_merNameAltLang = reqJsonObj.getString("MERNAMEALTLANG");
            ls_merPostalCode = reqJsonObj.getString("MERPOSTALCODE");
            ls_mobileNumber = reqJsonObj.getString("MOBILENUMBER");
            ls_purOfTransaction = reqJsonObj.getString("PUROFTRANS");
            ls_qrString = reqJsonObj.getString("QRSTRING");
            ls_refLabel = reqJsonObj.getString("REFLABEL");
            ls_remarks = reqJsonObj.getString("REMARKS");
            ls_sourceCard = reqJsonObj.getString("SOURCECARD");
            ls_sourceCardExpiry = reqJsonObj.getString("SOURCECARDEXP");
            ls_sourceName = reqJsonObj.getString("SOURCENAME");
            ls_storeLabel = reqJsonObj.getString("STORELABEL");
            ls_terLabel = reqJsonObj.getString("TERLABEL");
            ls_tipAmount = reqJsonObj.getString("TIPAMOUNT");
            ls_tranAmount = reqJsonObj.getString("TRANAMT");
            ls_tranCurrency = reqJsonObj.getString("TRANSCURRENCY");
            ls_transId = reqJsonObj.getString("TRANSID");

            /*if () {

            }*/
            ls_userName = readXML.getXmlData("root>CUST_360>AUTHORIZATION>USER_NAME");
            ls_password = readXML.getXmlData("root>CUST_360>AUTHORIZATION>PASSWORD");

            //get object factory object.
            city.qr.xsd.ObjectFactory xsdObjectFactory = new ObjectFactory();
            //created request object.
            DoQrMerchantPaymentRequest doQrMerchantPaymentMC = xsdObjectFactory.createDoQrMerchantPaymentRequest();

            //setting all the values
            doQrMerchantPaymentMC.setBillNumber(xsdObjectFactory.createDoQrMerchantPaymentRequestBillNumber(ls_billNumber));
            doQrMerchantPaymentMC.setConAmount(xsdObjectFactory.createDoQrMerchantPaymentRequestConAmount(Double.valueOf(ls_conAmount)));
            doQrMerchantPaymentMC.setConLabel(xsdObjectFactory.createDoQrMerchantPaymentRequestConLabel(ls_conLabel));
            doQrMerchantPaymentMC.setConversionRate(xsdObjectFactory.createDoQrMerchantPaymentRequestConversionRate(ls_conversionRate));
            doQrMerchantPaymentMC.setDestinationAmount(Double.valueOf(ls_destinationAmount));
            doQrMerchantPaymentMC.setEmailAddress(xsdObjectFactory.createDoQrMerchantPaymentRequestEmailAddress(ls_emailAddress));
            doQrMerchantPaymentMC.setFundingSource(xsdObjectFactory.createDoQrMerchantPaymentRequestFundingSource(ls_fundingSource));
            doQrMerchantPaymentMC.setLoyaltyNumber(xsdObjectFactory.createDoQrMerchantPaymentRequestLoyaltyNumber(ls_loyaltyNumber));
            doQrMerchantPaymentMC.setMCC(xsdObjectFactory.createDoQrMerchantPaymentRequestMCC(ls_mccString));
            doQrMerchantPaymentMC.setMarkUpDestinationAmount(Double.valueOf(ls_markUpDestinationAmount));
            doQrMerchantPaymentMC.setMarkUpPercentage(xsdObjectFactory.createDoQrMerchantPaymentRequestMarkUpPercentage(ls_markUpPercentage));
            doQrMerchantPaymentMC.setMerAccInfo(xsdObjectFactory.createDoQrMerchantPaymentRequestMerAccInfo(ls_merAccInfo));
            doQrMerchantPaymentMC.setMerCity(xsdObjectFactory.createDoQrMerchantPaymentRequestMerCity(ls_merCity));
            doQrMerchantPaymentMC.setMerCityAltLang(xsdObjectFactory.createDoQrMerchantPaymentRequestMerCityAltLang(ls_merCityAltLang));
            doQrMerchantPaymentMC.setMerCountryCode(xsdObjectFactory.createDoQrMerchantPaymentRequestMerCountryCode(ls_merCountryCode));
            doQrMerchantPaymentMC.setMerIdType(xsdObjectFactory.createDoQrMerchantPaymentRequestMerIdType(ls_merIdType));
            doQrMerchantPaymentMC.setMerName(xsdObjectFactory.createDoQrMerchantPaymentRequestMerName(ls_merName));
            doQrMerchantPaymentMC.setMerNameAltLang(xsdObjectFactory.createDoQrMerchantPaymentRequestMerNameAltLang(ls_merNameAltLang));
            doQrMerchantPaymentMC.setMerPostalCode(xsdObjectFactory.createDoQrMerchantPaymentRequestMerPostalCode(ls_merPostalCode));
            doQrMerchantPaymentMC.setMobileNumber(xsdObjectFactory.createDoQrMerchantPaymentRequestMobileNumber(ls_mobileNumber));
            doQrMerchantPaymentMC.setPassword(xsdObjectFactory.createDoQrMerchantPaymentRequestPassword(ls_password));
            doQrMerchantPaymentMC.setPurOfTransaction(xsdObjectFactory.createDoQrMerchantPaymentRequestPurOfTransaction(ls_purOfTransaction));
            doQrMerchantPaymentMC.setQrString(xsdObjectFactory.createDoQrMerchantPaymentRequestQrString(ls_qrString));
            doQrMerchantPaymentMC.setRefLabel(xsdObjectFactory.createDoQrMerchantPaymentRequestRefLabel(ls_refLabel));
            doQrMerchantPaymentMC.setRemarks(xsdObjectFactory.createDoQrMerchantPaymentRequestRemarks(ls_remarks));
            doQrMerchantPaymentMC.setSourceCard(xsdObjectFactory.createDoQrMerchantPaymentRequestSourceCard(ls_sourceCard));
            doQrMerchantPaymentMC.setSourceCardExpiry(xsdObjectFactory.createDoQrMerchantPaymentRequestSourceCardExpiry(ls_sourceCardExpiry));
            doQrMerchantPaymentMC.setSourceName(xsdObjectFactory.createDoQrMerchantPaymentRequestSourceName(ls_sourceName));
            doQrMerchantPaymentMC.setStoreLabel(xsdObjectFactory.createDoQrMerchantPaymentRequestStoreLabel(ls_storeLabel));
            doQrMerchantPaymentMC.setTerLabel(xsdObjectFactory.createDoQrMerchantPaymentRequestTerLabel(ls_terLabel));
            doQrMerchantPaymentMC.setTipAmount(xsdObjectFactory.createDoQrMerchantPaymentRequestTipAmount(Double.valueOf(ls_tipAmount)));
            doQrMerchantPaymentMC.setTranAmount(xsdObjectFactory.createDoQrMerchantPaymentRequestTranAmount(Double.valueOf(ls_tranAmount)));
            doQrMerchantPaymentMC.setTranCurrency(xsdObjectFactory.createDoQrMerchantPaymentRequestTranCurrency(ls_tranCurrency));
            doQrMerchantPaymentMC.setTransId(xsdObjectFactory.createDoQrMerchantPaymentRequestTransId(ls_transId));
            doQrMerchantPaymentMC.setUsername(xsdObjectFactory.createDoQrMerchantPaymentRequestUsername(ls_userName));

            //get the object factory object of axis2.
            org.apache.ws.axis2.ObjectFactory axis2ObjectFactory = new org.apache.ws.axis2.ObjectFactory();
            //wrapping the request object in request.
            org.apache.ws.axis2.DoQrMerchantPaymentMC doQrMerchantPaymentMc = axis2ObjectFactory.createDoQrMerchantPaymentMC();
            doQrMerchantPaymentMc.setRequest(axis2ObjectFactory.createDoQrMerchantPaymentMCRequest(doQrMerchantPaymentMC));

            //created response object.
            DoQrMerchantPaymentMCResponse doQrMerchantPaymentMcResponse = null;
            try {
                doQrMerchantPaymentMcResponse = (DoQrMerchantPaymentMCResponse) soapConnector.callWebService(doQrMerchantPaymentMc);
            } catch (SoapFaultClientException soapException) {
                actualErrMsg = soapException.getFaultStringOrReason();
                PrintErrLog("DoQrMerchantPaymentMC SoapFaultClientException : " + actualErrMsg);
                ls_responseData = common.ofGetErrDataJsonArray("999", "Alert", "Currently Service under maintenance so please try later (ENP040)", actualErrMsg, "", "0", "R");
                return ls_responseData;
            }
            //created response object.
            DoQrMerchantPaymentResponse xsd_doQrMerchantPaymentResponse = doQrMerchantPaymentMcResponse.getReturn().getValue();

            //getting all the data from the response.
            ls_responseCode = xsd_doQrMerchantPaymentResponse.getResponseCode().getValue();
            ls_responseMessage = xsd_doQrMerchantPaymentResponse.getResponseMessage().getValue();
            ls_transactionDateTime = xsd_doQrMerchantPaymentResponse.getTransactionDateTime().getValue();
            ls_transactionRefNumber = xsd_doQrMerchantPaymentResponse.getTransactionRefNumber().getValue();

            /* if response is 100 then success.
	     * If response is 101 then no records found.
             */
            if (ls_responseCode != null && "100".equals(ls_responseCode)) {

                JSONObjectImpl QrMerchantPaymentOnMCJsonObj = new JSONObjectImpl();
                JSONObject responseJsonObject = new JSONObject();
                JSONArray QrMerchantPaymentOnMCJsonArray = new JSONArray();

                QrMerchantPaymentOnMCJsonObj.put("RESPONSECODE", ls_responseCode);
                QrMerchantPaymentOnMCJsonObj.put("RESPONSEMESSAGE", ls_responseMessage);
                QrMerchantPaymentOnMCJsonObj.put("TRANSACTIONDATETIME", ls_transactionDateTime);
                QrMerchantPaymentOnMCJsonObj.put("TRANSACTIONREFNUMBER", ls_transactionRefNumber);

                QrMerchantPaymentOnMCJsonArray.put(QrMerchantPaymentOnMCJsonObj);

                /* If response is 100 and API is called successfully. */
                responseJsonObject.put("STATUS", "0");
                responseJsonObject.put("COLOR", "G");
                responseJsonObject.put("RESPONSE", QrMerchantPaymentOnMCJsonArray);
                responseJsonObject.put("MESSAGE", "");
                ls_responseData = responseJsonObject.toString();

            } else { //other than 100,101.			
                ls_responseData = common.ofGetErrDataJsonArray("999", "Alert", "Currently Service under maintenance so please try later (ENP036).", ls_responseMessage, "", ls_responseCode, "R");
            }
        } catch (Exception err) {
            actualErrMsg = common.ofGetTotalErrString(err, "");
            PrintErrLog("DoQrMerchantPaymentMC Exception : " + actualErrMsg);
            ls_responseData = common.ofGetErrDataJsonArray("999", "Alert", "Currently Service under maintenance so please try later (ENP043)", actualErrMsg, "", "0", "R");
            return ls_responseData;
        }
        return ls_responseData;
    }
}
