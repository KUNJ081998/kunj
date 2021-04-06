package com;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.core.io.ClassPathResource;

import com.easynet.util.common;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class EasyNetProApiv5Application {
		
	static Logger logger=LoggerFactory.getLogger(common.class);
	static String keyName="";
	static String valueData="";
	
	public static void main(String[] args) {
		
		Properties prop = new Properties();
		InputStream in=null;
		
		try {		
			try { 
				
				in=new ClassPathResource("/application.properties").getInputStream();						
				prop.load(in);
			}catch(Exception err) { 
				err.printStackTrace();
				logger.error("Error generated at the time of getting application property file \n"+err. getMessage()); 
			}
			
			//set values of application.properties file for used values using Environment class . 			
			prop.forEach((k, v) -> { 
				keyName=(String)k; 
				valueData=(String)v;
				System.setProperty(keyName.trim(),valueData.trim()); 
			});
		}catch(Exception exception) {
			exception.printStackTrace();
		}finally {
			try {
				in.close();
			} catch (IOException e) {				
				e.printStackTrace();
			}
		}		
		
		SpringApplication.run(EasyNetProApiv5Application.class, args);		
	}

}
