package com.pw.resumecameldemo.route;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.pw.resumecameldemo.bean.MyException;


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

        onException(MyException.class)
                    .log("MyException: ${exception.message}")
                    .handled(true)
                    .maximumRedeliveries(2)
                    .redeliveryDelay(1000)
                    .backOffMultiplier(10)
                    .retryAttemptedLogLevel(LoggingLevel.INFO)
                    .end();

        from("direct:file-exception")
            .throwException(new RuntimeException()); // <----- Make this a gateway exception to get caught by OnException Handler no retry

        from("direct:file-retry")
            .throwException(new RuntimeException()); // <----- Make this a gateway exception to get caught by OnException Handler for retry

        templatedRoute("fileRouteTemplate1")
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
