/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easynet.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;

import javax.servlet.http.HttpSession;

import org.json.JSONObject;

/**
 *
 * @author HP
 */
public class apiCall {

    
    public static String GetApi(String apiUrl,HttpSession session) {
        String responseString = "";
        String outputString = "";

        try {
            URL url = new URL(apiUrl);

            URLConnection connection = url.openConnection();
            HttpURLConnection httpConn = (HttpURLConnection) connection;
            httpConn.setRequestMethod("GET");
            httpConn.setConnectTimeout(60000); // set timeout 1 min
            httpConn.setDoOutput(true);

            InputStreamReader isr = new InputStreamReader(httpConn.getInputStream());
            BufferedReader in = new BufferedReader(isr);

            // Write the SOAP message response to a String.
            while ((responseString = in.readLine()) != null) {
                outputString = outputString + responseString;
            }
        } catch (SocketTimeoutException ex) {
            outputString = "[{\"STATUS\":\"99\",\"MESSAGE\":\"We are sorry for the inconvenience that happened \",\"RESPONSE\":\"" + ex.getMessage() + "\"}]";
        } catch (IOException ex) {
            outputString = "[{\"STATUS\":\"99\",\"MESSAGE\":\"We are sorry for the inconvenience that happened \",\"RESPONSE\":\"" + ex.getMessage() + "\"}]";
        }
        return outputString;
    }

    /**
     * ** POS
     *
     * @param apiUrl
     * @param apiParameter
     *
     * @return **
     */
    
    public static String PostApi(String apiUrl, String apiParameter,HttpSession session) {
        String responseString = "";
        String outputString = "";
        String ls_checksum = "";
        try {

            ls_checksum = "";

            URL url = new URL(apiUrl);

            URLConnection connection = url.openConnection();
            HttpURLConnection httpConn = (HttpURLConnection) connection;
            
            ByteArrayOutputStream bout = new ByteArrayOutputStream();

            String jsonInput = apiParameter;

            byte[] buffer = new byte[jsonInput.length()];
            buffer = jsonInput.getBytes();
            bout.write(buffer);
            byte[] b = bout.toByteArray();

            //add reuqest header
            httpConn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            httpConn.setRequestProperty("checksum", ls_checksum);
            httpConn.setConnectTimeout(60000); // set timeout 1 min
            httpConn.setRequestMethod("POST");
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

            if (outputString.trim().substring(0, 1).equals("[")) {
                outputString = (String) outputString.substring(1, outputString.length() - 1);
            } else {
                outputString = (String) outputString;
            }

        } catch (SocketTimeoutException ex) {
            outputString = "[{\"STATUS\":\"99\",\"MESSAGE\":\"We are sorry for the inconvenience that happened \",\"RESPONSE\":\"" + ex.getMessage() + "\"}]";
        } catch (IOException ex) {
            outputString = "[{\"STATUS\":\"99\",\"MESSAGE\":\"We are sorry for the inconvenience that happened \",\"RESPONSE\":\"" + ex.getMessage() + "\"}]";
        }
        if (readXML.getXmlData("logwrite").equalsIgnoreCase("Y")) {
            System.out.println("==================================================");
            System.out.println("Url : " + apiUrl);
            System.out.println("Parameter : " + apiParameter);
            System.out.println("outputString : " + outputString);
            System.out.println("==================================================");
        }
        return outputString;
    }

    
    /*
    Google Captcha Verify API
     */
    public static boolean CatchaValidationRecaptchaCheck(HttpSession session, String argument)  {
        
        try {
            if (readXML.getXmlData("captcha_cd").equalsIgnoreCase("CC")) {
                //System.out.println("captchakey session  : " + session.getAttribute("captchakey").toString());
                //System.out.println("captchakey session  : " + AESEncryption.decryptText(session.getAttribute("captchakey").toString()));
                //System.out.println("argument session    : " + argument);
                //System.out.println("==================================================");
                if (AESEncryption.decryptText(session.getAttribute("captchakey").toString()).equals(argument)) {
                    return true;
                }
                //return true;
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
                    String jsonInput = "secret=" + secret + "&response=" + argument;
                    byte[] buffer = new byte[jsonInput.length()];
                    buffer = jsonInput.getBytes();
                    bout.write(buffer);
                    byte[] b = bout.toByteArray();

                    //add reuqest header
                    httpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
                    httpConn.setConnectTimeout(60000); // set timeout 1 min
                    httpConn.setRequestMethod("GET");
                    httpConn.setDoOutput(true);
                    httpConn.setDoInput(true);

                    try (OutputStream out_strm = httpConn.getOutputStream()) {
                        out_strm.write(b);
                    }
                    
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
        /*Temparary By Pass Captcha*/
        //return true;
        return false;
    }
    
    
    
}
