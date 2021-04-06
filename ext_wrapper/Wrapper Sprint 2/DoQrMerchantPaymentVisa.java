package com.easynet.controller.ApiController;

import city.qr.xsd.DoQrMerchantPaymentRequest;
import city.qr.xsd.DoQrMerchantPaymentResponse;
import city.qr.xsd.ObjectFactory;
import com.easynet.configuration.SOAPConnector;
import com.easynet.impl.JSONObjectImpl;
import com.easynet.util.common;
import static com.easynet.util.common.PrintErrLog;
import com.easynet.util.readXML;
import org.apache.ws.axis2.DoQrMerchantPaymentVisaResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.soap.client.SoapFaultClientException;

@Component
public class DoQrMerchantPaymentVisa {

    @Autowired
    private SOAPConnector soapConnector;

    public String doQrMerchantPaymentVisa(String reqData) {

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
            DoQrMerchantPaymentRequest doQrMerchantPaymentVISA = xsdObjectFactory.createDoQrMerchantPaymentRequest();

            //setting all the values
            doQrMerchantPaymentVISA.setBillNumber(xsdObjectFactory.createDoQrMerchantPaymentRequestBillNumber(ls_billNumber));
            doQrMerchantPaymentVISA.setConAmount(xsdObjectFactory.createDoQrMerchantPaymentRequestConAmount(Double.valueOf(ls_conAmount)));
            doQrMerchantPaymentVISA.setConLabel(xsdObjectFactory.createDoQrMerchantPaymentRequestConLabel(ls_conLabel));
            doQrMerchantPaymentVISA.setConversionRate(xsdObjectFactory.createDoQrMerchantPaymentRequestConversionRate(ls_conversionRate));
            doQrMerchantPaymentVISA.setDestinationAmount(Double.valueOf(ls_destinationAmount));
            doQrMerchantPaymentVISA.setEmailAddress(xsdObjectFactory.createDoQrMerchantPaymentRequestEmailAddress(ls_emailAddress));
            doQrMerchantPaymentVISA.setFundingSource(xsdObjectFactory.createDoQrMerchantPaymentRequestFundingSource(ls_fundingSource));
            doQrMerchantPaymentVISA.setLoyaltyNumber(xsdObjectFactory.createDoQrMerchantPaymentRequestLoyaltyNumber(ls_loyaltyNumber));
            doQrMerchantPaymentVISA.setMCC(xsdObjectFactory.createDoQrMerchantPaymentRequestMCC(ls_mccString));
            doQrMerchantPaymentVISA.setMarkUpDestinationAmount(Double.valueOf(ls_markUpDestinationAmount));
            doQrMerchantPaymentVISA.setMarkUpPercentage(xsdObjectFactory.createDoQrMerchantPaymentRequestMarkUpPercentage(ls_markUpPercentage));
            doQrMerchantPaymentVISA.setMerAccInfo(xsdObjectFactory.createDoQrMerchantPaymentRequestMerAccInfo(ls_merAccInfo));
            doQrMerchantPaymentVISA.setMerCity(xsdObjectFactory.createDoQrMerchantPaymentRequestMerCity(ls_merCity));
            doQrMerchantPaymentVISA.setMerCityAltLang(xsdObjectFactory.createDoQrMerchantPaymentRequestMerCityAltLang(ls_merCityAltLang));
            doQrMerchantPaymentVISA.setMerCountryCode(xsdObjectFactory.createDoQrMerchantPaymentRequestMerCountryCode(ls_merCountryCode));
            doQrMerchantPaymentVISA.setMerIdType(xsdObjectFactory.createDoQrMerchantPaymentRequestMerIdType(ls_merIdType));
            doQrMerchantPaymentVISA.setMerName(xsdObjectFactory.createDoQrMerchantPaymentRequestMerName(ls_merName));
            doQrMerchantPaymentVISA.setMerNameAltLang(xsdObjectFactory.createDoQrMerchantPaymentRequestMerNameAltLang(ls_merNameAltLang));
            doQrMerchantPaymentVISA.setMerPostalCode(xsdObjectFactory.createDoQrMerchantPaymentRequestMerPostalCode(ls_merPostalCode));
            doQrMerchantPaymentVISA.setMobileNumber(xsdObjectFactory.createDoQrMerchantPaymentRequestMobileNumber(ls_mobileNumber));
            doQrMerchantPaymentVISA.setPassword(xsdObjectFactory.createDoQrMerchantPaymentRequestPassword(ls_password));
            doQrMerchantPaymentVISA.setPurOfTransaction(xsdObjectFactory.createDoQrMerchantPaymentRequestPurOfTransaction(ls_purOfTransaction));
            doQrMerchantPaymentVISA.setQrString(xsdObjectFactory.createDoQrMerchantPaymentRequestQrString(ls_qrString));
            doQrMerchantPaymentVISA.setRefLabel(xsdObjectFactory.createDoQrMerchantPaymentRequestRefLabel(ls_refLabel));
            doQrMerchantPaymentVISA.setRemarks(xsdObjectFactory.createDoQrMerchantPaymentRequestRemarks(ls_remarks));
            doQrMerchantPaymentVISA.setSourceCard(xsdObjectFactory.createDoQrMerchantPaymentRequestSourceCard(ls_sourceCard));
            doQrMerchantPaymentVISA.setSourceCardExpiry(xsdObjectFactory.createDoQrMerchantPaymentRequestSourceCardExpiry(ls_sourceCardExpiry));
            doQrMerchantPaymentVISA.setSourceName(xsdObjectFactory.createDoQrMerchantPaymentRequestSourceName(ls_sourceName));
            doQrMerchantPaymentVISA.setStoreLabel(xsdObjectFactory.createDoQrMerchantPaymentRequestStoreLabel(ls_storeLabel));
            doQrMerchantPaymentVISA.setTerLabel(xsdObjectFactory.createDoQrMerchantPaymentRequestTerLabel(ls_terLabel));
            doQrMerchantPaymentVISA.setTipAmount(xsdObjectFactory.createDoQrMerchantPaymentRequestTipAmount(Double.valueOf(ls_tipAmount)));
            doQrMerchantPaymentVISA.setTranAmount(xsdObjectFactory.createDoQrMerchantPaymentRequestTranAmount(Double.valueOf(ls_tranAmount)));
            doQrMerchantPaymentVISA.setTranCurrency(xsdObjectFactory.createDoQrMerchantPaymentRequestTranCurrency(ls_tranCurrency));
            doQrMerchantPaymentVISA.setTransId(xsdObjectFactory.createDoQrMerchantPaymentRequestTransId(ls_transId));
            doQrMerchantPaymentVISA.setUsername(xsdObjectFactory.createDoQrMerchantPaymentRequestUsername(ls_userName));

            //get the object factory object of axis2.
            org.apache.ws.axis2.ObjectFactory axis2ObjectFactory = new org.apache.ws.axis2.ObjectFactory();
            //wrapping the request object in request.
            org.apache.ws.axis2.DoQrMerchantPaymentVisa doqrmerchantpaymentVISA = axis2ObjectFactory.createDoQrMerchantPaymentVisa();
            doqrmerchantpaymentVISA.setRequest(axis2ObjectFactory.createDoQrMerchantPaymentVisaRequest(doQrMerchantPaymentVISA));

            //created response object.
            DoQrMerchantPaymentVisaResponse doQrMerchantPaymentVisaResponse = null;
            try {
                doQrMerchantPaymentVisaResponse = (DoQrMerchantPaymentVisaResponse) soapConnector.callWebService(doqrmerchantpaymentVISA);
            } catch (SoapFaultClientException soapException) {
                actualErrMsg = soapException.getFaultStringOrReason();
                PrintErrLog("DoQrMerchantPaymentVisa SoapFaultClientException : " + actualErrMsg);
                ls_responseData = common.ofGetErrDataJsonArray("999", "Alert", "Currently Service under maintenance so please try later (ENP040)", actualErrMsg, "", "0", "R");
                return ls_responseData;
            }

            //created response object.
            DoQrMerchantPaymentResponse xsd_doQrMerchantPaymentResponse = doQrMerchantPaymentVisaResponse.getReturn().getValue();

            //getting all the data from the response.
            ls_responseCode = xsd_doQrMerchantPaymentResponse.getResponseCode().getValue();
            ls_responseMessage = xsd_doQrMerchantPaymentResponse.getResponseMessage().getValue();
            ls_transactionDateTime = xsd_doQrMerchantPaymentResponse.getTransactionDateTime().getValue();
            ls_transactionRefNumber = xsd_doQrMerchantPaymentResponse.getTransactionRefNumber().getValue();

            
            /* if response is 100 then success.
	     * If response is 101 then no records found.
             */
            if (ls_responseCode != null && "100".equals(ls_responseCode)) {

                JSONObjectImpl QrMerchantPaymentOnUsJsonObj = new JSONObjectImpl();
                JSONObject responseJsonObject = new JSONObject();
                JSONArray QrMerchantPaymentOnUsJsonArray = new JSONArray();

                QrMerchantPaymentOnUsJsonObj.put("RESPONSECODE", ls_responseCode);
                QrMerchantPaymentOnUsJsonObj.put("RESPONSEMESSAGE", ls_responseMessage);
                QrMerchantPaymentOnUsJsonObj.put("TRANSACTIONDATETIME", ls_transactionDateTime);
                QrMerchantPaymentOnUsJsonObj.put("TRANSACTIONREFNUMBER", ls_transactionRefNumber);

                QrMerchantPaymentOnUsJsonArray.put(QrMerchantPaymentOnUsJsonObj);

                /* If response is 100 and API is called successfully. */
                responseJsonObject.put("STATUS", "0");
                responseJsonObject.put("COLOR", "G");
                responseJsonObject.put("RESPONSE", QrMerchantPaymentOnUsJsonArray);
                responseJsonObject.put("MESSAGE", "");
                ls_responseData = responseJsonObject.toString();

            }else { //other than 100,101.			
                ls_responseData = common.ofGetErrDataJsonArray("999", "Alert", "Currently Service under maintenance so please try later (ENP036).", ls_responseMessage, "", ls_responseCode, "R");
            }

        }catch (Exception err) {
            actualErrMsg = common.ofGetTotalErrString(err, "");
            PrintErrLog("DoQrMerchantPaymentVisa Exception : " + actualErrMsg);
            ls_responseData = common.ofGetErrDataJsonArray("999", "Alert", "Currently Service under maintenance so please try later (ENP043)", actualErrMsg, "", "0", "R");
            return ls_responseData;
        }
        return ls_responseData;
    }
}
