package com.pw.resumecameldemo.bean;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("gwhMqSender")
public class GwhMqSender {

    @Value("${outboundq:mailbox}")
    private String outboundQ;

    private int counter = 0;

    @Autowired
    JmsTemplate newFileJmsTemplate;

    @Transactional(transactionManager = "routeChainedTransactionManager")
    public void send(String message, Exchange exchange) {
        log.info("Sending message to " + outboundQ);
        log.info("Sending message: " + message);
        newFileJmsTemplate.convertAndSend(outboundQ, message);

        // if (counter++ > 5) {
        //     throw new MyCustomException("Simulating error");
        // }


    }

}
