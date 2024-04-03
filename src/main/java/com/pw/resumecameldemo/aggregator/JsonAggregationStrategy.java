package com.pw.resumecameldemo.aggregator;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

@Component("jsonAggregationStrategy")
public class JsonAggregationStrategy implements AggregationStrategy {

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {

        if (oldExchange == null) {
            return newExchange;
        }

        String oldBody = oldExchange.getIn().getBody(String.class);
        String newBody = newExchange.getIn().getBody(String.class);
        String body = null;

        if (oldBody.startsWith("[")) {
            body = oldBody.replace("}", "") + "," + newBody + "}";
        } else {
            body = "[" + oldBody + "," + newBody + "]";
        }
        oldExchange.getIn().setBody(body);
        return oldExchange;

    }

}
