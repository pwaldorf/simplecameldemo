package com.pw.resumecameldemo.route;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.EndpointConsumerBuilder;
import org.apache.camel.builder.endpoint.EndpointRouteBuilder;
import org.quartz.Scheduler;
import org.quartz.core.QuartzScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

//@Component
public class GwhFileRouteTemplate1 extends EndpointRouteBuilder {

    @Autowired
    EndpointConsumerBuilder endpointConsumerBuilder;


    @Override
    public void configure() throws Exception {

        routeTemplate("fileRouteTemplate1")
            .templateParameter("filecomponent", "ftp")
            .templateParameter("directid", "splitmqsend")
            .templateParameter("user")
            .templateParameter("server")
            .templateParameter("port", "21")
            .templateParameter("password")
            .templateParameter("filename")
            .templateParameter("groupcount", "500")
            .templateParameter("directid", "splitmqsend")
            .templateParameter("splittoken", "\n")
            .templateParameter("schedule", "0/20 * * * * ?")
            .templateParameter("exceptionhandler", "gwhFileExceptionHandler")
            .templateParameter("streamdownload", "true")
            .templateParameter("stepwise", "false")
            .templateParameter("idempotentrepository", "fileIdempotentRepository")
                .from(endpointConsumerBuilder)
                    .routePolicyRef("fileResumeRoutePolicy,cronScheduledRoutePolicy").noAutoStartup()
                    .streamCaching()
                    .idempotentConsumer(simple("${headers.CamelFileHost}-${headers.CamelFileName}-${headers.CamelFileLength}"))
                        .idempotentRepository("{{idempotentrepository}}")
                        .skipDuplicate(false)
                    //.bean("myExceptionTester", "process")
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

    public EndpointConsumerBuilder ftpEndpointConsumerBuilder() {

        EndpointConsumerBuilder ftpEndpointConsumerBuilder = ftp("{{user}}@{{server}}:{{port}}/pub")
                 .account("{{user}}")
                 .password("{{password}}")

                 .fileName("{{filename}}")
                 .streamDownload("{{streamdownload}}")
                 .move("completed/${file:name.noext}-${date:now:yyyyMMddHHmmssSSS}.${file:ext}")
                 .moveFailed("error/${file:name.noext}-${date:now:yyyyMMddHHmmssSSS}.${file:ext}")

                 .scheduler("quartz")
                 .schedulerProperties("cron", "{{0/20 * * * * ?}}")
        //         //.exceptionHandler("{{exceptionhandler}}")
                 .advanced()
                 .stepwise("{{stepwise}}");

        return ftpEndpointConsumerBuilder;
    }

}
