package com.pw.resumecameldemo.route;


import java.util.ArrayList;
import java.util.List;

import org.apache.camel.builder.EndpointConsumerBuilder;
import org.apache.camel.builder.endpoint.EndpointRouteBuilder;
import org.apache.camel.processor.aggregate.GroupedBodyAggregationStrategy;
import org.apache.camel.routepolicy.quartz.CronScheduledRoutePolicy;
import org.apache.camel.spi.RoutePolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.pw.resumecameldemo.bean.GwhDataFormat;


@Component
public class GwhFileRouteTemplates extends EndpointRouteBuilder {

    @Autowired
    CronScheduledRoutePolicy cronScheduledRoutePolicy;

    @Autowired
    FileResumeRoutePolicy fileResumeRoutePolicy;

    @Autowired
    GwhDataFormat gwhDataFormat;

    @Autowired
    @Qualifier("ftpEndpointConsumerBuilder")
    EndpointConsumerBuilder endpointConsumerBuilder;


    @Override
    public void configure() throws Exception {

        List<RoutePolicy> routePolicies = new ArrayList<>();
        routePolicies.add(cronScheduledRoutePolicy);
        routePolicies.add(fileResumeRoutePolicy);


        routeTemplate("fileRouteTemplate")
            .templateParameter("filecomponent", "ftp")
            .templateParameter("directid", "splitmqsend")
            .templateParameter("user")
            .templateParameter("server")
            .templateParameter("port", "21")
            .templateParameter("password")
            .templateParameter("filename")
            .templateParameter("streamdownload", "true")
            .templateParameter("stepwise", "false")
            // .from(new StringBuilder("{{filecomponent}}://")
            //                 .append("{{user}}@")
            //                 .append("{{server}}:")
            //                 .append("{{port}}/pub")
            //                 .append("?password={{password}}")
            //                 .append("&fileName={{filename}}")
            //                 .append("&streamDownload={{streamdownload}}")
            //                 .append("&stepwise={{stepwise}}")
            //                 .append("&bridgeErrorHandler=true")
            //                 .append("&backoffErrorThreshold=1")
            //                 .append("&backoffMultiplier=10")
            //                 .append("&delay=10000")
            //                 .append("&move=completed/${file:name.noext}-${date:now:yyyyMMddHHmmssSSS}.${file:ext}")
            //                 .append("&moveFailed=error/${file:name.noext}-${date:now:yyyyMMddHHmmssSSS}.${file:ext}")
            //                 .toString())
            .from(endpointConsumerBuilder)
                .routePolicy(routePolicies.toArray(RoutePolicy[]::new)).noAutoStartup()
                .convertBodyTo(String.class)
                .unmarshal(gwhDataFormat.geDataFormat("${file:onlyname}"))
                .choice()
                .when(simple("${body.getErrorCount} > 0"))
                    .log("Parsing error occured: simple(${body.getErrors})")
                    .throwException(new RuntimeException("simple(${body.getErrors})"))
                .end()
                .split().body()
                .aggregate(new GroupedBodyAggregationStrategy()).constant(true).eagerCheckCompletion()
                    .completionSize(4)
                    .completionTimeout(5000)
                    .completionPredicate(exchangeProperty("CamelSplitComplete").isEqualTo(true))
                    .marshal().json()
                    .log("MQ Body Final: ${body}")
                    //.to("direct:{{directid}}");
                .end();

    }
}
