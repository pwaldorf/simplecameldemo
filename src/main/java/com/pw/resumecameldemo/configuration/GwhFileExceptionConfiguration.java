package com.pw.resumecameldemo.configuration;

import java.io.IOException;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteConfigurationBuilder;
import org.springframework.stereotype.Component;

import com.pw.resumecameldemo.bean.MyException;

import jakarta.jms.MessageFormatException;

@Component
public class GwhFileExceptionConfiguration extends RouteConfigurationBuilder {

    @Override
    public void configuration() throws Exception {
        
        // Global Route Exception Handlers
        routeConfiguration()
            .errorHandler(springTransactionErrorHandler().maximumRedeliveries(0));

        routeConfiguration()
            .onException(MyException.class)
                    .log("MyException: ${exception.message}")
                    .handled(false)
                    .maximumRedeliveries(2)
                    .redeliveryDelay(1000)                
                    .backOffMultiplier(10)
                    .retryAttemptedLogLevel(LoggingLevel.INFO);

        routeConfiguration("ioException")
            .onException(IOException.class)
                    .log("IOException: ${exception.message}")
                    .handled(false)
                    .maximumRedeliveries(3)
                    .redeliveryDelay(5000)                
                    .backOffMultiplier(2)
                    .retryAttemptedLogLevel(LoggingLevel.INFO);

        routeConfiguration("jmsException")
            .onException(MessageFormatException.class)
                    .log("JMSException: ${exception.message}")
                    .handled(false)
                    .maximumRedeliveries(3)
                    .redeliveryDelay(500)
                    .backOffMultiplier(2)
                    .retryAttemptedLogLevel(LoggingLevel.INFO);
                    

    }
    
}
