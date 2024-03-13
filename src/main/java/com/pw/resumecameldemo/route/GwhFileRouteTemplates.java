package com.pw.resumecameldemo.route;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class GwhFileRouteTemplates extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        routeTemplate("fileRouteTemplate")
            .templateParameter("filecomponent", "ftp")
            .templateParameter("directid", "splitmqsend")        
            .templateParameter("user")
            .templateParameter("server")
            .templateParameter("port", "21")
            .templateParameter("password")
            .templateParameter("filename")
            .templateParameter("delete", "true")
            .templateParameter("groupcount", "500")
            .templateParameter("directid", "splitmqsend")  
            .templateParameter("splittoken", "\n")
            .templateParameter("schedule", "0/20 * * * * ?")            
            .templateParameter("cronroutepolicy", "cronScheduledRoutePolicy")
            .templateParameter("resumeroutepolicy", "fileResumeRoutePolicy")            
            .templateParameter("idempotentrepository", "fileIdempotentRepository")
            .from(new StringBuilder("{{filecomponent}}://")
                            .append("{{user}}@")
                            .append("{{server}}:")
                            .append("{{port}}")
                            .append("?password={{password}}")
                            .append("&fileName={{filename}}")
                            .append("&delete={{delete}}")
                            .append("&scheduler=quartz")
                            .append("&scheduler.cron={{schedule}}")
                            .toString())                                          
                    .routePolicyRef("{{cronroutepolicy}},{{resumeroutepolicy}}").noAutoStartup()                    
                    .idempotentConsumer(simple("${headers.CamelFileHost}-${headers.CamelFileName}-${headers.CamelFileLength}"))
                        .idempotentRepository("{{idempotentrepository}}")
                        .skipDuplicate(false)
                    .choice()
                    .when(simple("${exchangeProperty.CamelDuplicateMessage} == 'true'"))
                        .log(LoggingLevel.INFO, "Skipping Duplicate File: ${headers.CamelFileHost}-${headers.CamelFileName}-${headers.CamelFileLength}")
                    .otherwise()
                        .convertBodyTo(String.class)
                        .split().tokenize("{{splittoken}}", false, "{{groupcount}}" )
                            .streaming()
                            .stopOnException()
                        .to("direct:{{directid}}");                    

    }
    
}
