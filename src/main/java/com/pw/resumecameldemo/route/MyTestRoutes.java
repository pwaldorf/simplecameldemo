package com.pw.resumecameldemo.route;

import java.io.File;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.file.FileConstants;
import org.apache.camel.resume.Resumable;
import org.apache.camel.support.resume.Resumables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.pw.resumecameldemo.resume.TableResumeStrategyConfigurationBuilder;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Component
public class MyTestRoutes extends RouteBuilder {

    @Autowired
    @Qualifier("defaultTableResumeStrategyConfigurationBuilder")
    private TableResumeStrategyConfigurationBuilder tableResumeStrategyConfigurationBuilder;
    
    private long lastOffset;
    private long lineCount = 0;

    // Process required to set offset
    private void process(Exchange exchange) {
        final String body = exchange.getMessage().getBody(String.class);
        
        // FilePath used to set Cache Key
        final String filePath = exchange.getMessage().getHeader(Exchange.FILE_PATH, String.class);
        final File file = new File(filePath);

        // Get the initial offset and use it to update the last offset when reading the first line
        final Resumable resumable = exchange.getMessage().getHeader(FileConstants.INITIAL_OFFSET, Resumable.class);
        final Long value = resumable.getLastOffset().getValue(Long.class);
        
        if (lineCount == 0) {
            lastOffset += value;
        }

        // It sums w/ 1 in order to account for the newline that is removed by readLine
        // Reset to zero for last line        
        if ((boolean)exchange.getProperty("CamelSplitComplete")) {
            log.info("Last record, offset reset to zero");
            lastOffset = 0;
        } else {
            lastOffset += body.length() + 1;
            lineCount++;
        }
        
        exchange.getMessage().setHeader(Exchange.OFFSET, Resumables.of(file, lastOffset));        
        log.info("Read data: {} / offset key: {} / offset value: {}", body, filePath, lastOffset);        
    }

    @Override
    public void configure() throws Exception {

        from("file:{{input.dir}}?noop=true&fileName={{input.file}}")
                .routeId("largeFileRoute")                
                .convertBodyTo(String.class)
                .split(body().tokenize("\n"))
                    .streaming()
                    .stopOnException()                                        
                .resumable()
                    .configuration(tableResumeStrategyConfigurationBuilder)
                    .intermittent(false)
                    .process(this::process)
                    .end()
                .to("file:{{output.dir}}?fileName=summary.txt&fileExist=Append&appendChars=\n");
                                
    }    
    
}
