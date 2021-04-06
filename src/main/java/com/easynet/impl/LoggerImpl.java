package com.easynet.impl;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.slf4j.profiler.Profiler;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.Environment;

/**
 * The custom logging logic write in this class. Main purpose of writing this
 * class is to generate the all logs from one place. Only The required method of
 * logger class are implemented in this class.
 * 
 * @author Sagar Umate
 * @Date 10/03/2021
 */

//@Scope(value="prototype",proxyMode = ScopedProxyMode.TARGET_CLASS)
public class LoggerImpl {

	private Profiler profiler = null;
	static Environment environment;

	// @Value("${PER_MON_LOG_GEN}")
	static private String PER_MON_LOG_GEN = "";
	static private String LOG_WRITE = "";

	/* set the initial values */
	static {
		try {
			AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
			environment = ctx.getEnvironment();
			PER_MON_LOG_GEN = environment.getProperty("PER_MON_LOG_GEN");
			LOG_WRITE = environment.getProperty("LOG_WRITE");
		} catch (Exception exception) {
			exception.printStackTrace();
		}

	}

	/**
	 * This is customized trace method used for write logs.
	 * 
	 * @param logger -name of logger from which print the logs.
	 * @param msg    -msg to print
	 * @param object -multiple argument object. 1.API Name. 2.object for print.
	 *               3.Replace the data of msg.
	 * @return nothing to return only print msg
	 * @exception if exception generated then print the logs on console.
	 *
	 */
	public void trace(Logger logger, String msg, Object... object) {
		String apiName = "";
		String msgStr="";
		try {
			if ("Y".equalsIgnoreCase(LOG_WRITE)) {
				apiName = String.valueOf(object[0]);
				Marker apiNameMarker = MarkerFactory.getMarker(apiName);
				msgStr=getJsonFormatStr(msg);
				logger.trace(apiNameMarker, msgStr);
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	/**
	 * This is customized debug method used for write logs.
	 * 
	 * @param logger -name of logger from which print the logs.
	 * @param msg    -msg to print
	 * @param object -multiple argument object. 1.API Name. 2.object for print.
	 *               3.Replace the data of msg.
	 * @return nothing to return only print msg
	 * @exception if exception generated then print the logs on console.
	 *
	 */
	public void debug(Logger logger, String msg, Object... object) {
		String apiName = "";
		String msgStr="";

		try {
			if ("Y".equalsIgnoreCase(LOG_WRITE)) {
				apiName = String.valueOf(object[0]);
				Marker apiNameMarker = MarkerFactory.getMarker(apiName);
				msgStr=getJsonFormatStr(msg);
				logger.debug(apiNameMarker, msgStr);
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	/**
	 * This is customized info method used for write logs.
	 * 
	 * @param logger -name of logger from which print the logs.
	 * @param msg    -msg to print
	 * @param object -multiple argument object. 1.API Name. 2.object for print.
	 *               3.Replace the data of msg.
	 * @return nothing to return only print msg
	 * @exception if exception generated then print the logs on console.
	 *
	 */
	public void info(Logger logger, String msg, Object... object) {
		String apiName = "";
		String msgStr="";
		try {
			if ("Y".equalsIgnoreCase(LOG_WRITE)) {
				apiName = String.valueOf(object[0]);
				Marker apiNameMarker = MarkerFactory.getMarker(apiName);
				msgStr=getJsonFormatStr(msg);
				logger.info(apiNameMarker,msgStr);
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	/**
	 * This is customized error method used for write logs.
	 * 
	 * @param logger -name of logger from which print the logs.
	 * @param msg    -msg to print
	 * @param object -multiple argument object. 1.API Name. 2.object for print.
	 *               3.Replace the data of msg.
	 * @return nothing to return only print msg
	 * @exception if exception generated then print the logs on console.
	 *
	 */
	public void error(Logger logger, String msg, Object... object) {
		String apiName = "";
		String msgStr="";
		try {
			if ("Y".equalsIgnoreCase(LOG_WRITE)) {
				apiName = String.valueOf(object[0]);
				Marker apiNameMarker = MarkerFactory.getMarker(apiName);
				msgStr=getJsonFormatStr(msg);
				logger.error(apiNameMarker, msgStr);
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	/**
	 * This is customized printOptLogs method used for write performance logs.
	 * 
	 * @param logger -name of logger from which print the logs.
	 * @param msg    -msg to print
	 * @param object -multiple argument object. 1.API Name. 2.object for print.
	 *               3.Replace the data of msg.
	 * @return nothing to return only print msg
	 * @exception if exception generated then print the logs on console.
	 *
	 */
	public void printOptLogs(Logger logger, String msg, Object... object) {
		String apiName = "";
		String msgStr="";
		try {
			if ("Y".equalsIgnoreCase(LOG_WRITE)) {
				apiName = String.valueOf(object[0]);
				Marker apiNameMarker = MarkerFactory.getMarker(apiName);
				msgStr=getJsonFormatStr(msg);
				logger.debug(apiNameMarker, msgStr);
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	/**
	 * This is customized generateProfiler method used for generate the profiler.
	 * 
	 * @param aprofileName -name of profiler
	 * @return profile object if generated else null.
	 * @exception if exception generated then print the logs on console.
	 *
	 */
	public Profiler generateProfiler(String aprofileName) {

		try {
			if ("Y".equalsIgnoreCase(PER_MON_LOG_GEN)) {
				this.profiler = new Profiler(aprofileName);
				return this.profiler;
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return null;
	}

	/**
	 * This is customized startProfiler method used for start the profiler.
	 * 
	 * @param name -name of watcher
	 * @return true if generated else false.
	 * @exception if exception generated then print the logs on console.
	 *
	 */
	public boolean startProfiler(String name) {
		try {
			if ("Y".equalsIgnoreCase(PER_MON_LOG_GEN)) {
				this.profiler.start(name);
				return true;
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return false;
	}

	/**
	 * This is customized stopProfiler method used for stop the profiler.
	 * 
	 * @return true if stop else false.
	 * @exception if exception generated then print the logs on console.
	 *
	 */
	public boolean stopProfiler() {
		try {
			if ("Y".equalsIgnoreCase(PER_MON_LOG_GEN)) {
				this.profiler.stop();
				return true;
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return false;
	}

	/**
	 * This is customized getProfilerStr method used for get detail of profile in
	 * string format.
	 * 
	 * @return String if detail available else ""
	 * @exception if exception generated then print the logs on console.
	 *
	 */
	public String getProfilerStr() {
		try {
			if ("Y".equalsIgnoreCase(PER_MON_LOG_GEN)) {
				return this.profiler.toString();
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return "";
	}

	/**
	 * This is customized stopAndPrintOptLogs method. This method stop the
	 * profiler,get the profiler detail and print it using given logger with debug
	 * level
	 * 
	 * @exception if exception generated then print the logs on console.
	 *
	 */
	public void stopAndPrintOptLogs(Logger logger, String msg, Object... object) {
		String apiName = "";
		String profilerStr = "";
		String msgStr="";
		try {
			if ("Y".equalsIgnoreCase(PER_MON_LOG_GEN) && this.profiler != null) {
				stopProfiler();
				profilerStr = getProfilerStr();

				if ("Y".equalsIgnoreCase(LOG_WRITE)) {
					apiName = String.valueOf(object[0]);
					Marker apiNameMarker = MarkerFactory.getMarker(apiName);
					msgStr=getJsonFormatStr(msg);
					logger.debug(apiNameMarker, msgStr + "\n" + profilerStr);
				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	
	
	public String getJsonFormatStr(String inputStr) {
		String returnStr="";
		
		if(inputStr.contains("\"")) {			
			returnStr=inputStr.replace("\"", "\\\"");			
			return returnStr;
		}else{
			return inputStr;
		}
	}
	
	public String getStringJson(Object object) {
		JSONArray jSONArray = new JSONArray();

		String jsonStr = "";
		String changeStr = "";

		changeStr = jSONArray.put(new JSONArray(jsonStr).toString()).toString();
		changeStr = changeStr.trim().substring(2, changeStr.length() - 2);
		return changeStr;

		/*
		 * if(object instanceof String) { jsonStr=(String)object;
		 * if(jsonStr.trim().startsWith("{")) { changeStr=jSONArray.put(new
		 * JSONObject(jsonStr).toString()).toString();
		 * changeStr=changeStr.trim().substring(2,changeStr.length()- 2); return
		 * changeStr;
		 * 
		 * }else if(jsonStr.trim().startsWith("[")) { changeStr=jSONArray.put(new
		 * JSONArray(jsonStr).toString()).toString();
		 * changeStr=changeStr.trim().substring(2,changeStr.length()- 2); return
		 * changeStr;
		 * 
		 * } }
		 */

	//	return object.toString();
	}
}
