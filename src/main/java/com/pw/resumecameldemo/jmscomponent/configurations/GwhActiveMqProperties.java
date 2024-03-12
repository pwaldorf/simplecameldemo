package com.pw.resumecameldemo.jmscomponent.configurations;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "gwh.activemqtest.component")
public class GwhActiveMqProperties {
        
    /**
     * JMS broker url      
     */
    private String brokerUrl;

    /**
     * The Username to be used to autenticate with the JMS Broker
     */
    private String username;    

    /**
     * The Password to be used to autenticate with the JMS Broker
     */
    private String password;

    /**
     * JMS Connection factory Cache Size
     */
    private int sessionCacheSize;
   

}
