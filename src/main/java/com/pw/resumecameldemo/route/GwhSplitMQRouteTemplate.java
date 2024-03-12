package com.pw.resumecameldemo.route;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;


@Component
public class GwhSplitMQRouteTemplate extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        routeTemplate("splitmqsend")            
            .templateParameter("directid", "splitmqsend")
            .templateParameter("mqcomponent", "gwhMqSender")
            .templateParameter("mqcomponentmethod", "send")
            .templateParameter("transactedref", "txRequiredActiveMqTest")
            .templateParameter("splittoken", "\n")
            .templateParameter("routepolicy", "fileResumeUpdateRoutePolicy")
            .templateParameter("routeconfiguration", "resumeProcess")
            .templateParameter("idempotentrepository", "recordIdempotentRepository")
            .templateParameter("resumeupdate", "fileResumeUpdateRoutePolicy")
            .templateParameter("resumeupdatemethod", "updateLineCount")
            .from("direct:{{directid}}")                    
                    .routePolicyRef("{{routepolicy}}")
                    .routeConfigurationId("{{routeconfiguration}}")
                    .transacted("{{transactedref}}")                
                    .split().tokenize("{{splittoken}}")
                        .streaming()
                        .stopOnException()
                    .idempotentConsumer(simple("${body}"))
                        .idempotentRepository("{{idempotentrepository}}")
                        .skipDuplicate(false)                    
                    .bean("{{resumeupdate}}", "{{resumeupdatemethod}}")                                                
                    .to("bean:{{mqcomponent}}?method={{mqcomponentmethod}}");
                    
    }
    
}
