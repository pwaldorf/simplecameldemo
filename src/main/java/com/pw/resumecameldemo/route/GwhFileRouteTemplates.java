package com.pw.resumecameldemo.route;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.caffeine.processor.idempotent.CaffeineIdempotentRepository;
import org.apache.camel.routepolicy.quartz.CronScheduledRoutePolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GwhFileRouteTemplates extends RouteBuilder {

    @Autowired
    CaffeineIdempotentRepository fileIdempotentRepository;
    
    @Autowired
    CaffeineIdempotentRepository recordIdempotentRepository;

    @Autowired
    CronScheduledRoutePolicy cronScheduledRoutePolicy;

    @Autowired
    FileResumeRoutePolicy fileResumeRoutePolicy;

    @Value("${gwh.group.count:50}")
    private int groupCount;

    @Override
    public void configure() throws Exception {

        routeTemplate("fileRouteTemplate")
            .templateParameter("filecomponent", "ftp")
            .templateParameter("directid", "splitmqsend")        
            .templateParameter("url", "pwaldorf@192.168.6.2:21")
            .templateParameter("password", "Adriana77!")
            .templateParameter("filename", "test.txt")
            .templateParameter("delete", "true")
            .templateParameter("directid", "splitmqsend")  
            .templateParameter("splittoken", "\n")
            .templateParameter("schedule", "0/20 * * * * ?")        
            .from(new StringBuilder("{{filecomponent}}://{{url}}")
                            .append("?password={{password}}")
                            .append("&fileName={{filename}}")
                            .append("&delete={{delete}}")
                            .append("&scheduler=quartz")
                            .append("&scheduler.cron={{schedule}}")
                            .toString())                      
                    .routePolicy(cronScheduledRoutePolicy, fileResumeRoutePolicy).noAutoStartup()
                    .onCompletion().onCompleteOnly().bean(recordIdempotentRepository, "clear").end()                                    
                    .routeId("{{routeid}}")
                    .idempotentConsumer(simple("${headers.CamelFileName}"), fileIdempotentRepository).skipDuplicate(false)                
                    .choice()                    
                    .when(simple("${exchangeProperty.CamelDuplicateMessage} == 'true'"))
                        .log(LoggingLevel.INFO, "Duplicate File: ${headers.CamelFileName}")
                    .otherwise()            
                            .convertBodyTo(String.class)                        
                            .split().tokenize("{{splittoken}}", groupCount)
                                .streaming()
                                .stopOnException()                                        
                            .to("direct:{{directid}}")
                    .end();

    }
    
}
