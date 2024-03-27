package com.pw.resumecameldemo.route;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class GwhFileRouteTemplate extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        routeTemplate("gwhFileRouteTemplate")
            .templateParameter("user")
            .templateParameter("sftpServer")
            .templateParameter("sftpPort", "22")
            .templateParameter("filename")
            .templateParameter("sshKeyFileName")
            .templateParameter("groupcount", "50")
            .templateParameter("splittoken", "\n")
            .templateParameter("streamdownload", "true")
            .templateParameter("stepwise", "false")
            .templateParameter("recursive", "true")
            .templateParameter("antInclude", "*.*")
            .templateParameter("delay", "10000")
            .templateParameter("initialDelay", "5000")
            .templateParameter("lastRecord", "0")
            .from(new StringBuilder("sftp://")
                            .append("{{user}}@")
                            .append("{{sftpServer}}:")
                            .append("{{sftpPort}}")
                            .append("/{{location}}")
                            .append("&fileName={{filename}}")
                            .append("&privateKeyFile={{sshKeyFileName}}")
                            .append("&streamDownload={{streamdownload}}")
                            .append("&stepwise={{stepwise}}")
                            .append("&recursive={{recursive}}")
                            .append("&backoffErrorThreshold=1")
                            .append("&backoffMultiplier=10")
                            .append("&delay={{delay}}")
                            .append("&initialDelay={{initialDelay}}")
                            .append("&useFixedDelay=true")
                            .append("&move=completed/${file:name.noext}-${date:now:yyyyMMddHHmmssSSS}.${file:ext}")
                            .append("&moveFailed=error/${file:name.noext}-${date:now:yyyyMMddHHmmssSSS}.${file:ext}")
                            .toString())
                    .routeConfigurationId("ioException,jmsExceptionRetry,jmsExceptionNoRetry")
                    .streamCaching()
                    .transacted("txRequiredActiveMqTest")
                    .convertBodyTo(String.class)
                    .split().tokenize("{{splittoken}}", false, "{{groupcount}}" )
                            .streaming()
                            .stopOnException()
                    .filter(simple("${exchangeProperty.CamelSplitIndex} >= {{lastRecord}}"))
                    .to("bean:newRouterFileEngine?method=process");

    }

}
