package com.pw.resumecameldemo.bean;

import org.apache.camel.builder.EndpointConsumerBuilder;
import org.apache.camel.builder.endpoint.dsl.FtpEndpointBuilderFactory.FtpEndpointConsumerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.ftp;
import static org.apache.camel.builder.endpoint.StaticEndpointBuilders.sftp;

@Configuration
public class GwhEndpointConfigurations {


    @Bean("ftpEndpointConsumerBuilder")
    public EndpointConsumerBuilder ftpEndpointConsumerBuilder() {

        String sftpServer = "192.168.6.2";
        String sftpPort = "21";


        EndpointConsumerBuilder ftpEndpointConsumerBuilder =


        // ftp("{{server}}:{{port}}/pub")
        //             //.account("{{user}}")
        //             .username("{{user}}")
        //             .password("{{password}}")
        //             .fileName("{{filename}}")
        //             .streamDownload("{{streamdownload}}")
        //             .move("completed/${file:name.noext}-${date:now:yyyyMMddHHmmssSSS}.${file:ext}")
        //             .moveFailed("error/${file:name.noext}-${date:now:yyyyMMddHHmmssSSS}.${file:ext}")
        //             .delay(10000)
        //             .backoffErrorThreshold(1)
        //             .backoffMultiplier(10)
        //             .advanced() // advanced options need to go at the end
        //                 .stepwise("{{stepwise}}")
        //                 .bridgeErrorHandler(true);


        sftp(sftpServer + ":" + sftpPort + "/" + "resourceAttr.getLocation()")
                    .username("user")
                    .fileName("resourceAttr.getFileName()")
                    .streamDownload(true)
                    .move("completed/${file:name.noext}-${date:now:yyyyMMddHHmmssSSS}.${file:ext}")
                    .moveFailed("error/${file:name.noext}-${date:now:yyyyMMddHHmmssSSS}.${file:ext}")
                    .delay(10000)
                    .initialDelay(5000)
                    .useFixedDelay(true)
                    .backoffErrorThreshold(1)
                    .backoffMultiplier(10)
                    .advanced() // advanced options need to go at the end
                        .stepwise("{{stepwise}}")
                        .bridgeErrorHandler(true);



        return ftpEndpointConsumerBuilder;

    }
}
