package com.easynet.util;

/**
 *
 * @author 
 */
public class URLGenerator {

    public static final String WRITE_LOG = readXML.getXmlData("logwrite");
    
    private static final String MAIN_URL = readXML.getXmlData("main_url");
    
    //TOMCAT SERVER
    /*============== Server ==============*/
    public static final String URL_TOKEN = MAIN_URL + "/api_integ/auth/GenerateToken";
    public static final String URL_REFRESH_TOKEN = MAIN_URL + "/api_integ/auth/token";
    public static final String URL = MAIN_URL + "/easynetpro-serv/";
    public static final String EASY_BOT_URL = MAIN_URL + "/easybot/";
    /*************/
    public static final String USER_LOGIN = URL + "login_req";
    public static final String USER_REGISTER = URL + "reg_req";
    public static final String RESET_PASSWORD = URL + "rst_psw_req";
    public static final String USER_LOGOUT = URL + "logout_req";
    public static final String EASYBOT_REQ = EASY_BOT_URL + "request";
    public static final String REQUEST = URL + "request";
    public static final String BBPS_REQUEST = URL + "bbps-request";
    public static final String IMPS_REQUEST = URL + "imps-request";
}

