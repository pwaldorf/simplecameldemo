package com.pw.resumecameldemo.route;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.caffeine.processor.idempotent.CaffeineIdempotentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GwhSplitMQRouteTemplate extends RouteBuilder {

    @Autowired
    FileResumeUpdateRoutePolicy fileResumeUpdateRoutePolicy;

    @Autowired
    CaffeineIdempotentRepository recordIdempotentRepository;

    @Override
    public void configure() throws Exception {
        
        routeTemplate("splitmqsend")
            .templateParameter("routeid", "largeFileRoute")
            .templateParameter("directid", "splitmqsend")
            .templateParameter("mqcomponent", "activeMqTestProducerTx")
            .templateParameter("queue")
            .templateParameter("transactedref", "txRequiredActiveMqTest")
            .templateParameter("splittoken", "\n")
            .from("direct:{{directid}}")
                    .routePolicy(fileResumeUpdateRoutePolicy)
                    .transacted("{{transactedref}}")                
                    .split().tokenize("{{splittoken}}")
                        .streaming()
                        .stopOnException()
                    .idempotentConsumer(simple("${body}"), recordIdempotentRepository).skipDuplicate(false)                
                    .choice()
                        .when().method(fileResumeUpdateRoutePolicy, "skipRecord")                                        
                            .log(LoggingLevel.INFO, "Skipping Record for Resume: ${body}")
                        .when(simple("${exchangeProperty.CamelDuplicateMessage} == 'true'"))
                            .log(LoggingLevel.INFO, "Duplicate Record: ${body}")
                        .otherwise()
                            .log(LoggingLevel.INFO, "New Record")                        
                            .bean(fileResumeUpdateRoutePolicy, "updateLineCount")
                            .to(new StringBuilder("{{mqcomponent}}:queue:")
                            .append("{{queue}}")
                            .toString())
                    .end();
    }
    
}
