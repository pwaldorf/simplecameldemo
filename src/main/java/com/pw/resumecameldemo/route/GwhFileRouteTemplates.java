package com.pw.resumecameldemo.route;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GwhFileRouteTemplates extends RouteBuilder {


    @Value("${gwh.group.count:50}")
    private int groupCount;

    @Override
    public void configure() throws Exception {

        routeTemplate("fileRouteTemplate")
            .templateParameter("filecomponent", "ftp")
            .templateParameter("directid", "splitmqsend")        
            .templateParameter("user", "pwaldorf")
            .templateParameter("server", "192.168.6.2:21")
            .templateParameter("port", "21")
            .templateParameter("password", "XXXXXXXX")
            .templateParameter("filename", "test.txt")
            .templateParameter("delete", "true")
            .templateParameter("directid", "splitmqsend")  
            .templateParameter("splittoken", "\n")
            .templateParameter("schedule", "0/20 * * * * ?")             
            .templateParameter("routepolicy1", "cronScheduledRoutePolicy")
            .templateParameter("routepolicy2", "fileResumeRoutePolicy")            
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
                    .routePolicyRef("{{routepolicy1}},{{routepolicy2}}").noAutoStartup()                    
                    .idempotentConsumer(simple("${headers.CamelFileHost}-${headers.CamelFileName}-${headers.CamelFileLength}"))
                        .idempotentRepository("{{idempotentrepository}}")
                        .skipDuplicate(false)
                    .choice()
                    .when(simple("${exchangeProperty.CamelDuplicateMessage} == 'true'"))
                        .log(LoggingLevel.INFO, "Skipping Duplicate File: ${headers.CamelFileHost}-${headers.CamelFileName}-${headers.CamelFileLength}")
                    .otherwise()
                        .convertBodyTo(String.class)
                        .split().tokenize("{{splittoken}}", groupCount)
                            .streaming()
                            .stopOnException()
                        .to("direct:{{directid}}");                    

    }
    
}
