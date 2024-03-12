package com.pw.resumecameldemo.route;

import java.util.concurrent.TimeUnit;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.caffeine.processor.idempotent.CaffeineIdempotentRepository;
import org.apache.camel.routepolicy.quartz.CronScheduledRoutePolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.github.benmanes.caffeine.cache.Cache;
import com.pw.resumecameldemo.model.ResumeRecord;

import lombok.extern.slf4j.Slf4j;


@Slf4j
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
            .parameter("filename", "test.txt");

        templatedRoute("splitmqsend")
            .routeId("splitmqsend")
            .parameter("mqcomponent", "gwhMqSender")
            .parameter("mqcomponentmethod", "send");

    }    
    
}
