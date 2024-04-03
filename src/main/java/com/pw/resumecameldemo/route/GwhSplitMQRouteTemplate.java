package com.pw.resumecameldemo.route;


import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.processor.aggregate.GroupedBodyAggregationStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pw.resumecameldemo.aggregator.JsonAggregationStrategy;





@Component
public class GwhSplitMQRouteTemplate extends RouteBuilder {

    @Autowired
    JsonAggregationStrategy jsonAggregationStrategy;

    @Override
    public void configure() throws Exception {

        routeTemplate("splitmqsend")
            .templateParameter("directid", "splitmqsend")
            .templateParameter("mqcomponent", "gwhMqSender")
            .templateParameter("mqcomponentmethod", "send")
            .templateParameter("transactedref", "txRequiredActiveMqTest")
            .templateParameter("routepolicy", "fileResumeUpdateRoutePolicy")
            .templateParameter("routeconfiguration", "resumeProcess")
            .templateParameter("resumeupdate", "fileResumeUpdateRoutePolicy")
            .templateParameter("resumeupdatemethod", "updateLineCount")
            .from("direct:{{directid}}")
                    .routePolicyRef("{{routepolicy}}")
                    .routeConfigurationId("jmsExceptionRetry,customException")
                    // .to("bean:gwhMqSender1?method=send")

                    .log("CamelSplitComplete: ${exchangeProperty.CamelSplitComplete}")
                    .aggregate(new GroupedBodyAggregationStrategy()).constant(true).eagerCheckCompletion()
                        .completionSize(4)
                        .completionTimeout(5000)
                        .completionPredicate(exchangeProperty("CamelSplitComplete").isEqualTo(true))
                        .marshal().json()
                        .log("MQ Body Final: ${body}")
                    .end();
                    // .transacted("{{transactedref}}")
                        //.to("bean:gwhMqSender1?method=send")


                    // .to("bean:{{mqcomponent}}?method={{mqcomponentmethod}}");
                    // .log("MQ Body Final: ${body}");


                    // .choice()
                    // .when(simple("${exchangeProperty.CamelSplitComplete} == true"))
                    //     .setProperty(Exchange.AGGREGATION_COMPLETE_ALL_GROUPS, constant("true"))
                    //     .log("pjw true")
                    // .end();

    }

    // private static CsvDataFormat createCsvDataFormat() {

    //     CsvDataFormat dataFormat = new CsvDataFormat();
    //     dataFormat.setHeader("FIRSTNAME,LASTNAME,ADDRESS,CITY,STATE,ZIPCODE");
    //     dataFormat.setDelimiter(',');
    //     dataFormat.setTrim(true);
    //     dataFormat.setIgnoreSurroundingSpaces(true);
    //     //dataFormat.setCaptureHeaderRecord(true);
    //     dataFormat.setSkipHeaderRecord(false);
    //     dataFormat.setLazyLoad(true);
    //     dataFormat.setUseMaps(true);
    //     return dataFormat;
    // }

    // private static FlatpackDataFormat createFlatpackDelimitedDataFormat() {
    //     FlatpackDataFormat dataFormat = new FlatpackDataFormat();
    //  //   dataFormat.setDefinition("bean:gwhDataResource?method=getDefinitionDelimited");
    //     dataFormat.setFixed(false);
    //     dataFormat.setDelimiter(',');
    //     dataFormat.setTextQualifier('"');
    //     dataFormat.setIgnoreFirstRecord(false);
    //     return dataFormat;
    // }

    // private static FlatpackDataFormat createFlatpackFixedDataFormat() {
    //     FlatpackDataFormat dataFormat = new FlatpackDataFormat();
    //     dataFormat.setDefinition("bean:gwhDataResource?method=getDefinitionFixed");
    //     dataFormat.setFixed(true);
    //     dataFormat.setIgnoreFirstRecord(false);
    //     return dataFormat;
    // }

}
