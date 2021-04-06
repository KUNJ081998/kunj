package com.easynet.configuration;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

@Configuration
public class CommonConfiguration {

	/*
	 * @Bean public PropertySourcesPlaceholderConfigurer
	 * propertySourcesPlaceholderConfigurer() { PropertySourcesPlaceholderConfigurer
	 * propertySourcesPlaceholderConfigurer = new
	 * PropertySourcesPlaceholderConfigurer();
	 * propertySourcesPlaceholderConfigurer.setLocations(new
	 * ClassPathResource("messages_bn.properties"), new
	 * ClassPathResource("messages_en.properties"), new
	 * ClassPathResource("messages.properties"), new
	 * ClassPathResource("responsecode_mapping.properties"));
	 * 
	 * propertySourcesPlaceholderConfigurer.setIgnoreUnresolvablePlaceholders(false)
	 * ; return propertySourcesPlaceholderConfigurer; }
	 */

	@Bean
	public MessageSource messageSource() {
					
		ReloadableResourceBundleMessageSource messageSource
		= new ReloadableResourceBundleMessageSource();

		messageSource.setBasename("classpath:messages");
		messageSource.setDefaultEncoding("UTF-8");
		messageSource.setCacheSeconds(60);
		return messageSource;
	}
}
