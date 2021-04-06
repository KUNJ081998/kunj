package com.easynet.configuration;
import java.time.Duration;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.transport.http.HttpUrlConnectionMessageSender;

import com.easynet.util.readXML;

@Configuration
public class SoapConfig {

	@Value("${client.api.connectionTimeout:30}")
	private int connectionTimeout;

	@Value("${client.api.readTimeout:120}")
	private int readTimeout;

	/**
	 *This class generate the marshaller object and also bind all the generated class from
     	WSDL file.
	 * @return  Jaxb2Marshaller object
	 */
	@Bean
	public Jaxb2Marshaller marshaller() {
		Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();

		jaxb2Marshaller.setContextPaths(
				"java1.io.xsd:java1.security.xsd:card.xsd:fi.xsd:helper.xsd:org.apache.ws.axis2:org.w3c.dom.xsd:"
						+ "city.ababil.xsd:city.akash.xsd:city.amberit.xsd:city.bkash.xsd:city.cdm_otp.xsd:city.gp.xsd:city.ipay.xsd:city.ivac.xsd:city.kgdcl.xsd:city.mbm.xsd:"
						+ "city.movie.xsd:city.nsu.xsd:city.otherbank.xsd:city.paywell.xsd:city.qr.xsd:city.veefin.xsd:city.wallet.xsd:city.xsd:com.compassplus.schemas.two._1_0.fimi_types_xsd.xsd");

		try {
			jaxb2Marshaller.afterPropertiesSet();
		} catch (Exception ex) {
			throw new BeanCreationException(ex.getMessage(), ex);
		}
		return jaxb2Marshaller;
	}

	/**
     This method return the bean on SOAPConnector class.
	 * We set default SOAP URL in this method.
	 */
	@Bean
	public SOAPConnector soapConnector(Jaxb2Marshaller marshaller) {
		String ls_apiURL ="";

		ls_apiURL = readXML.getXmlData("root>FINACLE_API>API_URL");
		SOAPConnector soapConnector = new SOAPConnector();   		
		soapConnector.setDefaultUri(ls_apiURL);
		soapConnector.setMarshaller(marshaller);
		soapConnector.setUnmarshaller(marshaller);
		soapConnector.setMessageSender(httpUrlConnectionMessageSender());
		return soapConnector;
	}

	/*
	 * @Bean public WebServiceMessageSender webServiceMessageSender() {
	 * 
	 * System.out.println("connectionTimeout=>"+connectionTimeout);
	 * System.out.println("readTimeout=>"+readTimeout);
	 * 
	 * HttpWebServiceMessageSenderBuilder httpWebServiceMessageSenderBuilder=new
	 * HttpWebServiceMessageSenderBuilder();
	 * 
	 * // timeout for creating a connection
	 * httpWebServiceMessageSenderBuilder.setConnectTimeout(Duration.ofSeconds(
	 * connectionTimeout));
	 * 
	 * //Wait for response upto given time period.
	 * httpWebServiceMessageSenderBuilder.setReadTimeout(Duration.ofSeconds(
	 * readTimeout));
	 * 
	 * return httpWebServiceMessageSenderBuilder.build(); }
	 */

	@Bean
	HttpUrlConnectionMessageSender httpUrlConnectionMessageSender() {
		HttpUrlConnectionMessageSender httpUrlConnectionMessageSender = new HttpUrlConnectionMessageSender();

		httpUrlConnectionMessageSender.setReadTimeout(Duration.ofSeconds(readTimeout));
		httpUrlConnectionMessageSender.setConnectionTimeout(Duration.ofSeconds(connectionTimeout));
		return httpUrlConnectionMessageSender;
	}
}
