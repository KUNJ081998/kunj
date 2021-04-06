package com.easynet.util;

import java.sql.*;
import com.easynet.util.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.easynet.impl.LoggerImpl;

public class Connectiondb {
	
	static Logger logger=LoggerFactory.getLogger(Connectiondb.class);
	
    public static Connection Getconnection() {
        Connection conn = null;
        String url = common.getConfig("dburl");
        String username = common.getConfig("username");
        String password = common.getConfig("password");
        LoggerImpl loggerImpl=null;
        String actualErrMsg="";

        try {
        	loggerImpl=new LoggerImpl();
            Class.forName("oracle.jdbc.driver.OracleDriver");

            conn = DriverManager.getConnection(url, username, password);

            conn.setAutoCommit(false);
        } catch (ClassNotFoundException e) {
            actualErrMsg= common.ofGetTotalErrString(e, "");
            loggerImpl.error(logger, actualErrMsg, "IN:Getconnection");
            return null;
            
        } catch (SQLException e) {            
            actualErrMsg= common.ofGetTotalErrString(e, "");
            loggerImpl.error(logger, actualErrMsg, "IN:Getconnection");
            return null;
        }

        return conn;
    }
}

