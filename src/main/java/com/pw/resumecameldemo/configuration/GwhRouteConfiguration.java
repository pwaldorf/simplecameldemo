package com.pw.resumecameldemo.configuration;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteConfigurationBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.pw.resumecameldemo.route.FileResumeUpdateRoutePolicy;

@Component
public class GwhRouteConfiguration extends RouteConfigurationBuilder {

    @Autowired
    FileResumeUpdateRoutePolicy fileResumeUpdateRoutePolicy;

    @Value("${gwh.route.resumeprocess.enabled:true}")
    private String enabled;

    @Override
    public void configuration() throws Exception {

        routeConfiguration()
            .precondition(enabled)
            .id("resumeProcess")
            .interceptSendToEndpoint("^(bean:gwhMqSender).*")            
            .skipSendToOriginalEndpoint()
            .choice()
                .when(method(fileResumeUpdateRoutePolicy, "skipRecord"))
                    .log(LoggingLevel.INFO, "Skipping Resume Record: ${body}")
                .when(simple("${exchangeProperty.CamelDuplicateMessage} == 'true'"))
                    .log(LoggingLevel.INFO, "Skipping Duplicate Record: ${body}")
                .otherwise()
                    .log(LoggingLevel.INFO,"Processing Record: ${body}");
            
    }
    
}
