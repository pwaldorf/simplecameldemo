package com.pw.resumecameldemo.configuration;

import java.io.IOException;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteConfigurationBuilder;
import org.springframework.stereotype.Component;

import com.pw.resumecameldemo.bean.MyCustomException;

import jakarta.jms.JMSException;


@Component
public class GwhFileExceptionConfiguration extends RouteConfigurationBuilder {

    @Override
    public void configuration() throws Exception {

        // Global Route Exception Handlers
        routeConfiguration()
            //.errorHandler(springTransactionErrorHandler().maximumRedeliveries(0));
            .errorHandler(defaultErrorHandler().maximumRedeliveries(0));

        // One and Only Global Exception Handler
        routeConfiguration("customException")
            .onException(MyCustomException.class)
                    .log("MyException: ${exception.message}")
                    .handled(true)
                    .maximumRedeliveries(2)
                    .redeliveryDelay(1000)
                    .backOffMultiplier(10)
                    .retryAttemptedLogLevel(LoggingLevel.INFO)
                    //.bean("gwhExceptionHandler", "handleException") // Create bean to integrate with GW exception handling
                    .rollback("Custom Error Message");

        routeConfiguration("ioException")
            .onException(IOException.class)
                    .log("IOException: ${exception.message}")
                    .handled(false)
                    .maximumRedeliveries(3)
                    .redeliveryDelay(5000)
                    .backOffMultiplier(2)
                    .retryAttemptedLogLevel(LoggingLevel.INFO);
                    //.bean("gwhExceptionHandler", "handleException") // Create bean to integrate with GW exception handling
                    //.rollback("ioexception error");


        routeConfiguration("jmsExceptionRetry")
                .onException(JMSException.class)
                    // can add onWhen to call bean to determine if should do retry based on returncode or other info
           //         .onWhen(method("gwhExceptionClassification", "isRetryable"))
                        .log("JMSException: ${exception.message}")
                        .handled(true)
                        .maximumRedeliveries(5)
                        .redeliveryDelay(500)
                        .backOffMultiplier(2)
                        .retryAttemptedLogLevel(LoggingLevel.INFO);
                        //.bean("gwhExceptionHandler", "handleException") // Create bean to integrate with GW exception handling
                        //.rollback("jms error");

        // routeConfiguration("jmsExceptionNoRetry")
        //         // setup for noretry for JMSException
        //         .onException(JMSException.class)
        //             .log("JMSException: ${exception.message}")
        //             .handled(true)
        //             .maximumRedeliveries(0);
        //             //.bean("gwhExceptionHandler", "handleException") // Create bean to integrate with GW exception handling
        //             //.rollback("jms error");

    }
}
