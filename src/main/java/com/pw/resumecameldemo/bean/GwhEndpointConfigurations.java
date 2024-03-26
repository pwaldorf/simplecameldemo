package com.pw.resumecameldemo.bean;

import org.apache.camel.builder.EndpointConsumerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.ftp;

//@Configuration
public class GwhEndpointConfigurations {


    //@Bean("ftpEndpointConsumerBuilder")
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
