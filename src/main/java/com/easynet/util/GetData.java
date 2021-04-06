/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easynet.util;

import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.easynet.bean.GetRequestUniqueData;
import com.easynet.configuration.PropConfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.easynet.impl.LoggerImpl;

/**
 *
 * @author Sagar Umate
 * @date-12/01/2020 This class ate used to get data from database.
 */

@Repository
public class GetData {

	@Autowired
	GetRequestUniqueData getRequestUniqueData;

	@Autowired
	PropConfiguration propConfiguration;

	static Logger logger=LoggerFactory.getLogger(GetData.class);

	public String ofGetResponseData(String input) {
		String ls_return = null;
		String ls_req_flag = null;
		String ls_req_type = null;
		Connection con = null;
		CallableStatement cs = null;
		String actualErrMsg="";
		LoggerImpl loggerImpl=null;

		try {
			loggerImpl=new LoggerImpl();

			if (input.trim().substring(0, 1).equals("[")) {
				input = (String) input.substring(1, input.length() - 1);
			} else {
				input = (String) input;
			}

			JSONObject Jobj = new JSONObject(input);
			ls_req_type = Jobj.get("ACTION").toString();
			ls_req_flag = "R";

			con = Connectiondb.Getconnection();            
			cs=con.prepareCall("{CALL PACK_MOB_PROCESS.PROC_MOB_TRN(?,?,?,?,?,?)}");            
			cs.setString(1, ls_req_type);
			cs.setString(2, ls_req_flag);            
			cs.setString(3, input);
			cs.setString(4, "S");
			cs.registerOutParameter(5, Types.VARCHAR);
			cs.registerOutParameter(6, Types.CLOB);
			// execute stored procedure
			cs.execute();

			String ls_outputTo=cs.getString(5);
			Clob clob_data = cs.getClob(6); 
			if(clob_data==null) {
				ls_return=common.ofGetErrDataJsonObject("99",
						propConfiguration.getMessageOfResCode("commen.title.99", "Alert."),
						propConfiguration.getMessageOfResCode("commen.exception.999","","(ENP039)"),
						"Null response get from procedure PACK_MOB_PROCESS.PROC_MOB_TRN.",
						"Currently Service under maintenance so please try later (ENP039).","0","R");
			}else{
				ls_return = clob_data.getSubString(1, (int) clob_data.length());
			}

		} catch (SQLException ex) {
			actualErrMsg=common.ofGetTotalErrString(ex,"");          
			loggerImpl.error(logger,"SQLException : " + actualErrMsg,"IN:ofGetResponseData");

			ls_return=common.ofGetErrDataJsonObject("999",
					propConfiguration.getMessageOfResCode("commen.title.999", "Alert."),
					propConfiguration.getMessageOfResCode("commen.exception.999","","(ENP002)"),
					ex.getMessage(),"Currently Service under maintenance so please try later (ENP002).","0","R");   

		} catch (Exception ex) {
			actualErrMsg=common.ofGetTotalErrString(ex,"");
			loggerImpl.error(logger,"Exception : " + actualErrMsg,"IN:ofGetResponseData");

			ls_return=common.ofGetErrDataJsonObject("999",
					propConfiguration.getMessageOfResCode("commen.title.999", "Alert."),
					propConfiguration.getMessageOfResCode("commen.exception.999","","(ENP001)"),
					ex.getMessage(),"Currently Service under maintenance so please try later (ENP001).","0","R");            
		} finally {
			//It's important to close the statement when you are done with
			if (cs != null) {
				try {
					cs.close();
				} catch (SQLException e) {
					/* ignored */
				}
			}

			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					/* ignored */
				}
			}
		}
		return ls_return;
	}

	public String ofGetResponseData(String as_proc_name ,String input) {
		String ls_return = null;       
		Connection con = null;
		CallableStatement cs = null;
		String actualErrMsg="";
		LoggerImpl loggerImpl=null;

		try {
			loggerImpl=new LoggerImpl();

			if (input.trim().substring(0, 1).equals("[")) {
				input = (String) input.substring(1, input.length() - 1);
			} else {
				input = (String) input;
			}

			con = Connectiondb.Getconnection();            
			cs=con.prepareCall("{CALL "+as_proc_name+"(?,?)}");                      
			cs.setString(1, input);                        
			cs.registerOutParameter(2, Types.CLOB);
			// execute stored procedure
			cs.execute();

			Clob clob_data = cs.getClob(2); 
			if(clob_data==null) {
				ls_return=common.ofGetErrDataJsonObject("99",
						propConfiguration.getMessageOfResCode("commen.title.99", "Alert."),
						propConfiguration.getMessageOfResCode("commen.exception.999","","(ENP039)"),
						"Null response get from procedure "+as_proc_name+".",
						"Currently Service under maintenance so please try later (ENP039).","0","R");
			}else{
				ls_return = clob_data.getSubString(1, (int) clob_data.length());
			}

		} catch (SQLException ex) {
			actualErrMsg=common.ofGetTotalErrString(ex,"");          
			loggerImpl.error(logger,"SQLException : " + actualErrMsg,"IN:ofGetResponseData");
			
			ls_return=common.ofGetErrDataJsonObject("999",
					propConfiguration.getMessageOfResCode("commen.title.999", "Alert."),
					propConfiguration.getMessageOfResCode("commen.exception.999","","(ENP094)"),
					ex.getMessage(),"Currently Service under maintenance so please try later (ENP094).","0","R");
			
		} catch (Exception ex) {
			actualErrMsg=common.ofGetTotalErrString(ex,"");
			loggerImpl.error(logger,"Exception : " + actualErrMsg,"IN:ofGetResponseData");
			
			ls_return=common.ofGetErrDataJsonObject("999",
					propConfiguration.getMessageOfResCode("commen.title.999", "Alert."),
					propConfiguration.getMessageOfResCode("commen.exception.999","","(ENP095)"),
					ex.getMessage(),"Currently Service under maintenance so please try later (ENP095).","0","R");            
		} finally {
			//It's important to close the statement when you are done with
			if (cs != null) {
				try {
					cs.close();
				} catch (SQLException e) {
					/* ignored */
				}
			}

			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					/* ignored */
				}
			}
		}
		return ls_return;
	}
}
