package com.pw.resumecameldemo.route;

import jakarta.jms.JMSException;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.spring.spi.TransactionErrorHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class MyTestRoutes extends RouteBuilder {

 
    @Value("${gwh.ftp.user}")
    private String user;

    @Value("${gwh.ftp.server}")
    private String server;

    @Value("${gwh.ftp.port}")
    private String port;

    @Value("${MYSQL_PASSWORD}")
    private String password;

    @Value("${gwh.ftp.filename}")
    private String filename;

    @Value("${gwh.ftp.groupcount}")
    private String groupcount;

    @Override
    public void configure() throws Exception {
        
        errorHandler(springTransactionErrorHandler()
            .maximumRedeliveries(5)
            .redeliveryDelay(5000)
            .backOffMultiplier(10)            
            .retryAttemptedLogLevel(LoggingLevel.WARN));

        onException(JMSException.class)
                .log("Exception: ${exception.message}");
                //shutdown route
                

        onException(Exception.class)
                .handled(true)
                .log("Exception: ${exception.message}")
                .to("file:{{output.dir}}?fileName=summary2.txt&fileExist=Append&appendChars=\n");

        templatedRoute("fileRouteTemplate")
            .routeId("largeFileRoute")
            .parameter("user", user)
            .parameter("server", server)
            .parameter("port", port)
            .parameter("password", password)
            .parameter("filename", filename)
            .parameter("groupcount", groupcount)            
            .parameter("directid", "splitmqsend");

        templatedRoute("splitmqsend")
            .routeId("splitmqsend")
            .parameter("mqcomponent", "gwhMqSender")
            .parameter("mqcomponentmethod", "send")
            .parameter("directid", "splitmqsend");

    }    
    
}
