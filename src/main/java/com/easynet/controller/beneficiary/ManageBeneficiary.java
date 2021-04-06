package com.easynet.controller.beneficiary;

import com.easynet.util.Connectiondb;
import com.easynet.util.common; 

import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.apache.commons.validator.routines.EmailValidator;

/**
 * @author Mahendra Suthar This API will used for add benificiary.
 */
//used anotation Component for making class as sring bean.
@Component
public class ManageBeneficiary {	

	/*************************************************************************
	 * @date: -30-01-2020
	 * @author: Mahendra Suthar 
	 * @param: requestData - Call this method with request parameter for get Bank 
	 * @return: -This method return Json data of Bank Details. 
	 * 
	 ************************************************************************/
	public String funcBeneficiaryReq(String RequestType, String RequestData) {
		String ls_sql_call = null;
		String ls_benf_type= null;

		String ls_return = null;
		Connection con = null;
		CallableStatement cs = null;
		JSONObject jsonAction;
		JSONObject josnBenfRequestData;//store JSON card request
		//int jsonBenfLength=0;
		String ls_email_id= null;
		String ls_acct_no= null;

		try {

			if (RequestData.trim().substring(0, 1).equals("[")) {
				RequestData = (String) RequestData.substring(1, RequestData.length() - 1);
			} else {
				RequestData = (String) RequestData;
			}


			if (RequestType.equalsIgnoreCase("GETBENFBANK")){
				ls_sql_call ="{ CALL PROC_GET_BANK_DETAIL(?,?) }";

			}else if (RequestType.equalsIgnoreCase("ADDBENF")){
				//8.1	Add City Bank Account as Beneficiary	37
				//8.2 a	Add other City Bank Account as Beneficiary	38
				//8.2 b	Add other City Bank Card as Beneficiary	38
				//8.3	Add any MFS Account as Beneficiary	39
				//8.4	Add Email as Beneficiary	

				if (RequestData.trim().substring(0, 1).equals("[")) {
					jsonAction=new JSONArray(RequestData).getJSONObject(0);
				}else if (RequestData.trim().substring(0, 1).equals("{")) {
					jsonAction=new JSONObject(RequestData);                  
				}else {
					jsonAction=new JSONObject(RequestData);
				}        	        	        
				ls_benf_type=jsonAction.getString("BENF_TYPE");	

				josnBenfRequestData = jsonAction.getJSONArray("BENE_LIST").getJSONObject(0);

				ls_acct_no  		= josnBenfRequestData.getString("TO_ACCT_NO");
				ls_email_id  		= josnBenfRequestData.getString("TO_EMAIL_ID");


				if (ls_benf_type.equalsIgnoreCase("I")) {
					//Add Code for Validate IFT Beneficiary 


				}else if (ls_benf_type.equalsIgnoreCase("C")) {
					//Add Code for Validate Card BIN 
				}else if (ls_benf_type.equalsIgnoreCase("E")) {
					// Email ID Validation

					if (! EmailValidator.getInstance().isValid(ls_email_id)) {
						//If invalid email id then call the code
						return common.ofGetErrDataJsonArray("99", "Alert", "Invalid Beneficiry Email ID.", "", "", "0", "R");
					}
				}							 			

				ls_sql_call ="{ CALL PACK_MANAGE_BENF.PROC_ADD_BENEFICIARY(?,?) }";	
			}else if (RequestType.equalsIgnoreCase("ADDBENFOTHR")){

				ls_sql_call ="{ CALL PACK_MANAGE_BENF.PROC_ADD_BENEFICIARY(?,?) }";	
			}else if (RequestType.equalsIgnoreCase("ADDBENFCARD")){

				ls_sql_call ="{ CALL PACK_MANAGE_BENF.PROC_ADD_BENEFICIARY(?,?) }";	
			}else if (RequestType.equalsIgnoreCase("ADDBENFMFS")){

				ls_sql_call ="{ CALL PACK_MANAGE_BENF.PROC_ADD_BENEFICIARY(?,?) }";	
			}else if (RequestType.equalsIgnoreCase("ADDBENFEMAIL")){

				ls_sql_call ="{ CALL PACK_MANAGE_BENF.PROC_ADD_BENEFICIARY(?,?) }";	
			}else if (RequestType.equalsIgnoreCase("VERIFYADDBENF")){
				ls_sql_call ="{ CALL PACK_MANAGE_BENF.PROC_ADD_BENEFICIARY_VERIFY(?,?) }";	
			}else if (RequestType.equalsIgnoreCase("GETBENF")){
				ls_sql_call ="{ CALL PACK_MANAGE_BENF.PROC_GET_BENEFICIARY(?,?) }";	
			}else if (RequestType.equalsIgnoreCase("DELETEBENF")){
				ls_sql_call ="{ CALL PACK_MANAGE_BENF.PROC_DELETE_BENEFICIARY(?,?) }";	
			}else {
				return "";
			}

			con = Connectiondb.Getconnection();
			cs = con.prepareCall(ls_sql_call);
			cs.setString(1, RequestData);
			cs.registerOutParameter(2, Types.CLOB);
			cs.execute(); // execute stored procedure

			Clob clob_data = cs.getClob(2);
			if (clob_data == null) {
				ls_return = common.ofGetErrDataJsonObject("99", "Alert",
						"Currently Service under maintenance so please try later (ENP066)",
						"Null response get from procedure.", "", "0", "R");
			} else {
				ls_return = clob_data.getSubString(1, (int) clob_data.length());
			}

		} catch (SQLException ex) {
			System.out.println(common.ofGetTotalErrString(ex, "SQLException :"));
			ls_return = common.ofGetErrDataJsonObject("99", "Alert",
					"Currently Service under maintenance so please try later (ENP067)",
					common.ofGetTotalErrString(ex, "SQLException :"), "", "0", "R");

		} catch (Exception ex) {
			System.out.println(common.ofGetTotalErrString(ex, "Exception :"));
			ls_return = common.ofGetErrDataJsonObject("99", "Alert",
					"Currently Service under maintenance so please try later (ENP068)",
					common.ofGetTotalErrString(ex, "SQLException :"), "", "0", "R");
		} finally {
			// It's important to close the statement when you are done with
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
