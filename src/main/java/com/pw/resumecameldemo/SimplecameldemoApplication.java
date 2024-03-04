package com.pw.resumecameldemo;

import org.apache.camel.component.file.FileConsumer;
import org.apache.camel.component.file.consumer.adapters.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
//@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
public class SimplecameldemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SimplecameldemoApplication.class, args);	
				
	}

}
