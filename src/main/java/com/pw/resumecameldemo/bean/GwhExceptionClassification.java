package com.pw.resumecameldemo.bean;

import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("gwhExceptionClassification")
public class GwhExceptionClassification {

    public boolean isRetryable() {

        return true;

    }
}
