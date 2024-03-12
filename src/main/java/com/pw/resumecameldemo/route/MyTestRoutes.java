package com.pw.resumecameldemo.route;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;


@Component
public class MyTestRoutes extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        onException(Exception.class)
                .handled(true)
                .log("Exception: ${exception.message}")
                .to("file:{{output.dir}}?fileName=summary2.txt&fileExist=Append&appendChars=\n");

        templatedRoute("fileRouteTemplate")
            .routeId("largeFileRoute")
            .parameter("filename", "test.txt")
            .parameter("directid", "splitmqsend");

        templatedRoute("splitmqsend")
            .routeId("splitmqsend")
            .parameter("mqcomponent", "gwhMqSender")
            .parameter("mqcomponentmethod", "send")
            .parameter("directid", "splitmqsend");

    }    
    
}
