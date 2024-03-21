package com.pw.resumecameldemo.exception;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.spi.ExceptionHandler;
import org.apache.commons.net.ftp.FTPConnectionClosedException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GwhFileExceptionHandler implements ExceptionHandler {

    private final ProducerTemplate producerTemplate;

    public GwhFileExceptionHandler(ProducerTemplate producerTemplate) {
        this.producerTemplate = producerTemplate;
    }
    
    @Override
    public void handleException(Throwable exception) {
        handleException(exception.getMessage(), exception);
    }

    @Override
    public void handleException(String message, Throwable exception) {
        handleException(message, null, exception);
    }

    @Override
    public void handleException(final String message, final Exchange exchange, final Throwable exception) {

        // for FtpConnectionClosedException, we want to retry
        // for IOException and MalformedServerReplyException we want to stop
        // and not retry as these are not recoverable errors
        String endpointUri;
        
        log.error("Exception occurred: {}", exception.getMessage());

        if (exception instanceof FTPConnectionClosedException) {
            endpointUri = "direct:file-exception-retry";
        } else {
            endpointUri = "direct:file-exception-stop";
        }

        producerTemplate.send(endpointUri, new Processor() {

            
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.setException(exception);
                exchange.getIn().setBody(message);
            } 
           
        });
                
    }    
}
