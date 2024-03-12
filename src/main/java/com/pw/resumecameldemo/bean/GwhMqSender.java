package com.pw.resumecameldemo.bean;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.CamelContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("gwhMqSender")
public class GwhMqSender {

    @Value("${outboundq:mailbox}")
    private String outboundQ;

    @Autowired
    JmsTemplate newFileJmsTemplate;

    public void send(String message) {
        log.info("Sending message to " + outboundQ);
        newFileJmsTemplate.convertAndSend(outboundQ, message);
    }
    
}
