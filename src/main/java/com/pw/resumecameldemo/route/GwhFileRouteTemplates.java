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
            .templateParameter("groupcount", "500")
            .templateParameter("directid", "splitmqsend")
            .templateParameter("splittoken", "\n")
            .templateParameter("schedule", "0/20 * * * * ?")
            .templateParameter("exceptionhandler", "gwhFileExceptionHandler")
            .templateParameter("streamdownload", "true")
            .templateParameter("stepwise", "false")
            .templateParameter("idempotentrepository", "fileIdempotentRepository")
            .from(new StringBuilder("{{filecomponent}}://")
                            .append("{{user}}@")
                            .append("{{server}}:")
                            .append("{{port}}/pub")
                            .append("?password={{password}}")
                            .append("&fileName={{filename}}")
                            .append("&streamDownload={{streamdownload}}")
                            .append("&stepwise={{stepwise}}")
                            .append("&backoffErrorThreshold=1")
                            .append("&backoffMultiplier=10")
                            .append("&delay=10000")
                            //.append("&scheduler=quartz")
                            //.append("&scheduler.cron={{schedule}}")
                            .append("&move=completed/${file:name.noext}-${date:now:yyyyMMddHHmmssSSS}.${file:ext}")
                            .append("&moveFailed=error/${file:name.noext}-${date:now:yyyyMMddHHmmssSSS}.${file:ext}")
                            //.append("&exceptionHandler={{exceptionhandler}}")
                            .toString())
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
}
