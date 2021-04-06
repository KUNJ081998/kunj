/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easynet.util;

import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.springframework.core.io.ClassPathResource;
import org.w3c.dom.Document;
import java.io.File;
import java.net.URLDecoder;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.easynet.impl.LoggerImpl;

public class readXML {

    static File fXmlFile=null;// file object used for read xml file.
    static Logger logger=LoggerFactory.getLogger(readXML.class);
    
    public static String getXmlData(String argu_tag) {
        readXML 	xmlread = new readXML();
        LoggerImpl 	loggerImpl=null;
        String 		actualErrMsg="";        
        String xmlPath = null;
        
        loggerImpl=new LoggerImpl();
		try {
			xmlPath = new ClassPathResource(File.separatorChar + "URLDetail.xml").getFile().getAbsolutePath();
		} catch (IOException e) {
			xmlPath="";
			actualErrMsg = common.ofGetTotalErrString(e, "");
			loggerImpl.error(logger, actualErrMsg, "getXmlData");
		}
       
        String data = "";
        Document doc = null;
        
        try {
            if(fXmlFile==null)
            {
                 fXmlFile= new File(xmlPath);
            }
            
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();

            String[] level_value =argu_tag.split(">");
            int level_cnt = level_value.length;

            //get inilial 1st tag value.            
            NodeList nList = doc.getElementsByTagName(level_value[0]);
            //read first item
            Node node = nList.item(0);
            boolean findChildNode = true, findSubNode = true;
            NodeList childNodeList = node.getChildNodes();
            int i = 0; //used for get child node values
            
            //start loop from 1 because we already get 0 position value
            for (int j = 1; j < level_cnt; j++) {
                i = 0;
                findChildNode = true;
                if (!findSubNode) {
                    break; //means do not check further node and exist                    
                }
                do {
                   ///check for node name mached or not.
                    if (childNodeList.item(i).getNodeName().equals(level_value[j])) {
                           
                        // check matched node has child
                        if (childNodeList.item(i).hasChildNodes()) {
                                
                            //reinitilized valribale for further check nodes.
                            childNodeList = childNodeList.item(i).getChildNodes();
                            findChildNode = false;
                                
                            //length =1 means no node after this value
                            if (childNodeList.getLength() == 1) {
                                findSubNode = false;// donot check further
                                data = childNodeList.item(0).getNodeValue();//get the node value
                            }
                        } else {
                            //if no child then return node value
                            data = childNodeList.item(i).getNodeValue();
                        }
                    } else {
                        if (i+1 >= childNodeList.getLength()) {
                            break; // if node check count greater than child node list then exit.
                        }         
                        i++;

                    }
                } while (findChildNode);
            }

             return data;
        } catch (SAXException ex) {
            actualErrMsg = common.ofGetTotalErrString(ex, "");
			loggerImpl.error(logger, actualErrMsg, "getXmlData");			
            data = "";
        } catch (IOException ex) {
        	actualErrMsg = common.ofGetTotalErrString(ex, "");
			loggerImpl.error(logger, actualErrMsg, "getXmlData");
            data = "";
        } catch (ParserConfigurationException ex) {
        	actualErrMsg = common.ofGetTotalErrString(ex, "");
			loggerImpl.error(logger, actualErrMsg, "getXmlData");
            data = "";
        }
        
        return data;
    }

    public String getPath() {
        String path = this.getClass().getClassLoader().getResource("").getPath();
        String fullPath = "";
        try {
            fullPath = URLDecoder.decode(path, "UTF-8");
        } catch (Exception ex) {
        }

        String pathArr[] = fullPath.split("/WEB-INF/classes/");
        fullPath = pathArr[0];
        // to read a file from webcontent
        return new File(fullPath).getPath();
    }      
}